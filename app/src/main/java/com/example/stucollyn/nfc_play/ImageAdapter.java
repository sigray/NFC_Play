package com.example.stucollyn.nfc_play;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by StuCollyn on 07/06/2018.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    ImageView[] imageButtons;
    TextView[] imageDesc;
    int numberOfThumbs = 0;
    boolean imageButtonSelected = false;
    Activity storyGallery;
    File[] filesOnTag;
    int[] colourCode;

    public ImageAdapter(Activity storyGallery, Context c, int numberOfThumbs, File[] filesOnTag, int[] colourCode) {

        this.storyGallery = storyGallery;
        mContext = c;
        this.numberOfThumbs = numberOfThumbs;
        imageButtons = new ImageView[numberOfThumbs];
        imageDesc = new TextView[numberOfThumbs];
        this.filesOnTag = filesOnTag;
        this.colourCode = colourCode;

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

    // create a new TextView for each item referenced by the Adapter
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int currentColour = colourCode[position];

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.activity_story_gallery_grid_item, null);
            TextView imageCaption = (TextView) grid.findViewById(R.id.grid_item_text);
            imageButtons[position] = (ImageView)grid.findViewById(R.id.grid_item_background);
            ImageView imageBackground = (ImageView)grid.findViewById(R.id.grid_item_image);
            imageCaption.setText(filesOnTag[position].getName());
            imageButtons[position].setBackgroundColor(currentColour);
        } else {
            grid = (View) convertView;
        }

        imageButtons[position].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/"+filesOnTag[position].getName();
                File directory = new File(path);
                File[] files = directory.listFiles();

                //ThumbnailSelected();
                Intent intent = new Intent(storyGallery.getApplicationContext(), StoryGallerySaveOrView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("StoryDetails", filesOnTag[position]);
                intent.putExtra("filesOnTag", files);
                storyGallery.getApplicationContext().startActivity(intent);
                storyGallery.overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
            }
        });


        return grid;
    }



/*



        Log.d("Directory", "Creating Thumbnail: " + String.valueOf(position));


        convertView = new TextView(mContext);
        TextView cv = (TextView) convertView;
        imageButtons[position] = cv;
        imageButtons[position].setBackgroundColor(Color.parseColor("#756bc7"));
        imageButtons[position].setMinimumHeight(400);
        imageButtons[position].setText(filesOnTag[position].getName());
        imageButtons[position].setTextSize(30);
        imageButtons[position].setGravity(Gravity.CENTER);
        //imageButtons[position].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //imageButtons[position].setScaleType(TextView.ScaleType.FIT_CENTER);
        //imageButtons[position].setImageResource(R.drawable.nfc_tag);
        //imageButtons[position].setPadding(8, 8, 8, 8);
        //imageButtons[position].setLayoutParams(new ViewGroup.LayoutParams(85, 85));
        //TextDrawable textDrawable = new TextDrawable("Test Text");
        //textDrawable.draw();

        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/"+filesOnTag[position].getName();
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {

            String extension = FilenameUtils.getExtension(filesOnTag[position].toString());
            String fileName = files[i].toString();
            ImageToDrawable imageToDrawable = new ImageToDrawable(files[i]);

            if (extension.equalsIgnoreCase("jpg")) {

            imageButtons[position].setBackground(imageToDrawable.getPicture());

            }
        }


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
*/


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