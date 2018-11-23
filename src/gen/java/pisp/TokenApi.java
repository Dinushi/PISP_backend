package pisp;


import pisp.dto.AccessTokenDTO;
import pisp.factories.TokenApiServiceFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/token")


@io.swagger.annotations.Api(value = "/token", description = "the token API")
public class TokenApi {

   private final TokenApiService delegate = TokenApiServiceFactory.getTokenApi();

    @GET
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "get a access token by E-shop to initiate a payment", notes = "", response = AccessTokenDTO.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Token generated"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Required parameter missing"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "User not found") })

    public Response getAccessToken()
    {
    return delegate.getAccessToken();
    }


}

