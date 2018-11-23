package pisp;

import pisp.dto.BankInfoDTO;

import javax.ws.rs.core.Response;

public abstract class TokenApiService {

    public abstract Response getAccessToken();

}

