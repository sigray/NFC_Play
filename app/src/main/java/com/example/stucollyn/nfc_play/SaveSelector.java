package com.example.stucollyn.nfc_play;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.io.File;

public class SaveSelector extends AppCompatActivity {

    File fileDirectory;
    String tag_data = "";
    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_selector);
        mode = (Integer) getIntent().getExtras().get("Orientation");
        setRequestedOrientation(mode);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Save New Story");
        fileDirectory = (File)getIntent().getExtras().get("StoryDirectory");
        tag_data = (String)getIntent().getExtras().get("TagData");
    }

    public void StartConfirmation(View view){

        Intent intent = new Intent(SaveSelector.this, SavedStoryConfirmation.class);
        intent.putExtra("Orientation", mode);
        SaveSelector.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    public void StartSaveStoryToNFC(View view) {

        Intent intent = new Intent(SaveSelector.this, SaveStoryToNFC.class);
        intent.putExtra("TagData", tag_data);
        intent.putExtra("Orientation", mode);
        SaveSelector.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SaveSelector.this, NewStoryReview.class);
        intent.putExtra("Orientation", mode);
        SaveSelector.this.startActivity(intent);
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
