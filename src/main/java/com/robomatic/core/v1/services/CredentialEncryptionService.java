package com.robomatic.core.v1.services;

import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.exceptions.messages.InternalErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Servicio de encriptación/desencriptación usando AES-256-GCM.
 * Este algoritmo permite encriptar y recuperar el valor original,
 * a diferencia de BCrypt que es un hash de una sola vía.
 */
@Slf4j
@Service
public class CredentialEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;
    private static final int ITERATIONS = 65536;

    @Value("${robomatic.encryption.secret-key:default-secret-key-change-in-production}")
    private String secretKey;

    /**
     * Encripta un valor usando AES-256-GCM.
     * El resultado incluye: salt + iv + ciphertext (todo en Base64)
     *
     * @param plainText Texto a encriptar
     * @return Texto encriptado en Base64
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plain text cannot be null or empty");
        }

        try {
            // Generar salt e IV aleatorios
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            byte[] iv = new byte[GCM_IV_LENGTH];
            random.nextBytes(salt);
            random.nextBytes(iv);

            // Derivar clave desde la clave maestra + salt
            SecretKey key = deriveKey(secretKey, salt);

            // Configurar cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

            // Encriptar
            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));

            // Combinar: salt + iv + ciphertext
            ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + iv.length + cipherText.length);
            byteBuffer.put(salt);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);

            return Base64.getEncoder().encodeToString(byteBuffer.array());

        } catch (Exception e) {
            log.error("Error encrypting value: {}", e.getMessage());
            throw new InternalErrorException(InternalErrorCode.E500000, "Error encrypting credential");
        }
    }

    /**
     * Desencripta un valor encriptado con AES-256-GCM.
     *
     * @param encryptedText Texto encriptado en Base64
     * @return Texto original desencriptado
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            throw new IllegalArgumentException("Encrypted text cannot be null or empty");
        }

        try {
            // Decodificar Base64
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);

            // Extraer salt
            byte[] salt = new byte[SALT_LENGTH];
            byteBuffer.get(salt);

            // Extraer IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            // Extraer ciphertext
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            // Derivar clave
            SecretKey key = deriveKey(secretKey, salt);

            // Configurar cipher para descifrar
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

            // Desencriptar
            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, "UTF-8");

        } catch (Exception e) {
            log.error("Error decrypting value: {}", e.getMessage());
            throw new InternalErrorException(InternalErrorCode.E500000, "Error decrypting credential");
        }
    }

    /**
     * Deriva una clave AES-256 desde la clave maestra usando PBKDF2.
     */
    private SecretKey deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

}


