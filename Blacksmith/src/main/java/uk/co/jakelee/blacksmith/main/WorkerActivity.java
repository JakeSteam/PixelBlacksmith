package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
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

        final Runnable everyFiveSeconds = new Runnable() {
            @Override
            public void run() {
                scheduledTask();
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND * 5);
            }
        };
        handler.post(everyFiveSeconds);
    }

    @Override
    public void onResume() {
        super.onResume();

        scheduledTask();
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
        TextView workerButton = (TextView) traderRoot.findViewById(R.id.workerButton);

        Item tool = Item.findById(Item.class, worker.getToolUsed());
        final WorkerActivity activity = this;

        if (worker.isPurchased()) {
            workerCharacter.setImageResource(DisplayHelper.getCharacterDrawableID(this, worker.getCharacterID()));
            workerCharacter.setTag(worker);
            workerCharacter.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    String workerTimesCompleted = WorkerHelper.getTimesCompletedString(activity, (Worker) v.getTag());
                    ToastHelper.showToast(activity, Toast.LENGTH_SHORT, workerTimesCompleted, false);
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
                        ToastHelper.showToast(activity, Toast.LENGTH_LONG, String.format(getString(R.string.workerResources),
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
                        ToastHelper.showToast(activity, Toast.LENGTH_SHORT, exactTimeLeft, false);
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

    public void scheduledTask() {
        WorkerHelper.checkForFinishedWorkers(this);
        populateWorkers();
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
