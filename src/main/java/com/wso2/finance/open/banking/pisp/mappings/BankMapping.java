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

package com.wso2.finance.open.banking.pisp.mappings;

import com.wso2.finance.open.banking.pisp.dto.BankDTO;
import com.wso2.finance.open.banking.pisp.dto.BankInfoDTO;
import com.wso2.finance.open.banking.pisp.dto.BankSelectionResponseDTO;
import com.wso2.finance.open.banking.pisp.dto.DebtorBankDTO;
import com.wso2.finance.open.banking.pisp.models.InternalResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wso2.finance.open.banking.pisp.models.Bank;
import com.wso2.finance.open.banking.pisp.models.DebtorBank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is to map Bank related DTOs with internal models.
 */
public class BankMapping {

    private static Log log = LogFactory.getLog(BankMapping.class);

    /**
     * create the Bank instance for a BankDTO.
     * @param bankData
     * @return a instance of bank.
     */
    public static Bank createBankInstance(BankDTO bankData) {
        if (bankData == null) {
            return null;
        }
        Bank bank = new Bank();
        bank.setSchemeName(bankData.getSchemeName().toString());
        bank.setIdentification(bankData.getIdentification());
        bank.setBankName(bankData.getBankName());
        return bank;
    }

    /**
     * create a inherited instance of bank as debtor bank which holds banks that supported by PISP.
     * @param bankInfo
     * @return
     */
    public static Bank createDebtorbankInstance(BankInfoDTO bankInfo) {
        if (bankInfo == null) {
            return null;
        }
        Bank bank = new DebtorBank();
        bank.setBankName(bankInfo.getBank().getBankName());
        bank.setSchemeName(bankInfo.getBank().getSchemeName().toString());
        bank.setIdentification(bankInfo.getBank().getIdentification());
        ((DebtorBank) bank).setClientId(bankInfo.getClientId());
        ((DebtorBank) bank).setSpecForOB(bankInfo.getSpecForOB().toString());
        return bank;
    }

    /**
     * generate the bankDTO to create response to the client.
     * @param creditorBank
     * @return
     */
    public static BankDTO createBankDTO(Bank creditorBank) {
        BankDTO bankDTO = new BankDTO();
        BankDTO.SchemeNameEnum schemeName = BankDTO.SchemeNameEnum.valueOf(creditorBank.getSchemeName());
        bankDTO.setSchemeName(schemeName);
        bankDTO.setIdentification(creditorBank.getIdentification());
        bankDTO.setBankName(creditorBank.getBankName());
        return  bankDTO;
    }

    /**
     * return the response which contains whether bank account is mandated by spec followed by selected debtor bank.
     * @param internalResponse
     * @return
     */
    public static BankSelectionResponseDTO getBankSelectionResponseDTO(InternalResponse internalResponse) {
        BankSelectionResponseDTO bankSelectionResponseDTO = new BankSelectionResponseDTO();
        Boolean[] result = (Boolean[]) internalResponse.getData();
        log.info("IsDebtorAccountRequired :" + result[0]);
        bankSelectionResponseDTO.setAccountRequired(result[0]);
        bankSelectionResponseDTO.setSubmissionRequired(result[1]);
        return bankSelectionResponseDTO;
    }

    /**
     * return the arrayList of DTO objects which is created for the List of Banks supported by the PISP.
     * @param listOfBanks
     * @return
     */
    public static List<DebtorBankDTO> getListOfDebtorBankDTO(ArrayList<DebtorBank> listOfBanks) {
        List<DebtorBankDTO> bankList = new ArrayList<DebtorBankDTO>();

        Iterator<DebtorBank> bankListIterator = listOfBanks.iterator();
        while (bankListIterator.hasNext()) {
            DebtorBankDTO debtorBankDTO = new DebtorBankDTO();
            DebtorBank debtorBank = bankListIterator.next();
            debtorBankDTO.setBankUid(debtorBank.getBankUid());
            debtorBankDTO.setBankName(debtorBank.getBankName());
            bankList.add(debtorBankDTO);
        }
        return bankList;
    }
}
