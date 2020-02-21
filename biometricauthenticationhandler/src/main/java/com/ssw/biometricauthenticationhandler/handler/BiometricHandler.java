package com.ssw.biometricauthenticationhandler.handler;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static com.ssw.biometricauthenticationhandler.auth.BiometricAuthenticationHandler.ERROR_AUTH_FAILED;
import static com.ssw.biometricauthenticationhandler.auth.BiometricAuthenticationHandler.ERROR_KEYGUARD_NOT_SECURED;
import static com.ssw.biometricauthenticationhandler.auth.BiometricAuthenticationHandler.ERROR_NONE_ENROLLED;
import static com.ssw.biometricauthenticationhandler.auth.BiometricAuthenticationHandler.ERROR_NO_HARDWARE;
import static com.ssw.biometricauthenticationhandler.auth.BiometricAuthenticationHandler.ERROR_NO_PERMISSION;
import static com.ssw.biometricauthenticationhandler.common.ConstantList.DEV_MODE;

@RequiresApi(api = Build.VERSION_CODES.M)
public class BiometricHandler extends FingerprintManager.AuthenticationCallback {
    private static final String TAG = "BiometricHandler";

    private FragmentActivity fragmentActivity;

    private Cipher cipher;
    private KeyStore keyStore;
    private static final String KEY_NAME = "frimi";

    private BiometricHandlerEvents biometricHandlerEvents;

    public interface BiometricHandlerEvents {
        void onAuthSuccess();

        void onAuthFailed(int reason);

        void onAuthCancelled();
    }

    public BiometricHandler(FragmentActivity fragmentActivity, BiometricHandlerEvents biometricHandlerEvents) {
        this.fragmentActivity = fragmentActivity;
        this.biometricHandlerEvents = biometricHandlerEvents;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void fingerPrintInit() {
        KeyguardManager keyguardManager = (KeyguardManager) fragmentActivity.getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) fragmentActivity.getSystemService(FINGERPRINT_SERVICE);
        try {
            if (!fingerprintManager.isHardwareDetected()) {
                biometricHandlerEvents.onAuthFailed(ERROR_NO_HARDWARE);
            } else {
                if (ActivityCompat.checkSelfPermission(fragmentActivity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    biometricHandlerEvents.onAuthFailed(ERROR_NO_PERMISSION);
                } else {
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        biometricHandlerEvents.onAuthFailed(ERROR_NONE_ENROLLED);
                    } else {
                        if (!keyguardManager.isKeyguardSecure()) {
                            biometricHandlerEvents.onAuthFailed(ERROR_KEYGUARD_NOT_SECURED);
                        } else {
                            generateKey();
                            if (cipherInit()) {
                                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                startAuth(fingerprintManager, cryptoObject);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            biometricHandlerEvents.onAuthFailed(ERROR_AUTH_FAILED);
            if (DEV_MODE) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (Exception e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }


        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(fragmentActivity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        biometricHandlerEvents.onAuthCancelled();
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        biometricHandlerEvents.onAuthFailed(ERROR_AUTH_FAILED);
    }

    @Override
    public void onAuthenticationFailed() {
        biometricHandlerEvents.onAuthFailed(ERROR_AUTH_FAILED);
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        biometricHandlerEvents.onAuthSuccess();
    }

}
