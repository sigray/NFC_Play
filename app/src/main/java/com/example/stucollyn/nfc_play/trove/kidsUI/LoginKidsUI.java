package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.stucollyn.nfc_play.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/*
This activity handles the login procedure for the app, allowing a new user to sign up or an existing user to sign in. New users must submit their name, an  email address
and a password. New users must enter a pattern to sign in.
 */

/*
To do: allow users to set and reset password object patterns, and for that password to be pulled and validated from the cloud rather than as it currently is - hard coded (heart, key, book).
 */

public class LoginKidsUI extends FragmentActivity implements LoginOrSignUpDialogFragment.LoginOrSignUpDialogListener, SignUpDialogFragmentKidsUI.NoticeSignUpDialogListener,
        LoginDialogFragmentKidsUI.NoticeLoginDialogListener {

    //The ImageViews displayed on the activity layout
    ImageView backgroundShapes, zigzag1, zigzag2, zigzag3, zigzag4, star, moon, shell, book, key,
            leaf, umbrella, tear, teddy, heart, trove, back, halfcircle;

    //The animations used on the ImageViews
    Animation spin, shrink, blink, draw, bounce, fadeout, shake;

    //Arrays for grouping specific views together
    ImageView passCodeItemArray[], nonPassCodeItemArray[];

    //Hash map which couples each image view with an image resource. This is used later in the activity to load image recources into corresponding ImageViews.
    HashMap<ImageView, Integer> IvDrawable;

    //Handlers, runnables, and logical components governing the timing and repetition of animations
    Handler startupZigZagHandler, startupLargeObjectsHandler;
    int largeObjectsInt = 0;
    boolean startupLargeObjectsAnimationComplete = false, passcodeReady = false;
    ViewGroup mRootView;

    //trove Voice variables
    CommentaryInstruction commentaryInstruction;

    //Google Firebase Backend, password, and password display variables
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    boolean isNetworkConnected;
    boolean authenticated = false;
    String testPasscode = "heartmoonshell";
    String passcode = "heartkeybook";
    StringBuilder passcodeAppend;
    Integer[] paintColourArray;
    int paintColourArrayInt = 0;
    int attemptInt = 0;

    //onCreate is called when the activity starts
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Layout associated with this activity
        setContentView(R.layout.activity_login_kids_ui);
        //Ensure screen always stays on and never dims
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Initialize the ImageViews for programmatic use
        initializeViews();
        //Check for data connection before allowing user to sign in via cloud account
        checkConnection();
        //Check where the user navigated to this activity from and take appropriate action
        checkPreviousActivity();
    }

    //Check for data connection before allowing user to sign in via cloud account
    void checkConnection() {

        //Call method which checks for connection
        isNetworkConnected = isNetworkConnected();

        //If there is a data connection currently available for use, attempt to authenticate login details with Firebase,
        // allow user to login offline but without a profile and access only to local storage.
        if(isNetworkConnected) {

            AuthenticatedLogin();
        }

        else {

            //Fadeout back button and call passcodePhase method to show passcode objects
            //Note, the need to enter a passcode could be completely removed once the app goes live as security is only assured
            //to individuals with an active user account i.e. if an individual were to root the device. However, a simple non-cloud
            //authenticated passcode / useraccount works (locally in the short-term) as stories are currently saved to internal folders.
            back.startAnimation(fadeout);
            back.setVisibility(View.INVISIBLE);
            PasscodePhase();
        }
    }

    void initializeViews() {

        //Initialize view group - to reference all image views we will be manipulating and animating.
        mRootView = (ViewGroup) findViewById(R.id.login_kids_ui);

        //Initialize passcode builder which creates the string representation of object presses.
        passcodeAppend = new StringBuilder("");

        //Initialize the three different colours used to denote passcode object presses. Every time a new passcode object is pressed
        //it is painted by one of the below colours.
        paintColourArray = new Integer[3];
        paintColourArray[0] = android.graphics.Color.rgb(255, 157, 0);
        paintColourArray[1] = android.graphics.Color.rgb(253, 195, 204);
        paintColourArray[2] = android.graphics.Color.rgb(0, 235, 205);

        //Initialize the image views themselves.
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

        //Hashmap which we can use to reference the image resource of each imageview.
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

        //Initialize array which stores all of the large background objects used as part of the passcode.
        passCodeItemArray = new ImageView[10];
        passCodeItemArray[0] = star;
        passCodeItemArray[1] = moon;
        passCodeItemArray[2] = shell;
        passCodeItemArray[3] = book;
        passCodeItemArray[4] = key;
        passCodeItemArray[5] = leaf;
        passCodeItemArray[6] = umbrella;
        passCodeItemArray[7] = tear;
        passCodeItemArray[8] = teddy;
        passCodeItemArray[9] = heart;

        //Initialize array which stores all non-passcode objects.
        nonPassCodeItemArray = new ImageView[7];
        nonPassCodeItemArray[0] = backgroundShapes;
        nonPassCodeItemArray[1] = zigzag1;
        nonPassCodeItemArray[2] = zigzag2;
        nonPassCodeItemArray[3] = zigzag3;
        nonPassCodeItemArray[4] = zigzag4;
        nonPassCodeItemArray[5] = halfcircle;
        nonPassCodeItemArray[6] = trove;

        //Initialize animations
        fadeout = AnimationUtils.loadAnimation(this, R.anim.slowfadeout);
        shake = AnimationUtils.loadAnimation(this, R.anim.shake_left);
        commentaryInstruction = new CommentaryInstruction(this, this, false, authenticated);

    }

    //Check what the previous activity was and take appropriate action. Currently, the default previous activity will be the WelcomeScreen.
    void checkPreviousActivity() {

        //Check what the previous activity was from the variable passed from the last activity.
        String previousActivity = (String) getIntent().getExtras().get("PreviousActivity");

        //If the previous activity was WelcomeScreen, play the welcome commentary instruction.
        if(previousActivity.equals("WelcomeScreenKidsUI")) {

            commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.helloandpassword), false, null, "LoginKidsUI");
        }
    }

    //The sign up dialog fragment receives a reference to this Activity from Fragment.onAttach() callback. It uses this to call the following methods
    //defined by the SignUpDialogFragmentKidsUI.NoticeSignUpDialogListener interface.
    @Override
    public void onDialogSignUpPositiveClick(String username, String password, String firstName, String lastName) {
        // User touched the dialog's positive imageView
        Log.i("good", "success");

        FirebaseSignUp(username, password, firstName, lastName);
    }

    //The sign up dialog fragment receives a reference to this Activity from Fragment.onAttach() callback. It uses this to call the following methods
    //defined by the SignUpDialogFragmentKidsUI.NoticeSignUpDialogListener interface.
    @Override
    public void onDialogSignUpNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative imageView
    }

    //The login dialog fragment receives a reference to this Activity from Fragment.onAttach() callback. It uses this to call the following methods
    //defined by the LoginDialogFragmentKidsUI.NoticeLoginDialogListener interface.
    @Override
    public void onLoginDialogPositiveClick(String username, String password) {
        // User touched the dialog's positive imageView
        Log.i("username: ", username);
        Log.i("password: ", password);

        FirebaseLogin(username, password);
    }

    //The login dialog fragment receives a reference to this Activity from Fragment.onAttach() callback. It uses this to call the following methods
    //defined by the LoginDialogFragmentKidsUI.NoticeLoginDialogListener interface.
    @Override
    public void onLoginDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative imageView
    }

    @Override
    public void onLoginButton() {

        Toast.makeText(LoginKidsUI.this,
                "Wooohoo",
                Toast.LENGTH_LONG).show();
        DialogFragment dialog = new LoginDialogFragmentKidsUI();
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    public void onSignUpButton(){

//        DialogFragment dialog = new SignUpDialogFragmentKidsUI();
//        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    private boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    void newLoginSignUp() {

        DialogFragment dialog = new LoginOrSignUpDialogFragment();
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    void AuthenticatedLogin() {

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            Toast.makeText(LoginKidsUI.this,
                    "Logged in as " + user.getEmail(),
                    Toast.LENGTH_LONG).show();
            authenticated = true;
            PasscodePhase();
        }

        else {

            newLoginSignUp();
        }
    }

    void FirebaseLogin(String username, String password) {

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginKidsUI.this, "Logging in as: " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            PasscodePhase();
                        }

                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginKidsUI.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void FirebaseDatabaseNewUser(String username, String password, String firstName, String lastName) {

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("Username", username);
        newUser.put("Password", password);
        newUser.put("First Name", firstName);
        newUser.put("Last Name", lastName);

        db.collection("User").document(username)
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    void FirebaseSignUp(final String username, final String password, final String firstName, final String lastName) {

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginKidsUI.this, "Account Created.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseDatabaseNewUser(username, password, firstName, lastName);
                            FirebaseLogin(username, password);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginKidsUI.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    void PasscodePhase() {

        startupLargeItemAnimation();
    }

    void startupLargeItemAnimation() {

        for (int i = 0; i< nonPassCodeItemArray.length; i++) {

            nonPassCodeItemArray[i].setClickable(false);
            nonPassCodeItemArray[i].startAnimation(fadeout);
            nonPassCodeItemArray[i].setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i< passCodeItemArray.length; i++) {

            passCodeItemArray[i].setClickable(true);
        }

/*
        Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);

        for (int i=0; i<passCodeItemArray.length; i++) {

            passCodeItemArray[i].startAnimation(fadein);
            passCodeItemArray[i].setClickable(true);
            passCodeItemArray[i].setVisibility(View.VISIBLE);
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

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            v.vibrate(500);
        }

        for(int i = 0; i< passCodeItemArray.length; i++) {

            passCodeItemArray[i].startAnimation(shake);
            Drawable d = VectorDrawableCompat.create(getResources(), IvDrawable.get(passCodeItemArray[i]), null);
            d = DrawableCompat.wrap(d);
            DrawableCompat.setTint(d, android.graphics.Color.rgb(111, 133, 226));
            passCodeItemArray[i].setImageDrawable(d);
        }
    }

    void Login() {

//        Intent intent = new Intent(LoginKidsUI.this, LoggedInReadHomeKidsUI.class);
//        intent.putExtra("PreviousActivity", "LoginKidsUI");
//        LoginKidsUI.this.startActivity(intent);
//        overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);

        commentaryInstruction.onPlay(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chime), false, null, "LoginKidsUI");

        Transition explode = new Explode();


        explode.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {

                Intent intent = new Intent(LoginKidsUI.this, LoggedInReadHomeKidsUI.class);
                intent.putExtra("PreviousActivity", "LoginKidsUI");
                intent.putExtra("Authenticated", authenticated);
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

    //Activity Governance

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();
    }

    public void onDestroy() {

        super.onDestroy();

    }

    public void Back(View view) {

        onBackPressed();
    }

    @Override
    public void onBackPressed() {

        if(isNetworkConnected) {
            newLoginSignUp();
        }
    }

   /* public void setPasscodeReady(){

//        VectorChildFinder vector = new VectorChildFinder(LoginKidsUI.this, IvDrawable.get(passCodeItemArray[0]), passCodeItemArray[0]);
//
//        VectorDrawableCompat.VFullPath path1 = vector.findPathByName("path1");
//        path1.setFillColor(Color.RED);

        if(passcodeReady){

            for(int i =0; i<passCodeItemArray.length; i++) {

                passCodeItemArray[i].setClickable(true);
                passCodeItemArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(LoginKidsUI.this,
                            "The favorite list would appear on clicking this icon",
                            Toast.LENGTH_LONG).show();



                    Drawable d = VectorDrawableCompat.create(getResources(), IvDrawable.get(passCodeItemArray[largeObjectsInt]), null);
                    d = DrawableCompat.wrap(d);
                    DrawableCompat.setTint(d, Color.CYAN);
                    passCodeItemArray[largeObjectsInt].setImageDrawable(d);
                }
            });
            }
        }
    }

    */

}
