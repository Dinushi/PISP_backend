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
import pisp.BankConnectionApiService;
import pisp.factories.BankConnectionApiServiceFactory;

import io.swagger.annotations.ApiParam;

import pisp.dto.BankInfoDTO;
import pisp.dto.DebtorBankDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.*;

@Path("/bank-connection")


@io.swagger.annotations.Api(value = "/bank-connection", description = "the bank-connection API")
public class BankConnectionApi  {

   private final BankConnectionApiService delegate = BankConnectionApiServiceFactory.getBankConnectionApi();

    @POST
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Add connection to a bank API", notes = "The PISP will register as a TPP in ASPSP and admins will add required details to the PISP system to connect with the bank exposed APIs", response = void.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Invalid data supplied"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Some required body parameters are missing"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Unexpected error in server") })

    public Response addANewBank(@ApiParam(value = "Chosen content type" ,required=true , allowableValues="{values=[application/json]}")@HeaderParam("Content-Type") String contentType,
    @ApiParam(value = "The session id is set in the cookie" ,required=true , allowableValues="{values=[application/json]}")@HeaderParam("Cookie") String cookie,
    @ApiParam(value = "Information relevent to the bank" ,required=true ) BankInfoDTO body)
    {
    return delegate.addANewBank(cookie,body);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "get list of banks supported by PISP", notes = "", response = DebtorBankDTO.class, responseContainer = "List")
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successfully retrieved the payment history"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameter missing"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Username not found") })

    public Response getListOfBanks()
    {
    return delegate.getListOfBanks();
    }
    @DELETE
    @Path("/{bank-uid}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Remove the bank from pisp service", notes = "This to pisp admins to remove a bank from its supported banks list.", response = void.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "The bank successfully removed"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameters missing"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Authentication failed"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Username not found"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Unexpected error in server") })

    public Response removeBank(@ApiParam(value = "The id of the debtor bank as used in pisp",required=true, allowableValues="{values=[application/json]}" ) @PathParam("bank-uid")  String bankUid,
    @ApiParam(value = "The session id is set in the cookie" ,required=true , allowableValues="{values=[application/json]}")@HeaderParam("Cookie") String cookie)
    {
    return delegate.removeBank(bankUid,cookie);
    }

}

