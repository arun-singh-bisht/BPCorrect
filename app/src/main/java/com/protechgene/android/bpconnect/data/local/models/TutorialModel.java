package com.protechgene.android.bpconnect.data.local.models;

import java.util.ArrayList;
import java.util.List;

public class TutorialModel {

    String videoUrl;
    String title;
    String description;
    String releaseTime;
    boolean isWatched;

    public TutorialModel(String videoUrl, String title, String description, String releaseTime, boolean isWatched) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.description = description;
        this.releaseTime = releaseTime;
        this.isWatched = isWatched;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public boolean isWatched() {
        return isWatched;
    }

    public static List<TutorialModel> getData()
    {
        List<TutorialModel> tutorialModels = new ArrayList<>();
        for (int i =1;i<11;i++)
        {
            tutorialModels.add(new TutorialModel("","","","",false));
        }
        return tutorialModels;
    }
}
