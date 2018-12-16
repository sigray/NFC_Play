package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
This class is used to save a new series of story files to the cloud - FireBase database and FireStore cloud storage.
*/

public class SaveToCloud {

    boolean authenticated;
    File fileDirectory = null;
    String tag_data = "";
    String tag1String, tag2String, tag3String, object;
    int mode;
    private StorageReference mStorageRef;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    Date FireStoreTime;
    FirebaseStorage storage;
    boolean isNetworkConnected;
    HashMap<String,String> selectedMedia;
    UUID objectName;
    String fileType;
    String coverImage;

    SaveToCloud(File fileDirectory, UUID objectName) {

       this.fileDirectory = fileDirectory;
       this.objectName = objectName;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    //Save to cloud
    void CloudSaveNewStory() {

        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/validStoryFolders/Stories/"+fileDirectory.getName();
        File directory = new File(path);
        File[] files = directory.listFiles();

        for (int i = 0; i < files.length; i++) {

            String extension = FilenameUtils.getExtension(files[i].toString());
            String fileName = files[i].toString();
            coverImage = "no";

            if (extension.equalsIgnoreCase("jpg")) {

                Log.i("Uploading Audio", files[i].toString());
                fileType = "PictureFile";
//                    File to = new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/validStoryFolders/Stories/"+fileDirectory.getName(),".jpg");
//                    validStoryFolders[i].renameTo(to);
            }

            else if (extension.equalsIgnoreCase("mp3")) {

                Log.i("Uploading Audio", files[i].toString());
                fileType = "AudioFile";
            }

            uploadToCloud(files[i], i);
        }
    }

    //Save to cloud
    void CloudSaveNewObject() {

        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/validStoryFolders/Stories/"+fileDirectory.getName();
        File directory = new File(path);
        File[] files = directory.listFiles();

            for (int i = 0; i < files.length; i++) {

                String extension = FilenameUtils.getExtension(files[i].toString());
                String fileName = files[i].toString();
                coverImage = "no";

                if (extension.equalsIgnoreCase("jpg")) {

                    Log.i("Uploading Audio", files[i].toString());
                    fileType = "PictureFile";
                    coverImage = "yes";
//                    File to = new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/validStoryFolders/Stories/"+fileDirectory.getName(),".jpg");
//                    validStoryFolders[i].renameTo(to);
                }

                else if (extension.equalsIgnoreCase("mp3")) {

                    Log.i("Uploading Audio", files[i].toString());
                    fileType = "AudioFile";
                }

                uploadToCloud(files[i], i);
            }
    }

    void uploadToCloud(File fileToUpload, int i) {

        UploadTask uploadTask;
        Uri file = Uri.fromFile(fileToUpload);
        String number = String.valueOf(i);
        String userID = mAuth.getCurrentUser().getUid();
        StorageReference reference = mStorageRef.child(userID).child(objectName.toString()).child(number);
        uploadToDatabase(reference);
        uploadTask = reference.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Log.i("Mission Failed", "Failed ");
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.i("Mission Accomplished", "Completed ");

            }
        });
    }

    void uploadToDatabase(StorageReference reference) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getEmail();
        String storage = reference.toString();
        String storyName = UUID.randomUUID().toString();

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("Username", name);
        newUser.put("storyName", storyName.toString());
        newUser.put("Type", fileType);
        newUser.put("URL", storage);
        newUser.put( "Date", FieldValue.serverTimestamp());
        newUser.put( "objectName", objectName.toString());
        newUser.put( "Cover", coverImage);


        db.collection("ObjectStory")
                .add(newUser)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        Log.i("Success", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.i("Failure", "Error adding document", e);
                    }
                });
    }

    void DeleteLocalFiles(File fileToDelete) {

//        Log.i("File Directory", fileDirectory.toString());


        if (fileDirectory.isDirectory()) {
            String[] children = fileDirectory.list();

            if (children.length == 0) {

                fileDirectory.delete();
            }

            fileToDelete.delete();
        }
    }
}
