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
package pisp.PispFlow.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import pisp.exception.PispException;
import pisp.models.AccessToken;
import pisp.models.HTTPResponse;
import pisp.models.Payment;
import pisp.utilities.Utilities;
import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Properties;
import java.util.Random;

public class PispFlowUK extends GenericPispFlowImpl {

    private Log log = LogFactory.getLog(PispFlowUK.class);


    private String xFAPIInteractionIID;
    private String xFAPIFinancialID;

    private  String paymentSubmissionURL;

    public PispFlowUK(String bank_uid){
        super(bank_uid);
        loadConfigurations(bank_uid);//this should be changed o loadTheRestOfConfigurations();
    }


    @Override
    public void loadConfigurations(String bankUid) {
            Properties prop = new Properties();
            Path fileDirectory = FileSystems.getDefault().getPath("banks/" + bankUid + "/" + bankUid + ".properties");
            try (InputStream input = this.getClass().getClassLoader()
                .getResourceAsStream(fileDirectory.toString())) {

            prop.load(input);

            xFAPIInteractionIID = prop.getProperty("xFapiInteractionIid");
            xFAPIFinancialID = prop.getProperty("xFapiFinancialId");
            paymentSubmissionURL = prop.getProperty("paymentSubmissionURL");
        } catch (IOException | NullPointerException ex) {

            log.error(ErrorMessages.PROPERTIES_FILE_ERROR, ex);
            throw new PispException(ErrorMessages.PROPERTIES_FILE_ERROR);
        }

    }


    /*
    ==================================================================================
    Following methods are specific for invocation Payment Initiation resource of bank
    ==================================================================================
    */

    /**
     * Invoke the payment Initiation API resource of the bank
     * Request paymentInitiationID from bank APIs.
     *
     * @return The paymnet Initiation ID from bank.
     */
    @Override
    public String invokePaymentInitiation(Payment paymentData) {
        HttpPost paymentInitiation = new HttpPost(paymentInitiationsURL);

        paymentInitiation.setHeader(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE);
        paymentInitiation.setHeader(Constants.AUTHORIZATION_HEADER, authorizationString);
        paymentInitiation.setHeader(Constants.X_FAPI_FINANCIAL_ID, xFAPIFinancialID);
        paymentInitiation.setHeader(Constants.X_IDEMPOTENCY_KEY, generateXIdempotencyKey());
        paymentInitiation.setHeader(Constants.ACCEPT_HEADER, Constants.ACCEPT);

        StringEntity bodyEntity = new StringEntity(getPaymentInitiationRequestBody(paymentData), StandardCharsets.UTF_8);
        paymentInitiation.setEntity(bodyEntity);

        HTTPResponse response = Utilities.getHttpPostResponse(paymentInitiation, "Payment Initiation URL");

        String paymentInitiationPostResponse = response.getResponse();

        log.info("Returned for payment Initiation Request: " + paymentInitiationPostResponse);

        if (paymentInitiationPostResponse != null) {
            try {
                JSONObject paymentInitiationResponseJson = new JSONObject(paymentInitiationPostResponse);
                String id = (String) paymentInitiationResponseJson
                        .getJSONObject("Data").get("PaymentId");
                savePaymentInitiationID(id, paymentData.getPaymentInitReqId());
                return id;
            } catch (JSONException j) {
                log.error(ErrorMessages.PAYMENT_INITIATION_FAILED, j);
                throw new PispException(ErrorMessages.PAYMENT_INITIATION_FAILED);
            }
        } else {
            throw new PispException(ErrorMessages.PAYMENT_INITIATION_FAILED);
        }
    }

    /**
     * generate a random variable as the xidempotencykey
     * Unique request identifier to support idempotency of a POST request
     * @return
     */
    private String generateXIdempotencyKey(){
        Random rand = new Random();

        int number = rand.nextInt(100000000) + 1;
        return String.valueOf(number);

    }

    /**
     * Generate the Claims Body required for payment-Initiation-Request call.
     * @return The claims body.
     * @throws PispException If generation fails.
     */

    //need to change this request body as per the relevant paymentInitiationRequest
    private String getPaymentInitiationRequestBody(Payment paymentData) throws PispException {
        Path path = FileSystems.getDefault().getPath("banks/" + paymentData.getCustomerBank().getBankUid() + "/payment-request-body.json");

        //DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'");
        //Calendar c = Calendar.getInstance();

        try (InputStream input = PispFlowUK.class.getClassLoader()
                .getResourceAsStream(path.toString())) {

            String text = IOUtils.toString(input, StandardCharsets.UTF_8);
            log.info("Payment Initiation Request body"+ text);
            JSONObject initiationRequestBody = new JSONObject(text);

            //ned to sent all payment data to here.............................?????????????????????????????????

            /*
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("InstructedAmount").put("Amount", paymentdata.getInstructedAmount());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("InstructedAmount").put("Currency", paymentdata.getInstructedAmountCurrency());

            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("CreditorAccount").put("SchemeName", paymentdata.getMerchant().getMerchantAccount().getSchemeName());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("CreditorAccount").put("Identification", paymentdata.getMerchant().getMerchantAccount().getIdentification());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("CreditorAccount").put("Name", paymentdata.getMerchant().getMerchantAccount().getAccountOwnerName());

            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("CreditorAgent").put("SchemeName", paymentdata.getMerchant().getMerchantBank().getSchemeName());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("CreditorAgent").put("Identification", paymentdata.getMerchant().getMerchantBank().getIdentification());

            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("DebtorAccount").put("SchemeName", paymentdata.getCustomerBankAccount().getSchemeName());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("DebtorAccount").put("Identification", paymentdata.getCustomerBankAccount().getIdentification());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("DebtorAccount").put("Name", paymentdata.getCustomerBankAccount().getAccountOwnerName());

            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("DebtorAgent").put("SchemeName", paymentdata.getCustomerBank().getSchemeName());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("DebtorAgent").put("Identification",  paymentdata.getCustomerBank().getIdentification());
            */

            return initiationRequestBody.toString();

        } catch (IOException e) {
            log.error("Error while reading request object", e);
            throw new PispException("Error while reading request object");
        }
    }

    /*
    =======================================================================================
    following methods are specific to get PSU authorization for the payment
    =======================================================================================
    */
    /*    *//**
     * Get the Banks URL to redirect PSU to initiate authorization flow.
     *
     * @return URL of the entry point to authorization flow.
     *//*
    @Override
    public String generateAuthorizationURL(String paymentId) {

        String requestObject = getRequestObject(paymentId);
        String encodedString = new String(Base64.getEncoder()
                .encode(("pisp:" + paymentId).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

        String url = authorizeApiURL + "?" +
                "response_type=" + Constants.RESPONSE_TYPE + "&" +
                "client_id=" + clientID + "&" +
                "scope=payments openid" + "&" +
                "redirect_uri=" + redirectURL + "&" +
                "state=" + encodedString + "&" +
                "request=" + requestObject + "&" +
                "prompt=login&" +
                "nonce="+ Constants.NONCE;

        log.info("Generated and returning URL: " + url);
        return url;
    }*/

    /**
     * Get the Banks URL to initiate authorization flow.
     *
     * @return URL of the entry point to authorization flow.
     */
    @Override
    public String generateAuthorizationURL(String paymentId) {


        String token =getRequestObject (paymentId);
        log.info(token);
        //String encodedString = new String(Base64.getEncoder()
        //.encode(("pisp:" + paymentId).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

        String url = authorizeApiURL + "?" +
                "response_type=code id_token&" +
                "client_id=" + clientID + "&" +
                "scope=payments openid&" +
                "redirect_uri=" + redirectURL + "&" +
                "state=" + "YWlzcDozMTQ2" + "&" +
                "request=" + token + "&" +
                "prompt=login&" +
                "nonce="+ "n-0S6_WzA2M";

        log.info("Generated and returning URL: " + url);

        return url;
    }


    /*
    =====================================================================================================================
    Following methods are specific to get user access token once PSU has authorized the payment and to submit the payment
    =====================================================================================================================
    */
    /**
     * this will process any unique flow followed by a spec to process payment after PSU authorization
     * @param code
     * @param payment
     * @return
     */
    @Override
    public boolean processPaymentAfterPSUAuthorization(String code, Payment payment) {
        this.saveCodeGrant(code,payment.getPaymentInitReqId());
        AccessToken userAccessToken=this.getUserAccessToken(code, payment.getPaymentId());
        this.authorizationString=Constants.AUTHORIZATION_BEARER_HEADER + userAccessToken.getPrimaryAccessToken();
        log.info("User Access Token: "+this.authorizationString);

        String paymentStatus=this.submitPayment(payment);
        if(paymentStatus.equals(Constants.UK_TRANSACTION_STATUS_AFTER_SUBMISSION)){
            return true;
        }
        else{
            return false;
        }
    }


    /**
     * Save Authorization Code grant to database.
     * authorization code is generated upon the PSU provides the consent to the payment
     *
     * @param authorizationCode Authorization Code Grant to save.
     */
    private void saveCodeGrant(String authorizationCode, String paymentInitReqId) {
        accessTokenManagementDAO.saveAuthcode( authorizationCode,paymentInitReqId);
        log.info("Authorization Code saved to DB");
    }




    /**
     * Exchange the code grant to get the Access Token of the user and save it to DB.
     *
     * @param authorizationCode The code grant to exchange for token
     * @return The user access token of the user
     */
    public AccessToken getUserAccessToken(String authorizationCode, String paymentId) {

        //log.info("Retrieving Initiation IDs");
        //String initiationId = accessTokenManagementDAO.getInitiationId(username, bankID, "retrieve");
        //log.info("Initiation IDs retrieved");

        log.info("generating the user access token by exchanging authorization code");
        Validate.notNull(authorizationCode, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(paymentId, ErrorMessages.PARAMETERS_NULL);

        log.info("Getting Assertion Key");
        String assertionKey = getKey();
        log.info("Assertion Key retrieved");

        String body2 = "grant_type=" + Constants.GRANT_TYPE + "&" +
                "scope=" + Constants.PAYMENT_SCOPE + paymentId + "&" +
                "code=" + authorizationCode + "&" +
                "redirect_uri=" + redirectURL + "&" +
                "client_assertion_type=" + clientAssertionType + "&" +
                "client_assertion=" + assertionKey;

        StringEntity bodyEntity = new StringEntity(body2, StandardCharsets.UTF_8);
        HttpPost tokenApi = new HttpPost(tokenApiURL);
        tokenApi.setHeader(Constants.CONTENT_TYPE_HEADER, Constants.TOKEN_API_CONTENT_TYPE);
        tokenApi.setEntity(bodyEntity);

        log.info("Sending Call to exchange auth code with body: " + body2);
        HTTPResponse response = Utilities.getHttpPostResponse(tokenApi, "Access Token");

        if (response.getStatusCode() != 200) {
            if (response.getStatusCode() == 401) {
                throw new PispException("Access token expired. Re-authorize");
            }
        }

        String tokenApiResponse = response.getResponse();
        log.info("Returned for User Access Token: " + tokenApiResponse);

        try {
            if (tokenApiResponse != null) {
                JSONObject tokenApiResponseJson = new JSONObject(tokenApiResponse);

                Calendar c = Calendar.getInstance();
                c.add(Calendar.SECOND, tokenApiResponseJson.getInt("expires_in"));

                return new AccessToken(tokenApiResponseJson.getString("access_token"),
                        tokenApiResponseJson.getString("refresh_token"),
                        c.getTime());
            } else {
                throw new PispException("User Access token retrieval failed");
            }
        } catch (JSONException e) {
            log.error("User Access Token Missing. Check validity of parameters", e);
            log.info("Returned for User Access Token: " + tokenApiResponse);
            throw new PispException("User Access token retrieval failed");
        }
    }


    /**
     * Refresh an expired user access token and get a new one.
     *
     * @param refreshToken The refreshToken to use to refresh.
     * @param paymentId     The respective paymentId the token was issued to.
     * @return New AccessToken.
     */
    public AccessToken refreshAccessToken(String refreshToken, String paymentId) {
        //log.info("Retrieving Initiation IDs");
        //String initiationId = accessTokenManagementDAO.getInitiationId(username, bankID, "retrieve");
        //log.info("Initiation IDs retrieved");

        Validate.notNull(refreshToken, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(paymentId, ErrorMessages.PARAMETERS_NULL);

        log.info("Getting Assertion Key");
        String assertionKey = getKey();
        log.info("Assertion Key retrieved");

        String body2 = "grant_type=" + "refresh_token" + "&" +
                "scope=" + Constants.PAYMENT_SCOPE + paymentId + "&" +
                "refresh_token=" + refreshToken + "&" +
                "redirect_uri=" + redirectURL + "&" +
                "client_assertion_type=" + clientAssertionType + "&" +
                "client_assertion=" + assertionKey;

        StringEntity bodyEntity = new StringEntity(body2, StandardCharsets.UTF_8);
        HttpPost tokenApi = new HttpPost(tokenApiURL);
        tokenApi.setHeader(Constants.CONTENT_TYPE_HEADER, Constants.TOKEN_API_CONTENT_TYPE);
        tokenApi.setEntity(bodyEntity);

        log.info("Sending Refresh Token Call with body: " + body2);
        HTTPResponse response = Utilities.getHttpPostResponse(tokenApi, "Refresh Token");

        if (response.getStatusCode() != 200) {
            if (response.getStatusCode() == 401) {
                throw new PispException("Refresh token expired. Re-authorize");
            }
            throw new PispException("Error while refreshing token.");
        }

        String tokenApiResponse = response.getResponse();
        log.info("Returned for Refresh Token: " + tokenApiResponse);

        try {
            if (tokenApiResponse != null) {
                JSONObject tokenApiResponseJson = new JSONObject(tokenApiResponse);

                Calendar c = Calendar.getInstance();
                c.add(Calendar.SECOND, tokenApiResponseJson.getInt("expires_in"));

                AccessToken accessToken = new AccessToken(tokenApiResponseJson.getString("access_token"),
                        tokenApiResponseJson.getString("refresh_token"),
                        c.getTime());

                //saveAccessToken(accessToken, paymentId);
                return accessToken;
            } else {
                throw new PispException("Refresh token retrieval failed");
            }
        } catch (JSONException e) {
            log.error("Error: Refresh Token Missing. Check validity of parameters", e);
            log.info("Returned for Refresh Token: " + tokenApiResponse);
            throw new PispException("Refresh token retrieval failed");
        }
    }


    /*
    ===============================================================
    Following methods are specific to UK payment submission process
    ===============================================================
    */

    /**
     * Invoke the payment submission API resource of the bank
     * Request paymentSubmissionId from bank APIs.
     *
     * @return The payment submission ID from bank.
     */
    public String submitPayment(Payment paymentData) {
        HttpPost paymentSubmission = new HttpPost(paymentSubmissionURL);

        paymentSubmission.setHeader(Constants.X_IDEMPOTENCY_KEY, generateXIdempotencyKey());
        paymentSubmission.setHeader(Constants.X_FAPI_FINANCIAL_ID, xFAPIFinancialID);
        paymentSubmission.setHeader(Constants.AUTHORIZATION_HEADER, authorizationString);


        paymentSubmission.setHeader(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE);
        paymentSubmission.setHeader(Constants.ACCEPT_HEADER, Constants.ACCEPT);

        StringEntity bodyEntity = new StringEntity(getPaymentSubmissionRequestBody(paymentData), StandardCharsets.UTF_8);
        paymentSubmission.setEntity(bodyEntity);

        HTTPResponse response = Utilities.getHttpPostResponse(paymentSubmission, "Payment submission URL");

        String paymentSubmissionPostResponse = response.getResponse();

        log.info("Returned for payment submission Request: " + paymentSubmissionPostResponse);

        if (paymentSubmissionPostResponse != null) {
            try {
                JSONObject paymentSubmissionResponseJson = new JSONObject(paymentSubmissionPostResponse);
                String id = (String) paymentSubmissionResponseJson
                        .getJSONObject("Data").get("PaymentSubmissionId");
                String status= (String) paymentSubmissionResponseJson
                        .getJSONObject("Data").get("Status");
                savePaymentSubmissionID(id, paymentData.getPaymentInitReqId());
                return status;
            } catch (JSONException j) {
                log.error(ErrorMessages.PAYMENT_SUBMISSION_FAILED, j);
                throw new PispException(ErrorMessages.PAYMENT_SUBMISSION_FAILED);
            }
        } else {
            throw new PispException(ErrorMessages.PAYMENT_SUBMISSION_FAILED);
        }
    }


    /**
     * Save the paymentSubmissionId received from bank in the database
     *
     * @param paymentSubmissionId
     * @param paymentInitReqId
     */
    public void savePaymentSubmissionID(String paymentSubmissionId, String paymentInitReqId) {
        paymentManagementDAO.saveSubmissionIds(paymentSubmissionId,paymentInitReqId);
        log.info("Payment Submission ID received when submitting the payment at bank is saved to DB");
    }


    /**
     * Generate the Claims Body required for payment-Initiation-Request call.
     * @return The claims body.
     * @throws PispException If generation fails.
     */
    //need to chnage this request body as per the relevant paymentInitiationRequest
    private String getPaymentSubmissionRequestBody(Payment paymentData) throws PispException {
        Path path = FileSystems.getDefault().getPath("banks/" + paymentData.getCustomerBank().getBankUid() + "/payment-submission-body.json");

        //DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'");
        //Calendar c = Calendar.getInstance();

        try (InputStream input = PispFlowUK.class.getClassLoader()
                .getResourceAsStream(path.toString())) {

            String text = IOUtils.toString(input, StandardCharsets.UTF_8);
            log.info("Payment submission Request body"+ text);
            JSONObject initiationRequestBody = new JSONObject(text);

            //ned to sent all payment data to here.............................?????????????????????????????????

            /*
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("InstructedAmount").put("Amount", paymentdata.getInstructedAmount());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("InstructedAmount").put("Currency", paymentdata.getInstructedAmountCurrency());

            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("CreditorAccount").put("SchemeName", paymentdata.getMerchant().getMerchantAccount().getSchemeName());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("CreditorAccount").put("Identification", paymentdata.getMerchant().getMerchantAccount().getIdentification());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("CreditorAccount").put("Name", paymentdata.getMerchant().getMerchantAccount().getAccountOwnerName());

            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("CreditorAgent").put("SchemeName", paymentdata.getMerchant().getMerchantBank().getSchemeName());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("CreditorAgent").put("Identification", paymentdata.getMerchant().getMerchantBank().getIdentification());

            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("DebtorAccount").put("SchemeName", paymentdata.getCustomerBankAccount().getSchemeName());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("DebtorAccount").put("Identification", paymentdata.getCustomerBankAccount().getIdentification());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("DebtorAccount").put("Name", paymentdata.getCustomerBankAccount().getAccountOwnerName());

            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("DebtorAgent").put("SchemeName", paymentdata.getCustomerBank().getSchemeName());
            initiationRequestBody.getJSONObject("Data").getJSONObject("Initiation").getJSONObject("DebtorAgent").put("Identification",  paymentdata.getCustomerBank().getIdentification());
            initiationRequestBody.getJSONObject("Risk").put("DeliveryAddress",  paymentData.getDeliveryAddress());
            */

            return initiationRequestBody.toString();

        } catch (IOException e) {
            log.error("Error while reading request object", e);
            throw new PispException("Error while reading request object");
        }
    }

    /*
    ==========================================================================
    Following methods are specific to verify whether the payment has completed
    ==========================================================================
    */

    /**
     * this will fetch the status of payment from bank and notify the customer and e-commerce site
     * @param paymentId
     * @return
     */
    @Override
    public boolean getTransactionStatusOfPayment(String paymentId) {
        HttpGet getPayment = new HttpGet(paymentSubmissionURL+"/"+paymentId);

        getPayment.setHeader(Constants.X_FAPI_FINANCIAL_ID, xFAPIFinancialID);
        getPayment.setHeader(Constants.AUTHORIZATION_HEADER, authorizationString);
        getPayment.setHeader(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE);
        getPayment.setHeader(Constants.ACCEPT_HEADER, Constants.ACCEPT);
        getPayment.setHeader(Constants.X_IDEMPOTENCY_KEY, generateXIdempotencyKey());
        getPayment.setHeader(Constants.ACCEPT_HEADER, Constants.ACCEPT);


        HTTPResponse response = Utilities.getHttpGetResponse(getPayment, "Get Payment URL");

        String paymentGETResponse = response.getResponse();

        log.info("Returned for payment GET Request: " + paymentGETResponse);

        if (paymentGETResponse != null) {
            try {
                JSONObject paymentGETResponseJson = new JSONObject(paymentGETResponse);
                String status= (String) paymentGETResponseJson
                        .getJSONObject("Data").get("Status");
                if(status.equals(Constants.UK_TRANSACTION_STATUS_COMPLETED)){
                    return true;
                }
                //TODO:check for some way that you can read error status and return it
                return false;
            } catch (JSONException j) {
                log.error(ErrorMessages.PAYMENT_GET_REQUEST_FAILED, j);
                throw new PispException(ErrorMessages.PAYMENT_GET_REQUEST_FAILED);
            }
        } else {
            throw new PispException(ErrorMessages.PAYMENT_GET_REQUEST_FAILED);
        }

    }


}
