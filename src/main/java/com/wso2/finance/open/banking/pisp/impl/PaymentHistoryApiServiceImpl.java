package com.wso2.finance.open.banking.pisp.impl;

import com.wso2.finance.open.banking.pisp.ApiResponseMessage;
import com.wso2.finance.open.banking.pisp.PaymentHistoryApiService;
import javax.ws.rs.core.Response;

public class PaymentHistoryApiServiceImpl extends PaymentHistoryApiService {
    @Override
    public Response getPaymentReports(String username,String filter,String cookie,String startDate,String endDate){
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
