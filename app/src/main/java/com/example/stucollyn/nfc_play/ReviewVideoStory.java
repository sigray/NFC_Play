package com.example.stucollyn.nfc_play;

import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

public class ReviewVideoStory extends AppCompatActivity {

    TextView instruction;
    MediaController mediaController, fullScreenMediaController;
    VideoView captured_video, full_sized_video;
    ImageButton expand_video_button, shrink_video_button;
    ImageView captured_video_background, full_screen_video_background;
    boolean is_fullscreen_video_on = false;
    File videoFile;
    Uri story_directory_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Play Video Story");
        videoFile = (File)getIntent().getExtras().get("VideoFile");
        setContentView(R.layout.activity_review_video_story);

        captured_video = (VideoView) findViewById(R.id.captured_video);
        full_sized_video = (VideoView) findViewById(R.id.full_sized_video);
        expand_video_button = (ImageButton) findViewById(R.id.expand_video_button);
        shrink_video_button = (ImageButton) findViewById(R.id.shrink_video_button);
        captured_video_background = (ImageView) findViewById(R.id.captured_video_background);
        full_screen_video_background = (ImageView) findViewById(R.id.full_screen_video_background);
        mediaController = new MediaController(this);
        fullScreenMediaController = new MediaController(this);

        story_directory_uri = FileProvider.getUriForFile(this,
                "com.example.android.fileprovider",
                videoFile);
    }

    public void ShowFullSizedVideo() {

        if(!is_fullscreen_video_on) {

            captured_video.suspend();
            captured_video.setVisibility(View.INVISIBLE);
            expand_video_button.setVisibility(View.INVISIBLE);
            shrink_video_button.setVisibility(View.VISIBLE);
            full_screen_video_background.setVisibility(View.VISIBLE);
            full_sized_video.setVisibility(View.VISIBLE);
            full_sized_video.setVideoURI(story_directory_uri);
            fullScreenMediaController.setAnchorView(full_sized_video);
            full_sized_video.setMediaController(fullScreenMediaController);
            full_sized_video.start();
        }

        else {
            full_sized_video.setVisibility(View.INVISIBLE);
            full_screen_video_background.setVisibility(View.INVISIBLE);
            shrink_video_button.setVisibility(View.INVISIBLE);
            expand_video_button.setVisibility(View.VISIBLE);
            captured_video.setVisibility(View.VISIBLE);
            captured_video.resume();
            captured_video.start();
        }

    }

    public void ShowVideo() {

        captured_video.setVisibility(View.VISIBLE);
        captured_video_background.setVisibility(View.VISIBLE);
        expand_video_button.setVisibility(View.VISIBLE);

        captured_video.setVideoURI(story_directory_uri);
        mediaController.setAnchorView(captured_video);
        captured_video.setMediaController(mediaController);
        captured_video.start();

    }

    public void FullSizedVideo(View view) {

        ShowFullSizedVideo();
    }
}