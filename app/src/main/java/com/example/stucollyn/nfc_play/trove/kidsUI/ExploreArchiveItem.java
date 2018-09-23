package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ExploreArchiveItem extends AppCompatActivity {

    LinkedHashMap<String, ArrayList<ObjectStoryRecordKidsUI>> objectRecordMap;
    LinkedHashMap<String, File> fileMap;
    LinkedHashMap<String, Bitmap> storyCoverMap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_kids_ui);
        gridview = (HorizontalGridView) findViewById(R.id.gridView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        storyCoverMap = new LinkedHashMap<String, Bitmap>();
        objectRecordMap = (LinkedHashMap<String, ArrayList<ObjectStoryRecordKidsUI>>) getIntent().getExtras().get("ObjectRecordMap");
        objectName = (String) getIntent().getExtras().get("ObjectName");
        activity = this;
        context = this;
        authenticated = false;
    }

    void DownloadFromCloud(final String StoryName, String URLLink) {

        try {

            StorageReference gsReference;
            FirebaseStorage storage;
            storage = FirebaseStorage.getInstance();
            gsReference = storage.getReferenceFromUrl(URLLink);
            File story_directory;
            String newDirectory = ("/Cloud");
            story_directory = getExternalFilesDir(newDirectory);
            final File file = File.createTempFile("text", ".txt", story_directory);

            gsReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    Log.i("Success: ", "Found you stuff");

                    fileMap.put(StoryName, file);

//                    CloudThumbnailColours();
//                    progressBar.setVisibility(View.INVISIBLE);
//                    cloudImageAdapter = new ExploreImageAdapterKidsUI(activity, context, fileMap.size(), fileMap, colourCode, objectRecordMap, storyCoverMap);
//                    gridview.invalidate();
//                    gridview.setAdapter(cloudImageAdapter);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

        } catch (IOException error) {

        }
    }

    void LoadFiles() {

        ArrayList<ObjectStoryRecordKidsUI> objectFiles = new ArrayList<ObjectStoryRecordKidsUI>();
        objectFiles = objectRecordMap.get("objectName");

        for(int i=0; i<objectFiles.size(); i++) {

            if(objectFiles.get(i).getObjectContext().equals("Cloud")) {

                DownloadFromCloud(objectFiles.get(i).getStoryRef());
            }

            else if(objectFiles.get(i).getObjectContext().equals("Local")) {

                new LocalFiles().execute();
            }
        }
    }

    void LoadLocalFiles(){

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

    class LocalFiles extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            LoadLocalFiles();
            CloudThumbnailColours();

            return null;
        }

        protected void onPostExecute() {

            progressBar.setVisibility(View.INVISIBLE);
            cloudImageAdapter = new ExploreImageAdapterKidsUI(activity, context, fileMap.size(), fileMap, colourCode, objectRecordMap, storyCoverMap);
            gridview.invalidate();
            gridview.setAdapter(cloudImageAdapter);
        }
    }
}
