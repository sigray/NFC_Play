package com.example.stucollyn.nfc_play.trove.kidsUI;

import java.io.Serializable;

/**
 * Created by StuCollyn on 24/07/2018.
 */

public class ObjectStoryRecordKidsUI implements Serializable {

    String StoryID;
    String StoryName;
    String StoryDate;
    String StoryRef;
    String StoryType;
    String CoverImage;
    String linkedText;


    public ObjectStoryRecordKidsUI(String StoryID, String StoryName, String StoryDate, String StoryRef, String StoryType, String CoverImage) {

        this.StoryID = StoryID;
        this.StoryName = StoryName;
        this.StoryDate = StoryDate;
        this.StoryRef = StoryRef;
        this.StoryType = StoryType;
        this.CoverImage = CoverImage;
    }

    public String isCoverImage() {

        return CoverImage;
    }

    public String getStoryID() {

        return  StoryID;
    }

    public String getStoryName() {

        return  StoryName;
    }

    public String getStoryDate() {

        return  StoryDate;
    }

    public String getStoryRef() {

        return  StoryRef;
    }

    public String getStoryType() {

        return  StoryType;
    }
}
