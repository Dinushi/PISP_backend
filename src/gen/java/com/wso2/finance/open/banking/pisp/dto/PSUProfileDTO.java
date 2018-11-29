package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * The class that transfers PSU details.
 */
@ApiModel(description = "")

public class PSUProfileDTO {

    private String firstName = null;

    private String lastName = null;

    private String username = null;

    private String password = null;

    private String email = null;

    /**
     * the first name of the PSU/Customer.
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("firstName")
    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    /**
     * the last name of the PSU/Customer.
     **/
    @JsonProperty("lastName")
    public String getLastName() {

        return lastName;
    }

    public void setLastName(String lastName) {

        this.lastName = lastName;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("username")
    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("password")
    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("email")
    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class EShopProfileDTO {\n");

        sb.append("  firstName: ").append(firstName).append("\n");
        sb.append("  lastName: ").append(lastName).append("\n");
        sb.append("  username: ").append(username).append("\n");
        sb.append("  password: ").append(password).append("\n");
        sb.append("  email: ").append(email).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
