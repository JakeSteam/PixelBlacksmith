package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
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
        List<Visitor_Type> visitorTypes = Visitor_Type.listAll(Visitor_Type.class);
        GridLayout visitorGrid = (GridLayout) findViewById(R.id.visitorGrid);
        visitorGrid.setRowCount((visitorTypes.size() / Constants.NUMBER_OF_TROPHY_COLUMNS) + 1);

        for (Visitor_Type visitorType : visitorTypes) {
            Visitor_Stats visitorStats = Visitor_Stats.findById(Visitor_Stats.class, visitorType.getVisitorID());

            // Create image for the visitor.
            ImageView visitorImage = dh.createImageView("visitor", visitorType.getVisitorID(), 170, 170);
            visitorImage.setTag(visitorType.getVisitorID());
            visitorImage.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    loadVisitor(v);
                }
            });
            visitorImage.setPadding(5, 5, 5, 5);

            // Apply colouring based on number of visits.
            int numVisits = visitorStats.getVisits();
            if (numVisits < Constants.VISITS_TROPHY) {
                if (numVisits > Constants.VISITS_ALMOST) {
                    visitorImage.setColorFilter(Color.LTGRAY);
                } else if (numVisits > Constants.VISITS_STARTED) {
                    visitorImage.setColorFilter(Color.GRAY);
                } else if (numVisits > Constants.VISITS_UNSTARTED) {
                    visitorImage.setColorFilter(Color.DKGRAY);
                } else {
                    visitorImage.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
                }
            }

            visitorGrid.addView(visitorImage);
        }
    }

    public void loadVisitor(View v) {
        Long visitorTypeID = (Long) v.getTag();
        Visitor_Type visitorType = Visitor_Type.findById(Visitor_Type.class, visitorTypeID);
        Visitor_Stats visitorStats = Visitor_Stats.findById(Visitor_Stats.class, visitorTypeID);

        if (visitorStats.getVisits() >= Constants.VISITS_TROPHY) {
            int drawableId = this.getResources().getIdentifier("visitor" + visitorTypeID, "drawable", this.getPackageName());
            ((ImageView) findViewById(R.id.visitorPicture)).setImageDrawable(dh.createDrawable(drawableId, 90, 90));
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
            ((TextViewPixel) findViewById(R.id.visitorDesc)).setText(R.string.unknownText);
            ((TextViewPixel) findViewById(R.id.visitorFirstSeen)).setText("First seen: ???");
            ((TextViewPixel) findViewById(R.id.visitor100thVisit)).setText("100th visit: ???");
            ((TextViewPixel) findViewById(R.id.visitorBestItem)).setText("Best item: ???");
        }

        ((TextViewPixel) findViewById(R.id.visitorName)).setText(visitorType.getName());
        ((TextViewPixel) findViewById(R.id.visitorVisits)).setText("Visits: " + visitorStats.getVisits());
    }

    public void closePopup(View view) {
        finish();
    }
}
