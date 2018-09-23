package com.example.stucollyn.nfc_play.trove.kidsUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
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
    ImageView[] imageButtons;
    TextView[] imageDesc;
    int numberOfThumbs = 0;
    boolean imageButtonSelected = false;
    Activity storyGallery;
    LinkedHashMap<String, ArrayList<File>> filesOnTag;
    ArrayList<Bitmap> coverImages;
    int[] colourCode;
    HashMap<String, Bitmap> imageMap;
    LinkedHashMap<String, ArrayList<ObjectStoryRecordKidsUI>> folderToImageRef;
    private List<String> elements;


    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;

        public SimpleViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.grid_item_background_kids_ui);
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.activity_story_gallery_grid_item_kids_ui, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {

        int currentColour = colourCode[position];

        if(extension.equalsIgnoreCase("mp3")) {

            valueButton.setImageResource(R.drawable.audio_media);
            callActivityName = "ReviewAudioStory";
        }

        else if(extension.equalsIgnoreCase("mp4")) {

            valueButton.setImageResource(R.drawable.video_media);
            callActivityName = "ReviewVideoStory";
        }

        else if (extension.equalsIgnoreCase("txt")) {

            valueButton.setImageResource(R.drawable.written_media);
            callActivityName = "ReviewWrittenStory";
        }

        else if(extension.equalsIgnoreCase("jpg")) {

            valueButton.setImageResource(R.drawable.camera_media);
            callActivityName = "ReviewPictureStory";
        }





        if(!coverImages.isEmpty()) {

//            File value = folderToImageRef.get(filesOnTag.get(position));
            Bitmap bitmap = coverImages.get(position);
//            imageButtons[position].setImageBitmap(bitmap);
            holder.imageView.setImageBitmap(bitmap);
        }

        holder.imageView.setBackgroundColor(currentColour);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, "Position =" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(storyGallery.getApplicationContext(), ArchiveKidsUI.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ObjectStoryRecord", folderToImageRef);
                storyGallery.getApplicationContext().startActivity(intent);
                storyGallery.overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.elements.size();
    }


    public ExploreImageAdapterKidsUI(Activity storyGallery, Context c, int numberOfThumbs, LinkedHashMap<String, File> filesOnTag, int[] colourCode, LinkedHashMap<String, ArrayList<ObjectStoryRecordKidsUI>> folderToImageRef, LinkedHashMap<String, Bitmap> imageMap) {

        this.storyGallery = storyGallery;
        mContext = c;
        this.numberOfThumbs = numberOfThumbs;
        imageButtons = new ImageView[numberOfThumbs];
        imageDesc = new TextView[numberOfThumbs];
        this.filesOnTag = filesOnTag;
        this.colourCode = colourCode;
        this.imageMap = imageMap;
        this.folderToImageRef = folderToImageRef;

        this.elements = new ArrayList<String>();

        // Fill dummy list
        for(int i = 0; i < imageMap.size(); i++) {
            this.elements.add(i, "Position : " + i);
        }

        coverImages = new ArrayList<Bitmap>();
        
        for (Map.Entry<String, Bitmap> entry : imageMap.entrySet()) {
            String key = entry.getKey();
            Bitmap value = entry.getValue();
            coverImages.add(value);
        }
    }
}