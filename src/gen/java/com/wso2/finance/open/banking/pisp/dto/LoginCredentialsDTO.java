package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * This class is to transfer login credentials of user.
 */
@ApiModel(description = "")
public class LoginCredentialsDTO {

    @NotNull
    private String username = null;

    @NotNull
    private String password = null;

    /**
     * The username of e-shop/psu.
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("username")
    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    /**
     * The password of e-shop/psu.
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("password")
    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class LoginCredentialsDTO {\n");

        sb.append("  username: ").append(username).append("\n");
        sb.append("  password: ").append(password).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
