package com.example.stucollyn.nfc_play;

/**
 * Created by StuCollyn on 24/07/2018.
 */

public class StoryRecord {

    String StoryID;
    String StoryName;
    String StoryDate;
    String URLLink;
    String linkedText;


    public StoryRecord(String StoryID, String StoryName, String StoryDate, String URLLink) {

        this.StoryID = StoryID;
        this.StoryName = StoryName;
        this.StoryDate = StoryDate;
        this.URLLink = URLLink;

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

    public String getURLLink() {

        return  URLLink;
    }

}
