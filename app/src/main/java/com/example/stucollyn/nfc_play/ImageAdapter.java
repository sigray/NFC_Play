package com.example.stucollyn.nfc_play;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import java.io.File;

/**
 * Created by StuCollyn on 07/06/2018.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    ImageButton[] imageButtons;
    int numberOfThumbs = 0;
    boolean imageButtonSelected = false;
    Activity storyGallery;
    File[] filesOnTag;

    public ImageAdapter(Activity storyGallery, Context c, int numberOfThumbs, File[] filesOnTag) {

        this.storyGallery = storyGallery;
        mContext = c;
        this.numberOfThumbs = numberOfThumbs;
        imageButtons = new ImageButton[numberOfThumbs];
        this.filesOnTag = filesOnTag;
        Log.d("Directory", "Number of Files:" + String.valueOf(numberOfThumbs));
    }

    @Override
    public int getCount() {
        return numberOfThumbs;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Log.d("Directory", "Creating Thumbnail: " + String.valueOf(position));
        convertView = new ImageButton(mContext);
        ImageButton cv = (ImageButton) convertView;
        imageButtons[position] = cv;
        //imageButtons[position].setLayoutParams(new ViewGroup.LayoutParams(85, 85));
        imageButtons[position].setScaleType(ImageButton.ScaleType.FIT_CENTER);
        imageButtons[position].setPadding(8, 0, 8, 0);
        imageButtons[position].setImageResource(R.drawable.nfc_tag);

//        TextDrawable textDrawable = new TextDrawable("Test Text");
//        textDrawable.draw();

        imageButtons[position].setLayoutParams(new ViewGroup.LayoutParams(170, 170));
        imageButtons[position].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //ThumbnailSelected();
                    Intent intent = new Intent(storyGallery.getApplicationContext(), StoryGallerySaveOrView.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("StoryDetails", filesOnTag[position]);
                    intent.putExtra("filesOnTag", filesOnTag);
                    storyGallery.getApplicationContext().startActivity(intent);
                    storyGallery.overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
                }
            });




//        imageButton.setImageResource(mThumbIds[position]);
        return convertView;
    }

    void ThumbnailSelected() {

        imageButtonSelected = true;
    }

    boolean checkButtonSelection() {

        return  imageButtonSelected;
    }

    // references to our images
    private Integer[] mThumbIds = {

            R.drawable.nfc_icon, R.drawable.nfc
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7,
//            R.drawable.sample_0, R.drawable.sample_1,
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7,
//            R.drawable.sample_0, R.drawable.sample_1,
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7
    };
}