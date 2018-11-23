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
public class PaymentInitiationApi  {

   private final PaymentInitiationApiService delegate = PaymentInitiationApiServiceFactory.getPaymentInitiationApi();

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Make a new payment initiation request", notes = "The ecommerce site can make a payment initiation request onbehalf of customer", response = void.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 303, message = "See Other"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameters missing"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "E commerce user not found"),
        
        @io.swagger.annotations.ApiResponse(code = 405, message = "Invalid input"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "unexpected error in the server") })

    public Response makePaymentInitiationRequest(@ApiParam(value = "Chosen content type" ,required=true , allowableValues="{values=[application/json]}")@HeaderParam("Content-Type") String contentType,
    @ApiParam(value = "The unique id assigned to e-shop by PISP during the registration." ,required=true )@HeaderParam("Client-Id") String clientId,
    @ApiParam(value = "The unique id assigned by the ecommerce site/app to uniquely identify a purchase which a payment will be proceeded." ,required=true )@HeaderParam("Purchase-Id") String purchaseId,
    @ApiParam(value = "Bearer Token" ,required=true )@HeaderParam("Authorization") String authorization,

    @ApiParam(value = "created payment initiation request object" ,required=true ) PaymentInitRequestDTO body)
    {
    return delegate.makePaymentInitiationRequest(contentType,clientId,purchaseId,authorization,body);
    }


    @GET
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "query about a payment initiation request and get details ", notes = "Return a single payment-init-request object", response = PaymentInitResponseDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "successfully retrieved the details"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid ID supplied"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "The requested payment_init_req_id not found ") })

    public Response getPaymentInitRequestById(@ApiParam(value = "Chosen content type" ,required=true , allowableValues="{values=[application/json]}")@HeaderParam("Content-Type") String contentType,
                                              @Context HttpServletRequest request,
                                              @ApiParam(value = "PSU username" ,required=true )@HeaderParam("username") String username)
    {
        return delegate.getPaymentInitRequestById(contentType,request,username);
    }


    @POST
    @Path("/debtor-bank")

    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "The customer selects a bank as debtor agent", notes = "The customer chooses his debtor account bank once he is redirected to pisp", response = BankSelectionResponseDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "The bank selection response"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),

            @io.swagger.annotations.ApiResponse(code = 403, message = "Invalid data supplied"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "Some required body parameters are missing"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Unexpected error in server") })

    public Response selectDebtorBank(@ApiParam(value = "Chosen content type" ,required=true , allowableValues="{values=[application/json]}")@HeaderParam("Content-Type") String contentType,
                                     @Context HttpServletRequest request,
                                     @ApiParam(value = "PSU username" ,required=true )@HeaderParam("username") String username,
                                     @ApiParam(value = "Information relevent to the bank selected by customer" ,required=true ) DebtorBankDTO body)
    {

        return delegate.selectDebtorBank(contentType,request,username,body);
    }

    @POST
    @Path("/debtor-account")

    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "The customer selects a bank as debtor agent", notes = "The customer chooses his debtor account bank once he is redirected to pisp", response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 303, message = "See Other"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),

            @io.swagger.annotations.ApiResponse(code = 403, message = "Invalid data supplied"),

            @io.swagger.annotations.ApiResponse(code = 404, message = "Some required body parameters are missing"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Unexpected error in server") })

    public Response selectDebtorAccount(@ApiParam(value = "Chosen content type" ,required=true , allowableValues="{values=[application/json]}")@HeaderParam("Content-Type") String contentType,
                                        @Context HttpServletRequest request,
                                        @ApiParam(value = "PSU username" ,required=true )@HeaderParam("username") String username,
                                        @ApiParam(value = "Information relevent to the bank selected by customer" ,required=true ) BankAccountDTO body)
    {
        return delegate.selectDebtorAccount(contentType,request,username,body);
    }

   @POST
   @Path("/AuthCode")

   @Consumes({ "application/json" })
   @Produces({ "application/json" })
   @io.swagger.annotations.ApiOperation(value = "Add the Auth code generated by bank", notes = "This will be used to submit a payment to the bank", response = void.class)
   @io.swagger.annotations.ApiResponses(value = {
           @io.swagger.annotations.ApiResponse(code = 303, message = "See Other"),

           @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameters missing"),

           @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),

           @io.swagger.annotations.ApiResponse(code = 405, message = "Invalid input"),

           @io.swagger.annotations.ApiResponse(code = 500, message = "unexpected error in the server") })

   public Response addAuthorizationCode(@ApiParam(value = "Chosen content type" ,required=true , allowableValues="{values=[application/json]}")@HeaderParam("Content-Type") String contentType,
                                        //@ApiParam(value = "The paymentInitReq is set in the cookie" ,required=true )@CookieParam("Cookie") NewCookie cookie,
                                        @ApiParam(value = "PSU username" ,required=true )@HeaderParam("username") String username,
                                        @Context HttpServletRequest request,
                                        @ApiParam(value = "The body containing authorization code grant" ,required=true ) AuthCodeDTO body)
   {
    return delegate.addAuthorizationCodeGrant(contentType,username,request, body);
   }



}

