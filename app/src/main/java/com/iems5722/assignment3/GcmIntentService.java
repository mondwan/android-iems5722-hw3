package com.iems5722.assignment3;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class GcmIntentService extends IntentService {
    // A tag which will be used on logging
    private static final String TAG =
            GcmIntentService.class.getClass().getSimpleName();


    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(
                    messageType
            )) {
                // Error handler
                Log.e(
                        TAG,
                        String.format("GCM send error = |%s|", extras.toString())
                );
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(
                    messageType
            )) {
                // Delete handler
                Log.e(
                        TAG,
                        String.format("GCM delete msg = |%s|", extras.toString())
                );
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(
                    messageType
            )) {
                // Regular message handler

                // We need following keys from extras
                // {
                //   url: http,
                //   desc: string,
                //   title: string
                // }
                URL serverURL;
                String desc;
                String title;
                try {
                    serverURL = new URL(extras.getString("url"));
                    desc = extras.getString("desc");
                    title = extras.getString("title");
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, String.format("GCM msg.url = |%s|", serverURL.toString()));
                        Log.d(TAG, String.format("GCM msg.desc = |%s|", desc));
                        Log.d(TAG, String.format("GCM msg.title = |%s|", title));
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "IEMS GCM Server url malformed");
                }

            }
        } else {
            Log.e(TAG, "Bundle from IEMS GCM server is wrong");
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
