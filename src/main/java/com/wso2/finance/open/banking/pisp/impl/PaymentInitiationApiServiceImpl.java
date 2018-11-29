package com.wso2.finance.open.banking.pisp.impl;

import com.wso2.finance.open.banking.pisp.ApiResponseMessage;
import com.wso2.finance.open.banking.pisp.PaymentInitiationApiService;
import com.wso2.finance.open.banking.pisp.dto.AuthCodeDTO;
import com.wso2.finance.open.banking.pisp.dto.AuthUrlDTO;
import com.wso2.finance.open.banking.pisp.dto.BankAccountDTO;
import com.wso2.finance.open.banking.pisp.dto.BankSelectionResponseDTO;
import com.wso2.finance.open.banking.pisp.dto.DebtorBankDTO;
import com.wso2.finance.open.banking.pisp.dto.PaymentInitRequestDTO;
import com.wso2.finance.open.banking.pisp.dto.PaymentInitResponseDTO;
import com.wso2.finance.open.banking.pisp.exception.PispException;
import com.wso2.finance.open.banking.pisp.mappings.BankAccountMapping;
import com.wso2.finance.open.banking.pisp.mappings.BankMapping;
import com.wso2.finance.open.banking.pisp.mappings.PaymentInitiationRequestMapping;
import com.wso2.finance.open.banking.pisp.models.BankAccount;
import com.wso2.finance.open.banking.pisp.models.Payment;
import com.wso2.finance.open.banking.pisp.models.InternalResponse;
import com.wso2.finance.open.banking.pisp.services.PaymentManagementService;
import com.wso2.finance.open.banking.pisp.services.UserManagementService;
import com.wso2.finance.open.banking.pisp.utilities.SessionManager;
import com.wso2.finance.open.banking.pisp.utilities.constants.Constants;
import com.wso2.finance.open.banking.pisp.utilities.constants.ErrorMessages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

/**
 * This class holds the implementation logic behind payment API.
 */
public class PaymentInitiationApiServiceImpl extends PaymentInitiationApiService {

    private Log log = LogFactory.getLog(PaymentInitiationApiServiceImpl.class);

    @Override
    public Response makePaymentInitiationRequest(String clientId, String purchaseId, PaymentInitRequestDTO body) {

        log.info("Received a payment initiation request from " + body.getEShopUsername());

        if (clientId != null) {
            UserManagementService userManagementService = new UserManagementService();
            InternalResponse response = userManagementService.validateClientIdOfEShop(clientId);
            if (!response.isOperationSuccessful()) {
                throw new PispException(response.getMessage());
            }
        }
        Payment paymentInitiationRequest = PaymentInitiationRequestMapping.createPaymentInitiationRequestInstance(body, clientId, purchaseId);

        if (!paymentInitiationRequest.isError()) {

            PaymentManagementService paymentManagementService = new PaymentManagementService();
            String paymentInitReqId = paymentManagementService.storePaymentDataInDB(paymentInitiationRequest);
            Payment paymentData = paymentManagementService.retrievePaymentInitiationData(paymentInitReqId);
            PaymentInitResponseDTO responseBodyDTO = PaymentInitiationRequestMapping.
                        getPaymentInitiationResponseDTO(paymentData, false, true);
            log.info("returning the response for redirection");
                return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                        .entity(responseBodyDTO).build();
        } else {
            return Response.serverError().
                    header(Constants.PURCHASE_ID, paymentInitiationRequest.getPurchaseId())
                    .entity(new ApiResponseMessage(ApiResponseMessage.ERROR, paymentInitiationRequest.getErrorMessage())).build();
        }
    }

    @Override
    public Response getPaymentInitRequestById(HttpServletRequest request, String username) {

        HttpSession session = request.getSession(true);
        log.info("session is new :" + session.isNew());
        log.info("session id" + session.getId());
        Object sessionToken = session.getAttribute(Constants.SESSION_ID);

        InternalResponse sessionValidationResponse = SessionManager.getPaymentInitRequestIdFromSession(username, (String) sessionToken);
        if (sessionValidationResponse.isOperationSuccessful()) {
            String paymentInitReqId = sessionValidationResponse.getMessage();
            log.info("The payment Initiation request: " + paymentInitReqId);
            PaymentManagementService paymentManagementService = new PaymentManagementService();
            Payment paymentData = paymentManagementService.retrievePaymentInitiationData(paymentInitReqId);
            PaymentInitResponseDTO paymentInitResponseDTO = PaymentInitiationRequestMapping.
                    getPaymentInitiationResponseDTO(paymentData, false, true);
            return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                    .entity(paymentInitResponseDTO).build();
        } else {
            return Response.status(404).type("text/plain").entity(sessionValidationResponse.getMessage()).build();
        }

    }

    @Override
    public Response selectDebtorBank(HttpServletRequest request, String username, DebtorBankDTO body) {

        log.info("The customer has selected the debtor bank as " + body.getBankUid());

        HttpSession session = request.getSession(true);
        log.info("session is new :" + session.isNew());
        log.info("session id" + session.getId());

        Object sessionToken = session.getAttribute(Constants.SESSION_ID);
        InternalResponse sessionValidationResponse = SessionManager.getPaymentInitRequestIdFromSession(username, (String) sessionToken);

        if (sessionValidationResponse.isOperationSuccessful()) {
            String paymentInitReqId = sessionValidationResponse.getMessage();
            PaymentManagementService paymentManagementService = new PaymentManagementService();
            InternalResponse response = paymentManagementService.updatePaymentDebtorBank(paymentInitReqId, body.getBankUid());

            if (response.isOperationSuccessful()) {
                BankSelectionResponseDTO responseBodyDTO = BankMapping.getBankSelectionResponseDTO(response);
                log.info("IsAccountRequired:  " + responseBodyDTO.getAccountRequired());
                return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                        .entity(responseBodyDTO).build();

            } else {
                return Response.serverError().entity(response.getMessage()).build();
            }
        } else {
            return Response.status(404).type("text/plain").entity(sessionValidationResponse.getMessage()).build();
        }
    }

    @Override
    public Response selectDebtorAccount(HttpServletRequest request, String username, BankAccountDTO body) {

        log.info("The customer has selected the debtor account as " + body.getIdentification());

        HttpSession session = request.getSession();
        log.info("session id" + session.getId());

        Object sessionToken = session.getAttribute(Constants.SESSION_ID);
        InternalResponse sessionValidationResponse = SessionManager.getPaymentInitRequestIdFromSession(username, (String) sessionToken);

        if (sessionValidationResponse.isOperationSuccessful()) {

            String paymentInitReqId = sessionValidationResponse.getMessage();
            PaymentManagementService paymentManagementService = new PaymentManagementService();
            BankAccount debtorAccount = BankAccountMapping.createAccountInstance(body);
            InternalResponse result = paymentManagementService.updatePaymentDebtorAccountData(paymentInitReqId, debtorAccount);
            log.info("Bank account selection stored : " + result.getMessage());

            if (result.isOperationSuccessful()) {
                log.info("Updated the debtor account details");
                paymentManagementService.retrievePaymentInitiationData(paymentInitReqId);
                InternalResponse responseWithAuthUrl = paymentManagementService.processPaymentInitiationWithBank();

                if (responseWithAuthUrl.isOperationSuccessful()) {
                    log.info("Returning the authorization URL");
                    AuthUrlDTO authUrlDTO = new AuthUrlDTO();
                    authUrlDTO.setAuthUrl(responseWithAuthUrl.getMessage());
                    return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                            .entity(authUrlDTO).build();

                } else {
                    return Response.serverError().entity(responseWithAuthUrl.getMessage()).build();
                }
            } else {
                return Response.serverError().entity(ErrorMessages.ERROR_WHILE_RETRIEVING_PAYMENT_INITIATION).build();
            }
        } else {
            return Response.status(401).type("text/plain").entity(ErrorMessages.NO_PAYMENT_INITIATION_FOUND).build();
        }
    }

    @Override
    public Response addAuthorizationCode(String username, HttpServletRequest request, AuthCodeDTO body) {

        HttpSession session = request.getSession();
        log.info("session id" + session.getId());

        Object sessionToken = session.getAttribute(Constants.SESSION_ID);
        InternalResponse sessionValidationResponse = SessionManager.getPaymentInitRequestIdFromSession(username, (String) sessionToken);

        if (sessionValidationResponse.isOperationSuccessful()) {

            String paymentInitReqId = sessionValidationResponse.getMessage();
            PaymentManagementService paymentManagementService = new PaymentManagementService();
            paymentManagementService.setAuthCodeForThePayment(paymentInitReqId, body.getCode(), body.getIdToken());
            InternalResponse response = paymentManagementService.processPSUAuthorizationAndSubmit();

            if (response.isOperationSuccessful()) {
                log.info("Returning that the payment has completed.");
                PaymentInitResponseDTO paymentInitResponseDTO = PaymentInitiationRequestMapping.
                        getPaymentInitiationResponseDTO((Payment) response.getData(), true, true);
                return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                        .entity(paymentInitResponseDTO).build();
            } else {
                PaymentInitResponseDTO paymentInitResponseDTO = PaymentInitiationRequestMapping.
                        getPaymentInitiationResponseDTO((Payment) response.getData(), false, false);
                log.info("Returning that the payment is failed.");
                return Response.serverError().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                        .entity(paymentInitResponseDTO).build();
            }
        } else {
            return Response.status(401).type("text/plain").entity(ErrorMessages.SESSION_DOESNT_EXIST).build();
        }

    }

    @Override
    public Response getPaymentStatusFromBank(HttpServletRequest request, String username) {

        HttpSession session = request.getSession();
        log.info("session id" + session.getId());

        Object sessionToken = session.getAttribute(Constants.SESSION_ID);
        InternalResponse sessionValidationResponse = SessionManager.getPaymentInitRequestIdFromSession(username, (String) sessionToken);

        if (sessionValidationResponse.isOperationSuccessful()) {

            String paymentInitReqId = sessionValidationResponse.getMessage();

            PaymentManagementService paymentManagementService = new PaymentManagementService();
            Payment paymentInfo = paymentManagementService.retrievePaymentInitiationData(paymentInitReqId);
            InternalResponse paymentCompletionResponse = paymentManagementService.getTheStatusOfPayment(paymentInfo);

            if (paymentCompletionResponse.isOperationSuccessful()) {
                PaymentInitResponseDTO paymentInitResponseDTO = PaymentInitiationRequestMapping.
                        getPaymentInitiationResponseDTO((Payment) paymentCompletionResponse.getData(), true, true);
                log.info("Returning that the payment has completed.");
                return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                        .entity(paymentInitResponseDTO).build();
            } else {
                PaymentInitResponseDTO paymentInitResponseDTO = PaymentInitiationRequestMapping.
                        getPaymentInitiationResponseDTO((Payment) paymentCompletionResponse.getData(), false, false);
                log.info("Returning that the payment is failed.");
                return Response.serverError().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                        .entity(paymentInitResponseDTO).build();
            }

        } else {
            return Response.status(401).type("text/plain").entity(ErrorMessages.SESSION_DOESNT_EXIST).build();
        }
    }

}

