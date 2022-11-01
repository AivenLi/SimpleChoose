package com.aiven.hfl;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aiven.hfl.util.FloatManager;

public class FloatApp extends Application implements Application.ActivityLifecycleCallbacks {

    private int activityCount = 0;


    public void onCreate() {
        super.onCreate();
        FloatManager.getInstance(this);
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
//        Handler handler = FloatManager.getInstance(null).getHandler();
//        handler.sendMessage(handler.obtainMessage(FloatManager.HTTP_LOG_WHAT, null));
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        activityCount++;
        if (activityCount > 0) {
            FloatManager.getInstance(FloatApp.this).appToForeground();
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        activityCount--;
        if (activityCount == 0) {
            FloatManager.getInstance(FloatApp.this).appToBackground();
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
