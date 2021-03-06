package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Amount of money to be moved between the debtor and creditor.
 * before deduction of charges, expressed in the currency as ordered by the ecommerce site.
 * Usage- This amount has to be transported unchanged through the transaction chain.
 **/
@ApiModel(description = "Amount of money to be moved between the debtor and creditor")
public class InstructedAmountDTO {

    private String amount = null;

    private String currency = null;

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("amount")
    public String getAmount() {

        return amount;
    }

    public void setAmount(String amount) {

        this.amount = amount;
    }

    /**
     * A code allocated to a currency by a Maintenance Agency under an international identification scheme.
     * as described in the latest edition of the international
     * standard ISO 4217 - Codes for the representation of currencies and funds.
     **/
    @ApiModelProperty(value = "A code allocated to a currency by a Maintenance Agency.")
    @JsonProperty("currency")
    public String getCurrency() {

        return currency;
    }

    public void setCurrency(String currency) {

        this.currency = currency;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class InstructedAmountDTO {\n");

        sb.append("  amount: ").append(amount).append("\n");
        sb.append("  currency: ").append(currency).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
