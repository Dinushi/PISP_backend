package pisp.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.*;


import pisp.dto.BankInfoDTO;
import pisp.dto.DebtorBankDTO;
import pisp.dto.BankAccountDTO;
import pisp.dto.BankSelectionResponseDTO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


import pisp.mappings.BankMapping;
import pisp.models.DebtorBank;
import pisp.services.BankConnectionManagementService;

import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;


import javax.ws.rs.core.Response;

public class BankConnectionApiServiceImpl extends BankConnectionApiService {
    private Log log = LogFactory.getLog(BankConnectionApiServiceImpl.class);

    @Override
    public Response addANewBank(String contentType,String cookie,BankInfoDTO body){
        BankConnectionManagementService bankConnectionManagementService=new BankConnectionManagementService();
        DebtorBank debtorBank=(DebtorBank) BankMapping.createDebtorbankInstance(body);
        if(bankConnectionManagementService.addNewBankConnection(debtorBank)){
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, Constants.BANK_ADDED)).build();
        }else{
            return  Response.serverError().entity(ErrorMessages.FAILED_TO_ADD_BANK).build();
        }
    }


    @Override
    public Response getListOfBanks(){

        BankConnectionManagementService bankConnectionManagementService=new BankConnectionManagementService();
        ArrayList<DebtorBank> listOfBanks=bankConnectionManagementService.getListOfBanks();
        List<DebtorBankDTO> listOfBankDTO=BankMapping.getListOfDebtorBankDTO(listOfBanks);
        if(listOfBankDTO.size()>0){
            return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                    .entity(listOfBankDTO).build();
        }
        return  Response.serverError().entity(ErrorMessages.ERROR_GETTING_ACTIVE_BANKS).build();

    }

    @Override
    public Response removeBank(String bankUid,String cookie) {
        BankConnectionManagementService bankConnectionManagementService = new BankConnectionManagementService();
        if (bankConnectionManagementService.removeBank(bankUid)) {
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, Constants.BANK_REMOVED)).build();
        } else {
            return Response.serverError().entity(ErrorMessages.FAILED_TO_REMOVE_BANK).build();

        }
    }


}
