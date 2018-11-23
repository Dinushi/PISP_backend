package pisp.impl;


import pisp.TokenApiService;
import pisp.utilities.constants.ErrorMessages;

import javax.ws.rs.core.Response;

public class TokenApiServiceImpl extends TokenApiService {


    @Override
    public Response getAccessToken() {
        return Response.status(403).type("text/plain").entity(ErrorMessages.USERNAME_DOESNT_EXIST).build();
    }
}
