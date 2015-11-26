package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MineActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
    }

    public void closePopup(View view) {
        finish();
    }
}
