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

import com.wso2.finance.open.banking.pisp.dto.InstructedAmountDTO;
import com.wso2.finance.open.banking.pisp.dto.PaymentHistoryBankAccountDTO;
import com.wso2.finance.open.banking.pisp.dto.PaymentHistoryInnerDTO;
import com.wso2.finance.open.banking.pisp.models.Bank;
import com.wso2.finance.open.banking.pisp.models.BankAccount;
import com.wso2.finance.open.banking.pisp.models.Payment;

/**
 * This class is to create DTO objects that needed to transfer payment history data.
 */
public class PaymentHistoryMapping {

    /**
     * create the DTO object of payment history data.
     *
     * @param payment
     */
    public static PaymentHistoryInnerDTO createPaymentHistoryInnerDTO(Payment payment) {

        PaymentHistoryInnerDTO paymentHistoryInnerDTO = new PaymentHistoryInnerDTO();
        paymentHistoryInnerDTO.setInstructedAmount(createInstructedAmountDTO(
                payment.getInstructedAmount(), payment.getInstructedAmountCurrency()));
        paymentHistoryInnerDTO.setCustomerIdentificationNo(payment.getCustomerIdentification());
        paymentHistoryInnerDTO.setCustomerName(payment.getPsuUsername());
        paymentHistoryInnerDTO.setCustomerAccountData(createBankAccountDTO(payment.getMerchant().getMerchantBank(),
                payment.getMerchant().getMerchantAccount()));
        paymentHistoryInnerDTO.setMerchantAccountData(createBankAccountDTO(payment.getCustomerBank(),
                payment.getCustomerBankAccount()));
        paymentHistoryInnerDTO.setMerchantName(payment.getMerchant().getMerchantName());
        paymentHistoryInnerDTO.setMerchantIdentificationNo(payment.getMerchant().
                getMerchantIdentificationByEShop());

        return paymentHistoryInnerDTO;
    }

    private static PaymentHistoryBankAccountDTO createBankAccountDTO(Bank bank, BankAccount bankAccount) {

        PaymentHistoryBankAccountDTO paymentHistoryBankAccountDTO = new PaymentHistoryBankAccountDTO();
        paymentHistoryBankAccountDTO.setBank(bank.getBankName());
        paymentHistoryBankAccountDTO.setAccountNo(bankAccount.getIdentification());
        paymentHistoryBankAccountDTO.setAccountOwnerName(bankAccount.getAccountOwnerName());
        return paymentHistoryBankAccountDTO;
    }

    private static InstructedAmountDTO createInstructedAmountDTO(float amount, String currency) {

        InstructedAmountDTO instructedAmountDTO = new InstructedAmountDTO();
        instructedAmountDTO.setAmount(String.valueOf(amount));
        instructedAmountDTO.setCurrency(currency);
        return instructedAmountDTO;
    }
}
