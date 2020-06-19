package outskirts.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class DigestUtils {

    private static final byte[] DEFAULT_BUFFER = new byte[1024];

    public static final MessageDigest MD2 = getDigest("MD2");
    public static final MessageDigest MD5 = getDigest("MD5");
    public static final MessageDigest SHA1 = getDigest("SHA-1");
    public static final MessageDigest SHA256 = getDigest("SHA-256");
    public static final MessageDigest SHA384 = getDigest("SHA-384");
    public static final MessageDigest SHA512 = getDigest("SHA-512");

    public static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException("Cant find the algorithm.", ex);
        }
    }

    private static MessageDigest updateDigest(MessageDigest digest, InputStream data) throws IOException {
        int length = 0;
        while ((length = data.read(DEFAULT_BUFFER)) != -1) {
            digest.update(DEFAULT_BUFFER, 0, length);
        }
        return digest;
    }

    public static byte[] digest(MessageDigest digest, InputStream data) throws IOException {
        return updateDigest(digest, data).digest();
    }



    public static byte[] md2(byte[] data) {
        return MD2.digest(data);
    }
    public static byte[] md2(InputStream data) throws IOException {
        return digest(MD2, data);
    }

    public static byte[] md5(byte[] data) {
        return MD5.digest(data);
    }
    public static byte[] md5(InputStream data) throws IOException {
        return digest(MD5, data);
    }



    public static byte[] sha1(byte[] data) {
        return SHA1.digest(data);
    }
    public static byte[] sha1(InputStream data) throws IOException {
        return digest(SHA1, data);
    }

    public static byte[] sha256(byte[] data) {
        return SHA256.digest(data);
    }
    public static byte[] sha256(InputStream data) throws IOException {
        return digest(SHA256, data);
    }

    public static byte[] sha384(byte[] data) {
        return SHA384.digest(data);
    }
    public static byte[] sha384(InputStream data) throws IOException {
        return digest(SHA384, data);
    }

    public static byte[] sha512(byte[] data) {
        return SHA512.digest(data);
    }
    public static byte[] sha512(InputStream data) throws IOException {
        return digest(SHA512, data);
    }


}
