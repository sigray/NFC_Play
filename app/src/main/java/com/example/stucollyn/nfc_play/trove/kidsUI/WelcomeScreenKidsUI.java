package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.example.stucollyn.nfc_play.R;

import java.util.HashMap;
import java.util.Random;

public class WelcomeScreenKidsUI extends AppCompatActivity {

    ImageView backgroundShapes, zigzag1, zigzag2, zigzag3, zigzag4, star, moon, shell, book, key,
            leaf, umbrella, tear, teddy, halfcircle, heart, trove, back;
    Animation spin, shrink, blink, draw, bounce, fadeout;
    ImageView zigzagArray[], largeItemArray[], allViews[];
    Handler startupZigZagHandler, startupLargeObjectsHandler, idleZigZagHandler,
            idleLargeObjectsHandler, idleTroveHandler;
    int zigzagInt = 0;
    int largeObjectsInt = 0;
    boolean startupZigZagAnimationComplete = false, startupLargeObjectsAnimationComplete = false;
    ViewGroup mRootView;
    Runnable TroveRunnable;
    String previousActivity = "Empty";
    public static boolean AppStarted = false;
    HashMap<ImageView, Integer> IvDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kids_ui_activity_welcome_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRootView = (ViewGroup) findViewById(R.id.welcome_screen);
        //Initialize views
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

        zigzagArray = new ImageView[5];
        zigzagArray[0] = zigzag1;
        zigzagArray[1] = zigzag2;
        zigzagArray[2] = zigzag3;
        zigzagArray[3] = zigzag4;
        zigzagArray[4] = back;


        largeItemArray = new ImageView[11];
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
        largeItemArray[10] = halfcircle;

        allViews = new ImageView[16];
        allViews[0] = zigzag1;
        allViews[1] = zigzag2;
        allViews[2] = zigzag3;
        allViews[3] = zigzag4;
        allViews[4] = back;
        allViews[5] = star;
        allViews[6] = moon;
        allViews[7] = shell;
        allViews[8] = book;
        allViews[9] = key;
        allViews[10] = leaf;
        allViews[11] = umbrella;
        allViews[12] = tear;
        allViews[13] = teddy;
        allViews[14] = heart;
        allViews[15] = halfcircle;

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


        //Initialize animations
        spin = AnimationUtils.loadAnimation(this, R.anim.spin);
        blink = AnimationUtils.loadAnimation(this, R.anim.blink);
        draw = AnimationUtils.loadAnimation(this, R.anim.draw);
        shrink = AnimationUtils.loadAnimation(this, R.anim.shrink);
        bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);

        paintViews();
        animationStartSequence();
    }

        //Animations once setup
        //animationIdleSequence();
        /*
        //Create an animation set to do spin animation
        AnimationSet s = new AnimationSet(true);
        s.setInterpolator(new AccelerateInterpolator());
        s.addAnimation(draw);
        s.addAnimation(fadein);
        //zigzag1.startAnimation(s);


//            if (d instanceof AnimatedVectorDrawableCompat) {
//                AnimatedVectorDrawableCompat zigzaganim = (AnimatedVectorDrawableCompat) d;
//                zigzaganim.start();
//            } else if (d instanceof AnimatedVectorDrawable) {
//                AnimatedVectorDrawable zigzaganim = (AnimatedVectorDrawable) d;
//                zigzaganim.start();
//            }

*/

    void paintViews() {

        for(int i=0; i<largeItemArray.length; i++) {

            Drawable d = VectorDrawableCompat.create(getResources(), IvDrawable.get(largeItemArray[i]), null);
            d = DrawableCompat.wrap(d);
            DrawableCompat.setTint(d, android.graphics.Color.rgb(111,133,226));
            largeItemArray[i].setImageDrawable(d);
        }
    }

    void animationIdleSequence() {

        idleZigZagHandler = new Handler();
        idleLargeObjectsHandler = new Handler();
        idleTroveHandler = new Handler();

        //Runnable to handle idle trove animation
        TroveRunnable = new Runnable() {

            @Override
            public void run() {
                idleTroveAnimation();
                idleTroveHandler.postDelayed(this, 2000);
            }
        };

        //Runnable to handle the zig zag drawings
        Runnable AnimationRunnable = new Runnable() {

            @Override
            public void run() {
//                zigZagAnimation();
                idleZigZagHandler.postDelayed(this, 2000);
            }
        };

        //Runnable to handle random animation sequence of larger objects
        Runnable LargeItemRunnable = new Runnable() {

            @Override
            public void run() {
                idleLargeItemAnimation();
                idleZigZagHandler.postDelayed(this, 8000);
            }
        };

        idleZigZagHandler.post(AnimationRunnable);
        idleLargeObjectsHandler.post(LargeItemRunnable);
        idleTroveHandler.post(TroveRunnable);


    }

    void idleTroveAnimation() {

        trove.startAnimation(bounce);
    }

    void idleLargeItemAnimation() {

        Random random = new Random();
        int i = random.nextInt(9);
        backgroundShapes.startAnimation(blink);
//        largeItemArray[i].startAnimation(blink);
    }

    void animationStartSequence() {

        startupZigZagHandler = new Handler();
        startupLargeObjectsHandler = new Handler();

        //Runnable to handle the zig zag drawings
        final Runnable zigZagRunnable = new Runnable() {

            @Override
            public void run() {

                startupZigZagAnimation();

                if(!startupZigZagAnimationComplete) {

                    startupZigZagHandler.postDelayed(this, 1000);
                }

                else {

                    startupZigZagHandler.removeCallbacks(this);
                    startupTroveLogoAnimation();
                    animationIdleSequence();

                }
            }
        };

        final Runnable largeObjectsRunnable = new Runnable() {

            @Override
            public void run() {
                startupLargeItemAnimation();

                if(!startupLargeObjectsAnimationComplete) {

                    startupLargeObjectsHandler.postDelayed(this, 1000);
                }

                else {

                    startupLargeObjectsHandler.removeCallbacks(this);
                    startupZigZagHandler.post(zigZagRunnable);
                }
            }
        };

        startupLargeObjectsHandler.post(largeObjectsRunnable);

    }

    void startupTroveLogoAnimation() {

        Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        trove.startAnimation(fadein);
        trove.setVisibility(View.VISIBLE);

    }

    void startupLargeItemAnimation() {

        Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);

        if(largeObjectsInt<11) {

            largeItemArray[largeObjectsInt].startAnimation(fadein);

            fadein.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    largeItemArray[largeObjectsInt].setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    largeObjectsInt += 1;
                }
            });
        }

        else {

            largeObjectsInt=0;
            startupLargeObjectsAnimationComplete= true;
            backgroundShapes.startAnimation(fadein);
            backgroundShapes.setVisibility(View.VISIBLE);
        }

    }

    void startupZigZagAnimation(){

        if(zigzagInt<5) {

            zigzagArray[zigzagInt].setVisibility(View.VISIBLE);
            Drawable d = zigzagArray[zigzagInt].getDrawable();
            final AnimatedVectorDrawable zigzaganim = (AnimatedVectorDrawable) d;
            zigzaganim.start();
            zigzagInt+=1;
        }

        else {

            zigzagInt=0;
            startupZigZagAnimationComplete = true;
        }
    }

    public void Skip(View view) {

        Intent intent = new Intent(WelcomeScreenKidsUI.this, LoginKidsUI.class);
        intent.putExtra("PreviousActivity", "WelcomeScreenKidsUI");
        WelcomeScreenKidsUI.this.startActivity(intent);
        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
    }

    public void Continue(View view) {

        idleTroveHandler.removeCallbacks(TroveRunnable);
        trove.clearAnimation();
        Intent intent = new Intent(WelcomeScreenKidsUI.this, LoginKidsUI.class);
        intent.putExtra("PreviousActivity", "WelcomeScreenKidsUI");
        WelcomeScreenKidsUI.this.startActivity(intent);
    }

    private static void toggleVisibility(ImageView... views) {
        for (ImageView view : views) {
            boolean isVisible = view.getVisibility() == View.VISIBLE;
            view.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
