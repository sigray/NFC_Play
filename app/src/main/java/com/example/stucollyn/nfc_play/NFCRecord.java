package com.example.stucollyn.nfc_play;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NFCRecord extends AppCompatActivity {

    boolean record_button_on;
    boolean video_record_button_on;
    MediaRecorder recordStory;
    Layout pictureLayout;
    String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    AudioStoryFragment audio_story_fragment;
    PictureStoryFragment picture_story_fragment;
    FragmentTransaction ft;

    private static final int CAMERA_REQUEST = 1888;
    private static final String LOG_TAG = "AudioRecordTest";
    private static String audioFileName = null, pictureFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    boolean recordingStatus = false;
    boolean playbackStatus = false;
    boolean mPlayerSetup = false;
    boolean fullSizedPicture = false;
    MediaPlayer.OnCompletionListener audio_stop_listener;
    ArrayList<Fragment> fragmentNameArray;
    int fragmentArrayPosition = 0;
    File image;
    Bitmap adjustedFullSizedBitmap;
    Bitmap adjustedBitmap;
    int rotationInDegrees;


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
        audio_story_fragment = new AudioStoryFragment();
        picture_story_fragment = new PictureStoryFragment();
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, audio_story_fragment);
        ft.commit();

        fragmentNameArray = new ArrayList<Fragment>();
        fragmentNameArray.add(audio_story_fragment);
        fragmentNameArray.add(picture_story_fragment);
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

        if (fragmentArrayPosition < fragmentNameArray.size() - 1) {
            fragmentArrayPosition++;
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_frame, fragmentNameArray.get(fragmentArrayPosition));
            ft.commit();
        }

    }

    public void CompleteAudioRecording(View view) {

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, picture_story_fragment);
        ft.commit();
    }


    //Picture Record

    public void PictureRecordButton(View view) {

        picture_story_fragment.TakePicture(view);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                dispatchTakePictureIntent();
            }
        }, 1000);

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
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i("File Name", "1" + mCurrentPhotoPath);
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


        if(requestCode==100) {
           if (resultCode == RESULT_OK) {


                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(mCurrentPhotoPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                rotationInDegrees = exifToDegrees(rotation);

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;
                int smallSizeScaleFactor = Math.min(photoW / 200, photoH / 200);


                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = smallSizeScaleFactor;
                bmOptions.inPurgeable = true;
                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
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
        }
    }

    public void FullSizedPicture(View view) {

       /* if(!fullSizedPicture) {
            Bitmap fullSizedBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            adjustedFullSizedBitmap = Bitmap.createBitmap(fullSizedBitmap, 0, 0, fullSizedBitmap.getWidth(), fullSizedBitmap.getHeight(), matrix, true);
        }

        fullSizedPicture = !fullSizedPicture;
        picture_story_fragment.ShowFullSizedPicture(fullSizedPicture, adjustedFullSizedBitmap);
        */

        File file = new File(mCurrentPhotoPath);
        Intent openFullSize = new Intent(Intent.ACTION_VIEW);
        openFullSize.setDataAndType(Uri.fromFile(file), "image/");
        openFullSize.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        //Intent openFullSize = new Intent(Intent.ACTION_VIEW, Uri.parse(mCurrentPhotoPath));
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

        pictureFileName = mCurrentPhotoPath;
    }




/*

    public void VideoSetup() {

        video_button.setVisibility(View.VISIBLE);
        audio_button.setVisibility(View.INVISIBLE);
        camera_start_button.setVisibility(View.INVISIBLE);
    }

    public void VideoStartButton(View view) {

        if(!video_record_button_on) {

            video_record_button_on = true;
            video_button.setImageResource(R.drawable.video_on_min);
            record_instruction.setText("Video recording unavailable right now.");
        }

        else {
            video_record_button_on = false;
            video_button.setImageResource(R.drawable.video_off_min);
            record_instruction.setText("Touch the yellow button to start video camera.");
        }

    }



    */

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(NFCRecord.this, MainMenu.class);
        NFCRecord.this.startActivity(intent);
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
