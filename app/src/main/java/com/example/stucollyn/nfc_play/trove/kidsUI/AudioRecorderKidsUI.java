package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.stucollyn.nfc_play.NFCRecord;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by StuCollyn on 06/06/2018.
 */



public class AudioRecorderKidsUI extends Application {

    private MediaRecorder mRecorder = null;

    //File Save Variables
    private static String audioFileName = null;
    File story_directory;
    private Context context;
    File audioFile;

    public AudioRecorderKidsUI(Context context, File story_directory) {
            this.context=context;
            this.story_directory = story_directory;

    }


    protected void startRecording() throws IOException {

        // Check for permissions
        checkAudioRecordingPermissions();
        createAudioFile();
        setupAudioRecorder();
        mRecorder.start();
    }

    private void checkAudioRecordingPermissions() {

        // Check for permissions
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // If we don't have permissions, ask user for permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((NFCRecord) context, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

        }
    }

    protected void createAudioFile() {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "AudioMPEG4_" + timeStamp + "_";
        UUID storyName = UUID.randomUUID();;
        String imageFileName = storyName.toString();
        File storageDir;

        if (Build.VERSION.SDK_INT >= 19) {
            //storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            storageDir = story_directory;
        }

        else {
            //storageDir = context.getExternalFilesDir(Environment.getExternalStorageDirectory() + "/Documents");
            storageDir = story_directory;
        }

        try {

            audioFile = File.createTempFile(imageFileName, ".mp3", storageDir);

//            Log.i("Files on Tag (Z)", storageDir.toString());


            audioFileName = audioFile.getAbsolutePath();

        }

        catch (IOException e) {

            Log.i("Error", "Audio file creation failed");
        }

    }

    public File getAudioFile() {

        return audioFile;
    }

    protected void setupAudioRecorder() {

        // Save a file: path for use with ACTION_VIEW intents
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(audioFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("Error", "prepare() failed");
        }
    }

    protected void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    protected void DiscardAudio() {

        File file = new File(audioFileName);
        file.delete();
    }

    protected String getAudioFileName() {

        return audioFileName;
    }


}
