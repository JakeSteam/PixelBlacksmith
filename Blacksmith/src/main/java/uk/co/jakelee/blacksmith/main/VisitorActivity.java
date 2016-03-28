package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.helper.SoundHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        Long visitorId = Long.parseLong(intent.getStringExtra(DisplayHelper.VISITOR_TO_LOAD));

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

        TextViewPixel visitorName = (TextViewPixel) findViewById(R.id.visitorName);
        visitorName.setText(visitorType.getName());

        TextViewPixel visitorDesc = (TextViewPixel) findViewById(R.id.visitorDesc);
        visitorDesc.setText(visitorType.getDesc());

        TextViewPixel visitorVisits = (TextViewPixel) findViewById(R.id.visitorVisits);
        visitorVisits.setText(String.format("Visits: %d",
                visitorStats.getVisits()));
    }

    public void displayVisitorStats() {
        if (visitorType.isTypeDiscovered()) {
            ImageView typePic = (ImageView) findViewById(R.id.typeImage);
            TextViewPixel typeMultiplier = (TextViewPixel) findViewById(R.id.typeMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("type" + visitorType.getTypePreferred(), "drawable", getApplicationContext().getPackageName());
            typePic.setImageResource(typeDrawableId);
            typeMultiplier.setText(VisitorHelper.multiplierToPercent(visitorType.getTypeMultiplier()));
        }

        if (visitorType.isTierDiscovered()) {
            ImageView tierPic = (ImageView) findViewById(R.id.tierImage);
            TextViewPixel tierMultiplier = (TextViewPixel) findViewById(R.id.tierMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("tier" + visitorType.getTierPreferred(), "drawable", getApplicationContext().getPackageName());
            tierPic.setImageResource(typeDrawableId);
            tierMultiplier.setText(VisitorHelper.multiplierToPercent(visitorType.getTierMultiplier()));
        }

        if (visitorType.isStateDiscovered()) {
            ImageView statePic = (ImageView) findViewById(R.id.stateImage);
            TextViewPixel stateMultiplier = (TextViewPixel) findViewById(R.id.stateMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("state" + visitorType.getStatePreferred(), "drawable", getApplicationContext().getPackageName());
            statePic.setImageResource(typeDrawableId);
            stateMultiplier.setText(VisitorHelper.multiplierToPercent(visitorType.getStateMultiplier()));
        }

        if (!visitorStats.getBestItem().equals(Constants.ITEM_COINS)) {
            ImageView bestItem = (ImageView) findViewById(R.id.bestItemImage);
            int bestItemDrawableId = getApplicationContext().getResources().getIdentifier("item" + visitorStats.getBestItem(), "drawable", getApplicationContext().getPackageName());
            bestItem.setImageResource(bestItemDrawableId);
        }

        TextView bestItemValue = (TextView) findViewById(R.id.bestItemValue);
        bestItemValue.setText(String.format("%d", visitorStats.getBestItemValue()));
    }

    public void displayVisitorDemands() {
        TableLayout demandsTable = (TableLayout) findViewById(R.id.demandsTable);
        demandsTable.removeAllViews();

        List<Visitor_Demand> visitorDemands = Select.from(Visitor_Demand.class).where(
                Condition.prop("visitor_id").eq(visitor.getId())).list();

        // Backporting Boolean.Compare from API 19
        Collections.sort(visitorDemands, new Comparator<Visitor_Demand>() {
            @Override
            public int compare(Visitor_Demand demand1, Visitor_Demand demand2) {
                return demand2.isRequired() == demand1.isRequired() ? 0 : demand2.isRequired() ? 1 : -1;
            }
        });

        for (Visitor_Demand demand : visitorDemands) {
            TableRow demandRow = new TableRow(getApplicationContext());
            Criteria demandCriteria = Criteria.findById(Criteria.class, demand.getCriteriaType());

            int statusDrawableID = (demand.isDemandFulfilled() ? R.drawable.tick : R.drawable.cross);
            Drawable statusDrawable = dh.createDrawable(statusDrawableID, 25, 25);
            ImageView criteriaStatus = new ImageView(getApplicationContext());
            criteriaStatus.setImageDrawable(statusDrawable);

            String criteriaText = demandCriteria.getName() + ": " + Visitor_Demand.getCriteriaName(demand);
            TextViewPixel criteriaValue = dh.createTextView(criteriaText, 20, (demand.isRequired() ? Color.BLACK : Color.GRAY));
            criteriaValue.setHeight(dh.convertDpToPixel(35));
            criteriaValue.setGravity(Gravity.CENTER_VERTICAL);
            criteriaValue.setSingleLine(false);

            ImageView tradeBtn = new ImageView(getApplicationContext());
            if (!demand.isDemandFulfilled()) {
                tradeBtn.setImageDrawable(dh.createDrawable(R.drawable.open, 35, 35));
                int tradeBtnPadding = dh.convertDpToPixel(3);
                tradeBtn.setPadding(tradeBtnPadding, tradeBtnPadding, tradeBtnPadding, tradeBtnPadding);
                tradeBtn.setTag(demand.getId());
                tradeBtn.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
                        intent.putExtra(DisplayHelper.DEMAND_TO_LOAD, v.getTag().toString());
                        startActivity(intent);
                    }
                });
            } else {
                criteriaValue.setPaintFlags(criteriaValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            demandRow.addView(criteriaStatus);
            demandRow.addView(criteriaValue);
            demandRow.addView(tradeBtn);
            demandsTable.addView(demandRow);
        }
    }

    public void completeVisitor(View view) {
        if (visitor.isVisitorComplete()) {
            VisitorHelper.removeVisitor(visitor);
            SoundHelper.playSound(this, SoundHelper.walkingSounds);
            Player_Info.increaseByOne(Player_Info.Statistic.VisitorsCompleted);

            if (visitorStats.getVisits() == Constants.VISITS_TROPHY) {
                createVisitorTrophyReward(visitor);
                ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, R.string.visitorTrophyEarned);
            } else {
                int numRewards = VisitorHelper.getRandomNumber(Constants.MINIMUM_REWARDS, Constants.MAXIMUM_REWARDS);
                String itemName = createVisitorReward(visitor, numRewards);
                ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format("As the visitor leaves, you notice %dx %s on the counter.",
                        numRewards,
                        itemName));
            }

            int numVisitors = Player_Info.getVisitorsCompleted();
            GooglePlayHelper.UpdateLeaderboards(Constants.LEADERBOARD_VISITORS, numVisitors);

            closeVisitor(view);
        } else {
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, R.string.visitorCompleteFailure);
        }
    }

    public void dismissVisitor(View view) {
        AlertDialogHelper.confirmVisitorDismiss(getApplicationContext(), visitor, this);
    }

    public String createVisitorReward(Visitor visitor, int numRewards) {
        int typeID = VisitorHelper.pickRandomNumberFromArray(Constants.VISITOR_REWARD_TYPES);
        List<Item> matchingItems = Select.from(Item.class).where(
                Condition.prop("type").eq(typeID)).list();
        Item selectedItem = VisitorHelper.pickRandomItemFromList(matchingItems);

        Inventory.addItem(selectedItem.getId(), Constants.STATE_NORMAL, numRewards);
        return selectedItem.getName();
    }

    public void createVisitorTrophyReward(Visitor visitor) {
        Visitor_Type visitorType = Select.from(Visitor_Type.class).where(
                Condition.prop("visitor_id").eq(visitor.getType())).first();

        Item preferredItem;
        Long preferredState = visitorType.getStatePreferred();
        Long preferredTier = visitorType.getTierPreferred();
        Long preferredType = visitorType.getTypePreferred();

        preferredItem = Select.from(Item.class).where(
                Condition.prop("tier").eq(preferredTier),
                Condition.prop("type").eq(preferredType)).orderBy("value DESC").first();

        if (preferredItem == null) {
            preferredItem = Select.from(Item.class).where(
                    Condition.prop("type").eq(preferredType)).orderBy("value DESC").first();
        }

        Inventory.addItem(preferredItem.getId(), preferredState, Constants.TROPHY_ITEM_REWARDS);

    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Visitor);
        startActivity(intent);
    }

    public void closeVisitor(View view) {
        finish();
    }
}
