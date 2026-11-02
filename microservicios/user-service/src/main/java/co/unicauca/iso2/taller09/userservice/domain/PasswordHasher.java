package co.unicauca.iso2.taller09.userservice.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class PasswordHasher {
    private static final String SEPARATOR = "$";
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);
        String salt = HexFormat.of().formatHex(saltBytes);
        return salt + SEPARATOR + digest(salt, plainPassword);
    }

    public static boolean matches(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        int separator = storedHash.lastIndexOf(SEPARATOR);
        if (separator <= 0) {
            return false;
        }
        String salt = storedHash.substring(0, separator);
        byte[] expected = storedHash.substring(separator + 1).getBytes(StandardCharsets.UTF_8);
        byte[] actual = digest(salt, plainPassword).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, actual);
    }

    private static String digest(String salt, String plainPassword) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(sha256.digest((salt + plainPassword).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no está disponible", e);
        }
    }
}
