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
 * This sub class is to hold Banks supported by PISP which are used as PSU Banks.
 */
public class DebtorBank extends Bank {

    private String bankUid;

    private String specForOB = null;

    private String clientId = null;


    public String getSpecForOB() {
        return specForOB;
    }

    public void setSpecForOB(String specForOB) {
        this.specForOB = specForOB;
    }

    public String getClientId() {
        return clientId;
    }

    /**
     * set the client-id received from Bank when registering as a TPP.
     * @param clientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getBankUid() {
        return bankUid;
    }

    public void setBankUid(String bankUid) {
        this.bankUid = bankUid;
    }
}
