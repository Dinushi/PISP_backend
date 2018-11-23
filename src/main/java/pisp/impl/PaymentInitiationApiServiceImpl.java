package pisp.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.*;
import pisp.dto.*;
import pisp.dto.PaymentInitResponseDTO;
import pisp.dto.PaymentInitRequestDTO;
import java.net.URI;
import java.net.URISyntaxException;

import pisp.exception.PispException;
import pisp.mappings.BankAccountMapping;
import pisp.mappings.BankMapping;
import pisp.mappings.PaymentInitiationRequestMapping;
import pisp.models.Payment;
import pisp.models.PispInternalResponse;
import pisp.services.PaymentManagementService;
import pisp.services.UserManagementService;
import pisp.utilities.SessionManager;
import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

public class PaymentInitiationApiServiceImpl extends PaymentInitiationApiService {
    private Log log = LogFactory.getLog(PaymentInitiationApiServiceImpl.class);


    @Override
    public Response makePaymentInitiationRequest(String contentType,String clientId,String purchaseId,String authorization,PaymentInitRequestDTO body){

        log.info("Received a payment initiation request from "+ body.getEShopUsername());

        if (clientId != null) {
            UserManagementService userManagementService=new UserManagementService();
            PispInternalResponse response=userManagementService.validateClientIdOfEshop(clientId); //validate the client id for its existence in db
            if(!response.isOperationSuccessful()){
                throw new PispException(response.getMessage());
            }
        }
        //TODO: the Authorization bearer value is needed to be validated (No endpoint is CURRENTLY designed for PISP token issuing)

        Payment paymentInitiationRequest= PaymentInitiationRequestMapping.createPaymentInitiationRequestInstance(body,clientId,purchaseId);
        if(!paymentInitiationRequest.isError()){

            PaymentManagementService paymentManagementService=new PaymentManagementService(paymentInitiationRequest);
            PispInternalResponse paymentEntryResult=paymentManagementService.storePaymentDataInDB();

            if (paymentEntryResult.isOperationSuccessful()) {
                Payment paymentData=paymentManagementService.retrievePaymentInitiationData(paymentEntryResult.getMessage());
                PaymentInitResponseDTO responseBodyDTO=PaymentInitiationRequestMapping.getPaymentInitiationResponseDTO(paymentData);

                log.info("returning the response for redirection");
                return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                     .entity(responseBodyDTO).build();

            }else{
                return  Response.serverError().entity(paymentEntryResult.getMessage()).build();
            }
        }else{
            return Response.serverError().
                    header(Constants.PURCHASE_ID,paymentInitiationRequest.getPurchaseId())
                    .entity(new ApiResponseMessage(ApiResponseMessage.ERROR, paymentInitiationRequest.getErrorMessage())).build();//return error when fails to map data in Http request to internal model

        }
    }


    @Override
    public Response getPaymentInitRequestById(String contentType,HttpServletRequest request,String username){

        HttpSession session = request.getSession(true);
        log.info("session is new :"+session.isNew());
        log.info("session id"+session.getId());
        Object sessionToken=session.getAttribute(Constants.SESSION_ID);

        PispInternalResponse sessionValidationResponse=SessionManager.validateSessionTokenOfPSUAndGetPaymentInitRequestId(username,(String)sessionToken);
        if(sessionValidationResponse.isOperationSuccessful()){
            String paymentInitReqId=sessionValidationResponse.getMessage();
            log.info("The payment Initiation request: "+paymentInitReqId);
            PaymentManagementService paymentManagementService=new PaymentManagementService();
            Payment paymentData=paymentManagementService.retrievePaymentInitiationData(paymentInitReqId);
            PaymentInitResponseDTO paymentInitResponseDTO=PaymentInitiationRequestMapping.getPaymentInitiationResponseDTO(paymentData);
            return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                    .entity(paymentInitResponseDTO).build();
        }else{
            return Response.status(404).type("text/plain").entity(sessionValidationResponse.getMessage()).build();
        }

    }

    @Override
    public Response selectDebtorBank(String contentType, HttpServletRequest request,String username,DebtorBankDTO body) {
        log.info("The customer has selected the debtor bank as "+body.getBankUid());

        HttpSession session = request.getSession(true);
        log.info("session is new :"+session.isNew());
        log.info("session id"+session.getId());

        Object sessionToken=session.getAttribute(Constants.SESSION_ID);
        PispInternalResponse sessionValidationResponse=SessionManager.validateSessionTokenOfPSUAndGetPaymentInitRequestId(username,(String)sessionToken);

        if(sessionValidationResponse.isOperationSuccessful()){
            String paymentInitReqId=sessionValidationResponse.getMessage();
            PaymentManagementService paymentManagementService=new PaymentManagementService();
            PispInternalResponse response=paymentManagementService.updatePaymentDebtorBank(paymentInitReqId,body.getBankUid());

            if(response.isOperationSuccessful()){
                BankSelectionResponseDTO responseBodyDTO= BankMapping.getBankSelectionResponseDTO(response);
                log.info("The response returned - is account required:  "+responseBodyDTO.getAccountRequired());
                return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                        .entity(responseBodyDTO).build();

            }else{
                return  Response.serverError().entity(response.getMessage()).build();
            }
        } else{
            return Response.status(404).type("text/plain").entity(sessionValidationResponse.getMessage()).build();
        }
    }

    @Override
    public Response selectDebtorAccount(String contentType, HttpServletRequest request, String username,BankAccountDTO body) {
        log.info("The customer has selected the debtor account as "+body.getIdentification());

        HttpSession session = request.getSession();
        log.info("session id"+session.getId());


        Object sessionToken=session.getAttribute(Constants.SESSION_ID);
        PispInternalResponse sessionValidationResponse=SessionManager.validateSessionTokenOfPSUAndGetPaymentInitRequestId(username,(String)sessionToken);

        if(sessionValidationResponse.isOperationSuccessful()){

            String paymentInitReqId=sessionValidationResponse.getMessage();
            PaymentManagementService paymentManagementService=new PaymentManagementService();
            PispInternalResponse result=paymentManagementService.updatePaymentDebtorAccountData(paymentInitReqId, BankAccountMapping.createBankAccountInstance(body));

            if(result.isOperationSuccessful()){
                log.info("Updated the debtor account details");
                paymentManagementService.retrievePaymentInitiationData(paymentInitReqId);
                PispInternalResponse response = paymentManagementService.processPaymentInitiationWithBank();//response contains the redirect url to authorize endpoint

                if (response.isOperationSuccessful()) {
                    log.info("The Authorize URL is generated");
                    try {
                        log.info("returning the response for redirection");
                        return Response.seeOther(new URI(response.getMessage())).build();

                    } catch (URISyntaxException e) {
                        log.error("The URL syntax error");
                        return  Response.serverError().entity(ErrorMessages.ERROR_OCCURRED).build();
                    }
                } else {
                    return Response.serverError().entity(response.getMessage()).build();
                }
            }else{
                return  Response.serverError().entity(ErrorMessages.ERROR_WHILE_RETRIEVING_PAYMENT_INITIATION).build();
            }

        }else{
        return Response.status(401).type("text/plain").entity(ErrorMessages.NO_PAYMENT_INITIATION_FOUND).build();
        }

    }


    @Override
    public Response addAuthorizationCodeGrant(String contentType,String username, HttpServletRequest request, AuthCodeDTO body) {

        HttpSession session = request.getSession();
        log.info("session id"+session.getId());

        Object sessionToken=session.getAttribute(Constants.SESSION_ID);
        PispInternalResponse sessionValidationResponse=SessionManager.validateSessionTokenOfPSUAndGetPaymentInitRequestId(username,(String)sessionToken);


        if(sessionValidationResponse.isOperationSuccessful()){

            String paymentInitReqId=(String) sessionValidationResponse.getData();
            PaymentManagementService paymentManagementService=new PaymentManagementService(paymentInitReqId, body.getCode(),body.getIdToken());
            PispInternalResponse response=paymentManagementService.processPSUAuthorization();

            if(response.isOperationSuccessful()){
                PispInternalResponse paymentCompletionResponse=paymentManagementService.getTheStatusOfPayment((Payment) response.getData());

                if(paymentCompletionResponse.isOperationSuccessful()){
                    PaymentCompletionResponseDTO paymentCompletionResponseDTO=new PaymentCompletionResponseDTO();
                    paymentCompletionResponseDTO.setRedirectLink(paymentCompletionResponse.getMessage());
                    return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                            .entity(paymentCompletionResponseDTO).build();
                }
            }else{
                log.error(ErrorMessages.ERROR_PAYMENT_SUBMISSION_NOT_PROCESSED);
                return Response.serverError().entity(response.getMessage()).build();

            }
        }else{
            return Response.status(401).type("text/plain").entity(ErrorMessages.SESSION_DOESNT_EXIST).build();
        }
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
       /* if (cookie == null) {
            return Response.serverError().entity("ERROR").build();
        } else {
            String paymentInitReqId=cookie.getValue();
            AuthCodeValidationService authCodeValidationService=new AuthCodeValidationService(paymentInitReqId,body.getCode(),body.getIdToken());
            authCodeValidationService.validateThePaymentInitIDWithIdToken();
            //AuthCodeMapping.setBankUIDForTheAuthToken(body.getIdToken());
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
            //return Response.ok(cookie.getValue()).build();
        }*/

    }

}
