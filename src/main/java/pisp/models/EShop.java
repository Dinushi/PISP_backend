/*
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *   This software is the property of WSO2 Inc. and its suppliers, if any.
 *   Dissemination of any information or reproduction of any material contained
 *   herein is strictly forbidden, unless permitted by WSO2 in accordance with
 *   the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 *   language governing the permissions and limitations under this license,
 *   please see the license as well as any agreement you’ve entered into with
 *   WSO2 governing the purchase of this software and any associated services.
 */

package pisp.models;


import pisp.utilities.constants.Constants;

/**
 * This class is to hold the e-shop User details.
 */
public class EShop {


    private String eShopName;
    private String eShopRegistrationNo;
    private String registeredBusinessName;
    private String registeredCountry;
    private String clientId;
    private String clientSecret;
    private String eShopCategory;
    private String username;
    private String password;
    private String email;
    //this field is valid only if the e-commerceMarketplaceCategory is set to single_vendor
    private Merchant merchant;

    public EShop(String ecommerceMarketplaceCategory) {

        this.eShopCategory = ecommerceMarketplaceCategory;
        if (!ecommerceMarketplaceCategory.equals(Constants.SINGLE_VENDOR)) {
            this.merchant = null;
        }
    }

    public String getEShopRegistrationNo() {
        return eShopRegistrationNo;
    }

    public void setEShopRegistrationNo(String eShopRegistrationNo) {
        this.eShopRegistrationNo = eShopRegistrationNo;
    }

    public String getRegisteredBusinessName() {
        return registeredBusinessName;
    }

    public void setRegisteredBusinessName(String registeredBusinessName) {
        this.registeredBusinessName = registeredBusinessName;
    }

    public String getRegisteredCountry() {
        return registeredCountry;
    }

    public void setRegisteredCountry(String registeredCountry) {
        this.registeredCountry = registeredCountry;
    }

    public String getEShopCategory() {
        return eShopCategory;
    }

    public void setEShopCategory(String ecommerceMarketplaceCategory) {
        this.eShopCategory = ecommerceMarketplaceCategory;
    }

    public String getClientId() {
        return clientId;
    }

    /**
     * Set the unique id issued to EShop after successful registration with PISP.
     * @param clientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getEShopName() {
        return eShopName;
    }

    public void setEShopName(String eShopName) {
        this.eShopName = eShopName;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        if (this.eShopCategory.equals(Constants.SINGLE_VENDOR)) {
            this.merchant = merchant;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
