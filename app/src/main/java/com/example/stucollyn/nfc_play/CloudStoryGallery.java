package com.example.stucollyn.nfc_play;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URL;

public class CloudStoryGallery extends AppCompatActivity {

    int mode;
    private StorageReference mStorageRef;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    LinearLayout linearLayout;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        mode = (Integer) getIntent().getExtras().get("Orientation");
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_cloud_story_gallery);
        linearLayout = findViewById(R.id.test_linear);
        context = this;
        queryFireStoreDatabase();
    }

    void queryFireStoreDatabase() {


        CollectionReference citiesRef = db.collection("Stories");
        Query query = citiesRef.whereEqualTo("Username", "test");
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document: task.getResult()){
                                Log.i("Successful Query", document.getId() + " => " + document.getData());

                                String StoryID = document.getId().toString();
                                String URLlink = document.getData().get("URL").toString();
                                String linkedText = "<b>Story </b>" + StoryID + " = " +
                                        String.format("<a href=\"%s\">Download Link</a> ", URLlink);

                                TextView valueTV = new TextView(context);
                                valueTV.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                        LayoutParams.WRAP_CONTENT));
//                                valueTV.setText(document.getId().toString() + " => " + document.getData().toString());
                                //valueTV.setText("Story ID: " + document.getId().toString() + " = " +
                                   //     Html.fromHtml(linkedText)+"\n");
                                valueTV.setText(Html.fromHtml(linkedText));
                                valueTV.setClickable(true);
                                valueTV.setMovementMethod(LinkMovementMethod.getInstance());
                                linearLayout.addView(valueTV);
                            }
                        } else {
                            Log.i("Failed", "error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
