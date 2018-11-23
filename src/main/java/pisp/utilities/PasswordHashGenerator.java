
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
    package pisp.utilities;

    import org.apache.commons.logging.Log;
    import org.apache.commons.logging.LogFactory;

    import javax.crypto.SecretKeyFactory;
    import javax.crypto.spec.PBEKeySpec;
    import java.security.NoSuchAlgorithmException;
    import java.security.SecureRandom;
    import java.security.spec.InvalidKeySpecException;
    import java.util.Arrays;
    import java.util.Random;

    public class PasswordHashGenerator {


        /**
         * Hashing Password before storing in databases.
         * <p>
         * The algorithm used is PBKDF2WithHmacSHA1.
         */


        private static final Random RANDOM = new SecureRandom();
        private static final int ITERATIONS = 10000;
        private static final int KEY_LENGTH = 256;
        private static Log log = LogFactory.getLog(PasswordHashGenerator.class);

        /**
         * static utility class.
         */
        private PasswordHashGenerator() {
        }

        /**
         * Returns a random salt to be used to hash a password.
         *
         * @return a 16 bytes random salt
         */
        public static byte[] getNextSalt() {
            byte[] salt = new byte[16];
            RANDOM.nextBytes(salt);
            return salt;
        }

        /**
         * Returns a salted and hashed password using the provided hash.<br>
         * Note - side effect: the password is destroyed (the char[] is filled with zeros)
         *
         * @param password the password to be hashed
         * @param salt     a 16 bytes salt, ideally obtained with the getNextSalt method
         * @return the hashed password with a pinch of salt
         */
        public static byte[] hash(char[] password, byte[] salt) {
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            Arrays.fill(password, Character.MIN_VALUE);
            try {
                SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                return skf.generateSecret(spec).getEncoded();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
            } finally {
                spec.clearPassword();
            }
        }

        /**
         * Returns true if the given password and salt match the hashed value, false otherwise.<br>
         * Note - side effect: the password is destroyed (the char[] is filled with zeros)
         *
         * @param password     the password to check
         * @param salt         the salt used to hash the password
         * @param expectedHash the expected hashed value of the password
         * @return true if the given password and salt match the hashed value, false otherwise
         */
        public static boolean isExpectedPassword(char[] password, byte[] salt, byte[] expectedHash) {
            log.info("Checking hash");
            byte[] pwdHash = hash(password, salt);
            Arrays.fill(password, Character.MIN_VALUE);
            if (pwdHash.length != expectedHash.length) {
                log.info("Incorrect Hash");
                return false;
            }
            for (int i = 0; i < pwdHash.length; i++) {
                if (pwdHash[i] != expectedHash[i]) {
                    log.info("Correct Hash");
                    return false;
                }
            }
            return true;
        }
    }


