package uk.co.jakelee.blacksmith.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import uk.co.jakelee.blacksmith.sqlite.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    public static DatabaseHelper dbh;

    // Furnace UI
    private PopupWindow furnacePopup;
    private ImageButton openFurnaceButton;
    private View.OnClickListener furnaceClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            openFurnace();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbh = new DatabaseHelper(getApplicationContext());

        openFurnaceButton = (ImageButton) findViewById(R.id.open_furnace);
        openFurnaceButton.setOnClickListener(furnaceClickListener);
    }

    private void openFurnace() {
        try {
            // Setting up popup functionality
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.activity_furnace, (ViewGroup) findViewById(R.id.furnace));

            // Create and display popup, then call the creator method
            furnacePopup = new PopupWindow(layout, 800, 1400, true);
            furnacePopup.showAtLocation(layout, Gravity.CENTER, 0, 0);
            FurnaceActivity furnace = new FurnaceActivity();
            furnace.createFurnaceInterface();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void openAnvil(View view) {
        Intent intent = new Intent(this, AnvilActivity.class);
        startActivity(intent);
    }


}
