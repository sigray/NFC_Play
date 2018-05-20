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
    Animation miine_mini_fade_in;
    Animation miine_mini_fade_out;
    Animation cloud_archive_fade_in;
    Animation cloud_archive_fade_out;
    Animation miine_library_fade_in;
    Animation miine_library_fade_out;
    Animation play_story_fade_in;
    Animation play_story_fade_out;
    Animation record_story_fade_in;
    Animation record_story_fade_out;
    Animation welcome_fade_in;
    Animation welcome_fade_out;
    Animation top_half__video_fade_in;
    Animation top_half__video_fade_out;
    Animation top_holder_fade_in;
    Animation top_holder_fade_out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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

        miine_library_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        miine_library_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        cloud_archive_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        cloud_archive_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        play_story_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        play_story_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        record_story_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        record_story_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        miine_mini_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        welcome_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        welcome_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        top_half__video_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        top_half__video_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        top_holder_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        top_holder_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);

        record_story_fade_in.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

               // cloud_archive.setVisibility(View.VISIBLE);
                miine_library.setVisibility(View.VISIBLE);
                play_story.setVisibility(View.VISIBLE);
                record_story.setVisibility(View.VISIBLE);
                welcome.setVisibility(View.VISIBLE);
                top_half__video.setVisibility(View.VISIBLE);
                top_holder.setVisibility(View.VISIBLE);
            }
        });

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

       // cloud_archive.startAnimation(cloud_archive_fade_out);
//        miine_library.startAnimation(miine_library_fade_out);
//        play_story.startAnimation(play_story_fade_out);
//        //miine_mini.startAnimation(miine_mini_fade_out);
//        play_story_fade_out.setAnimationListener(new AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//               // cloud_archive.setVisibility(View.INVISIBLE);
//                miine_library.setVisibility(View.INVISIBLE);
//                play_story.setVisibility(View.INVISIBLE);
//                record_story.setVisibility(View.INVISIBLE);
//                miine_mini.setVisibility(View.INVISIBLE);
//                welcome.setVisibility(View.INVISIBLE);
//                top_half__video.setVisibility(View.INVISIBLE);
//                top_holder.setVisibility(View.INVISIBLE);
//                Intent intent = new Intent(MainMenu.this, NFCRead.class);
//                MainMenu.this.startActivity(intent);

        // }
       // });

    }

    public void RecordStory(View view){

        record_story.setEnabled(false);
        Intent intent = new Intent(MainMenu.this, NFCRecord.class);
        MainMenu.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
      //  cloud_archive.startAnimation(cloud_archive_fade_out);
//        miine_library.startAnimation(miine_library_fade_out);
//        play_story.startAnimation(play_story_fade_out);
//        welcome.startAnimation(play_story_fade_out);
//        top_half__video.startAnimation(play_story_fade_out);
//        top_holder.startAnimation(play_story_fade_out);
//
//        //miine_mini.startAnimation(miine_mini_fade_out);
//        play_story_fade_out.setAnimationListener(new AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//               // cloud_archive.setVisibility(View.INVISIBLE);
//                miine_library.setVisibility(View.INVISIBLE);
//                play_story.setVisibility(View.INVISIBLE);
//                record_story.setVisibility(View.INVISIBLE);
//                miine_mini.setVisibility(View.INVISIBLE);
//                welcome.setVisibility(View.INVISIBLE);
//                top_half__video.setVisibility(View.INVISIBLE);
//                top_holder.setVisibility(View.INVISIBLE);
//                Intent intent = new Intent(MainMenu.this, NFCRecord.class);
//                MainMenu.this.startActivity(intent);
//            }
//        });
    }

    public void Test(View view) {

        //Intent intent = new Intent(MainMenu.this, NFCRecord.class);
       // MainMenu.this.startActivity(intent);
    }

    }
