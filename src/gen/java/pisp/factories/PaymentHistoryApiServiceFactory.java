package pisp.factories;

import pisp.PaymentHistoryApiService;
import pisp.impl.PaymentHistoryApiServiceImpl;

public class PaymentHistoryApiServiceFactory {

   private final static PaymentHistoryApiService service = new PaymentHistoryApiServiceImpl();

   public static PaymentHistoryApiService getPaymentHistoryApi()
   {
      return service;
   }
}
