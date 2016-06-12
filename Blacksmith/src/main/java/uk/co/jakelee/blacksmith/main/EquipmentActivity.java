package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Item;
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
        vType = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());
    }

    @Override
    public void onResume() {
        super.onResume();
        dh.updateFullscreen(this);

        populateEquipment();
        populatePreferences();
    }

    private void populateEquipment() {
        ((TextViewPixel) findViewById(R.id.totalStrength)).setText(String.format(getString(R.string.heroTotalStrength), WorkerHelper.getTotalStrength(hero)));

        ((ImageView) findViewById(R.id.foodImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getFoodItem(), 25, 25, true, true));
        ((TextViewPixel) findViewById(R.id.foodStrength)).setText("+" + Item.findById(Item.class, hero.getFoodItem()).getValue());

        ((ImageView) findViewById(R.id.helmetImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getHelmetItem(), 25, 25, true, true));
        ((TextViewPixel) findViewById(R.id.helmetStrength)).setText("+" + WorkerHelper.getBasePrice(hero.getHelmetItem(), hero.getHelmetState()));

        ((ImageView) findViewById(R.id.armourImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getArmourItem(), 25, 25, true, true));
        ((TextViewPixel) findViewById(R.id.armourStrength)).setText("+" + WorkerHelper.getBasePrice(hero.getArmourItem(), hero.getArmourState()));

        ((ImageView) findViewById(R.id.weaponImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getWeaponItem(), 25, 25, true, true));
        ((TextViewPixel) findViewById(R.id.weaponStrength)).setText("+" + WorkerHelper.getBasePrice(hero.getWeaponItem(), hero.getWeaponState()));

        ((ImageView) findViewById(R.id.heroImage)).setImageDrawable(dh.createDrawable(dh.getVisitorDrawableID(this, hero.getVisitorId()), 25, 25));
        ((TextViewPixel) findViewById(R.id.heroName)).setText(vType.getName() + " (" + vType.getAdventuresCompleted() + ")");

        ((ImageView) findViewById(R.id.shieldImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getShieldItem(), 25, 25, true, true));
        ((TextViewPixel) findViewById(R.id.shieldStrength)).setText("+" + WorkerHelper.getBasePrice(hero.getShieldItem(), hero.getShieldState()));

        ((ImageView) findViewById(R.id.glovesImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getGlovesItem(), 25, 25, true, true));
        ((TextViewPixel) findViewById(R.id.glovesStrength)).setText("+" + WorkerHelper.getBasePrice(hero.getGlovesItem(), hero.getGlovesState()));

        ((ImageView) findViewById(R.id.bootsImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getBootsItem(), 25, 25, true, true));
        ((TextViewPixel) findViewById(R.id.bootsStrength)).setText("+" + WorkerHelper.getBasePrice(hero.getBootsItem(), hero.getBootsState()));

        ((ImageView) findViewById(R.id.ringImage)).setImageDrawable(dh.createItemImageDrawable((long) hero.getRingItem(), 25, 25, true, true));
        ((TextViewPixel) findViewById(R.id.ringStrength)).setText("+" + WorkerHelper.getBasePrice(hero.getRingItem(), hero.getRingState()));
    }

    private void populatePreferences() {
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
        String preferred = Tier.findById(Tier.class, (long) view.getTag(R.id.preferred)).getName();
        VisitorHelper.displayPreference(this, view, R.string.tierPreference, preferred);
    }

    public void typeClick(View view) {
        String preferred = Type.findById(Type.class, (long) view.getTag(R.id.preferred)).getName();
        VisitorHelper.displayPreference(this, view, R.string.typePreference, preferred);
    }

    public void stateClick(View view) {
        String preferred = State.findById(State.class, (long) view.getTag(R.id.preferred)).getName();
        VisitorHelper.displayPreference(this, view, R.string.statePreference, preferred);
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
