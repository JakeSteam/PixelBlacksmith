package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Hero_Adventure;
import uk.co.jakelee.blacksmith.model.Hero_Resource;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Worker;
import uk.co.jakelee.blacksmith.model.Worker_Resource;

public class WorkerActivity extends Activity {
    private static DisplayHelper dh;
    private static final Handler handler = new Handler();
    private boolean heroesSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        final Runnable everyFiveSeconds = new Runnable() {
            @Override
            public void run() {
                scheduledTask();
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND * 5);
            }
        };
        handler.post(everyFiveSeconds);

        dh.updateFullscreen(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        handler.removeCallbacksAndMessages(null);
    }

    private void populateWorkers() {
        LinearLayout container = (LinearLayout) findViewById(R.id.workerContainer);
        container.removeAllViews();

        List<Worker> workers = Select.from(Worker.class).orderBy("purchased DESC, level_unlocked ASC").list();
        for (Worker worker : workers) {
            container.addView(createWorkerRow(worker));
        }
    }

    private void populateHeroes() {
        LinearLayout container = (LinearLayout) findViewById(R.id.workerContainer);
        container.removeAllViews();

        List<Hero> heroes = Select.from(Hero.class).orderBy("purchased DESC, level_unlocked ASC").list();
        for (Hero hero : heroes) {
            container.addView(createHeroRow(hero));
        }
    }

    private RelativeLayout createHeroRow(Hero hero) {
        RelativeLayout heroRoot = createHeroRow();
        ImageView heroCharacter = (ImageView) heroRoot.findViewById(R.id.heroCharacter);
        ImageView heroFood = (ImageView) heroRoot.findViewById(R.id.heroFood);
        TextView heroCharacterText = (TextView) heroRoot.findViewById(R.id.heroCharacterText);
        ImageView heroAdventure = (ImageView) heroRoot.findViewById(R.id.heroAdventure);
        LinearLayout heroResourceContainer = (LinearLayout) heroRoot.findViewById(R.id.heroResource);
        final TextView heroButton = (TextView) heroRoot.findViewById(R.id.heroButton);

        Hero_Adventure adventure = Select.from(Hero_Adventure.class).where(Condition.prop("adventure_id").eq(hero.getCurrentAdventure())).first();

        final WorkerActivity activity = this;

        if (hero.isPurchased()) {
            heroCharacter.setImageResource(DisplayHelper.getVisitorDrawableID(this, hero.getVisitorId()));
            heroCharacter.setTag(hero);
            heroCharacter.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    String heroTimesCompleted = WorkerHelper.getTimesCompletedString(activity, (Hero) v.getTag());
                    ToastHelper.showToast(activity.findViewById(R.id.workerTitle), ToastHelper.SHORT, heroTimesCompleted, false);
                }
            });
            heroCharacterText.setText(WorkerHelper.isReady(hero) ? R.string.workerStatusReady : R.string.workerStatusBusy);

            int resourceID = R.drawable.transparent;
            if (hero.getFoodItem() > 0) {
                resourceID = DisplayHelper.getItemDrawableID(this, hero.getFoodItem());
            }
            if (WorkerHelper.isReady(hero)) {
                heroCharacter.setTag(hero);
                heroCharacter.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        Hero hero = (Hero) v.getTag();
                        if (WorkerHelper.isReady(hero)) {
                            Intent intent = new Intent(activity, EquipmentActivity.class);
                            intent.putExtra(WorkerHelper.INTENT_ID, hero.getHeroId());
                            startActivity(intent);
                        }
                    }
                });
            }
            heroFood.setImageResource(resourceID);
            heroFood.setVisibility(View.VISIBLE);

            heroAdventure.setImageResource(DisplayHelper.getAdventureDrawableID(this, adventure.getSubcategory()));
            heroAdventure.setTag(hero);
            heroAdventure.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Hero hero = (Hero) v.getTag();
                    if (WorkerHelper.isReady(hero)) {
                        Intent intent = new Intent(activity, AdventureActivity.class);
                        intent.putExtra(WorkerHelper.INTENT_ID, hero.getHeroId());
                        startActivity(intent);
                    }
                }
            });

            heroResourceContainer.setTag(hero);
            heroResourceContainer.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Hero hero = (Hero) v.getTag();
                    if (hero.isPurchased()) {
                        List<Hero_Resource> resources = WorkerHelper.getResourcesByAdventure((int) hero.getCurrentAdventure());
                        ToastHelper.showToast(activity.findViewById(R.id.workerTitle), ToastHelper.LONG, String.format(getString(R.string.workerResources),
                                WorkerHelper.getRewardResourcesText(hero, resources, false)), false);
                    }
                }
            });
            WorkerHelper.populateResources(dh, heroResourceContainer, hero.getCurrentAdventure());

            heroButton.setText(WorkerHelper.getButtonText(hero));
            heroButton.setTag(hero);
            heroButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Hero hero = (Hero) v.getTag();
                    if (WorkerHelper.sendOutHero(hero)) {
                        scheduledTask();
                    } else {
                        String exactTimeLeft = WorkerHelper.getTimeLeftString(activity, hero);
                        ToastHelper.showToast(heroButton, ToastHelper.SHORT, exactTimeLeft, false);
                    }
                }
            });
        } else if (hero.getLevelUnlocked() <= Player_Info.getPlayerLevel()) {
            heroCharacter.setImageResource(R.drawable.item52);
            heroCharacterText.setText(String.format(getString(R.string.workerCoins),
                    WorkerHelper.getBuyCost(hero)));
            heroButton.setText(R.string.buyHero);
            heroButton.setTag(hero);
            heroButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    AlertDialogHelper.confirmBuyHero(getApplicationContext(), activity, (Hero) v.getTag());
                }
            });
        } else {
            heroCharacter.setImageResource(R.drawable.lock);
            heroCharacterText.setText(String.format(getString(R.string.slotLevel), hero.getLevelUnlocked()));
        }

        return heroRoot;
    }

    public void toggleTab(View view) {
        heroesSelected = !heroesSelected;
        updateTabs();
        createInterface();
    }

    private void createInterface() {
        if (heroesSelected) {
            populateHeroes();
        } else {
            populateWorkers();
        }

        updateButtons();
    }

    private void updateTabs() {
        if (heroesSelected) {
            (findViewById(R.id.heroesTab)).setAlpha(0.3f);
            (findViewById(R.id.workersTab)).setAlpha(1f);
        } else {
            (findViewById(R.id.heroesTab)).setAlpha(1f);
            (findViewById(R.id.workersTab)).setAlpha(0.3f);
        }
    }

    private RelativeLayout createWorkerRow(Worker worker) {
        RelativeLayout workerRoot = createWorkerRow();
        ImageView workerCharacter = (ImageView) workerRoot.findViewById(R.id.workerCharacter);
        ImageView workerFood = (ImageView) workerRoot.findViewById(R.id.workerFood);
        TextView workerCharacterText = (TextView) workerRoot.findViewById(R.id.workerCharacterText);
        ImageView workerTool = (ImageView) workerRoot.findViewById(R.id.workerTool);
        TextView workerToolText = (TextView) workerRoot.findViewById(R.id.workerToolText);
        LinearLayout workerResourceContainer = (LinearLayout) workerRoot.findViewById(R.id.workerResource);
        final TextView workerButton = (TextView) workerRoot.findViewById(R.id.workerButton);

        Item tool = Item.findById(Item.class, worker.getToolUsed());
        final WorkerActivity activity = this;

        if (worker.isPurchased()) {
            workerCharacter.setImageResource(DisplayHelper.getCharacterDrawableID(this, worker.getCharacterID()));
            workerCharacter.setTag(worker);
            workerCharacter.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    String workerTimesCompleted = WorkerHelper.getTimesCompletedString(activity, (Worker) v.getTag());
                    ToastHelper.showToast(activity.findViewById(R.id.workerTitle), ToastHelper.SHORT, workerTimesCompleted, false);
                }
            });
            workerCharacterText.setText(WorkerHelper.isReady(worker) ? R.string.workerStatusReady : R.string.workerStatusBusy);

            int resourceID = R.drawable.transparent;
            if (worker.getFoodUsed() > 0) {
                resourceID = DisplayHelper.getItemDrawableID(this, worker.getFoodUsed());
            }
            if (WorkerHelper.isReady(worker)) {
                workerFood.setTag(worker);
                workerFood.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        Worker worker = (Worker) v.getTag();
                        if (WorkerHelper.isReady(worker)) {
                            Intent intent = new Intent(activity, FoodActivity.class);
                            intent.putExtra(WorkerHelper.INTENT_ID, worker.getWorkerID());
                            startActivity(intent);
                        }
                    }
                });
            }
            workerFood.setImageResource(resourceID);
            workerFood.setVisibility(View.VISIBLE);

            workerTool.setImageResource(DisplayHelper.getItemDrawableID(this, worker.getToolUsed()));
            workerTool.setTag(worker);
            workerTool.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Worker worker = (Worker) v.getTag();
                    if (WorkerHelper.isReady(worker)) {
                        Intent intent = new Intent(activity, ToolActivity.class);
                        intent.putExtra(WorkerHelper.INTENT_ID, worker.getWorkerID());
                        startActivity(intent);
                    }
                }
            });
            workerToolText.setText(String.format(getString(R.string.workerTool), tool.getName()));

            workerResourceContainer.setTag(worker);
            workerResourceContainer.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Worker worker = (Worker) v.getTag();
                    if (worker.isPurchased()) {
                        List<Worker_Resource> resources = WorkerHelper.getResourcesByTool((int) worker.getToolUsed());
                        ToastHelper.showToast(activity.findViewById(R.id.workerTitle), ToastHelper.LONG, String.format(getString(R.string.workerResources),
                                WorkerHelper.getRewardResourcesText(worker, resources, false)), false);
                    }
                }
            });
            WorkerHelper.populateResources(dh, workerResourceContainer, worker.getToolUsed());

            workerButton.setText(WorkerHelper.getButtonText(worker));
            workerButton.setTag(worker);
            workerButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Worker worker = (Worker) v.getTag();
                    if (WorkerHelper.sendOutWorker(worker)) {
                        scheduledTask();
                    } else {
                        String exactTimeLeft = WorkerHelper.getTimeLeftString(activity, worker);
                        ToastHelper.showToast(workerButton, ToastHelper.SHORT, exactTimeLeft, false);
                    }
                }
            });
        } else if (worker.getLevelUnlocked() <= Player_Info.getPlayerLevel()) {
            workerCharacter.setImageResource(R.drawable.item52);
            workerCharacterText.setText(String.format(getString(R.string.workerCoins),
                    WorkerHelper.getBuyCost(worker)));
            workerButton.setText(R.string.buyWorker);
            workerButton.setTag(worker);
            workerButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    AlertDialogHelper.confirmBuyWorker(getApplicationContext(), activity, (Worker) v.getTag());
                }
            });
        } else {
            workerCharacter.setImageResource(R.drawable.lock);
            workerCharacterText.setText(String.format(getString(R.string.slotLevel), worker.getLevelUnlocked()));
        }

        return workerRoot;
    }

    public void toggleAutofeed(View v) {
        Setting autofeed = Setting.findById(Setting.class, Constants.SETTING_AUTOFEED);
        if (autofeed != null) {
            autofeed.setBoolValue(!autofeed.getBoolValue());
            autofeed.save();
        }

        updateButtons();
    }

    private void updateButtons() {
        ImageView autofeedToggle = (ImageView) findViewById(R.id.autoFeedIndicator);
        TextView sendOutWorkers = (TextView) findViewById(R.id.gatherAllButton);

        Setting autofeedSetting = Setting.findById(Setting.class, Constants.SETTING_AUTOFEED);
        if (autofeedSetting != null) {
            Drawable tick = dh.createDrawable(R.drawable.tick, 30, 30);
            Drawable cross = dh.createDrawable(R.drawable.cross, 30, 30);

            boolean autofeedToggleValue = autofeedSetting.getBoolValue();
            autofeedToggle.setImageDrawable(autofeedToggleValue ? tick : cross);
        }

        if (!heroesSelected && Worker.getAvailableWorkersCount() > 0) {
            sendOutWorkers.setAlpha(1f);
        } else if (heroesSelected && Hero.getAvailableHeroesCount() > 0) {
            sendOutWorkers.setAlpha(1f);
        } else {
            sendOutWorkers.setAlpha(0.3f);
        }
    }

    public void sendAllGathering(View v) {
        // For each available worker, send out worker.
        if (heroesSelected) {
            List<Hero> heroes = Hero.getAvailableHeroes();
            int numHeroes = 0;
            for (Hero hero : heroes) {
                if (WorkerHelper.sendOutHero(hero)) {
                    numHeroes++;
                }
            }

            if (numHeroes > 0) {
                ToastHelper.showPositiveToast(v, ToastHelper.LONG, String.format(getString(R.string.sendOutHeroesToast), numHeroes), true);
            }
        } else {
            List<Worker> workers = Worker.getAvailableWorkers();
            int numWorkers = 0;
            for (Worker worker : workers) {
                if (WorkerHelper.sendOutWorker(worker)) {
                    numWorkers++;
                }
            }

            if (numWorkers > 0) {
                ToastHelper.showPositiveToast(v, ToastHelper.LONG, String.format(getString(R.string.sendOutWorkersToast), numWorkers), true);
            }
        }
        updateButtons();
    }

    public void scheduledTask() {
        if (heroesSelected) {
            WorkerHelper.checkForFinishedHeroes(this);
            populateHeroes();
        } else {
            WorkerHelper.checkForFinishedWorkers(this);
            populateWorkers();
        }
        updateButtons();
        updateTabs();
    }

    private RelativeLayout createWorkerRow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View inflatedView = inflater.inflate(R.layout.custom_worker, null);
        return (RelativeLayout) inflatedView.findViewById(R.id.workerRow);
    }

    private RelativeLayout createHeroRow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View inflatedView = inflater.inflate(R.layout.custom_hero, null);
        return (RelativeLayout) inflatedView.findViewById(R.id.heroRow);
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Worker);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
