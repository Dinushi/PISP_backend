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

import com.wso2.finance.open.banking.pisp.dto.BankAccountDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wso2.finance.open.banking.pisp.models.BankAccount;


/**
 * This class is to map BankAccountDTOs with internal models.
 */
public class BankAccountMapping {

    static Log log = LogFactory.getLog(BankAccountMapping.class);

    /**
     * create a BankAccount instance for BankAccountDTO.
     * @param bankData
     * @return a instance of bankAccount.
     */
    public static BankAccount createAccountInstance(BankAccountDTO bankData) {

        if (bankData.getIdentification() == null || bankData.getAccountOwnerName() == null) {
            log.info("PSU has skip the bank account submission");
            return null;
        }
        BankAccount bankAccount = new BankAccount();
        bankAccount.setSchemeName(bankData.getSchemeName().toString());
        bankAccount.setIdentification(bankData.getIdentification());
        bankAccount.setAccountOwnerName(bankData.getAccountOwnerName());
        return bankAccount;
    }

    /**
     * create BankAccountDTO for BankAccount instance.
     * @param creditorAccount
     * @return a DTO instance of a Bank Account.
     */
    public static BankAccountDTO createAccountDTO(BankAccount creditorAccount) {
        BankAccountDTO bankAccountDTO = new BankAccountDTO();
        BankAccountDTO.SchemeNameEnum schemeName = BankAccountDTO.SchemeNameEnum.
                valueOf(creditorAccount.getSchemeName());
        bankAccountDTO.setSchemeName(schemeName);
        bankAccountDTO.setIdentification(creditorAccount.getIdentification());
        bankAccountDTO.setAccountOwnerName(creditorAccount.getAccountOwnerName());
        return bankAccountDTO;
    }
}
