package com.iems5722.assignment3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    // A tag which will be used on logging
    private static final String TAG =
            MainActivity.class.getClass().getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String PROPERTY_APP_VERSION = "appVersion";

    public static final String PROPERTY_REG_ID = "registration_id";

    // This is the registration ID on GCM.
    // Default value is a empty string
    protected String GCM_REG_ID = "";

    // A reference of GCM instance
    // Default value is null before initialization
    protected GoogleCloudMessaging gcm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Checkout whether we have play service or not
        if (this.checkPlayService()) {
            // There is a play service
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "There is a play service");
            }

            // Short hand gcm variable
            GoogleCloudMessaging gcm;
            gcm = this.gcm = GoogleCloudMessaging.getInstance(this);

            Context ctx = this.getApplicationContext();
            this.GCM_REG_ID = this.getRegistrationId(ctx);
            if (this.GCM_REG_ID.isEmpty()) {
                // There is no such registration at first
                // We need to register a new one
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "We are going to register a GCM ID");
                }

                // Register a new ID
                GCMRegister gcmRegister = new GCMRegister(this);
                gcmRegister.execute();
            }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, String.format("reg_id=|%s|", this.GCM_REG_ID));
            }
        } else {
            // There is no play service
            Log.e(TAG, "There is no play service!");
        }

        this.publishGCMMessages(this.getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.checkPlayService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.publishGCMMessages(intent);
    }

    protected void publishGCMMessages(Intent intent) {
        // Create a list of GCM message
        ArrayList<GCMContentStorage> myMessages = new ArrayList<>();

        // Checkout whether there are any json data from Intent
        Bundle b = intent.getExtras();
        if (b != null) {
            if (BuildConfig.DEBUG) {
                Log.d(
                        TAG,
                        "There are json data"
                );
            }
            String jsonArrayString = b.getString("data");

            try {
                JSONArray jsonArray = new JSONArray(jsonArrayString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String title = jsonArray.getJSONObject(i).getString(
                            "title"
                    );
                    String description = jsonArray.getJSONObject(i).getString(
                            "desc"
                    );
                    String urlString = jsonArray.getJSONObject(i).getString(
                            "image"
                    );

                    URL url;

                    try {
                        url = new URL(urlString);
                        myMessages.add(
                                new GCMContentStorage(
                                        url,
                                        title,
                                        description
                                )
                        );
                    } catch (MalformedURLException e) {
                        Log.e(
                                TAG,
                                String.format(
                                        "json.image url errors |%s|",
                                        e.getMessage())
                        );
                    }

                }
            } catch (JSONException e) {
                Log.e(
                        TAG,
                        String.format(
                                "Data JSON errors |%s|",
                                e.getMessage()
                        )
                );
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(
                        TAG,
                        "There is no json data"
                );
            }
        }

        // Get the GCMMessageListView
        ListView listView = (ListView) this.findViewById(R.id.GCMMessageListView);

        // Link up our array with the adapter
        listView.setAdapter(
                new GCMMessageAdapter(
                        this, R.layout.gcm_message_item_view, myMessages
                )
        );
    }

    protected boolean checkPlayService() {
        // Check the device to make sure it has the Google Play Services APK. If
        // it doesn't, display a dialog that allows users to download the APK from
        // the Google Play Store or enable it in the device's system settings.

        boolean ret = true;

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(
                        resultCode,
                        this,
                        PLAY_SERVICES_RESOLUTION_REQUEST
                ).show();
            } else {
                Log.e(TAG, "This device is not supported.");
                // Close this activity
                this.finish();
            }
            ret = false;
        }

        return ret;
    }

    protected String getRegistrationId(Context context) {
        // Gets the current registration ID for application on GCM service.
        // If result is empty, the app needs to register.
        //
        // @param context Context
        // @return String
        //   Empty string if there is no such registration

        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences() {
        // @return Application's {@code SharedPreferences}.

        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(
                MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    public void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences();
        int appVersion = getAppVersion(context);

        if (BuildConfig.DEBUG) {
            Log.d(
                    TAG,
                    String.format(
                            "Saving regId |%s| on app version |%d|",
                            regId,
                            appVersion
                    )
            );
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    protected int getAppVersion(Context context) {
        // Application's version code from the {@code PackageManager}.
        //
        // @return int
        try {
            PackageInfo packageInfo =
                    context.getPackageManager().getPackageInfo(
                            context.getPackageName(),
                            0
                    );
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
