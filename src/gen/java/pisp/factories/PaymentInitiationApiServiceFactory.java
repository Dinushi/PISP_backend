package pisp.factories;

import pisp.PaymentInitiationApiService;
import pisp.impl.PaymentInitiationApiServiceImpl;

public class PaymentInitiationApiServiceFactory {

   private final static PaymentInitiationApiService service = new PaymentInitiationApiServiceImpl();

   public static PaymentInitiationApiService getPaymentInitiationApi()
   {
      return service;
   }
}
