package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * The response returned when the customer selects a bank to pay via.
 **/
@ApiModel(description = "The response returned when the customer selects a bank to pay via")
public class BankSelectionResponseDTO {

    private String bankName = null;

    @NotNull
    private Boolean accountRequired = null;

    @NotNull
    private Boolean submissionRequired = null;

    /**
     * Name of the selected bank.
     **/
    @ApiModelProperty(value = "Name of the selected bank")
    @JsonProperty("bankName")
    public String getBankName() {

        return bankName;
    }

    public void setBankName(String bankName) {

        this.bankName = bankName;
    }

    /**
     * Specifies True, if customer account is essential to initiate a payment in the selected bank.
     **/
    @ApiModelProperty(required = true, value = "Specifies True, if customer account is essential to initiate" +
            "a payment in the selected bank")
    @JsonProperty("accountRequired")
    public Boolean getAccountRequired() {

        return accountRequired;
    }

    public void setAccountRequired(Boolean accountRequired) {

        this.accountRequired = accountRequired;
    }

    /**
     * Specifies True, if a submission flow is required to be done after PSU authorization of payment.
     **/
    @ApiModelProperty(required = true, value = "Specifies True, if a submission flow is required to be " +
            "done after PSU authorization of payment")
    @JsonProperty("submissionRequired")
    public Boolean getSubmissionRequired() {

        return submissionRequired;
    }

    public void setSubmissionRequired(Boolean submissionRequired) {

        this.submissionRequired = submissionRequired;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class BankSelectionResponseDTO {\n");

        sb.append("  bankName: ").append(bankName).append("\n");
        sb.append("  accountRequired: ").append(accountRequired).append("\n");
        sb.append("  submissionRequired: ").append(submissionRequired).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
