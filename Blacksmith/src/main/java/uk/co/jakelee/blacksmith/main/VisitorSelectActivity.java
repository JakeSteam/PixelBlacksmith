package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
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

        new Thread(new Runnable() {
            public void run() {
                populateVisitors();
            }
        }).start();
    }

    private void populateVisitors() {
        final TableLayout visitorTable = (TableLayout) findViewById(R.id.visitorTable);
        List<Visitor_Type> allVisitors = Select.from(Visitor_Type.class).orderBy("name ASC").list();

        List<TableRow> availableRows = new ArrayList<>();
        List<TableRow> unavailableRows = new ArrayList<>();

        for (Visitor_Type visitor : allVisitors) {
            Visitor_Stats vStats = Visitor_Stats.findById(Visitor_Stats.class, visitor.getVisitorID());
            if (vStats == null) { continue; }
            boolean canBeSelected = WorkerHelper.canBeSelectedAsHero(visitor, vStats);
            ImageView visitorImage = dh.createImageView("visitor", visitor.getVisitorID(), 60, 60);
            TextView visitorReqs = dh.createTextView(String.format(getString(R.string.visitorRequirements),
                    visitor.getName(),
                    vStats.getVisits(), Constants.HERO_MIN_VISITS,
                    vStats.getBestItemValue(), Constants.HERO_MIN_TRADE,
                    visitor.getPreferencesDiscovered(), Constants.HERO_MIN_PREFS), 20);
            visitorReqs.setPadding(dh.convertDpToPixel(6), 0, 0, dh.convertDpToPixel(10));

            TableRow visitorRow = new TableRow(this);

            if (canBeSelected) {
                visitorRow.setTag(R.id.visitor, visitor.getVisitorID());
                visitorRow.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        visitorClick(v);
                    }
                });
                visitorReqs.setTextColor(Color.parseColor("#267c18"));
            } else {
                visitorImage.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
            }

            visitorRow.addView(visitorImage);
            visitorRow.addView(visitorReqs);

            if (canBeSelected) {
                availableRows.add(visitorRow);
            } else {
                unavailableRows.add(visitorRow);
            }
        }

        availableRows.addAll(unavailableRows);
        final List<TableRow> allRows = availableRows;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                visitorTable.removeAllViews();
                for (TableRow row : allRows) {
                    visitorTable.addView(row);
                }
            }
        });
    }

    public void visitorClick(View view) {
        int visitorId = (int) (long) view.getTag(R.id.visitor);
        Hero conflictingHero = Select.from(Hero.class).where(
                Condition.prop("visitor_id").eq(visitorId)).first();
        boolean alreadyInUse = conflictingHero != null && conflictingHero.getHeroId() != hero.getHeroId();


        if (alreadyInUse) {
            ToastHelper.showErrorToast(view, ToastHelper.SHORT, ErrorHelper.errors.get(Constants.ERROR_VISITOR_IN_USE), false);
        } else {
            hero.setVisitorId(visitorId);
            hero.save();

            finish();
        }
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
