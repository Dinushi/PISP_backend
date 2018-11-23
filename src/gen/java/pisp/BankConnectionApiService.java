package pisp;

import pisp.*;
import pisp.dto.*;

import pisp.dto.BankInfoDTO;
import pisp.dto.DebtorBankDTO;
import pisp.dto.BankAccountDTO;
import pisp.dto.BankSelectionResponseDTO;

import java.util.List;

import java.io.InputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

public abstract class BankConnectionApiService {
    public abstract Response addANewBank(String contentType,String cookie,BankInfoDTO body);
    public abstract Response getListOfBanks();
    public abstract Response removeBank(String bankUid,String cookie);
}

