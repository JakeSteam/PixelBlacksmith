package uk.co.jakelee.blacksmith.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import uk.co.jakelee.blacksmith.R;

public class MusicService extends Service {
    private MediaPlayer player;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        try {
            player = MediaPlayer.create(this, R.raw.music1);
            if (player != null) {
                    player.setLooping(true);
                    player.setVolume(volume, volume);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            player.start();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }
}