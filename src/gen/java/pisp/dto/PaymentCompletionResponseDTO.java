package pisp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;


/**
 * The response returned when the customer selects a bank to pay via
 **/


@ApiModel(description = "The response returned when the customer selects a bank to pay via")
public class PaymentCompletionResponseDTO {
  

  
  @NotNull
  private String redirectLink = null;



  
  /**
   * the link to redirect the customer back to the e-commerce site
   **/
  @ApiModelProperty(required = true, value = "the link to redirect the customer back to the e-commerce site")
  @JsonProperty("redirectLink")
  public String getRedirectLink() {
    return redirectLink;
  }
  public void setRedirectLink(String redirectLink) {
    this.redirectLink = redirectLink;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentCompletionResponseDTO {\n");

    sb.append("  redirectLink: ").append(redirectLink).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
