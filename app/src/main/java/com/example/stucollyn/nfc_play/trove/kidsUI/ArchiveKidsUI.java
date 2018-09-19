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
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.stucollyn.nfc_play.R;
import com.example.stucollyn.nfc_play.StoryRecord;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArchiveKidsUI extends AppCompatActivity {

    ArrayList<File> folders;
    LinkedHashMap<File, List<File>> folderFiles;
    HashMap<File, File> folderImages;
    HashMap<File, Bitmap> imageFiles;
    File[] files;
    ImageAdapterKidsUI imageAdapter;
    HorizontalGridView gridview;
    int colourCounter;
    int currentColour;
    int[] colourCode;
    int numberOfThumbs;
    Context context;
    Activity activity;
    ProgressBar progressBar;
    private StorageReference mStorageRef;
    ImageView back;
    AnimatedVectorDrawable backRetrace, backBegin;
    LinkedHashMap<String, ArrayList<ObjectStoryRecordKidsUI>> objectRecordMap;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Handler animationBackHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_kids_ui);
        gridview = (HorizontalGridView) findViewById(R.id.gridView);
        boolean authenticated = true;
        context = this;
        activity = this;
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
        folders = new ArrayList<File>();
        folderFiles = new LinkedHashMap<>();
        folderImages = new HashMap<File, File>();
        imageFiles = new HashMap<File, Bitmap>();
        AnimationSetup();
        CheckAuthentication(authenticated);
    }

    //Animation Setup
    void AnimationSetup() {

        back = (ImageView) findViewById(R.id.back);
        backBegin = (AnimatedVectorDrawable) getDrawable(R.drawable.kids_ui_back_anim);
        backRetrace = (AnimatedVectorDrawable) getDrawable(R.drawable.kids_ui_back_anim_retrace);
//                back.setImageDrawable(backRetrace);
//                backRetrace.start();


        animationBackHandler = new Handler();
        animationBackHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                back.setVisibility(View.VISIBLE);
                Drawable d = back.getDrawable();
                final AnimatedVectorDrawable zigzaganim = (AnimatedVectorDrawable) d;
                zigzaganim.start();
            }
        }, 2000);

        animationBackHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Drawable d = VectorDrawableCompat.create(getResources(), R.drawable.kids_ui_back, null);
                d = DrawableCompat.wrap(d);
                DrawableCompat.setTint(d, Color.WHITE);
                back.setImageDrawable(d);

            }
        }, 3000);
    }

    void ThumbnailColours() {

        colourCounter = 0;
        currentColour = Color.parseColor("#756bc7");;
        colourCode = new int[files.length];


        for (int i = 0; i < files.length; i++) {

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

    void CheckAuthentication(boolean authenticated) {

        if(authenticated) {

            CloudSetup();
        }

        else {

            LocalSetup();
        }

        new LoadImages().execute(authenticated);
    }

    void CloudSetup() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    void LocalSetup() {

        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/";
        File directory = new File(path);
        files = directory.listFiles();
    }

    void queryFireStoreDatabase() {

        objectRecordMap = new LinkedHashMap<String, ArrayList<ObjectStoryRecordKidsUI>>();

        String userID = mAuth.getCurrentUser().getEmail();
        CollectionReference citiesRef = db.collection("ObjectStory");
        Query query = citiesRef.whereEqualTo("Username", userID).orderBy("Date", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.i("Successful Query", document.getId() + " => " + document.getData());

//                                String StoryID = document.getId().toString();
                                String ObjectName = document.getData().get("ObjectName").toString();
                                String StoryName = document.getData().get("StoryName").toString();
                                String StoryDate = document.getData().get("Date").toString();
                                String URLlink = document.getData().get("URL").toString();
                                String StoryType = document.getData().get("Type").toString();
                                String CoverImage = document.getData().get("Cover").toString();
                                String linkedText = "<b>Story </b>" + ObjectName + " = " +
                                String.format("<a href=\"%s\">Download Link</a> ", URLlink);

                                //Create new ObjectStory Record. For a returned query record this creates an object with all its defining attributes
                                ObjectStoryRecordKidsUI objectStoryRecord = new ObjectStoryRecordKidsUI(ObjectName, StoryName, StoryDate, URLlink, StoryType, CoverImage);

                                /*In the following segment of code we create a linked list of all the ObjectStoryRecord objects, called objectRecordMap.
                                objectRecordMap's keys are the UUID of each object in the database, the values are all the ObjectStoryRecords, relating to
                                those keys. If a database query record's object UUID already exists within the objectRecordMap, the query record's ObjectStoryRecord
                                object is added as a value for that key. If not, a new entry is made in objectRecordMap, with a new key (the UUID of the object) and
                                the value to the ArrayList of stories pertaining to that key - the value (the ObjectStoryRecord object)*/
                                if (objectRecordMap.containsKey(ObjectName)) {

                                    objectRecordMap.get(ObjectName).add(objectStoryRecord);
                                }

                                else {

                                    ArrayList<ObjectStoryRecordKidsUI> objectStoryRecordObjectList = new ArrayList<ObjectStoryRecordKidsUI>();
                                    objectStoryRecordObjectList.add(objectStoryRecord);
                                    objectRecordMap.put(ObjectName, objectStoryRecordObjectList);
                                    Log.i("Adding to Test Map: ", ObjectName + " " + StoryName);
                                }

                            }
//                                showAllStories(objectRecordMap);
                                getCoverImages(objectRecordMap);


                            for (Map.Entry<String, ArrayList<ObjectStoryRecordKidsUI>> entry : objectRecordMap.entrySet()) {

                                String key = entry.getKey();
                                ArrayList<ObjectStoryRecordKidsUI> value = entry.getValue();

                                Log.i("Key: Value ", key + ": " + value);
                            }

                        }

                        else {
                            Log.i("Failed", "error getting documents: ", task.getException());
                        }
                    }
                });
    }

    void showAllStories(LinkedHashMap<String, ArrayList<ObjectStoryRecordKidsUI>> objectRecordMap) {
        ArrayList<String> mediaItems = new ArrayList<String>();
//        fullList = mediaItems;

        for (Map.Entry<String, ArrayList<ObjectStoryRecordKidsUI>> entry : objectRecordMap.entrySet()) {

            String value = entry.getValue().get(0).getStoryName();
            mediaItems.add(value);
        }
//        setupImageAdapter(objectRecordMap, mediaItems);

    }

    void getCoverImages(LinkedHashMap<String, ArrayList<ObjectStoryRecordKidsUI>> objectRecordMap) {

            String newDirectory;
            StorageReference gsReference;
            FirebaseStorage storage;
            String userID;
            File story_directory;
            newDirectory = ("/Cloud");
            storage = FirebaseStorage.getInstance();
            story_directory = getExternalFilesDir(newDirectory);
            userID = mAuth.getCurrentUser().getUid();
            final ArrayList<File> coverImages = new ArrayList<File>();


            for (Map.Entry<String, ArrayList<ObjectStoryRecordKidsUI>> entry : objectRecordMap.entrySet()) {


                for (int i = 0; i < objectRecordMap.size(); i++) {
                    gsReference = storage.getReferenceFromUrl(entry.getValue().get(i).getStoryRef());

                    try {

                        final File pictureFile = File.createTempFile("images", ".jpg", story_directory);

                        gsReference.getFile(pictureFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                Log.i("Success: ", "Found you stuff");

                                // Local temp file has been created

                                coverImages.add(pictureFile);
                                Log.i("Picture Name: ", pictureFile.toString());
                                //ShowPicture();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    }

                    catch (IOException error) {

                    }
                }
            }
    }

    public void setupLists(File[] files) {

        for (int i = 0; i < files.length; i++) {

            folders.add(files[i]);
            File[] subFiles = FilesForThumbnail(files[i]);

//            Log.i("Reached 1: File: ", files[i].getName());

            for (int j = 0; j < subFiles.length; j++) {

                put(folderFiles, files[i], subFiles[j]);

//                Log.i("Reached 2: File: Sub", files[i].getName() + ": " + subFiles[j].getName());
            }
        }

        for (Map.Entry<File, List<File>> entry : folderFiles.entrySet()) {
            File key = entry.getKey();
            List<File> value = entry.getValue();

//            Log.i("Reached 3: File: Sub", key.getName() + ": " + value.toString());

            int loadNum = 0;

            for(File element : value){

                String extension = FilenameUtils.getExtension(element.toString());
                String fileName = element.toString();

                if (extension.equalsIgnoreCase("jpg")) {

                    folderImages.put(key, element);
                    loadNum++;
                    int progressUpdate = (loadNum*100)/value.size();
                    progressBar.setProgress(progressUpdate);

                    Bitmap test = ShowPicture(element);
//                   Log.i("Test element", test.toString());

                    imageFiles.put(key, ShowPicture(element));

                }
            }

        }

    }

    public static void put(Map<File, List<File>> map, File key, File value) {
        if(map.get(key) == null){
            map.put(key, new ArrayList<File>());
        }
        map.get(key).add(value);
    }

    File[] FilesForThumbnail(File file) {

        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/"+file.getName();
        File directory = new File(path);
        File[] files = directory.listFiles();

        return files;
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

    public void Back(View view) {

        onBackPressed();
    }

    @Override
    public void onBackPressed() {

        animationBackHandler.removeCallbacksAndMessages(null);
        back.setImageDrawable(backRetrace);
        backRetrace.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ArchiveKidsUI.this, LoggedInWriteHomeKidsUI.class);
                intent.putExtra("PreviousActivity", "LoggedInWriteHomeKidsUI");
                ArchiveKidsUI.this.startActivity(intent);
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

    class LoadImages extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... params) {

            boolean authenticated = params[0];

            try {

                if(authenticated) {

                    queryFireStoreDatabase();
                }

                else {

                    setupLists(files);
                }

            ThumbnailColours();

            } catch (NullPointerException e) {

            } catch (IllegalArgumentException e) {

            }

            return null;

        }

        protected void onPostExecute(Boolean authenticated) {

            progressBar.setVisibility(View.INVISIBLE);

            if(authenticated) {

            }

            else {

                imageAdapter = new ImageAdapterKidsUI(activity, context, numberOfThumbs, folders, colourCode, folderImages, imageFiles);
            }

            imageAdapter = new ImageAdapterKidsUI(activity, context, numberOfThumbs, folders, colourCode, folderImages, imageFiles);
            gridview.invalidate();
            gridview.setAdapter(imageAdapter);

        }
    }
}
