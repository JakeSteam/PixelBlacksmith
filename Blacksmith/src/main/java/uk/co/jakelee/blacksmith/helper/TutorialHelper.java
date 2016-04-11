package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.view.View;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class TutorialHelper {
    public static void createTutorial(Activity activity, View view, int titleID, int bodyID) {
        createTutorial(activity, view, activity.getString(titleID), activity.getString(bodyID));
    }

    public static void createTutorial(Activity activity, View view, String title, String body) {
        final TourGuide handler = TourGuide.init(activity)
                .with(TourGuide.Technique.Click)
                .setToolTip(new ToolTip().setTitle(title).setDescription(body))
                .setOverlay(new Overlay())
                .playOn(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (handler != null) {
                    handler.cleanUp();
                }
            }
        });
    }
}
