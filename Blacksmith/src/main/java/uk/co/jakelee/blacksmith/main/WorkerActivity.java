package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Worker;

public class WorkerActivity extends Activity {
    private static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        dh = DisplayHelper.getInstance(getApplicationContext());

        populateWorkers();
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
        TextView workerCharacterText = (TextView) traderRoot.findViewById(R.id.workerCharacterText);
        ImageView workerTool = (ImageView) traderRoot.findViewById(R.id.workerTool);
        TextView workerToolText = (TextView) traderRoot.findViewById(R.id.workerToolText);
        LinearLayout workerResourceContainer = (LinearLayout) traderRoot.findViewById(R.id.workerResource);
        TextView workerButton = (TextView) traderRoot.findViewById(R.id.workerButton);

        Item tool = Item.findById(Item.class, worker.getToolUsed());
        final WorkerActivity activity = this;

        if (worker.isPurchased()) {
            workerCharacter.setImageResource(DisplayHelper.getCharacterDrawableID(this, worker.getCharacterID()));
            workerCharacterText.setText(WorkerHelper.isReady(worker) ? "Ready" : "Busy");
            workerTool.setImageResource(DisplayHelper.getItemDrawableID(this, worker.getToolUsed()));
            workerToolText.setText(tool.getName());
            WorkerHelper.populateResources(dh, workerResourceContainer, worker.getToolUsed());
            workerButton.setText(WorkerHelper.getButtonText(worker));
        } else if (worker.getLevelUnlocked() <= Player_Info.getPlayerLevel()) {
            workerCharacter.setImageResource(R.drawable.item52);
            workerCharacterText.setText(WorkerHelper.getBuyCost(worker) + " coins");
            workerButton.setText("Purchase Worker");
            workerButton.setTag(worker);
            workerButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    AlertDialogHelper.confirmBuyWorker(getApplicationContext(), activity, (Worker) v.getTag());
                }
            });
        } else {
            workerCharacter.setImageResource(R.drawable.lock);
            workerCharacterText.setText("Lev " + worker.getLevelUnlocked());
        }

        return traderRoot;
    }

    public void alertDialogCallback() {
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
