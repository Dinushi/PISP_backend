package pisp;

import pisp.*;
import pisp.dto.*;

import pisp.dto.PaymentHistoryDTO;

import java.util.List;

import java.io.InputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.ws.rs.core.Response;

public abstract class PaymentHistoryApiService {
    public abstract Response getPaymentReports(String username,String filter,String cookie,String startDate,String endDate);
}

