package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Worker;

public class ToolActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private static DisplayHelper dh;
    private Worker worker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);
        dh = DisplayHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        int workerID = (int) (long) intent.getLongExtra(WorkerHelper.INTENT_ID, 0);
        worker = Worker.findById(Worker.class, workerID);

        createDropdown();
        populateTools();
    }

    @Override
    public void onResume() {
        super.onResume();
        dh.updateFullscreen(this);
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
        toolSelector.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        List<String> tools = getToolStrings();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, tools);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        toolSelector.setAdapter(adapter);
        toolSelector.setOnItemSelectedListener(this);
    }

    private List<String> getToolStrings() {
        List<String> toolStrings = new ArrayList<>();
        toolStrings.add(getString(R.string.tool_pickaxe));
        toolStrings.add(getString(R.string.tool_hammer));
        toolStrings.add(getString(R.string.tool_fishing_rod));
        toolStrings.add(getString(R.string.tool_hatchet));
        toolStrings.add(getString(R.string.tool_gloves));
        toolStrings.add(getString(R.string.tool_gem));
        toolStrings.add(getString(R.string.tool_silver_ring));
        toolStrings.add(getString(R.string.tool_gold_ring));
        toolStrings.add(getString(R.string.tool_visage));
        return toolStrings;
    }

    private void populateTools() {
        populateTools(getString(R.string.tool_pickaxe));
    }

    private void populateTools(String selection) {
        TableLayout toolHolder = (TableLayout) findViewById(R.id.toolHolder);
        toolHolder.removeAllViews();
        TextView noToolsMessage = (TextView)findViewById(R.id.noTools);

        List<Inventory> tools = WorkerHelper.getTools(this, selection);
        if (tools.size() > 0) {
            noToolsMessage.setVisibility(View.GONE);
            for (Inventory tool : tools) {
                Item toolItem = Item.findById(Item.class, tool.getItem());
                ImageView itemImage = dh.createItemImage(tool.getItem(), (int)tool.getState(), 25, 25, true, true);
                TextView itemName = dh.createTextView(String.format(getString(R.string.genericQuantity),
                        tool.getQuantity(),
                        toolItem.getName(this)), 30);
                ImageView selectImage = new ImageView(this);
                selectImage.setImageDrawable(dh.createDrawable(R.drawable.open, 35, 35));

                TableRow row = new TableRow(this);
                row.setTag(R.id.itemID, tool.getItem());
                row.setTag(R.id.itemState, tool.getState());
                row.addView(itemImage);
                row.addView(itemName);
                row.addView(selectImage);
                row.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        selectTool(v);
                    }
                });
                toolHolder.addView(row);
            }
        } else {
            noToolsMessage.setVisibility(View.VISIBLE);
        }
    }

    public void selectTool(View v) {
        long itemID = (long) v.getTag(R.id.itemID);
        long itemState = (long) v.getTag(R.id.itemState);

        Inventory newItem = Inventory.getInventory(itemID, itemState);
        newItem.setQuantity(newItem.getQuantity() - 1);
        newItem.save();

        Inventory oldItem = Inventory.getInventory(worker.getToolUsed(), worker.getToolState());
        oldItem.setQuantity(oldItem.getQuantity() + 1);
        oldItem.save();

        worker.setToolUsed(newItem.getItem());
        worker.setToolState(newItem.getState());
        worker.save();

        this.finish();
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Helper_Tools);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
