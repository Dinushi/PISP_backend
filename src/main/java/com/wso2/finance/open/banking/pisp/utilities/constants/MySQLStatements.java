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
package com.wso2.finance.open.banking.pisp.utilities.constants;

/**
 * All MySQL Queries reside here.
 */

public class MySQLStatements {

    //PSU Related

    public static final String GET_PSU = "SELECT * FROM PSU WHERE PSU_USERNAME = ?;";

    public static final String ADD_NEW_PSU = "INSERT INTO PSU (PSU_USERNAME ,FIRST_NAME," +
            "LAST_NAME,PASSWORD_HASH, SALT, EMAIL) VALUES (?,?, ?, ?, ?, ?);";

    public static final String GET_PASSWORD_PSU = "SELECT PSU_USERNAME, FIRST_NAME, PASSWORD_HASH, " +
            "SALT FROM PSU WHERE PSU_USERNAME=?;";

    //PSU session management

    public static final String ADD_SESSION_TOKEN_FOR_PSU = "INSERT INTO PSU_SESSION_TOKENS (PSU_USERNAME," +
            " SESSION_KEY,PAYMENT_INIT_REQ_ID, EXPIRY_TIME) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
            "  SESSION_KEY=?,PAYMENT_INIT_REQ_ID=?, EXPIRY_TIME=?;";
    //on each duplicate username, the session id only updated. i.e. Only one entry is maintained for one usert

    public static final String GET_PSU_SESSION = "SELECT SESSION_KEY,PAYMENT_INIT_REQ_ID,EXPIRY_TIME " +
            " FROM PSU_SESSION_TOKENS WHERE PSU_USERNAME=?;";

    //E-shop related

    public static final String ADD_NEW_E_SHOP = "INSERT INTO E_SHOPS  (E_SHOP_USERNAME ,E_SHOP_NAME," +
            "REGISTERED_NO, REGISTERED_BUSINESS_NAME,REGISTERED_COUNTRY, ECOMMERCE_MARKETPLACE_CATEGORY, " +
            "PASSWORD_HASH, SALT, CLIENT_ID,CLIENT_SECRET, EMAIL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String GET_E_SHOP = "SELECT * FROM E_SHOPS WHERE E_SHOP_USERNAME = ?;";

    public static final String UPDATE_E_SHOP = "UPDATE E_SHOPS SET E_SHOP_NAME=? , EMAIL=?  WHERE E_SHOP_USERNAME = ?;";

    public static final String GET_PASSWORD_ESHOP = "SELECT E_SHOP_USERNAME, E_SHOP_NAME, PASSWORD_HASH, SALT " +
            "FROM E_SHOPS WHERE E_SHOP_USERNAME=?;";

    public static final String GET_MERCHANT_INFO = "SELECT * FROM MERCHANTS WHERE MERCHANT_IDENTIFICATION_BY_ESHOP = ?;";

    public static final String GET_CREDITOR_ACCOUNT_INFO = "SELECT * FROM CREDITOR_ACCOUNTS WHERE MERCHANT_ID=?;";

    public static final String GET_MARKETPLACE_CATEGORY = "SELECT ECOMMERCE_MARKETPLACE_CATEGORY FROM E_SHOPS WHERE " +
            "E_SHOP_USERNAME=?;";

    public static final String UPDATE_CREDITOR_ACCOUNT = "UPDATE CREDITOR_ACCOUNTS SET BANK_IDENTIFICATION_SCHEME = ? , " +
            "BANK_IDENTIFICATION_NO = ?, BANK_NAME = ?, ACCOUNT_IDENTIFICATION_SCHEME = ?, ACCOUNT_IDENTIFICATION_NO = ?," +
            " ACCOUNT_OWNER_NAME = ? WHERE MERCHANT_ID = ?;";

    //E-shop session management

    public static final String ADD_SESSION_TOKEN_FOR_ESHOP = "INSERT INTO ESHOP_SESSION_TOKENS (E_SHOP_USERNAME, " +
            "EXPIRY_TIME, SESSION_KEY) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE EXPIRY_TIME=?, SESSION_KEY=?;";
    //on each duplicate username, the session id only updated. i.e. Only one entry is maintained for one user

    public static final String DELETE_ESHOP = "DELETE FROM E_SHOPS WHERE E_SHOP_USERNAME = ?;";

    public static final String GET_E_SHOP_USER_SESSION = "SELECT SESSION_KEY,EXPIRY_TIME FROM ESHOP_SESSION_TOKENS " +
            "WHERE E_SHOP_USERNAME=?;";

    //bank related

    public static final String ADD_NEW_BANK = "INSERT INTO BANKS (BANK_UID, BANK_IDENTIFICATION_SCHEME," +
            "BANK_IDENTIFICATION_NO, BANK_NAME , SPEC_FOR_OB, STATUS) VALUES (?, ?, ?, ?, ?, ?);";

    public static final String GET_A_BANK = "SELECT * FROM BANKS WHERE BANK_UID=? AND STATUS=?;";

    public static final String GET_ALL_BANK = "SELECT * FROM BANKS WHERE STATUS=?;";

    //related to storing payment data at PISP with payment initiation POST request

    public static final String GET_MERCHANT_ID_IF_EXIST = "SELECT MERCHANT_ID FROM MERCHANTS " +
            "WHERE E_SHOP_USERNAME = ? AND MERCHANT_IDENTIFICATION_BY_ESHOP = ? ;";

    public static final String ADD_NEW_MERCHANT = "INSERT INTO MERCHANTS  (MERCHANT_ID , E_SHOP_USERNAME, " +
            "MERCHANT_IDENTIFICATION_BY_ESHOP,MERCHANT_NAME, MERCHANT_CATEGORY_CODE) VALUES (?, ?, ?, ?, ?);";

    public static final String GET_CREDITOR_ACCOUNT_UID_IF_EXIST = "SELECT CREDITOR_ACCOUNT_UID FROM CREDITOR_ACCOUNTS " +
            "WHERE BANK_IDENTIFICATION_SCHEME = ? AND BANK_IDENTIFICATION_NO = ? AND ACCOUNT_IDENTIFICATION_SCHEME=?" +
            " AND ACCOUNT_IDENTIFICATION_NO=? AND MERCHANT_ID=?;";

    public static final String GET_CREDITOR_ACCOUNT_UID_FOR_SINGLE_VENDOR = "SELECT CREDITOR_ACCOUNT_UID FROM" +
            " CREDITOR_ACCOUNTS WHERE MERCHANT_ID=?;";

    public static final String ADD_NEW_CREDITOR_ACCOUNT = "INSERT INTO CREDITOR_ACCOUNTS  (CREDITOR_ACCOUNT_UID , " +
            "MERCHANT_ID, BANK_IDENTIFICATION_SCHEME,BANK_IDENTIFICATION_NO, BANK_NAME,ACCOUNT_IDENTIFICATION_SCHEME, " +
            "ACCOUNT_IDENTIFICATION_NO,ACCOUNT_OWNER_NAME) VALUES (?, ?, ?, ?, ?,?,?,?);";

    public static final String ADD_NEW_PAYMENT_INITIATION = "INSERT INTO PAYMENTS  (PAYMENT_INIT_REQ_ID , E_SHOP_USERNAME, " +
            "MERCHANT_ID, TRANSACTION_ID,CURRENCY, PAYMENT_AMOUNT, CUSTOMER_IDENTIFICATION_BY_ESHOP, DELIVERY_ADDRESS_JSON," +
            "CREDITOR_ACCOUNT_UID, REDIRECT_URI, PAYMENT_STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    //related to updating payment when the PSU logs-in

    public static final String UPDATE_PAYMENT_INITIATION_WITH_PSU = "UPDATE PAYMENTS SET PSU_USERNAME = ? " +
            " WHERE PAYMENT_INIT_REQ_ID = ?;";

    //related to updating payment when psu selects his debtor bank

    public static final String UPDATE_PAYMENT_INITIATION_WITH_DEBTOR_BANK_UID = "UPDATE PAYMENTS SET DEBTOR_BANK_UID = ?, " +
            "PAYMENT_STATUS=? WHERE PAYMENT_INIT_REQ_ID = ?;";

    //related to updating payment when psu selects his debtor account

    public static final String GET_DEBTOR_ACCOUNT_UID_IF_EXIST = "SELECT DEBTOR_ACCOUNT_UID FROM DEBTOR_ACCOUNTS " +
            "WHERE ACCOUNT_IDENTIFICATION_SCHEME = ? AND ACCOUNT_IDENTIFICATION_NO=?;";

    public static final String ADD_NEW_DEBTOR_ACCOUNT = "INSERT INTO DEBTOR_ACCOUNTS  (DEBTOR_ACCOUNT_UID ," +
            "ACCOUNT_IDENTIFICATION_SCHEME, ACCOUNT_IDENTIFICATION_NO,ACCOUNT_OWNER_NAME) VALUES (?, ?, ?, ?);";

    public static final String UPDATE_PAYMENT_INITIATION_WITH_DEBTOR_ACCOUNT_UID = "UPDATE PAYMENTS SET " +
            "DEBTOR_ACCOUNT_UID = ?, PAYMENT_STATUS=? WHERE PAYMENT_INIT_REQ_ID = ?;";

    //related to retrieval of payment data stored in db

    public static final String GET_PAYMENT_INITIATION = "SELECT * FROM PAYMENTS " +
            "WHERE PAYMENT_INIT_REQ_ID = ?;";

    public static final String GET_CREDITOR_BANK_DETAILS = "SELECT * FROM CREDITOR_ACCOUNTS " +
            "WHERE CREDITOR_ACCOUNT_UID = ?;";

    public static final String GET_DEBTOR_ACCOUNT_DETAILS = "SELECT * FROM DEBTOR_ACCOUNTS " +
            "WHERE DEBTOR_ACCOUNT_UID = ?;";

    //related to payment initiation at Bank

    public static final String UPDATE_PAYMENT_ID = "UPDATE PAYMENTS SET PAYMENT_ID = ?, PAYMENT_STATUS=?" +
            " WHERE PAYMENT_INIT_REQ_ID = ?;";

    public static final String UPDATE_PAYMENT_SUBMISSION_ID = "UPDATE PAYMENTS SET PAYMENT_SUB_ID  = ?," +
            " PAYMENT_STATUS=? WHERE PAYMENT_INIT_REQ_ID = ?;";

    public static final String UPDATE_PAYMENT_INITIATION_AS_COMPLETED = "UPDATE PAYMENTS SET PAYMENT_STATUS=? " +
            "WHERE PAYMENT_INIT_REQ_ID = ?;";

    // Application Tokens related

    public static final String ADD_A_NEW_APPLICATION_TOKEN = "INSERT INTO APPLICATION_TOKENS " +
            "(BANK_UID,  VALID_TILL, TOKEN) VALUES (?, ?, ?);";

    public static final String GET_APPLICATION_TOKEN = "SELECT TOKEN, VALID_TILL FROM APPLICATION_TOKENS WHERE" +
            " BANK_UID = ? ORDER BY TIMESTAMP DESC LIMIT 1;";

    //uk payment initiation related

    public static final String GET_PAYMENT_ID = "SELECT PAYMENT_ID FROM PAYMENTS WHERE PAYMENT_INIT_REQ_ID = ? ;";

    public static final String UPDATE_PAYMENT_WITH_AUTH_CODE = "UPDATE PAYMENTS SET AUTHORIZATION_CODE = ? " +
            "WHERE PAYMENT_INIT_REQ_ID = ?;";

    // Merchant related

    public static final String GET_MERCHANT = "SELECT * FROM MERCHANTS WHERE MERCHANT_ID = ?;";

    //payment history related

    public static final String FILTER_PAYMENTS = "SELECT PAYMENT_INIT_REQ_ID  FROM PAYMENTS WHERE  " +
            "PAYMENT_STATUS=? AND E_SHOP_USERNAME =? AND DATE_TIME >= ? AND  DATE_TIME <= ? ORDER BY DATE_TIME DESC;";

}
