package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.percentlayout.widget.PercentRelativeLayout;

import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.ParticleHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Super_Upgrade;
import uk.co.jakelee.blacksmith.model.Upgrade;

public class UpgradeActivity extends Activity {
    private static DisplayHelper dh;
    private boolean superSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);
        superSelected = getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).getBoolean("upgradeTab", false);

        createInterface();
    }

    @Override
    public void onStop() {
        super.onStop();
        getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).edit().putBoolean("upgradeTab", superSelected).apply();
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, superSelected ? HelpActivity.TOPICS.Super_Upgrade : HelpActivity.TOPICS.Upgrade);
        startActivity(intent);
    }

    public void toggleTab(View view) {
        superSelected = !superSelected;
        updateTabs();
        createInterface();
        ParticleHelper.getInstance(this).triggerExplosion((PercentRelativeLayout)findViewById(R.id.upgrade), view, ParticleHelper.FEW);
    }

    private void updateTabs() {
        if (superSelected) {
            (findViewById(R.id.upgradeTab)).setAlpha(1f);
            (findViewById(R.id.superTab)).setAlpha(0.3f);
        } else {
            (findViewById(R.id.upgradeTab)).setAlpha(0.3f);
            (findViewById(R.id.superTab)).setAlpha(1f);
        }
    }

    private void createInterface() {
        updateTabs();

        if (superSelected) {
            createSuperInterface();
        } else {
            createUpgradeInterface();
        }
    }

    private void createSuperInterface() {
        List<Super_Upgrade> upgrades = Select.from(Super_Upgrade.class).orderBy("prestige_level, name ASC").list();
        TableLayout upgradeTable = (TableLayout) findViewById(R.id.upgradeTable);
        upgradeTable.removeAllViews();

        upgradeTable.addView(createCollectionText());
        upgradeTable.addView(createPrestigeText());

        for (Super_Upgrade upgrade : upgrades) {
            TableRow row = createSuperUpgradeRow(upgrade.getSuperUpgradeId());
            row.addView(createSuperUpgradeText(upgrade));
            row.addView(createSuperUpgradeImage(upgrade.isEnabled()));
            upgradeTable.addView(row);
        }
    }

    private TableRow createSuperUpgradeRow(int upgradeId) {
        TableRow row = new TableRow(this);
        row.setTag(upgradeId);
        row.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                toggleSetting(v, (int) v.getTag());
            }
        });
        return row;
    }

    private TextView createSuperUpgradeText(Super_Upgrade upgrade) {
        TextView superUpgrade = dh.createTextView(upgrade.getName(this) + "\n", 24);
        superUpgrade.setSingleLine(false);
        if (!upgrade.havePrestigeLevel()) {
            superUpgrade.setPaintFlags(superUpgrade.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        return superUpgrade;
    }

    private ImageView createSuperUpgradeImage(boolean enabled) {
        ImageView upgradeStatus = new ImageView(getApplicationContext());
        Drawable statusDrawable = dh.createDrawable((enabled ? R.drawable.tick : R.drawable.cross), 35, 35);
        upgradeStatus.setImageDrawable(statusDrawable);
        return upgradeStatus;
    }

    private TextView createCollectionText() {
        return dh.createTextView(String.format(getString(R.string.superUpgradeCollections),
                Player_Info.getCollectionsCrafted(),
                Constants.MAX_SUPGRADES_ENABLED
        ), 20);
    }

    private TextView createPrestigeText() {
        return dh.createTextView(String.format(getString(R.string.superUpgradePrestiges),
                Player_Info.getPrestige(),
                Super_Upgrade.totalUnlocked(),
                Super_Upgrade.total()
        ), 20);
    }

    private void toggleSetting(View view, int superUpgradeId) {
        Super_Upgrade upgrade = Super_Upgrade.find(superUpgradeId);
        if (!upgrade.havePrestigeLevel()) {
            ToastHelper.showErrorToast(view, Toast.LENGTH_SHORT, String.format(getString(R.string.superUpgradePrestigeLevel), upgrade.getPrestigeLevel()), false);
            return;
        } else if (!upgrade.isEnabled() && (Super_Upgrade.totalEnabled() >= Super_Upgrade.maxEnabled())) {
            ToastHelper.showErrorToast(view, Toast.LENGTH_SHORT, getString(ErrorHelper.errors.get(Constants.ERROR_MAXIMUM_SUPER_UPGRADE)), false);
            return;
        }

        upgrade.setEnabled(!upgrade.isEnabled());
        upgrade.save();
        ToastHelper.showPositiveToast(view, Toast.LENGTH_SHORT, String.format(getString(R.string.superUpgradeStatusChange),
                upgrade.getName(this),
                getString(upgrade.isEnabled() ? R.string.superUpgradeEnabled : R.string.superUpgradeDisabled)
        ), true);

        createSuperInterface();
    }

    private void createUpgradeInterface() {
        List<Upgrade> upgrades = Select.from(Upgrade.class).orderBy("name ASC").list();
        TableLayout upgradeTable = (TableLayout) findViewById(R.id.upgradeTable);
        upgradeTable.removeAllViews();

        for (Upgrade upgrade : upgrades) {
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
                    upgrade.getName(this),
                    upgrade.getCurrent(),
                    upgrade.getMaximum(),
                    upgrade.getUnits(),
                    upgrade.increases() ? "+" : "-",
                    upgrade.getIncrement(),
                    upgrade.getUnits(),
                    upgrade.getUpgradeCost());
        } else {
            return String.format(getString(R.string.upgradeCompletedDescription),
                    upgrade.getName(this),
                    upgrade.getCurrent(),
                    upgrade.getMaximum(),
                    upgrade.getUnits());
        }
    }

    private void upgradeOnClick(View v) {
        Upgrade selectedUpgrade = Upgrade.findById(Upgrade.class, (long) v.getTag());
        if (!selectedUpgrade.isAtMaximum()) {
            AlertDialogHelper.confirmUpgrade(this, this, selectedUpgrade);
        } else {
            ToastHelper.showErrorToast(findViewById(R.id.upgradeTitle), ToastHelper.SHORT, getString(ErrorHelper.errors.get(Constants.ERROR_MAXIMUM_UPGRADE)), false);
        }
    }

    public void alertDialogCallback() {
        createUpgradeInterface();
    }

    public void closePopup(View view) {
        finish();
    }
}
