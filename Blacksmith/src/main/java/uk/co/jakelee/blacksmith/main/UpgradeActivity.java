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
    private static DisplayHelper dh;

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

    private void createUpgradeInterface() {
        List<Upgrade> upgrades = Upgrade.listAll(Upgrade.class);
        TableLayout upgradeTable = (TableLayout) findViewById(R.id.upgradeTable);
        upgradeTable.removeAllViews();

        for (final Upgrade upgrade : upgrades) {
            if (upgrade.getName().equals("Legendary Chance") && !Player_Info.isPremium()) {
                continue;
            }

            TableRow upgradeRow = new TableRow(this);
            TextViewPixel upgradeDescription = dh.createTextView(getUpgradeText(upgrade), 20);
            ImageView upgradeButton = getUpgradeButton(upgrade);

            upgradeRow.addView(upgradeDescription);
            upgradeRow.addView(upgradeButton);
            upgradeTable.addView(upgradeRow);

        }
    }

    private ImageView getUpgradeButton(final Upgrade upgrade) {
        ImageView upgradeButton = dh.createImageView("", "uparrow", 50, 50);
        upgradeButton.setTag(upgrade.getId());
        upgradeButton.setPadding(0, 0, 0, dh.convertDpToPixel(15));
        upgradeButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                upgradeOnClick(v, upgrade);
            }
        });

        return upgradeButton;
    }

    private String getUpgradeText(Upgrade upgrade) {
        return String.format(getString(R.string.upgradeDescription),
                upgrade.getName(),
                upgrade.getCurrent(),
                upgrade.getMaximum(),
                upgrade.getUnits(),
                upgrade.increases() ? "+" : "-",
                upgrade.getIncrement(),
                upgrade.getUpgradeCost());
    }

    private void upgradeOnClick(View v, Upgrade upgrade) {
        Upgrade selectedUpgrade = Upgrade.findById(Upgrade.class, (long) v.getTag());
        int upgradeResponse = selectedUpgrade.tryUpgrade();
        if (upgradeResponse == Constants.SUCCESS) {
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format(getString(R.string.upgradeSuccess), upgrade.getName()), true);
            Player_Info.increaseByOne(Player_Info.Statistic.UpgradesBought);
            createUpgradeInterface();
        } else {
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(upgradeResponse), true);
        }
    }

    public void closePopup(View view) {
        finish();
    }
}
