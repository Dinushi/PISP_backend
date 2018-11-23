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
package pisp.mappings;

import pisp.dto.EShopProfileDTO;
import pisp.dto.EShopRegistrationResponseDTO;
import pisp.dto.MerchantInfoDTO;
import pisp.dto.PSUProfileDTO;
import pisp.models.*;
import pisp.utilities.constants.Constants;

public class UserMapping {

    /**
     * generate new PSU instance
     * @param psuProfilBody
     * @return
     */
    public static PSU createPSUInstance(PSUProfileDTO psuProfilBody){
        if (psuProfilBody == null) {
            return null;
        }
        PSU psu=new PSU();
        psu.setFirstName(psuProfilBody.getFirstName());
        psu.setLastName(psuProfilBody.getLastName());
        psu.setUsername(psuProfilBody.getUsername());
        psu.setPassword(psuProfilBody.getPassword());
        psu.setEmail(psuProfilBody.getEmail());

        return psu;
    }


    /**
     *
     * @param eshopProfileBody
     * @return E_shop instance which is mapped to the request body of EShopProfileDTO
     */
    public static E_shop createEshopInstance(EShopProfileDTO eshopProfileBody){
        if (eshopProfileBody == null) {
            return null;
        }

        E_shop e_shop=new E_shop(eshopProfileBody.getEcommerceMarketplaceCategory().toString());
        e_shop.setEShopName(eshopProfileBody.getEShopName());
        e_shop.setUsername(eshopProfileBody.getUsername());
        e_shop.setEShopRegistrationNo(eshopProfileBody.getEShopRegistrationNo());
        e_shop.setRegisteredBusinessName(eshopProfileBody.getRegisteredBussinessName());
        e_shop.setRegisteredCountry(eshopProfileBody.getRegisteredCountry());

        e_shop.setEmail(eshopProfileBody.getEmail());
        e_shop.setPassword(eshopProfileBody.getPassword());

        e_shop.setEcommerceMarketplaceCategory(eshopProfileBody.getEcommerceMarketplaceCategory().toString());


        if((e_shop).getEcommerceMarketplaceCategory().equals(Constants.SINGLE_VENDOR)){
            Merchant merchantInfo =new Merchant();

            //Here, the user is not supposed to submit values,the e-shop name and username itself is set as values for following 2 fields.
            merchantInfo.setMerchantIdentificationByEshop(eshopProfileBody.getUsername());
            merchantInfo.setMerchantName(eshopProfileBody.getEShopName());

            merchantInfo.setMerchantCategoryCode(eshopProfileBody.getMerchantInfo().getMerchantCategoryCode());
            merchantInfo.setMerchantBank(BankMapping.createBankInstance(eshopProfileBody.getMerchantInfo().getMerchantBank()));
            merchantInfo.setMerchantAccount(BankAccountMapping.createBankAccountInstance(eshopProfileBody.getMerchantInfo().getMerchantBankAccountData()));
            ( e_shop).setMerchant(merchantInfo);

        }

        return e_shop;
    }

    /**
     * return the E-shop registration response which contains credentials for the E-shop user to request authorization tokens
     * @param credentials
     * @return
     */
    public static EShopRegistrationResponseDTO getEShopRegistrationResponseDTO(String[] credentials){
        EShopRegistrationResponseDTO eShopRegistrationResponseDTO=new EShopRegistrationResponseDTO();
        eShopRegistrationResponseDTO.setEShopUsername(credentials[0]);
        eShopRegistrationResponseDTO.setCliendId(credentials[1]);
        eShopRegistrationResponseDTO.setClientSecreat(credentials[2]);
        return  eShopRegistrationResponseDTO;

    }

    /**
     * get the DTO object for E-Shop profile
     * @param e_shop
     * @return
     */
    public static EShopProfileDTO getEShopProfileDTO(E_shop e_shop){
        EShopProfileDTO eShopProfileDTO=new EShopProfileDTO();
        eShopProfileDTO.setEShopName(e_shop.getEShopName());
        eShopProfileDTO.setEShopRegistrationNo(e_shop.getEShopRegistrationNo());
        eShopProfileDTO.setRegisteredBussinessName(e_shop.getRegisteredBusinessName());
        eShopProfileDTO.setRegisteredCountry(e_shop.getRegisteredCountry());
        eShopProfileDTO.setUsername(e_shop.getUsername());
        eShopProfileDTO.setEmail(e_shop.getEmail());

        if(e_shop.getEcommerceMarketplaceCategory().equals(Constants.SINGLE_VENDOR)){
            eShopProfileDTO.setEcommerceMarketplaceCategory(EShopProfileDTO.EcommerceMarketplaceCategoryEnum.single_vendor);
            eShopProfileDTO.setMerchantInfo(getMerchantInfoDTO(e_shop));
        }else{
            eShopProfileDTO.setEcommerceMarketplaceCategory(EShopProfileDTO.EcommerceMarketplaceCategoryEnum.multi_vendor);
        }
        return eShopProfileDTO;

    }
    private static MerchantInfoDTO getMerchantInfoDTO(E_shop e_shop){
        MerchantInfoDTO merchantInfoDTO = new MerchantInfoDTO();
        merchantInfoDTO.setMerchantIdentificationByEshop(e_shop.getUsername());
        merchantInfoDTO.setMerchantName(e_shop.getMerchant().getMerchantName());
        merchantInfoDTO.setMerchantCategoryCode(e_shop.getMerchant().getMerchantCategoryCode());
        merchantInfoDTO.setMerchantBank(BankMapping.createBankDTO(e_shop.getMerchant().getMerchantBank()));
        merchantInfoDTO.setMerchantBankAccountData(BankAccountMapping.createBankAccountDTO(e_shop.getMerchant().getMerchantAccount()));
        return merchantInfoDTO;
    }

}

