package com.wso2.finance.open.banking.pisp.dto;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * The class that transfers E-Shop User details.
 */
@ApiModel(description = "")
public class EShopProfileDTO {

    @NotNull
    private String eShopName = null;

    private String eShopRegistrationNo = null;

    private String registeredBussinessName = null;

    private String registeredCountry = null;

    @NotNull
    private String username = null;

    private String password = null;

    private String email = null;

    /**
     * The market category of E-Shop.
     */
    public enum MarketCategoryEnum {
        single_vendor, multi_vendor,
    }


    private MarketCategoryEnum marketCategory = null;

    private MerchantInfoDTO merchantInfo = null;

    /**
     * The common name of E-shop.
     **/
    @ApiModelProperty(required = true,value = "")
    @JsonProperty("eShopName")
    public String getEShopName() {

        return eShopName;
    }

    public void setEShopName(String eShopName) {

        this.eShopName = eShopName;
    }

    /**
     * Reg number issued by a authorized institute.
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
     * Name used for registration.
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
     * The country where the E-Shop site is legally registered.
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("registeredCountry")
    public String getRegisteredCountry() {

        return registeredCountry;
    }

    public void setRegisteredCountry(String registeredCountry) {

        this.registeredCountry = registeredCountry;
    }

    /**
     * The username chosen by E-Shop.
     * @return
     */
    @ApiModelProperty(required = true,value = "")
    @JsonProperty("username")
    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }


    @ApiModelProperty(value = "")
    @JsonProperty("password")
    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    @ApiModelProperty(value = "")
    @JsonProperty("email")
    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    /**
     * Available categories-single-vendor, multi-vendor.
     * Should be selected as single-vendor if the owner of the site is the only merchant itself.
     * Should be selected as multi-vendor if the site is hosting for multiple vendors.
     **/
    @ApiModelProperty(value = "Available categories - single-vendor, multi-vendor.")
    @JsonProperty("marketCategory")
    public MarketCategoryEnum getMarketCategory() {

        return marketCategory;
    }

    public void setMarketCategory(MarketCategoryEnum marketCategory) {

        this.marketCategory = marketCategory;
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
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class EShopProfileDTO {\n");

        sb.append("  eShopName: ").append(eShopName).append("\n");
        sb.append("  eShopRegistrationNo: ").append(eShopRegistrationNo).append("\n");
        sb.append("  registeredBussinessName: ").append(registeredBussinessName).append("\n");
        sb.append("  registeredCountry: ").append(registeredCountry).append("\n");
        sb.append("  username: ").append(username).append("\n");
        sb.append("  password: ").append(password).append("\n");
        sb.append("  email: ").append(email).append("\n");
        sb.append("  marketCategory: ").append(marketCategory).append("\n");
        sb.append("  merchantInfo: ").append(merchantInfo).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
