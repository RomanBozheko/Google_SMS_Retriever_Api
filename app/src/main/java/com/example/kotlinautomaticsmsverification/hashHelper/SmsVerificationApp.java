package com.example.kotlinautomaticsmsverification.hashHelper;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class SmsVerificationApp extends Application {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override public void onCreate() {
        super.onCreate();
        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        appSignatureHelper.getAppSignatures();
    }
}
