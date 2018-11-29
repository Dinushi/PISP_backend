package com.wso2.finance.open.banking.pisp.mappings;

import com.google.gson.Gson;
import com.wso2.finance.open.banking.pisp.dao.UserManagementDAO;
import com.wso2.finance.open.banking.pisp.dto.PaymentHistoryInnerInstructedAmountDTO;
import com.wso2.finance.open.banking.pisp.dto.PaymentInitRequestDTO;
import com.wso2.finance.open.banking.pisp.dto.PaymentInitRequestDeliveryAddressDTO;
import com.wso2.finance.open.banking.pisp.dto.PaymentInitResponseDTO;
import com.wso2.finance.open.banking.pisp.models.InternalResponse;
import com.wso2.finance.open.banking.pisp.models.Merchant;
import com.wso2.finance.open.banking.pisp.models.Payment;
import com.wso2.finance.open.banking.pisp.utilities.ConfigFileReader;
import com.wso2.finance.open.banking.pisp.utilities.constants.Constants;
import com.wso2.finance.open.banking.pisp.utilities.constants.ErrorMessages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * This class maps Payment related DTOs with internal models.
 */
public class PaymentInitiationRequestMapping {

    static Log log = LogFactory.getLog(PaymentInitiationRequestMapping.class);
    private static boolean isSingleVendor = false;

    /**
     * create the Payment object by validating and mapping the data in the payment initiation request.
     *
     * @param paymentInitRequestDTO
     * @param clientId
     * @param purchaseId
     * @return
     */
    public static Payment createPaymentInitiationRequestInstance(PaymentInitRequestDTO paymentInitRequestDTO,
                                                                 String clientId, String purchaseId) {

        log.info("Creating the payment request instance");
        if (paymentInitRequestDTO == null) {
            return null;
        }
        isClientSingleVendor(clientId);
        InternalResponse internalResponse = validatePaymentInitiationRequestBody(paymentInitRequestDTO);

        Payment paymentInitiationRequest = new Payment();
        paymentInitiationRequest.setClientId(clientId);
        paymentInitiationRequest.setPurchaseId(purchaseId);

        if (!internalResponse.isOperationSuccessful()) {
            paymentInitiationRequest.setErrorStatus(true);
            paymentInitiationRequest.setErrorMessage(internalResponse.getMessage());
            return paymentInitiationRequest;
        } else {
            paymentInitiationRequest.setEShopUsername(paymentInitRequestDTO.getEShopUsername());
            paymentInitiationRequest.setInstructedAmountCurrency(paymentInitRequestDTO.
                    getInstructedAmount().getCurrency());
            paymentInitiationRequest.setInstructedAmount(Integer.parseInt(paymentInitRequestDTO.
                    getInstructedAmount().getAmount()));

            //only multi-vendor e-shop users are expected to sent merchant info at payment initiation.
            //For Single-vendor, merchant Info are already added to the database.
            Merchant merchant = new Merchant();
            if (!isSingleVendor) {
                merchant.setMerchantName(paymentInitRequestDTO.getMerchantInfo().getMerchantName());
                merchant.setMerchantCategoryCode(paymentInitRequestDTO.getMerchantInfo().getMerchantCategoryCode());
                merchant.setMerchantIdentificationByEShop(paymentInitRequestDTO.
                        getMerchantInfo().getMerchantIdentificationByEShop());

                merchant.setMerchantBank(BankMapping.createBankInstance(paymentInitRequestDTO.
                        getMerchantInfo().getMerchantBank()));
                merchant.setMerchantAccount(BankAccountMapping.createAccountInstance(paymentInitRequestDTO.
                        getMerchantInfo().getMerchantBankAccountData()));
                paymentInitiationRequest.setMerchant(merchant);
            } else {
                //Set e-shop username itself as the merchantIdentificationByEShop.
                merchant.setMerchantIdentificationByEShop(paymentInitRequestDTO.getEShopUsername());
                paymentInitiationRequest.setMerchant(merchant);
                paymentInitiationRequest.setMerchant(merchant);
            }

            paymentInitiationRequest.setCustomerIdentification(paymentInitRequestDTO.
                    getCustomerIdentificationByEShop());
            paymentInitiationRequest.setItemsPurchased(PurchaseItemMapping.
                    createPurchaseItemMapping(paymentInitRequestDTO.getItemsPurchased()));

            PaymentInitRequestDeliveryAddressDTO paymentInitRequestDeliveryAddressDTO = paymentInitRequestDTO.
                    getDeliveryAddress();
            String deliveryAddressJason = deliveryAddressToJSON(paymentInitRequestDeliveryAddressDTO);
            paymentInitiationRequest.setDeliveryAddress(deliveryAddressJason);
            paymentInitiationRequest.setRedirectURI(paymentInitRequestDTO.getRedirectURI());
            return paymentInitiationRequest;
        }
    }

    public static String deliveryAddressToJSON(Object modelObject) {

        Gson gson = new Gson();
        return gson.toJson(modelObject);
    }

    /**
     * validate the payment initiation request body for required data and formats.
     *
     * @param paymentInitRequestDTO
     * @return
     */
    private static InternalResponse validatePaymentInitiationRequestBody
                                                    (PaymentInitRequestDTO paymentInitRequestDTO) {

        log.info("Validating the payment initiation request");
        try {
            if (paymentInitRequestDTO.getInstructedAmount() == null ||
                    Float.parseFloat(paymentInitRequestDTO.getInstructedAmount().getAmount()) < 0) {
                return new InternalResponse(ErrorMessages.ERROR_INSTRUCTED_AMOUNT_NOT_SPECIFIED, false);
            } else if (!isSingleVendor && paymentInitRequestDTO.getMerchantInfo().getMerchantBank() == null) {
                return new InternalResponse(ErrorMessages.ERROR_MERCHANT_BANK_NOT_SPECIFIED, false);
            } else if (!isSingleVendor &&
                    paymentInitRequestDTO.getMerchantInfo().getMerchantBankAccountData() == null) {
                return new InternalResponse(ErrorMessages.ERROR_MERCHANT_BANK_ACCOUNT_NOT_SPECIFIED, false);
            } else {
                return new InternalResponse(ErrorMessages.PAYMENT_DATA_VALIDATED, true);
            }
        } catch (NumberFormatException e) {
            return new InternalResponse(ErrorMessages.ERROR_INVALID_INSTRUCTED_AMOUNT, false);
        }
    }

    /**
     * verify whether merchant info is required/not based on the e-shop's registered eShop Category.
     *
     * @param clientId
     */
    private static void isClientSingleVendor(String clientId) {

        UserManagementDAO userManagementDAO = new UserManagementDAO();
        String marketPlaceCategory = userManagementDAO.
                getMarketPlaceCategoryOfEshopUser(clientId);
        if (marketPlaceCategory.equals(Constants.SINGLE_VENDOR)) {
            isSingleVendor = true;
        }
    }

    /**
     * create the paymentInitiation ResponseDTO.
     *
     * @param paymentData  The payment data object.
     * @param isCompleted  Specifies whether the payment has completed.
     * @param isSuccessful Specifies whether the payment is success/failed.
     * @return
     */
    public static PaymentInitResponseDTO getPaymentInitiationResponseDTO(
            Payment paymentData, boolean isCompleted, boolean isSuccessful) {

        PaymentInitResponseDTO paymentInitResponseDTO = new PaymentInitResponseDTO();
        paymentInitResponseDTO.setPaymentInitReqId(paymentData.getPaymentInitReqId());
        paymentInitResponseDTO.setMerchantName(paymentData.getMerchant().getMerchantName());
        paymentInitResponseDTO.setMerchantCategoryCodeOfMerchant(paymentData.getMerchant().getMerchantCategoryCode());
        paymentInitResponseDTO.setMerchantBank(BankMapping.createBankDTO(paymentData.getMerchant().getMerchantBank()));
        paymentInitResponseDTO.setMerchantBankAccountData(BankAccountMapping.
                createAccountDTO(paymentData.getMerchant().getMerchantAccount()));
        paymentInitResponseDTO.setInstructedAmount(PaymentInitiationRequestMapping.
                getInstructedAmountDTO(paymentData.getInstructedAmount(), paymentData.getInstructedAmountCurrency()));

        if (!isCompleted) {
            if (isSuccessful) {
                paymentInitResponseDTO.setPaymentStatus(PaymentInitResponseDTO.PaymentStatusEnum.Received);

                //For the response for paymentInitiation only, the redirect URL is set with link to PISP-PSU login page.
                ConfigFileReader configFileReader = new ConfigFileReader();
                paymentInitResponseDTO.setRedirectLink(configFileReader.readFrontendDeploymentURL()
                        + paymentData.getPaymentInitReqId());
            } else {
                paymentInitResponseDTO.setPaymentStatus(PaymentInitResponseDTO.PaymentStatusEnum.Failed);

                //The redirect URL is set with link to redirect the PSU back to his e-commerce site.
                paymentInitResponseDTO.setRedirectLink(paymentData.getRedirectURI() + "#" +
                        Constants.PAYMENT_FAILURE_CODE);
            }
        } else {
            paymentInitResponseDTO.setCustomerBankUid(paymentData.getCustomerBank().getBankUid());
            if (paymentData.getCustomerBankAccount() != null) {
                paymentInitResponseDTO.setCustomerAccount(BankAccountMapping.
                        createAccountDTO(paymentData.getCustomerBankAccount()));
            }
            paymentInitResponseDTO.setPaymentStatus(PaymentInitResponseDTO.PaymentStatusEnum.Completed);

            //The redirect URL is set with link to redirect the PSU back to his e-commerce site.
            paymentInitResponseDTO.setRedirectLink(paymentData.getRedirectURI() + "#" +
                    Constants.PAYMENT_COMPLETION_CODE);
        }
        return paymentInitResponseDTO;
    }

    private static PaymentHistoryInnerInstructedAmountDTO getInstructedAmountDTO(float instructedAmount,
                                                                                 String currency) {

        PaymentHistoryInnerInstructedAmountDTO paymentInitRequestInstructedAmountDTO = new
                PaymentHistoryInnerInstructedAmountDTO();
        paymentInitRequestInstructedAmountDTO.setCurrency(currency);
        paymentInitRequestInstructedAmountDTO.setAmount(String.valueOf(instructedAmount));
        return paymentInitRequestInstructedAmountDTO;
    }
}
