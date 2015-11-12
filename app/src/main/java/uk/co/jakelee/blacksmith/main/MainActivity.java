package uk.co.jakelee.blacksmith.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import uk.co.jakelee.blacksmith.sqlite.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    public static DatabaseHelper dbh;
    Button btnClosePopup;
    ImageButton btnCreatePopup;
    private PopupWindow pwindo;
    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pwindo.dismiss();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbh = new DatabaseHelper(getApplicationContext());
        btnCreatePopup = (ImageButton) findViewById(R.id.open_furnace);
        btnCreatePopup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initiatePopupWindow();
            }
        });
    }

    private void initiatePopupWindow() {
        try {
            LayoutInflater inflater = (LayoutInflater) MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.activity_furnace,
                    (ViewGroup) findViewById(R.id.furnace));
            pwindo = new PopupWindow(layout, 800, 1400, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
            btnClosePopup.setOnClickListener(cancel_button_click_listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void openFurnace(View view) {
        Intent intent = new Intent(this, FurnaceActivity.class);
        startActivity(intent);
    }

    public void openAnvil(View view) {
        Intent intent = new Intent(this, AnvilActivity.class);
        startActivity(intent);
    }


}
