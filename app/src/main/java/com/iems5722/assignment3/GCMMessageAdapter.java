package com.iems5722.assignment3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mondwan on 14/2/15.
 */
public class GCMMessageAdapter extends ArrayAdapter<GCMMessage> {
    // An adapter for publishing contents in Class::GCMMessage to ListView

    private static final String TAG =
            GCMMessageAdapter.class.getClass().getSimpleName();

    public GCMMessageAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public GCMMessageAdapter(
            Context context, int resource, List<GCMMessage> messages) {
        super(context, resource, messages);
    }

    // Override default getView method so that it can publish our contents
    // into our custom view
    //
    // @param pos int
    //   The position of the item within the adapter's data set of the item
    //   whose view we want.
    // @param convertView
    //   The old view to reuse, if possible.
    // @param parent
    //   The parent that this view will eventually be attached to
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        // Checkout whether the old view is available or not
        if (convertView == null) {
            Context c = this.getContext();
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE
            );
            convertView = inflater.inflate(R.layout.gcm_message_item_view, parent, false);
        }

        // Get the GCMMessage reference
        GCMMessage msg = this.getItem(pos);

        TextView titleView = (TextView) convertView.findViewById(
                R.id.GCMMessageTitle
        );

        TextView descriptionView = (TextView) convertView.findViewById(
                R.id.GCMMessageDescription
        );

        ImageView pictureView = (ImageView) convertView.findViewById(
                R.id.GCMMessagePicture
        );

        pictureView.setImageResource(R.drawable.model1);
        titleView.setText(msg.getTitle());
        descriptionView.setText(msg.getDescription());

        return convertView;
    }
}
