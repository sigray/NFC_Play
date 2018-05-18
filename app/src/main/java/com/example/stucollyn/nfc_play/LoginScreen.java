package com.example.stucollyn.nfc_play;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout;

public class LoginScreen extends AppCompatActivity {

    Animation welcome_fade_in;
    Animation welcome_fade_out;
    Animation login_fade_in;
    Animation login_fade_out;
    Animation signup_fade_in;
    Animation signup_fade_out;
    Animation keypad_background_fade_in;
    Animation keypad_background_fade_out;
    Animation keypad_fade_in;
    Animation keypad_fade_out;
    Animation miine_fade_in;
    Animation miine_fade_out;
    Animation miine_shrink;
    Animation miine_shake;
    Animation miine_open_fade_in;
    Animation miine_open_fade_out;
    Animation instruction_fade_in;
    Animation instruction_fade_out;
    Animation balloon_move_normal, balloon_move_slower, balloon_move_faster;
    Animation passcode_box_fade_out;
    Button loginButton;
    Button signupButton;
    Button keypad1, keypad2, keypad3, keypad4, keypad5, keypad6, keypad7, keypad8, keypad9;
    TextView welcome;
    TextView instruction;
    TextView passCodeBox1, passCodeBox2, passCodeBox3, passCodeBox4;
    ImageView keypad_background;
    ImageView miine;
    ImageView miine_open;
    ImageView miine_mini;
    ImageView balloon1, balloon2, balloon3;
    TableLayout keypad, passCodeBoxTable;
    private static final String TAG = "miine App: ";
    StringBuilder passcodeAppend;
    String passcodeAttempt;
    String passcodeTarget;
    int passcodeCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        welcome_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        welcome_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        login_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        login_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        signup_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        signup_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        keypad_background_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        keypad_background_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        keypad_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        keypad_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        passcode_box_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        miine_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        miine_shrink = AnimationUtils.loadAnimation(this, R.anim.trove_shrink);
        miine_shake = AnimationUtils.loadAnimation(this, R.anim.trove_shake);
        miine_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        miine_open_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        miine_open_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        instruction_fade_in = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_in);
        instruction_fade_out = AnimationUtils.loadAnimation(this, R.anim.splash_screen_fade_out);
        balloon_move_normal = AnimationUtils.loadAnimation(this, R.anim.balloon_move_normal);
        balloon_move_slower = AnimationUtils.loadAnimation(this, R.anim.balloon_move_slower);
        balloon_move_faster = AnimationUtils.loadAnimation(this, R.anim.balloon_move_faster);

        welcome = (TextView) findViewById(R.id.welcome_text);
        loginButton = (Button) findViewById(R.id.login_button);
        signupButton = (Button) findViewById(R.id.signup_button);
        keypad1 = (Button) findViewById(R.id.keypad1);
        keypad2 = (Button) findViewById(R.id.keypad2);
        keypad3 = (Button) findViewById(R.id.keypad3);
        keypad4 = (Button) findViewById(R.id.keypad4);
        keypad5 = (Button) findViewById(R.id.keypad5);
        keypad6 = (Button) findViewById(R.id.keypad6);
        keypad7 = (Button) findViewById(R.id.keypad7);
        keypad8 = (Button) findViewById(R.id.keypad8);
        keypad9 = (Button) findViewById(R.id.keypad9);
        keypad_background = (ImageView) findViewById(R.id.keypad_background);
        keypad = (TableLayout) findViewById(R.id.keypad);
        passCodeBoxTable = (TableLayout) findViewById(R.id.passcode_box);
        passCodeBox1 = (TextView) findViewById(R.id.passcode_box_1);
        passCodeBox2 = (TextView) findViewById(R.id.passcode_box_2);
        passCodeBox3 = (TextView) findViewById(R.id.passcode_box_3);
        passCodeBox4 = (TextView) findViewById(R.id.passcode_box_4);
        miine = (ImageView) findViewById(R.id.miine);
        miine_mini = (ImageView) findViewById(R.id.miine_mini);
        balloon1 = (ImageView) findViewById(R.id.balloon1);
        balloon2 = (ImageView) findViewById(R.id.balloon2);
        balloon3 = (ImageView) findViewById(R.id.balloon3);
        miine_open = (ImageView) findViewById(R.id.miine_open);
        instruction = (TextView) findViewById(R.id.instruction_text);

        passcodeAppend = new StringBuilder("");
        passcodeAttempt = "";
        passcodeTarget = "1234";
        passcodeCounter = 0;

        FadeInLogin();
    }

    public void FadeInLogin() {

        welcome.startAnimation(welcome_fade_in);
        loginButton.startAnimation(login_fade_in);
        signupButton.startAnimation(signup_fade_in);
    }

    public void Login (View login){

        keypad_background.startAnimation(keypad_background_fade_in);
        keypad.startAnimation(keypad_fade_in);
        miine.startAnimation(miine_fade_out);
        loginButton.startAnimation(login_fade_out);
        signupButton.startAnimation(signup_fade_out);
        instruction.startAnimation(instruction_fade_in);
        miine.setVisibility(View.INVISIBLE);
        miine_open.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        signupButton.setVisibility(View.INVISIBLE);
        keypad.setVisibility(View.VISIBLE);
        keypad_background.setVisibility(View.VISIBLE);
        instruction.setVisibility(View.VISIBLE);
    }

    public void AnimateBalloons() {

        if(!balloon_move_slower.hasStarted()) {

            balloon1.startAnimation(balloon_move_normal);
            balloon2.startAnimation(balloon_move_slower);
            balloon3.startAnimation(balloon_move_faster);
        }

        if(balloon_move_slower.hasEnded()) {

            balloon1.startAnimation(balloon_move_normal);
            balloon2.startAnimation(balloon_move_slower);
            balloon3.startAnimation(balloon_move_faster);
        }
    }


    public void Open(View open) {

        if(miine_open.getVisibility()==View.INVISIBLE) {
            miine_open.setVisibility(View.VISIBLE);
        }
            AnimateBalloons();
    }

    public void Advance(){

        keypad_background.startAnimation(keypad_background_fade_out);
        keypad.startAnimation(keypad_fade_out);
        passCodeBoxTable.startAnimation(passcode_box_fade_out);
        instruction.startAnimation(instruction_fade_out);
        welcome.startAnimation(welcome_fade_out);
        miine.setVisibility(View.VISIBLE);
        instruction.setVisibility(View.INVISIBLE);
        welcome.setVisibility(View.INVISIBLE);
        passCodeBoxTable.setVisibility(View.INVISIBLE);

        keypad_fade_out.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                keypad.setVisibility(View.INVISIBLE);
                keypad_background.setVisibility(View.INVISIBLE);
                miine_open.setVisibility(View.VISIBLE);
                miine.setVisibility(View.VISIBLE);
                AnimateBalloons();
            }
        });

        balloon_move_faster.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                miine_open.startAnimation(miine_open_fade_out);
            }
        });


        miine_open_fade_out.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                miine_open.setVisibility(View.INVISIBLE);
                miine.startAnimation(miine_shrink);
            }
        });


        miine_shrink.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                miine_mini.setVisibility(View.VISIBLE);
                miine.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(LoginScreen.this, MainMenu.class);
                LoginScreen.this.startActivity(intent);
            }
        });

    }

    public void Keypad(View keypad){

        Button buttonPressed = (Button) keypad;
        String keypadEnteredCharacter = buttonPressed.getText().toString();
        Log.i(TAG, keypadEnteredCharacter);

        if(passcodeAppend.length()<4) {

            passcodeAppend = passcodeAppend.append(keypadEnteredCharacter);

           if(passcodeAppend.length()==1) {

                passCodeBox1.setText("*");
            }

            if(passcodeAppend.length()==2) {

                passCodeBox2.setText("*");
            }

            if(passcodeAppend.length()==3) {

               // passCodeBox2.setText("*");
                passCodeBox3.setText("*");
            }

            if(passcodeAppend.length()==4) {

               // passCodeBox3.setText("*");
                passCodeBox4.setText("*");
            }

          //  Log.i(TAG, passcodeAppend.toString());

        }

        if(passcodeAppend.length()==4) {

            if(passcodeAppend.toString().equals(passcodeTarget)) {

              //  passCodeBox4.setText("*");
                passcodeAppend.setLength(0);
                Advance();
                //Intent intent = new Intent(LoginScreen.this, NFCRead.class);
                //LoginScreen.this.startActivity(intent);
            }

            else {

                passcodeAppend.setLength(0);
                keypad_background.startAnimation(miine_shake);

                miine_shake.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        passCodeBox1.setText("");
                        passCodeBox2.setText("");
                        passCodeBox3.setText("");
                        passCodeBox4.setText("");
                    }
                });

            }
        }

      //  Log.i(TAG, passcodeAppend.toString());
    }

    public void FingerPrint() {

    }

    @Override
    public void onBackPressed() {

        this.recreate();

        /*
        keypad_background.startAnimation(keypad_background_fade_out);
        keypad.startAnimation(keypad_fade_out);
        miine.startAnimation(miine_fade_in);
        loginButton.startAnimation(login_fade_in);
        signupButton.startAnimation(signup_fade_in);
        instruction.startAnimation(instruction_fade_out);
        miine.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        signupButton.setVisibility(View.VISIBLE);
        keypad.setVisibility(View.INVISIBLE);
        keypad_background.setVisibility(View.INVISIBLE);
        instruction.setVisibility(View.INVISIBLE);
        */

    }

}
