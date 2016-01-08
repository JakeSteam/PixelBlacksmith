package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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
    }

    public void displayVisitorStats() {
        TextView visitorVisits = (TextView) findViewById(R.id.visitorVisits);
        visitorVisits.setText(Integer.toString(visitorStats.getVisits()));
    }

    public void displayVisitorDemands() {
        TableLayout demandsTable = (TableLayout) findViewById(R.id.demandsTable);

        List<Visitor_Demand> visitorDemands = Visitor_Demand.find(Visitor_Demand.class, "visitor_id = ?", Long.toString(visitorType.getVisitorID()));
        for(Visitor_Demand demand : visitorDemands) {
            TableRow demandRow = new TableRow(getApplicationContext());
            Criteria demandCriteria = Criteria.findById(Criteria.class, demand.getCriteriaType());

            TextView criteriaType = dh.createTextView(demandCriteria.getName(), 12, Color.BLACK);
            criteriaType.setPadding(15, 5, 15, 5);

            TextView criteriaValue = dh.createTextView(Long.toString(demand.getCriteriaValue()), 12, Color.BLACK);
            criteriaValue.setPadding(15, 5, 15, 5);

            ImageView tradeBtn = new ImageView(getApplicationContext());
            tradeBtn.setBackgroundResource(R.drawable.open_shop);

            demandRow.addView(criteriaType);
            demandRow.addView(criteriaValue);
            demandRow.addView(tradeBtn);

            demandsTable.addView(demandRow);
        }
    }

    public void closeVisitor(View view) {
        finish();
    }
}
