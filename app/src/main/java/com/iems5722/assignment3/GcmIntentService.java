package com.iems5722.assignment3;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class GcmIntentService extends IntentService {
    // A tag which will be used on logging
    private static final String TAG =
            GcmIntentService.class.getClass().getSimpleName();

    // An unique ID defined by our application
    public static final int NOTIFICATION_ID = 1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    protected void sendNotification(boolean success, GCMMessage msg) {
        // If success, send notification based on given message
        // If not success, send error notification

        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, MainActivity.class),
                0
        );
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setSound(alarmSound)
                        .setVibrate(new long[]{100, 1000, 1000, 1000, 1000})
                        .setContentTitle(msg.getTitle())
                        .setContentText(msg.getDescription())
                        .setContentIntent(contentIntent)
                        .setSmallIcon(
                                success ?
                                        R.drawable.model1 :
                                        R.drawable.ic_red_cross
                        );

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        // Determine whether incoming message is correct or not
        boolean notificationSuccess = false;

        // A reference for storing incoming message
        GCMMessage msg = null;

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(
                    messageType
            )) {
                // Error handler
                Log.e(
                        TAG,
                        String.format("GCM send error = |%s|", extras.toString())
                );
                notificationSuccess = false;
                msg = new GCMMessage(null, "GCM send error", extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(
                    messageType
            )) {
                // Delete handler
                Log.e(
                        TAG,
                        String.format("GCM delete msg = |%s|", extras.toString())
                );
                notificationSuccess = false;
                msg = new GCMMessage(null, "GCM delete msg", extras.toString());
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
                        Log.d(
                                TAG,
                                String.format(
                                        "GCM msg.url = |%s|",
                                        serverURL.toString()
                                )
                        );
                        Log.d(
                                TAG,
                                String.format(
                                        "GCM msg.desc = |%s|",
                                        desc
                                )
                        );
                        Log.d(
                                TAG,
                                String.format(
                                        "GCM msg.title = |%s|",
                                        title
                                )
                        );
                    }
                    msg = new GCMMessage(serverURL, title, desc);
                    notificationSuccess = true;
                } catch (MalformedURLException e) {
                    Log.e(TAG, "IEMS GCM Server url malformed");
                }
            }
        } else {
            Log.e(TAG, "Bundle from IEMS GCM server is wrong");
            notificationSuccess = false;
            msg = new GCMMessage(
                    null,
                    "GCM server error",
                    "Bundle from IEMS GCM is wrong"
            );
        }

        // Send notification anyway
        this.sendNotification(notificationSuccess, msg);

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
