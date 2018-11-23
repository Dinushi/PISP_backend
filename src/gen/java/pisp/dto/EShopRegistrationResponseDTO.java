package pisp.dto;


import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;





@ApiModel(description = "")
public class EShopRegistrationResponseDTO  {
  
  
  
  private String eShopUsername = null;
  
  
  private String cliendId = null;
  
  
  private String clientSecreat = null;

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("eShopUsername")
  public String getEShopUsername() {
    return eShopUsername;
  }
  public void setEShopUsername(String eShopUsername) {
    this.eShopUsername = eShopUsername;
  }

  
  /**
   * The client credentials needed to invoke payment API
   **/
  @ApiModelProperty(value = "The client credentials needed to invoke payment API")
  @JsonProperty("cliendId")
  public String getCliendId() {
    return cliendId;
  }
  public void setCliendId(String cliendId) {
    this.cliendId = cliendId;
  }

  
  /**
   * The client credentials needed to invoke payment API
   **/
  @ApiModelProperty(value = "The client credentials needed to invoke payment API")
  @JsonProperty("clientSecreat")
  public String getClientSecreat() {
    return clientSecreat;
  }
  public void setClientSecreat(String clientSecreat) {
    this.clientSecreat = clientSecreat;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class EShopRegistrationResponseDTO {\n");
    
    sb.append("  eShopUsername: ").append(eShopUsername).append("\n");
    sb.append("  cliendId: ").append(cliendId).append("\n");
    sb.append("  clientSecreat: ").append(clientSecreat).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
