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
package com.wso2.finance.open.banking.pisp;

import com.wso2.finance.open.banking.pisp.dto.EShopProfileDTO;
import com.wso2.finance.open.banking.pisp.dto.EShopRegistrationResponseDTO;
import com.wso2.finance.open.banking.pisp.dto.LoginCredentialsDTO;
import com.wso2.finance.open.banking.pisp.dto.PSUProfileDTO;
import com.wso2.finance.open.banking.pisp.factories.UserApiServiceFactory;
import io.swagger.annotations.ApiParam;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


/**
 * The User API.
 */
@Path("/user")

@io.swagger.annotations.Api(value = "/user", description = "the user API")
public class UserApi {

    private final UserApiService delegate = UserApiServiceFactory.getUserApi();

    @POST
    @Path("/e-shop")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Add and register a new ecommerce site/ app",
            notes = "Admins of ecommerce sites/apps can register their sites/apps at pisp.",
            response = EShopRegistrationResponseDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "E-Shop successfully registered"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameters missing"),

            @io.swagger.annotations.ApiResponse(code = 403, message = "Username already exists"),

            @io.swagger.annotations.ApiResponse(code = 405, message = "Invalid input"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "unexpected error in the server")})

    public Response addNewEshop(
            @ApiParam(value = "Chosen content type", required = true, allowableValues = "{values=[application/json]}")
            @HeaderParam("Content-Type") String contentType,
            @ApiParam(value = "created new e-shop object", required = true) EShopProfileDTO body) {

        return delegate.addNewEShop(body);
    }

    @POST
    @Path("/e-shop/login")

    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "E-shop admins login to the PISP system", notes = "",
            response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "User login successful"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameters missing"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "Authentication failed. Invalid Credentials."),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Unexpected error in server")})

    public Response eshopLogin(
            @ApiParam(value = "Chosen content type", required = true,
                    allowableValues = "{values=[application/json]}") @HeaderParam("Content-Type") String contentType,
            @Context HttpServletRequest request,
            @ApiParam(value = "login credentials of e-shop user", required = true) LoginCredentialsDTO body) {

        return delegate.eShopLogin(request, body);
    }

    @DELETE
    @Path("/e-shop/{username}")

    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Delete e-shop from pisp",
            notes = "This to ecommerce site/app admins to unregister their site from PISP.", response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "E-shop successfully deleted"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameters missing"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "Username not found"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Unexpected error in server")})

    public Response deleteEShop(
            @ApiParam(value = "The username that needs to be deleted", required = true)
            @PathParam("username") String username,
            @ApiParam(value = "The session id is set in the cookie", required = true,
                    allowableValues = "{values=[application/json]}") @HeaderParam("Cookie") String cookie) {

        return delegate.deleteEShop(username, cookie);
    }

    @GET
    @Path("/e-shop/{username}")

    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Get e-shop profile details by username",
            notes = "Return requested e-shop profile Info", response = EShopProfileDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "successfully retrieved the profile Information"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameter missing"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "Username not found")})

    public Response getEShopProfile(
            @ApiParam(value = "username of the e-shop to be fetched", required = true)
            @PathParam("username") String username,
            @ApiParam(value = "The session id is set in the cookie", required = true,
                    allowableValues = "{values=[application/json]}") @HeaderParam("Cookie") String cookie,
            @Context HttpServletRequest request) {

        return delegate.getEShopProfile(username, cookie, request);
    }

    @PUT
    @Path("/e-shop/{username}")

    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Update e-shop profile details",
            notes = "This is to ecommerce site/app admins to update their profile details.", response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 201, message = "E-shop profile successfully updated"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameters missing"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "Username not found"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Unexpected error in server")})

    public Response updateEshopProfile(
            @ApiParam(value = "username of the e-shop that need to be updated", required = true)
            @PathParam("username") String username,
            @ApiParam(value = "Chosen content type", required = true, allowableValues = "{values=[application/json]}")
            @HeaderParam("Content-Type") String contentType,
            @ApiParam(value = "The session id is set in the cookie", required = true,
                    allowableValues = "{values=[application/json]}") @HeaderParam("Cookie") String cookie,
            @Context HttpServletRequest request,
            @ApiParam(value = "Updated user object", required = true) EShopProfileDTO body) {

        return delegate.updateEShopProfile(username, request, cookie, body);
    }

    @POST
    @Path("/psu")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Add and register a new psu with PISP",
            notes = "Customer of ecommerce site can register with PISP.", response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "PSU successfully registered"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameters missing"),

            @io.swagger.annotations.ApiResponse(code = 403, message = "Username already exists"),

            @io.swagger.annotations.ApiResponse(code = 405, message = "Invalid input"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "unexpected error in the server")})

    public Response addNewPSU(@ApiParam(value = "Chosen content type", required = true,
            allowableValues = "{values=[application/json]}") @HeaderParam("Content-Type") String contentType,
                              @ApiParam(value = "created new psu object", required = true) PSUProfileDTO body) {

        return delegate.addNewPSU(body);
    }

    @POST
    @Path("/psu/login")

    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Log admin user to the PISP system", notes = "", response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "PSU login successful"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameters missing"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "Authentication failed. Invalid Credentials."),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Unexpected error in server")})

    public Response loginPSU(
            @ApiParam(value = "Chosen content type", required = true,
                    allowableValues = "{values=[application/json]}") @HeaderParam("Content-Type") String contentType,
            @HeaderParam("paymentInitReqId") String paymentInitReqId,
            @Context HttpServletRequest request,
            @ApiParam(value = " PSU login credentials", required = true) LoginCredentialsDTO body) {

        return delegate.loginPSU(paymentInitReqId, request, body);
    }

}

