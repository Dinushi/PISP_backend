package pisp.mappings;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.dao.UserManagementDAO;
import pisp.dto.*;
import pisp.models.*;
import pisp.services.UserManagementService;
import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;

public class PaymentInitiationRequestMapping {
    static Log log = LogFactory.getLog(PaymentInitiationRequestMapping.class);
    private static boolean isSingleVendor=false;


    /**
     * create the Payment object by validating and mapping the data in the payment initiation request
     * @param paymentInitRequestDTO
     * @param clientId
     * @param purchaseId
     * @return
     */
    public static Payment createPaymentInitiationRequestInstance(PaymentInitRequestDTO paymentInitRequestDTO, String clientId, String purchaseId){
        log.info("Creating the payment request instance");

        if (paymentInitRequestDTO == null) {
            return null;
        }
        isClientSingleVendor(clientId);
        PispInternalResponse pispInternalResponse=validatePaymentInitiationRequestBody(paymentInitRequestDTO);

        Payment paymentInitiationRequest=new Payment();
        paymentInitiationRequest.setClientId(clientId);
        paymentInitiationRequest.setPurchaseId(purchaseId);



        if(!pispInternalResponse.isOperationSuccessful()){
            paymentInitiationRequest.setErrorStatus(true);
            paymentInitiationRequest.setErrorMessage(pispInternalResponse.getMessage());
            return paymentInitiationRequest;

        }else{
            paymentInitiationRequest.setEShopUsername(paymentInitRequestDTO.getEShopUsername());
            paymentInitiationRequest.setInstructedAmountCurrency(paymentInitRequestDTO.getInstructedAmount().getCurrency());
            paymentInitiationRequest.setInstructedAmount(Integer.parseInt(paymentInitRequestDTO.getInstructedAmount().getAmount()));

            //only multi-vendor e-shop users are expected to sent merchant info at payment initiation. For Single-vendor, merchant Info are already added to the database.
            Merchant merchant=new Merchant();
            if(!isSingleVendor){
                merchant.setMerchantName(paymentInitRequestDTO.getMerchantInfo().getMerchantName());
                merchant.setMerchantCategoryCode(paymentInitRequestDTO.getMerchantInfo().getMerchantCategoryCode());
                merchant.setMerchantIdentificationByEshop(paymentInitRequestDTO.getMerchantInfo().getMerchantIdentificationByEshop());
                paymentInitiationRequest.setMerchant(merchant);

                paymentInitiationRequest.setMerchantBank(BankMapping.createBankInstance(paymentInitRequestDTO.getMerchantInfo().getMerchantBank()));
                paymentInitiationRequest.setMerchantBankAccount(BankAccountMapping.createBankAccountInstance(paymentInitRequestDTO.getMerchantInfo().getMerchantBankAccountData()));
            }else{
                merchant.setMerchantIdentificationByEshop(paymentInitRequestDTO.getEShopUsername());//Set e-shop username itself as the merchantIdentificationByEShop
                paymentInitiationRequest.setMerchant(merchant);
                paymentInitiationRequest.setMerchant(merchant);
            }

            paymentInitiationRequest.setCustomerIdentification(paymentInitRequestDTO.getCustomerIdentificationByEShop());
            paymentInitiationRequest.setItemsPurchased(PurchaseItemMapping.createPurchaseItemMapping(paymentInitRequestDTO.getItemsPurchased()));


            PaymentInitRequestDeliveryAddressDTO paymentInitRequestDeliveryAddressDTO=paymentInitRequestDTO.getDeliveryAddress();
            String deliveryAddressJason=deliveryAddressToJSON(paymentInitRequestDeliveryAddressDTO);
            paymentInitiationRequest.setDeliveryAddress(deliveryAddressJason);
            log.info("Delivery Address jason :"+ deliveryAddressJason);

            paymentInitiationRequest.setRedirectURI(paymentInitRequestDTO.getRedirectURI());


            return paymentInitiationRequest;

        }
    }

    public static String deliveryAddressToJSON(Object modelObject) {
        Gson gson = new Gson();
        return gson.toJson(modelObject);
    }

    /**
     * validate the payment initiation request body for required data and formats
     *
     * @param paymentInitRequestDTO
     * @return
     */
    private static PispInternalResponse validatePaymentInitiationRequestBody(PaymentInitRequestDTO paymentInitRequestDTO){
        log.info("Validating the payment initiation request");
        try{
            if(paymentInitRequestDTO.getInstructedAmount()==null || Integer.parseInt(paymentInitRequestDTO.getInstructedAmount().getAmount())< 0) {
                return new PispInternalResponse(ErrorMessages.ERROR_INSTRUCTED_AMOUNT_NOT_SPECIFIED, false);
            }
            else if(!isSingleVendor && paymentInitRequestDTO.getMerchantInfo().getMerchantBank()==null) {
                return new PispInternalResponse(ErrorMessages.ERROR_MERCHANT_BANK_NOT_SPECIFIED,false);
            }
            else if(!isSingleVendor && paymentInitRequestDTO.getMerchantInfo().getMerchantBankAccountData()==null) {
                return new PispInternalResponse(ErrorMessages.ERROR_MERCHANT_BANK_ACCOUNT_NOT_SPECIFIED,false);
            }
            else{
                return new PispInternalResponse(ErrorMessages.PAYMENT_DATA_VALIDATED,true);
            }
        }catch(NumberFormatException e){
            return new PispInternalResponse(ErrorMessages.ERROR_INVALID_INSTRUCTED_AMOUNT, false);
        }
        //Perform a currency validation
    }


    /**
     * verify whether merchant info is required/not based on the e-shop's registered market place category.
     * @param clientId
     */
    private static void isClientSingleVendor(String clientId){
        UserManagementDAO userManagementDAO=new UserManagementDAO();
        String marketPlaceCategory=userManagementDAO.getMarketPlaceCategoryOfEshopUser(clientId);
        if(marketPlaceCategory.equals(Constants.SINGLE_VENDOR)){
            isSingleVendor = true;
        }
    }


    /**
     * create the paymentInitiation ResponseDTO
     * @param paymentData
     * @return
     */
    public static PaymentInitResponseDTO getPaymentInitiationResponseDTO(Payment paymentData){

        PaymentInitResponseDTO paymentInitResponseDTO=new PaymentInitResponseDTO();
        paymentInitResponseDTO.setPaymentInitReqId(paymentData.getPaymentInitReqId());
        paymentInitResponseDTO.setMerchantName(paymentData.getMerchant().getMerchantName());
        paymentInitResponseDTO.setMerchantBank(BankMapping.createBankDTO(paymentData.getMerchantBank()));
        paymentInitResponseDTO.setMerchantBankAccountData(BankAccountMapping.createBankAccountDTO(paymentData.getMerchantBankAccount()));
        paymentInitResponseDTO.setInstructedAmount(PaymentInitiationRequestMapping.getInstructedAmountDTO(paymentData.getInstructedAmount(),paymentData.getInstructedAmountCurrency()));
        paymentInitResponseDTO.setRedirectLink(paymentData.getRedirectURI());
        paymentInitResponseDTO.setPaymentStatus(PaymentInitResponseDTO.PaymentStatusEnum.Received);

        return  paymentInitResponseDTO;
    }

    private static PaymentHistoryInnerInstructedAmountDTO getInstructedAmountDTO(float instructedAmount, String currency){
        PaymentHistoryInnerInstructedAmountDTO paymentInitRequestInstructedAmountDTO=new PaymentHistoryInnerInstructedAmountDTO();
        paymentInitRequestInstructedAmountDTO.setCurrency(currency);
        paymentInitRequestInstructedAmountDTO.setAmount(String.valueOf(instructedAmount));
        return paymentInitRequestInstructedAmountDTO;
    }


}
