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

import java.util.Date;

/**
 * This class is to hold Application Tokens and User Access Tokens isued by Banks.
 */
public class AccessToken {
    private String primaryAccessToken;
    private String refreshToken;
    private Date validTill;
    private String paymentInitiationId;

    public AccessToken(String primaryAccessToken, String refreshToken, Date validityTill) {
        this.primaryAccessToken = primaryAccessToken;
        this.refreshToken = refreshToken;
        this.validTill = new Date(validityTill.getTime());
    }

    public AccessToken(String primaryAccessToken, Date validTill) {
        this.primaryAccessToken = primaryAccessToken;
        this.validTill = new Date(validTill.getTime());
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > validTill.getTime();
    }

    public String getPrimaryAccessToken() {
        return primaryAccessToken;
    }

    public void setPrimaryAccessToken(String primaryAccessToken) {
        this.primaryAccessToken = primaryAccessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Date getValidTill() {
        return new Date(validTill.getTime());
    }

    public String getPaymentInitiationId() {
        return paymentInitiationId;
    }

    public void setPaymentInitiationId(String paymentInitiationId) {
        this.paymentInitiationId = paymentInitiationId;
    }

    /**
     * return the access token content in String.
     * @return
     */
    @Override
    public String toString() {
        return "AccessToken{" +
                "primaryAccessToken='" + primaryAccessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", validTill=" + validTill +
                ", paymentInitiationId='" + paymentInitiationId + '\'' +
                '}';
    }

}
