package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.stucollyn.nfc_play.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.example.stucollyn.nfc_play.trove.kidsUI.LoggedInWriteHomeKidsUI.REQUEST_IMAGE_CAPTURE;

/**
 * Created by StuCollyn on 07/06/2018.
 */

class CameraRecorder extends Application {

    //File Save Variables
    File story_directory, image;
    private Context context;
    String videoPath, photoPath;
    Activity activity;
    int rotationInDegrees;
    Bitmap adjustedBitmap;
    Uri photoURI;
    ImageButton captureButton;
    FrameLayout preview;
    LinearLayout camera_linear;
    Animation fadein, fadeout;
    Camera mCamera;
    CameraPreview mPreview;
    public static final int MEDIA_TYPE_IMAGE = 1;
    boolean pictureSave = false;



    public CameraRecorder(Activity activity, Context context, File story_directory, Camera mCamera, CameraPreview mPreview) {
        this.context = context;
        this.story_directory = story_directory;
        this.activity = activity;
        this.mCamera = mCamera;
        this.mPreview = mPreview;

        captureButton = (ImageButton) activity.findViewById(R.id.button_capture);
        preview = (FrameLayout) activity.findViewById(R.id.camera_preview);
        camera_linear = (LinearLayout) activity.findViewById(R.id.camera_linear);
        fadein = AnimationUtils.loadAnimation(context, R.anim.fadein);
        fadeout = AnimationUtils.loadAnimation(context, R.anim.fadeout);

        Log.i("Test", "This far D");
    }



    public static Camera getCameraInstance() {

            Camera c = null;
            try {
                c = Camera.open(1); // attempt to get a Camera instance
            }
            catch (Exception e){
                // Camera is not available (in use or does not exist)
                Log.i("Tag", "No Camera here mate");
            }
            return c; // returns null if camera is unavailable
        }

    /** Create a file Uri for saving an image or video */
    Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    File getOutputMediaFile(int type){


        File mediaStorageDir = story_directory;


        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_"+ timeStamp + ".jpg");
            UUID storyName = UUID.randomUUID();
            String imageFileName = storyName.toString();
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    imageFileName + ".jpg");
            photoPath = mediaFile.getAbsolutePath();

        } else {
            return null;
        }

        return mediaFile;


        /*

        try {


            UUID storyName = UUID.randomUUID();
            String imageFileName = storyName.toString();
            File storageDir;

            if (Build.VERSION.SDK_INT >= 19) {
                //storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                storageDir = story_directory;
            } else {
                //storageDir = getExternalFilesDir(Environment.getExternalStorageDirectory() + "/Documents");
                storageDir = story_directory;
            }

            File image = File.createTempFile(imageFileName, ".jpg", storageDir);

            // Save a file: path for use with ACTION_VIEW intents
            photoPath = image.getAbsolutePath();

        }

        catch (IOException e) {

        }

        return image;

        */

    }



/*

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(context.getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
               // photoURI = FileProvider.getUriForFile(this,
                  //      "com.example.android.fileprovider",
                    //    photoFile);
                photoURI = FileProvider.getUriForFile(context, "com.example.android.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                  intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
//        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
//        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                activity.startActivityForResult(intent, 100);
            }
        }

        */

    private File createImageFile() throws IOException {
        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
        UUID storyName = UUID.randomUUID();;
        String imageFileName = storyName.toString();
        File storageDir;

        if (Build.VERSION.SDK_INT >= 19) {
            //storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            storageDir = story_directory;
        }

        else {
            //storageDir = getExternalFilesDir(Environment.getExternalStorageDirectory() + "/Documents");
            storageDir = story_directory;
        }

        image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
        Log.i("File Name", "1" + photoPath);
        return image;
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

    void PictureProcessing() {

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        rotationInDegrees = exifToDegrees(rotation);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int smallSizeScaleFactor = Math.min(photoW / 200, photoH / 200);


        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = smallSizeScaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
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
    }

    void fullScreenPicture() {
          /* if(!fullSizedPicture) {
            Bitmap fullSizedBitmap = BitmapFactory.decodeFile(photoPath);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            adjustedFullSizedBitmap = Bitmap.createBitmap(fullSizedBitmap, 0, 0, fullSizedBitmap.getWidth(), fullSizedBitmap.getHeight(), matrix, true);
        }

        fullSizedPicture = !fullSizedPicture;
        picture_story_fragment.ShowFullSizedPicture(fullSizedPicture, adjustedFullSizedBitmap);
        */

        //        Intent openFullSize = new Intent(Intent.ACTION_VIEW);
//        openFullSize.setDataAndType(Uri.fromFile(file), "image/");
//        openFullSize.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    }

    Bitmap getAdjustedBitmap() {

        return adjustedBitmap;
    }

    int getRotationInDegrees() {

        return rotationInDegrees;
    }

    String getPhotoPath() {

        return photoPath;
    }

    Uri getPhotoURI() {

        return photoURI;
    }

}
