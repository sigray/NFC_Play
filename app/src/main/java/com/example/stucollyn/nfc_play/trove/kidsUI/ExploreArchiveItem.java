package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.stucollyn.nfc_play.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ExploreArchiveItem extends AppCompatActivity {

    HashMap<String, ArrayList<ObjectStoryRecordKidsUI>> objectRecordMap;
    LinkedHashMap<String, File> fileMap;
    LinkedHashMap<String, Bitmap> storyCoverMap;
    LinkedHashMap<String, String> storyTypeMap;
    ArrayList<ObjectStoryRecordKidsUI> objectFiles;
    ProgressBar progressBar;
    ExploreImageAdapterKidsUI cloudImageAdapter;
    HorizontalGridView gridview;
    int colourCounter;
    int currentColour;
    int[] colourCode;
    private StorageReference mStorageRef;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String objectName;
    Activity activity;
    Context context;
    boolean authenticated;
    File story_directory;
    ImageView back;
    AnimatedVectorDrawable backRetrace, backBegin;
    Handler animationBackHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_archive_kids_ui);
        gridview = (HorizontalGridView) findViewById(R.id.gridView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        storyCoverMap = new LinkedHashMap<String, Bitmap>();
        storyTypeMap = new LinkedHashMap<String, String>();
        objectRecordMap = (HashMap<String, ArrayList<ObjectStoryRecordKidsUI>>) getIntent().getExtras().get("ObjectStoryRecord");
        objectName = (String) getIntent().getExtras().get("ObjectName");
        activity = this;
        context = this;
        authenticated = (Boolean) getIntent().getExtras().get("Authenticated");
        fileMap = new LinkedHashMap<String, File>();

        AnimationSetup();
        LoadFiles();
    }

    //Animation Setup
    void AnimationSetup() {

        back = (ImageView) findViewById(R.id.back);
        backBegin = (AnimatedVectorDrawable) getDrawable(R.drawable.kids_ui_close_1);
        backRetrace = (AnimatedVectorDrawable) getDrawable(R.drawable.kids_ui_close_2);

        animationBackHandler = new Handler();
        animationBackHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                back.setVisibility(View.VISIBLE);
                Drawable d = back.getDrawable();
                final AnimatedVectorDrawable zigzaganim = (AnimatedVectorDrawable) d;
                zigzaganim.start();
            }
        }, 1000);

        animationBackHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.i("YUUUUUS", "yessir");
                back.setVisibility(View.VISIBLE);
                Drawable d = VectorDrawableCompat.create(getResources(), R.drawable.kids_ui_close, null);
                d = DrawableCompat.wrap(d);
                DrawableCompat.setTint(d, Color.WHITE);
                back.setImageDrawable(d);

            }
        }, 2000);
    }

    File setupStoryDirectory(String ObjectName) {

        String packageLocation = ("/Cloud");
        String newDirectory = packageLocation + "/" + ObjectName;
        story_directory = getExternalFilesDir(newDirectory);

        return story_directory;
    }

    void getStoryCover(String StoryName, String StoryType, File file) {

        if(StoryType.equalsIgnoreCase("AudioFile")) {

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.audio_icon);
            storyCoverMap.put(StoryName, icon);
            storyTypeMap.put(StoryName, StoryType);
        }

        else if(StoryType.equalsIgnoreCase("PictureFile")) {

            storyCoverMap.put(StoryName, ShowPicture(file));
            storyTypeMap.put(StoryName, StoryType);
        }

        else if (StoryType.equalsIgnoreCase("WrittenFile")) {

        }

        else if(StoryType.equalsIgnoreCase("VideoFile")) {

        }
    }

    File TempFile(String StoryName, String StoryType, File story_directory) {

        File file = null;
        String type = "";
        String ext = "";

        if(StoryType.equalsIgnoreCase("AudioFile")) {

            type = "audio";
            ext = ".mp3";
        }

        else if(StoryType.equalsIgnoreCase("PictureFile")) {

            type = "images";
            ext = ".jpg";        }

        else if (StoryType.equalsIgnoreCase("WrittenFile")) {

            type = "text";
            ext = ".txt";        }

        else if(StoryType.equalsIgnoreCase("VideoFile")) {

            type = "video";
            ext = ".mp4";        }

        try {



            file = File.createTempFile(StoryName, ext, story_directory);

            Uri story_directory_uri = FileProvider.getUriForFile(context,
                    "com.example.android.fileprovider",
                    file.getAbsoluteFile());
        }

        catch (IOException e) {

        }

        return file;
    }

    void DownloadFromCloud(String StoryName, String URLLink, String StoryType, File story_directory) {


        final String theStoryType = StoryType;
        final String theStoryName = StoryName;
        StorageReference gsReference;
        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();
        gsReference = storage.getReferenceFromUrl(URLLink);

        final File file = TempFile(theStoryName, theStoryType, story_directory);

            gsReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    fileMap.put(theStoryName, file);
                    getStoryCover(theStoryName, theStoryType, file);
                    CloudThumbnailColours();
                    progressBar.setVisibility(View.INVISIBLE);

                    cloudImageAdapter = new ExploreImageAdapterKidsUI(activity, context, fileMap.size(), fileMap, colourCode, objectRecordMap, storyCoverMap, storyTypeMap);
                    gridview.invalidate();
                    gridview.setAdapter(cloudImageAdapter);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
    }

    void LoadFiles() {

        objectFiles = new ArrayList<ObjectStoryRecordKidsUI>();
        objectFiles = objectRecordMap.get(objectName);
        File story_directory = setupStoryDirectory(objectName);

        if(authenticated) {

            for (int i = 0; i < objectFiles.size(); i++) {

//            if(objectFiles.get(i).getObjectContext().equals("Cloud")) {


                DownloadFromCloud(objectFiles.get(i).getStoryName(), objectFiles.get(i).getStoryRef(), objectFiles.get(i).getStoryType(), story_directory);
            }

        }
//            else if(objectFiles.get(i).getObjectContext().equals("Local")) {

        else {
            new LocalFiles().execute();
        }
    }

    void LoadLocalFiles(){

        for(int i=0; i<objectFiles.size(); i++) {

            String path = objectFiles.get(i).getStoryRef();
            File file = new File(path);
            fileMap.put(objectFiles.get(i).getStoryName(), file);

            Log.i("FileMap: ", fileMap.toString());
            Log.i("getStoryCover: ", objectFiles.get(i).getStoryName() + ", " + objectFiles.get(i).getStoryType() + ", " + file.getName());

            getStoryCover(objectFiles.get(i).getStoryName(), objectFiles.get(i).getStoryType(), file);
            CloudThumbnailColours();
        }
    }



    void CloudThumbnailColours() {

        colourCounter = 0;
        currentColour = Color.parseColor("#756bc7");;
        colourCode = new int[fileMap.size()];


        for (int i = 0; i < fileMap.size(); i++) {

            if(colourCounter==0) {

                currentColour = Color.parseColor("#756bc7");
                colourCounter++;
            }

            else if(colourCounter==1) {

                currentColour = Color.parseColor("#ffb491");
                colourCounter++;
            }

            else if (colourCounter>1) {

                currentColour = Color.parseColor("#54b8a9");
                colourCounter = 0;
            }

            colourCode[i] = currentColour;
        }
    }

    Bitmap ShowPicture(File pictureFile) {

        Bitmap adjustedBitmap;

            // Get the dimensions of the View
            int targetW = 300;
            int targetH = 300;

// Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), bmOptions); // you can get imagePath from file
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

// Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

// Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            adjustedBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), bmOptions);

        return adjustedBitmap;
    }
        /*
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
        */


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

    public void Drawer(View view){

        Intent intent = new Intent(ExploreArchiveItem.this, HamburgerKidsUI.class);
        intent.putExtra("PreviousActivity", "ArchiveKidsUI");
        intent.putExtra("Authenticated", authenticated);
        ExploreArchiveItem.this.startActivity(intent);
        overridePendingTransition(R.anim.left_to_right_slide_in_activity, R.anim.left_to_right_slide_out_activity);
    }

    public void Back(View view) {

        onBackPressed();
    }

    public void AddStory(View view) {

        Intent intent = new Intent(ExploreArchiveItem.this, ObjectAddStoryKidsUI.class);
        intent.putExtra("PreviousActivity", "ExploreArchiveItem");
        intent.putExtra("ObjectStoryRecord", objectRecordMap);
        intent.putExtra("ObjectName", objectName);
        intent.putExtra("Authenticated", authenticated);
        ExploreArchiveItem.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    public void Home(View view) {

        Intent intent = new Intent(ExploreArchiveItem.this, LoggedInReadHomeKidsUI.class);
        intent.putExtra("PreviousActivity", "ExploreArchiveItem");
        intent.putExtra("Authenticated", authenticated);
        ExploreArchiveItem.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    //Activity Governance
    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();
    }

    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        authenticated = savedInstanceState.getBoolean("Authenticated");
        objectName = savedInstanceState.getString("ObjectName");
        objectRecordMap = (HashMap<String, ArrayList<ObjectStoryRecordKidsUI>>) savedInstanceState.getSerializable("ObjectStoryRecord");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putBoolean("Authenticated", authenticated);
        savedInstanceState.putString("ObjectName", objectName);
        savedInstanceState.putSerializable("ObjectStoryRecord", objectRecordMap);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {

        back.setClickable(false);
        animationBackHandler.removeCallbacksAndMessages(null);
        back.setImageDrawable(backRetrace);
        backRetrace.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ExploreArchiveItem.this, ArchiveKidsUI.class);
                intent.putExtra("PreviousActivity", "LoggedInWriteHomeKidsUI");
                intent.putExtra("Authenticated", authenticated);
                ExploreArchiveItem.this.startActivity(intent);
                overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
            }
        }, 1000);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class LocalFiles extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            LoadLocalFiles();

            return null;
        }

        protected void onPostExecute(Void result) {

            progressBar.setVisibility(View.INVISIBLE);
            cloudImageAdapter = new ExploreImageAdapterKidsUI(activity, context, fileMap.size(), fileMap, colourCode, objectRecordMap, storyCoverMap, storyTypeMap);
            gridview.invalidate();
            gridview.setAdapter(cloudImageAdapter);
        }
    }
}
