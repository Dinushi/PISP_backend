package pisp.dto;

import io.swagger.annotations.ApiModel;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;



/**
 * conditional, only multivendor ecommerce users are required to provide the account details. Unambiguous identification of the account of the merchant/creditor to which a credit entry will be made as a result of the payment.
 **/


@ApiModel(description = "conditional, only multivendor ecommerce users are required to provide the account details. Unambiguous identification of the account of the merchant/creditor to which a credit entry will be made as a result of the payment.")
public class BankAccountDTO  {
  
  
  public enum SchemeNameEnum {
     IBAN, 
  };
  @NotNull
  private SchemeNameEnum schemeName = null;
  
  @NotNull
  private String identification = null;
  
  
  private String accountOwnerName = null;

  
  /**
   * Name of the identification scheme, in a coded form as published in an external list.
   **/
  @ApiModelProperty(required = true, value = "Name of the identification scheme, in a coded form as published in an external list.")
  @JsonProperty("schemeName")
  public SchemeNameEnum getSchemeName() {
    return schemeName;
  }
  public void setSchemeName(SchemeNameEnum schemeName) {
    this.schemeName = schemeName;
  }

  
  /**
   * Identification assigned by an institution to identify an account. This identification is known by the account owner.
   **/
  @ApiModelProperty(required = true, value = "Identification assigned by an institution to identify an account. This identification is known by the account owner.")
  @JsonProperty("identification")
  public String getIdentification() {
    return identification;
  }
  public void setIdentification(String identification) {
    this.identification = identification;
  }

  
  /**
   * The name of the account owner
   **/
  @ApiModelProperty(value = "The name of the account owner")
  @JsonProperty("accountOwnerName")
  public String getAccountOwnerName() {
    return accountOwnerName;
  }
  public void setAccountOwnerName(String accountOwnerName) {
    this.accountOwnerName = accountOwnerName;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class BankAccountDTO {\n");
    
    sb.append("  schemeName: ").append(schemeName).append("\n");
    sb.append("  identification: ").append(identification).append("\n");
    sb.append("  accountOwnerName: ").append(accountOwnerName).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
