package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;

import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.Utils;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.github.jinatonic.confetti.confetto.Confetto;

import java.util.Calendar;
import java.util.Random;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.model.Setting;

public class ParticleHelper {
    public static int MANY = 200;
    public static int SOME = 75;
    public static int FEW = 20;
    private static ParticleHelper phInstance = null;
    private ConfettoGenerator confettoGenerator = null;
    private DisplayHelper displayHelper = null;
    private ParticleHelper(final Context context) {
        if (Setting.getSafeBoolean(Constants.SETTING_SEASONAL_EFFECTS)) {
            // Christmas
            if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER) {
                confettoGenerator = new ConfettoGenerator() {
                    @Override
                    public Confetto generateConfetto(Random random) {
                        //return new BitmapConfetto(Utils.createCircleBitmap(Color.WHITE, 10));
                        return new BitmapConfetto(BitmapFactory.decodeResource(context.getResources(), R.drawable.snowflake));
                    }
                };
            }
        }
    }

    public static ParticleHelper getInstance(Context context) {
        if (phInstance == null) {
            phInstance = new ParticleHelper(context);
        }
        return phInstance;
    }

    public void triggerExplosion(ViewGroup container, View v, int quantity) {
        if (confettoGenerator != null) {
            int coordX = v.getLeft() + (v.getWidth() / 2);
            int coordY = ((View) v.getParent()).getTop() + (v.getHeight() / 2);
            ConfettiSource confettiSource = new ConfettiSource(coordX, coordY);
            ConfettiManager confettiManager = new ConfettiManager(v.getContext(), confettoGenerator, confettiSource, container);
            confettiManager.setNumInitialCount(quantity)
                    .setEmissionDuration(0)
                    .setVelocityX(0, 600)
                    .setVelocityY(400, 500)
                    .setAccelerationX(800, 300)
                    .enableFadeOut(Utils.getDefaultAlphaInterpolator())
                    .setInitialRotation(0, 180)
                    .setRotationalAcceleration(0, 180)
                    .animate();
        }
    }
}
