package com.example.stucollyn.nfc_play;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainMenu extends AppCompatActivity {

    ImageView miine_mini;
    Button cloud_archive;
    Button miine_library;
    Button play_story;
    Button record_story;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        miine_mini = (ImageView) findViewById(R.id.miine_mini);
        cloud_archive = (Button) findViewById(R.id.cloud_archive);
        miine_library = (Button) findViewById(R.id.miine_library);
        play_story = (Button) findViewById(R.id.play_story);
        record_story = (Button) findViewById(R.id.record_story);

        miine_library_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        miine_library_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        cloud_archive_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        cloud_archive_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        play_story_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        play_story_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        record_story_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        record_story_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        miine_mini_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);


        cloud_archive.startAnimation(cloud_archive_fade_in);
        miine_library.startAnimation(miine_library_fade_in);
        play_story.startAnimation(play_story_fade_in);
        record_story.startAnimation(record_story_fade_in);

        record_story_fade_in.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                cloud_archive.setVisibility(View.VISIBLE);
                miine_library.setVisibility(View.VISIBLE);
                play_story.setVisibility(View.VISIBLE);
                record_story.setVisibility(View.VISIBLE);
            }
        });
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
        cloud_archive.startAnimation(cloud_archive_fade_out);
        miine_library.startAnimation(miine_library_fade_out);
        play_story.startAnimation(play_story_fade_out);
        miine_mini.startAnimation(miine_mini_fade_out);
        miine_mini_fade_out.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cloud_archive.setVisibility(View.INVISIBLE);
                miine_library.setVisibility(View.INVISIBLE);
                play_story.setVisibility(View.INVISIBLE);
                record_story.setVisibility(View.INVISIBLE);
                miine_mini.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(MainMenu.this, NFCRead.class);
                MainMenu.this.startActivity(intent);
            }
        });

    }

    public void RecordStory(View view){

        record_story.setEnabled(false);
        cloud_archive.startAnimation(cloud_archive_fade_out);
        miine_library.startAnimation(miine_library_fade_out);
        play_story.startAnimation(play_story_fade_out);
        miine_mini.startAnimation(miine_mini_fade_out);
        miine_mini_fade_out.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cloud_archive.setVisibility(View.INVISIBLE);
                miine_library.setVisibility(View.INVISIBLE);
                play_story.setVisibility(View.INVISIBLE);
                record_story.setVisibility(View.INVISIBLE);
                miine_mini.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(MainMenu.this, NFCRecord.class);
                MainMenu.this.startActivity(intent);
            }
        });
    }

    public void Test(View view) {

        //Intent intent = new Intent(MainMenu.this, NFCRecord.class);
       // MainMenu.this.startActivity(intent);
    }

    }
