package com.example.stucollyn.nfc_play;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StoryGalleryMenu extends AppCompatActivity {

    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        mode = (Integer) getIntent().getExtras().get("Orientation");
        setContentView(R.layout.activity_story_gallery_menu);
    }

    public void Local(View view){

        Intent intent = new Intent(StoryGalleryMenu.this, LocalStoryGallery.class);
        intent.putExtra("Orientation", mode);
        StoryGalleryMenu.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    public void Cloud(View view){

        Intent intent = new Intent(StoryGalleryMenu.this, CloudStoryGalleryMenu.class);
        intent.putExtra("Orientation", mode);
        StoryGalleryMenu.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }
}
