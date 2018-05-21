package com.example.stucollyn.nfc_play;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class NewStoryReview extends AppCompatActivity implements Serializable {

    HashMap<String,String> selectedMedia;
    AudioStoryFragment audioStoryFragment;
    PictureStoryFragment pictureStoryFragment;
    VideoStoryFragment videoStoryFragment;
    WrittenStoryFragment writtenStoryFragment;
    ImageButton recorded_audio_cover, recorded_picture_cover, recorded_video_cover, recorded_writing_cover;
    ImageButton confirmation_button, discard_button;
    ImageButton recorded_audio;
    ImageView recorded_picture;
    VideoView recorded_video;
    TextView recorded_writing;

    boolean audioPlaying = false;
    boolean picturePlaying = false;
    boolean videoPlaying = false;
    boolean writtenPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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

        selectedMedia = new HashMap<String,String>();
        selectedMedia = (HashMap<String,String>)getIntent().getSerializableExtra("RecordedMedia");

        Log.i("HashMap", selectedMedia.toString());

        if(selectedMedia.containsKey("Audio")) {

            recorded_audio_cover.setVisibility(View.VISIBLE);
            recorded_audio.setVisibility(View.VISIBLE);
        }

        if(selectedMedia.containsKey("Picture")) {

            recorded_picture_cover.setVisibility(View.VISIBLE);
            recorded_picture.setVisibility(View.VISIBLE);
        }

        if(selectedMedia.containsKey("Video")) {

            recorded_video_cover.setVisibility(View.VISIBLE);
            recorded_video.setVisibility(View.VISIBLE);
        }

        if(selectedMedia.containsKey("Written")) {

            recorded_writing_cover.setVisibility(View.VISIBLE);
            recorded_writing.setVisibility(View.VISIBLE);
        }

    }

    public void AudioReview(View view) {

        if(!audioPlaying) {

            recorded_audio_cover.setVisibility(View.INVISIBLE);
        }

        else {

            recorded_audio_cover.setVisibility(View.VISIBLE);
        }

        audioPlaying = !audioPlaying;

    }

    public void PictureReview(View view) {

        if(!picturePlaying) {

            recorded_picture_cover.setVisibility(View.INVISIBLE);
        }

        else {

            recorded_picture_cover.setVisibility(View.VISIBLE);
        }

        picturePlaying = !picturePlaying;

    }

    public void VideoReview(View view) {

        if(!videoPlaying) {

            recorded_video_cover.setVisibility(View.INVISIBLE);
        }

        else {

            recorded_video_cover.setVisibility(View.VISIBLE);
        }

        videoPlaying = !videoPlaying;

    }

    public void WrittenReview(View view) {

        if(!writtenPlaying) {

            recorded_writing_cover.setVisibility(View.INVISIBLE);
        }

        else {

            recorded_writing_cover.setVisibility(View.VISIBLE);
        }

        writtenPlaying = !writtenPlaying;

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
}
