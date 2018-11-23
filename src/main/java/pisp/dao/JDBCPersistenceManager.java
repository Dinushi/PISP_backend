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

package pisp.dao;

import pisp.exception.PispException;
import pisp.utilities.constants.ErrorMessages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCPersistenceManager {


        private static final JDBCPersistenceManager instance = new JDBCPersistenceManager();
        private Log log = LogFactory.getLog(JDBCPersistenceManager.class);

        private JDBCPersistenceManager() {
        }

        static JDBCPersistenceManager getInstance() {
            return instance;
        }

        Connection getDBConnection() throws PispException {
            Properties prop = new Properties();
            Connection connect;
            Path path = FileSystems.getDefault().getPath("db/db.properties");
            try (InputStream input = this.getClass().getClassLoader()
                    .getResourceAsStream(path.toString())) {
                prop.load(input);
                Class.forName(prop.getProperty("mysql.driver"));
                connect = DriverManager.getConnection(prop.getProperty("mysql.url"),
                        prop.getProperty("mysql.username"), prop.getProperty("mysql.password"));
            } catch (IOException | ClassNotFoundException | SQLException e) {
                log.error("DB Connection refused : ", e);
                throw new PispException(ErrorMessages.ERROR_OCCURRED);
            }
            return connect;
        }
}

