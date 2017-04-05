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
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Pet;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class PetActivity extends Activity {
    private DisplayHelper dh;
    private ViewPager mViewPager;
    private PetPagerAdapter mCustomPagerAdapter;
    private int numPets = 0;
    private int selectedPet = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);
        dh = DisplayHelper.getInstance(this);
        dh.updateFullscreen(this);

        mCustomPagerAdapter = new PetPagerAdapter(this);
        numPets = (int)Pet.count(Pet.class);

        mViewPager = (ViewPager) findViewById(R.id.petScroller);
        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(30, 20, 30, 20);
        mViewPager.setPageMargin(-200);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                selectedPet = position + 1;
                displayPetInfo();
            }
        });
        mViewPager.setAdapter(mCustomPagerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        displayPetInfo();
    }

    public void buttonClick(View v) {
        Pet pet = Pet.get(selectedPet);
        if (pet.getObtained() > 0) {
            Player_Info activePet = Select.from(Player_Info.class).where(Condition.prop("name").eq("ActivePet")).first();
            activePet.setIntValue(selectedPet);
            activePet.save();
            displayPetInfo();
            ToastHelper.showPositiveToast(v, ToastHelper.SHORT, String.format(Locale.ENGLISH, getString(R.string.petAlertSelected), pet.getName(this)), true);
        } else {
            if (pet.getCoinsRequired() > Inventory.getCoins()) {
                ToastHelper.showErrorToast(v, ToastHelper.SHORT, getString(R.string.error_not_enough_coins), false);
            } else {
                AlertDialogHelper.confirmBuyPet(this, this, pet);
            }
        }
    }

    public void displayPetInfo() {
        Pet pet = Pet.get(selectedPet);
        ((TextView)findViewById(R.id.petName)).setText(TextHelper.getInstance(this).getText("pet_name_" + selectedPet));
        ((TextView)findViewById(R.id.petDesc)).setText(TextHelper.getInstance(this).getText("pet_desc_" + selectedPet));
        ((TextView)findViewById(R.id.mainButton)).setText(getButtonText(pet));
    }

    private String getButtonText(Pet pet) {
        if (pet.getObtained() == 0) {
            if (pet.getLevelRequired() > Player_Info.getPlayerLevel()) {
                return getString(R.string.petButtonLockedLevel) + " " + pet.getLevelRequired();
            } else {
                return getString(R.string.petButtonLockedCoins) + " " + pet.getCoinsRequired();
            }
        } else {
            if (Player_Info.getActivePet() == selectedPet) {
                return getString(R.string.petButtonDeselect);
            } else {
                return getString(R.string.petButtonSelect);
            }
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Pets);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }

    class PetPagerAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;

        public PetPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return numPets;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.custom_pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(DisplayHelper.getPetDrawableID(container.getContext(), position+1));

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
