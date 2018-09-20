package com.example.stucollyn.nfc_play.trove.kidsUI;

import java.io.Serializable;

/**
 * Created by StuCollyn on 24/07/2018.
 */

public class ObjectStoryRecordKidsUI implements Serializable {

    String ObjectName;
    String StoryName;
    String StoryDate;
    String StoryRef;
    String StoryType;
    String CoverImage;
    String linkedText;
    String ObjectContext;


    public ObjectStoryRecordKidsUI(String ObjectName, String StoryName, String StoryDate, String StoryRef, String StoryType, String CoverImage, String ObjectContext) {

        this.ObjectName = ObjectName;
        this.StoryName = StoryName;
        this.StoryDate = StoryDate;
        this.StoryRef = StoryRef;
        this.StoryType = StoryType;
        this.CoverImage = CoverImage;
        this.ObjectContext = ObjectContext;
    }

    public String isCoverImage() {

        return CoverImage;
    }

    public String getObjectName() {

        return ObjectName;
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

    public String getObjectContext() {

        return  ObjectContext;
    }
}
