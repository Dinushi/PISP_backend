package com.wso2.finance.open.banking.pisp.factories;

import com.wso2.finance.open.banking.pisp.UserApiService;
import com.wso2.finance.open.banking.pisp.impl.UserApiServiceImpl;

public class UserApiServiceFactory {

   private final static UserApiService service = new UserApiServiceImpl();

   public static UserApiService getUserApi()
   {
      return service;
   }
}
