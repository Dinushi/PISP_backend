package pisp.impl;

import pisp.*;
import pisp.dto.*;


import pisp.dto.PaymentHistoryDTO;

import java.util.List;

import java.io.InputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.ws.rs.core.Response;

public class PaymentHistoryApiServiceImpl extends PaymentHistoryApiService {
    @Override
    public Response getPaymentReports(String username,String filter,String cookie,String startDate,String endDate){
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
