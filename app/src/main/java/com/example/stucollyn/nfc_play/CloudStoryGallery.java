package com.example.stucollyn.nfc_play;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CloudStoryGallery extends AppCompatActivity {

    int mode;
    String queryType;
    private StorageReference mStorageRef;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    LinearLayout linearLayout;
    Context context;
    String storyName;
    EditText storySearchBar;
    ImageButton searchButton;
    HashMap<String, ArrayList<StoryRecord>> storyRecordMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        mode = (Integer) getIntent().getExtras().get("Orientation");
        queryType = (String) getIntent().getExtras().get("QueryType");
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
        storySearchBar = findViewById(R.id.search_bar);
        searchButton = (ImageButton) findViewById(R.id.search);
        context = this;
        queryFireStoreDatabase();
    }

    public void SearchByNameButton(View view) {

        orderByStoryName(storyRecordMap);
    }

    boolean RegionMatchesDemo {
    }

    void showAllStories(HashMap<String, ArrayList<StoryRecord>> storyRecordMap) {

        for (Map.Entry<String, ArrayList<StoryRecord>> entry : storyRecordMap.entrySet()) {

            String value = entry.getValue().get(0).getStoryName();

            Button valueTV = new Button(context);
            valueTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            //                                valueTV.setText(document.getId().toString() + " => " + document.getData().toString());
            //valueTV.setText("Story ID: " + document.getId().toString() + " = " +
            //     Html.fromHtml(linkedText)+"\n");
            valueTV.setText(Html.fromHtml(value));
            valueTV.setTextSize(30);
            valueTV.setClickable(true);
            valueTV.setMovementMethod(LinkMovementMethod.getInstance());
            linearLayout.addView(valueTV);

        }
    }

    void orderByStoryName(HashMap<String, ArrayList<StoryRecord>> storyRecordMap) {

        for (Map.Entry<String, ArrayList<StoryRecord>> entry : storyRecordMap.entrySet()) {

            String value = entry.getValue().get(0).getStoryName();

            if (storySearchBar.getText().toString().contains(value)) {


                Button valueTV = new Button(context);
                valueTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));
                valueTV.setText(Html.fromHtml(value));
                valueTV.setTextSize(30);
                valueTV.setClickable(true);
                valueTV.setMovementMethod(LinkMovementMethod.getInstance());
                linearLayout.addView(valueTV);
            }
        }
    }

    void orderByDate(HashMap<String, ArrayList<StoryRecord>> storyRecordMap) {

        for (Map.Entry<String, ArrayList<StoryRecord>> entry : storyRecordMap.entrySet()) {

            String value = entry.getValue().get(0).getStoryName();

                Button valueTV = new Button(context);
                valueTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));
                valueTV.setText(Html.fromHtml(value));
                valueTV.setTextSize(30);
                valueTV.setClickable(true);
                valueTV.setMovementMethod(LinkMovementMethod.getInstance());
                linearLayout.addView(valueTV);
        }

    }

    void queryFireStoreDatabase() {

//        final Multimap<String, StoryRecord> subStories = ArrayListMultimap.create();
//        final HashMap<String, String> storyRef = new HashMap<String, String>();
        storyRecordMap = new HashMap<String, ArrayList<StoryRecord>>();

        Log.i("Query: ", "Querying...");
        String userID = mAuth.getCurrentUser().getEmail();
        CollectionReference citiesRef = db.collection("Stories");
        Query query = citiesRef.whereEqualTo("Username", userID);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document: task.getResult()){
                                Log.i("Successful Query", document.getId() + " => " + document.getData());

//                                String StoryID = document.getId().toString();
                                String StoryID = document.getData().get("Story ID").toString();
                                String StoryName = document.getData().get("StoryName").toString();
                                String StoryDate = document.getData().get("Date").toString();
                                String URLlink = document.getData().get("URL").toString();
                                String linkedText = "<b>Story </b>" + StoryID + " = " +
                                String.format("<a href=\"%s\">Download Link</a> ", URLlink);

                                StoryRecord storyRecord = new StoryRecord(StoryID, StoryName, StoryDate, URLlink);

//                                subStories.put(StoryID, storyRecord);
//                                storyRef.put(StoryName, StoryID);

                                if(storyRecordMap.containsKey(StoryName)) {

                                    storyRecordMap.get(StoryName).add(storyRecord);
                                }

                                else {

                                    ArrayList<StoryRecord> storyRecordList = new ArrayList<StoryRecord>();
                                    storyRecordList.add(storyRecord);
                                    storyRecordMap.put(StoryName, storyRecordList);

                                }

                            }

                            if(queryType.equals("text")) {

                                    searchButton.setVisibility(View.VISIBLE);
                                    storySearchBar.setVisibility(View.VISIBLE);
                            }

                            else if(queryType.equals("image")) {

                            }

                            else if(queryType.equals("date")) {

                                // showAllStories();
                            }

                            for (Map.Entry<String,ArrayList<StoryRecord>> entry : storyRecordMap.entrySet()) {

                                String key = entry.getKey();
                                ArrayList<StoryRecord> value = entry.getValue();

                                Log.i("Test Map: ", key + ", " + value);

                                for(int i=0; i<value.size(); i++) {

                                    Log.i("Test Map Values: ", value.get(i).toString());

                                }
                            }
                        }


                        else {
                            Log.i("Failed", "error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
