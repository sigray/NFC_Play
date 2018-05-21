package com.example.stucollyn.nfc_play;

import android.content.ClipData;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.support.v4.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class StoryMediaChooser extends AppCompatActivity {

    boolean audio_selected = false, picture_selected = false, video_selected = false, written_selected = false;
    ImageButton audio_select_button, picture_select_button, video_select_button, written_select_button,
            audio_confirm_button, picture_confirm_button, video_confirm_button, written_confirm_button,
            confirmation_button;
    ArrayList<String> selectedMedia;
    ArrayList<Fragment> selectedFragments;
    AudioStoryFragment audioStoryFragment;
    PictureStoryFragment pictureStoryFragment;
    VideoStoryFragment videoStoryFragment;
    WrittenStoryFragment writtenStoryFragment;
    String audioMedia = "Audio";
    String pictureMedia = "Picture";
    String videoMedia = "Video";
    String writtenMedia = "Written";
    boolean audio = false;
    boolean picture = false;
    boolean video = false;
    boolean written = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Create New Story");

        setContentView(R.layout.activity_story_media_chooser);
        selectedMedia = new ArrayList<String>();
        selectedFragments = new ArrayList<Fragment>();
        audioStoryFragment = new AudioStoryFragment();
        pictureStoryFragment = new PictureStoryFragment();
        videoStoryFragment = new VideoStoryFragment();
        writtenStoryFragment = new WrittenStoryFragment();

        audio_select_button = findViewById(R.id.audio_media_thumb);
        picture_select_button = findViewById(R.id.picture_media_thumb);
        video_select_button = findViewById(R.id.video_media_thumb);
        written_select_button = findViewById(R.id.written_notes_thumb);
        audio_confirm_button = findViewById(R.id.audio_media_thumb_confirm);
        picture_confirm_button = findViewById(R.id.picture_media_thumb_confirm);
        video_confirm_button = findViewById(R.id.video_media_thumb_confirm);
        written_confirm_button = findViewById(R.id.written_notes_thumb_confirm);
        confirmation_button = findViewById(R.id.confirm_media);
    }

    public void AudioSelected(View view) {

        if(!audio_selected)
        {
            audio_confirm_button.setVisibility(View.VISIBLE);
            //addFragment(audioStoryFragment);
            addMedia(audioMedia);
        }

        else
        {
            audio_confirm_button.setVisibility(View.INVISIBLE);
            //removeFragment(audioStoryFragment);
            removeMedia(audioMedia);

        }

        audio_selected = !audio_selected;
    }

    public void PictureSelected(View view) {

        if(!picture_selected)
        {
            picture_confirm_button.setVisibility(View.VISIBLE);
           // addFragment(pictureStoryFragment);
            addMedia(pictureMedia);

        }

        else
        {
            picture_confirm_button.setVisibility(View.INVISIBLE);
           // removeFragment(pictureStoryFragment);
            removeMedia(pictureMedia);

        }

        picture_selected = !picture_selected;

    }

    public void VideoSelected(View view) {

        if(!video_selected)
        {
            video_confirm_button.setVisibility(View.VISIBLE);
            //addFragment(videoStoryFragment);
            addMedia(videoMedia);

        }

        else
        {
            video_confirm_button.setVisibility(View.INVISIBLE);
            //removeFragment(videoStoryFragment);
            removeMedia(videoMedia);

        }

        video_selected = !video_selected;

    }

    public void WrittenSelected(View view) {

        if(!written_selected)
        {
            written_confirm_button.setVisibility(View.VISIBLE);
            //addFragment(writtenStoryFragment);
            addMedia(writtenMedia);

        }

        else
        {
            written_confirm_button.setVisibility(View.INVISIBLE);
            //removeFragment(writtenStoryFragment);
            removeMedia(writtenMedia);

        }

        written_selected = !written_selected;
    }

    /*

    public void addFragment(Fragment fragment) {

        boolean match = false;

        if(selectedFragments.size()>0) {

            for (int i = 0; i < selectedFragments.size(); i++) {

                if (fragment != selectedFragments.get(i)) {

                    match = true;
                }
            }

            if(match) {

                selectedFragments.add(fragment);
            }
        }

        else {

            selectedFragments.add(fragment);
        }

        checkConfirm(selectedFragments.size());
    }

    public void removeFragment(Fragment fragment) {

        boolean match = false;

        for(int i=0; i<selectedFragments.size(); i++) {

            if(fragment==selectedFragments.get(i)) {

                match = true;
            }
        }

        if(match) {

            selectedFragments.remove(fragment);
        }

        checkConfirm(selectedFragments.size());

    }

*/

    public void addMedia(String string) {

        boolean match = false;

        if(selectedMedia.size()>0) {

            for (int i = 0; i < selectedMedia.size(); i++) {

                if (string != selectedMedia.get(i)) {

                    match = true;
                }
            }

            if(match) {

                selectedMedia.add(string);

                Log.i("added media:", selectedMedia.toString());
            }
        }

        else {

            selectedMedia.add(string);

            Log.i("added media:", selectedMedia.toString());

        }

        checkConfirm(selectedMedia.size());

    }

    public void removeMedia(String string) {

        boolean match = false;

        for(int i=0; i<selectedMedia.size(); i++) {

            if(string==selectedMedia.get(i)) {

                match = true;
            }
        }

        if(match) {

            selectedMedia.remove(string);
            Log.i("removed media:", selectedMedia.toString());

        }

        checkConfirm(selectedMedia.size());

    }

    public void checkConfirm(int numberFragmentsSelected) {

        if (numberFragmentsSelected > 0) {

            confirmation_button.setVisibility(View.VISIBLE);
        }

        else {

            confirmation_button.setVisibility(View.INVISIBLE);
        }
    }

    public void Confirm (View view) {


        Log.i("Choos selectedMedia:", selectedMedia.toString());
        Intent intent = new Intent(StoryMediaChooser.this, NFCRecord.class);
        intent.putStringArrayListExtra("Fragments", selectedMedia);
        StoryMediaChooser.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }
}
