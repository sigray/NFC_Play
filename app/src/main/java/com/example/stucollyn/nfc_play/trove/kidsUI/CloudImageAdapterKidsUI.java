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

public class CloudImageAdapterKidsUI extends  RecyclerView.Adapter<CloudImageAdapterKidsUI.SimpleViewHolder> {
    private Context mContext;
    CustomImageView[] imageButtons;
    TextView[] imageDesc;
    int numberOfThumbs = 0;
    boolean imageButtonSelected = false;
    Activity storyGallery;
    LinkedHashMap<String, ArrayList<File>> filesOnTag;
    ArrayList<Bitmap> coverImages;
    ArrayList<String> objectName;
    int[] colourCode;
    HashMap<String, Bitmap> imageMap;
    LinkedHashMap<String, ArrayList<ObjectStoryRecord>> folderToImageRef;
    private List<String> elements;
    boolean authenticated = false;
    int[] shapeResource = new int[]{R.raw.archive_shape_1, R.raw.archive_shape_2, R.raw.archive_shape_1};
    int[] shapeResourceBackground = new int[]{R.drawable.kids_ui_archive_shape_1, R.drawable.kids_ui_archive_shape_2, R.drawable.kids_ui_archive_shape_1};
    int shapeResourceCounter=0;
    CommentaryInstruction commentaryInstruction;

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

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {

        int currentColour = colourCode[position];

        if(!coverImages.isEmpty()) {

//            File value = folderToImageRef.get(filesOnTag.get(position));
            Bitmap bitmap = coverImages.get(position);
//            imageButtons[position].setImageBitmap(bitmap);
            holder.imageViewBackground.setBackgroundResource(shapeResourceBackground[shapeResourceCounter]);
//            holder.imageViewBackground.getLayoutParams().width = holder.imageView.getWidth()+100;
//            holder.imageViewBackground.getLayoutParams().height = holder.imageView.getHeight()+100;
            holder.imageView.setCustomImageResource(shapeResource[shapeResourceCounter]);
            holder.imageView.setImageBitmap(bitmap);
        }

//        holder.imageView.setBackgroundColor(currentColour);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, "Position =" + position, Toast.LENGTH_SHORT).show();
                commentaryInstruction.stopPlaying();
                holder.imageView.setClickable(false);
                Intent intent = new Intent(storyGallery.getApplicationContext(), ExploreArchiveItem.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ObjectName", objectName.get(position));
                intent.putExtra("ObjectStoryRecord", folderToImageRef);
                intent.putExtra("Authenticated", authenticated);
                storyGallery.getApplicationContext().startActivity(intent);
                storyGallery.overridePendingTransition(R.anim.splash_screen_fade_in, R.anim.full_fade_out);
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

    public CloudImageAdapterKidsUI(Activity storyGallery, Context c, int numberOfThumbs, LinkedHashMap<String, ArrayList<File>> filesOnTag, int[] colourCode, LinkedHashMap<String, ArrayList<ObjectStoryRecord>> folderToImageRef, HashMap<String, Bitmap> imageMap, boolean authenticated, CommentaryInstruction commentaryInstruction) {

        this.storyGallery = storyGallery;
        mContext = c;
        this.numberOfThumbs = numberOfThumbs;
        imageButtons = new CustomImageView[numberOfThumbs];
        imageDesc = new TextView[numberOfThumbs];
        this.filesOnTag = filesOnTag;
        this.colourCode = colourCode;
        this.imageMap = imageMap;
        this.folderToImageRef = folderToImageRef;
        this.authenticated = authenticated;
        this.commentaryInstruction = commentaryInstruction;

        this.elements = new ArrayList<String>();

        // Fill dummy list
        for(int i = 0; i < imageMap.size(); i++) {
            this.elements.add(i, "Position : " + i);
        }

        coverImages = new ArrayList<Bitmap>();
        objectName = new ArrayList<String>();

        for (Map.Entry<String, Bitmap> entry : imageMap.entrySet()) {
            String key = entry.getKey();
            Bitmap value = entry.getValue();
            coverImages.add(value);
            objectName.add(key);
        }
    }
}