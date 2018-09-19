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

/**
 * Created by StuCollyn on 18/09/2018.
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
    UUID storyName;
    String fileType;
    String coverImage;

    SaveToCloud(File fileDirectory, UUID objectName) {

       this.fileDirectory = fileDirectory;
       this.objectName = objectName;
       storyName = UUID.randomUUID();

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
    void CloudSave() {

        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/"+fileDirectory.getName();
        File directory = new File(path);
        File[] files = directory.listFiles();

            for (int i = 0; i < files.length; i++) {

                UUID storyUUID = UUID.randomUUID();
                String extension = FilenameUtils.getExtension(files[i].toString());
                String fileName = files[i].toString();
                coverImage = "no";

                if (extension.equalsIgnoreCase("jpg")) {

                    Log.i("Uploading Picture", " true");
                    fileType = "PictureFile";
                    coverImage = "yes";
                } else if (extension.equalsIgnoreCase("mp3")) {

                    Log.i("Uploading Audio", " true");
                    fileType = "AudioFile";
                } else if (extension.equalsIgnoreCase("mp4")) {

                    Log.i("Uploading Video", " true");
                    fileType = "AudioFile";
                } else if (extension.equalsIgnoreCase("txt")) {

                    Log.i("Uploading Text", " true");
                    fileType = "WrittenFile";
                }

                uploadToCloud(files[i]);
            }
    }

    void uploadToCloud(File fileToUpload) {

        UploadTask uploadTask;
        Uri file = Uri.fromFile(fileToUpload);
        String userID = mAuth.getCurrentUser().getUid();
        String name = UUID.randomUUID().toString();
        StorageReference reference = mStorageRef.child(userID).child(name);
        uploadToDatabase(reference);
        uploadTask = reference.putFile(file);
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
                Log.i("Mission Accomplished", "Completed ");
                DeleteLocalFiles();

            }
        });
    }

    void uploadToDatabase(StorageReference reference) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getEmail();
        String storage = reference.toString();

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("Username", name);
        newUser.put("StoryName", storyName.toString());
        newUser.put("Type", fileType);
        newUser.put("URL", storage);
        newUser.put( "Date", FieldValue.serverTimestamp());
        newUser.put( "ObjectName", objectName.toString());
        newUser.put( "Cover", coverImage);


        db.collection("ObjectStory")
                .add(newUser)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("Success", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Failure", "Error adding document", e);
                    }
                });
    }

    void DeleteLocalFiles() {

        Log.i("File Directory", fileDirectory.toString());

        if (fileDirectory.isDirectory())
        {
            String[] children = fileDirectory.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(fileDirectory, children[i]).delete();
            }
        }

        boolean deletedFile = fileDirectory.delete();
    }

}
