package encrypt;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 implements ICrypto {
    public byte[] encrypt(String input) {
        BigInteger md5 = null;
        if(null == input) return null;
        try {
            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //Update input string in message digest
            digest.update(input.getBytes(), 0, input.length());
            //Converts message digest value in base 16 (hex)
            md5 = new BigInteger(1, digest.digest());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5.toByteArray();
    }

    public String decrypt(byte[] message) throws Exception
    {
        return null;
    }
}