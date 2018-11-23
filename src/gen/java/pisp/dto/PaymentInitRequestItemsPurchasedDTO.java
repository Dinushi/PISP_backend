package pisp.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class PaymentInitRequestItemsPurchasedDTO  {
  
  
  
  private Integer quantity = null;
  
  
  private String cost = null;
  
  
  private String itemCode = null;
  
  
  private String currency = null;

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("quantity")
  public Integer getQuantity() {
    return quantity;
  }
  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("cost")
  public String getCost() {
    return cost;
  }
  public void setCost(String cost) {
    this.cost = cost;
  }

  
  /**
   * The item code issued for the item purchased
   **/
  @ApiModelProperty(value = "The item code issued for the item purchased")
  @JsonProperty("itemCode")
  public String getItemCode() {
    return itemCode;
  }
  public void setItemCode(String itemCode) {
    this.itemCode = itemCode;
  }

  
  /**
   * A code allocated to a currency by a Maintenance Agency under an international identification scheme, as described in the latest edition of the international standard ISO 4217 - Codes for the representation of currencies and funds.
   **/
  @ApiModelProperty(value = "A code allocated to a currency by a Maintenance Agency under an international identification scheme, as described in the latest edition of the international standard ISO 4217 - Codes for the representation of currencies and funds.")
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }
  public void setCurrency(String currency) {
    this.currency = currency;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentInitRequestItemsPurchasedDTO {\n");
    
    sb.append("  quantity: ").append(quantity).append("\n");
    sb.append("  cost: ").append(cost).append("\n");
    sb.append("  itemCode: ").append(itemCode).append("\n");
    sb.append("  currency: ").append(currency).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
