package com.iems5722.assignment3;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a list of faked GCMMessage
        ArrayList<GCMMessage> myMessages = new ArrayList<GCMMessage>();
        int i = 0;

        for (i = 0; i < 10; i++) {
            myMessages.add(new GCMMessage(
                    String.format("url%d", i),
                    String.format("title%d", i),
                    String.format("description%d", i)
            ));
        }

        // Get the listview
        ListView listView = (ListView) this.findViewById(R.id.GCMMessageListView);
        listView.setAdapter(
                new GCMMessageAdapter(
                        this, R.layout.gcm_message_item_view, myMessages
                )
        );
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
}
