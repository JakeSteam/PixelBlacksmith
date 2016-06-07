package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Worker;
import uk.co.jakelee.blacksmith.model.Worker_Resource;

public class WorkerActivity extends Activity {
    private static DisplayHelper dh;
    private static final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        final Runnable everyFiveSeconds = new Runnable() {
            @Override
            public void run() {
                scheduledTask();
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND * 5);
            }
        };
        handler.post(everyFiveSeconds);

        dh.updateFullscreen(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        handler.removeCallbacksAndMessages(null);
    }

    private void populateWorkers() {
        LinearLayout container = (LinearLayout) findViewById(R.id.workerContainer);
        container.removeAllViews();

        List<Worker> workers = Select.from(Worker.class).orderBy("purchased DESC, level_unlocked ASC").list();
        for (Worker worker : workers) {
            container.addView(createWorkerRow(worker));
        }
    }

    private RelativeLayout createWorkerRow(Worker worker) {
        RelativeLayout traderRoot = createTraderRoot();
        ImageView workerCharacter = (ImageView) traderRoot.findViewById(R.id.workerCharacter);
        ImageView workerFood = (ImageView) traderRoot.findViewById(R.id.workerFood);
        TextView workerCharacterText = (TextView) traderRoot.findViewById(R.id.workerCharacterText);
        ImageView workerTool = (ImageView) traderRoot.findViewById(R.id.workerTool);
        TextView workerToolText = (TextView) traderRoot.findViewById(R.id.workerToolText);
        LinearLayout workerResourceContainer = (LinearLayout) traderRoot.findViewById(R.id.workerResource);
        final TextView workerButton = (TextView) traderRoot.findViewById(R.id.workerButton);

        Item tool = Item.findById(Item.class, worker.getToolUsed());
        final WorkerActivity activity = this;

        if (worker.isPurchased()) {
            workerCharacter.setImageResource(DisplayHelper.getCharacterDrawableID(this, worker.getCharacterID()));
            workerCharacter.setTag(worker);
            workerCharacter.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    String workerTimesCompleted = WorkerHelper.getTimesCompletedString(activity, (Worker) v.getTag());
                    ToastHelper.showToast(activity.findViewById(R.id.workerTitle), ToastHelper.SHORT, workerTimesCompleted, false);
                }
            });
            workerCharacterText.setText(WorkerHelper.isReady(worker) ? R.string.workerStatusReady : R.string.workerStatusBusy);

            int resourceID = R.drawable.transparent;
            if (worker.getFoodUsed() > 0) {
                resourceID = DisplayHelper.getItemDrawableID(this, worker.getFoodUsed());
            }
            if (WorkerHelper.isReady(worker)) {
                workerFood.setTag(worker);
                workerFood.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        Worker worker = (Worker) v.getTag();
                        if (WorkerHelper.isReady(worker)) {
                            Intent intent = new Intent(activity, FoodActivity.class);
                            intent.putExtra(WorkerHelper.INTENT_ID, worker.getWorkerID());
                            startActivity(intent);
                        }
                    }
                });
            }
            workerFood.setImageResource(resourceID);
            workerFood.setVisibility(View.VISIBLE);

            workerTool.setImageResource(DisplayHelper.getItemDrawableID(this, worker.getToolUsed()));
            workerTool.setTag(worker);
            workerTool.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Worker worker = (Worker) v.getTag();
                    if (WorkerHelper.isReady(worker)) {
                        Intent intent = new Intent(activity, ToolActivity.class);
                        intent.putExtra(WorkerHelper.INTENT_ID, worker.getWorkerID());
                        startActivity(intent);
                    }
                }
            });
            workerToolText.setText(String.format(getString(R.string.workerTool), tool.getName()));

            workerResourceContainer.setTag(worker);
            workerResourceContainer.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Worker worker = (Worker) v.getTag();
                    if (worker.isPurchased()) {
                        List<Worker_Resource> resources = WorkerHelper.getResourcesByTool((int) worker.getToolUsed());
                        ToastHelper.showToast(activity.findViewById(R.id.workerTitle), ToastHelper.LONG, String.format(getString(R.string.workerResources),
                                WorkerHelper.getRewardResourcesText(worker, resources, false)), false);
                    }
                }
            });
            WorkerHelper.populateResources(dh, workerResourceContainer, worker.getToolUsed());

            workerButton.setText(WorkerHelper.getButtonText(worker));
            workerButton.setTag(worker);
            workerButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Worker worker = (Worker) v.getTag();
                    if (WorkerHelper.sendOutWorker(worker)) {
                        scheduledTask();
                    } else {
                        String exactTimeLeft = WorkerHelper.getTimeLeftString(activity, worker);
                        ToastHelper.showToast(workerButton, ToastHelper.SHORT, exactTimeLeft, false);
                    }
                }
            });
        } else if (worker.getLevelUnlocked() <= Player_Info.getPlayerLevel()) {
            workerCharacter.setImageResource(R.drawable.item52);
            workerCharacterText.setText(String.format(getString(R.string.workerCoins),
                    WorkerHelper.getBuyCost(worker)));
            workerButton.setText(R.string.buyWorker);
            workerButton.setTag(worker);
            workerButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    AlertDialogHelper.confirmBuyWorker(getApplicationContext(), activity, (Worker) v.getTag());
                }
            });
        } else {
            workerCharacter.setImageResource(R.drawable.lock);
            workerCharacterText.setText(String.format(getString(R.string.slotLevel), worker.getLevelUnlocked()));
        }

        return traderRoot;
    }

    public void toggleAutofeed(View v) {
        Setting autofeed = Setting.findById(Setting.class, Constants.SETTING_AUTOFEED);
        if (autofeed != null) {
            autofeed.setBoolValue(!autofeed.getBoolValue());
            autofeed.save();
        }

        updateButtons();
    }

    private void updateButtons() {
        ImageView autofeedToggle = (ImageView) findViewById(R.id.autoFeedIndicator);
        TextView sendOutWorkers = (TextView) findViewById(R.id.gatherAllButton);

        Setting autofeedSetting = Setting.findById(Setting.class, Constants.SETTING_AUTOFEED);
        if (autofeedSetting != null) {
            Drawable tick = dh.createDrawable(R.drawable.tick, 30, 30);
            Drawable cross = dh.createDrawable(R.drawable.cross, 30, 30);

            boolean autofeedToggleValue = autofeedSetting.getBoolValue();
            autofeedToggle.setImageDrawable(autofeedToggleValue ? tick : cross);
        }

        if (Worker.getAvailableWorkersCount() > 0) {
            sendOutWorkers.setAlpha(1f);
        } else {
            sendOutWorkers.setAlpha(0.3f);
        }
    }

    public void sendAllGathering(View v) {
        // For each available worker, send out worker.
        List<Worker> workers = Worker.getAvailableWorkers();
        int numWorkers = 0;
        for (Worker worker : workers) {
            if (WorkerHelper.sendOutWorker(worker)) {
                numWorkers++;
            }
        }

        if (numWorkers > 0) {
            ToastHelper.showPositiveToast(v, ToastHelper.LONG, String.format(getString(R.string.sendOutWorkersToast), numWorkers), true);
        }

        updateButtons();
    }

    public void scheduledTask() {
        WorkerHelper.checkForFinishedWorkers(this);
        populateWorkers();

        updateButtons();
    }

    private RelativeLayout createTraderRoot() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View inflatedView = inflater.inflate(R.layout.custom_worker, null);
        return (RelativeLayout) inflatedView.findViewById(R.id.traderRow);
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Worker);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
