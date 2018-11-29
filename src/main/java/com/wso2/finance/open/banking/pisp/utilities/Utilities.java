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
import com.wso2.finance.open.banking.pisp.models.BankResponse;
import com.wso2.finance.open.banking.pisp.utilities.constants.Constants;
import com.wso2.finance.open.banking.pisp.utilities.constants.ErrorMessages;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.nio.charset.StandardCharsets;

/**
 * The utility functions used throughout the application are here.
 */

public class Utilities {

    private static Log log = LogFactory.getLog(Utilities.class);

    /**
     * Executing HTTPClient POST Request and returning the response in string and status code.
     *
     * @param httppost The HttpPet object to do HTTP call.
     * @param caller   An identifier of the caller. For logging purposes.
     * @return The response in string and status code as a HTTPResponse object.
     * @throws PispException If errors while establishing connection or parsing to string.
     */
    public static BankResponse getHttpPostResponse(HttpPost httppost, String caller) throws PispException {

        Validate.notNull(httppost, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(caller, ErrorMessages.PARAMETERS_NULL);

        try (CloseableHttpClient httpclient = HttpClients.custom().
                setSSLHostnameVerifier(new NoopHostnameVerifier()).build()) {

            log.info("Consuming " + httppost.getURI());
            HttpResponse responseHttp = httpclient.execute(httppost);

            if (responseHttp.getStatusLine().getStatusCode() != 200 &&
                    responseHttp.getStatusLine().getStatusCode() != 201) {
                log.warn(ErrorMessages.POST_CALL_FAILED + " for " + caller +
                        ": Status: " + responseHttp.getStatusLine());
                return new BankResponse(responseHttp.getStatusLine().getStatusCode());
            }

            HttpEntity entity = responseHttp.getEntity();

            try (InputStream inStream = entity.getContent(); StringWriter writer = new StringWriter()) {
                IOUtils.copy(inStream, writer, StandardCharsets.UTF_8);
                return new BankResponse(200, writer.toString());
            } catch (IOException e) {
                log.error(ErrorMessages.CONTENT_PARSING_ERROR + caller, e);
                throw new PispException(ErrorMessages.CONTENT_PARSING_ERROR + caller);
            }

        } catch (IOException e) {
            log.error(ErrorMessages.POST_CALL_FAILED + caller, e);
            throw new PispException(ErrorMessages.POST_CALL_FAILED + caller);
        }
    }

    /**
     * Executing HTTPClient GET Request and returning the response in string and status code.
     *
     * @param httpGet The HttpGet object to do HTTP call.
     * @param caller  An identifier of the caller. For logging purposes.
     * @return The response in string and status code as a HTTPResponse object.
     * @throws PispException If errors while establishing connection or parsing to string.
     */
    public static BankResponse getHttpGetResponse(HttpGet httpGet, String caller) throws PispException {

        Validate.notNull(httpGet, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(caller, ErrorMessages.PARAMETERS_NULL);

        StringBuilder result = new StringBuilder();
        log.info("Consuming " + httpGet.getURI());

        try (CloseableHttpClient client = HttpClients.custom().
                setSSLHostnameVerifier(new NoopHostnameVerifier()).build()) {
            HttpResponse response2 = client.execute(httpGet);
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(response2.getEntity()
                    .getContent(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                if (response2.getStatusLine().getStatusCode() != 200 &&
                        response2.getStatusLine().getStatusCode() != 201) {
                    log.warn(ErrorMessages.GET_CALL_FAILED + " " + caller + ": Status: " +
                            response2.getStatusLine() + result.toString());

                    return new BankResponse(response2.getStatusLine().getStatusCode());
                }
            } catch (IOException e) {
                log.error(ErrorMessages.CONTENT_PARSING_ERROR, e);
            }
        } catch (IOException e) {
            log.error(ErrorMessages.GET_CALL_FAILED, e);
            throw new PispException(ErrorMessages.GET_CALL_FAILED);
        }
        return new BankResponse(200, result.toString());
    }

    /**
     * returns whether the payment initiation at bank requires the PISP to know the debtor account details in advance.
     *
     * @param spec
     * @return
     */
    public static boolean isDebtorAccountRequired(String spec) {

        switch (spec) {
            case Constants.OPEN_BANKING_UK: {
                return false;
            }
            case Constants.OPEN_BANKING_BERLIN: {
                return true;
            }
            default: {
                return false;
            }
        }

    }

    /**
     * returns whether the payment initiation at bank requires the PISP to submit the payment after PSU authorization.
     *
     * @param spec
     * @return
     */
    public static boolean isSubmissionRequired(String spec) {

        switch (spec) {
            case Constants.OPEN_BANKING_UK: {
                return true;
            }
            case Constants.OPEN_BANKING_BERLIN: {
                return false;
            }
            default: {
                return false;
            }
        }

    }
}
