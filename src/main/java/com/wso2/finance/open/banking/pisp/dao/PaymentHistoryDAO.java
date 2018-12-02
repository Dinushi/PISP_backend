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
package com.wso2.finance.open.banking.pisp.dao;

import com.wso2.finance.open.banking.pisp.exception.PispException;
import com.wso2.finance.open.banking.pisp.models.Payment;
import com.wso2.finance.open.banking.pisp.utilities.constants.Constants;
import com.wso2.finance.open.banking.pisp.utilities.constants.ErrorMessages;
import com.wso2.finance.open.banking.pisp.utilities.constants.MySQLStatements;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class is to retrieve payment history of a E-shop.
 */
public class PaymentHistoryDAO {

    private Log log = LogFactory.getLog(PaymentHistoryDAO.class);

    /**
     * Retrieve payments history of a user.
     *
     * @param username
     * @param startDate
     * @param endDate
     */
    public ArrayList<Payment> filterPayments(String username, String startDate, String endDate) {

        ArrayList<String> paymentIdsList;
        ArrayList<Payment> paymentsList = new ArrayList<>();
        paymentIdsList = this.filterPaymentsCompleted(username, startDate, endDate);
        PaymentManagementDAO paymentManagementDAO = new PaymentManagementDAO();
        Iterator<String> paymentsIterator = paymentIdsList.iterator();
        while (paymentsIterator.hasNext()) {
            Payment payment = paymentManagementDAO.retrievePayment(paymentsIterator.next());
            paymentsList.add(payment);
        }
        return paymentsList;
    }

    private ArrayList<String> filterPaymentsCompleted(String username, String
            startDate, String endDate) {

        ArrayList<String> paymentList = new ArrayList<>();
        final String sql = MySQLStatements.FILTER_PAYMENTS;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, Constants.PAYMENT_STATUS_7);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, startDate);
                preparedStatement.setString(4, endDate);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String paymentInitReqId = rs.getString("PAYMENT_INIT_REQ_ID");
                        paymentList.add(paymentInitReqId);
                    }
                    return paymentList;
                } catch (SQLException e) {
                    log.info(ErrorMessages.DB_PARSE_ERROR);
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
}
