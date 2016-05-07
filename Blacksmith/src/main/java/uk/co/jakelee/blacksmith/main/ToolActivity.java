package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Inventory;

public class ToolActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);
        dh = DisplayHelper.getInstance(getApplicationContext());

        createDropdown();
        populateTools();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String selectedItem = (String) parent.getItemAtPosition(pos);
        populateTools(selectedItem);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        populateTools();
    }

    private void createDropdown() {
        Spinner toolSelector = (Spinner) findViewById(R.id.toolTypes);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.toolsArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toolSelector.setAdapter(adapter);
    }

    private void populateTools() {
        populateTools("Pickaxe (Ore)");
    }

    private void populateTools(String selection) {
        TableLayout toolHolder = (TableLayout) findViewById(R.id.toolHolder);
        List<Inventory> tools = WorkerHelper.getTools(selection);
        for (Inventory tool : tools) {

        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Tool);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
