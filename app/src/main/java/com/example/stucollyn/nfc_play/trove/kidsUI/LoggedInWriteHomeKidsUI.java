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
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.stucollyn.nfc_play.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

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
    File tag_directory;
    File cover_directory;
    File cloud_directory;
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
    LinearLayout camera_linear;
    Animation fadein, fadeout;


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
        camera_linear = (LinearLayout) findViewById(R.id.camera_linear);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        authenticated = (Boolean) getIntent().getExtras().get("Authenticated");
        mCamera = cameraRecorder.getCameraInstance();
        mPreview = new CameraPreview(getApplicationContext(), mCamera);
        preview.addView(mPreview);
        archiveStoryHandler = new Handler();
        nfcInteraction = new NFCInteraction(this, this, authenticated);
        commentaryInstruction = new CommentaryInstruction(this, this, false, authenticated);
        commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.recordstory1), false, LoggedInWriteHomeKidsUI.class, "LoggedInWriteHomeKidsUI");
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

//        commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.holdrecordbutton), true, LoggedInReadHomeKidsUI.class);

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
                        commentaryInstruction.stopPlaying();
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
                        commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.takeapicture), false, LoggedInWriteHomeKidsUI.class, "LoggedInWriteHomeKidsUI");
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
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            Log.i("New Story Ready", String.valueOf(newStoryReady));
            mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, "Object Found.", Toast.LENGTH_LONG).show();

            if (newStoryReady) {

                    newStoryReady = false;
                    boolean success = false;
                    success = nfcInteraction.doWrite(mytag, tag_data);

                Log.i("Success Value", String.valueOf(success));

                if(success) {

                    disableViewClickability();
                    commentaryInstruction.stopPlaying();
                    CancelStoryArchiveHandlerTimer();
                    ResetCamera();
                    ReleaseCamera();
                    nfcInteraction.Complete(success);
                }

                else {

                    newStoryReady = true;
                }

            }
        }
    }

    //Setup new storage folder
    private void SetupStoryLocation() {

        deleteDirectories();

        newStoryReady = false;
//        String LocalStoryFolder = ("/Stories");
//        String TagFolder = ("/Tag");
//        String CoverFolder = ("/Covers");
//        String timeStamp = new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH).format(new Date());
        String name = UUID.randomUUID().toString();
        tag_data = name;
        story_directory = new File (getFilesDir() + File.separator + "Stories" + File.separator + name);
        tag_directory = new File (getFilesDir() + File.separator + "Tag" + File.separator + name);
        cover_directory = new File (getFilesDir() + File.separator + "Covers" + File.separator + name);
        cloud_directory = new File (getFilesDir() + File.separator + "Cloud" + File.separator + name);
        story_directory.mkdir();
        tag_directory.mkdir();
        cover_directory.mkdir();
        cloud_directory.mkdir();

        Log.i("Testing Story Path", story_directory.toString());
        Log.i("Testing Story Abso", story_directory.getAbsolutePath());

//        String newDirectory = LocalStoryFolder + "/" + name;
//        String newDirectory2 = TagFolder + "/" + name;
//        String newDirectory3 = CoverFolder + "/" + name;
//        story_directory = getExternalFilesDir(newDirectory);
//        tag_directory = getExternalFilesDir(newDirectory2);
//        cover_directory = getExternalFilesDir(newDirectory3);
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
            audioRecorder = new AudioRecorderKidsUI(this, story_directory, tag_directory);
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
            } else {
                slideInViewAnimation(cameraButton);
                audioRecorder.stopRecording();
                animationHandler.removeCallbacks(RecordButtonRunnable);
                recordButton.setImageDrawable(recordButtonNonAnim);
            }

            recordingStatus = !recordingStatus;

        }

        catch (RuntimeException r) {

        }
    }


    //Archive Communication
    public void Archive(View view) {

        commentaryInstruction.stopPlaying();
        disableViewClickability();
        archive.setClickable(false);
        ReleaseCamera();
        Intent intent = new Intent(LoggedInWriteHomeKidsUI.this, ArchiveKidsUI.class);
        intent.putExtra("Authenticated", authenticated);
        LoggedInWriteHomeKidsUI.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    void NewStoryArchiveHandlerTimer() {

        archiveStoryHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

//                Log.i("Annoying Handler", "Ach");
//                commentaryInstruction.setInputHandler(archiveStoryHandler);
//                commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.recorddone1), true, ArchiveKidsUI.class, "LoggedInWriteHomeKidsUI");
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

    //Camera Management
    public void Camera(View view) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
            }

        try {
            cameraRecorder = new CameraRecorder(this, this, story_directory, tag_directory, cover_directory, mCamera, mPreview);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    camera_linear.startAnimation(fadein);
                    captureButton.startAnimation(fadein);
                    preview.startAnimation(fadein);
                    preview.setVisibility(View.VISIBLE);
                    captureButton.setVisibility(View.VISIBLE);
                    camera_linear.setVisibility(View.VISIBLE);
                    captureButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // get an image from the camera
                                    captureButton.setImageResource(R.drawable.kids_ui_record_anim_alt_mini);
                                    mCamera.takePicture(null, null, mPicture);
                                }
                            }
                    );

                }
            }, 500);
        }

        catch (NullPointerException e) {


                Log.i("Error", "Eh nah");
            }
    }


    Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            camera_linear.startAnimation(fadeout);
            captureButton.startAnimation(fadeout);
            //preview.startAnimation(fadeout);
            captureButton.setVisibility(View.GONE);
            preview.setVisibility(View.GONE);
            camera_linear.setVisibility(View.GONE);
            File pictureFile = cameraRecorder.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d("Tag", "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                cameraRecorder.copyFile(pictureFile, cameraRecorder.getTagFile());
                cameraRecorder.copyFile(pictureFile, cameraRecorder.getCoverFile());
            } catch (FileNotFoundException e) {
                Log.d("Tag", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("Tag", "Error accessing file: " + e.getMessage());
            }

            ResetCamera();
//            new LoggedInWriteHomeKidsUI.ProcessPicture().execute();
            newStoryReady = true;
            NewStoryArchiveHandlerTimer();
            slideOutViewAnimation(cameraButton);
            slideInViewAnimation(archive);
            commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.attachnfc), false, null, "LoggedInWriteHomeKidsUI");
            UUID objectUUID = UUID.randomUUID();

            if(authenticated) {
                SaveToCloud saveToCloud = new SaveToCloud(story_directory, objectUUID);
                saveToCloud.CloudSaveNewObject();
            }
        }
    };

    void ResetCamera() {

        if(mCamera!=null) {
            mCamera.stopPreview();
            captureButton.setImageResource(R.drawable.kids_ui_record_circle_mini);
            mCamera.startPreview();
        }
    }

    void ReleaseCamera() {

        if(mCamera!=null) {
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    void deleteDirectories() {

        if(story_directory!=null&&!newStoryReady) {
            deleteStoryDirectory();
        }

        if(tag_directory!=null&&!newStoryReady) {
            deleteTagDirectory();
        }

        if(cover_directory!=null&&!newStoryReady) {
            deleteCoverDirectory();
        }
    }

    void deleteStoryDirectory() {

        try {

            Log.i("Deleting StoryDirectory", story_directory.toString());
            FileUtils.deleteDirectory(story_directory);
        }

        catch (IOException e) {

        }

    }

    void deleteTagDirectory() {

        try {

            Log.i("Deleting Tag Directory", tag_directory.toString());
            FileUtils.deleteDirectory(tag_directory);
        }

        catch (IOException e) {

        }

    }

    void deleteCoverDirectory() {

        try {

            Log.i("Deleting Covr Directory", cover_directory.toString());
            FileUtils.deleteDirectory(cover_directory);
        }

        catch (IOException e) {

        }

    }


    class ProcessPicture extends AsyncTask<View, Void, Void> {

        Bitmap processedBitmap;

        @Override
        protected Void doInBackground(View... params) {

//            imageView = params[0];

            try {

//                cameraRecorder.PictureProcessing();
//                photoPath = cameraRecorder.getPhotoPath();
//                photoUri = cameraRecorder.getPhotoURI();
////                picture_story_fragment.setPictureBoxDimensions(pictureRecorder.getRotationInDegrees());
//                processedBitmap = cameraRecorder.getAdjustedBitmap();

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

    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();
    }

    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        authenticated = savedInstanceState.getBoolean("Authenticated");
    }

    void disableViewClickability() {

        back.setClickable(false);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.logged_in_write_home_layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setClickable(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putBoolean("Authenticated", authenticated);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void Drawer(View view){

        disableViewClickability();
        deleteDirectories();
        ResetCamera();
        CancelStoryArchiveHandlerTimer();
        animationBackHandler.removeCallbacksAndMessages(null);
        commentaryInstruction.stopPlaying();

        ReleaseCamera();
        Intent intent = new Intent(LoggedInWriteHomeKidsUI.this, HamburgerKidsUI.class);
        intent.putExtra("PreviousActivity", "LoggedInWriteHomeKidsUI");
        intent.putExtra("Authenticated", authenticated);
        LoggedInWriteHomeKidsUI.this.startActivity(intent);
        overridePendingTransition(R.anim.left_to_right_slide_in_activity, R.anim.left_to_right_slide_out_activity);
    }


    public void Back(View view) {

        commentaryInstruction.stopPlaying();
        disableViewClickability();
        deleteDirectories();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {

        ResetCamera();
        CancelStoryArchiveHandlerTimer();
        animationBackHandler.removeCallbacksAndMessages(null);
        back.setImageDrawable(backRetrace);
        backRetrace.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ReleaseCamera();
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
                    saveToCloud.CloudSaveNewObject();
                }
            }
        }

        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {

//                new LoggedInWriteHomeKidsUI.ProcessVideo().execute();

            }
        }
    }


   /*
            new Thread(new Runnable() {
                public void run() {
                    while (!cameraRecorder.getPictureSave()) {
                        try {
                            Thread.sleep(200);
                            Log.i("Woo", "Checking");
                        } catch (InterruptedException ignored) {
                        }
                    }

                    Log.i("Woo", "Let's Party");
                    Thread.interrupted();
                    new LoggedInWriteHomeKidsUI.ProcessPicture().execute();
                    slideOutViewAnimation(cameraButton);
                    slideInViewAnimation(archive);
                    commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.attachtag_app), false, null);
                    UUID objectUUID = UUID.randomUUID();

                    if(authenticated) {
                        SaveToCloud saveToCloud = new SaveToCloud(story_directory, objectUUID);
                        saveToCloud.CloudSaveNewObject();
                    }
                    //do something
                }
            }).start();

            */
