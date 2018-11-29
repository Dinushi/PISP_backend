package com.wso2.finance.open.banking.pisp.impl;

import com.wso2.finance.open.banking.pisp.ApiResponseMessage;
import com.wso2.finance.open.banking.pisp.BankConnectionApiService;
import com.wso2.finance.open.banking.pisp.dto.BankInfoDTO;
import com.wso2.finance.open.banking.pisp.dto.DebtorBankDTO;
import com.wso2.finance.open.banking.pisp.mappings.BankMapping;
import com.wso2.finance.open.banking.pisp.models.DebtorBank;
import com.wso2.finance.open.banking.pisp.services.BankConnectionManagementService;
import com.wso2.finance.open.banking.pisp.utilities.constants.Constants;
import com.wso2.finance.open.banking.pisp.utilities.constants.ErrorMessages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

/**
 * This class holds the implementation logic behind the BankApi.
 */
public class BankConnectionApiServiceImpl extends BankConnectionApiService {

    private Log log = LogFactory.getLog(BankConnectionApiServiceImpl.class);

    @Override
    public Response addANewBank(String cookie, BankInfoDTO body) {

        BankConnectionManagementService bankConnectionManagementService = new BankConnectionManagementService();
        DebtorBank debtorBank = (DebtorBank) BankMapping.createDebtorbankInstance(body);
        if (bankConnectionManagementService.addNewBankConnection(debtorBank)) {
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, Constants.BANK_ADDED)).build();
        } else {
            return Response.serverError().entity(ErrorMessages.FAILED_TO_ADD_BANK).build();
        }
    }

    @Override
    public Response getListOfBanks() {

        BankConnectionManagementService bankConnectionManagementService = new BankConnectionManagementService();
        ArrayList<DebtorBank> listOfBanks = bankConnectionManagementService.getListOfBanks();
        List<DebtorBankDTO> listOfBankDTO = BankMapping.getListOfDebtorBankDTO(listOfBanks);
        if (listOfBankDTO.size() > 0) {
            return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                    .entity(listOfBankDTO).build();
        }
        return Response.serverError().entity(ErrorMessages.ERROR_GETTING_ACTIVE_BANKS).build();

    }

    @Override
    public Response removeBank(String bankUid, String cookie) {

        BankConnectionManagementService bankConnectionManagementService = new BankConnectionManagementService();
        if (bankConnectionManagementService.removeBank(bankUid)) {
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, Constants.BANK_REMOVED)).build();
        } else {
            return Response.serverError().entity(ErrorMessages.FAILED_TO_REMOVE_BANK).build();

        }
    }

}
