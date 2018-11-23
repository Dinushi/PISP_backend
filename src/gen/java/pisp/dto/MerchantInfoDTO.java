package pisp.dto;

import pisp.dto.BankAccountDTO;
import pisp.dto.BankDTO;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class MerchantInfoDTO  {
  
  
  @NotNull
  private String merchantIdentificationByEshop = null;
  
  
  private String merchantName = null;
  
  @NotNull
  private String merchantCategoryCode = null;
  
  
  private String merchantProductType = null;
  
  @NotNull
  private BankDTO merchantBank = null;
  
  @NotNull
  private BankAccountDTO merchantBankAccountData = null;

  
  /**
   * For multi vendor ecommerce sites/apps, The id of the merchant as registered in ecommerce site/app.For single vendor, use the eShopUsername itself for this field.
   **/
  @ApiModelProperty(required = true, value = "For multi vendor ecommerce sites/apps, The id of the merchant as registered in ecommerce site/app.For single vendor, use the eShopUsername itself for this field.")
  @JsonProperty("merchantIdentificationByEshop")
  public String getMerchantIdentificationByEshop() {
    return merchantIdentificationByEshop;
  }
  public void setMerchantIdentificationByEshop(String merchantIdentificationByEshop) {
    this.merchantIdentificationByEshop = merchantIdentificationByEshop;
  }

  
  /**
   * For multi vendor ecommerce users, The name of the merchant/product seller as registered in ecommerce site. For single vendor, use the eShopUsername itself for this field.
   **/
  @ApiModelProperty(value = "For multi vendor ecommerce users, The name of the merchant/product seller as registered in ecommerce site. For single vendor, use the eShopUsername itself for this field.")
  @JsonProperty("merchantName")
  public String getMerchantName() {
    return merchantName;
  }
  public void setMerchantName(String merchantName) {
    this.merchantName = merchantName;
  }

  
  /**
   * conditional. Only valid for single vendor.Category code conforms to ISO 18245, related to the type of services or goods the single vendor/merchant provides for the transaction
   **/
  @ApiModelProperty(required = true, value = "conditional. Only valid for single vendor.Category code conforms to ISO 18245, related to the type of services or goods the single vendor/merchant provides for the transaction")
  @JsonProperty("merchantCategoryCode")
  public String getMerchantCategoryCode() {
    return merchantCategoryCode;
  }
  public void setMerchantCategoryCode(String merchantCategoryCode) {
    this.merchantCategoryCode = merchantCategoryCode;
  }

  
  /**
   * conditional. Only valid for single vendor
   **/
  @ApiModelProperty(value = "conditional. Only valid for single vendor")
  @JsonProperty("merchantProductType")
  public String getMerchantProductType() {
    return merchantProductType;
  }
  public void setMerchantProductType(String merchantProductType) {
    this.merchantProductType = merchantProductType;
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
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("merchantBankAccountData")
  public BankAccountDTO getMerchantBankAccountData() {
    return merchantBankAccountData;
  }
  public void setMerchantBankAccountData(BankAccountDTO merchantBankAccountData) {
    this.merchantBankAccountData = merchantBankAccountData;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class MerchantInfoDTO {\n");
    
    sb.append("  merchantIdentificationByEshop: ").append(merchantIdentificationByEshop).append("\n");
    sb.append("  merchantName: ").append(merchantName).append("\n");
    sb.append("  merchantCategoryCode: ").append(merchantCategoryCode).append("\n");
    sb.append("  merchantProductType: ").append(merchantProductType).append("\n");
    sb.append("  merchantBank: ").append(merchantBank).append("\n");
    sb.append("  merchantBankAccountData: ").append(merchantBankAccountData).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
