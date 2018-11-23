package pisp.factories;

import pisp.BankConnectionApiService;
import pisp.impl.BankConnectionApiServiceImpl;

public class BankConnectionApiServiceFactory {

   private final static BankConnectionApiService service = new BankConnectionApiServiceImpl();

   public static BankConnectionApiService getBankConnectionApi()
   {
      return service;
   }
}
