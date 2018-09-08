package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

import com.example.stucollyn.nfc_play.LoginDialogFragment;

/**
 * Created by StuCollyn on 08/09/2018.
 */

public class ShowStoryContent {

    MediaPlayer mPlayer;
    Context context;
    Activity activity;

    public ShowStoryContent(MediaPlayer mPlayer, Context context, Activity activity) {

        this.mPlayer = mPlayer;
        this.context = context;
        this.activity = activity;

        showStoryContentDialog();
    }

    public void showStoryContentDialog() {
        // Create an instance of the dialog fragment and show it
        FragmentManager ft = ((FragmentActivity)activity).getSupportFragmentManager();
        DialogFragment newFragment = new ShowStoryContentDialog();
        newFragment.setCancelable(false);
        newFragment.show(ft, "mydialog");
    }
}
