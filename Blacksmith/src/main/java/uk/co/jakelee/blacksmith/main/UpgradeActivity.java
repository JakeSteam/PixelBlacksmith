package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Upgrade;

public class UpgradeActivity extends Activity {
    public static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        dh = DisplayHelper.getInstance(getApplicationContext());

        createUpgradeInterface();
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Upgrade);
        startActivity(intent);
    }

    public void createUpgradeInterface() {
        List<Upgrade> upgrades = Upgrade.listAll(Upgrade.class);
        TableLayout upgradeTable = (TableLayout) findViewById(R.id.upgradeTable);
        upgradeTable.removeAllViews();

        for (final Upgrade upgrade : upgrades) {
            TableRow upgradeRow = new TableRow(this);

            String upgradeDescriptionText = String.format("%s\n%d / %d %s\n%s coins",
                    upgrade.getName(),
                    upgrade.getCurrent(),
                    upgrade.getMaximum(),
                    upgrade.getUnits(),
                    upgrade.getUpgradeCost());
            TextViewPixel upgradeDescription = dh.createTextView(upgradeDescriptionText, 20);

            ImageView upgradeButton = dh.createImageView("", "uparrow", 50, 50);
            upgradeButton.setTag(upgrade.getId());
            upgradeButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Upgrade selectedUpgrade = Upgrade.findById(Upgrade.class, (long) v.getTag());
                    int upgradeResponse = selectedUpgrade.tryUpgrade();
                    if (upgradeResponse == Constants.SUCCESS) {
                        ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format("Upgraded %s!", upgrade.getName()));
                        Player_Info.increaseByOne(Player_Info.Statistic.UpgradesBought);
                        createUpgradeInterface();
                    } else {
                        ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(upgradeResponse));
                    }
                }
            });

            upgradeRow.addView(upgradeDescription);
            upgradeRow.addView(upgradeButton);
            upgradeTable.addView(upgradeRow);
        }
    }

    public void closePopup(View view) {
        finish();
    }
}
