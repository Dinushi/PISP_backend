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
import pisp.models.AccessToken;
import pisp.models.PispInternalResponse;
import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;
import pisp.utilities.constants.MySQLStatements;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is to store and retrieve application tokens and user access token to/from database.
 */
public class AccessTokenManagementDAO {

    private Log log = LogFactory.getLog(AccessTokenManagementDAO.class);

    /**
     * Retrieve the application token issued last for the bank. That is the most latest or active.
     * token is returned.
     *
     * @param bankID the bank which issues the token.
     * @return An Aggregator Response with the token embedded.
     */
    public PispInternalResponse getLastApplicationToken(String bankID) {

        Validate.notNull(bankID, ErrorMessages.PARAMETERS_NULL);

        final String sql = MySQLStatements.GET_APPLICATION_TOKEN;
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DB_DATE_FORMAT);

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, bankID);

                try (ResultSet rs = preparedStatement.executeQuery()) {

                    if (rs.next()) {
                        return new PispInternalResponse(new AccessToken(rs.getString("token"),
                                formatter.parse(rs.getString("valid_till"))), true);
                    } else {
                        return new PispInternalResponse(ErrorMessages.NO_RECORD_FOUND, false);
                    }
                } catch (SQLException e) {
                    log.error(ErrorMessages.DB_PARSE_ERROR, e);
                    throw new PispException(ErrorMessages.DB_PARSE_ERROR);
                } catch (ParseException e) {
                    log.error("Unable to parse date in access token from DB", e);
                    throw new PispException(ErrorMessages.UNABLE_TO_PASS_TOKEN_FROM_DB);
                }
            } catch (SQLException e) {
                log.error(ErrorMessages.SQL_QUERY_PREPARATION_ERROR, e);
                throw new PispException(ErrorMessages.SQL_QUERY_PREPARATION_ERROR);
            }
        } catch (SQLException e) {
            log.error(ErrorMessages.DB_CLOSE_ERROR, e);
            throw new PispException(ErrorMessages.DB_CLOSE_ERROR);
        }
    }

    /**
     * Save application token to the database.
     *
     * @param bankID    The bank which issues the token.
     * @param token     The application token.
     * @param validTill The time until while token is valid.
     * @throws PispException If DB saving errors.
     */
    public void saveApplicationToken(String bankID, String token, Date validTill) throws PispException {

        Validate.notNull(token, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(bankID, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(validTill, ErrorMessages.PARAMETERS_NULL);

        final String insert = MySQLStatements.ADD_A_NEW_APPLICATION_TOKEN;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {

                SimpleDateFormat sdf =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                preparedStatement.setString(1, bankID);
                preparedStatement.setString(2, token);
                preparedStatement.setString(3, sdf.format(validTill.getTime()));
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                log.error(ErrorMessages.SQL_QUERY_PREPARATION_ERROR, e);
                throw new PispException(ErrorMessages.SQL_QUERY_PREPARATION_ERROR);
            }
        } catch (SQLException e) {
            log.error(ErrorMessages.DB_CLOSE_ERROR, e);
            throw new PispException(ErrorMessages.DB_CLOSE_ERROR);
        }
    }

    /**
     * Save the authorization code received after PSU authorization of the payment.
     *
     * @param authorizationCode the code to save.
     * @param paymentInitReqId  the id which identifies a payment uniquely.
     * @throws PispException If database connection errors.
     */
    public void saveAuthCode(String authorizationCode, String paymentInitReqId)
            throws PispException {

        Validate.notNull(authorizationCode, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);

        final String insert = MySQLStatements.UPDATE_PAYMENT_WITH_AUTH_CODE;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
                preparedStatement.setString(1, authorizationCode);
                preparedStatement.setString(2, paymentInitReqId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                log.error(ErrorMessages.SQL_QUERY_PREPARATION_ERROR, e);
                throw new PispException(ErrorMessages.SQL_QUERY_PREPARATION_ERROR);
            }
        } catch (SQLException e) {
            log.error(ErrorMessages.DB_CLOSE_ERROR, e);
            throw new PispException(ErrorMessages.DB_CLOSE_ERROR);
        }
    }

}
