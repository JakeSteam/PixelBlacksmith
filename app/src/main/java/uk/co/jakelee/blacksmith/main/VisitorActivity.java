package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Visitor;

public class VisitorActivity extends Activity {
    public static DisplayHelper dh;
    public static Visitor visitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        dh = DisplayHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        int visitorId = Integer.parseInt(intent.getStringExtra(DisplayHelper.VISITOR_TO_LOAD));

        if (visitorId > 0) {
            visitor = Visitor.findById(Visitor.class, visitorId);
            createVisitorInterface();
        }
    }

    public void createVisitorInterface() {
        // Display visitor info
        // Display visitor demands
    }

    public void closeVisitor(View view) {
        finish();
    }
}
