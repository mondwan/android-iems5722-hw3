package com.iems5722.assignment3;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    // A tag which will be used on logging
    private static final String TAG =
            MainActivity.class.getClass().getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.publishFakedGCMMessages();

        // Checkout whether we have play service or not
        if (this.checkPlayService()) {
            // There is a play service
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "There is a play service");
            }
        } else {
            // There is no play service
            Log.e(TAG, "There is no play service!");
        }
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

    protected void publishFakedGCMMessages() {
        // Create a list of faked GCMMessage
        ArrayList<GCMMessage> myMessages = new ArrayList<>();
        int i;

        for (i = 0; i < 10; i++) {
            myMessages.add(new GCMMessage(
                    String.format("url%d", i),
                    String.format("title%d", i),
                    String.format("description%d", i)
            ));
        }

        // Get the GCMMessageListView
        ListView listView = (ListView) this.findViewById(R.id.GCMMessageListView);

        // Link up our faked array with the adapter
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
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "This device is not supported.");
                finish();
            }
            ret = false;
        }

        return ret;
    }
}
