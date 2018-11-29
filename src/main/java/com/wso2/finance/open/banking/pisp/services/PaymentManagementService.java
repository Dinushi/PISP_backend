/*
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *   This software is the property of WSO2 Inc. and its suppliers, if any.
 *   Dissemination of any information or reproduction of any material contained
 *   herein is strictly forbidden, unless permitted by WSO2 in accordance with
 *   the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 *   language governing the permissions and limitations under this license,
 *   please see the license as well as any agreement you’ve entered into with
 *   WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.finance.open.banking.pisp.services;

import com.wso2.finance.open.banking.pisp.PispFlow.PaymentMediator;
import com.wso2.finance.open.banking.pisp.dao.PaymentManagementDAO;
import com.wso2.finance.open.banking.pisp.models.BankAccount;
import com.wso2.finance.open.banking.pisp.models.InternalResponse;
import com.wso2.finance.open.banking.pisp.models.Payment;
import com.wso2.finance.open.banking.pisp.utilities.AuthCodeVerification;
import com.wso2.finance.open.banking.pisp.utilities.constants.Constants;
import com.wso2.finance.open.banking.pisp.utilities.constants.ErrorMessages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is to handle payment related operations.
 */
public class PaymentManagementService {

    private Log log = LogFactory.getLog(PaymentManagementService.class);

    private Payment paymentInitiation;
    private String code;
    private String paymentInitReqId;
    private String idToken;
    private PaymentMediator paymentMediator;

    /**
     * store a new payment initiation request in database.
     * return the unique id generated for payment initiation.
     *
     * @param paymentInitiation
     * @return
     */
    public String storePaymentDataInDB(Payment paymentInitiation) {

        paymentInitiation.setPaymentStatus(Constants.PAYMENT_STATUS_1);
        PaymentManagementDAO paymentManagementDAO = new PaymentManagementDAO();
        String paymentInitReqId = paymentManagementDAO.addPaymentInitiation(paymentInitiation);
        return paymentInitReqId;

    }

    /**
     * retrieve the details of particular payment initiation when required.
     *
     * @param paymentInitReqId The UUID that identifies the payment.
     * @return
     */
    public Payment retrievePaymentInitiationData(String paymentInitReqId) {

        PaymentManagementDAO paymentManagementDAO = new PaymentManagementDAO();
        Payment paymentRetrieved = paymentManagementDAO.retrievePayment(paymentInitReqId);
        this.paymentInitiation = paymentRetrieved;
        return paymentRetrieved;
    }

    /*
    ====================================================================================
    This section handles the debtor bank & account selection by customer of the payment
    ====================================================================================
    */

    /**
     * update the payment with the PSU information when the PSU logs-in.
     *
     * @param paymentInitReqId
     * @param psuUsername
     */
    public void updatePaymentWithPSU(String paymentInitReqId, String psuUsername) {

        PaymentManagementDAO paymentManagementDAO = new PaymentManagementDAO();
        paymentManagementDAO.updatePaymentInitiationWithPSU(paymentInitReqId, psuUsername);
    }

    /**
     * update the db with debtor bank details relevant to a payment initiation when psu confirms his bank.
     *
     * @param paymentInitReqId
     * @param bankUid
     * @return
     */
    public InternalResponse updatePaymentDebtorBank(String paymentInitReqId, String bankUid) {

        PaymentManagementDAO paymentManagementDAO = new PaymentManagementDAO();
        return paymentManagementDAO.updatePaymentInitiationWithDebtorBank(paymentInitReqId, bankUid);

    }

    /**
     * update the db with debtor bank details relevant to a payment initiation when psu confirms his bank account.
     *
     * @param paymentInitReqId
     * @param bankAccount
     * @return
     */
    public InternalResponse updatePaymentDebtorAccountData(String paymentInitReqId, BankAccount bankAccount) {

        if (bankAccount != null) {
            PaymentManagementDAO paymentManagementDAO = new PaymentManagementDAO();
            return paymentManagementDAO.updatePaymentInitiationWithDebtorAccount(paymentInitReqId, bankAccount);
        } else {
            //if user has skipped the selection of debtor account - no database access is performed
            return new InternalResponse(Constants.ACCOUNT_NOT_SPECIFIED, true);
        }

    }


    /*
    ========================================================================================================
    This section handles the processing the payment to invoke the payment initiation endpoint of debtor bank
    ========================================================================================================
    */

    /**
     * Invoke the debtor bank APIs, initiate a payment.
     * & return the authorization url as the response to the PSU for redirection to the bank.
     *
     * @return InternalResponse
     */
    public InternalResponse processPaymentInitiationWithBank() {

        this.paymentMediator = new PaymentMediator();

        log.info("Start to initiate the payment at debtor bank");
        //selects the required PISP flow.
        paymentMediator.selectPispFlow(this.paymentInitiation.getCustomerBank());
        String paymentId = paymentMediator.invokeBankAPI(this.paymentInitiation);

        if (paymentId == null) {
            return new InternalResponse(ErrorMessages.ERROR_WHILE_INITIATING_PAYMENT, false);
        } else {
            InternalResponse responseWithURL = paymentMediator.getAuthorizationUrl(paymentId);
            log.info("Auth URL" + responseWithURL.getMessage());
            return responseWithURL;
        }
    }

    /*
    ===================================================================================
    This section handles the processing the payment after PSU has authorize the payment
    ===================================================================================
    */

    /**
     * Accept the auth code and idToken generated after the PSU authorization of payment.
     *
     * @param paymentInitReqId
     * @param code
     * @param idToken
     */
    public void setAuthCodeForThePayment(String paymentInitReqId, String code, String idToken) {

        this.paymentInitReqId = paymentInitReqId;
        this.code = code;
        this.idToken = idToken;
    }

    /**
     * The rest of the payment process will be continued.
     * This is called once pisp receives the auth code after PSU authorization for the payment.
     *
     * @return
     */
    public InternalResponse processPSUAuthorizationAndSubmit() {

        retrievePaymentInitiationData(this.paymentInitReqId);
        AuthCodeVerification authCodeVerification = new AuthCodeVerification(this.idToken, this.paymentInitiation);
        authCodeVerification.verifyTheIdTokenAndPaymentData();

        this.paymentMediator = new PaymentMediator();
        this.paymentMediator.selectPispFlow(this.paymentInitiation.getCustomerBank());
        InternalResponse resultOfPaymentSubmission = paymentMediator.
                processPaymentAfterPSUAuthorization(this.paymentInitiation, code);
        InternalResponse response;
        if (resultOfPaymentSubmission.isOperationSuccessful()) {

            response = new InternalResponse(this.paymentInitiation, true);
            log.info(resultOfPaymentSubmission.getMessage());
            return response;
        } else {
            response = new InternalResponse(this.paymentInitiation, false);
            log.info(resultOfPaymentSubmission.getMessage());
            return response;
        }

    }

    /*
    ============================================================================
    This section handles the GET request to verify the completion of the payment
    ============================================================================
    */

    /**
     * check the status of payment for the verification of payment completion at the debtor bank.
     *
     * @param payment
     * @return
     */
    public InternalResponse getTheStatusOfPayment(Payment payment) {

        boolean result = this.paymentMediator.getPaymentStatus(payment.getPaymentId());
        if (result) {
            PaymentManagementDAO paymentManagementDAO = new PaymentManagementDAO();
            paymentManagementDAO.updatePaymentAsCompleted(payment.getPaymentInitReqId());
            this.paymentInitiation.setPaymentStatus(Constants.PAYMENT_STATUS_7);
            return new InternalResponse(this.paymentInitiation, true);
        } else {
            return new InternalResponse(this.paymentInitiation, false);

        }
    }
}
