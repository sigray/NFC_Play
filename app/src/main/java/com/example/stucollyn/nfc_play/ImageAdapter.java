package com.example.stucollyn.nfc_play;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by StuCollyn on 07/06/2018.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    ImageButton[] imageButtons;
    int numberOfThumbs = 0;

    public ImageAdapter(Context c, int numberOfThumbs) {

        mContext = c;
        this.numberOfThumbs = numberOfThumbs;
        imageButtons = new ImageButton[numberOfThumbs];
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
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("Directory", "Creating Thumbnail: " + String.valueOf(position));
        convertView = new ImageButton(mContext);
        ImageButton cv = (ImageButton) convertView;
        imageButtons[position] = cv;
        //imageButtons[position].setLayoutParams(new ViewGroup.LayoutParams(85, 85));
        imageButtons[position].setScaleType(ImageButton.ScaleType.FIT_CENTER);
        imageButtons[position].setPadding(8, 8, 8, 8);
        imageButtons[position].setImageResource(R.drawable.nfc_tag);
        imageButtons[position].setLayoutParams(new ViewGroup.LayoutParams(180, 180));
        imageButtons[position].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Button btn = (Button)v;
//                    btn.setText(position + "");
                }
            });




//        imageButton.setImageResource(mThumbIds[position]);
        return convertView;
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