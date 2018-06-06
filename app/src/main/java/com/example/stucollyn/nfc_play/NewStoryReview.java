package com.example.stucollyn.nfc_play;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

//NewStoryReview Activity lets the user review the contents of a newly created story
public class NewStoryReview extends AppCompatActivity implements Serializable {

    HashMap<String,String> selectedMedia;
    AudioStoryFragment audioStoryFragment;
    PictureStoryFragment pictureStoryFragment;
    VideoStoryFragment videoStoryFragment;
    WrittenStoryFragment writtenStoryFragment;
    ImageButton recorded_audio_cover, recorded_picture_cover, recorded_video_cover,
            recorded_writing_cover, confirmation_button, discard_button;
    ImageView recorded_audio, recorded_picture, recorded_video_background;
    VideoView recorded_video;
    TextView recorded_writing;
    boolean audioPlaying = false, picturePlaying = false, videoPlaying = false,
            writtenPlaying = false, playbackStatus = false, mPlayerSetup = false;
    private MediaPlayer mPlayer = null;
    MediaController mediaController;
    int rotationInDegrees;
    Bitmap adjustedBitmap;
    private StringBuilder text = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Create New Story");

        setContentView(R.layout.activity_new_story_review);
        selectedMedia = new HashMap<String, String>();
        audioStoryFragment = new AudioStoryFragment();
        pictureStoryFragment = new PictureStoryFragment();
        videoStoryFragment = new VideoStoryFragment();
        writtenStoryFragment = new WrittenStoryFragment();

        recorded_audio_cover = findViewById(R.id.audio_media_review_cover);
        recorded_picture_cover = findViewById(R.id.picture_media_review_cover);
        recorded_video_cover = findViewById(R.id.video_media_review_cover);
        recorded_writing_cover = findViewById(R.id.written_media_review_cover);
        confirmation_button = findViewById(R.id.confirm_media);
        discard_button = findViewById(R.id.discard_review);

        recorded_audio = findViewById(R.id.audio_media_review);
        recorded_picture = findViewById(R.id.picture_media_review);
        recorded_video = findViewById(R.id.video_media_review);
        recorded_writing = findViewById(R.id.written_media_review);

        recorded_video_background = findViewById(R.id.video_media_review_background);

        mediaController = new MediaController(this);

        selectedMedia = new HashMap<String,String>();
        selectedMedia = (HashMap<String,String>)getIntent().getSerializableExtra("RecordedMedia");

        Log.i("HashMap", selectedMedia.toString());

        if(selectedMedia.containsKey("Audio")) {

            recorded_audio_cover.setVisibility(View.VISIBLE);
        }

        if(selectedMedia.containsKey("Picture")) {

            recorded_picture_cover.setVisibility(View.VISIBLE);
        }

        if(selectedMedia.containsKey("Video")) {

            recorded_video_cover.setVisibility(View.VISIBLE);
        }

        if(selectedMedia.containsKey("Written")) {

            recorded_writing_cover.setVisibility(View.VISIBLE);
        }

    }




    public void AudioReview(View view) {

        if (!mPlayerSetup) {
            setupAudioMediaPlayer();
        }

        if(!audioPlaying) {

            recorded_audio_cover.setVisibility(View.INVISIBLE);
            recorded_audio.setVisibility(View.VISIBLE);
            recorded_audio.setImageResource(R.drawable.stop_black);
            startPlaying();
            audioPlaying = true;
        }

        else {

            recorded_audio.setImageResource(R.drawable.play_arrow_black);
            stopPlaying();
            audioPlaying = false;
        }

    }

    public void setupAudioMediaPlayer() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(selectedMedia.get("Audio"));
            mPlayer.prepare();
            mPlayerSetup = true;
        } catch (IOException e) {
            Log.e("Fail", "prepare() failed");
        }
    }

    public void startPlaying() {
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                stopPlaying();
            }

        });
    }

    public void pausePlaying() {

        mPlayer.pause();
    }

    public void stopPlaying() {

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
            mPlayerSetup = false;
            playbackStatus = false;
        }
    }









    public void PictureReview(View view) {

        if(!picturePlaying) {

            recorded_picture_cover.setVisibility(View.INVISIBLE);
            recorded_picture.setVisibility(View.VISIBLE);
            ShowPicture();
        }

        else {

            //recorded_picture_cover.setVisibility(View.VISIBLE);
        }

        picturePlaying = !picturePlaying;

    }


    public void ShowPicture() {

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(selectedMedia.get("Picture"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        rotationInDegrees = exifToDegrees(rotation);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedMedia.get("Picture"), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int smallSizeScaleFactor = Math.min(photoW / 200, photoH / 200);


        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = smallSizeScaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(selectedMedia.get("Picture"), bmOptions);
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

        recorded_picture.setImageBitmap(adjustedBitmap);

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








    public void VideoReview(View view) {

        if(!videoPlaying) {

            recorded_video_cover.setVisibility(View.INVISIBLE);
            recorded_video.setVisibility(View.VISIBLE);
            recorded_video_background.setVisibility(View.VISIBLE);
            VideoProcessing();
        }

        else {

            //recorded_video_cover.setVisibility(View.VISIBLE);
            recorded_video.pause();
        }

        videoPlaying = !videoPlaying;

    }

    public void VideoProcessing() {

        MediaMetadataRetriever m = new MediaMetadataRetriever();

        m.setDataSource(selectedMedia.get("Video"));
        Bitmap thumbnail = m.getFrameAtTime();
//
        if (Build.VERSION.SDK_INT >= 17) {
            String s = m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);

        }

        File file = new File(selectedMedia.get("Video"));
        Intent openFullSize = new Intent(Intent.ACTION_VIEW);
        openFullSize.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri videoURI = FileProvider.getUriForFile(this,
                "com.example.android.fileprovider",
                file);

        recorded_video.setVideoURI(videoURI);
        mediaController.setAnchorView(recorded_video);
        recorded_video.setMediaController(mediaController);
        recorded_video.start();

    }





    public void WrittenReview(View view) {

        if(!writtenPlaying) {

            recorded_writing_cover.setVisibility(View.INVISIBLE);
            recorded_writing.setVisibility(View.VISIBLE);
            ShowWriting();
            writtenPlaying = true;
        }

        else {

            //recorded_writing_cover.setVisibility(View.VISIBLE);
        }

        //writtenPlaying = !writtenPlaying;

    }





    public void ShowWriting() {

        BufferedReader reader = null;
        File fileWritten = new File(selectedMedia.get("Written"));

        try {
            FileReader in = new FileReader(fileWritten);
            reader = new BufferedReader(in);

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Error reading file!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }

            recorded_writing.setText((CharSequence) text);

        }

    }



    public void Confirm (View view) {

        Intent intent = new Intent(NewStoryReview.this, MainMenu.class);
        NewStoryReview.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    public void Discard (View view) {

        Intent intent = new Intent(NewStoryReview.this, MainMenu.class);
        NewStoryReview.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(NewStoryReview.this, StoryMediaChooser.class);
        NewStoryReview.this.startActivity(intent);
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
