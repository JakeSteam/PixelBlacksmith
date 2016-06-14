package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class VisitorSelectActivity extends Activity {
    private static DisplayHelper dh;
    private Hero hero;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitorselect);
        dh = DisplayHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        hero = Hero.findById(intent.getIntExtra(WorkerHelper.INTENT_HERO, 0));
    }

    @Override
    public void onResume() {
        super.onResume();
        dh.updateFullscreen(this);

        populateVisitors();
    }

    private void populateVisitors() {
        TableLayout visitorTable = (TableLayout) findViewById(R.id.visitorTable);
        visitorTable.removeAllViews();
        List<Visitor_Type> allVisitors = Visitor_Type.listAll(Visitor_Type.class);

        for (Visitor_Type visitor : allVisitors) {
            // Inflate a layout

            itemRow.setTag(R.id.visitor, visitor.getVisitorID());
            itemRow.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    visitorClick(v);
                }
            });
            equipmentTable.addView(itemRow);
        }
    }

    public void visitorClick(View view) {
        int visitorId = (int) view.getTag(R.id.visitor);
        hero.setVisitorId(visitorId);
        hero.save();

        finish();
    }


    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Hero_Visitors);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
