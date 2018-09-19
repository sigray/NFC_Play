package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stucollyn.nfc_play.R;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by StuCollyn on 07/06/2018.
 */

public class ImageAdapterKidsUI extends  RecyclerView.Adapter<ImageAdapterKidsUI.SimpleViewHolder> {
    private Context mContext;
    ImageView[] imageButtons;
    TextView[] imageDesc;
    int numberOfThumbs = 0;
    boolean imageButtonSelected = false;
    Activity storyGallery;
    ArrayList<File> filesOnTag;
    int[] colourCode;
    HashMap<File, Bitmap> imageMap;
    HashMap<File, File> folderToImageRef;
    private List<String> elements;


    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;

        public SimpleViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.grid_item_background_kids_ui);

        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.activity_story_gallery_grid_item_kids_ui, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {

        int currentColour = colourCode[position];

        if(!imageMap.isEmpty()) {

            File value = folderToImageRef.get(filesOnTag.get(position));


            for (Map.Entry<File,File> entry : folderToImageRef.entrySet()) {
                File key = entry.getKey();
                File values = entry.getValue();

//                Log.i("Folders with images: ", "Key: " + key + ", Value: " + values);
//
            }

            for (Map.Entry<File,Bitmap> entry : imageMap.entrySet()) {
                File key = entry.getKey();
                Bitmap values = entry.getValue();

//                    Log.i("Images Bitmaps: ", "Key: " + key + ", Value: " + values);

            }


            Bitmap bitmap = imageMap.get(filesOnTag.get(position));


//            imageButtons[position].setImageBitmap(bitmap);
            holder.imageView.setImageBitmap(bitmap);
        }







        holder.imageView.setBackgroundColor(currentColour);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Position =" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.elements.size();
    }


    public ImageAdapterKidsUI(Activity storyGallery, Context c, int numberOfThumbs, ArrayList<File> filesOnTag, int[] colourCode, HashMap<File, File> folderToImageRef, HashMap<File, Bitmap> imageMap) {

        this.storyGallery = storyGallery;
        mContext = c;
        this.numberOfThumbs = numberOfThumbs;
        imageButtons = new ImageView[numberOfThumbs];
        imageDesc = new TextView[numberOfThumbs];
        this.filesOnTag = filesOnTag;
        this.colourCode = colourCode;
        this.imageMap = imageMap;
        this.folderToImageRef = folderToImageRef;

        this.elements = new ArrayList<String>();
        // Fill dummy list
        for(int i = 0; i < filesOnTag.size() ; i++) {
            this.elements.add(i, "Position : " + i);
        }



        for (Map.Entry<File,File> entry : folderToImageRef.entrySet()) {
            File key = entry.getKey();
            File value = entry.getValue();

        }

        for (Map.Entry<File,Bitmap> entry : imageMap.entrySet()) {
            File key = entry.getKey();
            Bitmap value = entry.getValue();

        }

    }

    File[] FilesForThumbnail(int position) {

        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/"+filesOnTag.get(position).getName();
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
        int smallSizeScaleFactor = Math.min(photoW / 800, photoH / 800);


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

}