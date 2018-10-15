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
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.stucollyn.nfc_play.R;
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

    LinkedHashMap<String, ArrayList<File>> folderFiles;
    HashMap<File, File> folderImages;
    HashMap<File, Bitmap> imageFiles;
    File[] files, coverFiles;
    ImageAdapterKidsUI imageAdapter;
    CloudImageAdapterKidsUI cloudImageAdapter;
    HorizontalGridView gridview;
    int colourCounter;
    int currentColour;
    int[] colourCode;
    int numberOfThumbs;
    Context context;
    Activity activity;
    ProgressBar progressBar;
    ImageView back;
    AnimatedVectorDrawable backRetrace, backBegin;
    LinkedHashMap<String, ArrayList<ObjectStoryRecordKidsUI>> objectRecordMap;
    private StorageReference mStorageRef;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Handler animationBackHandler;
    ArrayList<File> coverImages;
    HashMap<String, Bitmap> coverImageMap;
    int numberOfDownloads = 0;
    int listSize=0;
    boolean authenticated = false;
    CommentaryInstruction commentaryInstruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_kids_ui);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        gridview = (HorizontalGridView) findViewById(R.id.gridView);
        authenticated = (Boolean) getIntent().getExtras().get("Authenticated");
        context = this;
        activity = this;
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
        folderFiles = new LinkedHashMap<String, ArrayList<File>>();
        folderImages = new HashMap<File, File>();
        imageFiles = new HashMap<File, Bitmap>();
        coverImageMap = new HashMap<String, Bitmap>();
        commentaryInstruction = new CommentaryInstruction(this, this, false, authenticated);
        commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.archive), false, LoggedInWriteHomeKidsUI.class, "LoggedInWriteHomeKidsUI");
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

    void LocalThumbnailColours() {

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

    void CloudThumbnailColours() {

        colourCounter = 0;
        currentColour = Color.parseColor("#756bc7");;
        colourCode = new int[coverImageMap.size()];


        for (int i = 0; i < coverImageMap.size(); i++) {

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


           queryFireStoreDatabase();
        }

        else {

            LocalSetup();
            new LoadLocalImages().execute(authenticated);
        }
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

//        String StoryFilesPath = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/";
//        String CoverFilesPath = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Covers/";
//        File directory = new File(StoryFilesPath);
//        File coverDirectory = new File(CoverFilesPath);
//        files = directory.listFiles();
//        coverFiles = coverDirectory.listFiles();


        File directory = new File(getFilesDir() + File.separator + "Stories");
        File coverDirectory = new File(getFilesDir() + File.separator + "Covers");
        files = directory.listFiles();
        coverFiles = coverDirectory.listFiles();

//        File[] files = directory.listFiles();
//        Log.d("Files", "Size: "+ files.length);
//        for (int i = 0; i < files.length; i++)
//        {
//            Log.d("Files", "FileName:" + files[i].getName());
//
//            File subFile = new File(getFilesDir() + File.separator + "Stories" + File.separator + files[i].getName());
//            File[] subFiles = subFile.listFiles();
//
//            for (int j = 0; j < subFiles.length; j++)
//            {
//                Log.d("SubFiles", "FileName:" + subFiles[j].getName());
//            }
//
//        }
    }

    void queryFireStoreDatabase() {

        objectRecordMap = new LinkedHashMap<String, ArrayList<ObjectStoryRecordKidsUI>>();

        String userID = mAuth.getCurrentUser().getEmail();
        CollectionReference citiesRef = db.collection("ObjectStory");
        Query query = citiesRef.whereEqualTo("Username", userID).orderBy("Date", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.i("Successful Query", document.getId() + " => " + document.getData());

                                String StoryDate = ""; // Error with server data currently
                                String ObjectName = document.getData().get("ObjectName").toString();
                                String StoryName = document.getData().get("StoryName").toString();
//                                String StoryDate = document.getData().get("Date").toString();
                                String URLlink = document.getData().get("URL").toString();
                                String StoryType = document.getData().get("Type").toString();
                                String CoverImage = document.getData().get("Cover").toString();
                                String linkedText = "<b>Story </b>" + ObjectName + " = " +
                                String.format("<a href=\"%s\">Download Link</a> ", URLlink);

                                //Create new ObjectStory Record. For a returned query record this creates an object with all its defining attributes
                                ObjectStoryRecordKidsUI objectStoryRecord = new ObjectStoryRecordKidsUI(ObjectName, StoryName, StoryDate, URLlink, StoryType, CoverImage, "Cloud");

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
                                }

                                if(CoverImage.equals("yes")) {

                                    getCoverImageCloud(ObjectName, URLlink);

                                }
                            }
                        }

                        else {

                            Log.i("Oops", "Oh No!");
                        }
                    }
                });

//        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot documentSnapshots) {
//
//                Log.i("Triggered", "Hello!" + String.valueOf(documentSnapshots.size()));
//                Log.i("ObjectRecordMap", objectRecordMap.toString());
//                Log.i("numberOfDownloads",  String.valueOf(numberOfDownloads));
//
//                listSize = documentSnapshots.size();

                /*for (Map.Entry<String, ArrayList<ObjectStoryRecordKidsUI>> entry : objectRecordMap.entrySet()) {

                    String key = entry.getKey();
                    ArrayList<ObjectStoryRecordKidsUI> value = entry.getValue();

                    for(int i=0; i<value.size(); i++) {

                        Log.i("Let's Go", String.valueOf(i));

                        if(value.get(i).isCoverImage().equals("yes")) {

                            getCoverImageCloud(key, value.get(i).getStoryRef(), listSize);
//                        }
//                    }
//                }
//                */
//            }
//    });

//            // Get the last visible document
//            DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
//                    .get(documentSnapshots.size() -1);



    }

    void getCoverImageCloud(String ObjectName, String URLlink) {

        String newDirectory;
        StorageReference gsReference;
        FirebaseStorage storage;
        String userID;
        File story_directory;
        newDirectory = ("/Cloud");
        storage = FirebaseStorage.getInstance();
//        story_directory = getExternalFilesDir(newDirectory);
        story_directory = new File(getFilesDir() + File.separator + "Cloud");

        userID = mAuth.getCurrentUser().getUid();
        final Bitmap adjustedBitmap;
        gsReference = storage.getReferenceFromUrl(URLlink);

            try {

                final File pictureFile = File.createTempFile("images", ".jpg", story_directory);
                final String URL = URLlink;
                final String theObjectName = ObjectName;
//                Log.i("CoverImage yes: ", ObjectName + " " + URLlink);


                gsReference.getFile(pictureFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        // Local temp file has been created
                        Bitmap adjustedBitmap = ShowPicture(pictureFile);
                        coverImageMap.put(theObjectName, adjustedBitmap);
                        numberOfDownloads++;

                        Log.i("numberOfDownloads B",  String.valueOf(numberOfDownloads));
                        Log.i("listSize B",  String.valueOf(listSize));

                        /*Set the image adapter only after the last query has been executed and cover image loaded */
                      //  if(numberOfDownloads==listSize) {

                            CloudThumbnailColours();
                            progressBar.setVisibility(View.INVISIBLE);
                            cloudImageAdapter = new CloudImageAdapterKidsUI(activity, context, numberOfThumbs, folderFiles, colourCode, objectRecordMap, coverImageMap, authenticated, commentaryInstruction);
                            gridview.invalidate();
                            gridview.setAdapter(cloudImageAdapter);
                       // }

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

    void setupListsLocal(File[] files, File[] coverFiles) {

        objectRecordMap = new LinkedHashMap<String, ArrayList<ObjectStoryRecordKidsUI>>();

        for (int i = 0; i < files.length; i++) {

            File[] subFiles = FilesForThumbnail(files[i]);
            boolean setCoverImage = false;

            for (int j = 0; j < subFiles.length; j++) {

                String CoverImage = "no";
                String FileContext = "local";
                String FileType = "";
                String extension = FilenameUtils.getExtension(subFiles[j].toString());

                    if (extension.equalsIgnoreCase("jpg")&&!setCoverImage) {

//                        CoverImage = "yes";
                        FileType = "PictureFile";
                    }

                    if(extension.equalsIgnoreCase("jpg")) {

                        FileType = "PictureFile";
                    }

                    else if(extension.equalsIgnoreCase("mp3")) {

                        FileType = "AudioFile";
                    }

                ObjectStoryRecordKidsUI objectStoryRecord = new ObjectStoryRecordKidsUI(files[i].getName(), subFiles[j].getName(), "", subFiles[j].getAbsolutePath(), FileType, CoverImage, FileContext);

                if (objectRecordMap.containsKey(files[i].getName())) {

                    objectRecordMap.get(files[i].getName()).add(objectStoryRecord);
                }

                else {

                    ArrayList<ObjectStoryRecordKidsUI> objectStoryRecordObjectList = new ArrayList<ObjectStoryRecordKidsUI>();
                    objectStoryRecordObjectList.add(objectStoryRecord);
                    objectRecordMap.put(files[i].getName(), objectStoryRecordObjectList);
                }
            }
        }

        for (int i = 0; i < coverFiles.length; i++) {

            //For the number of files in the Cover folder, return each file in turn and add it to
            File thumbnailsFile = Thumbnail(coverFiles[i]);
            getCoverImageLocal(coverFiles[i].getName(), thumbnailsFile);
            Log.i("Cover Files Name: ", coverFiles[i].getName() + " , File: " + coverFiles[i]);
            Log.i("Cover Image Map: ", coverImageMap.toString());
        }
    }

    void getCoverImageLocal(String ObjectName, File file) {

            final String theObjectName = ObjectName;
            Bitmap adjustedBitmap = ShowPicture(file);

        if (objectRecordMap.containsKey(ObjectName)) {
            coverImageMap.put(theObjectName, adjustedBitmap);
            CloudThumbnailColours();
        }
    }

    /*
    public void setupLists(File[] files) {

        for (int i = 0; i < files.length; i++) {

            folders.add(files[i]);
            File[] subFiles = FilesForThumbnail(files[i]);

           Log.i("Reached 1: File: ", files[i].getName());

            for (int j = 0; j < subFiles.length; j++) {

                put(folderFiles, files[i], subFiles[j]);

                Log.i("Reached 2: File: Sub", files[i].getName() + ": " + subFiles[j].getName());
            }
        }

        for (Map.Entry<File, List<File>> entry : folderFiles.entrySet()) {
            File key = entry.getKey();
            List<File> value = entry.getValue();

            Log.i("Reached 3: File: Sub", key.getName() + ": " + value.toString());

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
                    Log.i("Test element", test.toString());

                    imageFiles.put(key, ShowPicture(element));
                }
            }
        }
    }

    */

    public static void put(Map<File, List<File>> map, File key, File value) {
        if(map.get(key) == null){
            map.put(key, new ArrayList<File>());
        }
        map.get(key).add(value);
    }

    File[] FilesForThumbnail(File file) {

//        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/"+file.getName();
//        File directory = new File(path);
        File directory = new File (getFilesDir() + File.separator + "Stories" + File.separator + file.getName());
        File[] files = directory.listFiles();

        return files;
    }

    File Thumbnail(File file) {

//        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Covers/"+file.getName();
//        File directory = new File(path);
        File directory = new File (getFilesDir() + File.separator + "Covers" + File.separator + file.getName());
        File[] covers = directory.listFiles();
        File thumbnail = covers[0];

        return thumbnail;
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
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

// Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), bmOptions);

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

    public void Drawer(View view){

        commentaryInstruction.stopPlaying();
        Intent intent = new Intent(ArchiveKidsUI.this, HamburgerKidsUI.class);
        intent.putExtra("PreviousActivity", "ArchiveKidsUI");
        intent.putExtra("Authenticated", authenticated);
        ArchiveKidsUI.this.startActivity(intent);
        overridePendingTransition(R.anim.left_to_right_slide_in_activity, R.anim.left_to_right_slide_out_activity);
    }

    public void Back(View view) {

        onBackPressed();
    }

    public void Home(View view) {

        commentaryInstruction.stopPlaying();
        Intent intent = new Intent(ArchiveKidsUI.this, LoggedInReadHomeKidsUI.class);
        intent.putExtra("PreviousActivity", "ArchiveKidsUI");
        intent.putExtra("Authenticated", authenticated);
        ArchiveKidsUI.this.startActivity(intent);
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
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putBoolean("Authenticated", authenticated);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {

        commentaryInstruction.stopPlaying();
        back.setClickable(false);
        animationBackHandler.removeCallbacksAndMessages(null);
        back.setImageDrawable(backRetrace);
        backRetrace.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ArchiveKidsUI.this, LoggedInReadHomeKidsUI.class);
                intent.putExtra("PreviousActivity", "ArchiveKidsUI");
                intent.putExtra("Authenticated", authenticated);
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

    class LoadLocalImages extends AsyncTask<Boolean, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... params) {

            boolean authenticated = params[0];
            boolean success = false;


            if(files!=null) {
                setupListsLocal(files, coverFiles);
                CloudThumbnailColours();
                success = true;
            }

            return success;
        }

        protected void onPostExecute(Boolean success) {

            progressBar.setVisibility(View.INVISIBLE);

            if(success) {
                cloudImageAdapter = new CloudImageAdapterKidsUI(activity, context, numberOfThumbs, folderFiles, colourCode, objectRecordMap, coverImageMap, authenticated, commentaryInstruction);
                gridview.invalidate();
                gridview.setAdapter(cloudImageAdapter);
            }
        }
    }
}
