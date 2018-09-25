package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.example.stucollyn.nfc_play.R;

import java.io.IOException;

/**
 * Created by StuCollyn on 25/09/2018.
 */

public class CommentaryInstruction {

    Activity activity;
    Context context;
    MediaPlayer mPlayer;
    Uri audioFileUri;
    boolean playbackStatus = false;
    boolean authenticated = false;
    Handler inputHandler;
    String tag_data;


    public CommentaryInstruction(Activity activity, Context context, boolean playbackStatus, boolean authenticated) {

        this.activity = activity;
        this.context = context;
        this.playbackStatus = playbackStatus;
        this.authenticated = authenticated;

        mPlayer = new MediaPlayer();
    }

    /*When audio playback buttons are selected for first time, setup new audio media player. When
    user interacts with playback buttons after audio media player has already been setup, toggle
    between media player pause and play*/
    public void onPlay(Uri audioFileUri, final boolean onCompleteChangeActivitiy, final Class activityName) {

        setupAudioMediaPlayer(audioFileUri);
        if (!playbackStatus) {
            startPlaying(onCompleteChangeActivitiy, activityName);
            playbackStatus = true;
        }
    }

    //Setup new audio media player drawing from audio file location
    protected void setupAudioMediaPlayer(Uri audioFileUri) {

        try {

            if(mPlayer.isPlaying()) {
                mPlayer.stop();
                mPlayer.reset();
            }
            Log.i("audioFile", audioFileUri.toString());
            mPlayer.setDataSource(context, audioFileUri);
            mPlayer.prepare();
//            mPlayerSetup = true;
        } catch (IOException e) {
            Log.e("Error", "prepare() failed");
        }
    }

    //Start audio media player and start listening for stop imageView to be pressed
    public void startPlaying(final boolean onCompleteChangeActivitiy, final Class activityName) {
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {

                mPlayer.stop();
                mPlayer.reset();
                playbackStatus = false;

//                if(inputHandler!=null) {
//
//                    inputHandler.removeCallbacksAndMessages(null);
//                }

                if(onCompleteChangeActivitiy) {

                    if(activityName==LoggedInReadHomeKidsUI.class) {

                        LoggedInReadHomeKidsUI();
                    }

                    else if(activityName==ArchiveKidsUI.class) {

                        ArchiveKidsUI();
                    }

                    else {

                    }
                }
            }
        });
    }

    void ArchiveKidsUI() {

        Intent intent = new Intent(context, ArchiveKidsUI.class);
        intent.putExtra("PreviousActivity", "LoggedInWriteHomeKidsUI");
        intent.putExtra("Authenticated", authenticated);
        context.startActivity(intent);
    }

    void LoggedInReadHomeKidsUI() {

        Intent intent = new Intent(context, LoggedInReadHomeKidsUI.class);
        intent.putExtra("PreviousActivity", "LoggedInWriteHomeKidsUI");
        intent.putExtra("Authenticated", authenticated);
        intent.putExtra("NewStory", true);
        intent.putExtra("StoryRef", tag_data);
        context.startActivity(intent);
    }

    void setTagData(String data) {

        tag_data = data;
    }

    public void stopPlaying() {
        mPlayer.stop();
        mPlayer.reset();
        playbackStatus = false;
    }

    void setInputHandler(Handler input){

        inputHandler = input;
    }

    MediaPlayer getmPlayer(){

        return mPlayer;
    }

}
