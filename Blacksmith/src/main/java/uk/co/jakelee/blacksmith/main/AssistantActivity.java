package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Locale;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.TextHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Assistant;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;

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
        dh.updateFullscreen(this);

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
        displayAssistantInfo();
    }

    public void buttonClick(View v) {
        Assistant assistant = Assistant.get(selectedAssistant);
        if (selectedAssistant == assistant.getAssistantId()) {

        } else if (assistant.getObtained() > 0) {
            Player_Info activeAssistant = Select.from(Player_Info.class).where(Condition.prop("name").eq("ActiveAssistant")).first();
            activeAssistant.setIntValue(selectedAssistant);
            activeAssistant.save();
            displayAssistantInfo();
            ToastHelper.showPositiveToast(v, ToastHelper.SHORT, String.format(Locale.ENGLISH, getString(R.string.assistantAlertSelected), assistant.getName(this)), true);
        } else {
            if (assistant.getCoinsRequired() > Inventory.getCoins()) {
                ToastHelper.showErrorToast(v, ToastHelper.SHORT, getString(R.string.error_not_enough_coins), false);
            } else {
                AlertDialogHelper.confirmBuyAssistant(this, this, assistant);
            }
        }
    }

    public void displayAssistantInfo() {
        Assistant assistant = Assistant.get(selectedAssistant);
        ((TextView)findViewById(R.id.assistantName)).setText(TextHelper.getInstance(this).getText("assistant_name_" + selectedAssistant));
        ((TextView)findViewById(R.id.assistantDesc)).setText(TextHelper.getInstance(this).getText("assistant_desc_" + selectedAssistant));
        ((TextView)findViewById(R.id.mainButton)).setText(getButtonText(assistant));
    }

    private String getButtonText(Assistant assistant) {
        if (assistant.getObtained() == 0) {
            if (assistant.getLevelRequired() > Player_Info.getPlayerLevel()) {
                return getString(R.string.assistantButtonLockedLevel) + " " + assistant.getLevelRequired();
            } else {
                return getString(R.string.assistantButtonLockedCoins) + " " + assistant.getCoinsRequired();
            }
        } else {
            if (Player_Info.getActivePet() == selectedAssistant) {
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

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(DisplayHelper.getAssistantDrawableID(container.getContext(), position+1));

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
