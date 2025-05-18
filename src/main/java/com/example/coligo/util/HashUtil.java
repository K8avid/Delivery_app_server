package com.example.coligo.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HashUtil {

    private static final String SECRET_KEY = "bG9uZy1yYW5kb21seS1nZW5l_79YnP.@MLNNUYcmF0ZWQta2V5LXN0cmluZw";

    public static String generateSecureHash(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512"); // Utilisation de HMAC SHA-512 pour un hash plus long
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(data.getBytes());
            // Encode en Base64 pour obtenir un format lisible (sans padding pour éviter d'ajouter des caractères inutiles)
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du hash : " + e.getMessage(), e);
        }
    }
}
