package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stucollyn.nfc_play.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by StuCollyn on 07/06/2018.
 */

public class ExploreImageAdapterKidsUI extends  RecyclerView.Adapter<ExploreImageAdapterKidsUI.SimpleViewHolder> {
    private Context mContext;
    CustomImageView[] imageButtons;
    TextView[] imageDesc;
    int numberOfThumbs = 0;
    boolean imageButtonSelected = false;
    Activity storyGallery;
    LinkedHashMap<String, File> filesOnTag;
    ArrayList<Bitmap> coverImages;
    ArrayList<String> storyType;
    ArrayList<File> storyFile;
    int[] colourCode;
    HashMap<String, Bitmap> imageMap;
    HashMap<String, ArrayList<ObjectStoryRecordKidsUI>> folderToImageRef;
    LinkedHashMap<String, ArrayList<ObjectStoryRecordKidsUI>> storyTypeMap;
    private List<String> elements;
    private MediaPlayer mPlayer = null;
    ShowStoryContent showStoryContent;
    int[] shapeResource = new int[]{R.raw.archive_shape_1, R.raw.archive_shape_2, R.raw.archive_shape_1};
    int[] shapeResourceBackground = new int[]{R.drawable.kids_ui_archive_shape_1, R.drawable.kids_ui_archive_shape_2, R.drawable.kids_ui_archive_shape_1};
    int shapeResourceCounter=0;
    CommentaryInstruction commentaryInstruction;


    boolean record_button_on, video_record_button_on, recordingStatus = false,
            playbackStatus = false, mPlayerSetup = false, fullSizedPicture = false,
            permissionToRecordAccepted = false, isFullSizedVideo = false;


    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final CustomImageView imageView;
        public final ImageView imageViewBackground;

        public SimpleViewHolder(View view) {
            super(view);
            imageView = (CustomImageView) view.findViewById(R.id.grid_item_kids_ui);
            imageViewBackground = (ImageView) view.findViewById(R.id.grid_item_background_kids_ui);
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.activity_story_gallery_grid_item_kids_ui, parent, false);
        return new SimpleViewHolder(view);
    }

    void PlayFile(int position) {

        File[] filesOnTag;
        filesOnTag = new File[1];
        filesOnTag[0] = storyFile.get(position);

        ShowStoryContent showStoryContent = new ShowStoryContent(mPlayer, mContext, storyGallery, filesOnTag);
        showStoryContent.checkFilesOnTag();
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {

        int currentColour = colourCode[position];

        if(!coverImages.isEmpty()) {

            holder.imageViewBackground.setBackgroundResource(shapeResourceBackground[shapeResourceCounter]);
            holder.imageView.setCustomImageResource(shapeResource[shapeResourceCounter]);


            if(storyType.get(position).equals("PictureFile")) {

                Bitmap bitmap = coverImages.get(position);
                holder.imageView.setImageBitmap(bitmap);
            }

            else if(storyType.get(position).equals("AudioFile")) {

                holder.imageView.setImageResource(R.drawable.audio_media);
            }

        }

//        holder.imageView.setBackgroundColor(currentColour);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
////                Toast.makeText(mContext, "Position =" + position, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(storyGallery.getApplicationContext(), ArchiveKidsUI.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("ObjectStoryRecord", folderToImageRef);
//                storyGallery.getApplicationContext().startActivity(intent);
//                storyGallery.overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
                commentaryInstruction.stopPlaying();
        PlayFile(position);
            }
        });

        if(shapeResourceCounter<2) {
            shapeResourceCounter++;
        }

        else {
            shapeResourceCounter=0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.elements.size();
    }


    public ExploreImageAdapterKidsUI(Activity storyGallery, Context c, int numberOfThumbs, LinkedHashMap<String, File> filesOnTag, int[] colourCode,
                                     HashMap<String, ArrayList<ObjectStoryRecordKidsUI>> folderToImageRef, LinkedHashMap<String, Bitmap> imageMap,
                                     LinkedHashMap<String, String> storyTypeMap, CommentaryInstruction commentaryInstruction) {

        this.storyGallery = storyGallery;
        mContext = c;
        this.numberOfThumbs = numberOfThumbs;
        imageButtons = new CustomImageView[numberOfThumbs];
        imageDesc = new TextView[numberOfThumbs];
        this.filesOnTag = filesOnTag;
        this.colourCode = colourCode;
        this.imageMap = imageMap;
        this.folderToImageRef = folderToImageRef;
        this.commentaryInstruction = commentaryInstruction;

        this.elements = new ArrayList<String>();

        mPlayer = new MediaPlayer();


        // Fill dummy list
        for(int i = 0; i < imageMap.size(); i++) {
            this.elements.add(i, "Position : " + i);
        }

        coverImages = new ArrayList<Bitmap>();
        storyType = new ArrayList<String>();
        storyFile = new ArrayList<File>();


        /*For each object name add save cover image, file, and type */

        for (Map.Entry<String, Bitmap> entry : imageMap.entrySet()) {
            String key = entry.getKey();
            Bitmap value = entry.getValue();
            coverImages.add(value);
            storyFile.add(filesOnTag.get(key));
            storyType.add(storyTypeMap.get(key));
        }
    }
}