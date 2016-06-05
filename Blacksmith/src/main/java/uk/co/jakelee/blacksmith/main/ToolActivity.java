package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
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
        dh.updateFullscreen(this);

        Intent intent = getIntent();
        int workerID = (int) (long) intent.getLongExtra(WorkerHelper.INTENT_ID, 0);
        worker = Worker.findById(Worker.class, workerID);

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
        TextView noToolsMessage = (TextView)findViewById(R.id.noTools);

        List<Inventory> tools = WorkerHelper.getTools(selection);
        if (tools.size() > 0) {
            noToolsMessage.setVisibility(View.GONE);
            for (Inventory tool : tools) {
                Item toolItem = Item.findById(Item.class, tool.getItem());
                ImageView itemImage = dh.createItemImage(tool.getItem(), 25, 25, true, true);
                TextView itemName = dh.createTextView(String.format(getString(R.string.genericQuantity),
                        tool.getQuantity(),
                        toolItem.getName()), 30);
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
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Worker_Tools);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
