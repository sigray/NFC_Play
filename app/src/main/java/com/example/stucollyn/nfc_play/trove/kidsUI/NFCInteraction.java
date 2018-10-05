package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stucollyn.nfc_play.R;
import com.example.stucollyn.nfc_play.SavedStoryConfirmation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;

public class NFCInteraction {

    String action;
    String themessage;
    String owner;
    String type;
    String id;
    String colour;
    TextView instruction;
    static String display;
    Tag mytag;
    // File fileDirectory;
    String tag_data = "";
    int mode;
    Context context;
    Activity activity;
    NdefMessage tagContents;
    File[] filesOnTag;
    boolean authenticated = false;

    public NFCInteraction(Context context, Activity activity, boolean authenticated) {

        this.context = context;
        this.activity = activity;
        this.authenticated = authenticated;
    }

    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(context, "Object found.", Toast.LENGTH_LONG).show();
        }

        if (tag_data != null) {
            doWrite(mytag, tag_data);
        }
    }

    File[] read(Tag tag, PackageManager pacMan, String packageName) throws IOException, FormatException, IndexOutOfBoundsException, NullPointerException {

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
        for (int i = 0; i < len; i++) {
            recTypes[i] = new String(ndefRecords[i].getType());
            mesg = ndefRecords[i].getPayload();
            s = new String(mesg);
        }

        s = s.substring(3);

        try {
            PackageInfo p = pacMan.getPackageInfo(packageName, 0);
            packageName = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }

        ndef.close();

//        String path = packageName.toString()+"/files";
        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.example.stucollyn.nfc_play/files/Tag/" + s;
        Log.i("NFC_Tag_Files_Path", path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {

            Log.i("NFC_Tag_Files_Format", files[i].getName());

        }

        filesOnTag = files;
        return filesOnTag;
    }

    private void write(Tag tag) throws IOException, FormatException {

//        String fileToWrite = fileDirectory.getAbsolutePath();
        String fileToWrite = tag_data;
        NdefRecord[] records = {
                createRecord(fileToWrite)
        };
        NdefMessage message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;

        // set status byte (see NDEF spec for actual bits)
        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;

        //copy langbytes and textbytes into payload
        System.arraycopy(textBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);


        return recordNFC;
    }


    public boolean doWrite(Tag mytag, String tag_data) {

        boolean success = false;
        Toast.makeText(context, "Saving story.", Toast.LENGTH_LONG).show();
        this.mytag = mytag;
        this.tag_data = tag_data;

        try {
            if (mytag == null) {
                Toast.makeText(context, "Error Detected", Toast.LENGTH_LONG).show();
                Log.i("TagNull Exception", "");
//                Toast.makeText(this, this.getString(R.string.error_detected), Toast.LENGTH_LONG ).show();
            } else {
                //write(message.getText().toString(),mytag);
                Log.i("Reached", mytag.toString());
                write(mytag);
                success = true;
                Toast.makeText(context, "Story saved to object.", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(context, "Error Detected", Toast.LENGTH_LONG).show();
            Log.i("IOException Exception", "");
            e.printStackTrace();
            success = false;
        } catch (FormatException e) {
            Toast.makeText(context, "Error Detected", Toast.LENGTH_LONG).show();
            Log.i("Format Exception", "");

            e.printStackTrace();
            success = false;
        }

        Log.i("Success Value NFC", String.valueOf(success));


        return success;
    }



    void Complete(boolean complete) {

        if(complete) {

            Toast.makeText(context, "Story saved to object.", Toast.LENGTH_LONG ).show();
            Intent intent = new Intent(context, LoggedInReadHomeKidsUI.class);
            intent.putExtra("PreviousActivity", "LoggedInWriteHomeKidsUI");
            intent.putExtra("Authenticated", authenticated);
            intent.putExtra("NewStory", true);
            intent.putExtra("StoryRef", tag_data);
            context.startActivity(intent);
        }
    }

    void WriteModeOn(NfcAdapter adapter, PendingIntent pendingIntent, IntentFilter writeTagFilters[]){

        adapter.enableForegroundDispatch(activity, pendingIntent, writeTagFilters, null);
    }

    void WriteModeOff(NfcAdapter adapter){
        adapter.disableForegroundDispatch(activity);
    }

    void ReadModeOn(NfcAdapter adapter, PendingIntent pendingIntent, IntentFilter readTagFilters[]){

        adapter.enableForegroundDispatch(activity, pendingIntent, readTagFilters, null);
    }

    void ReadModeOff(NfcAdapter adapter){
        adapter.disableForegroundDispatch(activity);
    }
}
