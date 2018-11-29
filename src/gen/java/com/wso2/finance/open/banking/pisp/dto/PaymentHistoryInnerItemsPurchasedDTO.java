package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * The class that transfer payment data.
 */
@ApiModel(description = "")
public class PaymentHistoryInnerItemsPurchasedDTO {

    private String itemName = null;

    private Integer quantity = null;

    private PaymentHistoryInnerPriceDTO price = null;

    /**
     * The name of the item purchased.
     **/
    @ApiModelProperty(value = "The name of the item purchased")
    @JsonProperty("itemName")
    public String getItemName() {

        return itemName;
    }

    public void setItemName(String itemName) {

        this.itemName = itemName;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("quantity")
    public Integer getQuantity() {

        return quantity;
    }

    public void setQuantity(Integer quantity) {

        this.quantity = quantity;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("price")
    public PaymentHistoryInnerPriceDTO getPrice() {

        return price;
    }

    public void setPrice(PaymentHistoryInnerPriceDTO price) {

        this.price = price;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PaymentHistoryInnerItemsPurchasedDTO {\n");

        sb.append("  itemName: ").append(itemName).append("\n");
        sb.append("  quantity: ").append(quantity).append("\n");
        sb.append("  price: ").append(price).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
