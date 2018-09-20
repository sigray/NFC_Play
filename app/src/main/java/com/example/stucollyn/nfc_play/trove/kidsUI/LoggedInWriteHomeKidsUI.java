package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.stucollyn.nfc_play.NewStoryReview;
import com.example.stucollyn.nfc_play.NewStorySaveMetadata;
import com.example.stucollyn.nfc_play.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class LoggedInWriteHomeKidsUI extends AppCompatActivity {

    ImageView recordButton, cameraButton, archive, back;
//    AnimatedVectorDrawable d;
    //Request Code Variables
    //General Variables
    boolean record_button_on, video_record_button_on,
            playbackStatus = false, mPlayerSetup = false, fullSizedPicture = false,
            permissionToRecordAccepted = false, isFullSizedVideo = false;
    //File Save Variables

    AudioRecorderKidsUI audioRecorder;
    AnimatedVectorDrawable recordButtonAnim, backRetrace;
    Drawable recordButtonNonAnim;
    Handler animationHandler, animationBackHandler;
    Runnable RecordButtonRunnable;
    private MediaPlayer mPlayer = null;
    String photoPath;
    Animation slideout, slidein;

    //Request Code Variables
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    //File Save Variables
    private static String audioFileName = null, pictureFileName = null, videoFileName = null;
    File image, video;
    Uri videoURI, photoUri;
    File story_directory;
    String story_directory_path;
    Uri story_directory_uri;
    String tag_data = null;
    NFCInteraction nfcInteraction;
    Tag mytag;
    boolean newStoryReady = false;
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    //Classes
    CameraRecorder cameraRecorder;
    boolean authenticated;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    Date FireStoreTime;
    FirebaseStorage storage;
    private StorageReference mStorageRef;
    boolean recordingStatus = false;
    boolean currentlyRecording = false;

    //Grant permission to record audio (required for some newer Android devices)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }

        if (!permissionToRecordAccepted) {

            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_write_home);

        recordButton = (ImageView) findViewById(R.id.record);
        cameraButton = (ImageView) findViewById(R.id.camera);
        archive = (ImageView) findViewById(R.id.archive);
        back = (ImageView) findViewById(R.id.back);

        authenticated = false;

        //Prepare new story directory
        mPlayer = new MediaPlayer();
        nfcInteraction = new NFCInteraction(this, this);
        AnimationSetup();
        recordButtonController();

        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] {
                tagDetected
        };
    }


    //Animation Setup
    void AnimationSetup() {

        recordButtonAnim = (AnimatedVectorDrawable) getDrawable(R.drawable.kids_ui_record_anim_alt);
        recordButtonNonAnim = (Drawable) getDrawable(R.drawable.kids_ui_record_circle);
        backRetrace = (AnimatedVectorDrawable) getDrawable(R.drawable.kids_ui_back_anim_retrace);
        slideout = AnimationUtils.loadAnimation(this, R.anim.slideout);
        slidein = AnimationUtils.loadAnimation(this, R.anim.slidein);

        animationBackHandler = new Handler();
        animationBackHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                back.setVisibility(View.VISIBLE);
                Drawable d = back.getDrawable();
                final AnimatedVectorDrawable zigzaganim = (AnimatedVectorDrawable) d;
                zigzaganim.start();
            }
        }, 2000);

        animationBackHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Drawable d = VectorDrawableCompat.create(getResources(), R.drawable.kids_ui_back, null);
                d = DrawableCompat.wrap(d);
                DrawableCompat.setTint(d, Color.WHITE);
                back.setImageDrawable(d);

            }
        }, 3000);
    }

    void slideOutViewAnimation(View view) {

        int visibility = view.getVisibility();

        if(visibility==View.VISIBLE){

            view.startAnimation(slideout);
            view.setVisibility(View.INVISIBLE);
        }
    }

    void slideInViewAnimation(View view) {

        int visibility = view.getVisibility();

        if(visibility==View.INVISIBLE){

            view.startAnimation(slidein);
            view.setVisibility(View.VISIBLE);
        }
    }

    void recordButtonController() {

        recordButton.setOnTouchListener((new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

//                final View view = v;
//                final Handler handler;
//                Runnable mLongPressed;
//                handler = new Handler();
//                mLongPressed = new Runnable() {
//                    public void run() {
//                        Log.i("", "Long press!");
//                        recordingManager(view, false);
//                    }
//                };

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        handler.postDelayed(mLongPressed, ViewConfiguration.getLongPressTimeout());
                        currentlyRecording = true;
                        recordingStatus = false;
                        recordingManager(v);
                        recordButtonAnimationController();
                        break;
                    case MotionEvent.ACTION_UP:
//                        handler.removeCallbacks(mLongPressed);
                        currentlyRecording = false;
                        recordingStatus = true;
                        recordingManager(v);
                        recordButtonAnimationController();
                        break;
                }

                return true;
            }
        }));
    }

    void paintViews() {


        int paintColour = android.graphics.Color.rgb(253, 195, 204);
        Drawable d = VectorDrawableCompat.create(getResources(), R.drawable.kids_ui_back_anim, null);
            d = DrawableCompat.wrap(d);
            DrawableCompat.setTint(d, paintColour);
            back.setImageDrawable(d);
        }

            @Override
    protected void onNewIntent(Intent intent){
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, "Tag Discovered", Toast.LENGTH_LONG ).show();
            Log.i("Hello", "Found it");
        }

        if(newStoryReady) {
            nfcInteraction.doWrite(mytag, tag_data);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        nfcInteraction.WriteModeOff(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        nfcInteraction.WriteModeOn(adapter, pendingIntent, writeTagFilters);
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

    void recordAudio(View view) {

        //Request permission to record audio (required for some newer Android devices)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        try {
            audioRecorder = new AudioRecorderKidsUI(this, story_directory);
            audioRecorder.startRecording();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        audioFileName = audioRecorder.getAudioFileName();
    }

    void recordButtonAnimationController() {

        animationHandler = new Handler();

        //Runnable to handle idle trove animation
        RecordButtonRunnable = new Runnable() {

            @Override
            public void run() {
                recordButtonAnim.start();

                if(currentlyRecording) {
                    animationHandler.postDelayed(this, 1000);
                }

                else {
                    animationHandler.removeCallbacks(RecordButtonRunnable);
                }
            }
        };

        animationHandler.post(RecordButtonRunnable);

    }

    void recordingManager(View view) {

        try {


            if (!recordingStatus) {

                Log.i("Start recording", "Started");
                Log.i("Recording Status", String.valueOf(recordingStatus));
                SetupStoryLocation();
                slideOutViewAnimation(archive);
                recordButton.setImageDrawable(recordButtonAnim);
                recordAudio(view);
            } else {

                Log.i("Stop recording", "Stopped");
                slideInViewAnimation(cameraButton);
                audioRecorder.stopRecording();
                animationHandler.removeCallbacks(RecordButtonRunnable);
                recordButton.setImageDrawable(recordButtonNonAnim);
                newStoryReady = true;
            }

            recordingStatus = !recordingStatus;

        }

        catch (RuntimeException r) {

        }
    }

    void AttachToNFCInstruction() {

        Uri audioFileUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.attachtag_app);
        onPlay(audioFileUri);
    }

    /*When audio playback buttons are selected for first time, setup new audio media player. When
    user interacts with playback buttons after audio media player has already been setup, toggle
    between media player pause and play*/
    public void onPlay(Uri audioFileUri) {

        setupAudioMediaPlayer(audioFileUri);
        if (!playbackStatus) {
            startPlaying();
            playbackStatus = true;
        }
    }

    //Setup new audio media player drawing from audio file location
    protected void setupAudioMediaPlayer(Uri audioFileUri) {
        Log.i("audio file", audioFileName);

        try {
            mPlayer.setDataSource(this, audioFileUri);
            mPlayer.prepare();
            mPlayerSetup = true;
        } catch (IOException e) {
            Log.e("Error", "prepare() failed");
        }
    }

    //Start audio media player and start listening for stop imageView to be pressed
    public void startPlaying() {
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {

                mPlayer.stop();
                mPlayer.reset();
                playbackStatus = false;
            }
        });
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

    public void Camera(View view) {

        try {

            cameraRecorder = new CameraRecorder(this, this, story_directory);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                cameraRecorder.dispatchTakePictureIntent();
            }
        }, 500);
    } catch (NullPointerException e) {

        }
    }

    public void CompletePictureRecording(View view) {

        pictureFileName = photoPath;
//        recordedMediaHashMap.put("Picture", pictureFileName);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //Picture Processing
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

                new LoggedInWriteHomeKidsUI.ProcessPicture().execute();
                slideOutViewAnimation(cameraButton);
                slideInViewAnimation(archive);
                AttachToNFCInstruction();
                UUID objectUUID = UUID.randomUUID();

                if(authenticated) {
                    SaveToCloud saveToCloud = new SaveToCloud(story_directory, objectUUID);
                    saveToCloud.CloudSave();
                }
            }
        }

        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {

//                new LoggedInWriteHomeKidsUI.ProcessVideo().execute();

            }
        }
    }

    public void Archive(View view) {

        Intent intent = new Intent(LoggedInWriteHomeKidsUI.this, ArchiveKidsUI.class);
        LoggedInWriteHomeKidsUI.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    class ProcessPicture extends AsyncTask<View, Void, Void> {

        Bitmap processedBitmap;

        @Override
        protected Void doInBackground(View... params) {

//            imageView = params[0];

            try {

                cameraRecorder.PictureProcessing();
                photoPath = cameraRecorder.getPhotoPath();
                photoUri = cameraRecorder.getPhotoURI();
//                picture_story_fragment.setPictureBoxDimensions(pictureRecorder.getRotationInDegrees());
                processedBitmap = cameraRecorder.getAdjustedBitmap();

            } catch (NullPointerException e) {

            } catch (IllegalArgumentException e) {

            }

            return null;

        }

        protected void onPostExecute(Void result) {

            newStoryReady = true;
        }
    }

    public void Back(View view) {

        onBackPressed();
    }

    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public void onBackPressed() {

        animationBackHandler.removeCallbacksAndMessages(null);
        back.setImageDrawable(backRetrace);
        backRetrace.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoggedInWriteHomeKidsUI.this, LoggedInReadHomeKidsUI.class);
                intent.putExtra("PreviousActivity", "LoggedInWriteHomeKidsUI");
                LoggedInWriteHomeKidsUI.this.startActivity(intent);
//                overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
            }
        }, 1000);

    }
}
