package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class TutorialHelper {
    private List<ChainTourGuide> tourGuides = new ArrayList<>();

    public void addTutorial(Activity activity, View view, int titleID, int bodyID) {
        addTutorial(activity, view, activity.getString(titleID), activity.getString(bodyID));
    }

    public void addTutorial(Activity activity, View view, String title, String body) {
        tourGuides.add(ChainTourGuide.init(activity)
                .with(TourGuide.Technique.Click)
                .setToolTip(new ToolTip().setTitle(title).setDescription(body))
                .setOverlay(new Overlay())
                .playLater(view));
    }

    public void start (Activity activity) {
        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuides.toArray(new ChainTourGuide[tourGuides.size()]))
                .setDefaultOverlay(new Overlay())
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        ChainTourGuide.init(activity).playInSequence(sequence);
    }
}
