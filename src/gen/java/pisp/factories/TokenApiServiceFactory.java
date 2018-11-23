package pisp.factories;


import pisp.TokenApiService;
import pisp.impl.TokenApiServiceImpl;

public class TokenApiServiceFactory {

   private final static TokenApiService service = new TokenApiServiceImpl();

   public static TokenApiService getTokenApi()
   {
      return service;
   }
}
