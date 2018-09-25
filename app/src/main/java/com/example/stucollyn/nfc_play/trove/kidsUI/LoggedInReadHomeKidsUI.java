package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Environment;
import android.os.Handler;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.stucollyn.nfc_play.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LoggedInReadHomeKidsUI extends FragmentActivity {

    ImageView backgroundShapes, zigzag1, zigzag2, zigzag3, zigzag4, star, moon, shell, book, key,
            leaf, umbrella, tear, teddy, heart, trove, back, halfcircle;
    Handler startupZigZagHandler, startupLargeObjectsHandler;
    Animation spin, shrink, blink, draw, bounce, fadein, alpha, fadeout;
    ImageView zigzagArray[], largeItemArray[], allViews[];
    Integer[] paintColourArray;
    int paintColourArrayInt = 0;
    HashMap<ImageView, Integer> IvDrawable;
    ViewGroup mRootView;
    AnimatedVectorDrawable backRetrace;
    NFCInteraction nfcInteraction;
    Tag mytag;
    boolean newStoryReady = false;
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter readTagFilters[];
    private MediaPlayer mPlayer = null;
    ShowStoryContent showStoryContent;
    boolean authenticated = false;
    boolean record_button_on, video_record_button_on, recordingStatus = false,
            playbackStatus = false, mPlayerSetup = false, fullSizedPicture = false,
            permissionToRecordAccepted = false, isFullSizedVideo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_read_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRootView = (ViewGroup) findViewById(R.id.activity_logged_in_read_home);

        mPlayer = new MediaPlayer();

        paintColourArray = new Integer[4];
        paintColourArray[0] = android.graphics.Color.rgb(255, 157, 0);
        paintColourArray[1] = android.graphics.Color.rgb(253, 195, 204);
        paintColourArray[2] = android.graphics.Color.rgb(0, 235, 205);
        paintColourArray[3] = Color.WHITE;

        backgroundShapes = (ImageView) findViewById(R.id.small_shapes);
        zigzag1 = (ImageView) findViewById(R.id.zigzag_1);
        zigzag2 = (ImageView) findViewById(R.id.zigzag_2);
        zigzag3 = (ImageView) findViewById(R.id.zigzag_3);
        zigzag4 = (ImageView) findViewById(R.id.zigzag_4);
        star = (ImageView) findViewById(R.id.star);
        moon = (ImageView) findViewById(R.id.moon);
        shell = (ImageView) findViewById(R.id.shell);
        book = (ImageView) findViewById(R.id.book);
        key = (ImageView) findViewById(R.id.key);
        leaf = (ImageView) findViewById(R.id.leaf);
        umbrella = (ImageView) findViewById(R.id.umbrella);
        tear = (ImageView) findViewById(R.id.tear);
        teddy = (ImageView) findViewById(R.id.teddy);
        heart = (ImageView) findViewById(R.id.heart);
        halfcircle = (ImageView) findViewById(R.id.circle);
        trove = (ImageView) findViewById(R.id.trove);
        back = (ImageView) findViewById(R.id.back);
        backRetrace = (AnimatedVectorDrawable) getDrawable(R.drawable.kids_ui_back_anim_retrace);

        IvDrawable = new HashMap<ImageView, Integer>();
        IvDrawable.put(star, R.drawable.kids_ui_star);
        IvDrawable.put(moon, R.drawable.kids_ui_moon);
        IvDrawable.put(shell, R.drawable.kids_ui_shell);
        IvDrawable.put(book, R.drawable.kids_ui_book);
        IvDrawable.put(key, R.drawable.kids_ui_key);
        IvDrawable.put(leaf, R.drawable.kids_ui_leaf);
        IvDrawable.put(umbrella, R.drawable.kids_ui_umbrella);
        IvDrawable.put(tear, R.drawable.kids_ui_tear);
        IvDrawable.put(teddy, R.drawable.kids_ui_teddy);
        IvDrawable.put(heart,  R.drawable.kids_ui_heart);
        IvDrawable.put(halfcircle,  R.drawable.kids_ui_halfcircle);
        IvDrawable.put(zigzag1,  R.drawable.kids_ui_zigzag);
        IvDrawable.put(zigzag2,  R.drawable.kids_ui_zigzag);
        IvDrawable.put(zigzag3,  R.drawable.kids_ui_zigzag);
        IvDrawable.put(zigzag4,  R.drawable.kids_ui_zigzag);
        IvDrawable.put(back,  R.drawable.kids_ui_back);
        IvDrawable.put(trove,  R.drawable.kids_ui_trove);

        allViews = new ImageView[17];
        allViews[0] = zigzag2;
        allViews[1] = zigzag1;
        allViews[2] = zigzag4;
        allViews[3] = zigzag3;
        allViews[4] = star;
        allViews[5] = halfcircle;
        allViews[6] = leaf;
        allViews[7] = moon;
        allViews[8] = book;
        allViews[9] = key;
        allViews[10] = shell;
        allViews[11] = teddy;
        allViews[12] = tear;
        allViews[13] = umbrella;
        allViews[14] = heart;
        allViews[15] = back;
        allViews[16] = trove;

        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        alpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        String previousActivity = (String) getIntent().getExtras().get("PreviousActivity");
        authenticated = (Boolean) getIntent().getExtras().get("Authenticated");
        paintViews();
        animateViews();
        Autoplay(previousActivity);

        nfcInteraction = new NFCInteraction(this, this);
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        readTagFilters = new IntentFilter[] {tagDetected };
    }

    void Autoplay(String previousActivity){

        if(previousActivity.equals("LoginKidsUI")) {

            Uri audioFileUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.welcome_app);
            onPlay(audioFileUri);
        }

        else if(previousActivity.equals("LoggedInWriteHomeKidsUI")) {

            boolean newStory = (Boolean) getIntent().getExtras().get("NewStory");

            if(newStory) {

                String storyRef = (String) getIntent().getExtras().get("StoryRef");
                String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/" + storyRef;
                File directory = new File(path);
                File[] filesOnTag = directory.listFiles();
                PlayStory(filesOnTag);
            }
        }

        else {

        }
    }

    /*When audio playback buttons are selected for first time, setup new audio media player. When
   user interacts with playback buttons after audio media player has already been setup, toggle
   between media player pause and play*/
    public void onPlay(Uri audioFileUri) {

        setupAudioMediaPlayer(audioFileUri);
        if (!playbackStatus) {
            startPlaying();
            playbackStatus = true;
        }
    }

    //Setup new audio media player drawing from audio file location
    protected void setupAudioMediaPlayer(Uri audioFileUri) {

        try {
            mPlayer.setDataSource(this, audioFileUri);
            mPlayer.prepare();
            mPlayerSetup = true;
        } catch (IOException e) {
            Log.e("Error", "prepare() failed");
        }
    }

    //Start audio media player and start listening for stop imageView to be pressed
    public void startPlaying() {
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {

                mPlayer.stop();
                mPlayer.reset();
                playbackStatus = false;
            }
        });
    }

    void PlayStory(File[] filesOnTag) {


        Toast.makeText(this, "Test Tag Content", Toast.LENGTH_LONG ).show();
        ShowStoryContent showStoryContent = new ShowStoryContent(mPlayer, this, this, filesOnTag);
        showStoryContent.checkFilesOnTag();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, "Tag Discovered", Toast.LENGTH_LONG ).show();
        }


        try {
            if (mytag == null) {

                Toast.makeText(this, "Tag Null", Toast.LENGTH_LONG ).show();
            }

            else {

                PackageManager m = getPackageManager();
                String packageName = getPackageName();
                File[] filesOnTag = nfcInteraction.read(mytag, m, packageName);
                Log.d("NFC_Tag_Files_Format B", "FileName:" + filesOnTag[0].getName());
                Toast.makeText(this, "Tag Read", Toast.LENGTH_LONG ).show();

//                Uri story_directory_uri = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        filesOnTag[0].getAbsoluteFile());

//                Log.i("NFC URI: ", String.valueOf(story_directory_uri));

                if(filesOnTag!=null){

                    PlayStory(filesOnTag);
                }
            }

        } catch (IOException e) {
            // Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            System.out.println("Fail 1");
        } catch (FormatException e) {
            // Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            System.out.println("Fail 2");
        } catch (IndexOutOfBoundsException e) {
            // Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            System.out.println("Fail 3");
        } catch (NullPointerException e) {
            // Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            System.out.println("Fail 4");
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        nfcInteraction.ReadModeOff(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        nfcInteraction.ReadModeOn(adapter, pendingIntent, readTagFilters);
    }

    void paintViews() {

        for(int i=0; i<allViews.length; i++) {

            Drawable d = VectorDrawableCompat.create(getResources(), IvDrawable.get(allViews[i]), null);
            d = DrawableCompat.wrap(d);
            DrawableCompat.setTint(d, paintColourArray[paintColourArrayInt]);
            allViews[i].setImageDrawable(d);



            if(i==16) {

                DrawableCompat.setTint(d, Color.WHITE);
                allViews[16].setImageDrawable(d);
            }


            if(paintColourArrayInt<3) {

                paintColourArrayInt += 1;
            }

            else {

                paintColourArrayInt = 0;
            }

        }

        /*
        for(int i = 0; i<allViews.length; i++) {

            allViews[i].startAnimation(fadein);
            allViews[i].setVisibility(View.VISIBLE);
        }
        */
    }

    void animateViews() {

        startupTroveLogoAnimation();
    }

    void startupTroveLogoAnimation() {

        trove.startAnimation(bounce);
    }

    void delay() {

        startupLargeObjectsHandler = new Handler();
        startupLargeObjectsHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

//            Continue();
            }
        }, 5000);

    }

    public void Continue(View view) {

        trove.clearAnimation();
        Intent intent = new Intent(LoggedInReadHomeKidsUI.this, LoggedInWriteHomeKidsUI.class);
        intent.putExtra("Authenticated", authenticated);
        LoggedInReadHomeKidsUI.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);

/*
        Transition explode = new Explode();


        explode.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {

                Intent intent = new Intent(LoggedInReadHomeKidsUI.this, LoggedInWriteHomeKidsUI.class);
                LoggedInReadHomeKidsUI.this.startActivity(intent);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });


        TransitionManager.beginDelayedTransition(mRootView, explode);
        toggleVisibility(backgroundShapes, zigzag1, zigzag2, zigzag3, zigzag4, star, moon, shell, book, key,
                leaf, umbrella, tear, teddy, halfcircle, heart, trove, back);

                */
    }

    private static void toggleVisibility(ImageView... views) {
        for (ImageView view : views) {
            boolean isVisible = view.getVisibility() == View.VISIBLE;
            view.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        }
    }

    public void Back(View view) {

        onBackPressed();
    }

    @Override
    public void onBackPressed() {

        back.setImageDrawable(backRetrace);
        backRetrace.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Intent intent = new Intent(LoggedInReadHomeKidsUI.this, LoginKidsUI.class);
                LoggedInReadHomeKidsUI.this.startActivity(intent);
            }
        }, 1000);

    }
}
