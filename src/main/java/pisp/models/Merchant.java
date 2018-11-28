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

/**
 * Model the merchant who is the payee of the payment.
 */
public class Merchant {

    private String merchantId;
    private String merchantName;
    private String merchantIdentificationByEShop;
    private String merchantCategoryCode;
    private String merchantProductType;

    private Bank merchantBank;
    private BankAccount merchantAccount;

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantCategoryCode() {
        return merchantCategoryCode;
    }

    public void setMerchantCategoryCode(String merchantCategoryCode) {
        this.merchantCategoryCode = merchantCategoryCode;
    }

    public String getMerchantIdentificationByEShop() {
        return merchantIdentificationByEShop;
    }

    public void setMerchantIdentificationByEShop(String merchantIdentificationByEShop) {
        this.merchantIdentificationByEShop = merchantIdentificationByEShop;
    }

    public Bank getMerchantBank() {
        return merchantBank;
    }

    public void setMerchantBank(Bank merchantBank) {
        this.merchantBank = merchantBank;
    }

    public BankAccount getMerchantAccount() {
        return merchantAccount;
    }

    public void setMerchantAccount(BankAccount merchantAccount) {
        this.merchantAccount = merchantAccount;
    }

    public String getMerchantProductType() {
        return merchantProductType;
    }

    public void setMerchantProductType(String merchantProductType) {
        this.merchantProductType = merchantProductType;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
