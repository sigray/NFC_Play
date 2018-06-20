package com.example.stucollyn.nfc_play;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.io.File;

public class StoryGallery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_gallery);

        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/";
        File directory = new File(path);
        File[] files = directory.listFiles();

        for (int i = 0; i < files.length; i++) {
            Log.d("Directory", "FileName:" + files[i].getName());
        }

        int numberOfThumbs = files.length;
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, numberOfThumbs));
    }
}
