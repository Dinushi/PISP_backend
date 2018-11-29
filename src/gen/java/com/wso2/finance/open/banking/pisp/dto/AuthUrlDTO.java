package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * the response with authorization URL.
 */
@ApiModel(description = "")
public class AuthUrlDTO {

    @NotNull
    private String authUrl = null;

    /**
     * The authorization Url that the PSU should be redirected.
     **/
    @ApiModelProperty(required = true, value = "The link to authorize endpoint of debtor bank")
    @JsonProperty("authUrl")
    public String getAuthUrl() {

        return authUrl;
    }

    public void setAuthUrl(String authUrl) {

        this.authUrl = authUrl;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class AuthUrlDTO {\n");

        sb.append("  authUrl: ").append(authUrl).append("\n");

        sb.append("}\n");
        return sb.toString();
    }
}
