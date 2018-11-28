package pisp.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.*;
import pisp.dto.*;
import pisp.dto.EShopRegistrationResponseDTO;
import pisp.dto.EShopProfileDTO;
import pisp.dto.LoginCredentialsDTO;
import pisp.exception.PispException;
import pisp.mappings.UserMapping;
import pisp.models.*;
import pisp.services.PaymentManagementService;
import pisp.services.UserManagementService;
import pisp.utilities.SessionManager;
import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

/**
 * This class holds the implementation logic behind the User API.
 */
public class UserApiServiceImpl extends UserApiService {

    private Log log = LogFactory.getLog(UserApiServiceImpl.class);

    /*
    ===============================
    This section handles the E-Shop
    ===============================
    */

    @Override
    public Response addNewEShop(EShopProfileDTO body) {

        EShop newEShop = UserMapping.createEshopInstance(body);

        UserManagementService eShopManagementService = new UserManagementService();
        PispInternalResponse eShopValidationResult = eShopManagementService.validateUsername(newEShop.getUsername(), Constants.E_SHOP);
        if (eShopValidationResult.isOperationSuccessful()) {

            PispInternalResponse registration_result = eShopManagementService.registerNewEshop(newEShop);
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
            log.info("session is new :" + session.isNew());
            log.info("session id" + session.getId());

            UserManagementService userManagementService = new UserManagementService();

            PispInternalResponse eshopLoginResponse = userManagementService.loginUser(body.getUsername(), body.getPassword(), Constants.E_SHOP);
            if (eshopLoginResponse.isOperationSuccessful()) {
                Object sessionToken = SessionManager.generateSessionTokenForEShop(body.getUsername());
                log.info("session token generated for E-shop" + sessionToken);
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
    public Response getEShopProfile(String username, String cookie, HttpServletRequest request) {

        HttpSession session = request.getSession(true);
        log.info("session is new :" + session.isNew());
        log.info("session id" + session.getId());
        String sessionToken = (String) session.getAttribute(Constants.SESSION_ID);
        log.info("session Token " + sessionToken);

        PispInternalResponse response = SessionManager.validateSessionTokenOfEShop(username, sessionToken);
        if (response.isOperationSuccessful()) {
            UserManagementService userManagementService = new UserManagementService();
            EShop e_shop = userManagementService.getEshopUserProfle(username);
            log.info("e_shop details merchant cat code" + e_shop.getMerchant().getMerchantCategoryCode());
            EShopProfileDTO eShopProfileDTO = UserMapping.getEShopProfileDTO(e_shop);
            return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                    .entity(eShopProfileDTO).build();
        } else {
            return Response.serverError()
                    .entity(new ApiResponseMessage(ApiResponseMessage.ERROR, response.getMessage())).build();
        }

    }

    @Override
    public Response updateEShopProfile(String username, HttpServletRequest request, String cookie, EShopProfileDTO body) {

        HttpSession session = request.getSession(true);
        log.info("session is new :" + session.isNew());
        log.info("session id" + session.getId());
        String sessionToken = (String) session.getAttribute(Constants.SESSION_ID);
        log.info("session Token " + sessionToken);

        PispInternalResponse response = SessionManager.validateSessionTokenOfEShop(username, sessionToken);
        if (response.isOperationSuccessful()) {
            EShop updated_e_shop = UserMapping.createEshopInstance(body);
            UserManagementService userManagementService = new UserManagementService();
            userManagementService.updateEshopUserProfile(username, updated_e_shop);

            return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                    .entity("E-shop user updated").build();
        } else {
            return Response.serverError()
                    .entity(new ApiResponseMessage(ApiResponseMessage.ERROR, response.getMessage())).build();
        }
    }

    @Override
    public Response deleteEShop(String username, String cookie) {

        UserManagementService userManagementService = new UserManagementService();
        if (userManagementService.removeEshop(username)) {
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, Constants.ESHOP_DELETED)).build();
        } else {
            return Response.status(403).type("text/plain").entity(ErrorMessages.USERNAME_DOESNT_EXIST).build();
        }
    }

    /*
    ============================
    This section handles the PSU
    ============================
    */

    @Override
    public Response addNewPSU(PSUProfileDTO body) {

        PSU psu = UserMapping.createPSUInstance(body);

        UserManagementService psuManagementService = new UserManagementService();
        PispInternalResponse psuValidationResult = psuManagementService.validateUsername(psu.getUsername(), Constants.PSU);

        if (psuValidationResult.isOperationSuccessful()) {

            PispInternalResponse registration_result = psuManagementService.registerNewPSU(psu);

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
            log.info("session is new :" + session.isNew());
            log.info("session id" + session.getId());

            UserManagementService userManagementService = new UserManagementService();

            PispInternalResponse psuLoginResponse = userManagementService.loginUser(body.getUsername(), body.getPassword(), Constants.PSU);

            if (psuLoginResponse.isOperationSuccessful()) {
                Object sessionToken = SessionManager.generateSessionTokenForPSU(body.getUsername(), paymentInitReqId);
                log.info("session token generated for PSU" + sessionToken);
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
