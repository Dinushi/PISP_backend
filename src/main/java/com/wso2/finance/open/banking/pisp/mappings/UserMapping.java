/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 *
 */
package com.wso2.finance.open.banking.pisp.mappings;

import com.wso2.finance.open.banking.pisp.dto.BankAccountDTO;
import com.wso2.finance.open.banking.pisp.dto.EShopProfileDTO;
import com.wso2.finance.open.banking.pisp.dto.EShopRegistrationResponseDTO;
import com.wso2.finance.open.banking.pisp.dto.MerchantInfoDTO;
import com.wso2.finance.open.banking.pisp.dto.PSUProfileDTO;
import com.wso2.finance.open.banking.pisp.models.Bank;
import com.wso2.finance.open.banking.pisp.models.BankAccount;
import com.wso2.finance.open.banking.pisp.models.EShop;
import com.wso2.finance.open.banking.pisp.models.Merchant;
import com.wso2.finance.open.banking.pisp.models.PSU;
import com.wso2.finance.open.banking.pisp.utilities.constants.Constants;

/**
 * This class is used to map User related DTOs with internal models.
 */
public class UserMapping {

    /**
     * generate new PSU instance.
     * @param psuProfileBody
     * @return
     */
    public static PSU createPSUInstance (PSUProfileDTO psuProfileBody) {
        if (psuProfileBody == null) {
            return null;
        }
        PSU psu = new PSU();
        psu.setFirstName(psuProfileBody.getFirstName());
        psu.setLastName(psuProfileBody.getLastName());
        psu.setUsername(psuProfileBody.getUsername());
        psu.setPassword(psuProfileBody.getPassword());
        psu.setEmail(psuProfileBody.getEmail());
        return psu;
    }

    /**
     *
     * @param eShopProfileBody
     * @return E_shop instance which is mapped to the request body of EShopProfileDTO
     */
    public static EShop createEshopInstance(EShopProfileDTO eShopProfileBody) {
        if (eShopProfileBody == null) {
            return null;
        }
        EShop eShop = new EShop(eShopProfileBody.getMarketCategory().toString());
        eShop.setEShopName(eShopProfileBody.getEShopName());
        eShop.setUsername(eShopProfileBody.getUsername());
        eShop.setEShopRegistrationNo(eShopProfileBody.getEShopRegistrationNo());
        eShop.setRegisteredBusinessName(eShopProfileBody.getRegisteredBussinessName());
        eShop.setRegisteredCountry(eShopProfileBody.getRegisteredCountry());

        eShop.setEmail(eShopProfileBody.getEmail());
        eShop.setPassword(eShopProfileBody.getPassword());

        eShop.setEShopCategory(eShopProfileBody.getMarketCategory().toString());

        if ((eShop).getEShopCategory().equals(Constants.SINGLE_VENDOR)) {
            Merchant merchantInfo = new Merchant();

            //The single_vendor_user is not allowed to set the merchantIdentificationByEShop
            //The username is set for that.
            merchantInfo.setMerchantIdentificationByEShop(eShopProfileBody.getUsername());
            //The single_vendor_user is not allowed to set the merchantName and the eShopName is set for that.
            merchantInfo.setMerchantName(eShopProfileBody.getEShopName());

            merchantInfo.setMerchantCategoryCode(eShopProfileBody.getMerchantInfo().getMerchantCategoryCode());
            Bank merchantBank = BankMapping.createBankInstance(eShopProfileBody.getMerchantInfo().getMerchantBank());
            merchantInfo.setMerchantBank(merchantBank);
            BankAccount bankAccount = BankAccountMapping.createAccountInstance(
                    eShopProfileBody.getMerchantInfo().getMerchantBankAccountData());
            merchantInfo.setMerchantAccount(bankAccount);
            (eShop).setMerchant(merchantInfo);
        }
        return eShop;
    }

    /**
     * return the E-shop registration response.
     * It contains credentials for the E-shop user to request authorization tokens.
     * @param credentials
     * @return
     */
    public static EShopRegistrationResponseDTO getEShopRegistrationResponseDTO(String[] credentials) {
        EShopRegistrationResponseDTO eShopRegistrationResponseDTO = new EShopRegistrationResponseDTO();
        eShopRegistrationResponseDTO.setEShopUsername(credentials[0]);
        eShopRegistrationResponseDTO.setClientId(credentials[1]);
        eShopRegistrationResponseDTO.setClientSecret(credentials[2]);
        return  eShopRegistrationResponseDTO;

    }

    /**
     * get the DTO object for E-Shop profile.
     * @param eShop
     * @return
     */
    public static EShopProfileDTO getEShopProfileDTO(EShop eShop) {
        EShopProfileDTO eShopProfileDTO = new EShopProfileDTO();
        eShopProfileDTO.setEShopName(eShop.getEShopName());
        eShopProfileDTO.setEShopRegistrationNo(eShop.getEShopRegistrationNo());
        eShopProfileDTO.setRegisteredBussinessName(eShop.getRegisteredBusinessName());
        eShopProfileDTO.setRegisteredCountry(eShop.getRegisteredCountry());
        eShopProfileDTO.setUsername(eShop.getUsername());
        eShopProfileDTO.setEmail(eShop.getEmail());

        if (eShop.getEShopCategory().equals(Constants.SINGLE_VENDOR)) {
            eShopProfileDTO.setMarketCategory(
                    EShopProfileDTO.MarketCategoryEnum.single_vendor);
            eShopProfileDTO.setMerchantInfo(getMerchantInfoDTO(eShop));
        } else {
            eShopProfileDTO.setMarketCategory(
                    EShopProfileDTO.MarketCategoryEnum.multi_vendor);
        }
        return eShopProfileDTO;

    }

    private static MerchantInfoDTO getMerchantInfoDTO(EShop eShop) {
        MerchantInfoDTO merchantInfoDTO = new MerchantInfoDTO();
        merchantInfoDTO.setMerchantIdentificationByEShop(eShop.getUsername());
        merchantInfoDTO.setMerchantName(eShop.getMerchant().getMerchantName());
        merchantInfoDTO.setMerchantCategoryCode(eShop.getMerchant().getMerchantCategoryCode());
        merchantInfoDTO.setMerchantBank(BankMapping.createBankDTO(eShop.getMerchant().getMerchantBank()));
        BankAccountDTO bankAccountDTO = BankAccountMapping.createAccountDTO(eShop.getMerchant().getMerchantAccount());
        merchantInfoDTO.setMerchantBankAccountData(bankAccountDTO);
        return merchantInfoDTO;
    }
}
