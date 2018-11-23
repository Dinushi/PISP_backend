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
package pisp.utilities;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.models.Payment;
import pisp.models.PispInternalResponse;


import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class AuthCodeVerification {

    private static Log log = LogFactory.getLog(AuthCodeVerification.class);
    private String idToken;
    private Payment payment;

    public AuthCodeVerification(String idToken, Payment payment){
        this.idToken=idToken;
        this.payment=payment;
    }


    public PispInternalResponse verifyTheIdTokenAndPaymentData(){
        decodeTheIdTokenAndVerify();
        return  new PispInternalResponse("Ok",true);
    }

    private void decodeTheIdTokenAndVerify() {

        String[] split_string = this.idToken.split("\\.");
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
}
