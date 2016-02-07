package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class TrophyActivity extends Activity {
    public static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy);
        dh = DisplayHelper.getInstance(getApplicationContext());

        populateVisitorGrid();
    }

    public void populateVisitorGrid() {

    }

    public void loadVisitor(View v) {
        Long visitorTypeID = Long.parseLong((String) v.getTag());
        Visitor_Type visitorType = Visitor_Type.findById(Visitor_Type.class, visitorTypeID);
        Visitor_Stats visitorStats = Visitor_Stats.findById(Visitor_Stats.class, visitorTypeID);

        if (visitorStats.getVisits() > 0) {
            int drawableId = this.getResources().getIdentifier("visitor" + visitorTypeID, "drawable", this.getPackageName());
            ((ImageView) findViewById(R.id.visitorPicture)).setImageDrawable(dh.createDrawable(drawableId, 90, 90));
            ((TextViewPixel) findViewById(R.id.visitorVisits)).setText("Visits: " + visitorStats.getVisits());
            ((TextViewPixel) findViewById(R.id.visitorName)).setText(visitorType.getName());
            ((TextViewPixel) findViewById(R.id.visitorDesc)).setText(visitorType.getDesc());

            String firstSeenText = DateHelper.displayTime(visitorStats.getFirstSeen(), DateHelper.date);
            ((TextViewPixel) findViewById(R.id.visitorFirstSeen)).setText("First seen: " + firstSeenText);

            String trophyAchievedText = DateHelper.displayTime(visitorStats.getTrophyAchieved(), DateHelper.date);
            ((TextViewPixel) findViewById(R.id.visitor100thVisit)).setText("100th visit: " + trophyAchievedText);

            Item bestItem = Item.findById(Item.class, visitorStats.getBestItem());
            String bestItemText = bestItem.getPrefix(visitorStats.getBestItemState()) + bestItem.getName();
            ((TextViewPixel) findViewById(R.id.visitorBestItem)).setText("Best item: " + bestItemText);
        } else {
            ((ImageView) findViewById(R.id.visitorPicture)).setImageDrawable(new ColorDrawable(Color.BLACK));
            ((TextViewPixel) findViewById(R.id.visitorVisits)).setText(R.string.unknownText);
            ((TextViewPixel) findViewById(R.id.visitorName)).setText(R.string.unknownText);
            ((TextViewPixel) findViewById(R.id.visitorDesc)).setText(R.string.unknownText);
            ((TextViewPixel) findViewById(R.id.visitorFirstSeen)).setText("First seen: ???");
            ((TextViewPixel) findViewById(R.id.visitor100thVisit)).setText("100th visit: ???");
        }


    }

    public void closePopup(View view) {
        finish();
    }
}
