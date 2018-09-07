package com.example.stucollyn.nfc_play.trove.kidsUI;

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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stucollyn.nfc_play.R;
import com.example.stucollyn.nfc_play.SavedStoryConfirmation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class NFCWrite {

    String action;
    String themessage;
    String owner;
    String type;
    String id;
    String colour;
    TextView instruction;
    static String display;
    boolean success = true;
    Tag mytag;
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
   // File fileDirectory;
    String tag_data = "";
    int mode;
    Context context;
    Activity activity;

    public NFCWrite(Context context, Activity activity) {

        this.context = context;
        this.activity = activity;


        adapter = NfcAdapter.getDefaultAdapter(context);
        pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
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
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;

        // set status byte (see NDEF spec for actual bits)
        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;

        //copy langbytes and textbytes into payload
        System.arraycopy(textBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);


        return recordNFC;
    }

    public void doWrite(Tag mytag, String tag_data){

        this.mytag = mytag;
        this.tag_data = tag_data;

        try {
            if(mytag==null){
                Toast.makeText(context, "Error Detected", Toast.LENGTH_LONG ).show();
//                Toast.makeText(this, this.getString(R.string.error_detected), Toast.LENGTH_LONG ).show();
                success = false;
            }else{
                //write(message.getText().toString(),mytag);
                write(mytag);
                Toast.makeText(context, "Okay Writing", Toast.LENGTH_LONG ).show();
            }
        } catch (IOException e) {
            Toast.makeText(context, "Error Detected", Toast.LENGTH_LONG ).show();
            e.printStackTrace();
            success = false;
        } catch (FormatException e) {
            Toast.makeText(context, "Error Detected", Toast.LENGTH_LONG ).show();
            e.printStackTrace();
            success = false;
        }

        if(success) {

            Toast.makeText(context, "NFC write successful", Toast.LENGTH_LONG ).show();

        }

        else {

//            writeinstruction.setText("Error writing object. Please rescan.");
        }
    }

    private void WriteModeOn(){
//        writeMode = true;
        adapter.enableForegroundDispatch(activity, pendingIntent, writeTagFilters, null);
    }

    private void WriteModeOff(){
//        writeMode = false;
        adapter.disableForegroundDispatch(activity);
    }
}
