package com.example.stucollyn.nfc_play;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TableLayout;
import android.widget.Toast;


/*LoginScreen Activity follows after the SplashScreen introduction and allows existing users to
login to the trove app or new users sign up*/
public class LoginScreen extends AppCompatActivity {

    //Declare global variables
    private static final String TAG = "miine App: ";
    Animation welcome_fade_in, welcome_fade_out, login_fade_in, login_fade_out, signup_fade_in,
            signup_fade_out, keypad_background_fade_in, keypad_background_fade_out, keypad_fade_in,
            keypad_fade_out, miine_fade_in, miine_fade_out, miine_shrink, miine_shake,
            miine_open_fade_in, miine_open_fade_out, instruction_fade_in, instruction_fade_out,
            balloon_move_normal, balloon_move_slower, balloon_move_faster, passcode_box_fade_out;
    Button loginButton, signupButton, keypad1, keypad2, keypad3, keypad4, keypad5, keypad6, keypad7,
            keypad8, keypad9;
    TextView welcome, instruction, passCodeBox1, passCodeBox2, passCodeBox3, passCodeBox4;
    ImageView keypad_background, miine, miine_open, miine_mini, balloon1, balloon2, balloon3;
    TableLayout keypad, passCodeBoxTable;
    StringBuilder passcodeAppend;
    String passcodeAttempt, passcodeTarget;
    int passcodeCounter;
    int mode;


    //onCreate method called on Activity start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mode = (Integer) getIntent().getExtras().get("Orientation");
        setRequestedOrientation(mode);

        //Initialize String builder to accept passcode keypad input
        passcodeAppend = new StringBuilder("");
        passcodeAttempt = "";
        passcodeTarget = "1234";
        passcodeCounter = 0;

        //Initialize Activity views and animations, and display
        InitAnimation();
        InitView();
        FadeInLogin();
    }

    //Initialize the single view animations used in the class
    private void InitAnimation() {

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
    }

    //Initialize the single views used in the Activity
    private void InitView() {

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
       balloon1 = (ImageView) findViewById(R.id.balloon1);
       balloon2 = (ImageView) findViewById(R.id.balloon2);
       balloon3 = (ImageView) findViewById(R.id.balloon3);
       miine_open = (ImageView) findViewById(R.id.miine_open);
       instruction = (TextView) findViewById(R.id.instruction_text);
   }

   //Initial fade in animations for trove logo, login and sign up buttons
    private void FadeInLogin() {

        welcome.startAnimation(welcome_fade_in);
//        loginButton.startAnimation(login_fade_in);
//        signupButton.startAnimation(signup_fade_in);
    }

    //When login button is pressed execute the following
    public void Login (View login){

        //Show passcode keypad and instruction; hide login, sign up buttons, and trove logo
//        keypad_background.startAnimation(keypad_background_fade_in);
        welcome.startAnimation(welcome_fade_out);
//        keypad.startAnimation(keypad_fade_in);
        miine.startAnimation(miine_fade_out);
//        loginButton.startAnimation(login_fade_out);
//        signupButton.startAnimation(signup_fade_out);
//        instruction.startAnimation(instruction_fade_in);
        miine.setVisibility(View.INVISIBLE);
        miine_open.setVisibility(View.INVISIBLE);
//        loginButton.setVisibility(View.INVISIBLE);
//        signupButton.setVisibility(View.INVISIBLE);
//        keypad.setVisibility(View.VISIBLE);
//        keypad_background.setVisibility(View.VISIBLE);
//        instruction.setVisibility(View.VISIBLE);
        welcome.setVisibility(View.INVISIBLE);
        miine.setClickable(false);
        Advance();
    }

    //Balloon animation scheduler
    private void AnimateBalloons() {

        //If no balloons have started, start the slow, fast, and normal paced balloon animations
        if(!balloon_move_slower.hasStarted()) {

            balloon1.startAnimation(balloon_move_normal);
            balloon2.startAnimation(balloon_move_slower);
            balloon3.startAnimation(balloon_move_faster);
        }

        //If the slowest and final balloon has finished, start next wave of balloons
        if(balloon_move_slower.hasEnded()) {

            balloon1.startAnimation(balloon_move_normal);
            balloon2.startAnimation(balloon_move_slower);
            balloon3.startAnimation(balloon_move_faster);
        }
    }

    //When correct pass code is matched, start advancement animations, before opening new MainMenu Activity
    private void Advance(){


//        keypad_background.startAnimation(keypad_background_fade_out);
//        keypad.startAnimation(keypad_fade_out);
//        passCodeBoxTable.startAnimation(passcode_box_fade_out);
//        instruction.startAnimation(instruction_fade_out);
//        welcome.startAnimation(welcome_fade_out);
        miine.setVisibility(View.VISIBLE);
//        instruction.setVisibility(View.INVISIBLE);
        welcome.setVisibility(View.INVISIBLE);
//        passCodeBoxTable.setVisibility(View.INVISIBLE);

        miine_open.setVisibility(View.VISIBLE);
        miine.setVisibility(View.VISIBLE);
        AnimateBalloons();

        /*
        //Listen for the keypad view to finish fade out, then perform success balloon animation
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
        */

        //Listen for the success balloon animation to finish, then fade out trove logo
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

        //Listen for the trove logo to finish fading out, then start MainMenu Activity
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
                Intent intent = new Intent(LoginScreen.this, MainMenu.class);
                intent.putExtra("Orientation", mode);
                LoginScreen.this.startActivity(intent);

                //Activity fade transition
                overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
            }
        });

    }

    //Execute when any of the keypad buttons are touched to build and validate pass code
    public void Keypad(View keypad) {

        Button buttonPressed = (Button) keypad;
        String keypadEnteredCharacter = buttonPressed.getText().toString();

        //Handle new key press by adding button number to pass code string builder
            passcodeAppend = passcodeAppend.append(keypadEnteredCharacter);
            int passcodeAppendLength = passcodeAppend.length();

            /*Check pass code string builder length. If < 4 characters display * symbol above
            keypad. If 4 characters in length, check for correctness and either reset or
            begin correct pass code sequence */
            switch (passcodeAppendLength) {

                case (1):

                    passCodeBox1.setText("*");
                    break;

                case (2):

                    passCodeBox2.setText("*");
                    break;


                case (3):

                    passCodeBox3.setText("*");
                    break;

                case (4):

                    //Pause key button clickability while target match is processed
                    keypad1.setClickable(false);
                    keypad2.setClickable(false);
                    keypad3.setClickable(false);
                    keypad4.setClickable(false);
                    keypad5.setClickable(false);
                    keypad6.setClickable(false);
                    keypad7.setClickable(false);
                    keypad8.setClickable(false);
                    keypad9.setClickable(false);

                    passCodeBox4.setText("*");

                    //Check 4 digit code for correctness
                    if (passcodeAppend.toString().equals(passcodeTarget)) {

                        //Reset string builder and advance
                        passcodeAppend.setLength(0);
                        Advance();
                    }

                    else {

                        //Reset string builder, shake keypad animation to indicate wrong password
                    passcodeAppend.setLength(0);
                    keypad_background.startAnimation(miine_shake);

                    /*After keypad animation has finished, set remove pass code box * symbols and
                    make keypad clickable again
                     */
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
                            keypad1.setClickable(true);
                            keypad2.setClickable(true);
                            keypad3.setClickable(true);
                            keypad4.setClickable(true);
                            keypad5.setClickable(true);
                            keypad6.setClickable(true);
                            keypad7.setClickable(true);
                            keypad8.setClickable(true);
                            keypad9.setClickable(true);

                        }
                    });

                }

                break;

                default:
            }
    }

    //Future method for finger print login
    private void FingerPrint() {

    }

    //Future method for sign-up functionality
    private void SignUp() {

    }

    //On back button pressed, restart Activity
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(LoginScreen.this, SelectMode.class);
        LoginScreen.this.startActivity(intent);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//            setContentView(R.layout.activity_login_screen);
//        InitAnimation();
//        InitView();
//    }

}
