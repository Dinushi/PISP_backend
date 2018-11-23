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
package pisp.services;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.PispFlow.PaymentMediator;
import pisp.dao.PaymentManagementDAO;
import pisp.models.Payment;
import pisp.models.PispInternalResponse;
import pisp.utilities.constants.ErrorMessages;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class PaymentAuthorizationService {

    private Log log = LogFactory.getLog(PaymentAuthorizationService.class);


    private String code;
    private String paymentInitReqId;
    private String idToken;

    private Payment payment;
    private String paymentId;
    private String clientIdOfObApplication;
    private String bankUid;

    public PaymentAuthorizationService(String paymentInitReqId, String code,String idToken){
        this.paymentInitReqId=paymentInitReqId;
        this.code=code;
        this.idToken=idToken;

    }



    public PispInternalResponse processPSUAuthorization(){
        //PaymentManagementDAO paymentManagementDAO=new PaymentManagementDAO();
        //Payment paymentDetailsFromDb =paymentManagementDAO.retrievePayment(paymentInitReqId);
        decodeTheIdToken();
        this.payment=retrievePaymentDetails();
        //write some code to validate payment id..and client id ,etc.get the payment id from db relevent to the paymentInitreqId and valiadate
        boolean result=startPaymentProcessing(paymentInitReqId,payment,code);
        PispInternalResponse response=new PispInternalResponse(payment,result);
        return response;
    }


    private void decodeTheIdToken() {

        String[] split_string = idToken.split("\\.");
        String base64EncodedHeader = split_string[0];
        String base64EncodedBody = split_string[1];
        // String base64EncodedSignature = split_string[2];//TODO:can verify the signature

        byte[]  base64EncodedHeaderByteArray=base64EncodedHeader.getBytes(Charset.forName("UTF-8"));
        byte[]  base64EncodedBodyByteArray=base64EncodedBody.getBytes(Charset.forName("UTF-8"));



        log.info("~~~~~~~~~ JWT Header ~~~~~~~");
        Base64 base64=new Base64();
        byte[] headerByteArray =base64.decode(base64EncodedHeaderByteArray);
        String header = null;
        try {
            header = new String(headerByteArray,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.info("JWT Header : " + header);


        log.info("~~~~~~~~~ JWT Body ~~~~~~~");
        byte[] bodyByteArray = base64.decode(base64EncodedBodyByteArray);
        String body = null;
        try {
            body = new String(bodyByteArray,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.info("JWT Body : "+body);
        //String [] idTokenContent=body.split(",");

    }

    private Payment retrievePaymentDetails(){
        PaymentManagementDAO paymentManagementDAO=new PaymentManagementDAO();
        Payment payment=paymentManagementDAO.retrievePayment(this.paymentInitReqId);
        return  payment;

    }


    /**
     * Get an User Access Token from bank, by exchanging the one-time code grant and save it to the DB.
     *
     * @param paymentInitReqId The paymentInitiation to bind the token with.
     * @param payment   The bank UID to get the token from.
     * @param code     The code grant to exchange for AccessToken.
     */
    private boolean startPaymentProcessing(String paymentInitReqId, Payment payment, String code) {
        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(payment, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(code, ErrorMessages.PARAMETERS_NULL);

        PaymentMediator paymentMediator=new PaymentMediator();
        return paymentMediator.processPaymentAfterPSUAuthorization(paymentInitReqId, payment, code);
    }
}
