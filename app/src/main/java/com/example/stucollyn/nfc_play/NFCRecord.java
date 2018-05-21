package com.example.stucollyn.nfc_play;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class NFCRecord extends AppCompatActivity implements Serializable {

    boolean record_button_on;
    boolean video_record_button_on;
    MediaRecorder recordStory;
    Layout pictureLayout;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    AudioStoryFragment audio_story_fragment;
    PictureStoryFragment picture_story_fragment;
    VideoStoryFragment video_story_fragment;
    WrittenStoryFragment written_story_fragment;
    FragmentTransaction ft;
    ArrayList<String> selectedMedia;

    private static final int CAMERA_REQUEST = 1888;
    private static final String LOG_TAG = "AudioRecordTest";
    private static String audioFileName = null, pictureFileName = null, videoFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    boolean recordingStatus = false;
    boolean playbackStatus = false;
    boolean mPlayerSetup = false;
    boolean fullSizedPicture = false;
    MediaPlayer.OnCompletionListener audio_stop_listener;
    ArrayList<Fragment> fragmentNameArray;
    HashMap<String,String> recordedMediaHashMap = new HashMap<String,String>();
    int fragmentArrayPosition = 0;
    File image, video;
    Bitmap adjustedFullSizedBitmap;
    Bitmap adjustedBitmap;
    int rotationInDegrees;
    Uri videoURI;
    String testData="Lies";
    boolean isFullSizedVideo = false;

    String audioPath, photoPath, videoPath, writtenPath;

/*
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_audio:
                    AudioSetup();
                    return true;
                case R.id.navigation_picture:
                    PictureSetup();
                    return true;
                case R.id.navigation_video:
                    VideoSetup();
                    return true;
            }
            return false;
        }
    };
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Create New Story");
        audio_story_fragment = new AudioStoryFragment();
        picture_story_fragment = new PictureStoryFragment();
        video_story_fragment = new VideoStoryFragment();
        written_story_fragment = new WrittenStoryFragment();

        fragmentNameArray = new ArrayList<Fragment>();
//        fragmentNameArray = (ArrayList<Fragment>)getIntent().getSerializableExtra("Fragments");

        selectedMedia = (ArrayList<String>)getIntent().getSerializableExtra("Fragments");

        HashMap<String,Fragment> mediaFragmentLookup = new HashMap<String,Fragment>();
        mediaFragmentLookup.put("Audio", audio_story_fragment);
        mediaFragmentLookup.put("Picture", picture_story_fragment);
        mediaFragmentLookup.put("Video", video_story_fragment);
        mediaFragmentLookup.put("Written", written_story_fragment);

        for(int i=0; i<selectedMedia.size(); i++) {

            fragmentNameArray.add(mediaFragmentLookup.get(selectedMedia.get(i)));
        }


        Log.i("fragfull:", fragmentNameArray.toString());

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, fragmentNameArray.get(fragmentArrayPosition));
        ft.commit();
    }


    //Audio Recording

    public void AudioRecordButton(View view) {

        // ((TextView) audio_story_fragment.getView().findViewById(R.id.record_instruction)).setText("Start speaking. Press the button again to finish.");
        // audio_story_fragment = (AudioStoryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
        recordingStatus = !recordingStatus;
        audio_story_fragment.AudioRecordButtonSwitch(recordingStatus, view);
        onRecord(recordingStatus, view);
    }

    public void onRecord(boolean start, View view) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
            audio_story_fragment.PlayBackAndSaveSetup(view);
        }
    }

    public void onPlay(View view) {
        if (!mPlayerSetup) {
            setupAudioMediaPlayer();
        }

        if (!playbackStatus) {
            startPlaying(view);
            playbackStatus = true;
        } else {
            pausePlaying(view);
            playbackStatus = false;
        }

        audio_story_fragment.PlaybackButtonSwitch(playbackStatus, view);
    }

    public void setupAudioMediaPlayer() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(audioFileName);
            mPlayer.prepare();
            mPlayerSetup = true;
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void startPlaying(View view) {
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying(findViewById(R.id.stop_button));
            }

        });
    }

    public void pausePlaying(View view) {
        mPlayer.pause();
    }

    public void stopPlaying(View view) {

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
            mPlayerSetup = false;
            playbackStatus = false;
            audio_story_fragment.PlaybackButtonSwitch(playbackStatus, view);
        }
    }

    public void startRecording() {

        /*String m_path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath();
        mFileName = "Recording";*/

        audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        audioFileName += "/audiorecordtest.mp4";

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(audioFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void DiscardAudio(View view) {

        File file = new File(audioFileName);
        file.delete();
        audio_story_fragment.ResetView(view);
    }

    public void Skip(View view) {

        UpdateFragment();

    }

    public void CompleteAudioRecording(View view) {

        audioPath = audioFileName;
        recordedMediaHashMap.put("Audio", audioPath);
        UpdateFragment();
    }



    //Picture Recording

    public void PictureRecordButton(View view) {

        picture_story_fragment.TakePicture(view);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                dispatchTakePictureIntent();
            }
        }, 500);

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    //new ProcessPicture().execute();
                    startActivityForResult(takePictureIntent, 100);
                }
            }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
        Log.i("File Name", "1" + photoPath);
        return image;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (
                exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.i("Request Code:", String.valueOf(requestCode));
        Log.i("Result Code:", String.valueOf(resultCode));
        Log.i("Data:", String.valueOf(data));

        //Picture Processing
        if(requestCode==100) {
           if (resultCode == RESULT_OK) {

                PictureProcessing();
            }
        }

        if(requestCode==200) {
            if (resultCode == RESULT_OK) {

                VideoProcessing();
            }
        }


    }

    public void PictureProcessing() {

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        rotationInDegrees = exifToDegrees(rotation);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int smallSizeScaleFactor = Math.min(photoW / 200, photoH / 200);


        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = smallSizeScaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        Matrix matrix = new Matrix();
        if (rotation != 0f) {
            matrix.preRotate(rotationInDegrees);
        }

        if (rotationInDegrees == 90 || rotationInDegrees == 270) {
            adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else if (rotationInDegrees == 180) {
            adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            // adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            adjustedBitmap = bitmap;
        }

        picture_story_fragment.setPictureBoxDimensions(rotationInDegrees);
        picture_story_fragment.ShowPicture(adjustedBitmap);

    }

    public void FullSizedPicture(View view) {

       /* if(!fullSizedPicture) {
            Bitmap fullSizedBitmap = BitmapFactory.decodeFile(photoPath);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            adjustedFullSizedBitmap = Bitmap.createBitmap(fullSizedBitmap, 0, 0, fullSizedBitmap.getWidth(), fullSizedBitmap.getHeight(), matrix, true);
        }

        fullSizedPicture = !fullSizedPicture;
        picture_story_fragment.ShowFullSizedPicture(fullSizedPicture, adjustedFullSizedBitmap);
        */

        File file = new File(photoPath);
        Intent openFullSize = new Intent(Intent.ACTION_VIEW);
        openFullSize.setDataAndType(Uri.fromFile(file), "image/");
        openFullSize.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        //Intent openFullSize = new Intent(Intent.ACTION_VIEW, Uri.parse(photoPath));
        try {
            startActivity(openFullSize);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(NFCRecord.this,
                    "Error showing image", Toast.LENGTH_LONG).show();
        }
    }

    public void DiscardPicture(View view) {

        picture_story_fragment.DiscardPicture();
    }

    public void CompletePictureRecording(View view) {

        pictureFileName = photoPath;
        recordedMediaHashMap.put("Picture", pictureFileName);
        UpdateFragment();
    }




    //Video Recording

    public void VideoRecordButton(View view) {

        video_story_fragment.TakeVideo(view);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                dispatchTakeVideoIntent();
            }
        }, 1000);

    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File videoFile = null;
            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (videoFile != null) {
                videoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        videoFile);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                startActivityForResult(takeVideoIntent, 200);
            }
        }
    }

    private File createVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "MPEG4_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        video = File.createTempFile(videoFileName, ".mp4", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        videoPath = video.getAbsolutePath();
        return video;
    }

    public void VideoProcessing() {

        MediaMetadataRetriever m = new MediaMetadataRetriever();

        m.setDataSource(videoPath);
        Bitmap thumbnail = m.getFrameAtTime();
//
        if (Build.VERSION.SDK_INT >= 17) {
            String s = m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);

            Log.e("Rotation", s);
        }

        video_story_fragment.ShowVideo(videoURI);
    }

    public void FullSizedVideo(View view) {

        video_story_fragment.ShowFullSizedVideo(isFullSizedVideo, videoURI);
        isFullSizedVideo = !isFullSizedVideo;
    }

    public void DiscardVideo(View view) {

        video_story_fragment.DiscardVideo();
    }

    public void CompleteVideoRecording(View view) {

        videoFileName = videoPath;
        recordedMediaHashMap.put("Video", videoFileName);
        UpdateFragment();
    }


    //Written
    //Video Recording

    public void WriteStory(View view) {

        written_story_fragment.StartWritingNotification(view);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                written_story_fragment.StartWriting();
            }
        }, 500);

    }

    public void DiscardWrittenStory(View view) {

        written_story_fragment.DiscardWriting();
    }

    public void CompleteWrittenStory(View view) {

        /*
        Intent intent = new Intent(NFCRecord.this, MainMenu.class);
        NFCRecord.this.startActivity(intent);
        */

        recordedMediaHashMap.put("Written", writtenPath);
        UpdateFragment();
    }

    private File createWrittenFile() throws IOException {

//        audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        audioFileName += "/audiorecordtest.mp4";

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(audioFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String textFileName = "TEXT_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File text = File.createTempFile(textFileName, ".txt", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        writtenPath = text.getAbsolutePath();
        return text;
    }

    public void UpdateFragment() {

        if (fragmentArrayPosition < fragmentNameArray.size() - 1) {
            fragmentArrayPosition++;
            Log.i("New Frag", fragmentNameArray.get(fragmentArrayPosition).toString());
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_frame, fragmentNameArray.get(fragmentArrayPosition));
            ft.commit();
        }

        else {
            /*
            Intent intent = new Intent(NFCRecord.this, MainMenu.class);
            NFCRecord.this.startActivity(intent);
            overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
            */

            Log.i("HashMap in NFCRecord", recordedMediaHashMap.toString());
            Intent intent = new Intent(NFCRecord.this, NewStoryReview.class);
            intent.putExtra("RecordedMedia", recordedMediaHashMap);
            NFCRecord.this.startActivity(intent);
            overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(NFCRecord.this, StoryMediaChooser.class);
        NFCRecord.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }
}

/*
    public class ProcessPicture extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {



            try {

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

            catch (NullPointerException e) {

            }

            catch (IllegalArgumentException e) {

            }

            return null;

        }

        protected void onPostExecute(Void result) {

        }
    }
}
*/

// catch (NullPointerException e) {
//
//         Log.i("Exception", "NullPointerException");
//         Bitmap fullSizedBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.baloon1);
//         Matrix matrix = new Matrix();
//         matrix.postRotate(90);
//         adjustedBitmap = Bitmap.createBitmap(fullSizedBitmap, 0, 0, fullSizedBitmap.getWidth(), fullSizedBitmap.getHeight(), matrix, true);
//         }
//
//         catch (IllegalArgumentException e) {
//
//         Log.i("Exception", "IllegalArgumentException");
//         Bitmap fullSizedBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.baloon1);
//         Matrix matrix = new Matrix();
//         matrix.postRotate(90);
//         adjustedBitmap = Bitmap.createBitmap(fullSizedBitmap, 0, 0, fullSizedBitmap.getWidth(), fullSizedBitmap.getHeight(), matrix, true);
//         }
