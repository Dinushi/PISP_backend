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
package pisp;

import pisp.dto.*;
import pisp.PaymentInitiationApiService;
import pisp.factories.PaymentInitiationApiServiceFactory;

import io.swagger.annotations.ApiParam;

import pisp.dto.PaymentInitResponseDTO;
import pisp.dto.PaymentInitRequestDTO;

import java.util.List;

import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.*;

@Path("/payment-initiation")

@io.swagger.annotations.Api(value = "/payment-initiation", description = "the payment-initiation API")
public class PaymentInitiationApi {

    private final PaymentInitiationApiService delegate = PaymentInitiationApiServiceFactory.getPaymentInitiationApi();

    @POST

    @Consumes({"application/json"})
    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Make a new payment initiation request", notes = "The ecommerce site can make a payment initiation request onbehalf of customer", response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 303, message = "See Other"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameters missing"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "E commerce user not found"),

            @io.swagger.annotations.ApiResponse(code = 405, message = "Invalid input"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "unexpected error in the server")})

    public Response makePaymentInitiationRequest(@ApiParam(value = "Chosen content type", required = true, allowableValues = "{values=[application/json]}") @HeaderParam("Content-Type") String contentType,
                                                 @ApiParam(value = "The unique id assigned to e-shop by PISP during the registration.", required = true) @HeaderParam("Client-Id") String clientId,
                                                 @ApiParam(value = "The unique id assigned by the ecommerce site/app to uniquely identify a purchase which a payment will be proceeded.", required = true) @HeaderParam("Purchase-Id") String purchaseId,
                                                 @ApiParam(value = "Bearer Token", required = true) @HeaderParam("Authorization") String authorization,
                                                 @ApiParam(value = "created payment initiation request object", required = true) PaymentInitRequestDTO body) {

        return delegate.makePaymentInitiationRequest(clientId, purchaseId, body);
    }

    @GET
    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "retrieve the details about the payment initiation relevent to the logged-in psu", notes = "Return a single payment-init-request object", response = PaymentInitResponseDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "successfully retrieved the details"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid username supplied"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "The requested payment_init_req_id not found ")})

    public Response getPaymentInitRequestById(@ApiParam(value = "Chosen content type", required = true, allowableValues = "{values=[application/json]}") @HeaderParam("Content-Type") String contentType,
                                              @Context HttpServletRequest request,
                                              @ApiParam(value = "PSU username", required = true) @HeaderParam("username") String username) {

        return delegate.getPaymentInitRequestById(request, username);
    }

    @POST
    @Path("/debtor-bank")

    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "The customer selects a bank as debtor agent", notes = "The customer chooses his debtor account bank once he is redirected to pisp", response = BankSelectionResponseDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "The bank selection response"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),

            @io.swagger.annotations.ApiResponse(code = 403, message = "Invalid data supplied"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "Some required body parameters are missing"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Unexpected error in server")})

    public Response selectDebtorBank(@ApiParam(value = "Chosen content type", required = true, allowableValues = "{values=[application/json]}") @HeaderParam("Content-Type") String contentType,
                                     @Context HttpServletRequest request,
                                     @ApiParam(value = "PSU username", required = true) @HeaderParam("username") String username,
                                     @ApiParam(value = "Information relevent to the bank selected by customer", required = true) DebtorBankDTO body) {

        return delegate.selectDebtorBank(request, username, body);
    }

    @POST
    @Path("/debtor-account")

    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "The customer selects a bank as debtor agent", notes = "The customer chooses his debtor account bank once he is redirected to pisp", response = AuthUrlDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Authorization URL generated"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),

            @io.swagger.annotations.ApiResponse(code = 403, message = "Invalid data supplied"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "Some required body parameters are missing"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Unexpected error in server")})

    public Response selectDebtorAccount(@ApiParam(value = "Chosen content type", required = true, allowableValues = "{values=[application/json]}") @HeaderParam("Content-Type") String contentType,
                                        @Context HttpServletRequest request,
                                        @ApiParam(value = "PSU username", required = true) @HeaderParam("username") String username,
                                        @ApiParam(value = "Information relevent to the bank selected by customer", required = true) BankAccountDTO body) {

        return delegate.selectDebtorAccount(request, username, body);
    }

    @POST
    @Path("/submission")

    @Consumes({"application/json"})
    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Add the Auth code received in redirection after PSU authorization",
            notes = "This will be used to submit a payment to the bank", response = PaymentInitResponseDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 303, message = "See Other"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameters missing"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),

            @io.swagger.annotations.ApiResponse(code = 405, message = "Invalid input"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "unexpected error in the server")})

    public Response addAuthorizationCode(@ApiParam(value = "Chosen content type", required = true, allowableValues = "{values=[application/json]}") @HeaderParam("Content-Type") String contentType,
                                         @ApiParam(value = "PSU username", required = true) @HeaderParam("username") String username,
                                         @Context HttpServletRequest request,
                                         @ApiParam(value = "The body containing authorization code grant", required = true) AuthCodeDTO body) {

        return delegate.addAuthorizationCode(username, request, body);
    }

    @GET
    @Path("/status")

    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "make a request to send GET Status request to ASPSP and check the status of payment ", notes = "Return a single payment-init-request object with payment status", response = PaymentInitResponseDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "successfully retrieved the status of payment from ASPSP"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid username supplied"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "The requested payment_init_req_id not found")})

    public Response getPaymentStatusFromBank(@ApiParam(value = "Chosen content type", required = true, allowableValues = "{values=[application/json]}") @HeaderParam("Content-Type") String contentType,
                                             @Context HttpServletRequest request,
                                             @ApiParam(value = "PSU username", required = true) @HeaderParam("username") String username) {

        return delegate.getPaymentStatusFromBank(request, username);
    }

}

