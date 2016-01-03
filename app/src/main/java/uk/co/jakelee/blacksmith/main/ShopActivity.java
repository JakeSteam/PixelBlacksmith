package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;

public class ShopActivity extends Activity {
    public static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        dh = DisplayHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        String message = intent.getStringExtra(MineActivity.SHOP_TO_LOAD);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

        updateInventoryTable();
    }

    public void updateInventoryTable() {

    }

    public void closeShop(View view) {
        finish();
    }
}
