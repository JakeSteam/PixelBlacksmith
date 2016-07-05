package uk.co.jakelee.blacksmith.helper;

import android.content.Context;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Super_Upgrade;

public class QuestHelper {
    public static String getQuestReward(Context context, String difficulty) {
        int xpAmount = 0;
        double rewardModifier = 0;
        int rewardPageCount = 0;
        switch (difficulty) {
            case "Elite" :
                xpAmount = Player_Info.getPlayerLevel() * Constants.QUEST_XP_MODIFIER_ELITE;
                rewardModifier = Constants.QUEST_REWARD_MODIFIER_ELITE;
                rewardPageCount = getPagesRewarded(Constants.QUEST_PAGE_CHANCE_ELITE);
                break;
            case "Hard" :
                xpAmount = Player_Info.getPlayerLevel() * Constants.QUEST_XP_MODIFIER_HARD;
                rewardModifier = Constants.QUEST_REWARD_MODIFIER_HARD;
                rewardPageCount = getPagesRewarded(Constants.QUEST_PAGE_CHANCE_HARD);
                break;
            case "Medium" :
                xpAmount = Player_Info.getPlayerLevel() * Constants.QUEST_XP_MODIFIER_MEDIUM;
                rewardModifier = Constants.QUEST_REWARD_MODIFIER_MEDIUM;
                rewardPageCount = getPagesRewarded(Constants.QUEST_PAGE_CHANCE_MEDIUM);
                break;
            case "Easy" :
                xpAmount = Player_Info.getPlayerLevel() * Constants.QUEST_XP_MODIFIER_EASY;
                rewardModifier = Constants.QUEST_REWARD_MODIFIER_EASY;
                rewardPageCount = getPagesRewarded(Constants.QUEST_PAGE_CHANCE_EASY);
                break;
        }

        // 75% chance to get a normal reward, 25% chance to get coin amount. All subject to multiplier.
        Item rewardItem = Item.findById(Item.class, Constants.ITEM_COINS);
        double minimumRewards = Constants.MINIMUM_REWARDS * rewardModifier;
        double maximumRewards = Constants.MAXIMUM_REWARDS * rewardModifier;
        if (VisitorHelper.getRandomBoolean(25)) {
            int typeID = VisitorHelper.pickRandomNumberFromArray(Constants.VISITOR_REWARD_TYPES);
            List<Item> matchingItems = Select.from(Item.class).where(Condition.prop("type").eq(typeID)).list();
            rewardItem = VisitorHelper.pickRandomItemFromList(matchingItems);
        } else {
            minimumRewards = Constants.MINIMUM_COIN_REWARDS * rewardModifier;
            maximumRewards = Constants.MAXIMUM_COIN_REWARDS * rewardModifier;
        }
        int rewardItemCount = VisitorHelper.getRandomNumber((int) minimumRewards, (int) maximumRewards);
        Inventory.addItem(rewardItem.getId(), Constants.STATE_NORMAL, rewardItemCount);

        Player_Info.addXp(xpAmount);

        if (rewardPageCount > 0) {
            List<Item> pages = Select.from(Item.class).where(Condition.prop("type").eq(Constants.TYPE_PAGE)).list();
            Item rewardPage = VisitorHelper.pickRandomItemFromList(pages);
            Inventory.addItem(rewardPage.getId(), Constants.STATE_NORMAL, rewardPageCount);

            return String.format(context.getString(R.string.questRewardPage),
                    xpAmount,
                    rewardItemCount,
                    rewardItem.getName(),
                    rewardPageCount,
                    rewardPage.getName());
        } else {
            return String.format(context.getString(R.string.questRewardNoPage),
                    xpAmount,
                    rewardItemCount,
                    rewardItem.getName());
        }
    }

    private static int getPagesRewarded(double pageChance) {
        if (Super_Upgrade.isEnabled(Constants.SU_QUEST_ELITE) && pageChance < Constants.QUEST_PAGE_CHANCE_ELITE) {
            return (int) Constants.QUEST_PAGE_CHANCE_ELITE;
        } else if (Super_Upgrade.isEnabled(Constants.SU_QUEST_HARD) && pageChance < Constants.QUEST_PAGE_CHANCE_HARD) {
            return (int) Constants.QUEST_PAGE_CHANCE_HARD;
        } else if (Super_Upgrade.isEnabled(Constants.SU_QUEST_MED) && pageChance < Constants.QUEST_PAGE_CHANCE_MEDIUM) {
            return (int) Constants.QUEST_PAGE_CHANCE_MEDIUM;
        }

        int pages;
        if ((int) pageChance >= 1) {
            pages = (int) pageChance;
        } else {
            int percentPageChance = (int) (pageChance * 100);
            pages = VisitorHelper.getRandomBoolean(100 - percentPageChance) ? 1 : 0;
        }

        return (Super_Upgrade.isEnabled(Constants.SU_PAGE_CHANCE) && pages < 1) ? 1 : pages;
    }
}
