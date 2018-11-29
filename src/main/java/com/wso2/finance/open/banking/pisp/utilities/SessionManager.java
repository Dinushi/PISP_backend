/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 *
 */
package com.wso2.finance.open.banking.pisp.utilities;

import com.wso2.finance.open.banking.pisp.dao.SessionTokenManagementDAO;
import com.wso2.finance.open.banking.pisp.models.InternalResponse;
import com.wso2.finance.open.banking.pisp.utilities.constants.Constants;
import com.wso2.finance.open.banking.pisp.utilities.constants.ErrorMessages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class that handles the user sessions.
 */
public class SessionManager {

    private static Log log = LogFactory.getLog(SessionManager.class);

    /**
     * generate the session token for the logging PSU user.
     * map it with the previously initiated payment relevant to the PSU.
     *
     * @param psuUsername
     * @param paymentInitReqId
     * @return
     */
    public static String generateSessionTokenForPSU(String psuUsername, String paymentInitReqId) {

        SessionTokenManagementDAO sessionTokenManagementDAO = new SessionTokenManagementDAO();
        return sessionTokenManagementDAO.generateSessionTokenForPSU(psuUsername, paymentInitReqId);
    }

    /**
     * validate the received session key of PSU and get the relevant paymentInitReqId.
     *
     * @param username
     * @param receivedSessionToken
     * @return
     */
    public static InternalResponse getPaymentInitRequestIdFromSession(String username, String receivedSessionToken) {

        SessionTokenManagementDAO sessionTokenManagementDAO = new SessionTokenManagementDAO();
        InternalResponse response = sessionTokenManagementDAO.getSessionTokenForPsu(username);
        if (response.isOperationSuccessful()) {
            String[] sessionDetails = (String[]) response.getData();
            if (receivedSessionToken.equals(sessionDetails[0])) {
                if (log.isDebugEnabled()) {
                    log.debug("Payment ini req id  relevant to the PSU :" + sessionDetails[1]);
                }
                return new InternalResponse(sessionDetails[1], true);
            } else {
                return new InternalResponse(ErrorMessages.SESSION_TOKEN_MISMATCH, false);
            }
        } else {
            return response;
        }

    }

    /**
     * generate the session token for the logging E-shop user.
     *
     * @param eShopUsername
     * @return
     */
    public static String generateSessionTokenForEShop(String eShopUsername) {

        SessionTokenManagementDAO sessionTokenManagementDAO = new SessionTokenManagementDAO();
        return sessionTokenManagementDAO.generateSessionTokenForEShop(eShopUsername);
    }

    /**
     * retrieve the session token for the e-shop and validate it.
     *
     * @param username
     * @param receivedSessionToken
     * @return
     */
    public static InternalResponse validateSessionTokenOfEShop(String username, String receivedSessionToken) {

        SessionTokenManagementDAO sessionTokenManagementDAO = new SessionTokenManagementDAO();
        InternalResponse response = sessionTokenManagementDAO.getSessionTokenForLoggedInEShopUser(username);
        if (response.isOperationSuccessful()) {
            if (receivedSessionToken.equals(response.getMessage())) {
                return new InternalResponse(Constants.USER_AUTHORIZED, true);
            } else {
                return new InternalResponse(ErrorMessages.SESSION_TOKEN_MISMATCH, false);
            }
        } else {
            return response;
        }
    }

}
