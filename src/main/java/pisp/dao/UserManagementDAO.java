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
import pisp.models.*;
import pisp.utilities.PasswordHashGenerator;
import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;
import pisp.utilities.constants.MySQLStatements;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * This class is to store and retrieve data related to PSU and E-Shop user.
 */
public class UserManagementDAO {

    private Log log = LogFactory.getLog(UserManagementDAO.class);


    /**
     * Validate if the e_shop username exists in the database.
     *
     * @param eshopUserName The username to check.
     * @return True if username exists or False if doesn't.
     * @throws PispException If database connection errors.
     */
    public boolean validateUserNameOfEShop(String eshopUserName) throws PispException {
        Validate.notNull(eshopUserName, ErrorMessages.PARAMETERS_NULL);

        final String sql;
        sql = MySQLStatements.GET_E_SHOP;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, eshopUserName);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    boolean isInDb = rs.next();
                    if (isInDb) {
                        log.info("Validated: E-shop user in DB");
                    } else {
                        log.info("Validated: E-shop user not in DB");
                    }
                    return isInDb;
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
     * Validate if the psu username exists in the database.
     *
     * @param PSUUserName The username to check.
     * @return True if username exists or False if doesn't.
     * @throws PispException If database connection errors.
     */
    public boolean validateUserNameOfPSU(String PSUUserName) throws PispException {
        Validate.notNull(PSUUserName, ErrorMessages.PARAMETERS_NULL);
;
        final String sql;
        sql = MySQLStatements.GET_PSU;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, PSUUserName);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    boolean isInDb = rs.next();
                    if (isInDb) {
                        log.info("Validated: PSU in DB");
                    } else {
                        log.info("Validated: PSU not in DB");
                    }
                    return isInDb;
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
     * Check if the username-password combination is correct for logging e-shop.
     *
     * @param username The user to check.
     * @param password The password of user.
     * @return True if password is correct or False if it isn't.
     * @throws PispException If database connection errors.
     */
    public PispInternalResponse loginEShopUser(String username, String password) throws PispException {

        Validate.notNull(username, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(password, ErrorMessages.PARAMETERS_NULL);

        final String sql = MySQLStatements.GET_PASSWORD_ESHOP;
        byte[] salt;
        byte[] hash;
        String name;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        salt = rs.getBytes("salt");
                        hash = rs.getBytes("password_hash");
                        name = rs.getString("e_shop_name");
                        if (PasswordHashGenerator.isExpectedPassword(password.toCharArray(), salt, hash)) {
                            return new PispInternalResponse(name, true);
                        } else {
                            return new PispInternalResponse(ErrorMessages.INCORRECT_PASSWORD, false);
                        }
                    } else {
                        log.info(ErrorMessages.USERNAME_DOESNT_EXIST);
                        return new PispInternalResponse(ErrorMessages.USERNAME_DOESNT_EXIST, false);
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

    /**
     * Check if the username-password combination is correct for logging psu.
     *
     * @param username The user to check.
     * @param password The password of user.
     * @return True if password is correct or False if it isn't.
     * @throws PispException If database connection errors.
     */
    public PispInternalResponse loginPSU(String username, String password) throws PispException {

        Validate.notNull(username, ErrorMessages.PARAMETERS_NULL);
        Validate.notNull(password, ErrorMessages.PARAMETERS_NULL);

        final String sql = MySQLStatements.GET_PASSWORD_PSU;
        byte[] salt;
        byte[] hash;
        String firstName;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        salt = rs.getBytes("salt");
                        hash = rs.getBytes("password_hash");
                        firstName = rs.getString("first_name");
                        if (PasswordHashGenerator.isExpectedPassword(password.toCharArray(), salt, hash)) {
                            return new PispInternalResponse(firstName, true);
                        } else {
                            return new PispInternalResponse(ErrorMessages.INCORRECT_PASSWORD, false);
                        }
                    } else {
                        log.info(ErrorMessages.USERNAME_DOESNT_EXIST);
                        return new PispInternalResponse(ErrorMessages.USERNAME_DOESNT_EXIST, false);
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
    ============================================================
    This section responsible of registering a new PSU user in db
    =============================================================
    */

    public boolean addNewPSU(PSU psu) throws PispException {
        Validate.notNull(psu, ErrorMessages.PARAMETERS_NULL);

        final String sql = MySQLStatements.ADD_NEW_PSU;

        byte[] salt = PasswordHashGenerator.getNextSalt();
        byte[] passwordHash = PasswordHashGenerator.hash(psu.getPassword().toCharArray(), salt);

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, psu.getUsername());
                preparedStatement.setString(2, psu.getFirstName());
                preparedStatement.setString(3, psu.getLastName());
                preparedStatement.setBytes(4, passwordHash);
                preparedStatement.setBytes(5, salt);
                preparedStatement.setString(6, psu.getEmail());

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
    ================================================================
    This section responsible of registering a new e-shop user in db
    ================================================================
    */

    /**
     * Add a e_shop record to the database.
     * @param e_shop User to add.
     * @throws PispException If database connection errors.
     */
    public boolean registerEShop(EShop e_shop) throws PispException {
        boolean result=this.addToEShopTbl(e_shop);
        if(e_shop.getEShopCategory().equals(Constants.SINGLE_VENDOR)){
            String merchantId=this.addToMerchantTbl(e_shop);
            if(this.addToCreditorAccountTbl(e_shop.getMerchant(),merchantId)){
                log.info("E-shop -single vendor updated the merchant and creditor account details");
                return true;
            }
        }
        return result;
    }

    /**
     * update e-shop table with e-shop user data.
     * @param e_shop
     * @return
     * @throws PispException
     */
    private boolean addToEShopTbl(EShop e_shop) throws PispException {
        Validate.notNull(e_shop, ErrorMessages.PARAMETERS_NULL);

        final String sql = MySQLStatements.ADD_NEW_E_SHOP;

        byte[] salt = PasswordHashGenerator.getNextSalt();
        byte[] passwordHash = PasswordHashGenerator.hash(e_shop.getPassword().toCharArray(), salt);

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, e_shop.getUsername());
                preparedStatement.setString(2, e_shop.getEShopName());
                preparedStatement.setString(3, e_shop.getEShopRegistrationNo());
                preparedStatement.setString(4, e_shop.getRegisteredBusinessName());
                preparedStatement.setString(5, e_shop.getRegisteredCountry());
                preparedStatement.setString(6, e_shop.getEShopCategory());
                preparedStatement.setBytes(7, passwordHash);
                preparedStatement.setBytes(8, salt);
                preparedStatement.setString(9, e_shop.getClientId());
                preparedStatement.setString(10, e_shop.getClientSecret());
                preparedStatement.setString(11, e_shop.getEmail());
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
     * If e-shop user is single vendor, only one entry will added to merchant table.
     * with the merchantIdentificationByEshop set to the same value as of e-shop username.
     * This same entry will be used and referred for all later payment initiations created by this single vendor e-shop.
     *
     * @param e_shop
     * @return
     */
    private String addToMerchantTbl(EShop e_shop){

        UUID uuid = UUID.randomUUID();
        String merchantId = uuid.toString();

        final String sql1 = MySQLStatements.ADD_NEW_MERCHANT;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql1)) {
                preparedStatement.setString(1, merchantId);
                preparedStatement.setString(2, e_shop.getClientId());
                preparedStatement.setString(3, e_shop.getMerchant().getMerchantIdentificationByEShop());
                preparedStatement.setString(4, e_shop.getMerchant().getMerchantName());
                preparedStatement.setString(5, e_shop.getMerchant().getMerchantCategoryCode());
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
        return merchantId;
    }


    /**
     * If e-shop user is single vendor, a one entry will added to creditor account table.
     * This entry will be used and refereed for all later payment initiations created by a single vendor e-shop.
     * @param merchant
     * @param merchantId
     * @return
     */
    private boolean addToCreditorAccountTbl(Merchant merchant, String merchantId){
        UUID uuid = UUID.randomUUID();
        String creditorAcountUID = uuid.toString();
        final String sql1 = MySQLStatements.ADD_NEW_CREDITOR_ACCOUNT;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql1)) {
                preparedStatement.setString(1, creditorAcountUID);
                preparedStatement.setString(2, merchantId);
                preparedStatement.setString(3, merchant.getMerchantBank().getSchemeName());
                preparedStatement.setString(4, merchant.getMerchantBank().getIdentification());
                preparedStatement.setString(5, merchant.getMerchantBank().getBankName());
                preparedStatement.setString(6, merchant.getMerchantAccount().getSchemeName() );
                preparedStatement.setString(7, merchant.getMerchantAccount().getIdentification());
                preparedStatement.setString(8, merchant.getMerchantAccount().getAccountOwnerName());
                preparedStatement.executeUpdate();

                log.info("New creditor account is added");
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
    ================================================================================
    Following methods are specific for retrieving E-shop profile data from db tables
    ================================================================================
    */

    /**
     * @param username
     * @return requested e-shop user details.
     * @throws PispException
     */
    public EShop retrieveEShopDetails(String username){
        EShop eShop=this.retrieveFromEShopTbl(username);
        if(eShop.getEShopCategory().equals(Constants.SINGLE_VENDOR)){
            Merchant merchant=this.retrieveMerchantInfo(username);
            Object[] merchantBankAccount=this.retrieveMerchantBankAndAccount(merchant.getMerchantId());
            merchant.setMerchantBank((Bank) merchantBankAccount[0]);
            merchant.setMerchantAccount((BankAccount) merchantBankAccount[1]);

            eShop.setMerchant(merchant);
        }
        return eShop;
    }

    private EShop retrieveFromEShopTbl(String userName) throws PispException {
        Validate.notNull(userName, ErrorMessages.PARAMETERS_NULL);

        final String sql = MySQLStatements.GET_E_SHOP;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, userName);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    EShop eShop =null;
                    if (rs.next()) {
                        eShop = new EShop(rs.getString("ECOMMERCE_MARKETPLACE_CATEGORY"));
                        eShop.setUsername(rs.getString("E_SHOP_USERNAME"));
                        eShop.setEShopName(rs.getString("E_SHOP_NAME"));
                        eShop.setEShopRegistrationNo(rs.getString("REGISTERED_NO"));
                        eShop.setRegisteredBusinessName(rs.getString("REGISTERED_BUSINESS_NAME"));
                        eShop.setRegisteredCountry(rs.getString("REGISTERED_COUNTRY"));
                        eShop.setClientId(rs.getString("CLIENT_ID"));
                        eShop.setClientSecret(rs.getString("CLIENT_SECRET"));
                        eShop.setEmail(rs.getString("EMAIL"));
                        log.info("Successfully retrieved E-shop");
                    } else {
                        log.info("User not in DB");
                    }
                    return eShop;
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

    private Merchant retrieveMerchantInfo(String username){

        Merchant merchant=new Merchant();
        final String sql = MySQLStatements.GET_MERCHANT_INFO;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        merchant.setMerchantId(rs.getString("MERCHANT_ID"));
                        merchant.setMerchantName(rs.getString("MERCHANT_NAME"));
                        merchant.setMerchantCategoryCode(rs.getString("MERCHANT_CATEGORY_CODE"));
                        log.info("Retrieving merchant details of the single-vendor E-shop");
                        return merchant;
                    } else {
                        log.info(ErrorMessages.ERROR_MERCHANT_RETRIEVAL);
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

    private Object[] retrieveMerchantBankAndAccount(String merchantId){

        Bank creditorBank=new Bank();
        BankAccount creditorAccount=new BankAccount();

        final String sql = MySQLStatements.GET_CREDITOR_ACCOUNT_INFO;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1,merchantId);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        creditorBank.setSchemeName(rs.getString("BANK_IDENTIFICATION_SCHEME"));
                        creditorBank.setIdentification(rs.getString("BANK_IDENTIFICATION_NO"));
                        creditorBank.setBankName(rs.getString("BANK_NAME"));
                        creditorAccount.setSchemeName(rs.getString("ACCOUNT_IDENTIFICATION_SCHEME"));
                        creditorAccount.setIdentification(rs.getString("ACCOUNT_IDENTIFICATION_NO"));
                        creditorAccount.setAccountOwnerName(rs.getString("ACCOUNT_OWNER_NAME"));
                        log.info("Retrieving Creditor Bank & Account details of the Single Vendor E-shop");
                        return new Object[]{creditorBank, creditorAccount};
                    } else {
                        log.error(ErrorMessages.ERROR_CREDITOR_BANK_RETRIEVAL);
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


   /*
    =========================================================================================
    Methods in this section is used during payment initiation at the E-shop - user validation
    =========================================================================================
    */

    /**
     * verify whether a given client id belongs to a registered E-shop
     * @param clientId
     * @return
     * @throws PispException
     */
    public boolean validateClientId(String clientId) throws PispException {
        Validate.notNull(clientId, ErrorMessages.PARAMETERS_NULL);

        log.info("Validating the existance of clientId: " + clientId);
        final String sql = MySQLStatements.VALIDATE_CLIENT_ID;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, clientId);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    boolean isInDb = rs.next();
                    if (isInDb) {
                        log.info("Validated: Client id is in DB");
                    } else {
                        log.info("Validated: Client Id not found");
                    }
                    return isInDb;
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
     * get the e-commerce market place category of the E-shop user.
     * @param clientId The unique ID assigned to the E-Shop at the registration with PISP
     * @return
     * @throws PispException
     */
    public String getMarketPlaceCategoryOfEshopUser(String clientId) throws PispException {
        Validate.notNull(clientId, ErrorMessages.PARAMETERS_NULL);


        final String sql = MySQLStatements.GET_MARKETPLACE_CATEGORY;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, clientId);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    String marketPlaceCategory=null;
                    if (rs.next()) {
                        marketPlaceCategory = rs.getString("ECOMMERCE_MARKETPLACE_CATEGORY");
                    }
                    return  marketPlaceCategory;
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
    =============================================================
    Methods in this section is used to update details of a E-shop
    =============================================================
    */
    //TODO : this update is needed to be re-checked

    /**
     * update the E-shop table by replacing new data for existing values.
     *  Here, if e-shop is a single vendor type, merchant table is also updated when the user updates the username.
     *
      * @param username
     * @param updated_e_shop
     * @return
     */
    public boolean updateEshopProfile(String username , EShop updated_e_shop){
        EShop existing_data=this.retrieveEShopDetails(username);
        existing_data=this.replaceTheExistingEShopWithUpdatedData(existing_data,updated_e_shop);
        this.updateEShopTbl(username,existing_data);
        if(existing_data.getEShopCategory().equals(Constants.SINGLE_VENDOR)){
            if(!username.equals(updated_e_shop.getUsername())){
                this.updateMerchantTblWithUsernameUpdate(username ,updated_e_shop.getUsername());
            }
            //write 2 functions to update merchant table/creditor Account tables with existing_data.getMerchant() details..
        }
        return true;

    }

    /**
     * update EShop data object with updated values.
     * @param existing_data
     * @param updated_data
     * @return
     */
    private EShop replaceTheExistingEShopWithUpdatedData(EShop existing_data,EShop updated_data){
        if(updated_data.getUsername()!=null){
            existing_data.setUsername(updated_data.getUsername());
        }
        if(updated_data.getEmail()!=null){
            existing_data.setEmail(updated_data.getEmail());
        }
        if(updated_data.getEShopName()!=null){
            existing_data.setEShopName(updated_data.getEShopName());
        }
        if(updated_data.getEShopRegistrationNo()!=null){
            existing_data.setEShopRegistrationNo(updated_data.getEShopRegistrationNo());
        }
        if(updated_data.getRegisteredBusinessName()!=null){
            existing_data.setRegisteredBusinessName(updated_data.getRegisteredBusinessName());
        }
        if(updated_data.getRegisteredCountry()!=null){
            existing_data.setRegisteredCountry(updated_data.getRegisteredCountry());
        }
        if(updated_data.getMerchant()!=null){
            existing_data.setMerchant(this.replaceExistingMerchantDataWithUpdatedData(existing_data.getMerchant(),updated_data.getMerchant()));
        }

        return  existing_data;
    }

    private Merchant replaceExistingMerchantDataWithUpdatedData(Merchant existing_data, Merchant updated_data){
        if(updated_data.getMerchantCategoryCode()!=null){
            existing_data.setMerchantCategoryCode(updated_data.getMerchantCategoryCode());
        }
        if(updated_data.getMerchantBank()!=null){
            existing_data.setMerchantBank(updated_data.getMerchantBank());
        }
        if(updated_data.getMerchantAccount()!=null){
            existing_data.setMerchantAccount(updated_data.getMerchantAccount());
        }
        return existing_data;
    }

    /**
     * update the details of a e-shop user.
     * @param username
     * @param updated_e_shop
     * @return
     */
    private boolean updateEShopTbl(String username , EShop updated_e_shop){

        final String Add = MySQLStatements.UPDATE_E_SHOP;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(Add)) {
                preparedStatement.setString(1, updated_e_shop.getUsername());
                preparedStatement.setString(2, updated_e_shop.getEShopName());
                preparedStatement.setString(3, updated_e_shop.getEShopRegistrationNo());
                preparedStatement.setString(4, updated_e_shop.getRegisteredBusinessName());
                preparedStatement.setString(5, updated_e_shop.getRegisteredCountry());
                preparedStatement.setString(6, updated_e_shop.getEmail());
                preparedStatement.setString(7, username);

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

    private void updateMerchantTblWithUsernameUpdate(String old_mechantIdentificationByEShop, String new_mechantIdentificationByEShop){
        final String Add = MySQLStatements.UPDATE_MERCHANT_IDENTIFICATION_BY_ESHOP;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(Add)) {
                preparedStatement.setString(1, new_mechantIdentificationByEShop);
                preparedStatement.setString(1, old_mechantIdentificationByEShop);

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



    /*
    ================================================================
    Methods in this section is used to unregister a E-shop from PISP
    ================================================================
    */

    /**
     * remove a E-shop from the PISP.
     * @param username
     * @return
     */
    public boolean removeEshop(String username) {
        Validate.notNull(username, ErrorMessages.PARAMETERS_NULL);

        final String sql = MySQLStatements.DELETE_ESHOP;

        try (Connection connection = JDBCPersistenceManager.getInstance().getDBConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
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

}
