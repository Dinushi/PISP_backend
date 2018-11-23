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
package pisp.services;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.dao.UserManagementDAO;
import pisp.exception.PispException;
import pisp.models.*;
import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;

public class UserManagementService {
    private Log log = LogFactory.getLog(UserManagementService.class);

    UserManagementDAO userManagementDAO;

    public UserManagementService(){
        this.userManagementDAO=new UserManagementDAO();
    }


    /**
     * @param username
     * @return result after validating the username entered by new PSU for the registration
     */
    public PispInternalResponse validateUsername(String username, String userType) {

        boolean result;
        if(userType.equals(Constants.E_SHOP)){
            result=userManagementDAO.validateUserNameOfEShop(username);
        }else{
            result=userManagementDAO.validateUserNameOfPSU(username);
        }
        if(result){
            return new PispInternalResponse(ErrorMessages.USERNAME_EXISTS,false);
        }else{
            return new PispInternalResponse(ErrorMessages.USERNAME_DOESNT_EXIST,true);
        }

    }

    /**perform actions to register the PSU in PISP and return the response
     *
     * @param psu
     * @return
     */
    public PispInternalResponse registerNewPSU(PSU psu){
        log.info("Registering the New PSU.......");
        if(this.userManagementDAO.addNewPSU(psu)){
            return new PispInternalResponse(Constants.REGISTERED_SUCCESSFULLY,true);
        }else{
            return new  PispInternalResponse(ErrorMessages.DB_SAVING_ERROR,false);
        }
    }



    /**
     * perform actions to register the Eshop in PISP and return the response with credentials to the E-shop for payment initiation
     *
     * @param e_shop
     * @return
     */
    public PispInternalResponse registerNewEshop(E_shop e_shop){
        log.info("Registering the New E commerce user.......");
        String[] credentials=generateClientCredentialsForEShop(e_shop.getUsername());
        e_shop.setClient_id(credentials[1]);
        e_shop.setClient_secret(credentials[2]);
        if(this.userManagementDAO.registerEShop(e_shop)){
            return  new PispInternalResponse(credentials,true);
        }else{
            return new  PispInternalResponse(ErrorMessages.DB_SAVING_ERROR,false);
        }
    }


    /**
     * generate client credentials required by EShop to access Token API of PISP
     * @return
     */
    private String[] generateClientCredentialsForEShop(String username){
        String[] credentials=new String[3];
       //Todo: currently no client-id/client-secret is generated. The username is used for now.
        credentials[0]=username;
        credentials[1]=username;
        credentials[2]=username;
        return credentials;

    }


    /**
     * Log-in users(PSU/E-Shop).
     *
     * @param username The username of the logging user
     * @param password The password to check validity.
     */
    public PispInternalResponse loginUser(String username, String password, String userType){
        Validate.notNull(username, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(password, ErrorMessages.PARAMETERS_NULL);
        if(userType.equals(Constants.E_SHOP)){
            return  userManagementDAO.loginEShopUser(username,password);
        }else{
            return  userManagementDAO.loginPSU(username,password);
        }

    }


    public PispInternalResponse validateClientIdOfEshop(String client_id){
        try {
            if(userManagementDAO.validateClientId(client_id)){
                return new PispInternalResponse(ErrorMessages.CLIENT_ID_EXISTS,true);
            }else{
                return new PispInternalResponse(ErrorMessages.CLIENT_ID_DOESNT_EXIST,false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PispInternalResponse(ErrorMessages.ERROR_OCCURRED,false);

    }


    /**
     * get e-shop user profile details
     * @param username
     * @return
     */
    public E_shop getEshopUserProfle(String username){
        Validate.notNull(username, ErrorMessages.PARAMETERS_NULL);
        E_shop e_shop=userManagementDAO.retrieveEShopDetails(username);
        return e_shop;
    }

    /**
     * update the e-shop profile details
     * @param username
     * @param updated_e_shop
     * @return
     */
    public boolean updateEshopUserProfle(String username,E_shop updated_e_shop){
        Validate.notNull(username, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(updated_e_shop, ErrorMessages.PARAMETERS_NULL);
        userManagementDAO.updateEshopProfile(username,updated_e_shop);
        return true;

    }

    /**
     * remove a E-shop from pisp
     * @param username
     * @return
     */
    public boolean removeEshop(String username){
        return userManagementDAO.removeEshop(username);
    }


}
