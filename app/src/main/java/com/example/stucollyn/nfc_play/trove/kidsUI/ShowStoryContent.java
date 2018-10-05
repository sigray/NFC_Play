package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.stucollyn.nfc_play.LoginDialogFragment;
import com.example.stucollyn.nfc_play.R;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by StuCollyn on 08/09/2018.
 */

public class ShowStoryContent {

    MediaPlayer mPlayer;
    Context context;
    Activity activity;
    ShowStoryContentDialog newFragment;
    File[] filesOnTag;
    FragmentManager ft;


    public ShowStoryContent(MediaPlayer mPlayer, Context context, Activity activity, File[] filesOnTag) {

        this.mPlayer = mPlayer;
        this.context = context;
        this.activity = activity;
        this.filesOnTag = filesOnTag;
        ft = ((FragmentActivity)activity).getSupportFragmentManager();
    }

    void checkFilesOnArchive(){

        for(int i=0; i<filesOnTag.length; i++) {


            String extension = FilenameUtils.getExtension(filesOnTag[i].toString());
            String fileName = filesOnTag[i].toString();

            if(extension.equalsIgnoreCase("mp4")) {

                String substring=fileName.substring(fileName.lastIndexOf("/")+1);

                Uri story_directory_uri = FileProvider.getUriForFile(context,
                        "com.example.android.fileprovider",
                        filesOnTag[i]);

//                Uri audioFileUri = Uri.parse("android.resource://" + activity.getPackageName() + "/" + R.raw.welcome_app);


//                Log.i("Files on Tag (C)", story_directory_uri.toString());


                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(context, story_directory_uri);
                    mPlayer.prepare();
                    mPlayer.start();
                }

                catch (IOException e) {
                    Log.e("Error", "prepare() failed");
                }

            }

            if(extension.equalsIgnoreCase("jpg")) {

                showStoryContentDialog(filesOnTag[i]);
            }
        }
    }

    void checkFilesOnTag(){

        for(int i=0; i<filesOnTag.length; i++) {

            Log.i("Files on Tag", String.valueOf(filesOnTag[i].toString()));

            String extension = FilenameUtils.getExtension(filesOnTag[i].toString());
            String fileName = filesOnTag[i].toString();

            if(extension.equalsIgnoreCase("mp3")) {

                Uri story_directory_uri = FileProvider.getUriForFile(context,
                        "com.example.android.fileprovider",
                        filesOnTag[i]);

                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(context, story_directory_uri);
                    mPlayer.prepare();
                    mPlayer.start();
                }

                catch (IOException e) {
                    Log.e("Error", "prepare() failed");
                }

            }

            if(extension.equalsIgnoreCase("jpg")) {

                showStoryContentDialog(filesOnTag[i]);
            }
        }
    }

    ShowStoryContentDialog returnDialog() {

        return newFragment;
    }

    void closeOpenFragments(ShowStoryContentDialog currentFragment) {

        if(currentFragment!=null) {


            currentFragment.dismiss();
        }
    }

    void showStoryContentDialog(File imageFile) {
        // Create an instance of the dialog fragment and show it
        Bundle bundle = new Bundle();
        ft = ((FragmentActivity)activity).getSupportFragmentManager();
        newFragment = new ShowStoryContentDialog();
        bundle.putSerializable("ImageFile", imageFile);
        newFragment.setArguments(bundle);
        newFragment.setCancelable(false);
        newFragment.show(ft, "mydialog");
    }
}
