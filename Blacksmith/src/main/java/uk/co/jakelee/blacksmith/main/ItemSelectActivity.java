package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Item;

public class ItemSelectActivity extends Activity {
    public static String INTENT_ID = "uk.co.jakelee.blacksmith.itemSelectID";
    private DisplayHelper dh;
    private List<Item> items;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemselect);
        dh = DisplayHelper.getInstance(getApplicationContext());

        this.items = dh.itemSelectionItems;
        displayItemsList();
    }

    private void displayItemsList() {
        LinearLayout itemsContainer = (LinearLayout) findViewById(R.id.itemsTable);
        int itemIndex = 0;
        for (Item item : items) {
            TextViewPixel itemName = dh.createTextView(item.getName(), 22);
            itemName.setTag(itemIndex++);
            itemName.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickItem(v);
                }
            });

            itemsContainer.addView(itemName);
        }
    }

    public void clickItem(View view) {
        int position = (int)view.getTag();
        dh.itemSelectionFlipper.setDisplayedChild(position);
        dh.itemSelectionDots.addDots(dh, dh.itemSelectionFlipper.getChildCount(), dh.itemSelectionFlipper.getDisplayedChild());
        this.finish();
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Item_Picker);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
