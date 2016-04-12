package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;
import uk.co.jakelee.blacksmith.model.Message;

public class TutorialHelper {

    public static boolean currentlyInTutorial = false;
    private List<ChainTourGuide> tourGuides = new ArrayList<>();

    public void addTutorial(Activity activity, View view, int titleID, int bodyID, boolean clickable) {
        addTutorial(activity, view, activity.getString(titleID), activity.getString(bodyID), Overlay.Style.Circle, clickable, Gravity.CENTER);
    }

    public void addTutorial(Activity activity, View view, int titleID, int bodyID, boolean clickable, int gravity) {
        addTutorial(activity, view, activity.getString(titleID), activity.getString(bodyID), Overlay.Style.Circle, clickable, gravity);
    }

    public void addTutorialNoOverlay(Activity activity, View view, int titleID, int bodyID, boolean clickable) {
        addTutorial(activity, view, activity.getString(titleID), activity.getString(bodyID), Overlay.Style.NoHole, clickable, Gravity.CENTER);
    }

    public void addTutorialRectangle(Activity activity, View view, int titleID, int bodyID, boolean clickable, int gravity) {
        addTutorial(activity, view, activity.getString(titleID), activity.getString(bodyID), Overlay.Style.Rectangle, clickable, gravity);
    }

    public void addTutorialRectangle(Activity activity, View view, int titleID, int bodyID, boolean clickable) {
        addTutorial(activity, view, activity.getString(titleID), activity.getString(bodyID), Overlay.Style.Rectangle, clickable, Gravity.CENTER);
    }

    public void addTutorial(Activity activity, View view, String title, String body, Overlay.Style style, boolean clickable, int gravity) {
        if (view == null) {
            return;
        }
        tourGuides.add(ChainTourGuide.init(activity)
                .with(TourGuide.Technique.Click)
                .setToolTip(new ToolTip()
                        .setTitle(title)
                        .setDescription(body)
                        .setGravity(gravity))
                .setOverlay(new Overlay()
                        .disableClick(true)
                        .disableClickThroughHole(!clickable)
                        .setStyle(style))
                .playLater(view));

        Message.add(body);
    }

    public void start(Activity activity) {
        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuides.toArray(new ChainTourGuide[tourGuides.size()]))
                .setDefaultOverlay(new Overlay())
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        ChainTourGuide.init(activity).playInSequence(sequence);
    }
}
