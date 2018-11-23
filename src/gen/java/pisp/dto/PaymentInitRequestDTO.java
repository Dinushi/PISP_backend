package pisp.dto;

import java.util.ArrayList;
import java.util.List;
import pisp.dto.MerchantInfoDTO;
import pisp.dto.PaymentInitRequestDeliveryAddressDTO;
import pisp.dto.PaymentInitRequestInstructedAmountDTO;
import pisp.dto.PaymentInitRequestItemsPurchasedDTO;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class PaymentInitRequestDTO  {
  
  
  @NotNull
  private String eShopUsername = null;
  
  
  private PaymentInitRequestInstructedAmountDTO instructedAmount = null;
  
  
  private MerchantInfoDTO merchantInfo = null;
  
  @NotNull
  private String customerIdentificationByEShop = null;
  
  
  private List<PaymentInitRequestItemsPurchasedDTO> itemsPurchased = new ArrayList<PaymentInitRequestItemsPurchasedDTO>();
  
  
  private PaymentInitRequestDeliveryAddressDTO deliveryAddress = null;
  
  @NotNull
  private String redirectURI = null;

  
  /**
   * The username of ecommerce user as registered at PISP
   **/
  @ApiModelProperty(required = true, value = "The username of ecommerce user as registered at PISP")
  @JsonProperty("eShopUsername")
  public String getEShopUsername() {
    return eShopUsername;
  }
  public void setEShopUsername(String eShopUsername) {
    this.eShopUsername = eShopUsername;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("instructedAmount")
  public PaymentInitRequestInstructedAmountDTO getInstructedAmount() {
    return instructedAmount;
  }
  public void setInstructedAmount(PaymentInitRequestInstructedAmountDTO instructedAmount) {
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
   * The ID used by ecommerce site/app to uniquely identify the customer who requested to initiate the payment
   **/
  @ApiModelProperty(required = true, value = "The ID used by ecommerce site/app to uniquely identify the customer who requested to initiate the payment")
  @JsonProperty("customerIdentificationByEShop")
  public String getCustomerIdentificationByEShop() {
    return customerIdentificationByEShop;
  }
  public void setCustomerIdentificationByEShop(String customerIdentificationByEShop) {
    this.customerIdentificationByEShop = customerIdentificationByEShop;
  }

  
  /**
   * The items purchased by the payer/customer
   **/
  @ApiModelProperty(value = "The items purchased by the payer/customer")
  @JsonProperty("itemsPurchased")
  public List<PaymentInitRequestItemsPurchasedDTO> getItemsPurchased() {
    return itemsPurchased;
  }
  public void setItemsPurchased(List<PaymentInitRequestItemsPurchasedDTO> itemsPurchased) {
    this.itemsPurchased = itemsPurchased;
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
   * Link URI
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
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentInitRequestDTO {\n");
    
    sb.append("  eShopUsername: ").append(eShopUsername).append("\n");
    sb.append("  instructedAmount: ").append(instructedAmount).append("\n");
    sb.append("  merchantInfo: ").append(merchantInfo).append("\n");
    sb.append("  customerIdentificationByEShop: ").append(customerIdentificationByEShop).append("\n");
    sb.append("  itemsPurchased: ").append(itemsPurchased).append("\n");
    sb.append("  deliveryAddress: ").append(deliveryAddress).append("\n");
    sb.append("  redirectURI: ").append(redirectURI).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
