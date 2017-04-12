package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.components.Hero_Set;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.HeroSetHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.State;
import uk.co.jakelee.blacksmith.model.Tier;
import uk.co.jakelee.blacksmith.model.Type;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class EquipmentActivity extends Activity {
    private static DisplayHelper dh;
    private Hero hero;
    private Visitor_Type vType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);
        dh = DisplayHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        int heroId = intent.getIntExtra(WorkerHelper.INTENT_ID, 0);
        hero = Hero.findById(heroId);
    }

    @Override
    public void onResume() {
        super.onResume();
        dh.updateFullscreen(this);
        hero = Hero.findById(hero.getHeroId());
        vType = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());

        populateEquipment();
        populateSets();
        populatePreferences();
    }

    private void populateEquipment() {
        ((TextViewPixel) findViewById(R.id.totalStrength)).setText(String.format(getString(R.string.heroTotalStrength), WorkerHelper.getTotalStrength(hero, vType)));

        if (hero.getFoodItem() > 0) {
            ((ImageView) findViewById(R.id.foodImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getFoodItem(), hero.getFoodState(), 25, 25, true, true));
            WorkerHelper.setStrengthText(vType, (TextViewPixel) findViewById(R.id.foodStrength), hero.getFoodItem(), hero.getFoodState());
        }

        if (hero.getHelmetItem() > 0) {
            ((ImageView) findViewById(R.id.helmetImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getHelmetItem(), hero.getHelmetState(), 25, 25, true, true));
            WorkerHelper.setStrengthText(vType, (TextViewPixel) findViewById(R.id.helmetStrength), hero.getHelmetItem(), hero.getHelmetState());
        }

        if (hero.getArmourItem() > 0) {
            ((ImageView) findViewById(R.id.armourImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getArmourItem(), hero.getArmourState(), 25, 25, true, true));
            WorkerHelper.setStrengthText(vType, (TextViewPixel) findViewById(R.id.armourStrength), hero.getArmourItem(), hero.getArmourState());
        }

        if (hero.getWeaponItem() > 0) {
            ((ImageView) findViewById(R.id.weaponImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getWeaponItem(), hero.getWeaponState(), 25, 25, true, true));
            WorkerHelper.setStrengthText(vType, (TextViewPixel) findViewById(R.id.weaponStrength), hero.getWeaponItem(), hero.getWeaponState());
        }

        if (hero.getVisitorId() > 0) {
            ((ImageView) findViewById(R.id.heroImage)).setImageDrawable(dh.createDrawable(DisplayHelper.getVisitorDrawableID(this, hero.getVisitorId()), 25, 25));
            ((TextViewPixel) findViewById(R.id.heroName)).setText(vType.getName(this) + " (" + vType.getAdventuresCompleted() + ")");
        } else {
            ((TextViewPixel) findViewById(R.id.heroName)).setText(R.string.workerStatusSelectHero);
        }

        if (hero.getShieldItem() > 0) {
            ((ImageView) findViewById(R.id.shieldImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getShieldItem(), hero.getShieldState(), 25, 25, true, true));
            WorkerHelper.setStrengthText(vType, (TextViewPixel) findViewById(R.id.shieldStrength), hero.getShieldItem(), hero.getShieldState());
        }

        if (hero.getGlovesItem() > 0) {
            ((ImageView) findViewById(R.id.glovesImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getGlovesItem(), hero.getGlovesState(), 25, 25, true, true));
            WorkerHelper.setStrengthText(vType, (TextViewPixel) findViewById(R.id.glovesStrength), hero.getGlovesItem(), hero.getGlovesState());
        }

        if (hero.getBootsItem() > 0) {
            ((ImageView) findViewById(R.id.bootsImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getBootsItem(), hero.getBootsState(), 25, 25, true, true));
            WorkerHelper.setStrengthText(vType, (TextViewPixel) findViewById(R.id.bootsStrength), hero.getBootsItem(), hero.getBootsState());
        }

        if (hero.getRingItem() > 0) {
            ((ImageView) findViewById(R.id.ringImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getRingItem(), hero.getRingState(), 25, 25, true, true));
            WorkerHelper.setStrengthText(vType, (TextViewPixel) findViewById(R.id.ringStrength), hero.getRingItem(), hero.getRingState());
        }
    }

    private void populateSets() {
        List<Hero_Set> sets = HeroSetHelper.getCurrentSets(hero);
        LinearLayout setContainer = (LinearLayout) findViewById(R.id.heroSets);
        setContainer.removeAllViews();
        if (sets.size() > 0) {
            for (Hero_Set set : sets) {
                View textView = dh.createTextView(String.format("%1$s (+%2$s%%)", set.getName(), set.getBonus()), 30);
                textView.setTag(String.format(Locale.ENGLISH, getString(R.string.hero_set_bonus), set.getName(), set.getBonus(), set.getDescription()));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastHelper.showTipToast(view, Toast.LENGTH_SHORT, (String) view.getTag(), true);
                    }
                });
                setContainer.addView(textView);
            }
        } else {
            setContainer.addView(dh.createTextView(getString(R.string.hero_no_set_bonus), 35));
        }
    }

    public void equipmentClick(View view) {
        Intent intent = new Intent(this, EquipmentSelectActivity.class);
        intent.putExtra(WorkerHelper.INTENT_TYPE, (String) view.getTag());
        intent.putExtra(WorkerHelper.INTENT_HERO, hero.getHeroId());
        startActivity(intent);
    }

    public void visitorClick(View view) {
        Intent intent = new Intent(this, VisitorSelectActivity.class);
        intent.putExtra(WorkerHelper.INTENT_HERO, hero.getHeroId());
        startActivity(intent);
    }

    private void populatePreferences() {
        if (vType == null) {
            return;
        }
        ImageView typePic = (ImageView) findViewById(R.id.typeImage);
        TextViewPixel typeMultiplier = (TextViewPixel) findViewById(R.id.typeBonus);
        ImageView tierPic = (ImageView) findViewById(R.id.tierImage);
        TextViewPixel tierMultiplier = (TextViewPixel) findViewById(R.id.tierBonus);
        ImageView statePic = (ImageView) findViewById(R.id.stateImage);
        TextViewPixel stateMultiplier = (TextViewPixel) findViewById(R.id.stateBonus);

        int typeDrawableId = getApplicationContext().getResources().getIdentifier("type" + vType.getTypePreferred(), "drawable", getApplicationContext().getPackageName());
        typePic.setImageResource(typeDrawableId);
        typePic.setTag(R.id.preferred, vType.getTypePreferred());
        typePic.setTag(R.id.multiplier, VisitorHelper.multiplierToPercent(vType.getTypeMultiplier()));
        typeMultiplier.setText(VisitorHelper.multiplierToPercent(vType.getTypeMultiplier()));

        int tierDrawableId = getApplicationContext().getResources().getIdentifier("tier" + vType.getTierPreferred(), "drawable", getApplicationContext().getPackageName());
        tierPic.setImageResource(tierDrawableId);
        tierPic.setTag(R.id.preferred, vType.getTierPreferred());
        tierPic.setTag(R.id.multiplier, VisitorHelper.multiplierToPercent(vType.getTierMultiplier()));
        tierMultiplier.setText(VisitorHelper.multiplierToPercent(vType.getTierMultiplier()));

        int stateDrawableId = getApplicationContext().getResources().getIdentifier("state" + vType.getStatePreferred(), "drawable", getApplicationContext().getPackageName());
        statePic.setImageResource(stateDrawableId);
        statePic.setTag(R.id.preferred, vType.getStatePreferred());
        statePic.setTag(R.id.multiplier, VisitorHelper.multiplierToPercent(vType.getStateMultiplier()));
        stateMultiplier.setText(VisitorHelper.multiplierToPercent(vType.getStateMultiplier()));
    }

    public void tierClick(View view) {
        if (hero.getVisitorId() > 0) {
            String preferred = Tier.findById(Tier.class, (long) view.getTag(R.id.preferred)).getName(this);
            VisitorHelper.displayPreference(this, view, R.string.tierPreferenceHero, preferred);
        }
    }

    public void typeClick(View view) {
        if (hero.getVisitorId() > 0) {
            String preferred = Type.findById(Type.class, (long) view.getTag(R.id.preferred)).getName(this);
            VisitorHelper.displayPreference(this, view, R.string.typePreferenceHero, preferred);
        }
    }

    public void stateClick(View view) {
        if (hero.getVisitorId() > 0) {
            String preferred = State.findById(State.class, (long) view.getTag(R.id.preferred)).getName(this);
            VisitorHelper.displayPreference(this, view, R.string.statePreferenceHero, preferred);
        }
    }


    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Hero_Equipment);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
