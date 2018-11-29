package com.wso2.finance.open.banking.pisp.factories;

import com.wso2.finance.open.banking.pisp.PaymentInitiationApiService;
import com.wso2.finance.open.banking.pisp.impl.PaymentInitiationApiServiceImpl;

public class PaymentInitiationApiServiceFactory {

   private final static PaymentInitiationApiService service = new PaymentInitiationApiServiceImpl();

   public static PaymentInitiationApiService getPaymentInitiationApi()
   {
      return service;
   }
}
