package com.wso2.finance.open.banking.pisp.factories;

import com.wso2.finance.open.banking.pisp.BankConnectionApiService;
import com.wso2.finance.open.banking.pisp.impl.BankConnectionApiServiceImpl;

public class BankConnectionApiServiceFactory {

   private final static BankConnectionApiService service = new BankConnectionApiServiceImpl();

   public static BankConnectionApiService getBankConnectionApi()
   {
      return service;
   }
}
