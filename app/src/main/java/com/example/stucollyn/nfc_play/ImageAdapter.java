package com.example.stucollyn.nfc_play;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

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

    File[] FilesForThumbnail(int position) {

        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/"+filesOnTag[position].getName();
        File directory = new File(path);
        File[] files = directory.listFiles();

        return files;
    }

    boolean CheckForPicture(File[] files, int position, boolean coverExists) {


        return coverExists;
    }

    File GetPicture(File[] files) {

        File file = null;

        for(int i = 0; i<files.length; i++) {

            String extension = FilenameUtils.getExtension(files[i].toString());
            String fileName = files[i].toString();

            if (extension.equalsIgnoreCase("jpg")) {

                file = files[i];
            }

        }

        return file;
    }

    Bitmap ShowPicture(File pictureFile) {

        ExifInterface exif = null;
        Bitmap adjustedBitmap;
        try {
            exif = new ExifInterface(pictureFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotationInDegrees = exifToDegrees(rotation);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int smallSizeScaleFactor = Math.min(photoW / 500, photoH / 500);


        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = smallSizeScaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), bmOptions);
        Matrix matrix = new Matrix();
        if (rotation != 0f) {
            matrix.preRotate(rotationInDegrees);
        }

        if (rotationInDegrees == 90 || rotationInDegrees == 270) {
            adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else if (rotationInDegrees == 180) {
            adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            // adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            adjustedBitmap = bitmap;
        }

        return adjustedBitmap;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (
                exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View grid;
        LayoutInflater inflater;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        grid = new View(mContext);
        grid = inflater.inflate(R.layout.activity_story_gallery_grid_item, null);

        int currentColour = colourCode[position];

        if (convertView == null) {


            TextView imageCaption = (TextView) grid.findViewById(R.id.grid_item_text);
            imageButtons[position] = (ImageView) grid.findViewById(R.id.grid_item_background);
            imageCaption.setText(filesOnTag[position].getName());
            imageButtons[position].setBackgroundColor(currentColour);

            imageButtons[position].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    File[] files = FilesForThumbnail(position);

                    //ThumbnailSelected();
                    Intent intent = new Intent(storyGallery.getApplicationContext(), StoryGallerySaveOrView.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("StoryDetails", filesOnTag[position]);
                    intent.putExtra("filesOnTag", files);
                    storyGallery.getApplicationContext().startActivity(intent);
                    storyGallery.overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
                }
            });

            File[] files = FilesForThumbnail(position);
            File file = GetPicture(files);

            if(file!= null) {

                Bitmap coverConv = ShowPicture(file);
                imageButtons[position].setImageBitmap(coverConv);
                imageButtons[position].setBackgroundColor(currentColour);

            }

        }

        else {

            grid = (View) convertView;
        }

        return grid;
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