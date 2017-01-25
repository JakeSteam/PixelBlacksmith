package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

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
    public static ChainTourGuide chainTourGuide;
    private Activity activity;
    public static int currentStage;
    private final List<ChainTourGuide> tourGuides = new ArrayList<>();
    private final Animation enterAnimation = new AlphaAnimation(0f, 1f);

    public TutorialHelper(Activity activity, int stage) {
        currentStage = stage;
        this.activity = activity;
        tourGuides.clear();

        enterAnimation.setDuration(300);
        enterAnimation.setFillAfter(false);
    }

    public void addTutorial(View view, int titleID, int bodyID, boolean clickable) {
        addTutorial(view, activity.getString(titleID), activity.getString(bodyID), Overlay.Style.Circle, clickable, Gravity.CENTER);
    }

    public void addTutorial(View view, int titleID, int bodyID, boolean clickable, int gravity) {
        addTutorial(view, activity.getString(titleID), activity.getString(bodyID), Overlay.Style.Circle, clickable, gravity);
    }

    public void addTutorialNoOverlay(View view, int titleID, int bodyID, boolean clickable) {
        addTutorial(view, activity.getString(titleID), activity.getString(bodyID), Overlay.Style.NoHole, clickable, Gravity.CENTER);
    }

    public void addTutorialNoOverlay(View view, int titleID, int bodyID, boolean clickable, int gravity) {
        addTutorial(view, activity.getString(titleID), activity.getString(bodyID), Overlay.Style.NoHole, clickable, gravity);
    }

    public void addTutorialRectangle(View view, int titleID, int bodyID, boolean clickable, int gravity) {
        addTutorial(view, activity.getString(titleID), activity.getString(bodyID), Overlay.Style.Rectangle, clickable, gravity);
    }

    public void addTutorialRectangle(View view, int titleID, int bodyID, boolean clickable) {
        addTutorial(view, activity.getString(titleID), activity.getString(bodyID), Overlay.Style.Rectangle, clickable, Gravity.CENTER);
    }

    private void addTutorial(View view, String title, String body, Overlay.Style style, boolean clickable, int gravity) {
        if (view == null) {
            return;
        }
        tourGuides.add(ChainTourGuide.init(activity)
                .with(TourGuide.Technique.Click)
                .setToolTip(new ToolTip()
                        .setTitle(title)
                        .setDescription(body)
                        .setGravity(gravity)
                        .setBackgroundColor(Color.parseColor("#AAae6c37"))
                        .setShadow(true)
                        .setEnterAnimation(enterAnimation))
                .setOverlay(new Overlay()
                        .disableClick(true)
                        .disableClickThroughHole(!clickable)
                        .setStyle(style))
                .playLater(view));

        Message.add(body);
    }

    public void start() {
        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuides.toArray(new ChainTourGuide[tourGuides.size()]))
                .setDefaultOverlay(new Overlay())
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        chainTourGuide = ChainTourGuide
                .init(activity)
                .playInSequence(sequence);
    }
}
