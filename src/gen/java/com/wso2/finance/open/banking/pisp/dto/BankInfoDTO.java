package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * The class that transfer details about banks that are supported by PISP.
 */
@ApiModel(description = "")
public class BankInfoDTO {

    @NotNull
    private BankDTO bank = null;

    /**
     * OB Spec Names.
     */
    public enum SpecForOBEnum {
        UK, BERLIN, STET,
    }

    ;
    @NotNull
    private SpecForOBEnum specForOB = null;

    private String clientId = null;

    /**
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("bank")
    public BankDTO getBank() {

        return bank;
    }

    public void setBank(BankDTO bank) {

        this.bank = bank;
    }

    /**
     * The open banking specification followed by the bank.
     **/
    @ApiModelProperty(required = true, value = "The open banking specification followed by the bank")
    @JsonProperty("specForOB")
    public SpecForOBEnum getSpecForOB() {

        return specForOB;
    }

    public void setSpecForOB(SpecForOBEnum specForOB) {

        this.specForOB = specForOB;
    }

    /**
     * The production keys issued by bank to the PISP, once registered as a TPP and created.
     * an Application subscribing to payment API. Will be use for client credentials grant.
     **/
    @ApiModelProperty(value = "The production keys issued by bank to the PISP, once registered as a TPP " +
            "and created an Application subscribing to payment API.")
    @JsonProperty("clientId")
    public String getClientId() {

        return clientId;
    }

    public void setClientId(String clientId) {

        this.clientId = clientId;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class BankInfoDTO {\n");

        sb.append("  bank: ").append(bank).append("\n");
        sb.append("  specForOB: ").append(specForOB).append("\n");
        sb.append("  clientId: ").append(clientId).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
