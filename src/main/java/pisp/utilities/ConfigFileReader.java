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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.exception.PispException;
import pisp.utilities.constants.ErrorMessages;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

/**
 * This class is to read the configuration file of PISP.
 * Which used to store the URL of PISP front-end login page.
 */
public class ConfigFileReader {

    private static Log log = LogFactory.getLog(SessionManager.class);

    /**
     * read the file and get the location of PISP frontend deployment.
     *
     * @return
     */
    public String readFrontendDeploymentURL() {

        String urlToRedirectPSU;
        Properties prop = new Properties();
        Path fileDirectory = FileSystems.getDefault().getPath("configurations/pispFrontEnd.properties");
        try (InputStream input = this.getClass().getClassLoader()
                .getResourceAsStream(fileDirectory.toString())) {

            prop.load(input);

            urlToRedirectPSU = prop.getProperty("redirectURLForPSU");
            return urlToRedirectPSU;

        } catch (IOException | NullPointerException ex) {
            log.error(ErrorMessages.PROPERTIES_FILE_ERROR, ex);
            throw new PispException(ErrorMessages.PROPERTIES_FILE_ERROR);
        }

    }

}
