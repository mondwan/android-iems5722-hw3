package com.iems5722.assignment3;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationSettingDialogFragment extends DialogFragment {

    public static final int SOUND_AND_VIBRATE = 0;
    public static final int VIBRATE_ONLY = 1;
    public static final int SLIENT = 2;
    public static final int DEFAULT = 0;

    // The activity that creates an instance of this dialog fragment must
    // implement this interface in order to receive event callbacks.
    public interface NotificationSettingDialogListener {
        // Callback call with user's choice
        //
        // @param choice user
        public void processUserChoice(int choice);
    }

    // Use this instance of the interface to deliver action events
    protected NotificationSettingDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the
    // NotificationSettingDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NotificationSettingDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(
                    activity.toString() +
                            " must implement NotificationSettingDialogListener"
            );
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.action_settings)
                .setItems(
                        R.array.notificationSettings,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog,
                                    int which
                            ) {
                                // which === user's choice
                                NotificationSettingDialogFragment n =
                                        NotificationSettingDialogFragment.this;
                                n.mListener.processUserChoice(which);
                            }
                        }
                );

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
