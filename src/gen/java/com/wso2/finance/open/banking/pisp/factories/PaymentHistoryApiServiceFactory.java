package com.wso2.finance.open.banking.pisp.factories;

import com.wso2.finance.open.banking.pisp.PaymentHistoryApiService;
import com.wso2.finance.open.banking.pisp.impl.PaymentHistoryApiServiceImpl;

public class PaymentHistoryApiServiceFactory {

   private final static PaymentHistoryApiService service = new PaymentHistoryApiServiceImpl();

   public static PaymentHistoryApiService getPaymentHistoryApi()
   {
      return service;
   }
}
