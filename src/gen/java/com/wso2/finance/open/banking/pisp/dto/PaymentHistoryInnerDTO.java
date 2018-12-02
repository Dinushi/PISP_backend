package com.wso2.finance.open.banking.pisp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The class is to transfer payment history details.
 */
@ApiModel(description = "")
public class PaymentHistoryInnerDTO {

    private String dateTime = null;

    private String customerIdentificationNo = null;

    private PaymentHistoryBankAccountDTO customerAccountData = null;

    private String paymentId = null;

    private String purchaseId = null;

    private String merchantIdentificationNo = null;

    private PaymentHistoryBankAccountDTO merchantAccountData = null;

    /**
     * The status of payments.
     */
    public enum PaymentStatusEnum {
        completed, declined,
    }

    ;

    private PaymentStatusEnum paymentStatus = null;

    private InstructedAmountDTO instructedAmount = null;

    private String customerName = null;

    private String merchantName = null;

    /**
     * The time which the payment was committed.
     **/
    @ApiModelProperty(value = "The time which the payment was committed")
    @JsonProperty("dateTime")
    public String getDateTime() {

        return dateTime;
    }

    public void setDateTime(String dateTime) {

        this.dateTime = dateTime;
    }

    /**
     * The id of the customer issued by e-commerce site/app.
     **/
    @ApiModelProperty(value = "The id of the customer issued by e-commerce site/app")
    @JsonProperty("customerIdentificationNo")
    public String getCustomerIdentificationNo() {

        return customerIdentificationNo;
    }

    public void setCustomerIdentificationNo(String customerIdentificationNo) {

        this.customerIdentificationNo = customerIdentificationNo;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("customerAccountData")
    public PaymentHistoryBankAccountDTO getCustomerAccountData() {

        return customerAccountData;
    }

    public void setCustomerAccountData(PaymentHistoryBankAccountDTO customerAccountData) {

        this.customerAccountData = customerAccountData;
    }

    /**
     * The id for the payment issued by PISP.
     **/
    @ApiModelProperty(value = "The id for the payment issued by PISP")
    @JsonProperty("paymentId")
    public String getPaymentId() {

        return paymentId;
    }

    public void setPaymentId(String paymentId) {

        this.paymentId = paymentId;
    }

    /**
     * The unique id issued by e-shop to identify the purchase/payment.
     **/
    @ApiModelProperty(value = "The unique id issued by e-shop to identify the purchase/payment.")
    @JsonProperty("purchaseId")
    public String getPurchaseId() {

        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {

        this.purchaseId = purchaseId;
    }

    /**
     * The id of the seller/merchant issued by e-commerce site/app.
     **/
    @ApiModelProperty(value = "The id of the seller/merchant issued by e-commerce site/app")
    @JsonProperty("merchantIdentificationNo")
    public String getMerchantIdentificationNo() {

        return merchantIdentificationNo;
    }

    public void setMerchantIdentificationNo(String merchantIdentificationNo) {

        this.merchantIdentificationNo = merchantIdentificationNo;
    }

    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("merchantAccountData")
    public PaymentHistoryBankAccountDTO getMerchantAccountData() {

        return merchantAccountData;
    }

    public void setMerchantAccountData(PaymentHistoryBankAccountDTO merchantAccountData) {

        this.merchantAccountData = merchantAccountData;
    }

    /**
     * The status of the payment.
     **/
    @ApiModelProperty(value = "The status of the payment.")
    @JsonProperty("paymentStatus")
    public PaymentStatusEnum getPaymentStatus() {

        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {

        this.paymentStatus = paymentStatus;
    }

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
     * The name of the buyer/customer.
     **/
    @ApiModelProperty(value = "The name of the buyer/customer")
    @JsonProperty("customerName")
    public String getCustomerName() {

        return customerName;
    }

    public void setCustomerName(String customerName) {

        this.customerName = customerName;
    }

    /**
     * The name of the seller/merchant.
     **/
    @ApiModelProperty(value = "The name of the seller/merchant")
    @JsonProperty("merchantName")
    public String getMerchantName() {

        return merchantName;
    }

    public void setMerchantName(String merchantName) {

        this.merchantName = merchantName;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PaymentHistoryInnerDTO {\n");

        sb.append("  dateTime: ").append(dateTime).append("\n");
        sb.append("  customerIdentificationNo: ").append(customerIdentificationNo).append("\n");
        sb.append("  customerAccountData: ").append(customerAccountData).append("\n");
        sb.append("  paymentId: ").append(paymentId).append("\n");
        sb.append("  purchaseId: ").append(purchaseId).append("\n");
        sb.append("  merchantIdentificationNo: ").append(merchantIdentificationNo).append("\n");
        sb.append("  merchantAccountData: ").append(merchantAccountData).append("\n");
        sb.append("  paymentStatus: ").append(paymentStatus).append("\n");
        sb.append("  instructedAmount: ").append(instructedAmount).append("\n");
        sb.append("  customerName: ").append(customerName).append("\n");
        sb.append("  merchantName: ").append(merchantName).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
