package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.stucollyn.nfc_play.R;

public class HomeScreenKidsUI extends AppCompatActivity {

    ImageView recordButton;
    AnimatedVectorDrawable d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_kids_ui);

        recordButton = (ImageView) findViewById(R.id.record);
        d = (AnimatedVectorDrawable) getDrawable(R.drawable.kids_ui_record_anim); // Insert your AnimatedVectorDrawable resource identifier

    }

    public void Record(View view) {

       animateRecordButton();
    }

    void animateRecordButton() {

        recordButton.setImageDrawable(d);
        d.registerAnimationCallback(new Animatable2.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                d.start();
            }
        });
        d.start();
    }

    public void Camera(View view) {

    }
}
