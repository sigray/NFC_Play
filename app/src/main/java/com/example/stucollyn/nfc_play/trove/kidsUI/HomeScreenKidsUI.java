package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.example.stucollyn.nfc_play.AudioRecorder;
import com.example.stucollyn.nfc_play.R;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class HomeScreenKidsUI extends AppCompatActivity {

    ImageView recordButton;
//    AnimatedVectorDrawable d;
    //Request Code Variables
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    //General Variables
    boolean record_button_on, video_record_button_on, recordingStatus = false,
            playbackStatus = false, mPlayerSetup = false, fullSizedPicture = false,
            permissionToRecordAccepted = false, isFullSizedVideo = false;
    //File Save Variables
    private static String audioFileName = null, pictureFileName = null, videoFileName = null;
    File image, video;
    Uri videoURI, photoUri;
    File story_directory;
    String story_directory_path;
    Uri story_directory_uri;
    String tag_data;
    AudioRecorderKidsUI audioRecorder;
    AnimatedVectorDrawable recordButtonAnim;
    Drawable recordButtonNonAnim;
    Handler animationHandler;
    Runnable RecordButtonRunnable;

    //Grant permission to record audio (required for some newer Android devices)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_kids_ui);

        recordButton = (ImageView) findViewById(R.id.record);
//        d = (AnimatedVectorDrawable) getDrawable(R.drawable.kids_ui_record_anim); // Insert your AnimatedVectorDrawable resource identifier
        recordButtonAnim = (AnimatedVectorDrawable) getDrawable(R.drawable.kids_ui_record_anim);
        recordButtonNonAnim = (Drawable) getDrawable(R.drawable.kids_ui_record_circle);
    }

    public void Record(View view) {

       animateRecordButton();
       recordAudio(view);
    }

    void recordAudio(View view) {

        //Request permission to record audio (required for some newer Android devices)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        recordingStatus = !recordingStatus;
        onRecord(recordingStatus, view);
        audioFileName = audioRecorder.getAudioFileName();
    }

    /*When audio recording is started, try to startRecording(). When audio recording is stopped,
    stopRecording() and change fragment views accordingly*/
    public void onRecord(boolean start, View view) {
        if (start) {

            try {
                audioRecorder = new AudioRecorderKidsUI(this, story_directory);
                audioRecorder.startRecording();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

        } else {
            audioRecorder.stopRecording();
        }
    }


    //Setup new storage folder
    private void SetupStoryLocation() {

        String packageLocation = ("/Stories");
        String timeStamp = new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH).format(new Date());
        String name = UUID.randomUUID().toString();

        tag_data = name;
        String newDirectory = packageLocation + "/" + name;
        story_directory = getExternalFilesDir(newDirectory);
        // story_directory_uri = FileProvider.getUriForFile(this,
        //       "com.example.android.fileprovider",
        //     story_directory);
        story_directory_path = story_directory.getAbsolutePath();
    }

    void animateRecordButton() {

        if(!recordingStatus) {
            recordButton.setImageDrawable(recordButtonAnim);

            animationHandler = new Handler();

            //Runnable to handle idle trove animation
            RecordButtonRunnable = new Runnable() {

                @Override
                public void run() {
                    recordButtonAnim.start();
                    animationHandler.postDelayed(this, 2000);
                }
            };

            animationHandler.post(RecordButtonRunnable);
        }

        else {

            animationHandler.removeCallbacks(RecordButtonRunnable);
            recordButton.setImageDrawable(recordButtonNonAnim);
        }


/*
            new Thread(new Runnable() {
                public void run() {
                    while (recordButton != null) {
                        try {
                            recordButton.post(new Runnable() {
                                @Override
                                public void run() {
                                    recordButtonAnim.start();
                                }
                            });
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            */
    }

    public void Camera(View view) {

    }
}
