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

        tutorialModels.add(new TutorialModel("https://www.heart.org/en/health-topics/high-blood-pressure/changes-you-can-make-to-manage-high-blood-pressure/limiting-alcohol-to-manage-high-blood-pressure"," • Limit Alcohol ","Alcohol can raise your blood pressure. If you drink, you should limit your alcohol consumption to no more than 2 drinks per day in men and 1 drink per day in females. Click here for more info. ","",false));
        tutorialModels.add(new TutorialModel("https://www.heart.org/-/media/data-import/downloadables/pe-abh-why-should-i-be-physically-active-ucm_300469.pdf"," •  Stay Physically Active","The American Heart Association recommends that adults get at least 150 minutes of moderate-intesnity physical activity each week (30 minutes on most days of the week). More information about exercise can be found here ","",false));
        tutorialModels.add(new TutorialModel("https://www.heart.org/en/health-topics/high-blood-pressure/changes-you-can-make-to-manage-high-blood-pressure/shaking-the-salt-habit-to-lower-high-blood-pressure"," •  Eat a diet that is low in salt","Avoiding foods high in sodium can help lower your blood pressure. One way to cut back is to avoid table salt. However, most of the sodium in our diets come from packaged, processed foods.s like soups, tomato sauce, condiments and canned goods. More information about reducing sodium in your diet can be found here. ","",false));
        tutorialModels.add(new TutorialModel("https://www.heart.org/en/health-topics/high-blood-pressure/changes-you-can-make-to-manage-high-blood-pressure/managing-weight-to-control-high-blood-pressure"," •  Maintain a Healthy Weight","Losing as little as 5 to 10 pounds may help lower your blood pressure. More information about managing weight to control high blood pressure can be found here. ","",false));
        tutorialModels.add(new TutorialModel("https://www.heart.org/en/health-topics/high-blood-pressure/changes-you-can-make-to-manage-high-blood-pressure/managing-high-blood-pressure-medications"," •  Take your medications properly","Your doctor may determine that you need prescription medication in addition to lifestyle changes to control high blood pressure. More information about about managing high blood pressure can be found here","",false));
        tutorialModels.add(new TutorialModel("https://www.heart.org/en/health-topics/high-blood-pressure/changes-you-can-make-to-manage-high-blood-pressure/types-of-blood-pressure-medications"," • Types of Blood Pressure Medications","Many blood pressure medications, known as antihypertensives, are available by prescription to lower high blood pressure (HBP or hypertension). There are a variety of classes of high blood pressure medications and they include a number of different drugs. More information about types of blood pressure medications can be found here","",false));

        return tutorialModels;
    }
}
