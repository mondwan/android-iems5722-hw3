package com.iems5722.assignment3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

/**
 * Created by mondwan on 14/2/15.
 */
public class GCMMessageAdapter extends ArrayAdapter<GCMContentStorage> {
    // An adapter for publishing contents in Class::GCMMessage to ListView

    private static final String TAG =
            GCMMessageAdapter.class.getClass().getSimpleName();

    protected DisplayImageOptions options;

    public GCMMessageAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.initializeImageLoader();
    }

    public GCMMessageAdapter(
            Context context, int resource, List<GCMContentStorage> messages) {
        super(context, resource, messages);
        this.initializeImageLoader();
    }

    protected void initializeImageLoader() {
        // Unique initializeImageLoader method for this class custom attribute
        //
        // NOTE: sourced from sample project from ImageLoader

        // Create global configuration and initializeImageLoader ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this.getContext()
        )
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(1 * 1024 * 1024) // 1Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        // Cache an option for displaying image
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.model1)
                .showImageOnFail(R.drawable.ic_red_cross)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
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
        GCMContentStorage msg = this.getItem(pos);

        TextView titleView = (TextView) convertView.findViewById(
                R.id.GCMMessageTitle
        );

        TextView descriptionView = (TextView) convertView.findViewById(
                R.id.GCMMessageDescription
        );

        ImageView pictureView = (ImageView) convertView.findViewById(
                R.id.GCMMessagePicture
        );

        ImageLoader.getInstance().displayImage(
                msg.url.toString(), pictureView, this.options);
        titleView.setText(msg.getTitle());
        descriptionView.setText(msg.getDescription());

        return convertView;
    }
}
