package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.stucollyn.nfc_play.R;
import com.example.stucollyn.nfc_play.ReviewAudioStory;
import com.example.stucollyn.nfc_play.ReviewPictureStory;
import com.example.stucollyn.nfc_play.ReviewVideoStory;
import com.example.stucollyn.nfc_play.ReviewWrittenStory;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class ExploreArchiveItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_archive_item);
    }
}
