package com.iems5722.assignment3;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    // A tag which will be used on logging
    private static final String TAG =
            GcmBroadcastReceiver.class.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(
                context.getPackageName(),
                GcmIntentService.class.getName()
        );
        // Start the service, keeping the device awake while it is launching.
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Receive a broadcast from gcm");
        }
        this.startWakefulService(context, (intent.setComponent(comp)));
        this.setResultCode(Activity.RESULT_OK);
    }
}
