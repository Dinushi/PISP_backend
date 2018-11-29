package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * This class is to transfer E-shop registration response.
 */
@ApiModel(description = "")
public class EShopRegistrationResponseDTO {

    private String eShopUsername = null;

    private String clientId = null;

    private String clientSecret = null;

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("eShopUsername")
    public String getEShopUsername() {

        return eShopUsername;
    }

    public void setEShopUsername(String eShopUsername) {

        this.eShopUsername = eShopUsername;
    }

    /**
     * The client credentials needed to invoke payment API.
     **/
    @ApiModelProperty(value = "The client credentials needed to invoke payment API")
    @JsonProperty("clientId")
    public String getClientId() {

        return clientId;
    }

    public void setClientId(String clientId) {

        this.clientId = clientId;
    }

    /**
     * The client credentials needed to invoke payment API.
     **/
    @ApiModelProperty(value = "The client credentials needed to invoke payment API")
    @JsonProperty("clientSecret")
    public String getClientSecret() {

        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {

        this.clientSecret = clientSecret;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class EShopRegistrationResponseDTO {\n");

        sb.append("  eShopUsername: ").append(eShopUsername).append("\n");
        sb.append("  clientId: ").append(clientId).append("\n");
        sb.append("  clientSecret: ").append(clientSecret).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
