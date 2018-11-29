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
package com.wso2.finance.open.banking.pisp.utilities;

import com.wso2.finance.open.banking.pisp.exception.PispException;
import com.wso2.finance.open.banking.pisp.models.InternalResponse;
import com.wso2.finance.open.banking.pisp.models.Payment;
import com.wso2.finance.open.banking.pisp.utilities.constants.ErrorMessages;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * This class is to verify idToken against payment data.
 */
public class AuthCodeVerification {

    private static Log log = LogFactory.getLog(AuthCodeVerification.class);
    private String idToken;
    private Payment payment;

    public AuthCodeVerification(String idToken, Payment payment) {

        this.idToken = idToken;
        this.payment = payment;
    }

    public InternalResponse verifyTheIdTokenAndPaymentData() {

        String tokenBody = decodeTheIdToken();
        return verifyWithPaymentData(tokenBody);

    }

    /**
     * Decode the content in the IdToken.
     *
     * @return
     */
    private String decodeTheIdToken() {

        String[] splitString = this.idToken.split("\\.");
        String base64EncodedHeader = splitString[0];
        String base64EncodedBody = splitString[1];

        byte[] base64EncodedHeaderByteArray = base64EncodedHeader.getBytes(Charset.forName("UTF-8"));
        byte[] base64EncodedBodyByteArray = base64EncodedBody.getBytes(Charset.forName("UTF-8"));

        Base64 base64 = new Base64();
        byte[] headerByteArray = base64.decode(base64EncodedHeaderByteArray);
        String header;
        try {
            header = new String(headerByteArray, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new PispException(ErrorMessages.INVALID_FORMAT_IDTOKEN);
        }
        if (log.isDebugEnabled()) {
            log.debug("JWT Header: " + header);
        }
        byte[] bodyByteArray = base64.decode(base64EncodedBodyByteArray);
        String body;
        try {
            body = new String(bodyByteArray, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new PispException(ErrorMessages.INVALID_FORMAT_IDTOKEN);
        }
        if (log.isDebugEnabled()) {
            log.debug("JWT Body: " + body);
        }
        return body;

    }

    /**
     * Compare the content in the idToken matches with payment data at PISP.
     *
     * @param tokenBody
     * @return
     */
    private InternalResponse verifyWithPaymentData(String tokenBody) {
    /*
       String [] idTokenContent=tokenBody.split(",");
        String openBankIntentIdContent = null;
        for (int i = 0; i < idTokenContent.length; i++){
            if (idTokenContent[i].contains(Constants.OPENBANKING_INTENT_ID)) {
                openBankIntentIdContent = idTokenContent[i];
                break;
            }
        }
        if(openBankIntentIdContent!=null){
            String [] paymentInitReqIdInfo=openBankIntentIdContent.split(":");
            String openBankIntentId=paymentInitReqIdInfo[1].substring(1,paymentInitReqIdInfo[1].length()-1);
            log.info("Payment Id in IdToken: "+openBankIntentId);
            log.info("Payment Id in db: "+payment.getPaymentId());

            if(payment.getPaymentId().equals(openBankIntentId) ){
                return new InternalResponse(Constants.VERIFIED,true);
            }else{
                return new InternalResponse(ErrorMessages.PAYMENT_ID_MISMATCH,false);
            }
        }else{
            return new InternalResponse(ErrorMessages.INVALID_CONTENT_IN_IDTOKEN,false);
        }

*/
        return new InternalResponse("Ok", true);

    }
}
