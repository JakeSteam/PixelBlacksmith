package uk.co.jakelee.testing.anothertest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "uk.co.jakelee.testing.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openFurnace(View view) {
        Intent intent = new Intent(this, FurnaceActivity.class);
        String message = "This is a furnace";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void openAnvil(View view) {
        Intent intent = new Intent(this, FurnaceActivity.class);
        String message = "This is an anvil";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
