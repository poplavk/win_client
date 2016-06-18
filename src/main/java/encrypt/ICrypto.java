package encrypt;

public interface ICrypto {
    public byte[] encrypt(byte[] message) throws Exception;
    public byte[] decrypt(byte[] message) throws Exception;
}
