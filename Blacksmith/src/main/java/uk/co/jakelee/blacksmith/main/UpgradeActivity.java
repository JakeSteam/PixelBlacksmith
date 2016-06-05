package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
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
        dh.updateFullscreen(this);

        createUpgradeInterface();
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Upgrade);
        startActivity(intent);
    }

    private void createUpgradeInterface() {
        List<Upgrade> upgrades = Select.from(Upgrade.class).orderBy("name ASC").list();
        TableLayout upgradeTable = (TableLayout) findViewById(R.id.upgradeTable);
        upgradeTable.removeAllViews();

        for (final Upgrade upgrade : upgrades) {
            if (upgrade.getName().equals("Legendary Chance") && !Player_Info.isPremium()) {
                continue;
            }

            TableRow upgradeRow = new TableRow(this);
            TextViewPixel upgradeDescription = dh.createTextView(getUpgradeText(upgrade), 20, upgrade.isAtMaximum() ? Color.parseColor("#267c18") : Color.BLACK);
            ImageView upgradeButton = getUpgradeButton(upgrade);

            upgradeRow.addView(upgradeDescription);
            upgradeRow.addView(upgradeButton);
            upgradeTable.addView(upgradeRow);

        }
    }

    private ImageView getUpgradeButton(final Upgrade upgrade) {
        ImageView upgradeButton = dh.createImageView("", upgrade.isAtMaximum() ? "tick" : "uparrow", 50, 50);
        upgradeButton.setTag(upgrade.getId());
        upgradeButton.setPadding(0, 0, 0, dh.convertDpToPixel(15));
        upgradeButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                upgradeOnClick(v);
            }
        });

        return upgradeButton;
    }

    private String getUpgradeText(Upgrade upgrade) {
        if (!upgrade.isAtMaximum()) {
            return String.format(getString(R.string.upgradeDescription),
                    upgrade.getName(),
                    upgrade.getCurrent(),
                    upgrade.getMaximum(),
                    upgrade.getUnits(),
                    upgrade.increases() ? "+" : "-",
                    upgrade.getIncrement(),
                    upgrade.getUnits(),
                    upgrade.getUpgradeCost());
        } else {
            return String.format(getString(R.string.upgradeCompletedDescription),
                    upgrade.getName(),
                    upgrade.getCurrent(),
                    upgrade.getMaximum(),
                    upgrade.getUnits());
        }
    }

    private void upgradeOnClick(View v) {
        Upgrade selectedUpgrade = Upgrade.findById(Upgrade.class, (long) v.getTag());
        if (!selectedUpgrade.isAtMaximum()){
            AlertDialogHelper.confirmUpgrade(this, this, selectedUpgrade);
        } else {
            ToastHelper.showErrorToast(this, ToastHelper.SHORT, ErrorHelper.errors.get(Constants.ERROR_MAXIMUM_UPGRADE), false);
        }
    }

    public void alertDialogCallback() {
        createUpgradeInterface();
    }

    public void closePopup(View view) {
        finish();
    }
}
