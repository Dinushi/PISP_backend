package com.wso2.finance.open.banking.pisp.impl;

import com.wso2.finance.open.banking.pisp.ApiResponseMessage;
import com.wso2.finance.open.banking.pisp.PaymentHistoryApiService;
import com.wso2.finance.open.banking.pisp.dto.PaymentHistoryDTO;
import com.wso2.finance.open.banking.pisp.dto.PaymentHistoryInnerDTO;
import com.wso2.finance.open.banking.pisp.mappings.PaymentHistoryMapping;
import com.wso2.finance.open.banking.pisp.models.InternalResponse;
import com.wso2.finance.open.banking.pisp.models.Payment;
import com.wso2.finance.open.banking.pisp.services.PaymentManagementService;
import com.wso2.finance.open.banking.pisp.utilities.SessionManager;
import com.wso2.finance.open.banking.pisp.utilities.constants.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

/**
 * This class is to handle payment history retrieval requests by a E-shop.
 */
public class PaymentHistoryApiServiceImpl extends PaymentHistoryApiService {

    @Override
    public Response getPaymentReports(String username, String filter, HttpServletRequest request, String startDate, String endDate) {

        HttpSession session = request.getSession(true);
        String sessionToken = (String) session.getAttribute(Constants.SESSION_ID);

        InternalResponse response = SessionManager.validateSessionTokenOfEShop(username, sessionToken);
        if (response.isOperationSuccessful()) {
            PaymentManagementService paymentManagementService = new PaymentManagementService();
            ArrayList<Payment> paymentsList = paymentManagementService.retrievePaymentHistory(username, startDate, endDate);
            PaymentHistoryDTO paymentHistoryDTO = createPaymentHistoryDTO(paymentsList);
            return Response.ok().header(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE)
                    .entity(paymentHistoryDTO).build();
        } else {
            return Response.serverError()
                    .entity(new ApiResponseMessage(ApiResponseMessage.ERROR, response.getMessage())).build();
        }
    }

    private PaymentHistoryDTO createPaymentHistoryDTO(ArrayList<Payment> paymentsList) {

        PaymentHistoryDTO paymentHistoryDTO = new PaymentHistoryDTO();
        Iterator<Payment> paymentsIterator = paymentsList.iterator();
        while (paymentsIterator.hasNext()) {
            PaymentHistoryInnerDTO paymentHistoryInnerDTO = PaymentHistoryMapping.createPaymentHistoryInnerDTO(paymentsIterator.next());
            paymentHistoryDTO.add(paymentHistoryInnerDTO);
        }
        return paymentHistoryDTO;
    }
}