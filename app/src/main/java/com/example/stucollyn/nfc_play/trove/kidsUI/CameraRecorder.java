package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.stucollyn.nfc_play.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by StuCollyn on 07/06/2018.
 */

class CameraRecorder extends Application {

    //File Save Variables
    File story_directory, tag_directory = null, cover_directory = null, image, tagFile, coverFile;
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
    private boolean safeToTakePicture = false;

    public CameraRecorder(Activity activity, Context context, File story_directory, File tag_directory, File cover_directory, Camera mCamera, CameraPreview mPreview) {
        this.context = context;
        this.story_directory = story_directory;
        this.tag_directory = tag_directory;
        this.cover_directory = cover_directory;
        this.activity = activity;
        this.mCamera = mCamera;
        this.mPreview = mPreview;

        captureButton = (ImageButton) activity.findViewById(R.id.button_capture);
        preview = (FrameLayout) activity.findViewById(R.id.camera_preview);
        camera_linear = (LinearLayout) activity.findViewById(R.id.camera_linear);
        fadein = AnimationUtils.loadAnimation(context, R.anim.fadein);
        fadeout = AnimationUtils.loadAnimation(context, R.anim.fadeout);

        Log.i("SD: ", story_directory.toString());
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

    void setSafeToTakePicture(boolean safeToTakePicture) {

        this.safeToTakePicture = safeToTakePicture;
    }

    boolean getSafeToTakePicture() {

        return safeToTakePicture;
    }

    /** Create a file Uri for saving an image or video */
    Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    File getOutputMediaFile(int type){


//        File mediaStorageDir = story_directory;


        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_"+ timeStamp + ".jpg");
            UUID storyName = UUID.randomUUID();
            String imageFileName = storyName.toString();

            Log.i("SD2: ", story_directory.toString());

            mediaFile = new File(story_directory.getPath() + File.separator +
                        imageFileName + ".jpg");
            photoPath = mediaFile.getAbsolutePath();


            if(tag_directory!=null) {
                Log.i("Cool", "t directory empty" + ": " + tag_directory.toString());
                tagFile = new File(tag_directory.getPath() + File.separator +
                        imageFileName + ".jpg");
            }

            if(cover_directory!=null) {
                Log.i("Cool", "c directory empty" + ": " + cover_directory.toString());
                coverFile = new File(cover_directory.getPath() + File.separator +
                        imageFileName + ".jpg");
            }

        } else {
            return null;
        }

        return mediaFile;
    }

    File getTagFile(){

        return tagFile;
    }

    File getCoverFile(){

        return coverFile;
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

            // progressToRecordStory only if the File was successfully created
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
//        tagImage = File.createTempFile(imageFileName, ".jpg", storageDir, tag_directory);

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
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

    public void copyFile(File sourceFile, File destFile) throws IOException {

        Log.i("UMMM", "t directory" + ": " + destFile.toString());

        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }

        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
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
