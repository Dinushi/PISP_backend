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

package pisp.services;

import pisp.dao.BankManagementDAO;
import pisp.models.DebtorBank;

import java.util.ArrayList;

public class BankConnectionManagementService {
    BankManagementDAO bankManagementDAO;

    public BankConnectionManagementService(){
        this.bankManagementDAO=new BankManagementDAO();

    }

    /**
     * add a new bank connection request to th database
     * @param bank
     * @return
     */
    public boolean addNewBankConnection(DebtorBank bank){
        return bankManagementDAO.addNewBankConnection(bank);
    }

    /**
     * returen the active list of banks supported by PISP
     * @return
     */
    public ArrayList getListOfBanks(){
        return bankManagementDAO.getDebtorBankList();

    }

    /**
     * Remove a bank from supported list of banks by PISP
     * @param bankUid
     * @return
     */
    public  boolean removeBank(String bankUid){
        return bankManagementDAO.removeActiveBank(bankUid);

    }
}
