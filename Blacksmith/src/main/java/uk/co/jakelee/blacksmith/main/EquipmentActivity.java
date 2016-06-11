package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Hero;

public class EquipmentActivity extends Activity {
    private static DisplayHelper dh;
    private Hero hero;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);
        dh = DisplayHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        int heroId = intent.getIntExtra(WorkerHelper.INTENT_ID, 0);
        hero = Hero.findById(Hero.class, heroId);
    }

    @Override
    public void onResume() {
        super.onResume();
        dh.updateFullscreen(this);

        populateEquipment();
    }

    private void populateEquipment() {
        ((TextViewPixel) findViewById(R.id.totalStrength)).setText(String.format(getString(R.string.heroTotalStrength), 999));

        // Find all equipment slots and: display image, set tag, set onclick

    }


    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Hero_Equipment);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
