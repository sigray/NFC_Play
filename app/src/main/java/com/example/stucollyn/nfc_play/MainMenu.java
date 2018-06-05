package com.example.stucollyn.nfc_play;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;

public class MainMenu extends AppCompatActivity {

    TextView welcome;
    ImageView miine_mini;
    VideoView top_half__video;
    Button cloud_archive;
    ImageButton miine_library;
    ImageButton play_story;
    ImageButton record_story;
    FrameLayout top_holder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Home");

        miine_mini = (ImageView) findViewById(R.id.miine_mini);
        //cloud_archive = (Button) findViewById(R.id.cloud_archive);
        miine_library = (ImageButton) findViewById(R.id.miine_library);
        play_story = (ImageButton) findViewById(R.id.play_story);
        record_story = (ImageButton) findViewById(R.id.record_story);
        top_half__video = (VideoView) findViewById(R.id.main_menu_video);
        top_holder = (FrameLayout) findViewById(R.id.top_half_holder);
        welcome = (TextView) findViewById(R.id.welcome);

        // cloud_archive.setVisibility(View.VISIBLE);
        miine_library.setVisibility(View.VISIBLE);
        play_story.setVisibility(View.VISIBLE);
        record_story.setVisibility(View.VISIBLE);
        welcome.setVisibility(View.VISIBLE);
        top_half__video.setVisibility(View.VISIBLE);
        top_holder.setVisibility(View.VISIBLE);

        try {
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(top_half__video);
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.trove_video);
            Log.i("Resrouce", uri.toString());
            mediaController.setAnchorView(top_half__video);
            top_half__video.setMediaController(mediaController);
            top_half__video.setVideoURI(uri);
            top_half__video.start();
        }

        catch (NullPointerException e) {

        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(MainMenu.this, LoginScreen.class);
        MainMenu.this.startActivity(intent);
    }

    public void CloudArchive(View view) {

        cloud_archive.setEnabled(false);
    }

    public void MiineLibrary(View view) {

        miine_library.setEnabled(false);
    }

    public void PlayStory(View view) {

        play_story.setEnabled(false);
        Intent intent = new Intent(MainMenu.this, NFCRead.class);
        MainMenu.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);

    }

    public void RecordStory(View view){

        record_story.setEnabled(false);
        Intent intent = new Intent(MainMenu.this, StoryMediaChooser.class);
        MainMenu.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);

    }

    public void Test(View view) {

        //Intent intent = new Intent(MainMenu.this, NFCRecord.class);
       // MainMenu.this.startActivity(intent);
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
