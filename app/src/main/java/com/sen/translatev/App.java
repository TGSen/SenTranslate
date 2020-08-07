package com.sen.translatev;

import android.app.Application;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "d9b2f7e2a0", false);
        Log.e("Harriosn","init");
    }
}
