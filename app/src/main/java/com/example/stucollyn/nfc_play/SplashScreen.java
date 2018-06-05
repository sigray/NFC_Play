package com.example.stucollyn.nfc_play;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    //Duration of wait before opening Main Menu page
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    Animation meineck_fade_in;
    Animation meineck_fade_out;
    Animation uob_fade_in;
    Animation uob_fade_out;
    Animation miine_fade_in;
    Animation miine_fade_out;
    ImageView meineckLogo;
    ImageView uobLogo;
    ImageView miineLogo;
    private static final String TAG = "miine App: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        meineck_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        meineck_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        uob_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        uob_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        miine_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        miine_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        meineckLogo = (ImageView) findViewById(R.id.meineck);
        uobLogo = (ImageView) findViewById(R.id.uob);
        miineLogo = (ImageView) findViewById(R.id.miine);
        animateStudioMeineckLogo();
/*
*/

      /* new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
               /* Intent mainIntent = new Intent(SplashScreen.this, NFCRead.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }

        }, SPLASH_DISPLAY_LENGTH); */


    }


    public void animateStudioMeineckLogo() {

        meineckLogo = (ImageView) findViewById(R.id.meineck);
        meineckLogo.startAnimation(meineck_fade_in);

        meineck_fade_in.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                meineckLogo.startAnimation(meineck_fade_out);
            }
        });



        meineck_fade_out.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                meineckLogo.setVisibility(View.INVISIBLE);
                animateUoBLogo();
            }
        });

    }

    public void animateUoBLogo() {

        uobLogo = (ImageView) findViewById(R.id.uob);
        uobLogo.startAnimation(uob_fade_in);

        uob_fade_in.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                uobLogo.startAnimation(uob_fade_out);
            }
        });

        Log.i(TAG, "Help");


        uob_fade_out.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                uobLogo.setVisibility(View.INVISIBLE);
                animateMiineLogo();
            }
        });

    }

    public void animateMiineLogo() {

        miineLogo = (ImageView) findViewById(R.id.miine);
        miineLogo.startAnimation(miine_fade_in);
        miineLogo.setVisibility(View.VISIBLE);

        miine_fade_in.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
          //      miineLogo.startAnimation(miine_fade_out);
            }
        });


        miine_fade_in.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
                SplashScreen.this.startActivity(intent);

            }
        });


    }

    public void Skip(View view) {

        Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
        SplashScreen.this.startActivity(intent);

    }

}
