package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmith.helper.TextHelper;

public class Contribution_Goal extends SugarRecord {
    private int goalId;
    private String name;
    private int reqContributions;
    private String teaserText;
    private String unlockedText;

    public Contribution_Goal() {
    }

    public Contribution_Goal(int goalId, String name, int reqContributions, String teaserText, String unlockedText) {
        this.goalId = goalId;
        this.name = name;
        this.reqContributions = reqContributions;
        this.teaserText = teaserText;
        this.unlockedText = unlockedText;
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("con_name_" + goalId);
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReqContributions() {
        return reqContributions;
    }

    public void setReqContributions(int reqContributions) {
        this.reqContributions = reqContributions;
    }

    public String getTeaserText(Context context) {
        return TextHelper.getInstance(context).getText("con_teaser_" + goalId);
    }

    public void setTeaserText(String teaserText) {
        this.teaserText = teaserText;
    }

    public String getUnlockedText(Context context) {
        return TextHelper.getInstance(context).getText("con_unlocked_" + goalId);
    }

    public void setUnlockedText(String unlockedText) {
        this.unlockedText = unlockedText;
    }
}
