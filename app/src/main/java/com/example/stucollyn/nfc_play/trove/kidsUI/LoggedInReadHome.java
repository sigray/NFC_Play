package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.stucollyn.nfc_play.R;
import com.example.stucollyn.nfc_play.SelectMode;

import java.util.HashMap;

public class LoggedInReadHome extends AppCompatActivity {

    ImageView backgroundShapes, zigzag1, zigzag2, zigzag3, zigzag4, star, moon, shell, book, key,
            leaf, umbrella, tear, teddy, heart, trove, back, halfcircle;
    Handler startupZigZagHandler, startupLargeObjectsHandler;
    Animation spin, shrink, blink, draw, bounce, fadein, alpha, fadeout;
    ImageView zigzagArray[], largeItemArray[], allViews[];
    Integer[] paintColourArray;
    int paintColourArrayInt = 0;
    HashMap<ImageView, Integer> IvDrawable;
    ViewGroup mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_read_home);

        mRootView = (ViewGroup) findViewById(R.id.activity_logged_in_read_home);

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

        paintViews();
        animateViews();
//        delay();
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


        Transition explode = new Explode();


        explode.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {

                Intent intent = new Intent(LoggedInReadHome.this, HomeScreenKidsUI.class);
                LoggedInReadHome.this.startActivity(intent);
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
    }

    private static void toggleVisibility(ImageView... views) {
        for (ImageView view : views) {
            boolean isVisible = view.getVisibility() == View.VISIBLE;
            view.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
