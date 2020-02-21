# BiometricAuthentication
Android library for implement biometric authentication in Java or Kotlin code base

## What is BiometricAuthentication
BiometricAuthentication is an android library which can be used to implement biometric authentications such as fingerprint, faceID, IRIS etc...

## Implementation

### Step : 1 - Add the JitPack repository to your project root build.gradle file
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### Step : 2- Add the dependency
```gradle
dependencies {
      implementation 'com.github.Sumudu-Sahan:BiometricAuthentication:1.0'
}
```
#### This library packs with SDP, SSP libraries, androidx biometric library, vibrator, biometric related permissions. Therefore no need to add below mentioned permissions again and libraries
##### Below permissions are already included inside the library
```XML
<uses-permission android:name="android.permission.USE_FINGERPRINT" />
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
<uses-permission android:name="android.permission.VIBRATE" />
```
##### below dependencies are already packed with the library
```Gradle
implementation 'androidx.biometric:biometric:1.0.0'
implementation 'com.intuit.ssp:ssp-android:1.0.6'
implementation 'com.intuit.sdp:sdp-android:1.0.6'
```
#### Then sync the project.

## Few Functions

### Biometric Authentication
```Java
BiometricAuthenticationHandler biometricAuthenticationHandler = new BiometricAuthenticationHandler(fragmentActivity, new BiometricAuthenticationHandler.BiometricAuthenticationHandlerEvents() {
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

        biometricAuthenticationHandler.startAuthentication("Title", "SubTitle", "Description", "BTNTEXT", resID); //resID - popup dialog image icon resource
```

##### Authentication Failing Reasons
```Java
BiometricAuthenticationHandler.ERROR_NO_HARDWARE
BiometricAuthenticationHandler.ERROR_NONE_ENROLLED
BiometricAuthenticationHandler.ERROR_HARDWARE_UNAVAILABLE
BiometricAuthenticationHandler.ERROR_AUTH_FAILED
BiometricAuthenticationHandler.ERROR_NO_PERMISSION
BiometricAuthenticationHandler.ERROR_KEYGUARD_NOT_SECURED
```

That's it. 
Happy Coding :smiley: :smiley: :smiley:
