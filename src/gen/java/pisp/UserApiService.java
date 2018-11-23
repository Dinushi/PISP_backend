package pisp;

import pisp.*;
import pisp.dto.*;

import pisp.dto.EShopRegistrationResponseDTO;
import pisp.dto.EShopProfileDTO;
import pisp.dto.LoginCredentialsDTO;
import pisp.dto.AdminUserDTO;

import java.util.List;

import java.io.InputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

public abstract class UserApiService {
    public abstract Response addNewEshop(String contentType,EShopProfileDTO body);
    public abstract Response deleteEshop(String username,String cookie);
    public abstract Response eshopLogin(String contentType, HttpServletRequest request,LoginCredentialsDTO body);
    public abstract Response getEshopProfile(String username,String cookie, HttpServletRequest request);
    public abstract Response loginAdminUser(String contentType,LoginCredentialsDTO body);
    public abstract Response updateEshopProfile(String username,String contentType, HttpServletRequest request,String cookie,EShopProfileDTO body);
    public abstract Response addNewPSU(String contentType,PSUProfileDTO body);
    public abstract Response loginPSU(String contentType,String paymentInitReqId,HttpServletRequest request,LoginCredentialsDTO body);
}

