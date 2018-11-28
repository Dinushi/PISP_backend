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
package pisp.utilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.dao.SessionTokenManagementDAO;
import pisp.models.PispInternalResponse;
import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;

import java.sql.PseudoColumnUsage;

public class SessionManager {


    private static Log log = LogFactory.getLog(SessionManager.class);

    /**
     * generate the session token for the logging PSU user and mp it with the previously initiated payment relevant to the PSU
     * @param psu_username
     * @param paymentInitReqId
     * @return
     */
    public static String generateSessionTokenForPSU(String psu_username, String paymentInitReqId){
        SessionTokenManagementDAO sessionTokenManagementDAO=new SessionTokenManagementDAO();
        return sessionTokenManagementDAO.generateSessionTokenForPSU(psu_username, paymentInitReqId);
    }


    /**
     * validate the received session key of PSU and get the relevant paymentInitReqId
     *
     * @param username
     * @param receivedSessionToken
     * @return
     */
    public static PispInternalResponse getPaymentInitRequestIdFromSession(String username, String receivedSessionToken){
        SessionTokenManagementDAO sessionTokenManagementDAO=new SessionTokenManagementDAO();
        PispInternalResponse response=sessionTokenManagementDAO.getSessionTokenForPsu(username);
        if(response.isOperationSuccessful()){
            String[] sessionDetails=(String[]) response.getData();
            if(receivedSessionToken.equals(sessionDetails[0])){
                if (log.isDebugEnabled()) {
                    log.debug("Payment ini req id  relevant to the PSU :" + sessionDetails[1]);
                }
                return new PispInternalResponse(sessionDetails[1],true);
            }else{
                return  new PispInternalResponse(ErrorMessages.SESSION_TOKEN_MISMATCH,false);
            }
        }else{
            return response;
        }

    }


    /**
     * generate the session token for the logging E-shop user
     * @param eShopUsername
     * @return
     */
    public static String generateSessionTokenForEShop(String eShopUsername){
        SessionTokenManagementDAO sessionTokenManagementDAO=new SessionTokenManagementDAO();
        return  sessionTokenManagementDAO.generateSessionTokenForEShop(eShopUsername);
    }

    /**
     * retrieve the session token for the e-shop and validate it
     * @param username
     * @param receivedSessionToken
     * @return
     */
    public static PispInternalResponse validateSessionTokenOfEShop(String username, String receivedSessionToken){
        SessionTokenManagementDAO sessionTokenManagementDAO=new SessionTokenManagementDAO();
        PispInternalResponse response=sessionTokenManagementDAO.getSessionTokenForLoggedInEShopUser(username);
        if(response.isOperationSuccessful()){
            if(receivedSessionToken.equals(response.getMessage())){
                return new PispInternalResponse(Constants.USER_AUTHORIZED,true);
            }else{
                return  new PispInternalResponse(ErrorMessages.SESSION_TOKEN_MISMATCH,false);
            }
        }else{
            return response;
        }
    }


}
