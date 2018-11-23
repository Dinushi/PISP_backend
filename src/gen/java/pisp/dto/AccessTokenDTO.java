package pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;


/**
 * The response returned when the customer selects a bank to pay via
 **/


@ApiModel(description = "The response returned when the customer selects a bank to pay via")
public class AccessTokenDTO {


  
  @NotNull
  private String accessToken = null;

  @NotNull
  private String expireTime = null;


  /**
   * The access Token
   **/
  @ApiModelProperty(value = "The access Token")
  @JsonProperty("accessToken")
  public String getAccessToken() {
    return accessToken;
  }
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }


  /**
   * The time which the token will expire
   **/
  @ApiModelProperty(value = "The time which the token will expire")
  @JsonProperty("dateTime")
  public String getExpireTime() {
    return expireTime;
  }
  public void seteEpireTime(String expireTime) {
    this.expireTime = expireTime;
  }



  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccessTokenDTO {\n");
    
    sb.append("  accessToken: ").append(accessToken).append("\n");
    sb.append("  expireTime: ").append(expireTime).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
