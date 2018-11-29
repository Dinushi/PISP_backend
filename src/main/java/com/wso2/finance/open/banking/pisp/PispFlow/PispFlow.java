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

/**
 * This interface contain the methods required to initiate and submit payments to a selected ASPSP.
 */
public interface PispFlow {

    /**
     * Load the generic set of attributes needed for API invocations from properties file.
     *
     * @param bankUid the bankId to generate configurations.
     * @throws PispException If configuration reading errs.
     */
    void loadBasicConfigurations(String bankUid);

    /**
     * Load the bank specific attributes needed for API invocations from properties file.
     *
     * @param bankUid the bankId to generate configurations.
     * @throws PispException If configuration reading errs.
     */

    void loadConfigurations(String bankUid);

    /**
     * check for any unexpired token and if not exists call to retrieve a new token.
     */
    void getApplicationAccessToken();

    /**
     * this will call the token api of bank, get a new access token and save to the database.
     */
    void retrieveAndSaveApplicationToken();

    /**
     * Invoke the payment Initiation API resource of the bank.
     * Request paymentInitiationID from bank APIs.
     *
     * @return The payment Initiation ID from bank.
     */
    String invokePaymentInitiation(Payment paymentdata);

    /**
     * save paymentInitiationId in the database.
     *
     * @param accountInitiationId
     * @param username
     */
    void savePaymentInitiationID(String accountInitiationId, String username);

    /**
     * Get the Banks URL to redirect PSU to initiate authorization flow.
     *
     * @return URL of the entry point to authorization flow.
     */
    String generateAuthorizationURL(String paymentId);

    /**
     * This will be implemented if any specification has a remaining process after PSU authorization.
     * eg: payment submission in UK.
     *
     * @return
     */
    InternalResponse processPaymentAfterPSUAuthorization(String code, Payment payment);

    /**
     * this will fetch the status of payment from bank and notify the customer and e-commerce site.
     *
     * @param paymentId
     * @return
     */
    boolean getTransactionStatusOfPayment(String paymentId);

}
