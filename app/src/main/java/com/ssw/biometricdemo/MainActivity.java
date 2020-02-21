package com.ssw.biometricdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ssw.biometricauthenticationhandler.auth.BiometricAuthenticationHandler;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private String reasonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
    }

    private void initComponents() {
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyBiometrics();
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Finger Print and Face ID Handler">
    private void verifyBiometrics() {
        BiometricAuthenticationHandler biometricAuthenticationHandler = new BiometricAuthenticationHandler(this, new BiometricAuthenticationHandler.BiometricAuthenticationHandlerEvents() {
            @Override
            public void onAuthenticationSuccess() {
                System.out.println("Authentication Success");
            }

            @Override
            public void onAuthenticationFailed(int reason) {
                switch (reason) {
                    case BiometricAuthenticationHandler.ERROR_NO_HARDWARE:
                        reasonString = "Hardware not found";
                        break;
                    case BiometricAuthenticationHandler.ERROR_NONE_ENROLLED:
                        reasonString = "No biometric enrollments";
                        break;
                    case BiometricAuthenticationHandler.ERROR_HARDWARE_UNAVAILABLE:
                        reasonString = "Hardware not available";
                        break;
                    case BiometricAuthenticationHandler.ERROR_AUTH_FAILED:
                    default:
                        reasonString = "Authentication failed";
                        break;
                    case BiometricAuthenticationHandler.ERROR_NO_PERMISSION:
                        reasonString = "No Permissions";
                        break;
                    case BiometricAuthenticationHandler.ERROR_KEYGUARD_NOT_SECURED:
                        reasonString = "Keyguard not secured";
                        break;
                }

                System.out.println("Authentication Failed " + reasonString);
            }

            @Override
            public void onAuthenticationCancelled() {
                System.out.println("Authentication Cancelled");
            }
        });

        biometricAuthenticationHandler.startAuthentication("Title", "SubTitle", "Description", "Cancel", R.mipmap.ic_launcher);
    }
    // </editor-fold>
}
