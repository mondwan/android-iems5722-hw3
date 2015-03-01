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

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class GCMIntentService extends IntentService {
    // A tag which will be used on logging
    private static final String TAG =
            GCMIntentService.class.getClass().getSimpleName();

    // An unique ID defined by our application
    public static final int NOTIFICATION_ID = 1;

    // A reference for storing logging message if any
    protected String loggingMessage;

    public GCMIntentService() {
        super("GCMIntentService");
    }

    protected void sendNotification(boolean success, GCMContentStorage msg) {
        // If success, send notification based on given message
        // If not success, send error notification

        // Get a reference for notification manager
        NotificationManager notificationManager =
                (NotificationManager)
                        this.getSystemService(Context.NOTIFICATION_SERVICE);

        // Get default alarm sound from system
        Uri alarmSound = RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION
        );

        // Define an activity we will go after pressing the notification
        Intent intent = new Intent(this, MainActivity.class);

        // Fetch JSON data from GCM and pack them into a bundle so that we
        // can send back data to the activity we defined later on.
        //
        // Note: Since we are not working on UI thread, below method is a
        // blocking call for simplicity
        if (success) {
            String data = this.fetchDataFromGCMURL(msg);

            // Pack data into this intent
            Bundle b = new Bundle();
            b.putString("data", data);
            intent.putExtras(b);
        }

        // Wraps our intent with PendingIntent which required by notification
        // manager
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Setup notification object
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

        // Prompt notification
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    protected String fetchDataFromGCMURL(GCMContentStorage msg) {
        // Expect to get a JSON reply from msg.url via HTTP. Grammar of this
        // JSON should be like to following.
        //
        // [{
        //   title: string,
        //   desc: string,
        //   image: url,
        // }, ...]
        //
        // @param msg GCMContentStorage
        // @return String
        //   Empty json array in plain text will be return if there are any errors
        //   Otherwise, json array in plain text will be return

        String ret;
        JSONArray jsonArray = null;
        this.loggingMessage = "";

        try {
            URL serverURL = msg.getUrl();

            HttpURLConnection urlSocket =
                    (HttpURLConnection) serverURL.openConnection();

            try {
                // Read response
                InputStream in = new BufferedInputStream(
                        urlSocket.getInputStream()
                );
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in));

                String response = reader.readLine();
                try {
                    jsonArray = new JSONArray(response);

                    if (BuildConfig.DEBUG) {
                        Log.d(
                                TAG,
                                String.format(
                                        "jsonArray=|%s|",
                                        jsonArray.toString()
                                )
                        );
                    }
                } catch (JSONException e) {
                    this.loggingMessage = e.getMessage();
                    Log.e(TAG, String.format("JSON error |%s|", this.loggingMessage));
                    jsonArray = null;
                } finally {
                    reader.close();
                }
            } finally {
                urlSocket.disconnect();
            }
        } catch (IOException e) {
            Log.e(TAG, String.format("Server error |%s|", e.getMessage()));

            this.loggingMessage = e.getMessage();
            jsonArray = null;
        }

        if (jsonArray != null) {
            // Everything runs smoothly
            ret = jsonArray.toString();
        } else {
            // There are errors for parsing
            ret = "[]";

            // Alter message title and description in order to show errors
            msg.setTitle("Data JSON from IEMS server errors");
            msg.setDescription(this.loggingMessage);
        }

        return ret;
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
        GCMContentStorage msg = null;

        if (!extras.isEmpty()) {
            switch (messageType) {
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    // Error handler
                    Log.e(
                            TAG,
                            String.format("GCM send error = |%s|", extras.toString())
                    );
                    notificationSuccess = false;
                    msg = new GCMContentStorage(null, "GCM send error", extras.toString());
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    // Delete handler
                    Log.e(
                            TAG,
                            String.format("GCM delete msg = |%s|", extras.toString())
                    );
                    notificationSuccess = false;
                    msg = new GCMContentStorage(null, "GCM delete msg", extras.toString());
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
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
                        msg = new GCMContentStorage(serverURL, title, desc);
                        notificationSuccess = true;
                    } catch (MalformedURLException e) {
                        Log.e(TAG, "IEMS GCM Server url malformed");
                    }
                    break;
            }
        } else {
            Log.e(TAG, "Bundle from IEMS GCM server is wrong");
            notificationSuccess = false;
            msg = new GCMContentStorage(
                    null,
                    "GCM server error",
                    "Bundle from IEMS GCM is wrong"
            );
        }

        // Send notification anyway
        this.sendNotification(notificationSuccess, msg);

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }
}
