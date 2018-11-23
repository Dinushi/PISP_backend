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

package pisp.PispFlow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.dao.BankManagementDAO;
import pisp.exception.PispException;
import pisp.models.Bank;
import pisp.models.DebtorBank;
import pisp.models.Payment;
import pisp.models.PispInternalResponse;
import pisp.utilities.constants.ErrorMessages;

public class PaymentMediator {

    private Log log = LogFactory.getLog(PaymentMediator.class);

    private  BankManagementDAO banksManagementDAO;

    private PispFlow pispFlow;

    public PaymentMediator(){
        this.banksManagementDAO=new BankManagementDAO();
    }


    /**
     * get the pisp flow object according to the spec of bank
     * Within the constructor of PISP flow, access tokens are generated
     * @param customerBank
     * @return
     */
    public void selectPispFlow( DebtorBank customerBank) {

        this.pispFlow=PispFlowFactory.getPispFlow(customerBank.getSpecForOB(),customerBank.getBankUid());
    }

    /**
     * invoke the payment Initiation Endpoint of the debtor bank
     *
     * @param payment
     * @return
     */
    public String invokeBankAPI(Payment payment){
        pispFlow.getApplicationAccessToken();
        return pispFlow.invokePaymentInitiation(payment);

    }

    /**
     * Now the Payment has initiated at bank and this step is to generate authorization URL to redirect the PSU
     *
     * The redirection URL needs to redirect PSU to begin authorization flow.
     * @param paymentID
     * @return
     */
    public PispInternalResponse getAuthorizationUrl(String paymentID) {
        log.info("Processing request for Authorization URL for " + paymentID);
        String url = pispFlow.generateAuthorizationURL(paymentID);
        PispInternalResponse pispInternalResponse;
        if(url==null){
            pispInternalResponse=new PispInternalResponse(ErrorMessages.ERROR_WHILE_GETTING_AUTH_URL, false);
        }else{
            pispInternalResponse=new PispInternalResponse(url, true);
            log.info("Processed request & url generated as "+url);
        }
        return pispInternalResponse ;


    }

    /**
     * Again create a matching PISP Flow to process the rest of payment process with bank.
     * exchanging Auth code with bank/retrieval and saving user access token/submitting the payment
     *
     * @param paymentInitReqId username to store the token in.
     * @param payment   the bank access token belongs to.
     * @param code     the token.
     */
    public boolean processPaymentAfterPSUAuthorization(String paymentInitReqId, Payment payment, String code) throws PispException {
        String bankUid=payment.getCustomerBank().getBankUid();
        log.info("Processing request for getting user access Token for " + bankUid);

        Boolean result=this.pispFlow.processPaymentAfterPSUAuthorization(code, payment);
        log.info("Processed request");

        return result;
    }

    public boolean getPaymentStatus(String paymentId){
        boolean result=pispFlow.getTransactionStatusOfPayment(paymentId);
        return result;
    }
}

