package pisp;

import pisp.dto.*;
import pisp.PaymentHistoryApiService;
import pisp.factories.PaymentHistoryApiServiceFactory;

import io.swagger.annotations.ApiParam;

import pisp.dto.PaymentHistoryDTO;

import java.util.List;

import java.io.InputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.ws.rs.core.Response;
import javax.ws.rs.*;

@Path("/payment-history")


@io.swagger.annotations.Api(value = "/payment-history", description = "the payment-history API")
public class PaymentHistoryApi  {

   private final PaymentHistoryApiService delegate = PaymentHistoryApiServiceFactory.getPaymentHistoryApi();

    @GET
    @Path("/{username}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "get payments history related to e-commerce site/app", notes = "Every registered e-shop can view its payments history", response = PaymentHistoryDTO.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successfully retrieved the payment history"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameter missing"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Username not found") })

    public Response getPaymentReports(@ApiParam(value = "username of the e-shop who requesting payment history reports",required=true ) @PathParam("username")  String username,
    @ApiParam(value = "selected filter by the user",required=true, allowableValues="{values=[all, completed, declined]}") @QueryParam("filter")  String filter,
    @ApiParam(value = "The session id is set in the cookie" ,required=true , allowableValues="{values=[application/json]}")@HeaderParam("Cookie") String cookie,
    @ApiParam(value = "The start of time period which payment history is requested" ,required=true )@HeaderParam("startDate") String startDate,
    @ApiParam(value = "The end of time period which payment history is requested"  )@HeaderParam("endDate") String endDate)
    {
    return delegate.getPaymentReports(username,filter,cookie,startDate,endDate);
    }
}

