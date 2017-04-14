package uk.co.jakelee.blacksmith.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DatabaseHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        final Picasso picasso = new Picasso.Builder(this).build();
        final ImageView imageView = (ImageView) findViewById(R.id.image);
        final Handler h = new Handler();
        h.post(new Runnable() {
            public void run() {
                picasso.load(getRandomItemOrVisitorDrawable())
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(imageView);
                h.postDelayed(this, 1000);
            }
        });

        new DatabaseHelper(this, true).execute();
    }

    private int getRandomItemOrVisitorDrawable() {
        if (VisitorHelper.getRandomBoolean(50)) {
            return DisplayHelper.getItemDrawableID(this, VisitorHelper.getRandomNumber(1, 221));
        } else {
            return DisplayHelper.getVisitorDrawableID(this, VisitorHelper.getRandomNumber(1, 50));
        }
    }

    public void startGame() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
