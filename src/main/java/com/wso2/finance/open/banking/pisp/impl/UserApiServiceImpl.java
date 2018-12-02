package com.wso2.finance.open.banking.pisp.impl;

import com.wso2.finance.open.banking.pisp.ApiResponseMessage;
import com.wso2.finance.open.banking.pisp.UserApiService;
import com.wso2.finance.open.banking.pisp.dto.EShopProfileDTO;
import com.wso2.finance.open.banking.pisp.dto.EShopRegistrationResponseDTO;
import com.wso2.finance.open.banking.pisp.dto.LoginCredentialsDTO;
import com.wso2.finance.open.banking.pisp.dto.PSUProfileDTO;
import com.wso2.finance.open.banking.pisp.exception.PispException;
import com.wso2.finance.open.banking.pisp.mappings.UserMapping;
import com.wso2.finance.open.banking.pisp.models.EShop;
import com.wso2.finance.open.banking.pisp.models.InternalResponse;
import com.wso2.finance.open.banking.pisp.models.PSU;
import com.wso2.finance.open.banking.pisp.services.PaymentManagementService;
import com.wso2.finance.open.banking.pisp.services.UserManagementService;
import com.wso2.finance.open.banking.pisp.utilities.SessionManager;
import com.wso2.finance.open.banking.pisp.utilities.constants.Constants;
import com.wso2.finance.open.banking.pisp.utilities.constants.ErrorMessages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

/**
 * This class holds the implementation logic behind the User API.
 */
public class UserApiServiceImpl extends UserApiService {

    private Log log = LogFactory.getLog(UserApiServiceImpl.class);

    @Override
    public Response addNewEShop(EShopProfileDTO body) {

        EShop newEShop = UserMapping.createEshopInstance(body);
        UserManagementService eShopManagementService = new UserManagementService();
        InternalResponse eShopValidationResult = eShopManagementService.validateUsername(newEShop.getUsername(), Constants.E_SHOP);
        if (eShopValidationResult.isOperationSuccessful()) {

            InternalResponse registration_result = eShopManagementService.registerNewEshop(newEShop);
            if (registration_result.isOperationSuccessful()) {

                EShopRegistrationResponseDTO responseBodyDTO = UserMapping.getEShopRegistrationResponseDTO((String[]) registration_result.getData());
                return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                        .entity(responseBodyDTO).build();
            } else {
                return Response.serverError().
                        header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                        .entity(new ApiResponseMessage(ApiResponseMessage.ERROR, registration_result.getMessage())).build();
            }

        } else {
            return Response.status(403).type("text/plain").entity(ErrorMessages.USERNAME_DOESNT_EXIST).build();
        }
    }

    @Override
    public Response eShopLogin(HttpServletRequest request, LoginCredentialsDTO body) {

        try {
            HttpSession session = request.getSession(true);
            UserManagementService userManagementService = new UserManagementService();

            InternalResponse eshopLoginResponse = userManagementService.loginUser(body.getUsername(), body.getPassword(), Constants.E_SHOP);
            if (eshopLoginResponse.isOperationSuccessful()) {
                Object sessionToken = SessionManager.generateSessionTokenForEShop(body.getUsername());
                if (log.isDebugEnabled()) {
                    log.debug("session token generated for E-shop" + body.getUsername());
                }
                session.setAttribute(Constants.SESSION_ID, sessionToken);
                return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, eshopLoginResponse.getMessage())).build();
            } else {
                if (eshopLoginResponse.getMessage().equals(ErrorMessages.USERNAME_DOESNT_EXIST)) {
                    return Response.status(404).type("text/plain").entity(ErrorMessages.USERNAME_DOESNT_EXIST).build();
                } else if (eshopLoginResponse.getMessage().equals(ErrorMessages.INCORRECT_PASSWORD)) {
                    return Response.status(403).type("text/plain").entity(ErrorMessages.INCORRECT_PASSWORD).build();
                } else {
                    return Response.serverError()
                            .entity(new ApiResponseMessage(ApiResponseMessage.ERROR, eshopLoginResponse.getMessage())).build();
                }
            }
        } catch (PispException e) {
            return Response.serverError().entity("").build();
        }
    }

    @Override
    public Response getEShopProfile(String username, HttpServletRequest request) {

        HttpSession session = request.getSession(true);
        String sessionToken = (String) session.getAttribute(Constants.SESSION_ID);

        InternalResponse response = SessionManager.validateSessionTokenOfEShop(username, sessionToken);
        if (response.isOperationSuccessful()) {
            UserManagementService userManagementService = new UserManagementService();
            EShop e_shop = userManagementService.getEshopUserProfle(username);
            EShopProfileDTO eShopProfileDTO = UserMapping.getEShopProfileDTO(e_shop);
            return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                    .entity(eShopProfileDTO).build();
        } else {
            return Response.serverError()
                    .entity(new ApiResponseMessage(ApiResponseMessage.ERROR, response.getMessage())).build();
        }

    }

    @Override
    public Response updateEShopProfile(String username, HttpServletRequest request, EShopProfileDTO body) {

        HttpSession session = request.getSession(true);
        String sessionToken = (String) session.getAttribute(Constants.SESSION_ID);

        InternalResponse response = SessionManager.validateSessionTokenOfEShop(username, sessionToken);
        if (response.isOperationSuccessful()) {
            EShop updated_e_shop = UserMapping.createEshopInstance(body);
            UserManagementService userManagementService = new UserManagementService();
            userManagementService.updateEshopUserProfile(username, updated_e_shop);
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, Constants.UPDATED_SUCCESSFULLY)).build();
        } else {
            return Response.serverError()
                    .entity(new ApiResponseMessage(ApiResponseMessage.ERROR, response.getMessage())).build();
        }
    }

    @Override
    public Response deleteEShop(String username, HttpServletRequest request) {

        HttpSession session = request.getSession(true);
        String sessionToken = (String) session.getAttribute(Constants.SESSION_ID);

        InternalResponse response = SessionManager.validateSessionTokenOfEShop(username, sessionToken);
        if (response.isOperationSuccessful()) {
            UserManagementService userManagementService = new UserManagementService();
            if (userManagementService.removeEshop(username)) {
                return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, Constants.ESHOP_DELETED)).build();
            } else {
                return Response.status(403).type("text/plain").entity(ErrorMessages.USERNAME_DOESNT_EXIST).build();
            }
        } else {
            return Response.serverError()
                    .entity(new ApiResponseMessage(ApiResponseMessage.ERROR, response.getMessage())).build();
        }

    }

    @Override
    public Response addNewPSU(PSUProfileDTO body) {

        PSU psu = UserMapping.createPSUInstance(body);
        UserManagementService psuManagementService = new UserManagementService();
        InternalResponse psuValidationResult = psuManagementService.validateUsername(psu.getUsername(), Constants.PSU);

        if (psuValidationResult.isOperationSuccessful()) {
            InternalResponse registration_result = psuManagementService.registerNewPSU(psu);
            if (registration_result.isOperationSuccessful()) {
                return Response.ok()
                        .entity(new ApiResponseMessage(ApiResponseMessage.OK, registration_result.getMessage())).build();
            } else {
                return Response.serverError()
                        .entity(new ApiResponseMessage(ApiResponseMessage.ERROR, registration_result.getMessage())).build();
            }
        } else {
            return Response.status(403).type("text/plain").entity(psuValidationResult.getMessage()).build();
        }
    }

    @Override
    public Response loginPSU(String paymentInitReqId, HttpServletRequest request, LoginCredentialsDTO body) {

        try {
            HttpSession session = request.getSession(true);
            UserManagementService userManagementService = new UserManagementService();

            InternalResponse psuLoginResponse = userManagementService.loginUser(body.getUsername(), body.getPassword(), Constants.PSU);

            if (psuLoginResponse.isOperationSuccessful()) {
                Object sessionToken = SessionManager.generateSessionTokenForPSU(body.getUsername(), paymentInitReqId);
                if (log.isDebugEnabled()) {
                    log.debug("session token generated for PSU" + body.getUsername());
                }
                session.setAttribute(Constants.SESSION_ID, sessionToken);
                PaymentManagementService paymentManagementService = new PaymentManagementService();
                paymentManagementService.updatePaymentWithPSU(paymentInitReqId, body.getUsername());

                return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, psuLoginResponse.getMessage())).build();
            } else {
                if (ErrorMessages.USERNAME_DOESNT_EXIST.equals(psuLoginResponse.getMessage())) {
                    log.info(ErrorMessages.USERNAME_DOESNT_EXIST);
                    return Response.status(404).type("text/plain").entity(ErrorMessages.USERNAME_DOESNT_EXIST).build();
                } else if (ErrorMessages.INCORRECT_PASSWORD.equals(psuLoginResponse.getMessage())) {
                    log.info(ErrorMessages.INCORRECT_PASSWORD);
                    return Response.status(403).type("text/plain").entity(ErrorMessages.INCORRECT_PASSWORD).build();
                } else {
                    return Response.serverError()
                            .entity(new ApiResponseMessage(ApiResponseMessage.ERROR, psuLoginResponse.getMessage())).build();
                }
            }
        } catch (PispException e) {
            return Response.serverError().entity("").build();
        }
    }
}
