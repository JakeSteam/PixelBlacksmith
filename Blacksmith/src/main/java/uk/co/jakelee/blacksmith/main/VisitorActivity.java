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

import java.util.ArrayList;
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
import uk.co.jakelee.blacksmith.helper.TutorialHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.State;
import uk.co.jakelee.blacksmith.model.Tier;
import uk.co.jakelee.blacksmith.model.Type;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class VisitorActivity extends Activity {
    private static DisplayHelper dh;
    private static Visitor visitor;
    private static Visitor_Type visitorType;
    private static Visitor_Stats visitorStats;

    private static boolean identifyFirstDemand = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        dh = DisplayHelper.getInstance(getApplicationContext());
    }

    @Override
    public void onPause() {
        super.onPause();

        if (TutorialHelper.currentlyInTutorial) {
            TutorialHelper.chainTourGuide.next();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        Long visitorId = Long.parseLong(intent.getStringExtra(DisplayHelper.VISITOR_TO_LOAD));

        if (visitorId > 0) {
            visitor = Visitor.findById(Visitor.class, visitorId);
            if (visitor != null) {
                visitorType = Visitor_Type.findById(Visitor_Type.class, visitor.getType());
                visitorStats = Visitor_Stats.findById(Visitor_Stats.class, visitor.getType());
            }

            if (visitorType != null && visitorStats != null) {
                createVisitorInterface();
            }
        }

        if (TutorialHelper.currentlyInTutorial) {
            if (TutorialHelper.currentStage <= Constants.STAGE_2_VISITOR) {
                startFirstTutorial();
            } else if (TutorialHelper.currentStage <= Constants.STAGE_4_VISITOR) {
                startSecondTutorial();
            } else if (TutorialHelper.currentStage < Constants.STAGE_12_VISITOR) {
                startThirdTutorial();
            }
        }
    }

    private void createVisitorInterface() {
        displayVisitorInfo();
        displayVisitorStats();
        displayVisitorDemands();
    }

    private void startFirstTutorial() {
        // Stage 2
        TutorialHelper th = new TutorialHelper(Constants.STAGE_2_VISITOR);
        th.addTutorial(this, findViewById(R.id.visitorPicture), R.string.tutorialVisitorPicture, R.string.tutorialVisitorPictureText, false);
        th.addTutorial(this, findViewById(R.id.tierImage), R.string.tutorialVisitorPrefs, R.string.tutorialVisitorPrefsText, false);
        th.addTutorialRectangle(this, findViewById(R.id.demandInfo), R.string.tutorialVisitorDemands, R.string.tutorialVisitorDemandsText, true, Gravity.BOTTOM);
        th.start(this);
    }

    private void startSecondTutorial() {
        // Stage 4
        TutorialHelper th = new TutorialHelper(Constants.STAGE_4_VISITOR);
        th.addTutorialRectangle(this, findViewById(R.id.demandsTable), R.string.tutorialVisitorDemandsLeft, R.string.tutorialVisitorDemandsLeftText, false, Gravity.TOP);
        th.addTutorial(this, findViewById(R.id.close), R.string.tutorialVisitorClose, R.string.tutorialVisitorCloseText, true);
        th.start(this);
    }

    private void startThirdTutorial() {
        // Stage 12
        TutorialHelper th = new TutorialHelper(Constants.STAGE_12_VISITOR);
        th.addTutorialNoOverlay(this, findViewById(R.id.completeButton), R.string.tutorialVisitorFinishBtn, R.string.tutorialVisitorFinishBtnText, true, Gravity.TOP);
        th.addTutorialRectangle(this, findViewById(R.id.demandsTable), R.string.tutorialVisitorFinish, R.string.tutorialVisitorFinishText, true, Gravity.TOP);
        th.start(this);
    }

    private void displayVisitorInfo() {
        int drawableId = getApplicationContext().getResources().getIdentifier("visitor" + visitorType.getVisitorID(), "drawable", getApplicationContext().getPackageName());
        ImageView visitorPicture = (ImageView) findViewById(R.id.visitorPicture);
        visitorPicture.setImageResource(drawableId);

        TextViewPixel visitorName = (TextViewPixel) findViewById(R.id.visitorName);
        visitorName.setText(visitorType.getName());

        TextViewPixel visitorDesc = (TextViewPixel) findViewById(R.id.visitorDesc);
        visitorDesc.setText(visitorType.getDesc());

        TextViewPixel visitorVisits = (TextViewPixel) findViewById(R.id.visitorVisits);
        visitorVisits.setText(String.format("Visits: %d", visitorStats.getVisits()));
    }

    private void displayVisitorStats() {
        if (visitorType.isTypeDiscovered()) {
            ImageView typePic = (ImageView) findViewById(R.id.typeImage);
            TextViewPixel typeMultiplier = (TextViewPixel) findViewById(R.id.typeMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("type" + visitorType.getTypePreferred(), "drawable", getApplicationContext().getPackageName());
            typePic.setImageResource(typeDrawableId);
            typePic.setTag(R.id.preferred, visitorType.getTypePreferred());
            typePic.setTag(R.id.multiplier, VisitorHelper.multiplierToPercent(visitorType.getTypeMultiplier()));
            typeMultiplier.setText(VisitorHelper.multiplierToPercent(visitorType.getTypeMultiplier()));
        }

        if (visitorType.isTierDiscovered()) {
            ImageView tierPic = (ImageView) findViewById(R.id.tierImage);
            TextViewPixel tierMultiplier = (TextViewPixel) findViewById(R.id.tierMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("tier" + visitorType.getTierPreferred(), "drawable", getApplicationContext().getPackageName());
            tierPic.setImageResource(typeDrawableId);
            tierPic.setTag(R.id.preferred, visitorType.getTierPreferred());
            tierPic.setTag(R.id.multiplier, VisitorHelper.multiplierToPercent(visitorType.getTierMultiplier()));
            tierMultiplier.setText(VisitorHelper.multiplierToPercent(visitorType.getTierMultiplier()));
        }

        if (visitorType.isStateDiscovered()) {
            ImageView statePic = (ImageView) findViewById(R.id.stateImage);
            TextViewPixel stateMultiplier = (TextViewPixel) findViewById(R.id.stateMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("state" + visitorType.getStatePreferred(), "drawable", getApplicationContext().getPackageName());
            statePic.setImageResource(typeDrawableId);
            statePic.setTag(R.id.preferred, visitorType.getStatePreferred());
            statePic.setTag(R.id.multiplier, VisitorHelper.multiplierToPercent(visitorType.getStateMultiplier()));
            stateMultiplier.setText(VisitorHelper.multiplierToPercent(visitorType.getStateMultiplier()));
        }

        if (!visitorStats.getBestItem().equals(Constants.ITEM_COINS)) {
            ImageView bestItem = (ImageView) findViewById(R.id.bestItemImage);
            int bestItemDrawableId = getApplicationContext().getResources().getIdentifier("item" + visitorStats.getBestItem(), "drawable", getApplicationContext().getPackageName());
            bestItem.setImageResource(bestItemDrawableId);
            bestItem.setTag(R.id.bestItemID, visitorStats.getBestItem());
            bestItem.setTag(R.id.bestItemValue, visitorStats.getBestItemValue());
            bestItem.setTag(R.id.bestItemState, visitorStats.getBestItemState());

            TextView bestItemValue = (TextView) findViewById(R.id.bestItemValue);
            bestItemValue.setText(String.format("%d", visitorStats.getBestItemValue()));
        }
    }

    private void displayVisitorDemands() {
        TableLayout demandsTable = (TableLayout) findViewById(R.id.demandsTable);
        demandsTable.removeAllViews();

        if (visitor == null) {
            this.finish();
        } else {
            List<Visitor_Demand> visitorDemands = Select.from(Visitor_Demand.class).where(
                    Condition.prop("visitor_id").eq(visitor.getId())).list();

            // Backporting Boolean.Compare from API 19
            Collections.sort(visitorDemands, new Comparator<Visitor_Demand>() {
                @Override
                public int compare(Visitor_Demand demand1, Visitor_Demand demand2) {
                    return demand2.isRequired() == demand1.isRequired() ? 0 : demand2.isRequired() ? 1 : -1;
                }
            });

            if (TutorialHelper.currentlyInTutorial) {
                identifyFirstDemand = true;
            }

            for (Visitor_Demand demand : visitorDemands) {
                Criteria demandCriteria = Criteria.findById(Criteria.class, demand.getCriteriaType());
                TableRow demandRow = new TableRow(getApplicationContext());
                if (identifyFirstDemand) {
                    demandRow.setId(R.id.demandInfo);
                    identifyFirstDemand = false;
                }

                int statusDrawableID = (demand.isDemandFulfilled() ? R.drawable.tick : R.drawable.cross);
                Drawable statusDrawable = dh.createDrawable(statusDrawableID, 25, 25);
                ImageView criteriaStatus = new ImageView(getApplicationContext());
                criteriaStatus.setImageDrawable(statusDrawable);

                String criteriaText = String.format(getString(R.string.visitorDemand),
                        demand.getQuantity() - demand.getQuantityProvided(),
                        demandCriteria.getName(),
                        Visitor_Demand.getCriteriaName(demand));
                TextViewPixel criteriaValue = dh.createTextView(criteriaText, 20, (demand.isRequired() ? Color.BLACK : Color.GRAY));
                criteriaValue.setHeight(dh.convertDpToPixel(35));
                criteriaValue.setGravity(Gravity.CENTER_VERTICAL);
                criteriaValue.setSingleLine(false);

                ImageView tradeBtn = new ImageView(getApplicationContext());
                if (!demand.isDemandFulfilled()) {
                    tradeBtn.setImageDrawable(dh.createDrawable(R.drawable.open, 35, 35));
                    int tradeBtnPadding = dh.convertDpToPixel(3);
                    tradeBtn.setPadding(tradeBtnPadding, tradeBtnPadding, tradeBtnPadding, tradeBtnPadding);
                } else {
                    criteriaValue.setPaintFlags(criteriaValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

                demandRow.addView(criteriaStatus);
                demandRow.addView(criteriaValue);
                demandRow.addView(tradeBtn);

                demandRow.setTag(demand.getId());
                if (!demand.isDemandFulfilled()) {
                    demandRow.setOnClickListener(new Button.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
                            intent.putExtra(DisplayHelper.DEMAND_TO_LOAD, v.getTag().toString());
                            startActivity(intent);
                        }
                    });
                }
                demandsTable.addView(demandRow);
            }
        }
    }

    public void completeVisitor(View view) {
        if (visitor.isVisitorComplete()) {
            createVisitorReward(visitor.isVisitorFullyComplete());
            VisitorHelper.removeVisitor(visitor);
            SoundHelper.playSound(this, SoundHelper.walkingSounds);
            Player_Info.increaseByOne(Player_Info.Statistic.VisitorsCompleted);

            if (visitorStats.getVisits() == Constants.VISITS_TROPHY) {
                createVisitorTrophyReward(visitor);
                ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, R.string.visitorTrophyEarned, true);
            }

            int numVisitors = Player_Info.getVisitorsCompleted();
            GooglePlayHelper.UpdateLeaderboards(Constants.LEADERBOARD_VISITORS, numVisitors);
            MainActivity.needToRedrawVisitors = true;
            closePopup(view);
        } else {
            ToastHelper.showErrorToast(getApplicationContext(), Toast.LENGTH_SHORT, getString(R.string.visitorCompleteFailure), false);
        }
    }

    public void dismissVisitor(View view) {
        AlertDialogHelper.confirmVisitorDismiss(getApplicationContext(), visitor, this);
    }

    private void createVisitorReward(boolean isFullyComplete) {
        int minimumRewards = Upgrade.getValue("Minimum Visitor Rewards");
        int maximumRewards = Upgrade.getValue("Maximum Visitor Rewards");
        int numRewards = (isFullyComplete ? 2 : 1) * VisitorHelper.getRandomNumber(minimumRewards, maximumRewards);
        boolean rewardLegendary = Player_Info.isPremium() && VisitorHelper.getRandomBoolean(100 - Upgrade.getValue("Legendary Chance"));
        int typeID = VisitorHelper.pickRandomNumberFromArray(Constants.VISITOR_REWARD_TYPES);

        // Get normal reward
        List<Item> matchingItems = Select.from(Item.class).where(Condition.prop("type").eq(typeID)).list();
        Item selectedItem = VisitorHelper.pickRandomItemFromList(matchingItems);
        Inventory.addItem(selectedItem.getId(), Constants.STATE_NORMAL, numRewards);
        String rewardString = getRewardString(rewardLegendary, isFullyComplete);

        // Get legendary reward
        if (rewardLegendary) {
            List<Item> premiumItems = Select.from(Item.class).where(Condition.prop("tier").eq(Constants.TIER_PREMIUM)).list();
            Item premiumItem = VisitorHelper.pickRandomItemFromList(premiumItems);
            Inventory.addItem(premiumItem.getId(), Constants.STATE_UNFINISHED, 1);
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_LONG, String.format(rewardString,
                    numRewards,
                    selectedItem.getName(),
                    premiumItem.getFullName(Constants.STATE_UNFINISHED)), true);
        } else {
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_LONG, String.format(rewardString,
                    numRewards,
                    selectedItem.getFullName(Constants.STATE_NORMAL)), true);
        }
    }

    private String getRewardString(boolean rewardLegendary, boolean isFullyComplete) {
        List<String> strings = new ArrayList<>();
        if (rewardLegendary && isFullyComplete) {
            strings.add(getString(R.string.visitorLeavesCompletePremium1));
            strings.add(getString(R.string.visitorLeavesCompletePremium2));
            strings.add(getString(R.string.visitorLeavesCompletePremium3));
        } else if (rewardLegendary && !isFullyComplete) {
            strings.add(getString(R.string.visitorLeavesPremium1));
            strings.add(getString(R.string.visitorLeavesPremium2));
            strings.add(getString(R.string.visitorLeavesPremium3));
        }else if (!rewardLegendary && isFullyComplete) {
            strings.add(getString(R.string.visitorLeavesComplete1));
            strings.add(getString(R.string.visitorLeavesComplete2));
            strings.add(getString(R.string.visitorLeavesComplete3));
        }else if (!rewardLegendary && !isFullyComplete) {
            strings.add(getString(R.string.visitorLeaves1));
            strings.add(getString(R.string.visitorLeaves2));
            strings.add(getString(R.string.visitorLeaves3));
        }
        int position = VisitorHelper.getRandomNumber(0, strings.size() - 1);
        return strings.get(position);
    }

    private Item createVisitorTrophyReward(Visitor visitor) {
        Visitor_Type visitorType = Select.from(Visitor_Type.class).where(
                Condition.prop("visitor_id").eq(visitor.getType())).first();

        Long preferredState = visitorType.getStatePreferred();
        Long preferredTier = visitorType.getTierPreferred();
        Long preferredType = visitorType.getTypePreferred();

        Item preferredItem = Select.from(Item.class).where(
                Condition.prop("tier").eq(preferredTier),
                Condition.prop("type").eq(preferredType)).orderBy("value DESC").first();

        if (preferredItem == null) {
            preferredItem = Select.from(Item.class).where(
                    Condition.prop("type").eq(preferredType)).orderBy("value DESC").first();
        }

        Inventory.addItem(preferredItem.getId(), preferredState, Constants.TROPHY_ITEM_REWARDS);

        return preferredItem;
    }

    public void tierClick(View view) {
        if (view.getTag(R.id.preferred) == null || view.getTag(R.id.multiplier) == null) {
            ToastHelper.showToast(this, Toast.LENGTH_SHORT, R.string.undiscoveredPreference, false);
        } else {
            String preferred = Tier.findById(Tier.class, (long) view.getTag(R.id.preferred)).getName();
            VisitorHelper.displayPreference(this, view, R.string.tierPreference, preferred);
        }
    }

    public void typeClick(View view) {
        if (view.getTag(R.id.preferred) == null || view.getTag(R.id.multiplier) == null) {
            ToastHelper.showToast(this, Toast.LENGTH_SHORT, R.string.undiscoveredPreference, false);
        } else {
            String preferred = Type.findById(Type.class, (long) view.getTag(R.id.preferred)).getName();
            VisitorHelper.displayPreference(this, view, R.string.typePreference, preferred);
        }
    }

    public void stateClick(View view) {
        if (view.getTag(R.id.preferred) == null || view.getTag(R.id.multiplier) == null) {
            ToastHelper.showToast(this, Toast.LENGTH_SHORT, R.string.undiscoveredPreference, false);
        } else {
            String preferred = State.findById(State.class, (long) view.getTag(R.id.preferred)).getName();
            VisitorHelper.displayPreference(this, view, R.string.statePreference, preferred);
        }
    }

    public void bestItemClick(View view) {
        if (view.getTag(R.id.bestItemID) == null || view.getTag(R.id.bestItemValue) == null || view.getTag(R.id.bestItemState) == null) {
            ToastHelper.showToast(this, Toast.LENGTH_SHORT, R.string.noBestItem, false);
        } else {
            long bestItemID = (long) view.getTag(R.id.bestItemID);
            long bestItemState = (long) view.getTag(R.id.bestItemState);
            int bestItemValue = (int) view.getTag(R.id.bestItemValue);

            String bestItemName = Item.findById(Item.class, bestItemID).getFullName(bestItemState);
            ToastHelper.showToast(this, Toast.LENGTH_SHORT, String.format(getString(R.string.bestItemMessage),
                    visitorType.getName(),
                    bestItemName,
                    bestItemValue), false);
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Visitor);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
