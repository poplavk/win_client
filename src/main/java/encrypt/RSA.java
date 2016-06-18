package encrypt;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA implements ICrypto {

    private BigInteger n, d, e;
    private int bitlen = 1024;

    public RSA() {
        generateKeys();
    }

    public byte[] encrypt(byte[] message) throws Exception {
        return (new BigInteger(message).modPow(e, n).toByteArray());
    }

    public byte[] decrypt(byte[] message) throws Exception {
        //return new String((new BigInteger(message.toString())).modPow(d, n).toByteArray());
        return new BigInteger(message).modPow(d,n).toByteArray();
        //return null;
    }

    private void generateKeys() {
        SecureRandom r = new SecureRandom();
        BigInteger p = new BigInteger(bitlen / 2, 100, r);
        BigInteger q = new BigInteger(bitlen / 2, 100, r);
        n = p.multiply(q);
        BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q
                .subtract(BigInteger.ONE));
        e = new BigInteger("3");
        while (m.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }
        d = e.modInverse(m);
    }

}