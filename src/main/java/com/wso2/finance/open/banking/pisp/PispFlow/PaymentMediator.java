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

package com.wso2.finance.open.banking.pisp.PispFlow;

import com.wso2.finance.open.banking.pisp.exception.PispException;
import com.wso2.finance.open.banking.pisp.models.InternalResponse;
import com.wso2.finance.open.banking.pisp.models.Payment;
import com.wso2.finance.open.banking.pisp.utilities.constants.ErrorMessages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wso2.finance.open.banking.pisp.dao.BankManagementDAO;
import com.wso2.finance.open.banking.pisp.models.DebtorBank;

/**
 * This is to map a selected ASPSP/debtor bank with the relevant PISPFlow and execute it.
 * Based on the open banking specification followed by the bank.
 */
public class PaymentMediator {

    private Log log = LogFactory.getLog(PaymentMediator.class);
    private BankManagementDAO banksManagementDAO;
    private PispFlow pispFlow;

    public PaymentMediator() {

        this.banksManagementDAO = new BankManagementDAO();
    }

    /**
     * create the pisp flow object according to the spec of bank.
     *
     * @param customerBank
     * @return
     */
    public void selectPispFlow(DebtorBank customerBank) {

        this.pispFlow = PispFlowFactory.getPispFlow(customerBank.getSpecForOB(), customerBank.getBankUid());
    }

    /**
     * invoke the payment Initiation Endpoint of the debtor bank.
     *
     * @param payment
     * @return
     */
    public String invokeBankAPI(Payment payment) {

        pispFlow.getApplicationAccessToken();
        return pispFlow.invokePaymentInitiation(payment);

    }

    /**
     * Now the Payment has initiated at bank and this step is to generate authorization URL
     * which will be used to redirect the PSU to authorize the payment at ASPSP.
     *
     * @param paymentID
     * @return
     */
    public InternalResponse getAuthorizationUrl(String paymentID) {

        String url = pispFlow.generateAuthorizationURL(paymentID);
        InternalResponse internalResponse;
        if (url == null) {
            internalResponse = new InternalResponse(ErrorMessages.ERROR_WHILE_GETTING_AUTH_URL, false);
        } else {
            internalResponse = new InternalResponse(url, true);
        }
        return internalResponse;

    }

    /**
     * This method is executed when the PISP receives the code-grant after PSU authorization has completed.
     * Again create a matching PISP Flow to process the rest of payment process with bank.
     * Exchanging Auth code with bank.
     * Retrieval and saving user access token.
     * Submitting the payment to the bank.
     *
     * @param payment the payment which the auth code belongs to.
     * @param code    the auth code sent from ASPSP attached to redirect URL.
     * @return The response which includes the payment status.
     */
    public InternalResponse processPaymentAfterPSUAuthorization(Payment payment, String code) throws PispException {

        InternalResponse paymentSubmissionResult = this.pispFlow.processPaymentAfterPSUAuthorization(code, payment);
        if (log.isDebugEnabled()) {
            log.debug("Finished the payment submission to the bank");
        }
        return paymentSubmissionResult;
    }

    /**
     * Sent a GET request to the ASPSP to query about the status of a payment initiation made.
     *
     * @param paymentId
     * @return
     */
    public boolean getPaymentStatus(String paymentId) {

        boolean result = this.pispFlow.getTransactionStatusOfPayment(paymentId);
        return result;
    }
}

