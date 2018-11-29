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
package pisp.utilities.constants;

/**
 * All the constants used in the application is here.
 */
public final class Constants {

    // Specific to application
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String CONTENT_TYPE = "application/json";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String TOKEN_API_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String CHARSET_HEADER = "charset";
    public static final String TOKEN_API_CHARSET = "ISO-8859-1";
    public static final String SIGNING_ALGORITHM = "SHA-256";
    public static final String ACCEPT_HEADER = "Accept";
    public static final String ACCEPT = "application/json";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_BEARER_HEADER = "Bearer ";
    public static final String APPLICATION_TOKEN = "Application Token";
    public static final String GRANT_TYPE = "authorization_code";
    public static final String GRANT_TYPE_CLIENT = "client_credentials";
    public static final String RESPONSE_TYPE = "code id_token";
    public static final String PROMPT = "login";
    //public static final String RESPONSE_TYPE = "code id_token token";
    public static final String NONCE = "n-0S6_WzA2M";
    public static final String X_FAPI_INTERACTION_ID_HEADER = "x-fapi-interaction-id";
    public static final String X_FAPI_FINANCIAL_ID = "x-fapi-financial-id";
    public static final String X_IDEMPOTENCY_KEY = "x-idempotency-key";
    public static final String PAYMENT_SCOPE = "payments";

    //specific for UK v2
    public static final String PAYMENTS_SCOPE = "payments openid";
    public static final String PAYMENTS_SCOPE_UK = "am_application_scope payments openid";
    public static final String UK_TRANSACTION_STATUS_AFTER_SUBMISSION = "AcceptedSettlementInProcess";
    public static final String UK_TRANSACTION_STATUS_COMPLETED = "AcceptedSettlementInProcess";

    //specific for BERLIN
    public static final String PAYMENTS_SCOPE_BERLIN = "am_application_scope payments openid";

    //related to idToken verification
    public static final String OPENBANKING_INTENT_ID = "openbanking_intent_id";
    public static final String VERIFIED = "The openbanking_intent_id matches with paymentId";

    // payment status handling at PISP
    public static final String PAYMENT_STATUS_1 = "InitiatedAtPisp";
    public static final String PAYMENT_STATUS_2 = "DebtorBankSpecified";
    public static final String PAYMENT_STATUS_3 = "DebtorAccountSpecified";
    public static final String PAYMENT_STATUS_4 = "InitiatedAtBank";
    public static final String PAYMENT_STATUS_5 = "SubmittedToPSUAuthorization";
    public static final String PAYMENT_STATUS_6 = "SubmittedToBank";
    public static final String PAYMENT_STATUS_7 = "Completed";

    //other
    public static final String E_SHOP = "e_shop";
    public static final String PSU = "psu";
    public static final String SINGLE_VENDOR = "single_vendor";
    public static final String SESSION_ID = "SESSIONID";
    public static final String BANK_STATUS_ACTIVE = "active";
    public static final String BANK_STATUS_DEACTIVE = "de-active";
    public static final String DEBTOR_ACC_REQUIRED = "required";
    public static final String DEBTOR_ACC_NOT_REQUIRED = "notRequired";
    public static final String CLIENT_ID_EXISTS = "Client-Id found in database";
    public static final String PURCHASE_ID = "Purchase-Id";
    public static final String ESHOP_DELETED = "e-shop user is successfully registered";
    public static final String BANK_ADDED = "The new bank is added to db successfully";
    public static final String BANK_REMOVED = "The bank is removed successfully";
    public static final String USER_AUTHORIZED = "User Authorized";
    public static final String PAYMENT_COMPLETED = "The payment is completed";
    public static final String PAYMENT_COMPLETION_FAILED = "The payment is failed to submit";
    public static final String ADDED_BANK_ACCOUNT_UID = "BankAccountUID updated";
    public static final String ACCOUNT_NOT_SPECIFIED = "AccountIsNotSpecified";
    public static final String REGISTERED_SUCCESSFULLY = "The user is registered successfully";

    public static final String PAYMENT_COMPLETION_CODE =  "failed";
    public static final String PAYMENT_FAILURE_CODE =  "completed";

    //Supported OB specs
    public static final String OPEN_BANKING_UK = "UK";
    public static final String OPEN_BANKING_BERLIN = "BERLIN";



}
