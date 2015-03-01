package com.iems5722.assignment3;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
    // A tag which will be used on logging
    private static final String TAG =
            GCMBroadcastReceiver.class.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GCMIntentService will handle the intent.
        ComponentName comp = new ComponentName(
                context.getPackageName(),
                GCMIntentService.class.getName()
        );
        // Start the service, keeping the device awake while it is launching.
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Receive a broadcast from gcm");
        }
        this.startWakefulService(context, (intent.setComponent(comp)));
        this.setResultCode(Activity.RESULT_OK);
    }
}
