package ru.alta.svd.client.core.service.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import ru.alta.svd.client.core.service.api.EncryptorService;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

public class DesEncryptorService implements EncryptorService {

    private static Logger logger = Logger.getLogger(DesEncryptorService.class);
    private Cipher ecipher;
    private Cipher dcipher;

    private byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    private static volatile DesEncryptorService encryptorService;

    private int iterationCount = 19;

    public DesEncryptorService(String passPhrase) {
        {
            try {
                KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
                SecretKey key = SecretKeyFactory.getInstance(
                        "PBEWithMD5AndDES").generateSecret(keySpec);
                ecipher = Cipher.getInstance(key.getAlgorithm());
                dcipher = Cipher.getInstance(key.getAlgorithm());

                AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

                ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
                dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
            } catch (java.security.InvalidAlgorithmParameterException | java.security.spec.InvalidKeySpecException | NoSuchPaddingException | java.security.NoSuchAlgorithmException | java.security.InvalidKeyException e) {
                logger.error("Can not create encrypter DesEncrypter: " + e.getMessage());
                logger.error(e);
            }
        }
    }

    public static EncryptorService getInstance(){
        if(encryptorService==null){
            synchronized (DesEncryptorService.class){
                if(encryptorService==null){
                    encryptorService = new DesEncryptorService("AltaJhttpPassphrase1");
                }
            }
        }
        return encryptorService;
    }

    @Override
    public byte[] encrypt(String str) {
        if (str==null)return "".getBytes();
        try {
            byte[] utf8 = str.getBytes(StandardCharsets.UTF_8);
            byte[] enc = ecipher.doFinal(utf8);
            return enc;
        } catch (BadPaddingException | IllegalBlockSizeException ignored) {

        }
        return null;
    }

    @Override
    public byte[] decrypt(String str) {
        try {
            byte[] dec = Base64.decodeBase64(str);
            byte[] utf8 = dcipher.doFinal(dec);
            return utf8;
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            logger.error("Can not decrypt message: " + e.getMessage());
            logger.error(e);
            return str.getBytes();
        }
    }
}
