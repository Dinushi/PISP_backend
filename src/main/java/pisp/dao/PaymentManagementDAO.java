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

public class PaymentManagementDAO {

    private Log log = LogFactory.getLog(PaymentManagementDAO.class);


    /*
    ========================================================================================================================================
    Following methods are specific for storing the new payment initiation in PISP database after payment initiation POST request is received
    ========================================================================================================================================
    */

    /**
     *generate all required Ids and add the payment initiation details to relevant database tables
     * @param paymentInitiationRequest
     * @throws PispException
     * store the payment initiation request in database
     */
    public boolean addPaymentInitiation(Payment paymentInitiationRequest) throws PispException {

        Validate.notNull(paymentInitiationRequest, ErrorMessages.PARAMETERS_NULL);

        String merchantId = this.getMerchantId(paymentInitiationRequest);

        String creditorAccountUID;
        if(paymentInitiationRequest.getMerchant().getMerchantBank()==null && paymentInitiationRequest.getMerchantBankAccount()==null){
            creditorAccountUID=this.getCreditorAccountUID(merchantId);//for single vendors, no merchant info is accepted in the payment initiation request
        }else{
            creditorAccountUID = this.getCreditorAccountUID(paymentInitiationRequest.getMerchantBank(),paymentInitiationRequest.getMerchantBankAccount(),merchantId);
        }


        final String sql = MySQLStatements.ADD_NEW_PAYMENT_INITIATION;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, paymentInitiationRequest.getPaymentInitReqId());
                preparedStatement.setString(2, paymentInitiationRequest.getClientId());
                preparedStatement.setString(3, merchantId);
                preparedStatement.setString(4, paymentInitiationRequest.getPurchaseId());
                preparedStatement.setString(5, paymentInitiationRequest.getInstructedAmountCurrency());
                preparedStatement.setFloat( 6, paymentInitiationRequest.getInstructedAmount());
                preparedStatement.setString(7, paymentInitiationRequest.getCustomerIdentification());
                preparedStatement.setString(8, paymentInitiationRequest.getDeliveryAddress());
                preparedStatement.setString(9, creditorAccountUID);
                preparedStatement.setString(10,paymentInitiationRequest.getRedirectURI());
                preparedStatement.setString(11,paymentInitiationRequest.getPaymentStatus());

                preparedStatement.executeUpdate();

                log.info("Payment Initiation added to database");
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
     * verify whether a prior entry is available for a particular merchant, and return merchantId
     * if not found, create a new merchant entry and return the new merchantId
     *
     * @param paymentInitRequest
     * @return merchantId for the payment initiation
     */

    private String getMerchantId(Payment paymentInitRequest){
        String merchantId=null;

        final String sql = MySQLStatements.GET_MERCHANT_ID_IF_EXIST;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, paymentInitRequest.getClientId());
                preparedStatement.setString(2, paymentInitRequest.getMerchant().getMerchantIdentificationByEshop());


                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        merchantId = rs.getString("MERCHANT_ID");
                        log.info("The Merchant exists");
                    } else {
                        log.info("The Merchant does not exist");
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

        if(merchantId==null){
            UUID uuid = UUID.randomUUID();
            merchantId = uuid.toString();
            final String sql1 = MySQLStatements.ADD_NEW_MERCHANT;

            try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql1)) {
                    preparedStatement.setString(1, merchantId);
                    preparedStatement.setString(2, paymentInitRequest.getClientId());
                    preparedStatement.setString(3, paymentInitRequest.getMerchant().getMerchantIdentificationByEshop());
                    preparedStatement.setString(4, paymentInitRequest.getMerchant().getMerchantName());
                    preparedStatement.setString(5, paymentInitRequest.getMerchant().getMerchantCategoryCode());
                    preparedStatement.executeUpdate();

                    log.info("New Merchant added");

                } catch (SQLException e) {
                    log.error(ErrorMessages.SQL_QUERY_PREPARATION_ERROR, e);
                    throw new PispException(ErrorMessages.SQL_QUERY_PREPARATION_ERROR);
                }
            } catch (SQLException e) {
                log.error(ErrorMessages.DB_CLOSE_ERROR, e);
                throw new PispException(ErrorMessages.DB_CLOSE_ERROR);
            }

        }
        return  merchantId;

    }


    private String getCreditorAccountUID(String merchantId){

        String creditorAccountUID=null;

        final String sql = MySQLStatements.GET_CREDITOR_ACCOUNT_UID_FOR_SINGLE_VENDOR;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, merchantId);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        creditorAccountUID = rs.getString("CREDITOR_ACCOUNT_UID");
                        log.info("The creditor account for single vendor e-shop exists");
                    } else {
                        log.info("The creditor account for single vendor e-shop does not exist");
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

    private String getCreditorAccountUID(Bank creditorBank , BankAccount creditorAccount ,String merchantId) {

        String creditorAccountUID=null;

        final String sql = MySQLStatements.GET_CREDITOR_ACCOUNT_UID_IF_EXIST;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, creditorBank.getSchemeName() );
                preparedStatement.setString(2, creditorBank.getIdentification());
                preparedStatement.setString(3, creditorAccount.getSchemeName());
                preparedStatement.setString(4, creditorAccount.getIdentification());
                preparedStatement.setString(5, merchantId);


                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        creditorAccountUID = rs.getString("CREDITOR_ACCOUNT_UID");
                        log.info("The creditor account exists");
                    } else {
                        log.info("The creditor account does not exist");
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

        if(creditorAccountUID==null){
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
                    preparedStatement.setString(6, creditorAccount.getSchemeName() );
                    preparedStatement.setString(7, creditorAccount.getIdentification());
                    preparedStatement.setString(8, creditorAccount.getAccountOwnerName());
                    preparedStatement.executeUpdate();

                    log.info("New creditor account is added");

                } catch (SQLException e) {
                    log.error(ErrorMessages.SQL_QUERY_PREPARATION_ERROR, e);
                    throw new PispException(ErrorMessages.SQL_QUERY_PREPARATION_ERROR);
                }
            } catch (SQLException e) {
                log.error(ErrorMessages.DB_CLOSE_ERROR, e);
                throw new PispException(ErrorMessages.DB_CLOSE_ERROR);
            }

        }
        return  creditorAccountUID;
    }

    /*
    ============================================================================================================
    Following method is specific for updating the payment initiation entry after psu logs to perform the payment
    ============================================================================================================
    */

    /**
     * upadte the payment initiation with PSU info
     * @param paymentInitReqId
     * @param psu_username
     * @return
     */
    public boolean updatePaymentInitiationWithPSU(String paymentInitReqId,String psu_username){
        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(psu_username, ErrorMessages.PARAMETERS_NULL);

        final String Add = MySQLStatements.UPDATE_PAYMENT_INITIATION_WITH_PSU;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(Add)) {
                preparedStatement.setString(1, psu_username);
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
    ===========================================================================================================================
    Following methods are specific for updating the payment initiation entry after psu specifies his debtor bank and account
    ===========================================================================================================================
    */

    /**
     * store the debtor bank choice of the PSU in the database
     * @param paymentInitReqId
     * @param bank_uid
     * @return
     */
    public PispInternalResponse updatePaymentInitiationWithDebtorBank(String paymentInitReqId,String bank_uid){

        BankAccount bankAccount=new BankAccount();

        String debtorAccountUID=this.getDebtorAccountUID(bank_uid, bankAccount); //passes an bank account object with null values set to its variables.

        BankManagementDAO bankManagementDAO=new BankManagementDAO();
        DebtorBank debtorBank= bankManagementDAO.retrieveBankInfo(bank_uid);
        if( this.updatePaymentInitiationData(paymentInitReqId,debtorAccountUID, Constants.PAYMENT_STATUS_2)){
            boolean isDebtorAccountRequired= Utilities.isDebtorAccountRequired(debtorBank.getSpecForOB());
            boolean isPaymentSubmissionRequired= Utilities.isSubmissionRequired(debtorBank.getSpecForOB());

            log.info("isDebtorBankRequired , isPaymentsubmissionRequired :"+ isDebtorAccountRequired +" , "+isPaymentSubmissionRequired);
            Boolean[] result= new Boolean[2];
            result[0]=isDebtorAccountRequired;
            result[1]=isPaymentSubmissionRequired;

            return new PispInternalResponse(result,true);

            /*
            if(Utilities.isDebtorAccountRequired(debtorBank.getSpecForOB())){
                log.info("The bank doesn't require account data");
                return new PispInternalResponse(Constants.DEBTOR_ACC_REQUIRED,true);

            }else{
                log.info("The bank doesn't require account data");
                return new PispInternalResponse(Constants.DEBTOR_ACC_NOT_REQUIRED,true);
            }
            */

        }else{
            return new PispInternalResponse(ErrorMessages.ERROR_OCCURRED,false);
        }

    }

    /**
     * update the debtor account details if and only if the PSU has specified his account details
     * @param paymentInitReqId
     * @param bankAccount
     * @return
     */

    public PispInternalResponse updatePaymentInitiationWithDebtorAccount(String paymentInitReqId,BankAccount bankAccount){

        String existing_debtor_account_uid= this.getExistingDebtorBankUid(paymentInitReqId);
        String bankUid = this.getActualBankUID(existing_debtor_account_uid);
        String debtorAccountUID=this.getDebtorAccountUID(bankUid,bankAccount);
        this.updatePaymentInitiationData(paymentInitReqId,debtorAccountUID, Constants.PAYMENT_STATUS_3);
        return new PispInternalResponse("BankAccountUID updated",true);

    }


    /**
     * verify whether a prior entry is available for a particular debtor bankAccount, and return debtorBankUID
     * if not found, create a new debtorBankAccount entry and return the new debtorBankUID
     * @param bank_uid
     * @throws PispException
     */
    private String  getDebtorAccountUID(String bank_uid ,BankAccount bankAccount) throws PispException {

        Validate.notNull(bank_uid, ErrorMessages.PARAMETERS_NULL);

        String debtorAccountUID=null;

        final String sql = MySQLStatements.GET_DEBTOR_ACCOUNT_UID_IF_EXIST;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, bank_uid);
                preparedStatement.setString(2, bankAccount.getSchemeName());
                preparedStatement.setString(3, bankAccount.getIdentification());

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        debtorAccountUID = rs.getString("DEBTOR_ACCOUNT_UID");
                        log.info("The debtor bank entry exists");
                    } else {
                        log.info("The debtor bank entry does not exist");
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

        if(debtorAccountUID==null){
            UUID uuid = UUID.randomUUID();
            debtorAccountUID = uuid.toString();
            final String sql1 = MySQLStatements.ADD_NEW_DEBTOR_ACCOUNT;

            try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql1)) {
                    preparedStatement.setString(1, debtorAccountUID);
                    preparedStatement.setString(2, bank_uid);
                    preparedStatement.setString(3, bankAccount.getSchemeName());
                    preparedStatement.setString(4, bankAccount.getIdentification());
                    preparedStatement.setString(5, bankAccount.getAccountOwnerName());
                    preparedStatement.executeUpdate();

                    log.info("New debtor account is added");

                } catch (SQLException e) {
                    log.error(ErrorMessages.SQL_QUERY_PREPARATION_ERROR, e);
                    throw new PispException(ErrorMessages.SQL_QUERY_PREPARATION_ERROR);
                }
            } catch (SQLException e) {
                log.error(ErrorMessages.DB_CLOSE_ERROR, e);
                throw new PispException(ErrorMessages.DB_CLOSE_ERROR);
            }

        }
        return  debtorAccountUID;
    }


    private String getExistingDebtorBankUid(String paymentInitReqId){
        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);

        String debtorAccountUID=null;

        final String sql = MySQLStatements.GET_AVAILABLE_DEBTOR_ACCOUNT_UID;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, paymentInitReqId);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        debtorAccountUID = rs.getString("DEBTOR_ACCOUNT_UID");
                        log.info("The debtor bank account UID exists");
                    } else {
                        log.info("The debtor bank account UID does not exist");
                    }
                    return debtorAccountUID;
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

    private String getActualBankUID(String debtorAccountUid){
        Validate.notNull(debtorAccountUid, ErrorMessages.PARAMETERS_NULL);

        String bankUID=null;

        final String sql = MySQLStatements.GET_BANK_UID_RELEVANT_TO_DEBTOR_ACCOUNT_UID;
        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, debtorAccountUid);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        bankUID = rs.getString("BANK_UID");
                        log.info("The entry exists");

                    } else {
                        log.info("The entry does not exist");
                    }
                    return bankUID;
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



    /**
     * update the payment entry in the payment table with relevant debtorAccountUID
     * @param paymentInitReqId
     * @param debtorAccountUID
     * @return
     */
    private boolean updatePaymentInitiationData(String paymentInitReqId , String debtorAccountUID, String paymentStatus){
        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(debtorAccountUID, ErrorMessages.PARAMETERS_NULL);

        final String Add = MySQLStatements.UPDATE_PAYMENT_INITIATION_WITH_DEBTOR_ACCOUNT_UID;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(Add)) {
                preparedStatement.setString(1, debtorAccountUID);
                preparedStatement.setString(2, paymentStatus);
                preparedStatement.setString(3, paymentInitReqId);

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
     * check whether a given paymentInitReqId exists in the database
     * @param paymentInitReqId
     * @return
     * @throws PispException
     */
    public boolean checkExistanceOfPayment(String paymentInitReqId) throws PispException {

        final String sql = MySQLStatements.CHECK_EXISTANCE_OF_A_PAYMENT_INITIATION;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1,paymentInitReqId);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                      return true;
                    }
                    return false;
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

    /**
     *
     * @return
     * @throws PispException
     * retrieve a selected payment initiation request
     */
    public Payment retrievePayment(String paymentInitReqId) throws PispException {

        Payment payment=new Payment();
        log.info("The requested payment initiation :"+paymentInitReqId);

        final String sql = MySQLStatements.GET_PAYMENT_INITIATION;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1,paymentInitReqId);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        //public static final String ADD_NEW_PAYMENT_INITIATION = "INSERT INTO PAYMENTS  (PAYMENT_INIT_REQ_ID , CLIENT_ID, MERCHANT_ID, PURCHASE_ID," +
                                //"CURRENCY, PAYMENT_AMOUNT, CUSTOMER_ID, CREDITOR_ACCOUNT_UID, REDIRECT_URI, PAYMENT_STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

                        payment.setPaymentInitReqId(rs.getString("PAYMENT_INIT_REQ_ID"));
                        payment.setInstructedAmountCurrency(rs.getString("CURRENCY"));
                        payment.setInstructedAmount(Float.parseFloat(rs.getString("PAYMENT_AMOUNT")));
                        payment.setRedirectURI(rs.getString("REDIRECT_URI"));
                        payment.setDeliveryAddress(rs.getString("DELIVERY_ADDRESS_JSON"));
                        payment.setCustomerIdentification(rs.getString("CUSTOMER_IDENTIFICATION_BY_ESHOP"));

                        String merchantId=rs.getString("MERCHANT_ID");
                        String creditorAccountUID=rs.getString("CREDITOR_ACCOUNT_UID");
                        String debtorAccountUID=rs.getString("DEBTOR_ACCOUNT_UID");

                        payment.setPaymentId(rs.getString("PAYMENT_ID"));

                        Merchant merchant=retrieveMerchantInfo(merchantId);
                        payment.setMerchant(merchant);

                        Object[] creditorBankData=this.retrieveCreditorBankAndAccount(creditorAccountUID);
                        payment.setMerchantBank((Bank) creditorBankData[0]);
                        payment.setMerchantBankAccount((BankAccount) creditorBankData[1]);

                        if(debtorAccountUID!=null){
                            Object[] debtorBankData=this.retrieveDebtorBankAndAccount(debtorAccountUID);
                            payment.setCustomerBank((DebtorBank) debtorBankData[0]);
                            payment.setCustomerBankAccount((BankAccount) debtorBankData[1]);
                        }

                        //add the whole fields here
                        //you need to access merchant/customer/creditorAccount tables also to completly construct back the payment init request.This does not work.
                        log.info("The requested payment initiation exists");

                        return payment;
                    } else {
                        payment.setErrorStatus(true);
                        payment.setErrorMessage("The requested paymentInitReqId does not exist");
                        log.info("The requested payment initiation  does not exist");
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

    private Object[] retrieveCreditorBankAndAccount(String creditorAccountUID){

        Bank creditorBank=new Bank();
        BankAccount creditorAccount=new BankAccount();

        final String sql = MySQLStatements.GET_CREDITOR_BANK_DETAILS;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1,creditorAccountUID);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        creditorBank.setSchemeName(rs.getString("BANK_IDENTIFICATION_SCHEME"));
                        creditorBank.setIdentification(rs.getString("BANK_IDENTIFICATION_NO"));
                        creditorBank.setBankName(rs.getString("BANK_NAME"));
                        creditorAccount.setSchemeName(rs.getString("ACCOUNT_IDENTIFICATION_SCHEME"));
                        creditorAccount.setIdentification(rs.getString("ACCOUNT_IDENTIFICATION_NO"));
                        creditorAccount.setAccountOwnerName(rs.getString("ACCOUNT_OWNER_NAME"));
                        log.info("Retrieving Creditor Bank & Account details of the payment");
                        return new Object[]{creditorBank, creditorAccount};
                    } else {
                        log.info("Error retrieving the creditor Bank Details");
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

    private Object[] retrieveDebtorBankAndAccount(String debtorAccountUID){

        DebtorBank debtorBank;
        BankAccount debtorAccount=new BankAccount();
        String bank_uid;
        log.info("The debtor account UID to retrive"+debtorAccountUID);

        final String sql = MySQLStatements.GET_DEBTOR_ACCOUNT_DETAILS;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1,debtorAccountUID);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        bank_uid=rs.getString("BANK_UID");
                        debtorAccount.setSchemeName(rs.getString("ACCOUNT_IDENTIFICATION_SCHEME"));
                        debtorAccount.setIdentification(rs.getString("ACCOUNT_IDENTIFICATION_NO"));
                        debtorAccount.setAccountOwnerName(rs.getString("ACCOUNT_OWNER_NAME"));

                        log.info("Retrieving debtor Bank UID & Account details of the payment");
                        debtorBank=this.retrieveDebtorBankInfo(bank_uid);
                        return new Object[]{debtorBank, debtorAccount};

                    } else {
                        log.info("Error retrieving the debtor Bank Details");
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

    private DebtorBank retrieveDebtorBankInfo(String bankUid){

        DebtorBank debtorbank=new DebtorBank();
        log.info("receiving bank info"+ bankUid);

        final String sql = MySQLStatements.GET_A_BANK;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1,bankUid);
                preparedStatement.setString(2,Constants.BANK_STATUS_ACTIVE);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        debtorbank.setBankUid(rs.getString("BANK_UID"));
                        debtorbank.setSchemeName(rs.getString("BANK_IDENTIFICATION_SCHEME"));
                        debtorbank.setIdentification(rs.getString("BANK_IDENTIFICATION_NO"));
                        debtorbank.setBankName(rs.getString("BANK_NAME"));
                        debtorbank.setSpecForOB(rs.getString("SPEC_FOR_OB"));
                        log.info("Retrieving Debtor bank details");

                        return debtorbank;
                    } else {
                        log.info("Error retrieving the debtor Bank Details");
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

    private Merchant retrieveMerchantInfo(String merchantId){

        Merchant merchant=new Merchant();


        final String sql = MySQLStatements.GET_MERCHANT;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, merchantId);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        merchant.setMerchantIdentificationByEshop(rs.getString("MERCHANT_IDENTIFICATION_BY_ESHOP"));
                        merchant.setMerchantName(rs.getString("MERCHANT_NAME"));
                        merchant.setMerchantCategoryCode(rs.getString("MERCHANT_CATEGORY_CODE"));

                        log.info("Retrieving merchant details of the payment");
                        return merchant;
                    } else {
                        log.info("Error retrieving merchant Details");
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
     * save payment Initiation id in the database
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
                preparedStatement.setString(2, Constants.PAYMENT_STATUS_4 );
                preparedStatement.setString(3, paymentInitReqId);

                preparedStatement.executeUpdate();
                log.info("successfully stored the payment initiation id");
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
     * save payment Submission id in the database
     *
     * @param paymentSubmissionId
     * @param paymentInitReqId
     * @throws PispException
     */
    public void saveSubmissionIds(String paymentSubmissionId, String paymentInitReqId) throws PispException {
        Validate.notNull(paymentSubmissionId, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);

        final String insert = MySQLStatements.UPDATE_PAYMENT_SUBMISSION_ID ;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
                preparedStatement.setString(1, paymentSubmissionId);
                preparedStatement.setString(2, Constants.PAYMENT_STATUS_6);
                preparedStatement.setString(3, paymentInitReqId);

                preparedStatement.executeUpdate();
                log.info("successfully stored the payment submission id");

            } catch (SQLException e) {
                log.error(ErrorMessages.SQL_QUERY_PREPARATION_ERROR, e);
                throw new PispException(ErrorMessages.SQL_QUERY_PREPARATION_ERROR);
            }
        } catch (SQLException e) {
            log.error(ErrorMessages.DB_CLOSE_ERROR, e);
            throw new PispException(ErrorMessages.DB_CLOSE_ERROR);
        }
    }

    public void updatePaymentAsCompleted(String paymentInitReqId){
        Validate.notNull(paymentInitReqId, ErrorMessages.PARAMETERS_NULL);

        final String Add = MySQLStatements.UPDATE_PAYMENT_INITIATION_AS_COMPLETED;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(Add)) {
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


    public String retrievePaymentId(String paymentInitReqId){

       String paymentId;


        final String sql = MySQLStatements.GET_MERCHANT;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, paymentInitReqId);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        paymentId=rs.getString("PAYMENT_ID");


                        log.info("Retrieving payment id of the payment");
                        return paymentId;
                    } else {
                        log.info("Error retrieving payment id");
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


}
