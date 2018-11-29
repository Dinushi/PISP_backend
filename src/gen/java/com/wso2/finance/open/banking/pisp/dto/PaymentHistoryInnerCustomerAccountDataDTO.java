package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * The class is to transfer payment history details.
 */
@ApiModel(description = "")
public class PaymentHistoryInnerCustomerAccountDataDTO {

    private String bank = null;

    private String accountOwnerName = null;

    private String iban = null;

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("bank")
    public String getBank() {

        return bank;
    }

    public void setBank(String bank) {

        this.bank = bank;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("accountOwnerName")
    public String getAccountOwnerName() {

        return accountOwnerName;
    }

    public void setAccountOwnerName(String accountOwnerName) {

        this.accountOwnerName = accountOwnerName;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("iban")
    public String getIban() {

        return iban;
    }

    public void setIban(String iban) {

        this.iban = iban;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PaymentHistoryInnerCustomerAccountDataDTO {\n");
        sb.append("  bank: ").append(bank).append("\n");
        sb.append("  accountOwnerName: ").append(accountOwnerName).append("\n");
        sb.append("  iban: ").append(iban).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
