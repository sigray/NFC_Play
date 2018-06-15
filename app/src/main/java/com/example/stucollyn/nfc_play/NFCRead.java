package com.example.stucollyn.nfc_play;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import android.media.MediaPlayer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;


public class NFCRead extends AppCompatActivity {

    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter readTagFilters[];
    Tag mytag;
    NdefMessage tagContents;
    Context ctx;
    ArrayList<String> messageArray;
    String takeAction="", playerType="", owner="", theMessage ="", colour="", id="", type="", retheMessage = "", reowner = "", reid = "",
            recolour = "", retype = "", retakeAction = "", speakInstruction;
    ImageView nfc_transmit;
    boolean commit = false, errorOccurred = false, posession = false, readMode = false, caught = false;
    MediaPlayer mp = new MediaPlayer();
    boolean mpPlaying = false;
    Button pauseNplay;
    boolean pauseNplayVisibility = false;
    boolean pauseOrplay = false;
    Animation nfc_transmit_animation;


    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_nfc);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.trove_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Play Tagged Story");

        pauseNplay = (Button)findViewById(R.id.button);
        NFCSetup();
        Log.d("d", "Check");
        nfc_transmit = (ImageView) findViewById(R.id.nfc_transmit);
        nfc_transmit_animation = AnimationUtils.loadAnimation(this, R.anim.flash);
        int visi = nfc_transmit.getVisibility();
        Log.i("i", Integer.toString(visi));
        nfc_transmit.startAnimation(nfc_transmit_animation);
    }

    public void NFCSetup(){
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        readTagFilters = new IntentFilter[] { tagDetected };
    }

    private void read(Tag tag) throws IOException, FormatException, IndexOutOfBoundsException, NullPointerException {

        Log.d("d", "On");
        String s = null;
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        tagContents = ndef.getNdefMessage();

        // get NDEF message details
        NdefMessage ndefMesg = ndef.getCachedNdefMessage();
        NdefRecord[] ndefRecords = ndefMesg.getRecords();
        int len = ndefRecords.length;

        byte[] mesg = null;
        String[] recTypes = new String[len];     // will contain the NDEF record types
        for (int i = 0; i < len; i++)
        {
            recTypes[i] = new String(ndefRecords[i].getType());
            mesg = ndefRecords[i].getPayload();
            s = new String(mesg);

        }

        s = s.substring(3);

        PackageManager m = getPackageManager();
        String packageName = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(packageName, 0);
            packageName = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }

        Log.d("d", "Tag Path: " + s);
        Log.d("d", "Default Path: " + Environment.getExternalStorageDirectory().toString());
        Log.d("d", "Package Path: " + packageName.toString());


        ndef.close();

//        String path = packageName.toString()+"/files";
        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/"+s;
//        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Stories/20180615_190621";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }



        /*
        pauseNplay.setText("II");
        nfc_transmit.setVisibility(View.INVISIBLE);
        nfc_transmit.animate().cancel();
        nfc_transmit.clearAnimation();

        if(pauseNplayVisibility==false) {
            pauseNplay.setVisibility(View.VISIBLE);
            pauseNplayVisibility = true;
        }

        mp.setDataSource(s);
        mp.prepare();
        mp.start();
        mpPlaying = true;




        /*
        Uri selectedUri = Uri.parse(s);
        Log.d("HATERZ", "URI: " + selectedUri);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");

          startActivity(intent);


        if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
        {
            startActivity(intent);
        }
        else
        {
            Log.d("HATERZ", "Feck");

        }
        */


    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            //Toast.makeText(this, "Tag Detected", Toast.LENGTH_LONG).show();
            Log.d("d", "Found it");

                mp.reset();
            }


        try {
            if (mytag == null) {
               // Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
                takeAction = "Do Nothing";

            } else {
                read(mytag);

            }

        } catch (IOException e) {
           // Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            errorOccurred = true;
            System.out.println("Fail 1");
        } catch (FormatException e) {
           // Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            errorOccurred = true;
            System.out.println("Fail 2");
        } catch (IndexOutOfBoundsException e) {
           // Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            errorOccurred = true;
            System.out.println("Fail 3");
        } catch (NullPointerException e) {
           // Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            errorOccurred = true;
            System.out.println("Fail 4");
        }

    }

    public void pauseNplay(View view){

        Log.d("d", "pauseOrplay: " + pauseOrplay);

        if(pauseOrplay==false) {

            pauseOrplay = true;
            pauseNplay.setText("\u25B6");
            mp.pause();
        }

        else if(pauseOrplay==true) {

            pauseOrplay = false;
            pauseNplay.setText("II");
            mp.start();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        adapter.disableForegroundDispatch(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        adapter.enableForegroundDispatch(this, pendingIntent, readTagFilters, null);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(NFCRead.this, MainMenu.class);
        NFCRead.this.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}