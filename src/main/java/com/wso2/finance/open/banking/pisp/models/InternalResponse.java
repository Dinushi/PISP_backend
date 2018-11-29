/*
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *   This software is the property of WSO2 Inc. and its suppliers, if any.
 *   Dissemination of any information or reproduction of any material contained
 *   herein is strictly forbidden, unless permitted by WSO2 in accordance with
 *   the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 *   language governing the permissions and limitations under this license,
 *   please see the license as well as any agreement you’ve entered into with
 *   WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.finance.open.banking.pisp.models;

/**
 * Use to model any data and result of a method invocation and to transfer them across the layers.
 */
public class InternalResponse {
    private String message;
    private Object data;
    private boolean isOperationSuccessful;

    public InternalResponse(String message, boolean isOperationSuccessful) {
        this.setMessage(message);
        this.isOperationSuccessful = isOperationSuccessful;
    }

    public InternalResponse(Object data, boolean isOperationSuccessful) {
        this.setData(data);
        this.isOperationSuccessful = isOperationSuccessful;
    }

    public boolean isOperationSuccessful() {
        return isOperationSuccessful;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
