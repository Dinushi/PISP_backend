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

package pisp.mappings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.models.BankAccount;
import pisp.dto.BankAccountDTO;

public class BankAccountMapping {

    static Log log = LogFactory.getLog(BankAccountMapping.class);


    /**
     *
     * @param bankData
     * @return a instance of bankAccount
     */
    public static BankAccount createBankAccountInstance(BankAccountDTO bankData){

        if (bankData == null || bankData.getIdentification()==null || bankData.getAccountOwnerName()==null) {
            log.info("PSU has skip the bank account submission");
            return null;
        }

        BankAccount bankAccount=new BankAccount();

        bankAccount.setSchemeName(bankData.getSchemeName().toString());
        bankAccount.setIdentification(bankData.getIdentification());
        bankAccount.setAccountOwnerName(bankData.getAccountOwnerName());
        return bankAccount ;
    }


    /**
     *
     * @param creditorAccount
     * @return a DTO instance of a Bank Account
     */
    public static BankAccountDTO createBankAccountDTO(BankAccount creditorAccount){
        BankAccountDTO bankAccountDTO=new BankAccountDTO();
        BankAccountDTO.SchemeNameEnum schemeName= BankAccountDTO.SchemeNameEnum.valueOf(creditorAccount.getSchemeName());
        bankAccountDTO.setSchemeName(schemeName);
        bankAccountDTO.setIdentification(creditorAccount.getIdentification());
        bankAccountDTO.setAccountOwnerName(creditorAccount.getAccountOwnerName());
        return bankAccountDTO;
    }
}
