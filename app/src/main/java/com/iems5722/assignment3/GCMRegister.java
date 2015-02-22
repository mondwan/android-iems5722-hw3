package com.iems5722.assignment3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mondwan on 22/2/15.
 */
public class GCMRegister extends AsyncTask<Void, Void, Boolean> {
    // A class encapsulates logic for registering a GCM ID in a asynchronous
    // mechanism

    // A tag which will be used on logging
    private static final String TAG =
            GCMRegister.class.getClass().getSimpleName();

    // A reference to the caller activity
    protected MainActivity mainActivity;

    // A property for storing internal message;
    protected String msg = "";

    // A reference to the gcm instance
    protected GoogleCloudMessaging gcm = null;

    // This is the project number you got from the API Console, as described
    // in "Getting Started."
    protected String SENDER_ID = "145180457203";

    public GCMRegister(MainActivity mainActivity) {
        // Constructor

        super();

        // Initialize class attributes
        this.mainActivity = mainActivity;
        this.gcm = GoogleCloudMessaging.getInstance(
                this.mainActivity.getApplicationContext()
        );
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // Actual implementation for registration
        //
        // @return Boolean
        //   Whether this registration success or not
        boolean ret;

        try {
            // Register the GCM_ID
            String regID = this.gcm.register(this.SENDER_ID);

            // Send back the registration ID
            this.sendRegistrationIdToBackend(regID);

            // Store the registration ID
            Context ctx = this.mainActivity.getApplicationContext();
            this.mainActivity.storeRegistrationId(ctx, regID);

            ret = true;
        } catch (IOException e) {
            this.msg = "Error: " + e.getMessage();
            Log.e(TAG, this.msg);

            ret = false;
        }

        return ret;
    }

    protected boolean sendRegistrationIdToBackend(String regID) {
        // Send registration ID to our backend server
        //
        // @return boolean
        //   Indicate whether this operation success or not
        boolean ret;

        try {
            URL serverURL = new URL(
                    "http://iems5722v.ie.cuhk.edu.hk:8080/gcm_register.php"
            );
            HttpURLConnection urlSocket =
                    (HttpURLConnection) serverURL.openConnection();

            try {
                // Set POST request
                urlSocket.setDoOutput(true);

                // Define POST payload
                String payload = String.format(
                        "sid=%s&gcm_id=%s", "1155002613", regID);
                urlSocket.setRequestProperty(
                        "Content-Length", String.valueOf(payload.length()));

                // Write POST data
                OutputStreamWriter writer = new OutputStreamWriter(
                        urlSocket.getOutputStream()
                );
                writer.write(payload);
                writer.flush();
                writer.close();

                // Read response
                InputStream in = new BufferedInputStream(
                        urlSocket.getInputStream());
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in));

                String response = reader.readLine();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, String.format("Status=|%s|", status));
                        Log.d(TAG, String.format("message=|%s|", message));
                    }

                    this.msg = message;

                    if (status.equals("OK")) {
                        ret = true;
                    } else {
                        ret = false;
                    }

                } catch (JSONException e) {
                    this.msg = e.getMessage();
                    Log.e(TAG, String.format("JSON error |%s|", this.msg));
                }
            } finally {
                urlSocket.disconnect();
            }

            ret = true;
        } catch (MalformedURLException e) {
            Log.e(TAG, String.format("URL error |%s|", e.getMessage()));

            this.msg = e.getMessage();
            ret = false;
        } catch (IOException e) {
            Log.e(TAG, String.format("Server error |%s|", e.getMessage()));

            this.msg = e.getMessage();
            ret = false;
        }

        return ret;
    }
}
