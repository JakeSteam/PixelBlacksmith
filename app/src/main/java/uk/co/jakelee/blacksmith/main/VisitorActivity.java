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
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class VisitorActivity extends Activity {
    public static DisplayHelper dh;
    public static Visitor visitor;
    public static Visitor_Type visitorType;
    public static Visitor_Stats visitorStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        dh = DisplayHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        int visitorId = Integer.parseInt(intent.getStringExtra(DisplayHelper.VISITOR_TO_LOAD));

        if (visitorId > 0) {
            visitor = Visitor.findById(Visitor.class, visitorId);
            visitorType = Visitor_Type.findById(Visitor_Type.class, visitor.getType());
            visitorStats = Visitor_Stats.findById(Visitor_Stats.class, visitor.getType());
            createVisitorInterface();
        }
    }

    public void createVisitorInterface() {
        displayVisitorInfo();
        displayVisitorStats();
        displayVisitorDemands();
    }

    public void displayVisitorInfo() {
        int drawableId = getApplicationContext().getResources().getIdentifier("visitor" + visitorType.getVisitorID(), "drawable", getApplicationContext().getPackageName());
        ImageView visitorPicture = (ImageView) findViewById(R.id.visitorPicture);
        visitorPicture.setImageResource(drawableId);

        TextView visitorName = (TextView) findViewById(R.id.visitorName);
        visitorName.setText(visitorType.getName());

        TextView visitorDesc = (TextView) findViewById(R.id.visitorDesc);
        visitorDesc.setText(visitorType.getDesc());

        TextView visitorVisits = (TextView) findViewById(R.id.visitorVisits);
        visitorVisits.setText("Visits: " + Integer.toString(visitorStats.getVisits()));
    }

    public void displayVisitorStats() {
        if (visitorType.isTypeDiscovered()) {
            ImageView typePic = (ImageView) findViewById(R.id.typeImage);
            TextView typeMultiplier = (TextView) findViewById(R.id.typeMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("type" + visitorType.getTypePreferred(), "drawable", getApplicationContext().getPackageName());
            typePic.setImageResource(typeDrawableId);
            typeMultiplier.setText(Double.toString(visitorType.getTypeMultiplier()) + "x");
        }

        if (visitorType.isTierDiscovered()) {
            ImageView tierPic = (ImageView) findViewById(R.id.tierImage);
            TextView tierMultiplier = (TextView) findViewById(R.id.tierMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("tier" + visitorType.getTierPreferred(), "drawable", getApplicationContext().getPackageName());
            tierPic.setImageResource(typeDrawableId);
            tierMultiplier.setText(Double.toString(visitorType.getTierMultiplier()) + "x");
        }

        if (visitorType.isStateDiscovered()) {
            ImageView statePic = (ImageView) findViewById(R.id.stateImage);
            TextView stateMultiplier = (TextView) findViewById(R.id.stateMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("state" + visitorType.getTierPreferred(), "drawable", getApplicationContext().getPackageName());
            statePic.setImageResource(typeDrawableId);
            stateMultiplier.setText(Double.toString(visitorType.getStateMultiplier()) + "x");
        }

        ImageView bestItem = (ImageView) findViewById(R.id.bestItemImage);
        int bestItemDrawableId = getApplicationContext().getResources().getIdentifier("item" + visitorStats.getBestItem(), "drawable", getApplicationContext().getPackageName());
        bestItem.setImageResource(bestItemDrawableId);
    }

    public void displayVisitorDemands() {
        TableLayout demandsTable = (TableLayout) findViewById(R.id.demandsTable);

        // Create header row
        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.addView(dh.createTextView("Status", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("Criteria", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("Trade", 18, Color.BLACK));
        demandsTable.addView(headerRow);

        List<Visitor_Demand> visitorDemands = Visitor_Demand.find(Visitor_Demand.class, "visitor_id = ?", Long.toString(visitorType.getVisitorID()));
        for(Visitor_Demand demand : visitorDemands) {
            TableRow demandRow = new TableRow(getApplicationContext());
            Criteria demandCriteria = Criteria.findById(Criteria.class, demand.getCriteriaType());

            String status = (demand.isDemandFulfilled() ? "Done" : "X");
            TextView criteriaStatus = dh.createTextView(status, 15, Color.BLACK);

            String criteriaText = demandCriteria.getName() + ": " + Visitor_Demand.getCriteriaName(demand);
            TextView criteriaValue = dh.createTextView(criteriaText, 15, Color.BLACK);

            ImageView tradeBtn = new ImageView(getApplicationContext());
            if (!demand.isDemandFulfilled()) {
                tradeBtn.setBackgroundResource(R.drawable.open_shop);
                tradeBtn.setTag(demand.getId());
                tradeBtn.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
                        intent.putExtra(DisplayHelper.DEMAND_TO_LOAD, v.getTag().toString());
                        startActivity(intent);
                    }
                });
            }

            demandRow.addView(criteriaStatus);
            demandRow.addView(criteriaValue);
            demandRow.addView(tradeBtn);
            demandsTable.addView(demandRow);
        }
    }

    public void closeVisitor(View view) {
        finish();
    }
}
