package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * The class that transfer the Bank details.
 */
@ApiModel(description = "")
public class BankDTO {

    /**
     * Scheme Names of Banks.
     */
    public enum SchemeNameEnum {
        BICFI,
    }

    ;
    @NotNull
    private SchemeNameEnum schemeName = null;

    @NotNull
    private String identification = null;

    @NotNull
    private String bankName = null;

    /**
     * Name of the identification scheme, in a coded form as published in an external list.
     **/
    @ApiModelProperty(required = true, value = "Name of the identification scheme, in a coded " +
            "form as published in an external list.")
    @JsonProperty("schemeName")
    public SchemeNameEnum getSchemeName() {

        return schemeName;
    }

    public void setSchemeName(SchemeNameEnum schemeName) {

        this.schemeName = schemeName;
    }

    /**
     * Unique and unambiguous identification of the bank under above scheme.
     **/
    @ApiModelProperty(required = true, value = "Unique and unambiguous identification of the bank under above scheme.")
    @JsonProperty("identification")
    public String getIdentification() {

        return identification;
    }

    public void setIdentification(String identification) {

        this.identification = identification;
    }

    /**
     * The publicly refer name for the bank.
     **/
    @ApiModelProperty(required = true, value = "The publicly referd name for the bank")
    @JsonProperty("bankName")
    public String getBankName() {

        return bankName;
    }

    public void setBankName(String bankName) {

        this.bankName = bankName;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class BankDTO {\n");
        sb.append("  schemeName: ").append(schemeName).append("\n");
        sb.append("  identification: ").append(identification).append("\n");
        sb.append("  bankName: ").append(bankName).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
