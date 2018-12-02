/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 *
 */
package com.wso2.finance.open.banking.pisp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Model the payment information.
 */
public class Payment {
    private String eShopUsername = null;
    private String transactionId = null;
    private Merchant merchant;
    private float instructedAmount = 0;
    private String instructedAmountCurrency;
    private String customerIdentification = null;
    private String deliveryAddress = null;
    private String redirectURI = null;
    private String psuUsername = null;
    private DebtorBank customerBank = null;
    private BankAccount customerBankAccount = null;
    private boolean errorStatus = false;
    private String errorMessage = null;
    private String paymentInitReqId = null;
    private String paymentStatus;
    private  String paymentId;
    private  String paymentSubmissionId;

    public String getEShopUsername() {
        return eShopUsername;
    }

    public void setEShopUsername(String eShopUsername) {
        this.eShopUsername = eShopUsername;
    }

    public float getInstructedAmount() {
        return instructedAmount;
    }

    public void setInstructedAmount(float instructedAmount) {
        this.instructedAmount = instructedAmount;
    }

    public String getInstructedAmountCurrency() {
        return instructedAmountCurrency;
    }

    public void setInstructedAmountCurrency(String instructedAmountCurrency) {
        this.instructedAmountCurrency = instructedAmountCurrency;
    }

    public String getCustomerIdentification() {
        return customerIdentification;
    }

    public void setCustomerIdentification(String customerIdentification) {
        this.customerIdentification = customerIdentification;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getRedirectURI() {
        return redirectURI;
    }

    public void setRedirectURI(String redirectURI) {
        this.redirectURI = redirectURI;
    }


    public boolean isError() {
        return errorStatus;
    }

    public void setErrorStatus(boolean errorStatus) {
        this.errorStatus = errorStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getPaymentInitReqId() {
        return paymentInitReqId;
    }

    public void setPaymentInitReqId(String paymentInitReqId) {
        this.paymentInitReqId = paymentInitReqId;
    }


    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public DebtorBank getCustomerBank() {
        return customerBank;
    }

    public void setCustomerBank(DebtorBank customerBank) {
        this.customerBank = customerBank;
    }

    public BankAccount getCustomerBankAccount() {
        return customerBankAccount;
    }

    public void setCustomerBankAccount(BankAccount customerBankAccount) {
        this.customerBankAccount = customerBankAccount;
    }
    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPsuUsername() {
        return psuUsername;
    }

    public void setPsuUsername(String psuUsername) {
        this.psuUsername = psuUsername;
    }

    public String getPaymentSubmissionId() {
        return paymentSubmissionId;
    }

    public void setPaymentSubmissionId(String paymentSubmissionId) {
        this.paymentSubmissionId = paymentSubmissionId;
    }

    public String getTransactionId() {

        return transactionId;
    }

    public void setTransactionId(String transactionId) {

        this.transactionId = transactionId;
    }
}
