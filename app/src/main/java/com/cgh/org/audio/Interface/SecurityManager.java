package com.cgh.org.audio.Interface;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import androidx.core.content.ContextCompat;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Security Manager for Android 16
 * Implements advanced security features and data protection
 */
public class SecurityManager {
    
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "AudioAppKey";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    
    private Context context;
    private KeyStore keyStore;
    
    public SecurityManager(Context context) {
        this.context = context;
        initializeKeyStore();
    }
    
    /**
     * Initialize Android Keystore
     */
    private void initializeKeyStore() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            
            // Generate key if it doesn't exist
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                generateKey();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Generate encryption key with Android 16 security features
     */
    private void generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
            )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true);
            
            // Android 16 specific security features
            if (Build.VERSION.SDK_INT >= 36) {
                // Enhanced security for Android 16
                builder.setUserAuthenticationRequired(true)
                       .setUserAuthenticationValidityDurationSeconds(300) // 5 minutes
                       .setInvalidatedByBiometricEnrollment(true)
                       .setRequireUserAuthentication(true);
            }
            
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Encrypt data with Android 16 security features
     */
    public String encryptData(String data) {
        try {
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_ALIAS, null);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            byte[] iv = cipher.getIV();
            
            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Decrypt data with Android 16 security features
     */
    public String decryptData(String encryptedData) {
        try {
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_ALIAS, null);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            
            byte[] combined = Base64.getDecoder().decode(encryptedData);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
            
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);
            
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            
            byte[] decryptedData = cipher.doFinal(encrypted);
            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Check if device has biometric authentication
     */
    public boolean hasBiometricAuthentication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) ||
                   packageManager.hasSystemFeature(PackageManager.FEATURE_FACE) ||
                   packageManager.hasSystemFeature(PackageManager.FEATURE_IRIS);
        }
        return false;
    }
    
    /**
     * Check if device is secure (has lock screen)
     */
    public boolean isDeviceSecure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.app.KeyguardManager keyguardManager = 
                (android.app.KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return keyguardManager.isKeyguardSecure();
        }
        return false;
    }
    
    /**
     * Generate secure random token
     */
    public String generateSecureToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[32];
        secureRandom.nextBytes(token);
        return Base64.getEncoder().encodeToString(token);
    }
    
    /**
     * Validate data integrity
     */
    public boolean validateDataIntegrity(String data, String hash) {
        try {
            String calculatedHash = calculateHash(data);
            return calculatedHash.equals(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Calculate SHA-256 hash
     */
    private String calculateHash(String data) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Check if app is running in secure environment
     */
    public boolean isSecureEnvironment() {
        // Check for root access
        if (isDeviceRooted()) {
            return false;
        }
        
        // Check for debugging
        if (isDebuggingEnabled()) {
            return false;
        }
        
        // Check for emulator
        if (isEmulator()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if device is rooted
     */
    private boolean isDeviceRooted() {
        try {
            String[] rootPaths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"
            };
            
            for (String path : rootPaths) {
                if (new java.io.File(path).exists()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check if debugging is enabled
     */
    private boolean isDebuggingEnabled() {
        return (context.getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
    
    /**
     * Check if running on emulator
     */
    private boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic") ||
               Build.FINGERPRINT.startsWith("unknown") ||
               Build.MODEL.contains("google_sdk") ||
               Build.MODEL.contains("Emulator") ||
               Build.MODEL.contains("Android SDK built for x86") ||
               Build.MANUFACTURER.contains("Genymotion") ||
               (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
               "google_sdk".equals(Build.PRODUCT);
    }
    
    /**
     * Get security level of the device
     */
    public int getSecurityLevel() {
        int level = 0;
        
        // Basic security
        if (isDeviceSecure()) {
            level += 1;
        }
        
        // Biometric authentication
        if (hasBiometricAuthentication()) {
            level += 2;
        }
        
        // Secure environment
        if (isSecureEnvironment()) {
            level += 3;
        }
        
        // Android 16 specific features
        if (Build.VERSION.SDK_INT >= 36) {
            level += 2;
        }
        
        return level;
    }
    
    /**
     * Get security recommendations
     */
    public String getSecurityRecommendations() {
        StringBuilder recommendations = new StringBuilder();
        
        if (!isDeviceSecure()) {
            recommendations.append("• Enable device lock screen\n");
        }
        
        if (!hasBiometricAuthentication()) {
            recommendations.append("• Enable biometric authentication\n");
        }
        
        if (!isSecureEnvironment()) {
            recommendations.append("• Avoid using rooted devices\n");
            recommendations.append("• Disable USB debugging\n");
        }
        
        if (Build.VERSION.SDK_INT < 36) {
            recommendations.append("• Update to Android 16 for enhanced security\n");
        }
        
        return recommendations.toString();
    }
}