package pisp.dto;

import pisp.dto.BankAccountDTO;
import pisp.dto.BankDTO;
import pisp.dto.PaymentHistoryInnerInstructedAmountDTO;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class PaymentInitResponseDTO  {


  @NotNull
  private String paymentInitReqId = null;

  public enum PaymentStatusEnum {
    Received,  AccessedByDebtor,  Initialized,  Submitted,  Completed,
  };

  private PaymentStatusEnum paymentStatus = null;


  private String username = null;


  private String merchantCategoryCodeOfMerchant = null;


  private String endToEndIdentificationOfPayment = null;


  private PaymentHistoryInnerInstructedAmountDTO instructedAmount = null;

  @NotNull
  private String merchantName = null;

  @NotNull
  private BankDTO merchantBank = null;


  private BankAccountDTO merchantBankAccountData = null;

  @NotNull
  private String redirectLink = null;


  /**
   * The id created by PISP for a new payment init request created
   **/
  @ApiModelProperty(required = true, value = "The id created by PISP for a new payment init request created")
  @JsonProperty("paymentInitReqId")
  public String getPaymentInitReqId() {
    return paymentInitReqId;
  }
  public void setPaymentInitReqId(String paymentInitReqId) {
    this.paymentInitReqId = paymentInitReqId;
  }


  /**
   * The ststus of the payment initiation request during the process through PISP
   **/
  @ApiModelProperty(value = "The ststus of the payment initiation request during the process through PISP")
  @JsonProperty("paymentStatus")
  public PaymentStatusEnum getPaymentStatus() {
    return paymentStatus;
  }
  public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
    this.paymentStatus = paymentStatus;
  }


  /**
   * The username of ecommerce user as registered at PISP
   **/
  @ApiModelProperty(value = "The username of ecommerce user as registered at PISP")
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }


  /**
   * Category code of merchant conforms to ISO 18245, related to the type of services or goods the single vendor/merchant provides for the purchase
   **/
  @ApiModelProperty(value = "Category code of merchant conforms to ISO 18245, related to the type of services or goods the single vendor/merchant provides for the purchase")
  @JsonProperty("merchantCategoryCodeOfMerchant")
  public String getMerchantCategoryCodeOfMerchant() {
    return merchantCategoryCodeOfMerchant;
  }
  public void setMerchantCategoryCodeOfMerchant(String merchantCategoryCodeOfMerchant) {
    this.merchantCategoryCodeOfMerchant = merchantCategoryCodeOfMerchant;
  }


  /**
   * The id issued by the ecommerce site to uniquely identify the initiated payment.
   **/
  @ApiModelProperty(value = "The id issued by the ecommerce site to uniquely identify the initiated payment.")
  @JsonProperty("endToEndIdentificationOfPayment")
  public String getEndToEndIdentificationOfPayment() {
    return endToEndIdentificationOfPayment;
  }
  public void setEndToEndIdentificationOfPayment(String endToEndIdentificationOfPayment) {
    this.endToEndIdentificationOfPayment = endToEndIdentificationOfPayment;
  }


  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("instructedAmount")
  public PaymentHistoryInnerInstructedAmountDTO getInstructedAmount() {
    return instructedAmount;
  }
  public void setInstructedAmount(PaymentHistoryInnerInstructedAmountDTO instructedAmount) {
    this.instructedAmount = instructedAmount;
  }


  /**
   * conditional,only for multi vendor ecommerce users, The name of the merchant/product seller as registered in ecommerce site
   **/
  @ApiModelProperty(required = true, value = "conditional,only for multi vendor ecommerce users, The name of the merchant/product seller as registered in ecommerce site")
  @JsonProperty("merchantName")
  public String getMerchantName() {
    return merchantName;
  }
  public void setMerchantName(String merchantName) {
    this.merchantName = merchantName;
  }


  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("merchantBank")
  public BankDTO getMerchantBank() {
    return merchantBank;
  }
  public void setMerchantBank(BankDTO merchantBank) {
    this.merchantBank = merchantBank;
  }


  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("merchantBankAccountData")
  public BankAccountDTO getMerchantBankAccountData() {
    return merchantBankAccountData;
  }
  public void setMerchantBankAccountData(BankAccountDTO merchantBankAccountData) {
    this.merchantBankAccountData = merchantBankAccountData;
  }


  /**
   * Link URI to redirect the customer to PISP
   **/
  @ApiModelProperty(required = true, value = "Link URI to redirect the customer to PISP")
  @JsonProperty("redirectLink")
  public String getRedirectLink() {
    return redirectLink;
  }
  public void setRedirectLink(String redirectLink) {
    this.redirectLink = redirectLink;
  }



  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentInitResponseDTO {\n");

    sb.append("  paymentInitReqId: ").append(paymentInitReqId).append("\n");
    sb.append("  paymentStatus: ").append(paymentStatus).append("\n");
    sb.append("  username: ").append(username).append("\n");
    sb.append("  merchantCategoryCodeOfMerchant: ").append(merchantCategoryCodeOfMerchant).append("\n");
    sb.append("  endToEndIdentificationOfPayment: ").append(endToEndIdentificationOfPayment).append("\n");
    sb.append("  instructedAmount: ").append(instructedAmount).append("\n");
    sb.append("  merchantName: ").append(merchantName).append("\n");
    sb.append("  merchantBank: ").append(merchantBank).append("\n");
    sb.append("  merchantBankAccountData: ").append(merchantBankAccountData).append("\n");
    sb.append("  redirectLink: ").append(redirectLink).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
