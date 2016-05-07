package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;

public class WorkerActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        LinearLayout root = (LinearLayout) findViewById(R.id.workerContainer);
        TextView textView = new TextViewPixel(this);
        textView.setText("Test text");
        root.addView(textView);
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
