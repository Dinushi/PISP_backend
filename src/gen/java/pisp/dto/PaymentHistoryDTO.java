package pisp.dto;

import io.swagger.annotations.ApiModel;
import java.util.ArrayList;
import java.util.List;
import pisp.dto.PaymentHistoryInnerDTO;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;



/**
 * The payment history for the requested time period
 **/


@ApiModel(description = "The payment history for the requested time period")
public class PaymentHistoryDTO extends ArrayList<PaymentHistoryInnerDTO> {
  private static final long serialVersionUID = 6106269076155338045L;
  

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentHistoryDTO {\n");
    sb.append("  " + super.toString()).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
