package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmith.helper.TextHelper;

public class Character extends SugarRecord {
    private Long id;
    private String name;
    private String intro;

    public Character() {
    }

    public Character(Long id, String name, String intro) {
        this.id = id;
        this.name = name;
        this.intro = intro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("character_name_" + id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro(Context context) {
        return TextHelper.getInstance(context).getText("character_intro_" + id);
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}
