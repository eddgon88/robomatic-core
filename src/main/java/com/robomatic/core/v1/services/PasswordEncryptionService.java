package com.robomatic.core.v1.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio para encriptación y verificación de contraseñas usando BCrypt.
 * BCrypt es un algoritmo de hash adaptativo que incluye salt automático
 * y protección contra ataques de fuerza bruta.
 */
@Service
public class PasswordEncryptionService {

    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordEncryptionService() {
        // Strength 12 proporciona un buen balance entre seguridad y rendimiento
        // Cada incremento duplica el tiempo de procesamiento
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    /**
     * Encripta una contraseña en texto plano usando BCrypt.
     * 
     * @param rawPassword Contraseña en texto plano
     * @return Hash BCrypt de la contraseña (60 caracteres)
     */
    public String encryptPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash BCrypt.
     * 
     * @param rawPassword Contraseña en texto plano a verificar
     * @param encryptedPassword Hash BCrypt almacenado
     * @return true si la contraseña coincide, false en caso contrario
     */
    public boolean verifyPassword(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || encryptedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }
}
