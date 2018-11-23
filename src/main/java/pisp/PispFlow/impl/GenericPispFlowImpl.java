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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import pisp.PispFlow.PispFlow;
import pisp.dao.AccessTokenManagementDAO;
import pisp.dao.PaymentManagementDAO;
import pisp.exception.PispException;
import pisp.models.AccessToken;
import pisp.models.HTTPResponse;
import pisp.models.Payment;
import pisp.models.PispInternalResponse;
import pisp.utilities.Utilities;
import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public abstract class GenericPispFlowImpl implements PispFlow {

    String clientID;
    String redirectURL;

    String bank_uid;

    String paymentInitiationsURL;

    String tokenApiURL;
    String authorizeApiURL;

    String authorizationString;
    String clientAssertionType;

    char[] keyStorePassword;
    String keyStoreDomainName;
    String keyStorePath;

    String audienceValue;

    Certificate certificate = null;
    JWSAlgorithm signatureAlgorithm = JWSAlgorithm.RS256;


    AccessTokenManagementDAO accessTokenManagementDAO;
    PaymentManagementDAO paymentManagementDAO;

    private Log log = LogFactory.getLog(GenericPispFlowImpl.class);


    public GenericPispFlowImpl(String bank_uid){
        this.accessTokenManagementDAO = new AccessTokenManagementDAO();
        this.paymentManagementDAO=new PaymentManagementDAO();

        this.bank_uid = bank_uid;
        this.loadBasicConfigurations(bank_uid);
    }



    @Override
    public void loadBasicConfigurations(String bankUid){
        Properties prop = new Properties();
        Path fileDirectory = FileSystems.getDefault().getPath("banks/" + bankUid + "/" + bankUid + ".properties");
        try (InputStream input = this.getClass().getClassLoader()
                .getResourceAsStream(fileDirectory.toString())) {

            prop.load(input);

            clientID = prop.getProperty("clientID");
            redirectURL = prop.getProperty("redirect_uri");
            paymentInitiationsURL = prop.getProperty("paymentInitiationURL");
            tokenApiURL = prop.getProperty("tokenAPIURL");
            authorizeApiURL = prop.getProperty("authorizeAPIURL");

            keyStorePassword = prop.getProperty("keyStorePassword").toCharArray();
            keyStoreDomainName = prop.getProperty("keyStoreDomainName");
            keyStorePath = FileSystems.getDefault().getPath("banks/" + bankUid + "/" +
                    prop.getProperty("keyStoreName")).toString();

            if (prop.containsKey("audience")) {
                audienceValue = prop.getProperty("audience");
            } else {
                log.warn("Dedicated Audience value not found. Using Token URL instead. " +
                        "Please add an audience value to properties file");
                audienceValue = prop.getProperty("tokenAPIURL");
            }

            clientAssertionType = prop.getProperty("clientAssertionType");


        } catch (IOException | NullPointerException ex) {
            log.error(ErrorMessages.PROPERTIES_FILE_ERROR, ex);
            throw new PispException(ErrorMessages.PROPERTIES_FILE_ERROR);
        }
    }


    /*
    =============================================================================================
    Following methods are specific for invocation of token API to get an application access token
    =============================================================================================
    */

    @Override
    public void getApplicationAccessToken() {
        PispInternalResponse applicationTokenResponse = accessTokenManagementDAO.getLastApplicationToken(bank_uid);

        if (applicationTokenResponse.isOperationSuccessful()) {
            AccessToken token = (AccessToken) applicationTokenResponse.getData();
            if (token.isExpired()) {
                log.info("Expired Application Token. Requesting new token....");
                retrieveAndSaveApplicationToken();
            } else {
                authorizationString = Constants.AUTHORIZATION_BEARER_HEADER + token.getPrimaryAccessToken();
            }

        } else {
            log.info("No Application Token found in DB. Getting new token now...");
            retrieveAndSaveApplicationToken();
        }
    }


    @Override
    public void retrieveAndSaveApplicationToken(){
        HttpPost tokenApiPostReq = new HttpPost(tokenApiURL);

        String assertionKey = getKey();

        String body2 = "grant_type=" + Constants.GRANT_TYPE_CLIENT + "&" +
                "redirect_uri=" + redirectURL + "&" +
                "client_id=" + clientID + "&" +
                "client_assertion_type=" + clientAssertionType + "&" +
                "client_assertion=" + assertionKey + "&"+
                "scope=" + Constants.PAYMENTS_SCOPE_UK;

        log.info("token API request body "+body2);

        StringEntity bodyEntity = new StringEntity(body2, StandardCharsets.UTF_8);
        tokenApiPostReq.setHeader(Constants.CONTENT_TYPE_HEADER, Constants.TOKEN_API_CONTENT_TYPE);

        tokenApiPostReq.setEntity(bodyEntity);

        HTTPResponse response = Utilities.getHttpPostResponse(tokenApiPostReq, "Application Token");
        String tokenApiResponse = response.getResponse();

        if (response.getStatusCode() != 200) {
            if (response.getStatusCode() == 401) {
                throw new PispException(ErrorMessages.APPLICATION_TOKEN_EXPIRED);
            }
        }

        log.info("Returned for Application Token: " + tokenApiResponse);

        if (tokenApiResponse != null) {
            JSONObject tokenApiResponseJson = new JSONObject(tokenApiResponse);
            String applicationToken;
            try {
                applicationToken = (String) tokenApiResponseJson.get("access_token");
                authorizationString = Constants.AUTHORIZATION_BEARER_HEADER + applicationToken;

                Long timeBoundary = 3153600000L; // Seconds in a year. To check if validity of token is set to infinity
                Long expiresIn = tokenApiResponseJson.getLong("expires_in");

                Date expiryDate;
                if (expiresIn > timeBoundary) {
                    // Setting expiry to 100 years from now
                    expiryDate = new Date(System.currentTimeMillis() + (timeBoundary * 1000));
                } else {
                    expiryDate = new Date(
                            System.currentTimeMillis() + (tokenApiResponseJson.getLong("expires_in") * 1000));
                }

                accessTokenManagementDAO.saveApplicationToken(bank_uid, applicationToken, expiryDate);
            } catch (JSONException e) {
                log.error("Error: Application Token Missing. Check validity of parameters", e);
                throw new PispException("Application Token retrieval failed");
            }
        } else {
            throw new PispException("Application Token retrieval failed");
        }
    }

     /*
    ==================================================================================
    Following methods are specific for invocation Payment Initiation resource of bank
    ==================================================================================
    */


    /**
     * Save the paymentId received from bank in the database
     *
     * @param paymentId
     * @param paymentInitReqId
     */
    @Override
    public void savePaymentInitiationID(String paymentId, String paymentInitReqId) {
        paymentManagementDAO.saveInitiationIds(paymentId,paymentInitReqId);
        log.info("Payment ID received when initiating the payment at bank is saved to DB");
    }



    /*
    =======================================================================================
    following methods are specific to get PSU authorization for the payment
    =======================================================================================
    */

    /**
     * Get the Banks URL to initiate authorization flow.
     *
     * @return URL of the entry point to authorization flow.
     */
    @Override
    public abstract String generateAuthorizationURL(String paymentId);



    /*
    ================================================================
    Following methods will be implemented based on the specification
    ================================================================
    */

    /**
     * this will process any unique flow followed by a spec to process payment after PSU authorization
     * @param code
     * @param payment
     * @return
     */
    @Override
    public abstract boolean processPaymentAfterPSUAuthorization(String code, Payment payment);


    @Override
    public abstract boolean getTransactionStatusOfPayment(String paymentId);



    /*
    =================================================================
    Rest of the methods are specific for Assertion Authentication
    =================================================================
    */

    /**
     * Generate signed claims set to be used in application authorization process.
     *
     * @return Signed Claim Set.
     */
    public String getKey() {
        return signJWTWithRSA(createClientAssertionClaimSet());
    }

    /**
     * Generate signed claims set to be used in user authorization process.
     *
     * @param paymentId The account Initiation ID to use in creating claims.
     * @return The ClaimsSet created.
     */
    public String getRequestObject(String paymentId) {
        return signJWTWithRSA(createUserAssertionClaimSet(paymentId));
    }

    /**
     * Create claims set for application authorization process.
     *
     * @return The ClaimsSet created.
     */
    public JWTClaimsSet createClientAssertionClaimSet() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 6);

        JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder();
        claimsSet.issuer(clientID);
        claimsSet.subject(clientID);
        claimsSet.expirationTime(c.getTime());
        claimsSet.issueTime(new Date());
        claimsSet.jwtID(Long.toString(System.currentTimeMillis()));
        claimsSet.audience(audienceValue);

        return claimsSet.build();
    }

    /*
     */
/**
 * Create claims set for user authorization process.
 *
 * @param paymentId The paymentID to use in creating claims.
 * @return The ClaimsSet created.
 *//*

    private JWTClaimsSet createUserAssertionClaimSet(String paymentId) throws PispException {
        String encodedString = new String(Base64.getEncoder()
                .encode(("pisp:" + paymentId).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        Path path = FileSystems.getDefault().getPath("banks/" + bank_uid + "/claims-request-body.json");

        try (InputStream input = this.getClass().getClassLoader()
                .getResourceAsStream(path.toString())) {

            String text = IOUtils.toString(input, StandardCharsets.UTF_8);
            JSONObject claimsRequestBody = new JSONObject(text);

/////////////////////////
*/
/*
            JSONObject jsonObj1 = new JSONObject();

            JSONObject jsonObjInner1 = new JSONObject();

            JSONObject json = new JSONObject();
            json.put("value", paymentId);
            json.put("essential", true);

            jsonObjInner1.put("openbanking_intent_id",json);

            jsonObj1.put("userinfo",jsonObjInner1);


            JSONObject jsonObjInner2 = new JSONObject();

            JSONObject json1 = new JSONObject();
            json1.put("value", paymentId);
            json1.put("essential", true);

            jsonObjInner2.put("openbanking_intent_id",json1);


            JSONObject json3 = new JSONObject();
            json3.put("essential", true);

            JSONArray array = new JSONArray();
            array.put("urn:openbanking:psd2:sca");
            array.put("urn:openbanking:psd2:ca");
            json3.put("values", array);

            jsonObjInner2.put("acr",json3);

            jsonObj1.put("id_token",jsonObjInner2);

*//*


        /////////////////////////////////

            claimsRequestBody.getJSONObject("userinfo").
                    getJSONObject("openbanking_intent_id").put("value", paymentId);
            claimsRequestBody.getJSONObject("id_token").
                    getJSONObject("openbanking_intent_id").put("value", paymentId);

            JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder();

            claimsSet.audience(audienceValue);
            claimsSet.issuer(clientID);

            claimsSet.claim("response_type", Constants.RESPONSE_TYPE);
            claimsSet.claim("client_id", clientID);
            claimsSet.claim("redirect_uri", redirectURL);
            claimsSet.claim("scope",Constants.TOKEN_SCOPE + paymentId);
            //claimsSet.claim("state", "YWlzcDozMTQ2");
            claimsSet.claim("state", encodedString);
            claimsSet.claim("nonce", Constants.NONCE);

            claimsSet.claim("max_age", 86400);

            claimsSet.claim("claims", claimsRequestBody.toString().replace("\\\\",""));
            //claimsSet.claim("claims", jsonObj1.toString().replace("\\\\",""));
            log.info("Claims are " +  claimsRequestBody.toString().replace("\\\\",""));

            return claimsSet.build();
        } catch (IOException e) {
            log.error("Error while reading request object", e);
            throw new PispException("Error while reading request object");
        }
    }
*/
    /**
     * Create claims set for user authorization process.
     *
     * @param paymentId The account Initiation ID to use in creating claims.
     * @return The ClaimsSet created.
     */
    public JWTClaimsSet createUserAssertionClaimSet(String paymentId) throws PispException {
        //String encodedString = new String(Base64.getEncoder()
        //.encode(("pisp:" + paymentId).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

        Path path = FileSystems.getDefault().getPath("banks/" + bank_uid + "/claims-request-body.json");

        try (InputStream input = this.getClass().getClassLoader()
                .getResourceAsStream(path.toString())) {

            String text = IOUtils.toString(input, StandardCharsets.UTF_8);
            JSONObject claimsRequestBody = new JSONObject(text);

            claimsRequestBody.getJSONObject("userinfo").
                    getJSONObject("openbanking_intent_id").put("value", paymentId);
            claimsRequestBody.getJSONObject("id_token").
                    getJSONObject("openbanking_intent_id").put("value", paymentId);

            JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder();
            claimsSet.audience(audienceValue);
            claimsSet.issuer(clientID);

            claimsSet.claim("response_type", Constants.RESPONSE_TYPE);
            claimsSet.claim("client_id", clientID);
            claimsSet.claim("redirect_uri", redirectURL);
            claimsSet.claim("scope", "payments openid");
            claimsSet.claim("state", "YWlzcDozMTQ2");
            claimsSet.claim("nonce", "n-0S6_WzA2M");
            claimsSet.claim("max_age", 86400);
            String claimsetss=claimsRequestBody.toString();

            claimsetss=claimsetss.replace("\\", "");
            claimsetss = claimsetss.replace(System.getProperty("line.separator"), "");
            //claimsSet.claim("claims", claimsRequestBody.toString());
            log.info("String Claims sre " +claimsetss);

            claimsSet.claim("claims", claimsetss);

            log.info("Claims are " + claimsRequestBody);
            return claimsSet.build();
        } catch (IOException e) {
            log.error("Error while reading request object", e);
            throw new PispException("Error while reading request object");
        }
    }

    /**
     * Sign a JWT claim with RSA algorithm.
     *
     * @param jwtClaimsSet The claim set to sign.
     * @return Signed claim set in string.
     * @throws PispException If signing errors.
     */
    public String signJWTWithRSA(JWTClaimsSet jwtClaimsSet) throws PispException {
        try {
            log.info("Claim Set before signing : \n"+jwtClaimsSet.toJSONObject());
            Key privateKey = getPrivateKey(keyStorePassword, keyStoreDomainName, keyStorePath);

            JWSSigner signer = new RSASSASigner((RSAPrivateKey) privateKey);
            JWSHeader jwsHeader = new JWSHeader.Builder(signatureAlgorithm).keyID(getThumbPrint()).build();
            log.info(jwtClaimsSet.toString());
            SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
            signedJWT.sign(signer);
            String key = signedJWT.serialize();
            if (log.isDebugEnabled()) {
                log.info("Signed key is: " + key);
            }
            return key;
        } catch (JOSEException e) {
            log.error(ErrorMessages.KEY_SIGNING_ERROR, e);
            throw new PispException(ErrorMessages.KEY_SIGNING_ERROR);
        }
    }

    /**
     * Get the thumbprint for the certificate.
     *
     * @return The thumbprint for the certificate.
     * @throws PispException If thumb print creation errors.
     */
    public String getThumbPrint() throws PispException {
        Certificate certificate = this.certificate;

        MessageDigest digestValue;
        try {
            digestValue = MessageDigest.getInstance(Constants.SIGNING_ALGORITHM);
            byte[] der = certificate.getEncoded();
            digestValue.update(der);
            byte[] digestInBytes = digestValue.digest();

            String publicCertThumbprint = hexify(digestInBytes);
            if (log.isDebugEnabled()) {
                log.info("Thumb print is: " + publicCertThumbprint);
            }
            return publicCertThumbprint;
        } catch (NoSuchAlgorithmException | CertificateEncodingException e) {
            log.error(ErrorMessages.THUMBPRINT_ERROR, e);
            throw new PispException(ErrorMessages.THUMBPRINT_ERROR);
        }
    }


    /**
     * Get the private key of the application host key store.
     *
     * @param password The password for accessing key store.
     * @param domain   The domain of the key store.
     * @param path     The name of the keystore.
     * @return The private key.
     * @throws PispException If key was not read properly.
     */
    public Key getPrivateKey(char[] password, String domain, String path) throws PispException {
        try (FileInputStream fis =
                     new FileInputStream(this.getClass().getClassLoader().getResource(path).getFile())) {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(fis, password);

            KeyStore.PrivateKeyEntry pkEntry =
                    (KeyStore.PrivateKeyEntry) ks.getEntry(domain, new KeyStore.PasswordProtection(password));
            certificate = pkEntry.getCertificate();
            return pkEntry.getPrivateKey();
        } catch (KeyStoreException | IOException | CertificateException |
                UnrecoverableEntryException | NoSuchAlgorithmException | NullPointerException e) {
            log.error(ErrorMessages.PRIVATE_KEY_ERROR, e);
            throw new PispException(ErrorMessages.PRIVATE_KEY_ERROR);
        }

    }
    public String hexify(byte[] bytes) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        StringBuilder buf = new StringBuilder(bytes.length * 2);

        for (byte aByte : bytes) {
            buf.append(hexDigits[(aByte & 240) >> 4]);
            buf.append(hexDigits[aByte & 15]);
        }

        return buf.toString();
    }



}
