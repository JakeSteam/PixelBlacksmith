package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;

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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.toolsArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toolSelector.setAdapter(adapter);
        toolSelector.setOnItemSelectedListener(this);
    }

    private void populateTools() {
        populateTools("Pickaxe (Ore)");
    }

    private void populateTools(String selection) {
        TableLayout toolHolder = (TableLayout) findViewById(R.id.toolHolder);
        toolHolder.removeAllViews();

        List<Inventory> tools = WorkerHelper.getTools(selection);
        for (Inventory tool : tools) {
            Item toolItem = Item.findById(Item.class, tool.getItem());
            ImageView itemImage = dh.createItemImage(tool.getItem(), 25, 25, true, true);
            TextView itemName = dh.createTextView(toolItem.getName(), 22);

            TableRow row = new TableRow(this);
            row.addView(itemImage);
            row.addView(itemName);
            toolHolder.addView(row);
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
