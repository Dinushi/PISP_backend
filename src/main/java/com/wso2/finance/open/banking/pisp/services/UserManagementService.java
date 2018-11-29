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
package com.wso2.finance.open.banking.pisp.services;

import com.wso2.finance.open.banking.pisp.dao.UserManagementDAO;
import com.wso2.finance.open.banking.pisp.exception.PispException;
import com.wso2.finance.open.banking.pisp.models.EShop;
import com.wso2.finance.open.banking.pisp.models.InternalResponse;
import com.wso2.finance.open.banking.pisp.models.PSU;
import com.wso2.finance.open.banking.pisp.utilities.constants.Constants;
import com.wso2.finance.open.banking.pisp.utilities.constants.ErrorMessages;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is to handle user related operations.
 */
public class UserManagementService {

    private Log log = LogFactory.getLog(UserManagementService.class);

    UserManagementDAO userManagementDAO;

    public UserManagementService() {

        this.userManagementDAO = new UserManagementDAO();
    }

    /**
     * @param username
     * @return result after validating the username entered by new PSU for the registration.
     */
    public InternalResponse validateUsername(String username, String userType) {

        boolean result;
        if (userType.equals(Constants.E_SHOP)) {
            result = userManagementDAO.validateUserNameOfEShop(username);
        } else {
            result = userManagementDAO.validateUserNameOfPSU(username);
        }
        if (result) {
            return new InternalResponse(ErrorMessages.USERNAME_EXISTS, false);
        } else {
            return new InternalResponse(ErrorMessages.USERNAME_DOESNT_EXIST, true);
        }

    }

    /**
     * perform actions to register the PSU in PISP and return the response.
     *
     * @param psu
     * @return
     */
    public InternalResponse registerNewPSU(PSU psu) {

        if (this.userManagementDAO.addNewPSU(psu)) {
            return new InternalResponse(Constants.REGISTERED_SUCCESSFULLY, true);
        } else {
            return new InternalResponse(ErrorMessages.DB_SAVING_ERROR, false);
        }
    }

    /**
     * perform actions to register the Eshop in PISP.
     * return the response with credentials to the E-shop for payment initiation.
     *
     * @param eShop
     * @return
     */
    public InternalResponse registerNewEshop(EShop eShop) {

        String[] credentials = generateClientCredentialsForEShop(eShop.getUsername());
        eShop.setClientId(credentials[1]);
        eShop.setClientSecret(credentials[2]);
        if (this.userManagementDAO.registerEShop(eShop)) {
            return new InternalResponse(credentials, true);
        } else {
            return new InternalResponse(ErrorMessages.DB_SAVING_ERROR, false);
        }
    }

    /**
     * generate client credentials required by EShop to access Token API of PISP.
     *
     * @return
     */
    private String[] generateClientCredentialsForEShop(String username) {

        String[] credentials = new String[3];
        //Todo: currently no specific client-id/client-secret is generated. The username is used for now.
        credentials[0] = username;
        credentials[1] = username;
        credentials[2] = username;
        return credentials;
    }

    /**
     * Log-in users(PSU/E-Shop).
     *
     * @param username The username of the logging user
     * @param password The password to check validity.
     */
    public InternalResponse loginUser(String username, String password, String userType) {

        Validate.notNull(username, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(password, ErrorMessages.PARAMETERS_NULL);
        if (userType.equals(Constants.E_SHOP)) {
            return userManagementDAO.loginEShopUser(username, password);
        } else {
            return userManagementDAO.loginPSU(username, password);
        }

    }

    public InternalResponse validateClientIdOfEShop(String clientId) {

        try {
            if (userManagementDAO.validateClientId(clientId)) {
                return new InternalResponse(Constants.CLIENT_ID_EXISTS, true);
            } else {
                return new InternalResponse(ErrorMessages.CLIENT_ID_DOESNT_EXIST, false);
            }
        } catch (Exception e) {
            log.error(ErrorMessages.DB_CLOSE_ERROR, e);
            throw new PispException(ErrorMessages.ERROR_OCCURRED);
        }
    }

    /**
     * get e-shop user details from db.
     *
     * @param username
     * @return
     */
    public EShop getEshopUserProfle(String username) {

        Validate.notNull(username, ErrorMessages.PARAMETERS_NULL);
        EShop eShop = userManagementDAO.retrieveEShopDetails(username);
        return eShop;
    }

    /**
     * update the e-shop details in db.
     *
     * @param username
     * @param updatedEShop
     * @return
     */
    public boolean updateEshopUserProfile(String username, EShop updatedEShop) {

        Validate.notNull(username, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(updatedEShop, ErrorMessages.PARAMETERS_NULL);
        userManagementDAO.updateEshopProfile(username, updatedEShop);
        return true;

    }

    /**
     * Remove a E-shop from pisp.
     *
     * @param username
     * @return
     */
    public boolean removeEshop(String username) {

        return userManagementDAO.removeEshop(username);
    }

}
