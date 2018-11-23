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
package pisp.dao;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pisp.exception.PispException;
import pisp.models.PispInternalResponse;
import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;
import pisp.utilities.constants.MySQLStatements;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class SessionTokenManagementDAO {

    private Log log = LogFactory.getLog(SessionTokenManagementDAO.class);



   /*
    ===================================================================================
    Methods in this section is used during the session management for E-shop user tasks
    ===================================================================================
    */


    /**
     * Create a unique Session ID for E-shop and store in DB.
     * @param username The username token is bound with.
     * @return The created sessionID
     */
    public String generateSessionTokenForEShop(String username) {
        Random random = new SecureRandom();
        String token = new BigInteger(130, random).toString(32);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 1);
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final String sql = MySQLStatements.ADD_SESSION_TOKEN_FOR_ESHOP;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, token);
                preparedStatement.setString(3, sdf.format(cal.getTime()));
                preparedStatement.setString(4, token);
                preparedStatement.setString(5, sdf.format(cal.getTime()));

                preparedStatement.executeUpdate();

                return token;

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
     * get session Token of the logged in e-shop user and verify that it is not expired
     *
     * @param username
     * @return
     */
    public PispInternalResponse getSessionTokenForLoggedInEShopUser(String username) {

        Validate.notNull(username, ErrorMessages.PARAMETERS_NULL);

        final String sql = MySQLStatements.GET_E_SHOP_USER_SESSION;
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DB_DATE_FORMAT);
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        String sessionToken=rs.getString("SESSION_KEY");
                        log.info("found session token:"+sessionToken);
                        try {
                            Date validTill=formatter.parse(rs.getString("EXPIRY_TIME"));
                            if(!this.isTokenExpired(validTill)){
                                log.info("session token not expired");
                                return new PispInternalResponse(sessionToken,true);
                            }else{
                                return new PispInternalResponse(ErrorMessages.SESSION_TOKEN_EXPIRED,false);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            throw new PispException(ErrorMessages.DB_PARSE_ERROR);
                        }
                    } else {
                        return new PispInternalResponse(ErrorMessages.NO_SESSION_FOUND,false);
                    }
                } catch (SQLException e) {
                    log.error(ErrorMessages.DB_PARSE_ERROR, e);
                    throw new PispException(ErrorMessages.DB_PARSE_ERROR);
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


    /*
    ===========================================================================
    Methods in this section is used during the session management for PSU tasks
    ===========================================================================
    */


    /**
     * Create a unique Session ID for PSU and store in DB.
     * @param username The username token is bound with.
     * @param paymentInitReqId The paymentInitiation bound with the PSU login.
     * @return The created sessionID
     */
    public String generateSessionTokenForPSU(String username, String paymentInitReqId) {
        Random random = new SecureRandom();
        String token = new BigInteger(130, random).toString(32);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 1);
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final String sql = MySQLStatements.ADD_SESSION_TOKEN_FOR_PSU;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, token);
                preparedStatement.setString(3, paymentInitReqId);
                preparedStatement.setString(4, sdf.format(cal.getTime()));
                preparedStatement.setString(5, token);
                preparedStatement.setString(6, paymentInitReqId);
                preparedStatement.setString(7, sdf.format(cal.getTime()));

                preparedStatement.executeUpdate();

                return token;

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
     * get session Token of the logged in psu and verify that it is not expired
     *
     * @param username
     * @return
     */
    public PispInternalResponse getSessionTokenForPsu(String username) {

        Validate.notNull(username, ErrorMessages.PARAMETERS_NULL);

        final String sql = MySQLStatements.GET_PSU_SESSION;
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DB_DATE_FORMAT);
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);


                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        String sessionToken=rs.getString("SESSION_KEY");
                        String paymentInitReqId=rs.getString("PAYMENT_INIT_REQ_ID");
                        log.info("Payment-init-req ID at DAO :"+paymentInitReqId);
                        log.info("found session token:"+sessionToken);
                        try {
                            Date validTill=formatter.parse(rs.getString("EXPIRY_TIME"));
                            if(!this.isTokenExpired(validTill)){
                                log.info("session token not expired");
                                String[] sessionDetails=new String[2];
                                sessionDetails[0]=sessionToken;
                                sessionDetails[1]=paymentInitReqId;
                                return new PispInternalResponse(sessionDetails,true);
                            }else{
                                return new PispInternalResponse(ErrorMessages.SESSION_TOKEN_EXPIRED,false);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            throw new PispException(ErrorMessages.DB_PARSE_ERROR);
                        }
                    } else {
                        return new PispInternalResponse(ErrorMessages.NO_SESSION_FOUND,false);
                    }
                } catch (SQLException e) {
                    log.error(ErrorMessages.DB_PARSE_ERROR, e);
                    throw new PispException(ErrorMessages.DB_PARSE_ERROR);
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
     * check whether the session token is valid
     * @param validTill
     * @return
     */
    private boolean isTokenExpired( Date validTill) {
        return System.currentTimeMillis() > validTill.getTime();
    }

}

