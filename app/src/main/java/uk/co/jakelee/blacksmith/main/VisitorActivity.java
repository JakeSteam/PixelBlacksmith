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

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
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
        Long visitorId = Long.parseLong(intent.getStringExtra(DisplayHelper.VISITOR_TO_LOAD));

        if (visitorId > 0) {
            visitor = Visitor.findById(Visitor.class, visitorId);
            visitorType = Visitor_Type.findById(Visitor_Type.class, visitor.getType());
            visitorStats = Visitor_Stats.findById(Visitor_Stats.class, visitor.getType());
            createVisitorInterface();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayVisitorStats();
        displayVisitorDemands();
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

        TextViewPixel visitorName = (TextViewPixel) findViewById(R.id.visitorName);
        visitorName.setText(visitorType.getName());

        TextViewPixel visitorDesc = (TextViewPixel) findViewById(R.id.visitorDesc);
        visitorDesc.setText(visitorType.getDesc());

        TextViewPixel visitorVisits = (TextViewPixel) findViewById(R.id.visitorVisits);
        visitorVisits.setText("Visits: " + Integer.toString(visitorStats.getVisits()));
    }

    public void displayVisitorStats() {
        if (visitorType.isTypeDiscovered()) {
            ImageView typePic = (ImageView) findViewById(R.id.typeImage);
            TextViewPixel typeMultiplier = (TextViewPixel) findViewById(R.id.typeMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("type" + visitorType.getTypePreferred(), "drawable", getApplicationContext().getPackageName());
            typePic.setImageResource(typeDrawableId);
            typeMultiplier.setText(Double.toString(visitorType.getTypeMultiplier()) + "x");
        }

        if (visitorType.isTierDiscovered()) {
            ImageView tierPic = (ImageView) findViewById(R.id.tierImage);
            TextViewPixel tierMultiplier = (TextViewPixel) findViewById(R.id.tierMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("tier" + visitorType.getTierPreferred(), "drawable", getApplicationContext().getPackageName());
            tierPic.setImageResource(typeDrawableId);
            tierMultiplier.setText(Double.toString(visitorType.getTierMultiplier()) + "x");
        }

        if (visitorType.isStateDiscovered()) {
            ImageView statePic = (ImageView) findViewById(R.id.stateImage);
            TextViewPixel stateMultiplier = (TextViewPixel) findViewById(R.id.stateMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("state" + visitorType.getStatePreferred(), "drawable", getApplicationContext().getPackageName());
            statePic.setImageResource(typeDrawableId);
            stateMultiplier.setText(Double.toString(visitorType.getStateMultiplier()) + "x");
        }

        ImageView bestItem = (ImageView) findViewById(R.id.bestItemImage);
        int bestItemDrawableId = getApplicationContext().getResources().getIdentifier("item" + visitorStats.getBestItem(), "drawable", getApplicationContext().getPackageName());
        bestItem.setImageResource(bestItemDrawableId);

        TextView bestItemValue = (TextView) findViewById(R.id.bestItemValue);
        bestItemValue.setText(Integer.toString(visitorStats.getBestItemValue()));
    }

    public void displayVisitorDemands() {
        TableLayout demandsTable = (TableLayout) findViewById(R.id.demandsTable);
        demandsTable.removeAllViews();

        // Create header row
        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.addView(dh.createTextView("Status", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("Criteria", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("Trade", 18, Color.BLACK));
        demandsTable.addView(headerRow);

        List<Visitor_Demand> visitorDemands = Select.from(Visitor_Demand.class).where(
                Condition.prop("visitor_id").eq(visitor.getId())).list();

        for(Visitor_Demand demand : visitorDemands) {
            TableRow demandRow = new TableRow(getApplicationContext());
            Criteria demandCriteria = Criteria.findById(Criteria.class, demand.getCriteriaType());

            String status = (demand.isDemandFulfilled() ? "Done" : "X");
            TextViewPixel criteriaStatus = dh.createTextView(status, 15, (demand.isRequired() ? Color.BLACK : Color.GRAY));

            String criteriaText = demandCriteria.getName() + ": " + Visitor_Demand.getCriteriaName(demand);
            TextViewPixel criteriaValue = dh.createTextView(criteriaText, 15, (demand.isRequired() ? Color.BLACK : Color.GRAY));

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

    public void completeVisitor(View view) {
        if (visitor.isVisitorComplete()) {
            Visitor_Demand.deleteAll(Visitor_Demand.class, "visitor_id = " + visitor.getId());
            Visitor.delete(visitor);
            closeVisitor(view);
        }
    }

    public void closeVisitor(View view) {
        finish();
    }
}
