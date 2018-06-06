package com.example.stucollyn.nfc_play;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/* The NewStoryRecord Activity is the main class for dealing with recording of different media.
* It takes in the types of media defined in StoryMediaChooser and lets the user record stories
* using the types selected. The Activity launches different fragments to deal with the different
* media, and communicates events with the fragments which they then display to the user.*/
public class NFCRecord extends AppCompatActivity implements Serializable {

    //General Variables
    boolean record_button_on, video_record_button_on, recordingStatus = false,
            playbackStatus = false, mPlayerSetup = false, fullSizedPicture = false,
            permissionToRecordAccepted = false, isFullSizedVideo = false;
    int fragmentArrayPosition = 0, rotationInDegrees;
    String testData="Lies", audioPath, photoPath, videoPath, writtenPath;
    HashMap<String,String> recordedMediaHashMap = new HashMap<String,String>();
    Bitmap adjustedFullSizedBitmap, adjustedBitmap;


    //Media Recorder Variables
    MediaRecorder recordStory;
    MediaPlayer.OnCompletionListener audio_stop_listener;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    //Fragment Variables
    ArrayList<Fragment> fragmentNameArray;
    Layout pictureLayout;
    AudioStoryFragment audio_story_fragment;
    PictureStoryFragment picture_story_fragment;
    VideoStoryFragment video_story_fragment;
    WrittenStoryFragment written_story_fragment;
    FragmentTransaction ft;
    ArrayList<String> selectedMedia;

    //Request Code Variables
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    //File Save Variables
    private static String audioFileName = null, pictureFileName = null, videoFileName = null;
    File image, video;
    Uri videoURI;

    //Grant permission to record audio (required for some newer Android devices)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    //onCreate called when Activity begins
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ActionBarSetup();

        //Request permission to record audio (required for some newer Android devices)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        /*Receive array list of selected media types from StoryMediaChooser Activity and copy this
        list to a selectedMedia array list in this Activity*/
        selectedMedia = (ArrayList<String>)getIntent().getSerializableExtra("Fragments");
        InitFragments();
        }

    //Setup action bar
    private void ActionBarSetup() {

        //Display both title and image, and a back button in action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set and show trove logo in action bar
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //Set page title shown in action bar
        getSupportActionBar().setTitle("Home");
    }

    //Initialize fragments for use
    private void InitFragments() {

        //Initialize new fragment instances
        audio_story_fragment = new AudioStoryFragment();
        picture_story_fragment = new PictureStoryFragment();
        video_story_fragment = new VideoStoryFragment();
        written_story_fragment = new WrittenStoryFragment();
        fragmentNameArray = new ArrayList<Fragment>();

        /*Create and populate hash map, linking strings of possible media types with a corresponding
        fragment*/
        HashMap<String,Fragment> mediaFragmentLookup = new HashMap<String,Fragment>();
        mediaFragmentLookup.put("Audio", audio_story_fragment);
        mediaFragmentLookup.put("Picture", picture_story_fragment);
        mediaFragmentLookup.put("Video", video_story_fragment);
        mediaFragmentLookup.put("Written", written_story_fragment);

        /*Iterate through the array list of the user's selected media (e.g. String [Audio, Picture,
        Video, Written]). For every selected media entry, look up the corresponding fragment in the
        hash map. Add this fragment to an array list of fragments to be used in this current story.*/
        for(int i=0; i<selectedMedia.size(); i++) {

            fragmentNameArray.add(mediaFragmentLookup.get(selectedMedia.get(i)));
        }

        //Open first fragment in the fragment array list
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, fragmentNameArray.get(fragmentArrayPosition));
        ft.commit();
    }

    //When red audio record button is pressed, activate audio recording sequence
    public void AudioRecordButton(View view) {

        // ((TextView) audio_story_fragment.getView().findViewById(R.id.record_instruction)).setText("Start speaking. Press the button again to finish.");
        // audio_story_fragment = (AudioStoryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
        recordingStatus = !recordingStatus;
        audio_story_fragment.AudioRecordButtonSwitch(recordingStatus, view);
        onRecord(recordingStatus, view);
    }

    /*When audio recording is started, try to startRecording(). When audio recording is stopped,
    stopRecording()*/
    public void onRecord(boolean start, View view) {
        if (start) {

            try {
                startRecording();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

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
            Log.e("Error", "prepare() failed");
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

    public void startRecording() throws IOException {

        /*String m_path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath();
        mFileName = "Recording";*/

      //  audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
      //  audioFileName += "/audiorecordtest.mp4";

        // Check for permissions
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

// If we don't have permissions, ask user for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "AudioMPEG4_" + timeStamp + "_";
        File storageDir;

        if (Build.VERSION.SDK_INT >= 19) {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        }else{
            storageDir = getExternalFilesDir(Environment.getExternalStorageDirectory() + "/Documents");
        }

        File audioFile = File.createTempFile(imageFileName, ".mp4", storageDir);
        audioFileName = audioFile.getAbsolutePath();

        // Save a file: path for use with ACTION_VIEW intents
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(audioFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("Error", "prepare() failed");
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
        File storageDir;

        if (Build.VERSION.SDK_INT >= 19) {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else{
            storageDir = getExternalFilesDir(Environment.getExternalStorageDirectory() + "/Documents");
        }

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
//        Intent openFullSize = new Intent(Intent.ACTION_VIEW);
//        openFullSize.setDataAndType(Uri.fromFile(file), "image/");
//        openFullSize.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent openFullSize = new Intent(Intent.ACTION_VIEW);
        openFullSize.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
       // Uri apkURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        Uri photoURI = FileProvider.getUriForFile(this,
                "com.example.android.fileprovider",
                file);
        openFullSize.setDataAndType(photoURI, "image/");
        openFullSize.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // End New Approach
        this.startActivity(openFullSize);

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
        File storageDir;

        File currentDir = null;
        if (Build.VERSION.SDK_INT >= 19) {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else{
            storageDir = getExternalFilesDir(Environment.getExternalStorageDirectory() + "/Documents");
        }

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

        try {
            createWrittenFile();
        }

        catch (IOException e) {

        }

        recordedMediaHashMap.put("Written", writtenPath);
        UpdateFragment();
    }

    private File createWrittenFile() throws IOException {

//        audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        audioFileName += "/audiorecordtest.mp4";

//        mRecorder = new MediaRecorder();
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mRecorder.setOutputFile(audioFileName);
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String textFileName = "TEXT_" + timeStamp + "_";
        File storageDir;

        if (Build.VERSION.SDK_INT >= 19) {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        }else{
            storageDir = getExternalFilesDir(Environment.getExternalStorageDirectory() + "/Documents");
        }

        File textFile = File.createTempFile(textFileName, ".txt", storageDir);

        //if file doesnt exists, then create it
     if(!textFile.exists()){
            textFile.createNewFile();
            Log.i("creatingFile", textFile.getName());
     }

    try {

//        Log.i("textFileName", textFile.getName());
//        FileWriter fileWritter = new FileWriter(textFile.getName(),true);
//        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
//        bufferWritter.write(written_story_fragment.getTextContent());
//        bufferWritter.close();

        FileOutputStream fOut = new FileOutputStream(textFile.getAbsoluteFile(),true);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

        //Write the header : in your case it is : Student class Marsk (only one time)
        myOutWriter.write(written_story_fragment.getTextContent());
        myOutWriter.flush();


    }

    catch (IOException e) {

        e.printStackTrace();

        }

        // Save a file: path for use with ACTION_VIEW intents
        writtenPath = textFile.getAbsolutePath();
        Log.i("text location", writtenPath);

        return textFile;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
