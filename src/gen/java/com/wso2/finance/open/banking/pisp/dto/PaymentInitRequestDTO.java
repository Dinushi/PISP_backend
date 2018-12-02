package com.wso2.finance.open.banking.pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;

/**
 * The class that transfer the payment details from e-shop to PISP.
 */
@ApiModel(description = "")
public class PaymentInitRequestDTO {

    private InstructedAmountDTO instructedAmount = null;

    private MerchantInfoDTO merchantInfo = null;

    @NotNull
    private String customerIdentificationByEShop = null;



    private PaymentInitRequestDeliveryAddressDTO deliveryAddress = null;

    @NotNull
    private String redirectURI = null;

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("instructedAmount")
    public InstructedAmountDTO getInstructedAmount() {

        return instructedAmount;
    }

    public void setInstructedAmount(InstructedAmountDTO instructedAmount) {

        this.instructedAmount = instructedAmount;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("merchantInfo")
    public MerchantInfoDTO getMerchantInfo() {

        return merchantInfo;
    }

    public void setMerchantInfo(MerchantInfoDTO merchantInfo) {

        this.merchantInfo = merchantInfo;
    }

    /**
     * The ID used by ecommerce site/app to uniquely identify the customer who requested to initiate the payment.
     **/
    @ApiModelProperty(required = true, value = "The ID used by ecommerce site/app to uniquely identify the customer")
    @JsonProperty("customerIdentificationByEShop")
    public String getCustomerIdentificationByEShop() {

        return customerIdentificationByEShop;
    }

    public void setCustomerIdentificationByEShop(String customerIdentificationByEShop) {

        this.customerIdentificationByEShop = customerIdentificationByEShop;
    }



    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("deliveryAddress")
    public PaymentInitRequestDeliveryAddressDTO getDeliveryAddress() {

        return deliveryAddress;
    }

    public void setDeliveryAddress(PaymentInitRequestDeliveryAddressDTO deliveryAddress) {

        this.deliveryAddress = deliveryAddress;
    }

    /**
     * Link URI.
     **/
    @ApiModelProperty(required = true, value = "Link URI")
    @JsonProperty("redirectURI")
    public String getRedirectURI() {

        return redirectURI;
    }

    public void setRedirectURI(String redirectURI) {

        this.redirectURI = redirectURI;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PaymentInitRequestDTO {\n");
        sb.append("  instructedAmount: ").append(instructedAmount).append("\n");
        sb.append("  merchantInfo: ").append(merchantInfo).append("\n");
        sb.append("  customerIdentificationByEShop: ").append(customerIdentificationByEShop).append("\n");
        sb.append("  deliveryAddress: ").append(deliveryAddress).append("\n");
        sb.append("  redirectURI: ").append(redirectURI).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
