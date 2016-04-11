package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.view.View;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class TutorialHelper {
    public ChainTourGuide mTourGuideHandler;

    public ChainTourGuide createTutorial(Activity activity, View view, int titleID, int bodyID) {
        return createTutorial(activity, view, activity.getString(titleID), activity.getString(bodyID));
    }

    public ChainTourGuide createTutorial(Activity activity, View view, String title, String body) {
        return ChainTourGuide.init(activity)
                .with(TourGuide.Technique.Click)
                .setToolTip(new ToolTip().setTitle(title).setDescription(body))
                .setOverlay(new Overlay())
                .playLater(view);
    }

    public void finishUp (Activity activity, ChainTourGuide view1, ChainTourGuide view2) {
        Sequence sequence = new Sequence.SequenceBuilder()
                .add(view1, view2)
                .setDefaultOverlay(new Overlay())
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();


        ChainTourGuide.init(activity).playInSequence(sequence);
    }
}
