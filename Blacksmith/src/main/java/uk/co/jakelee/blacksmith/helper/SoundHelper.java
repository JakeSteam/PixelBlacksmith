package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.Random;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.model.Setting;

public class SoundHelper {
    public static final int[] enchantingSounds = {R.raw.enchant1};
    public static final int[] sellingSounds = {R.raw.sell1, R.raw.sell2};
    public static final int[] smithingSounds = {R.raw.smith1, R.raw.smith2, R.raw.smith3};
    public static final int[] walkingSounds = {R.raw.footsteps1};
    public static final int[] transitionSounds = {R.raw.slide1, R.raw.slide2, R.raw.slide3};

    // If an array is passed, pick one at random to play.
    public static void playSound(Context context, int[] sounds) {
        int soundID = sounds[new Random().nextInt(sounds.length)];
        playSound(context, soundID);
    }

    private static void playSound(Context context, int soundID) {
        // Only play if the user has sounds enabled.
        if (Setting.getSafeBoolean(Constants.SETTING_SOUNDS)) {
            try {
                MediaPlayer mediaPlayer = MediaPlayer.create(context, soundID);
                mediaPlayer.start();
            } catch (Exception e) {
                Log.d("Blacksmith", e.toString());
            }
        }
    }
}