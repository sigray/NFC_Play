package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.view.View.OnClickListener;

import com.example.stucollyn.nfc_play.R;

import java.util.HashMap;

public class LoginKidsUI extends AppCompatActivity {

    ImageView backgroundShapes, zigzag1, zigzag2, zigzag3, zigzag4, star, moon, shell, book, key,
            leaf, umbrella, tear, teddy, heart, trove, back, halfcircle;
    Handler startupZigZagHandler, startupLargeObjectsHandler;
    Animation spin, shrink, blink, draw, bounce, fadeout;
    int largeObjectsInt = 0;
    boolean startupLargeObjectsAnimationComplete = false, passcodeReady = false;
    ImageView largeItemArray[], otherItemArray[];
    HashMap<ImageView, Integer> IvDrawable;
    String testPasscode = "heartmoonshell";
    String passcode = "heartkeybook";
    StringBuilder passcodeAppend;
    Integer[] paintColourArray;
    int paintColourArrayInt = 0;
    int attemptInt = 0;
    ViewGroup mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_kids_ui);
        mRootView = (ViewGroup) findViewById(R.id.login_kids_ui);

        passcodeAppend = new StringBuilder("");
        paintColourArray = new Integer[3];
        paintColourArray[0] = android.graphics.Color.rgb(255, 157, 0);
        paintColourArray[1] = android.graphics.Color.rgb(253, 195, 204);
        paintColourArray[2] = android.graphics.Color.rgb(0, 235, 205);

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

        largeItemArray = new ImageView[10];
        largeItemArray[0] = star;
        largeItemArray[1] = moon;
        largeItemArray[2] = shell;
        largeItemArray[3] = book;
        largeItemArray[4] = key;
        largeItemArray[5] = leaf;
        largeItemArray[6] = umbrella;
        largeItemArray[7] = tear;
        largeItemArray[8] = teddy;
        largeItemArray[9] = heart;

        otherItemArray = new ImageView[7];
        otherItemArray[0] = backgroundShapes;
        otherItemArray[1] = zigzag1;
        otherItemArray[2] = zigzag2;
        otherItemArray[3] = zigzag3;
        otherItemArray[4] = zigzag4;
        otherItemArray[5] = back;
        otherItemArray[6] = halfcircle;


        //Initialize animations
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        startupLargeItemAnimation();
    }


    void AuthenticatedLogin() {



    }

    void startupLargeItemAnimation() {

        for (int i=0; i<otherItemArray.length; i++) {

            otherItemArray[i].startAnimation(fadeout);
            otherItemArray[i].setVisibility(View.INVISIBLE);

        }

        for (int i=0; i<largeItemArray.length; i++) {

            largeItemArray[i].setClickable(true);
        }

/*
        Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);

        for (int i=0; i<largeItemArray.length; i++) {

            largeItemArray[i].startAnimation(fadein);
            largeItemArray[i].setClickable(true);
            largeItemArray[i].setVisibility(View.VISIBLE);
        }

        */
    }

    public void shell (View view) {

        AppendPassword("shell", shell, R.drawable.kids_ui_shell);
    }

    public void heart (View view) {

        AppendPassword("heart", heart, R.drawable.kids_ui_heart);
    }

    public void teddy (View view) {

        AppendPassword("teddy", teddy, R.drawable.kids_ui_teddy);
    }

    public void key (View view) {

        AppendPassword("key", key, R.drawable.kids_ui_key);
    }

    public void star (View view) {

        AppendPassword("star", star, R.drawable.kids_ui_star);
    }

    public void umbrella (View view) {

        AppendPassword("umbrella", umbrella, R.drawable.kids_ui_umbrella);
    }

    public void leaf (View view) {

        AppendPassword("leaf", leaf, R.drawable.kids_ui_leaf);
    }

    public void tear (View view) {

        AppendPassword("tear", tear, R.drawable.kids_ui_tear);
    }

    public void moon (View view) {

        AppendPassword("moon", moon, R.drawable.kids_ui_moon);
    }

    public void book (View view) {

        AppendPassword("book", book, R.drawable.kids_ui_book);
    }

    void AppendPassword(String imageName, ImageView ivName, Integer drawable) {

        Drawable d = VectorDrawableCompat.create(getResources(), drawable, null);
        d = DrawableCompat.wrap(d);
        DrawableCompat.setTint(d, paintColourArray[paintColourArrayInt]);
        ivName.setImageDrawable(d);

        if(paintColourArrayInt<2) {

            paintColourArrayInt += 1;
        }

        else {

            paintColourArrayInt = 0;
        }

        passcodeAppend.append(imageName);

        if(passcodeAppend.toString().equals(passcode)) {

            Login();
        }

        if(attemptInt<9) {

            attemptInt += 1;
        }

        else {

            attemptInt = 0;
            passcodeAppend.setLength(0);
            ResetAttempt();
        }
    }

    void ResetAttempt(){

        for(int i=0; i<largeItemArray.length; i++) {

            Drawable d = VectorDrawableCompat.create(getResources(), IvDrawable.get(largeItemArray[i]), null);
            d = DrawableCompat.wrap(d);
            DrawableCompat.setTint(d, android.graphics.Color.rgb(111, 133, 226));
            largeItemArray[i].setImageDrawable(d);

        }
    }

    void Login() {

        Intent intent = new Intent(LoginKidsUI.this, LoggedInReadHomeKidsUI.class);
        LoginKidsUI.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);


        /*
        Transition explode = new Explode();


        explode.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {

                Intent intent = new Intent(LoginKidsUI.this, LoggedInReadHomeKidsUI.class);
                LoginKidsUI.this.startActivity(intent);
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
        toggleVisibility(star, moon, shell, book, key,
                leaf, umbrella, tear, teddy, heart);

                */

    }

    private static void toggleVisibility(ImageView... views) {
        for (ImageView view : views) {
            boolean isVisible = view.getVisibility() == View.VISIBLE;
            view.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        }
    }


    private OnClickListener myListener = new OnClickListener() {
        public void onClick(View v) {
            Object tag = v.getTag();
            // Do something depending on the value of the tag
        }
    };


   /* public void setPasscodeReady(){

//        VectorChildFinder vector = new VectorChildFinder(LoginKidsUI.this, IvDrawable.get(largeItemArray[0]), largeItemArray[0]);
//
//        VectorDrawableCompat.VFullPath path1 = vector.findPathByName("path1");
//        path1.setFillColor(Color.RED);

        if(passcodeReady){

            for(int i =0; i<largeItemArray.length; i++) {

                largeItemArray[i].setClickable(true);
                largeItemArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(LoginKidsUI.this,
                            "The favorite list would appear on clicking this icon",
                            Toast.LENGTH_LONG).show();



                    Drawable d = VectorDrawableCompat.create(getResources(), IvDrawable.get(largeItemArray[largeObjectsInt]), null);
                    d = DrawableCompat.wrap(d);
                    DrawableCompat.setTint(d, Color.CYAN);
                    largeItemArray[largeObjectsInt].setImageDrawable(d);
                }
            });
            }
        }
    }

    */

}
