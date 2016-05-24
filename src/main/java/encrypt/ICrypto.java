package encrypt;

public interface ICrypto {
    public byte[] encrypt(String message) throws Exception;
    public String decrypt(byte[] message) throws Exception;
}
