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

package com.wso2.finance.open.banking.pisp.dao;

import com.wso2.finance.open.banking.pisp.exception.PispException;
import com.wso2.finance.open.banking.pisp.models.DebtorBank;
import com.wso2.finance.open.banking.pisp.utilities.constants.Constants;
import com.wso2.finance.open.banking.pisp.utilities.constants.ErrorMessages;
import com.wso2.finance.open.banking.pisp.utilities.constants.MySQLStatements;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class is to store/retrieve bank related information to/from database.
 */
public class BankManagementDAO {

    private Log log = LogFactory.getLog(BankManagementDAO.class);

    /**
     * Return the information regarding a bank when bankUid is provided.
     *
     * @param bankUId
     * @return
     * @throws PispException
     */
    public DebtorBank retrieveBankInfo(String bankUId) throws PispException {

        Validate.notNull(bankUId, ErrorMessages.PARAMETERS_NULL);
        final String sql = MySQLStatements.GET_A_BANK;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, bankUId);
                preparedStatement.setString(2, Constants.BANK_STATUS_ACTIVE);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    DebtorBank bank = new DebtorBank();
                    if (rs.next()) {
                        bank.setBankUid(rs.getString("BANK_UID"));
                        bank.setIdentification(rs.getString("BANK_IDENTIFICATION_SCHEME"));
                        bank.setSchemeName(rs.getString("BANK_IDENTIFICATION_SCHEME"));
                        bank.setBankName(rs.getString("BANK_NAME"));
                        bank.setSpecForOB(rs.getString("SPEC_FOR_OB"));
                    } else {
                        log.error(ErrorMessages.THE_BANK_DOES_NOT_EXIST);
                    }
                    return bank;
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
     * add a bew bank connection to the database.
     *
     * @param debtorbank
     * @return
     */
    public boolean addNewBankConnection(DebtorBank debtorbank) {

        Validate.notNull(debtorbank, ErrorMessages.PARAMETERS_NULL);
        final String sql = MySQLStatements.ADD_NEW_BANK;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, debtorbank.getBankUid());
                preparedStatement.setString(2, debtorbank.getSchemeName());
                preparedStatement.setString(3, debtorbank.getIdentification());
                preparedStatement.setString(4, debtorbank.getBankName());
                preparedStatement.setString(5, debtorbank.getSpecForOB());
                preparedStatement.setString(6, Constants.BANK_STATUS_ACTIVE);
                preparedStatement.executeUpdate();
                return true;

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
     * To remove any bank, i.e. mark it as de-active.
     *
     * @param bankUid
     * @throws PispException
     */
    public boolean removeActiveBank(String bankUid) throws PispException {

        Validate.notNull(bankUid, ErrorMessages.PARAMETERS_NULL);

        final String update = MySQLStatements.UPDATE_PAYMENT_ID;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(update)) {
                preparedStatement.setString(1, Constants.BANK_STATUS_DEACTIVE);
                preparedStatement.setString(2, bankUid);

                preparedStatement.executeUpdate();
                return true;
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
     * get the whole list of active banks supported by PISP.
     *
     * @return
     */
    public ArrayList getDebtorBankList() {

        final String sql = MySQLStatements.GET_ALL_BANK;
        ArrayList<DebtorBank> bankList = new ArrayList<>();

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, Constants.BANK_STATUS_ACTIVE);

                try (ResultSet rs = preparedStatement.executeQuery()) {

                    while (rs.next()) {
                        DebtorBank debtorBank = new DebtorBank();
                        debtorBank.setBankUid(rs.getString("BANK_UID"));
                        debtorBank.setBankName(rs.getString("BANK_NAME"));
                        bankList.add(debtorBank);
                        if (log.isDebugEnabled()) {
                            log.debug("A bank found" + debtorBank.getBankUid());
                        }
                    }
                    return bankList;
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
}
