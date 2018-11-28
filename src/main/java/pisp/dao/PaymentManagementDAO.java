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
import pisp.models.*;
import pisp.utilities.Utilities;
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
import java.util.UUID;

/**
 * This class is to store and retrieve database related information in/from database.
 */
public class PaymentManagementDAO {

    private Log log = LogFactory.getLog(PaymentManagementDAO.class);


    /*
    ========================================================================================================================================
    Following methods are specific for storing the new payment initiation in PISP database after payment initiation POST request is received
    ========================================================================================================================================
    */

    /**
     * generate all required Ids and add the payment initiation details to relevant database tables.
     * store the payment initiation request in database.
     *
     * @param paymentInitiationRequest
     * @throws PispException
     */
    public String addPaymentInitiation(Payment paymentInitiationRequest) throws PispException {

        Validate.notNull(paymentInitiationRequest, ErrorMessages.PARAMETERS_NULL);
        String paymentInitReqId = this.generateUniquePaymentInitiationId();
        paymentInitiationRequest.setPaymentInitReqId(paymentInitReqId);
        String merchantId = this.getMerchantId(paymentInitiationRequest);

        String creditorAccountUID;
        if (paymentInitiationRequest.getMerchant().getMerchantBank() == null && paymentInitiationRequest.getMerchant().getMerchantAccount() == null) {
            //for single vendors, no merchant info is accepted in the payment initiation request
            creditorAccountUID = this.getCreditorAccountUID(merchantId);
        } else {
            creditorAccountUID = this.getCreditorAccountUID(paymentInitiationRequest.
                    getMerchant().getMerchantBank(), paymentInitiationRequest.getMerchant().getMerchantAccount(), merchantId);
        }
        final String sql = MySQLStatements.ADD_NEW_PAYMENT_INITIATION;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, paymentInitiationRequest.getPaymentInitReqId());
                preparedStatement.setString(2, paymentInitiationRequest.getClientId());
                preparedStatement.setString(3, merchantId);
                preparedStatement.setString(4, paymentInitiationRequest.getPurchaseId());
                preparedStatement.setString(5, paymentInitiationRequest.getInstructedAmountCurrency());
                preparedStatement.setFloat(6, paymentInitiationRequest.getInstructedAmount());
                preparedStatement.setString(7, paymentInitiationRequest.getCustomerIdentification());
                preparedStatement.setString(8, paymentInitiationRequest.getDeliveryAddress());
                preparedStatement.setString(9, creditorAccountUID);
                preparedStatement.setString(10, paymentInitiationRequest.getRedirectURI());
                preparedStatement.setString(11, paymentInitiationRequest.getPaymentStatus());
                preparedStatement.executeUpdate();
                if (log.isDebugEnabled()) {
                    log.debug("Payment Initiation added to database");
                }
                return paymentInitReqId;
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
     * generate a unique id for each payment initiation.
     *
     * @return the created UUID
     */
    private String generateUniquePaymentInitiationId() {

        UUID uuid = UUID.randomUUID();
        String paymentInitReqId = uuid.toString();
        if (log.isDebugEnabled()) {
            log.info("The payment Init Req Id generated for the payment: " + paymentInitReqId);
        }
        return paymentInitReqId;
    }

    /**
     * verify whether a prior entry is available for a particular merchant, and return merchantId.
     * if not found, create a new merchant entry and return the new merchantId.
     *
     * @param paymentInitRequest
     * @return merchantId for the payment initiation.
     */

    private String getMerchantId(Payment paymentInitRequest) {

        String merchantId = null;
        final String sql = MySQLStatements.GET_MERCHANT_ID_IF_EXIST;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, paymentInitRequest.getClientId());
                preparedStatement.setString(2, paymentInitRequest.getMerchant().getMerchantIdentificationByEShop());

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        merchantId = rs.getString("MERCHANT_ID");
                        if (log.isDebugEnabled()) {
                            log.debug("The Merchant exists");
                        }
                    }
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
        if (merchantId == null) {
            UUID uuid = UUID.randomUUID();
            merchantId = uuid.toString();
            final String sql1 = MySQLStatements.ADD_NEW_MERCHANT;
            try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql1)) {
                    preparedStatement.setString(1, merchantId);
                    preparedStatement.setString(2, paymentInitRequest.getClientId());
                    preparedStatement.setString(3, paymentInitRequest.getMerchant().getMerchantIdentificationByEShop());
                    preparedStatement.setString(4, paymentInitRequest.getMerchant().getMerchantName());
                    preparedStatement.setString(5, paymentInitRequest.getMerchant().getMerchantCategoryCode());
                    preparedStatement.executeUpdate();
                    if (log.isDebugEnabled()) {
                        log.debug("New Merchant added");
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
        return merchantId;
    }

    private String getCreditorAccountUID(String merchantId) {

        String creditorAccountUID = null;
        final String sql = MySQLStatements.GET_CREDITOR_ACCOUNT_UID_FOR_SINGLE_VENDOR;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, merchantId);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        creditorAccountUID = rs.getString("CREDITOR_ACCOUNT_UID");
                        if (log.isDebugEnabled()) {
                            log.debug("The creditor account for single vendor e-shop exists");
                        }
                    }
                    return creditorAccountUID;
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

    private String getCreditorAccountUID(Bank creditorBank, BankAccount creditorAccount, String merchantId) {

        String creditorAccountUID = null;
        final String sql = MySQLStatements.GET_CREDITOR_ACCOUNT_UID_IF_EXIST;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, creditorBank.getSchemeName());
                preparedStatement.setString(2, creditorBank.getIdentification());
                preparedStatement.setString(3, creditorAccount.getSchemeName());
                preparedStatement.setString(4, creditorAccount.getIdentification());
                preparedStatement.setString(5, merchantId);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        creditorAccountUID = rs.getString("CREDITOR_ACCOUNT_UID");
                        if (log.isDebugEnabled()) {
                            log.debug("The creditor account exists");
                        }
                    }
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
        if (creditorAccountUID == null) {
            UUID uuid = UUID.randomUUID();
            creditorAccountUID = uuid.toString();
            final String sql1 = MySQLStatements.ADD_NEW_CREDITOR_ACCOUNT;

            try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql1)) {
                    preparedStatement.setString(1, creditorAccountUID);
                    preparedStatement.setString(2, merchantId);
                    preparedStatement.setString(3, creditorBank.getSchemeName());
                    preparedStatement.setString(4, creditorBank.getIdentification());
                    preparedStatement.setString(5, creditorBank.getBankName());
                    preparedStatement.setString(6, creditorAccount.getSchemeName());
                    preparedStatement.setString(7, creditorAccount.getIdentification());
                    preparedStatement.setString(8, creditorAccount.getAccountOwnerName());
                    preparedStatement.executeUpdate();
                    if (log.isDebugEnabled()) {
                        log.debug("New creditor account is added");
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
        return creditorAccountUID;
    }

    /*
    ============================================================================================================
    Following method is specific for updating the payment initiation entry after psu logs to perform the payment
    ============================================================================================================
    */

    /**
     * update the payment initiation with PSU information.
     *
     * @param paymentInitReqId
     * @param psuUsername
     * @return
     */
    public boolean updatePaymentInitiationWithPSU(String paymentInitReqId, String psuUsername) {

        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(psuUsername, ErrorMessages.PARAMETERS_NULL);
        final String add = MySQLStatements.UPDATE_PAYMENT_INITIATION_WITH_PSU;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(add)) {
                preparedStatement.setString(1, psuUsername);
                preparedStatement.setString(2, paymentInitReqId);
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

    /*
    ============================================================================================================
    Following methods are specific for updating the payment initiation entry after psu specifies his debtor bank
    ============================================================================================================
    */

    /**
     * store the debtor bank choice of the PSU in the database.
     * return the response whether the selected bank requires the bank account for payment initiation.
     *
     * @param paymentInitReqId
     * @param bankUid
     * @return
     */
    public PispInternalResponse updatePaymentInitiationWithDebtorBank(String paymentInitReqId, String bankUid) {

        BankManagementDAO bankManagementDAO = new BankManagementDAO();
        DebtorBank debtorBank = bankManagementDAO.retrieveBankInfo(bankUid);
        if (this.updatePaymentTblWithDebtorBank(paymentInitReqId, bankUid, Constants.PAYMENT_STATUS_2)) {
            boolean isDebtorAccountRequired = Utilities.isDebtorAccountRequired(debtorBank.getSpecForOB());
            boolean isPaymentSubmissionRequired = Utilities.isSubmissionRequired(debtorBank.getSpecForOB());
            Boolean[] result = new Boolean[2];
            result[0] = isDebtorAccountRequired;
            result[1] = isPaymentSubmissionRequired;
            return new PispInternalResponse(result, true);
        } else {
            return new PispInternalResponse(ErrorMessages.ERROR_OCCURRED, false);
        }

    }

    private boolean updatePaymentTblWithDebtorBank(String paymentInitReqId, String bankUid, String paymentStatus) {

        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(bankUid, ErrorMessages.PARAMETERS_NULL);

        final String add = MySQLStatements.UPDATE_PAYMENT_INITIATION_WITH_DEBTOR_BANK_UID;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(add)) {
                preparedStatement.setString(1, bankUid);
                preparedStatement.setString(2, paymentStatus);
                preparedStatement.setString(3, paymentInitReqId);
                preparedStatement.executeUpdate();
                if (log.isDebugEnabled()) {
                    log.debug("Payment updated with Debtor Bank");
                }
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

    /*
    ===============================================================================================================
    Following methods are specific for updating the payment initiation entry after psu specifies his debtor account
    ===============================================================================================================
    */

    /**
     * update the debtor account details if and only if the PSU has specified his account details.
     *
     * @param paymentInitReqId
     * @param bankAccount
     * @return
     */
    public PispInternalResponse updatePaymentInitiationWithDebtorAccount(String paymentInitReqId, BankAccount bankAccount) {

        String debtorAccountUID = this.getDebtorAccountUID(bankAccount);
        this.updatePaymentInitiationWithDebtorAccount(paymentInitReqId, debtorAccountUID, Constants.PAYMENT_STATUS_3);
        return new PispInternalResponse(Constants.ADDED_BANK_ACCOUNT_UID, true);

    }

    /**
     * verify whether a prior entry is available for a particular debtor bankAccount, and return debtorBankUID.
     * if not found, create a new debtorBankAccount entry and return the new debtorBankUID.
     *
     * @param bankAccount
     * @throws PispException
     */
    private String getDebtorAccountUID(BankAccount bankAccount) throws PispException {

        Validate.notNull(bankAccount, ErrorMessages.PARAMETERS_NULL);
        String debtorAccountUID = null;
        final String sql = MySQLStatements.GET_DEBTOR_ACCOUNT_UID_IF_EXIST;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, bankAccount.getSchemeName());
                preparedStatement.setString(2, bankAccount.getIdentification());

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        debtorAccountUID = rs.getString("DEBTOR_ACCOUNT_UID");
                        if (log.isDebugEnabled()) {
                            log.debug("The debtor account entry exists");
                        }
                    }
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

        if (debtorAccountUID == null) {
            UUID uuid = UUID.randomUUID();
            debtorAccountUID = uuid.toString();
            final String sql1 = MySQLStatements.ADD_NEW_DEBTOR_ACCOUNT;

            try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql1)) {
                    preparedStatement.setString(1, debtorAccountUID);
                    preparedStatement.setString(2, bankAccount.getSchemeName());
                    preparedStatement.setString(3, bankAccount.getIdentification());
                    preparedStatement.setString(4, bankAccount.getAccountOwnerName());
                    preparedStatement.executeUpdate();
                    if (log.isDebugEnabled()) {
                        log.debug("New debtor account is added");
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
        return debtorAccountUID;
    }

    /**
     * update the payment entry in the payment table with relevant debtorAccountUID.
     *
     * @param paymentInitReqId
     * @param debtorAccountUID
     * @return
     */
    private boolean updatePaymentInitiationWithDebtorAccount(String paymentInitReqId, String debtorAccountUID, String paymentStatus) {

        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(debtorAccountUID, ErrorMessages.PARAMETERS_NULL);

        final String add = MySQLStatements.UPDATE_PAYMENT_INITIATION_WITH_DEBTOR_ACCOUNT_UID;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(add)) {
                preparedStatement.setString(1, debtorAccountUID);
                preparedStatement.setString(2, paymentStatus);
                preparedStatement.setString(3, paymentInitReqId);
                if (log.isDebugEnabled()) {
                    log.debug("Payment updated with debtor Account");
                }
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


    /*
    =====================================================================================
    Following methods are specific for retrieving payment initiation data from db tables
    =====================================================================================
    */

    /**
     * retrieve a selected payment initiation request.
     *
     * @param paymentInitReqId
     * @return
     * @throws PispException
     */
    public Payment retrievePayment(String paymentInitReqId) throws PispException {

        Payment payment = new Payment();
        final String sql = MySQLStatements.GET_PAYMENT_INITIATION;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, paymentInitReqId);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        payment.setPaymentInitReqId(rs.getString("PAYMENT_INIT_REQ_ID"));
                        payment.setClientId(rs.getString("CLIENT_ID"));
                        payment.setPurchaseId(rs.getString("PURCHASE_ID"));

                        payment.setInstructedAmountCurrency(rs.getString("CURRENCY"));
                        payment.setInstructedAmount(Float.parseFloat(rs.getString("PAYMENT_AMOUNT")));
                        payment.setCustomerIdentification(rs.getString("CUSTOMER_IDENTIFICATION_BY_ESHOP"));
                        payment.setDeliveryAddress(rs.getString("DELIVERY_ADDRESS_JSON"));
                        payment.setPsuUsername(rs.getString("PSU_USERNAME"));

                        String merchantId = rs.getString("MERCHANT_ID");
                        String creditorAccountUID = rs.getString("CREDITOR_ACCOUNT_UID");

                        String debtorBankUID = rs.getString("DEBTOR_BANK_UID");
                        String debtorAccountUID = rs.getString("DEBTOR_ACCOUNT_UID");

                        payment.setPaymentId(rs.getString("PAYMENT_ID"));
                        payment.setPaymentSubmissionId(rs.getString("PAYMENT_SUB_ID"));
                        payment.setPaymentStatus(rs.getString("PAYMENT_STATUS"));
                        payment.setRedirectURI(rs.getString("REDIRECT_URI"));

                        Merchant merchant = retrieveMerchantInfo(merchantId);

                        Object[] creditorBankData = this.retrieveCreditorBankAndAccount(creditorAccountUID);
                        merchant.setMerchantBank((Bank) creditorBankData[0]);
                        merchant.setMerchantAccount((BankAccount) creditorBankData[1]);

                        payment.setMerchant(merchant);

                        DebtorBank debtorBank = this.retrieveDebtorBankInfo(debtorBankUID);
                        payment.setCustomerBank(debtorBank);

                        if (debtorAccountUID != null) {
                            BankAccount debtorAccount = this.retrieveDebtorAccount(debtorAccountUID);
                            payment.setCustomerBankAccount(debtorAccount);
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("The requested payment initiation exists");
                        }
                        return payment;
                    } else {
                        payment.setErrorStatus(true);
                        payment.setErrorMessage(ErrorMessages.PAYMENT_INITIATION_NOT_FOUND);
                        return payment;
                    }
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

    private Merchant retrieveMerchantInfo(String merchantId) {

        Merchant merchant = new Merchant();
        final String sql = MySQLStatements.GET_MERCHANT;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, merchantId);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        merchant.setMerchantIdentificationByEShop(rs.getString("MERCHANT_IDENTIFICATION_BY_ESHOP"));
                        merchant.setMerchantName(rs.getString("MERCHANT_NAME"));
                        merchant.setMerchantCategoryCode(rs.getString("MERCHANT_CATEGORY_CODE"));
                        if (log.isDebugEnabled()) {
                            log.debug("Retrieved merchant details of the payment");
                        }
                        return merchant;
                    } else {
                        log.error(ErrorMessages.ERROR_MERCHANT_RETRIEVAL);
                        return null;
                    }
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

    private Object[] retrieveCreditorBankAndAccount(String creditorAccountUID) {

        Bank creditorBank = new Bank();
        BankAccount creditorAccount = new BankAccount();

        final String sql = MySQLStatements.GET_CREDITOR_BANK_DETAILS;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, creditorAccountUID);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        creditorBank.setSchemeName(rs.getString("BANK_IDENTIFICATION_SCHEME"));
                        creditorBank.setIdentification(rs.getString("BANK_IDENTIFICATION_NO"));
                        creditorBank.setBankName(rs.getString("BANK_NAME"));
                        creditorAccount.setSchemeName(rs.getString("ACCOUNT_IDENTIFICATION_SCHEME"));
                        creditorAccount.setIdentification(rs.getString("ACCOUNT_IDENTIFICATION_NO"));
                        creditorAccount.setAccountOwnerName(rs.getString("ACCOUNT_OWNER_NAME"));
                        if (log.isDebugEnabled()) {
                            log.debug("Retrieved Creditor Bank & Account details of the payment");
                        }
                        return new Object[]{creditorBank, creditorAccount};
                    } else {
                        log.info(ErrorMessages.ERROR_CREDITOR_BANK_RETRIEVAL);
                        return new Object[]{null, null};
                    }
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

    private BankAccount retrieveDebtorAccount(String debtorAccountUID) {

        BankAccount debtorAccount = new BankAccount();
        log.info("The debtor account UID to retrieve" + debtorAccountUID);

        final String sql = MySQLStatements.GET_DEBTOR_ACCOUNT_DETAILS;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, debtorAccountUID);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        debtorAccount.setSchemeName(rs.getString("ACCOUNT_IDENTIFICATION_SCHEME"));
                        debtorAccount.setIdentification(rs.getString("ACCOUNT_IDENTIFICATION_NO"));
                        debtorAccount.setAccountOwnerName(rs.getString("ACCOUNT_OWNER_NAME"));
                        if (log.isDebugEnabled()) {
                            log.debug("Retrieved debtor Bank UID & Account details of the payment");
                        }
                        return debtorAccount;
                    } else {
                        log.error(ErrorMessages.ERROR_DEBTOR_BANK_RETRIEVAL);
                        return null;
                    }
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

    private DebtorBank retrieveDebtorBankInfo(String bankUid) {

        DebtorBank debtorbank = new DebtorBank();
        final String sql = MySQLStatements.GET_A_BANK;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, bankUid);
                preparedStatement.setString(2, Constants.BANK_STATUS_ACTIVE);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        debtorbank.setBankUid(rs.getString("BANK_UID"));
                        debtorbank.setSchemeName(rs.getString("BANK_IDENTIFICATION_SCHEME"));
                        debtorbank.setIdentification(rs.getString("BANK_IDENTIFICATION_NO"));
                        debtorbank.setBankName(rs.getString("BANK_NAME"));
                        debtorbank.setSpecForOB(rs.getString("SPEC_FOR_OB"));
                        if (log.isDebugEnabled()) {
                            log.debug("Retrieved Debtor bank details");
                        }
                        return debtorbank;
                    } else {
                        log.error(ErrorMessages.ERROR_BANK_INFO_RETRIEVAL);
                        return null;
                    }
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

    /*
    ===================================================================================================
    Following methods are specific for handling related data after Invoking Payment API of Debtor Bank
    ===================================================================================================
    */

    /**
     * save payment Initiation id in the database.
     *
     * @param paymentInitiationId
     * @param paymentInitReqId
     * @throws PispException
     */
    public void saveInitiationIds(String paymentInitiationId, String paymentInitReqId) throws PispException {

        Validate.notNull(paymentInitiationId, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);

        final String insert = MySQLStatements.UPDATE_PAYMENT_ID;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
                preparedStatement.setString(1, paymentInitiationId);
                preparedStatement.setString(2, Constants.PAYMENT_STATUS_4);
                preparedStatement.setString(3, paymentInitReqId);

                preparedStatement.executeUpdate();
                if (log.isDebugEnabled()) {
                    log.debug("Successfully stored the payment initiation id");
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
     * save payment Submission id in the database.
     *
     * @param paymentSubmissionId
     * @param paymentInitReqId
     * @throws PispException
     */
    public void saveSubmissionIds(String paymentSubmissionId, String paymentInitReqId) throws PispException {

        Validate.notNull(paymentSubmissionId, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);

        final String insert = MySQLStatements.UPDATE_PAYMENT_SUBMISSION_ID;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
                preparedStatement.setString(1, paymentSubmissionId);
                preparedStatement.setString(2, Constants.PAYMENT_STATUS_6);
                preparedStatement.setString(3, paymentInitReqId);
                preparedStatement.executeUpdate();
                if (log.isDebugEnabled()) {
                    log.debug("Successfully stored the payment submission id");
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
     * update the payment as completed when the response is received from bank as payment completed.
     *
     * @param paymentInitReqId
     */
    public void updatePaymentAsCompleted(String paymentInitReqId) {

        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);
        final String add = MySQLStatements.UPDATE_PAYMENT_INITIATION_AS_COMPLETED;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(add)) {
                preparedStatement.setString(1, Constants.PAYMENT_STATUS_7);
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
