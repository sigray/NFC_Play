package com.example.stucollyn.nfc_play;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SaveSelector extends AppCompatActivity {

    File fileDirectory = null;
    String tag_data = "";
    int mode;
    private StorageReference mStorageRef;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_selector);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mode = (Integer) getIntent().getExtras().get("Orientation");
        setRequestedOrientation(mode);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Save New Story");
        fileDirectory = (File)getIntent().getExtras().get("StoryDirectory");
        tag_data = (String)getIntent().getExtras().get("TagData");
    }

    public void StartConfirmation(View view){

        ShowMedia();
        Intent intent = new Intent(SaveSelector.this, SavedStoryConfirmation.class);
        intent.putExtra("Orientation", mode);
        SaveSelector.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    public void StartSaveStoryToNFC(View view) {

        Intent intent = new Intent(SaveSelector.this, SaveStoryToNFC.class);
        intent.putExtra("TagData", tag_data);
        intent.putExtra("Orientation", mode);
        SaveSelector.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SaveSelector.this, NewStoryReview.class);
        intent.putExtra("Orientation", mode);
        SaveSelector.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    public void ShowMedia() {

        Log.i("File to upload: ", fileDirectory.toString());

        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/"+fileDirectory.getName();
        UUID storyUUID = UUID.randomUUID();
        String fileType = "";
        File directory = new File(path);
        File[] files = directory.listFiles();

//        WifiManager wifi = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        if (wifi.isWifiEnabled()){
//

            for(int i = 0; i<files.length; i++) {

                String extension = FilenameUtils.getExtension(files[i].toString());
                String fileName = files[i].toString();

                if (extension.equalsIgnoreCase("jpg")) {

                    fileType = "PictureFile";
                }

                if (extension.equalsIgnoreCase("mp3")) {

                    fileType = "AudioFile";
                }

                if (extension.equalsIgnoreCase("mp4")) {

                    fileType = "VideoFile";
                }

                if (extension.equalsIgnoreCase("txt")) {

                    fileType = "WrittenFile";
                }

                uploadToCloud(files[i], storyUUID, fileType);
            }

        //}

    }

    void uploadToCloud(File fileToUpload, final UUID storyUUID, final String fileType) {

        UploadTask uploadTask;
        Uri file = Uri.fromFile(fileToUpload);
        String userID = mAuth.getCurrentUser().getUid();
        Log.i("User ID", userID);
        final StorageReference riversRef = mStorageRef.child(userID).child(fileToUpload.toString());

        uploadTask = riversRef.putFile(file);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.i("Download Link", downloadUri.toString());
                    uploadToDatabase(downloadUri, storyUUID, fileType);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    void uploadToDatabase(Uri downloadURI, UUID storyUUID, String fileType) {

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String name = user.getDisplayName();

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("Username", "test");
        newUser.put("Story ID", storyUUID.toString());
        newUser.put("Type", fileType);
        newUser.put("URL", downloadURI.toString());

        db.collection("Stories")
                .add(newUser)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Success", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failure", "Error adding document", e);
                    }
                });


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
}
