package uk.co.jakelee.blacksmith.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tried creating toast, and tried running on UI thread. Don't trigger until main activity is ready...

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
