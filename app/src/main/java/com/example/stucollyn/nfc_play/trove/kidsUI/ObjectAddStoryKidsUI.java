package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * Created by StuCollyn on 28/09/2018.
 */

public class ObjectAddStoryKidsUI extends AppCompatActivity {

    ImageView recordButton, cameraButton, back;
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
    File tag_directory = null;
    File cover_directory = null;
    String story_directory_path;
    Uri story_directory_uri;
    String tag_data = null;
//    NFCInteraction nfcInteraction;
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
    HashMap<String, ArrayList<ObjectStoryRecordKidsUI>> objectRecordMap;
    String objectName;

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
        setContentView(R.layout.activity_add_story_to_object_kids_ui);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        recordButton = (ImageView) findViewById(R.id.record);
        cameraButton = (ImageView) findViewById(R.id.camera);
        back = (ImageView) findViewById(R.id.back);
        captureButton = (ImageButton) findViewById(R.id.button_capture);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        camera_linear = (LinearLayout) findViewById(R.id.camera_linear);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        authenticated = (Boolean) getIntent().getExtras().get("Authenticated");
        objectRecordMap = (HashMap<String, ArrayList<ObjectStoryRecordKidsUI>>) getIntent().getExtras().get("ObjectStoryRecord");
        objectName = (String) getIntent().getExtras().get("ObjectName");
        mCamera = cameraRecorder.getCameraInstance();
        mPreview = new CameraPreview(getApplicationContext(), mCamera);
        preview.addView(mPreview);
        archiveStoryHandler = new Handler();
        commentaryInstruction = new CommentaryInstruction(this, this, false, authenticated);
        commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.holdrecordbutton), false, LoggedInWriteHomeKidsUI.class, "ObjectAddStoryKidsUI");
        SetupStoryLocation();
        AnimationSetup();
        recordButtonController();
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
                        commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.recorddone1), false, LoggedInReadHomeKidsUI.class, "ObjectAddStoryKidsUI");
                        onBackPressed();
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

    //Setup new storage folder
    private void SetupStoryLocation() {

        String LocalStoryFolder = ("/Stories/");
        String newDirectory = LocalStoryFolder + "/" + objectName;
        story_directory = getExternalFilesDir(newDirectory);
    }

    //Recording Audio Management
    void recordAudio(View view) {

        //Request permission to record audio (required for some newer Android devices)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        try {
            audioRecorder = new AudioRecorderKidsUI(this, story_directory, null);
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

                recordButton.setImageDrawable(recordButtonAnim);
                recordAudio(view);
                Log.i("Tag", "Starting Recording");
            } else {
                Log.i("Tag", "Stopping Recording");
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
            Log.i("Tag", "This far A");

            cameraRecorder = new CameraRecorder(this, this, story_directory, tag_directory, cover_directory, mCamera, mPreview);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    Log.i("Tag", "This far C");

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
            } catch (FileNotFoundException e) {
                Log.d("Tag", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("Tag", "Error accessing file: " + e.getMessage());
            }

            ResetCamera();
//            new LoggedInWriteHomeKidsUI.ProcessPicture().execute();
            UUID objectUUID = UUID.randomUUID();

            if(authenticated) {
                SaveToCloud saveToCloud = new SaveToCloud(story_directory, objectUUID);
                saveToCloud.CloudSaveNewStory();
            }

            ReleaseCamera();
            commentaryInstruction.setTagData(tag_data);
            commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.recorddone1), false, LoggedInReadHomeKidsUI.class, "ObjectAddStoryKidsUI");
            onBackPressed();
        }
    };

    void ResetCamera() {

        mCamera.stopPreview();
        captureButton.setImageResource(R.drawable.kids_ui_record_circle_mini);
        mCamera.startPreview();
    }

    void ReleaseCamera() {

        mCamera.stopPreview();
        mCamera.release();
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

        }
    }



    //Activity Governance

    @Override
    public void onPause(){
        super.onPause();
//        nfcInteraction.WriteModeOff(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
//        nfcInteraction.WriteModeOn(adapter, pendingIntent, writeTagFilters);
    }

    public void Back(View view) {

        onBackPressed();
    }

    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public void onBackPressed() {

        ResetCamera();
        animationBackHandler.removeCallbacksAndMessages(null);
        back.setImageDrawable(backRetrace);
        backRetrace.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ReleaseCamera();
                Intent intent = new Intent(ObjectAddStoryKidsUI.this, ExploreArchiveItem.class);
                intent.putExtra("ObjectName", objectName);
                intent.putExtra("ObjectStoryRecord", objectRecordMap);
                intent.putExtra("Authenticated", authenticated);
                ObjectAddStoryKidsUI.this.startActivity(intent);
                ObjectAddStoryKidsUI.this.overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
//                overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
            }
        }, 1000);

    }
}
