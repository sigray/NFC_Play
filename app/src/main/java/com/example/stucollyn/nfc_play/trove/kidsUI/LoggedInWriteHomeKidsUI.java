package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

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

    private Camera mCamera;
    private CameraPreview mPreview;

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
    boolean authenticated = false;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    Date FireStoreTime;
    FirebaseStorage storage;
    private StorageReference mStorageRef;
    boolean recordingStatus = false;
    boolean currentlyRecording = false;
    CommentaryInstruction commentaryInstruction;
    Handler archiveStoryHandler;
    ImageButton captureButton;
    FrameLayout preview;


    //Grant permission to record audio (required for some newer Android devices)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case CAMERA_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        recordButton = (ImageView) findViewById(R.id.record);
        cameraButton = (ImageView) findViewById(R.id.camera);
        archive = (ImageView) findViewById(R.id.archive);
        back = (ImageView) findViewById(R.id.back);
        captureButton = (ImageButton) findViewById(R.id.button_capture);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        authenticated = (Boolean) getIntent().getExtras().get("Authenticated");
        archiveStoryHandler = new Handler();
        nfcInteraction = new NFCInteraction(this, this);
        commentaryInstruction = new CommentaryInstruction(this, this, false, authenticated);
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


    //Animation and Layout Setup
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



    //Handle NFC Interactions

            @Override
    protected void onNewIntent(Intent intent){
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, "Object Found.", Toast.LENGTH_LONG ).show();
        }

        if(newStoryReady) {
            boolean success = nfcInteraction.doWrite(mytag, tag_data);

            if(success) {

                CancelStoryArchiveHandlerTimer();
                commentaryInstruction.setTagData(tag_data);
                commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.recorddone1), true, LoggedInReadHomeKidsUI.class);
            }
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
    }

    void StoryReset() {

        //Delete Any Previous Recordings


        //Remove Previous Audio Commentary Callbacks
        CancelStoryArchiveHandlerTimer();
    }

    //Recording Audio Management
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
                    Log.i("Tag", "I'm a barbie girl");
                }

                else {
                    Log.i("Tag", "In a barbie world");
                    animationHandler.removeCallbacks(RecordButtonRunnable);
                    recordButton.setImageDrawable(recordButtonNonAnim);
                }
            }
        };

        animationHandler.post(RecordButtonRunnable);

    }

    void recordingManager(View view) {

        try {


            if (!recordingStatus) {

                StoryReset();
                SetupStoryLocation();
                slideOutViewAnimation(archive);
                recordButton.setImageDrawable(recordButtonAnim);
                recordAudio(view);
                Log.i("Tag", "Starting Recording");
            } else {
                Log.i("Tag", "Stopping Recording");
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


    //Archive Communication
    public void Archive(View view) {

        Intent intent = new Intent(LoggedInWriteHomeKidsUI.this, ArchiveKidsUI.class);
        intent.putExtra("Authenticated", authenticated);
        LoggedInWriteHomeKidsUI.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    void NewStoryArchiveHandlerTimer() {

        archiveStoryHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.i("Annoying Handler", "Ach");
                commentaryInstruction.setInputHandler(archiveStoryHandler);
                commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.recorddone1), true, ArchiveKidsUI.class);
            }
        }, 120000);
    }

    void CancelStoryArchiveHandlerTimer() {

        archiveStoryHandler.removeCallbacksAndMessages(null);
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static Camera getCameraInstance() {

        Camera c = null;
        try {
            c = Camera.open(1); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.i("Tag", "No Camera here mate");
        }
        return c; // returns null if camera is unavailable
    }

    //Camera Management
    public void Camera(View view) {

        try {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
            }

            cameraRecorder = new CameraRecorder(this, this, story_directory);

            boolean hasCam = checkCameraHardware(this);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                mCamera = getCameraInstance();
                mPreview = new CameraPreview(getApplicationContext(), mCamera);
                mPreview.setVisibility(View.VISIBLE);
                cameraButton.setVisibility(View.VISIBLE);
                preview.addView(mPreview);
                captureButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // get an image from the camera
                                mCamera.takePicture(null, null, mPicture);
                            }
                        }
                );

            }
        }, 500);
    } catch (NullPointerException e) {

        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            cameraButton.setVisibility(View.GONE);
            preview.setVisibility(View.GONE);
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d("Tag", "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("Tag", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("Tag", "Error accessing file: " + e.getMessage());
            }
        }
    };


    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
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
                commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.attachtag_app), false, null);
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
            NewStoryArchiveHandlerTimer();
        }
    }



    //Activity Governance

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

    public void Back(View view) {

        onBackPressed();
    }

    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public void onBackPressed() {

        CancelStoryArchiveHandlerTimer();
        animationBackHandler.removeCallbacksAndMessages(null);
        back.setImageDrawable(backRetrace);
        backRetrace.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoggedInWriteHomeKidsUI.this, LoggedInReadHomeKidsUI.class);
                intent.putExtra("PreviousActivity", "LoggedInWriteHomeKidsUI");
                intent.putExtra("Authenticated", authenticated);
                intent.putExtra("NewStory", false);
                LoggedInWriteHomeKidsUI.this.startActivity(intent);
//                overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
            }
        }, 1000);

    }
}


//
//
//    void AttachToNFCInstruction() {
//
//        Uri audioFileUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.attachtag_app);
//        onPlay(audioFileUri);
//    }
//
//    /*When audio playback buttons are selected for first time, setup new audio media player. When
//    user interacts with playback buttons after audio media player has already been setup, toggle
//    between media player pause and play*/
//    public void onPlay(Uri audioFileUri) {
//
//        setupAudioMediaPlayer(audioFileUri);
//        if (!playbackStatus) {
//            startPlaying();
//            playbackStatus = true;
//        }
//    }
//
//    //Setup new audio media player drawing from audio file location
//    protected void setupAudioMediaPlayer(Uri audioFileUri) {
//        Log.i("audio file", audioFileName);
//
//        try {
//            mPlayer.setDataSource(this, audioFileUri);
//            mPlayer.prepare();
//            mPlayerSetup = true;
//        } catch (IOException e) {
//            Log.e("Error", "prepare() failed");
//        }
//    }
//
//    //Start audio media player and start listening for stop imageView to be pressed
//    public void startPlaying() {
//        mPlayer.start();
//        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            public void onCompletion(MediaPlayer mp) {
//
//                mPlayer.stop();
//                mPlayer.reset();
//                playbackStatus = false;
//            }
//        });
//    }
//
//

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