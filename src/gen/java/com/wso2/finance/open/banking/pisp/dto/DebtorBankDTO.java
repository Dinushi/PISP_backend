package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * The class that transfers debtor bank details selected by PSU.
 */
@ApiModel(description = "")
public class DebtorBankDTO {

    @NotNull
    private String bankUid = null;

    private String bankName = null;

    /**
     * Unique and unambiguous identification of the bank under above scheme.
     **/
    @ApiModelProperty(required = true, value = "Unique and unambiguous identification of the bank under above scheme.")
    @JsonProperty("bankUid")
    public String getBankUid() {

        return bankUid;
    }

    public void setBankUid(String bankUid) {

        this.bankUid = bankUid;
    }

    /**
     * The publicly referd name for the bank.
     **/
    @ApiModelProperty(value = "The publicly referd name for the bank")
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
        sb.append("class DebtorBankDTO {\n");

        sb.append("  bankUid: ").append(bankUid).append("\n");
        sb.append("  bankName: ").append(bankName).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
