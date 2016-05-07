package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
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
        List<Worker> workers = Select.from(Worker.class).orderBy("purchased DESC, level_unlocked ASC").list();
        for (Worker worker : workers) {
            RelativeLayout traderRoot = createTraderRoot();
            ImageView workerCharacter = (ImageView) traderRoot.findViewById(R.id.workerCharacter);
            ImageView workerTool = (ImageView) traderRoot.findViewById(R.id.workerTool);
            ImageView workerResource = (ImageView) traderRoot.findViewById(R.id.workerResource);
            TextView workerTimer = (TextView) traderRoot.findViewById(R.id.workerTimer);
            if (worker.isPurchased()) {
                workerCharacter.setImageResource(DisplayHelper.getCharacterDrawableID(this, worker.getCharacterID()));
                workerTool.setImageResource(DisplayHelper.getItemDrawableID(this, worker.getToolUsed()));
                workerResource.setImageResource(WorkerHelper.getResourceIDByTool(this, worker.getToolUsed()));
                workerTimer.setText("Time goes here!");
            } else if (worker.getLevelUnlocked() <= Player_Info.getPlayerLevel()) {
                workerCharacter.setImageResource(R.drawable.item52);
            } else {
                workerCharacter.setImageResource(R.drawable.lock);
            }

            container.addView(traderRoot);
        }
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
