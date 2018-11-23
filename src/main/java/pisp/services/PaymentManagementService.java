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

package pisp.services;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.PispFlow.PaymentMediator;
import pisp.dao.PaymentManagementDAO;
import pisp.exception.PispException;
import pisp.models.*;

import pisp.utilities.AuthCodeVerification;
import pisp.utilities.SessionManager;
import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.UUID;

public class PaymentManagementService {
    private Log log = LogFactory.getLog(PaymentManagementService.class);


    private Payment paymentInitiation;
    private boolean error;


    private String code;
    private String paymentInitReqId;
    private String idToken;

    private PaymentMediator paymentMediator;


    public PaymentManagementService(){
        log.info("Initializing the payment management service");
        this.error=false;
    }

    public PaymentManagementService(Payment paymentInitiationRequest){
        log.info("Initializing the payment management service");
        this.error=false;
        this.paymentInitiation=paymentInitiationRequest;

        generateUniquePaymentInitiationId();

    }

    public PaymentManagementService (String paymentInitReqId, String code,String idToken){
        log.info("Initializing the payment management service");
        this.error=false;
        this.paymentInitReqId=paymentInitReqId;
        this.code=code;
        this.idToken=idToken;
    }



    /*
    =============================================================================================
    This section responsible of responding to a payment initiation request by a registered E-shop
    =============================================================================================
    */

    /**
     * generate a unique id for each payment initiation
     */
    private void generateUniquePaymentInitiationId(){
        UUID uuid = UUID.randomUUID();
        this.paymentInitReqId= uuid.toString();
        log.info("The payment Init Req Id generated for this payment  "+this.paymentInitReqId);
        this.paymentInitiation.setPaymentInitReqId(this.paymentInitReqId);
    }

    public PispInternalResponse storePaymentDataInDB() {
        this.paymentInitiation.setPaymentStatus(Constants.PAYMENT_STATUS_1);
        PaymentManagementDAO paymentManagementDAO=new PaymentManagementDAO();
        boolean result=paymentManagementDAO.addPaymentInitiation(paymentInitiation);
        //SessionManager.addSessionForPSU(sessionId, this.paymentInitReqId);
        if(result){
            return new PispInternalResponse(paymentInitReqId,true);
        }
        return new PispInternalResponse(ErrorMessages.DB_SAVING_ERROR,false);
    }


    public void updatePaymentWithPSU(String paymentInitReqId, String psu_username){
        PaymentManagementDAO paymentManagementDAO=new PaymentManagementDAO();
        paymentManagementDAO.updatePaymentInitiationWithPSU(paymentInitReqId, psu_username);
    }


    /*
    ====================================================================================
    This section handles the debtor bank & account selection by customer of the payment
    ====================================================================================
    */

    /**
     * update the db with debtor bank details relevant to a payment initiation when psu confirms his bank
     * @param paymentInitReqId
     * @param bank_uid
     * @return
     */
    public PispInternalResponse updatePaymentDebtorBank(String paymentInitReqId,String bank_uid) {

        PaymentManagementDAO paymentManagementDAO=new PaymentManagementDAO();
        return paymentManagementDAO.updatePaymentInitiationWithDebtorBank(paymentInitReqId, bank_uid);

    }

    /**
     * update the db with debtor bank details relevant to a payment initiation when psu confirms his bank account
     * @param paymentInitReqId
     * @param bankAccount
     * @return
     */
    public PispInternalResponse updatePaymentDebtorAccountData(String paymentInitReqId,BankAccount bankAccount) {
        if(bankAccount!=null){
            PaymentManagementDAO paymentManagementDAO=new PaymentManagementDAO();
            return paymentManagementDAO.updatePaymentInitiationWithDebtorAccount(paymentInitReqId, bankAccount);
        }else{
            //if user has skipped the selection of debtor account - no database access is performed
            return new PispInternalResponse("AccountIsNotSpecified",true);
        }

    }

     /*
    ========================================================================================================
    This section handles the processing the payment to invoke the payment initiation endpoint of debtor bank
    ========================================================================================================
    */

    /**
     * retrieve the details of particular payment initiation when psu confirms his bank / bank account
     * Afetr this step, a payment initiation request is sent to debtor bank
     *
     * @param paymentInitReqId
     * @return
     */
    public Payment retrievePaymentInitiationData(String paymentInitReqId) {
        PaymentManagementDAO paymentManagementDAO=new PaymentManagementDAO();
        Payment paymentRetrieved=paymentManagementDAO.retrievePayment(paymentInitReqId);
        log.info("Payment Data retrieved");
        this.paymentInitiation=paymentRetrieved;
        return paymentRetrieved;
    }


    /**
     * Invoke the debtor bank APIs, initiate a payment
     * & return the authorization url as the response to the PSU for redirection to the bank.
     *
     * @return PispInternalResponse
     */
    public PispInternalResponse processPaymentInitiationWithBank(){
        this.paymentMediator=new PaymentMediator();


        paymentMediator.selectPispFlow(this.paymentInitiation.getCustomerBank());//selects the required PISP flow
        String paymentId=paymentMediator.invokeBankAPI(this.paymentInitiation); //start the operations with the bank.The paymentId is returned as the response

        if(paymentId==null){
            return new PispInternalResponse(ErrorMessages.ERROR_WHILE_INITIATING_PAYMENT ,false);
        }else{
            PispInternalResponse responseWithURL=paymentMediator.getAuthorizationUrl(paymentId);
            log.info("Auth URL"+responseWithURL.getMessage());
            return responseWithURL;
        }

        //need a proper way to handle these payment initiation requests and submissions with banks

    }

    /*
    ===================================================================================
    This section handles the processing the payment after PSU has authorize the payment
    ===================================================================================
    */

    /**
     * Once received the auth code after PSU authorization for the payment, the rest of the PISP process will be continued
     * @return
     */
      public PispInternalResponse processPSUAuthorization(){
          retrievePaymentInitiationData(this.paymentInitReqId);
          AuthCodeVerification authCodeVerification=new AuthCodeVerification(this.idToken,this.paymentInitiation);//????????????????need to complete this class
          authCodeVerification.verifyTheIdTokenAndPaymentData();


          this.paymentMediator=new PaymentMediator();
          this.paymentMediator.selectPispFlow(this.paymentInitiation.getCustomerBank());
          boolean result=paymentMediator.processPaymentAfterPSUAuthorization(this.paymentInitReqId, this.paymentInitiation, code);

          PispInternalResponse response=new PispInternalResponse(paymentInitiation,result);
          log.info("The result of the payment submission: "+result);
          return response;
      }

    /*
    ============================================================================
    This section handles the GET request to verify the completion of the payment
    ============================================================================
    */

    /**
     * check the status of payment again for the verification.
     * @param payment
     * @return
     */
    public PispInternalResponse getTheStatusOfPayment(Payment payment){
        boolean result=this.paymentMediator.getPaymentStatus(payment.getPaymentId());
        if(result){
            PaymentManagementDAO paymentManagementDAO=new PaymentManagementDAO();
            paymentManagementDAO.updatePaymentAsCompleted(payment.getPaymentInitReqId());
            return new PispInternalResponse(payment.getRedirectURI(),true);
        }
        return new PispInternalResponse(payment.getRedirectURI(),false);


    }





}
