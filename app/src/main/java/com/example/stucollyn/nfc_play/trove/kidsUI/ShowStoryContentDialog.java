package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.stucollyn.nfc_play.LoginDialogFragment;
import com.example.stucollyn.nfc_play.R;

/**
 * Created by StuCollyn on 08/09/2018.
 */

public class ShowStoryContentDialog extends DialogFragment {

//    /* The activity that creates an instance of this dialog fragment must
//   * implement this interface in order to receive event callbacks.
//   * Each method passes the DialogFragment in case the host needs to query it. */
//    public interface NoticeLoginDialogListener {
//        public void onLoginDialogPositiveClick(String username, String password);
//        public void onLoginDialogNegativeClick(DialogFragment dialog);
//    }
//
//    // Use this instance of the interface to deliver action events
//    LoginDialogFragment.NoticeLoginDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_signin, null));
        View prompt = inflater.inflate(R.layout.activity_view_story_content_kids_ui, null);
        builder.setView(prompt);
        ImageView imageView = (ImageView) prompt.findViewById(R.id.imageView);
        return builder.create();
    }

    public void Close(View view) {

        this.dismiss();
    }
}
