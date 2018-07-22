package com.example.stucollyn.nfc_play;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CloudStoryGalleryMenu extends AppCompatActivity {

    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = (Integer) getIntent().getExtras().get("Orientation");
        setContentView(R.layout.activity_cloud_story_gallery_menu);
    }

    public void SearchText(View view) {

        Intent intent = new Intent(CloudStoryGalleryMenu.this, CloudStoryGallery.class);
        intent.putExtra("Orientation", mode);
        CloudStoryGalleryMenu.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }


    public void SearchImage(View view) {

        Intent intent = new Intent(CloudStoryGalleryMenu.this, CloudStoryGallery.class);
        intent.putExtra("Orientation", mode);
        CloudStoryGalleryMenu.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }


    public void SearchDate(View view) {

        Intent intent = new Intent(CloudStoryGalleryMenu.this, CloudStoryGallery.class);
        intent.putExtra("Orientation", mode);
        CloudStoryGalleryMenu.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }
}
