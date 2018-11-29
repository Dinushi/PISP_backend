package com.wso2.finance.open.banking.pisp.dto;

import io.swagger.annotations.ApiModel;

import java.util.ArrayList;

/**
 * The payment history for the requested time period.
 **/
@ApiModel(description = "The payment history for the requested time period")
public class PaymentHistoryDTO extends ArrayList<PaymentHistoryInnerDTO> {

    private static final long serialVersionUID = 6106269076155338045L;

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PaymentHistoryDTO {\n");
        sb.append("  " + super.toString()).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
