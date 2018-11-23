package pisp.dto;

import pisp.dto.MerchantInfoDTO;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class EShopProfileDTO  {
  
  
  
  private String eShopName = null;
  

  private String eShopRegistrationNo = null;
  
  
  private String registeredBussinessName = null;
  

  private String registeredCountry = null;
  

  private String username = null;
  

  private String password = null;
  

  private String email = null;
  
  public enum EcommerceMarketplaceCategoryEnum {
     single_vendor,  multi_vendor, 
  };

  private EcommerceMarketplaceCategoryEnum ecommerceMarketplaceCategory = null;
  
  
  private MerchantInfoDTO merchantInfo = null;

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("eShopName")
  public String getEShopName() {
    return eShopName;
  }
  public void setEShopName(String eShopName) {
    this.eShopName = eShopName;
  }

  
  /**
   * Reg number issued by a authorized institute
   **/
  @ApiModelProperty(value = "Reg number issued by a authorized institute")
  @JsonProperty("eShopRegistrationNo")
  public String getEShopRegistrationNo() {
    return eShopRegistrationNo;
  }
  public void setEShopRegistrationNo(String eShopRegistrationNo) {
    this.eShopRegistrationNo = eShopRegistrationNo;
  }

  
  /**
   * Name used for registration
   **/
  @ApiModelProperty(value = "Name used for registration")
  @JsonProperty("registeredBussinessName")
  public String getRegisteredBussinessName() {
    return registeredBussinessName;
  }
  public void setRegisteredBussinessName(String registeredBussinessName) {
    this.registeredBussinessName = registeredBussinessName;
  }

  
  /**
   **/
  @ApiModelProperty( value = "")
  @JsonProperty("registeredCountry")
  public String getRegisteredCountry() {
    return registeredCountry;
  }
  public void setRegisteredCountry(String registeredCountry) {
    this.registeredCountry = registeredCountry;
  }

  
  /**
   **/
  @ApiModelProperty( value = "")
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }

  
  /**
   **/
  @ApiModelProperty( value = "")
  @JsonProperty("password")
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  
  /**
   * Avaialble categories-single-vendor, multi-vendor. Should be selected as single-vendor if the owner of the site is the only merchant itself and multi-vendor if the site is hosting for multiple vendors.
   **/
  @ApiModelProperty( value = "Avaialble categories-single-vendor, multi-vendor. Should be selected as single-vendor if the owner of the site is the only merchant itself and multi-vendor if the site is hosting for multiple vendors.")
  @JsonProperty("ecommerceMarketplaceCategory")
  public EcommerceMarketplaceCategoryEnum getEcommerceMarketplaceCategory() {
    return ecommerceMarketplaceCategory;
  }
  public void setEcommerceMarketplaceCategory(EcommerceMarketplaceCategoryEnum ecommerceMarketplaceCategory) {
    this.ecommerceMarketplaceCategory = ecommerceMarketplaceCategory;
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

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class EShopProfileDTO {\n");
    
    sb.append("  eShopName: ").append(eShopName).append("\n");
    sb.append("  eShopRegistrationNo: ").append(eShopRegistrationNo).append("\n");
    sb.append("  registeredBussinessName: ").append(registeredBussinessName).append("\n");
    sb.append("  registeredCountry: ").append(registeredCountry).append("\n");
    sb.append("  username: ").append(username).append("\n");
    sb.append("  password: ").append(password).append("\n");
    sb.append("  email: ").append(email).append("\n");
    sb.append("  ecommerceMarketplaceCategory: ").append(ecommerceMarketplaceCategory).append("\n");
    sb.append("  merchantInfo: ").append(merchantInfo).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
