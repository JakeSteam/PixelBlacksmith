package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Locale;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ParticleHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Assistant;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;

import static com.orm.query.Select.from;

import androidx.percentlayout.widget.PercentRelativeLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class AssistantActivity extends Activity {
    private DisplayHelper dh;
    private ViewPager mViewPager;
    private AssistantPagerAdapter mCustomPagerAdapter;
    private int numAssistants = 0;
    private int selectedAssistant = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);
        dh = DisplayHelper.getInstance(this);

        mCustomPagerAdapter = new AssistantPagerAdapter(this);
        numAssistants = (int) Assistant.count(Assistant.class);

        mViewPager = (ViewPager) findViewById(R.id.assistantScroller);
        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(30, 20, 30, 20);
        mViewPager.setPageMargin(-200);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                selectedAssistant = position + 1;
                displayAssistantInfo();
            }
        });
        mViewPager.setAdapter(mCustomPagerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        dh.updateFullscreen(this);
        displayAssistantInfo();
    }

    public void buttonClick(View v) {
        Assistant assistant = Assistant.get(selectedAssistant);
        Player_Info activeAssistant = from(Player_Info.class).where(Condition.prop("name").eq("ActiveAssistant")).first();
        Player_Info lastClaim = Select.from(Player_Info.class).where(Condition.prop("name").eq("LastAssistantClaim")).first();
        if (assistant.getObtained() > 0) {
            if (assistant.getAssistantId() != activeAssistant.getIntValue()) {
                lastClaim.setLongValue(System.currentTimeMillis());
                lastClaim.save();
                activeAssistant.setIntValue(selectedAssistant);
                activeAssistant.save();
                displayAssistantInfo();
                ToastHelper.showPositiveToast(v, ToastHelper.SHORT, String.format(Locale.ENGLISH, getString(R.string.assistantAlertSelected), assistant.getTypeName(this)), true);
            }
        } else {
            if (assistant.getLevelRequired() > Player_Info.getPlayerLevel()) {
                ToastHelper.showErrorToast(v, ToastHelper.SHORT, getString(R.string.error_player_level), false);
            } else if (assistant.getCoinsRequired() > Inventory.getCoins()) {
                ToastHelper.showErrorToast(v, ToastHelper.SHORT, getString(R.string.error_not_enough_coins), false);
            } else {
                AlertDialogHelper.confirmBuyAssistant(this, this, assistant);
            }
        }

        ParticleHelper.getInstance(this).triggerExplosion((PercentRelativeLayout)findViewById(R.id.petContainer), v, ParticleHelper.MANY);
    }

    public void displayAssistantInfo() {
        Assistant assistant = Assistant.get(selectedAssistant);
        Item rewardItem = Item.findById(Item.class, assistant.getRewardItem());

        ((TextView) findViewById(R.id.assistantName)).setText(assistant.getObtained() == 0L ?
                assistant.getTypeName(this) :
                String.format(Locale.ENGLISH, getString(R.string.assistantName),
                        assistant.getName().equals("") ? assistant.getTypeName(this) : assistant.getName()));
        int progress = assistant.getLevelProgress();
        ((TextView) findViewById(R.id.assistantSpecs)).setText(String.format(Locale.ENGLISH, getString(R.string.assistantSpecs),
                assistant.getTier() + 1,
                assistant.getTypeName(this)));
        ((TextView) findViewById(R.id.assistantOverallProgress)).setText(String.format(Locale.ENGLISH, getString(R.string.assistantOverallProgressText),
                assistant.getAssistantId(),
                Assistant.count(Assistant.class)));
        ((ProgressBar) findViewById(R.id.assistantProgress)).setProgress(progress);
        ((TextView) findViewById(R.id.assistantProgressText)).setText(String.format(Locale.ENGLISH, getString(R.string.assistantProgressText),
                assistant.getLevel(),
                assistant.getMaxLevel(),
                progress));

        ((TextView) findViewById(R.id.assistantDesc)).setText(String.format(Locale.ENGLISH, getString(R.string.assistantDesc),
                assistant.getRewardQuantity(),
                rewardItem.getFullName(this, assistant.getRewardState()),
                DateHelper.getHoursMinsRemaining(assistant.getRewardFrequency()),
                assistant.getBoost(1) * 100d,
                assistant.getRewardQuantity() * assistant.getLevel(),
                assistant.getBoost() * 100d));
        ((TextView) findViewById(R.id.mainButton)).setText(getButtonText(assistant));
    }

    public void nameChange(View v) {
        Assistant assistant = Assistant.get(selectedAssistant);
        if (assistant != null && assistant.getObtained() > 0) {
            AlertDialogHelper.enterAssistantName(this, Assistant.get(selectedAssistant));
        }
    }

    public void unlockAssistant() {
        mViewPager.setAdapter(mCustomPagerAdapter);
        mViewPager.setCurrentItem(selectedAssistant - 1);
        ToastHelper.showPositiveToast(null, ToastHelper.SHORT, String.format(Locale.ENGLISH, getString(R.string.assistantUnlockAlert),
                Assistant.get(selectedAssistant).getTypeName(this)), true);
        displayAssistantInfo();
    }

    public void displayAssistantProgress(View v) {
        Assistant assistant = Assistant.get(selectedAssistant);
        ToastHelper.showTipToast(v, ToastHelper.SHORT, String.format(Locale.ENGLISH, getString(R.string.assistantProgressAlert),
                assistant.getLevel() + 1,
                assistant.getCurrentXp(),
                Assistant.getXpForLevel(assistant.getLevelModifier(), assistant.getLevel() + 1)), false);
    }

    private String getButtonText(Assistant assistant) {
        if (assistant.getObtained() == 0) {
            if (assistant.getLevelRequired() > Player_Info.getPlayerLevel()) {
                return getString(R.string.assistantButtonLockedLevel) + " " + assistant.getLevelRequired();
            } else {
                return getString(R.string.assistantButtonLockedCoins) + " " + assistant.getCoinsRequired();
            }
        } else {
            if (from(Player_Info.class).where(Condition.prop("name").eq("ActiveAssistant")).first().getIntValue() == selectedAssistant) {
                return getString(R.string.assistantButtonSelected);
            } else {
                return getString(R.string.assistantButtonSelect);
            }
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Assistants);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }

    class AssistantPagerAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;

        public AssistantPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return numAssistants;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.custom_pager_item, container, false);
            Assistant assistant = Assistant.get(position + 1);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(DisplayHelper.getAssistantDrawableID(container.getContext(), assistant));

            if (assistant != null && assistant.getObtained() > 0L) {
                imageView.clearColorFilter();
            } else {
                imageView.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            }

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
