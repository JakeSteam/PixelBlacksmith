package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import uk.co.jakelee.blacksmith.helper.TutorialHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.State;
import uk.co.jakelee.blacksmith.model.Tier;
import uk.co.jakelee.blacksmith.model.Type;
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
    private static boolean loadingTrade = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);
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
        loadingTrade = false;

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
        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_2_VISITOR);
        th.addTutorial(findViewById(R.id.visitorPicture), R.string.tutorialVisitorPicture, R.string.tutorialVisitorPictureText, false);
        th.addTutorial(findViewById(R.id.tierImage), R.string.tutorialVisitorPrefs, R.string.tutorialVisitorPrefsText, false);
        th.addTutorialRectangle(findViewById(R.id.demandInfo), R.string.tutorialVisitorDemands, R.string.tutorialVisitorDemandsText, true, Gravity.BOTTOM);
        th.start();
    }

    private void startSecondTutorial() {
        // Stage 4
        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_4_VISITOR);
        th.addTutorialRectangle(findViewById(R.id.demandsTable), R.string.tutorialVisitorDemandsLeft, R.string.tutorialVisitorDemandsLeftText, false, Gravity.TOP);
        th.addTutorial(findViewById(R.id.close), R.string.tutorialVisitorClose, R.string.tutorialVisitorCloseText, true);
        th.start();
    }

    private void startThirdTutorial() {
        // Stage 12
        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_12_VISITOR);
        th.addTutorialNoOverlay(findViewById(R.id.completeButton), R.string.tutorialVisitorFinishBtn, R.string.tutorialVisitorFinishBtnText, true, Gravity.TOP);
        th.addTutorialRectangle(findViewById(R.id.demandsTable), R.string.tutorialVisitorFinish, R.string.tutorialVisitorFinishText, true, Gravity.TOP);
        th.start();
    }

    private void displayVisitorInfo() {
        int drawableId = getApplicationContext().getResources().getIdentifier("visitor" + visitorType.getVisitorID(), "drawable", getApplicationContext().getPackageName());
        ImageView visitorPicture = (ImageView) findViewById(R.id.visitorPicture);
        visitorPicture.setImageResource(drawableId);

        TextViewPixel visitorName = (TextViewPixel) findViewById(R.id.visitorName);
        visitorName.setText(visitorType.getName(this));

        TextViewPixel visitorDesc = (TextViewPixel) findViewById(R.id.visitorDesc);
        visitorDesc.setText(visitorType.getDesc(this));

        TextViewPixel visitorVisits = (TextViewPixel) findViewById(R.id.visitorVisits);
        visitorVisits.setText(String.format("Visits: %d", visitorStats.getVisits()));
    }

    private void displayVisitorStats() {
        if (visitorType.isTypeDiscovered()) {
            ImageView typePic = (ImageView) findViewById(R.id.typeImage);
            TextViewPixel typeMultiplier = (TextViewPixel) findViewById(R.id.typeMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("type" + visitorType.getTypePreferred(), "drawable", getApplicationContext().getPackageName());
            try {
                typePic.setImageResource(typeDrawableId);
            } catch (OutOfMemoryError e) {
            }
            typePic.setTag(R.id.preferred, visitorType.getTypePreferred());
            typePic.setTag(R.id.multiplier, VisitorHelper.multiplierToPercent(visitorType.getTypeMultiplier()));
            typeMultiplier.setText(VisitorHelper.multiplierToPercent(visitorType.getTypeMultiplier()));
        }

        if (visitorType.isTierDiscovered()) {
            ImageView tierPic = (ImageView) findViewById(R.id.tierImage);
            TextViewPixel tierMultiplier = (TextViewPixel) findViewById(R.id.tierMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("tier" + visitorType.getTierPreferred(), "drawable", getApplicationContext().getPackageName());
            try {
                tierPic.setImageResource(typeDrawableId);
            } catch (OutOfMemoryError e) {
            }
            tierPic.setTag(R.id.preferred, visitorType.getTierPreferred());
            tierPic.setTag(R.id.multiplier, VisitorHelper.multiplierToPercent(visitorType.getTierMultiplier()));
            tierMultiplier.setText(VisitorHelper.multiplierToPercent(visitorType.getTierMultiplier()));
        }

        if (visitorType.isStateDiscovered()) {
            ImageView statePic = (ImageView) findViewById(R.id.stateImage);
            TextViewPixel stateMultiplier = (TextViewPixel) findViewById(R.id.stateMultiplier);

            int typeDrawableId = getApplicationContext().getResources().getIdentifier("state" + visitorType.getStatePreferred(), "drawable", getApplicationContext().getPackageName());
            try {
                statePic.setImageResource(typeDrawableId);
            } catch (OutOfMemoryError e) {
            }
            statePic.setTag(R.id.preferred, visitorType.getStatePreferred());
            statePic.setTag(R.id.multiplier, VisitorHelper.multiplierToPercent(visitorType.getStateMultiplier()));
            stateMultiplier.setText(VisitorHelper.multiplierToPercent(visitorType.getStateMultiplier()));
        }

        if (!visitorStats.getBestItem().equals(Constants.ITEM_COINS)) {
            ImageView bestItem = (ImageView) findViewById(R.id.bestItemImage);
            int bestItemDrawableId = getApplicationContext().getResources().getIdentifier("item" + visitorStats.getBestItem(), "drawable", getApplicationContext().getPackageName());
            try {
                bestItem.setImageResource(bestItemDrawableId);
            } catch (OutOfMemoryError e) {
            }
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
                criteriaStatus.setPadding(0, 6, 0, 0);

                String criteriaText = String.format(getString(R.string.visitorDemand),
                        demand.getQuantity() - demand.getQuantityProvided(),
                        demandCriteria.getName(this),
                        Visitor_Demand.getCriteriaName(this, demand));
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
                            if (!loadingTrade) {
                                loadingTrade = true;
                                Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
                                intent.putExtra(DisplayHelper.DEMAND_TO_LOAD, v.getTag().toString());
                                startActivity(intent);
                            }
                        }
                    });
                }
                demandsTable.addView(demandRow);
            }
        }
    }

    public void completeVisitor(View view) {
        if (visitor.isVisitorComplete()) {
            VisitorHelper.createVisitorReward(this, visitor);
            VisitorHelper.rewardXp(visitor.isVisitorFullyComplete());
            VisitorHelper.removeVisitor(visitor);
            SoundHelper.playSound(this, SoundHelper.walkingSounds);
            Player_Info.increaseByOne(Player_Info.Statistic.VisitorsCompleted);

            if (visitorStats.getVisits() == Constants.VISITS_TROPHY) {
                List<Pair<Item, Integer>> rewards = VisitorHelper.createVisitorTrophyReward(visitor);
                Pair<Item, Integer> rewardedItem = rewards.get(0);
                Pair<Item, Integer> rewardedPage = rewards.get(1);
                ToastHelper.showToast(findViewById(R.id.visitor), ToastHelper.SHORT, String.format(getString(R.string.visitorTrophyEarned),
                        rewardedItem.first.getFullName(this, rewardedItem.second),
                        rewardedPage.first.getFullName(this, rewardedPage.second)), true);
            }

            if (visitorStats.getVisits() >= Constants.VISITS_TROPHY && visitorStats.getTrophyAchieved() == 0) {
                visitorStats.setTrophyAchieved(System.currentTimeMillis());
                visitorStats.save();
                visitorType.setWeighting(visitorType.getWeighting() / 2);
                visitorType.save();
            }

            if (visitor.isVisitorFullyComplete()) {
                GooglePlayHelper.UpdateEvent(Constants.EVENT_VISITOR_FULLY_COMPLETED, 1);
            }

            int numVisitors = Player_Info.getVisitorsCompleted();
            GooglePlayHelper.UpdateLeaderboards(Constants.LEADERBOARD_VISITORS, numVisitors);
            GooglePlayHelper.UpdateEvent(Constants.EVENT_VISITOR_COMPLETED, 1);
            MainActivity.needToRedrawVisitors = true;
            closePopup(view);
        } else {
            ToastHelper.showErrorToast(findViewById(R.id.visitor), ToastHelper.SHORT, getString(R.string.visitorCompleteFailure), false);
        }
    }

    public void dismissVisitor(View view) {
        AlertDialogHelper.confirmVisitorDismiss(getApplicationContext(), visitor, this);
    }

    public void tierClick(View view) {
        if (view.getTag(R.id.preferred) == null || view.getTag(R.id.multiplier) == null) {
            ToastHelper.showToast(findViewById(R.id.visitor), ToastHelper.SHORT, getString(R.string.undiscoveredPreference), false);
        } else {
            String preferred = Tier.findById(Tier.class, (long) view.getTag(R.id.preferred)).getName(this);
            VisitorHelper.displayPreference(this, view, R.string.tierPreference, preferred);
        }
    }

    public void typeClick(View view) {
        if (view.getTag(R.id.preferred) == null || view.getTag(R.id.multiplier) == null) {
            ToastHelper.showToast(findViewById(R.id.visitor), ToastHelper.SHORT, getString(R.string.undiscoveredPreference), false);
        } else {
            String preferred = Type.findById(Type.class, (long) view.getTag(R.id.preferred)).getName(this);
            VisitorHelper.displayPreference(this, view, R.string.typePreference, preferred);
        }
    }

    public void stateClick(View view) {
        if (view.getTag(R.id.preferred) == null || view.getTag(R.id.multiplier) == null) {
            ToastHelper.showToast(findViewById(R.id.visitor), ToastHelper.SHORT, getString(R.string.undiscoveredPreference), false);
        } else {
            String preferred = State.findById(State.class, (long) view.getTag(R.id.preferred)).getName(this);
            VisitorHelper.displayPreference(this, view, R.string.statePreference, preferred);
        }
    }

    public void bestItemClick(View view) {
        if (view.getTag(R.id.bestItemID) == null || view.getTag(R.id.bestItemValue) == null || view.getTag(R.id.bestItemState) == null) {
            ToastHelper.showToast(findViewById(R.id.visitor), ToastHelper.SHORT, getString(R.string.noBestItem), false);
        } else {
            long bestItemID = (long) view.getTag(R.id.bestItemID);
            long bestItemState = (long) view.getTag(R.id.bestItemState);
            int bestItemValue = (int) view.getTag(R.id.bestItemValue);

            String bestItemName = Item.findById(Item.class, bestItemID).getFullName(this, bestItemState);
            ToastHelper.showToast(findViewById(R.id.visitor),
                    ToastHelper.SHORT, String.format(getString(R.string.bestItemMessage),
                            visitorType.getName(this),
                            bestItemName,
                            bestItemValue), false);
        }
    }

    public void callbackDismiss() {
        VisitorHelper.removeVisitor(visitor);
        SoundHelper.playSound(this, SoundHelper.walkingSounds);
        ToastHelper.showToast(findViewById(R.id.visitor), ToastHelper.LONG, getString(R.string.dismissComplete), true);
        this.finish();
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
