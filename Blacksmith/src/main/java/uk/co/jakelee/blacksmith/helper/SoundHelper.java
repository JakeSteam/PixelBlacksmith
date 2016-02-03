package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.Random;

import uk.co.jakelee.blacksmith.R;

public class SoundHelper {
    public static int[] enchantingSounds = {R.raw.enchant1};
    public static int[] sellingSounds = {R.raw.smith1, R.raw.smith2, R.raw.smith3};
    public static int[] smithingSounds = {R.raw.smith1, R.raw.smith2, R.raw.smith3};

    // If an array is passed, pick one at random to play.
    public static void playSound(Context context, int[] sounds) {
        int soundID = sounds[new Random().nextInt(sounds.length)];
        playSound(context, soundID);
    }

    public static void playSound(Context context, int soundID) {
        MediaPlayer mp = MediaPlayer.create(context, soundID);
        mp.start();
    }
}