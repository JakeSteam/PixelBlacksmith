package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import uk.co.jakelee.blacksmith.R;

public class GestureHelper {
    private final Context context;
    private final Animation slide_in_left;
    private final Animation slide_out_right;
    private final Animation slide_in_right;
    private final Animation slide_out_left;

    public GestureHelper(Context context) {
        this.context = context;

        slide_in_left = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
        slide_in_right = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
        slide_out_left = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);
    }

    public void swipe(ViewFlipper viewFlipper, MotionEvent startXY, MotionEvent finishXY) {
        // Check we've actually got some co-ords.
        if (startXY == null || finishXY == null) {
            return;
        }

        // Swipe left (next)
        if (startXY.getX() > finishXY.getX()) {
            viewFlipper.setInAnimation(slide_in_right);
            viewFlipper.setOutAnimation(slide_out_left);
            viewFlipper.showNext();
            SoundHelper.playSound(context, SoundHelper.transitionSounds);
        }

        // Swipe right (previous)
        if (startXY.getX() < finishXY.getX()) {
            viewFlipper.setInAnimation(slide_in_left);
            viewFlipper.setOutAnimation(slide_out_right);
            viewFlipper.showPrevious();
            SoundHelper.playSound(context, SoundHelper.transitionSounds);
        }
    }
}
