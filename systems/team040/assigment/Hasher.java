package systems.team040.assigment;

import com.sun.prism.PixelFormat;

import javax.crypto.SecretKeyFactory;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.Data;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.DataTruncation;
import java.util.Arrays;
import java.util.Base64;


public class Hasher {
    private static final int ITERATIONS = 1000;
    private static final int SALT_LEN = 8;
    private static final int KEY_LEN = 64 * 4;
    private static final String RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA1";

    private static byte[] generateSalt() {
        SecureRandom sr;
        try {
            sr = SecureRandom.getInstance(RANDOM_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("This shouldn't happen");
            e.printStackTrace();
            return null;
        }

        byte[] retVal = new byte[SALT_LEN];
        sr.nextBytes(retVal);
        return retVal;
    }

    public static String generateDigest(char[] password) {
        byte[] salt = generateSalt();
        byte[] hash = hash(password, salt);

        return DatatypeConverter.printHexBinary(salt) + "$" + DatatypeConverter.printHexBinary(hash);
    }

    private static byte[] hash(char[] password, byte[] salt) {
        SecretKeyFactory skf = null;
        try {
            skf = SecretKeyFactory.getInstance(HASH_ALGORITHM);
            PBEKeySpec keySpec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LEN);

            return skf.generateSecret(keySpec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean validatePassword(String entered, String stored) {
        String[] parts = stored.split("\\$");
        byte[] salt = DatatypeConverter.parseHexBinary(parts[0]);
        byte[] hash = DatatypeConverter.parseHexBinary(parts[1]);

        char[] enteredPassword = entered.toCharArray();

        return Arrays.equals(hash(enteredPassword, salt), hash);
    }
}
