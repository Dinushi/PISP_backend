package pisp;

import pisp.*;
import pisp.dto.*;

import pisp.dto.PaymentInitRequestDTO;

import java.util.List;

import java.io.InputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

public abstract class PaymentInitiationApiService {
    public abstract Response getPaymentInitRequestById(String contentType,HttpServletRequest request,String username);
    public abstract Response makePaymentInitiationRequest(String contentType,String clientId,String purchaseId,String authorization,PaymentInitRequestDTO body);
    public abstract Response selectDebtorBank(String contentType,HttpServletRequest request,String username, DebtorBankDTO body);
    public abstract Response selectDebtorAccount(String contentType,HttpServletRequest request,String username,BankAccountDTO body);
    public abstract Response addAuthorizationCodeGrant(String contentType, String username,HttpServletRequest request, AuthCodeDTO body);

}

