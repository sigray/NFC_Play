package com.example.stucollyn.nfc_play;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridView;

import java.io.File;

public class StoryGallery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Story Library");
        setContentView(R.layout.activity_story_gallery);

        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/";
        File directory = new File(path);
        File[] files = directory.listFiles();

        int colourCounter = 0;
        int currentColour = Color.parseColor("#756bc7");;
        int[] colourCode = new int[files.length];


        for (int i = 0; i < files.length; i++) {

            Log.i("Files in Folder: ", files[i].getName());


            if(colourCounter==0) {

                currentColour = Color.parseColor("#756bc7");
                colourCounter++;
            }

            else if(colourCounter==1) {

                currentColour = Color.parseColor("#ffb491");
                colourCounter++;
            }

            else if (colourCounter>1) {

                currentColour = Color.parseColor("#54b8a9");
                colourCounter = 0;
            }

            colourCode[i] = currentColour;

        }

        int numberOfThumbs = files.length;
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, this, numberOfThumbs, files, colourCode));
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(StoryGallery.this, MainMenu.class);
        StoryGallery.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
        finish();
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
