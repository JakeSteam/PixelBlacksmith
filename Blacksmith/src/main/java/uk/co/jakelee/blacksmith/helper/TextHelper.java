package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;

public class TextHelper {
    private static TextHelper thInstance = null;
    private final Resources resources;
    private final String packageName;

    public TextHelper(Context context) {
        this.resources = context.getResources();
        this.packageName = context.getPackageName();
    }

    public static TextHelper getInstance(Context ctx) {
        if (thInstance == null) {
            thInstance = new TextHelper(ctx.getApplicationContext());
        }
        return thInstance;
    }

    public static List<String> getTips(Context context) {
        List<String> tips = new ArrayList<>();
        tips.add(context.getString(R.string.tip_1));
        tips.add(context.getString(R.string.tip_2));
        tips.add(context.getString(R.string.tip_3));
        tips.add(context.getString(R.string.tip_4));
        tips.add(context.getString(R.string.tip_5));
        tips.add(context.getString(R.string.tip_6));
        tips.add(context.getString(R.string.tip_7));
        tips.add(context.getString(R.string.tip_8));
        tips.add(context.getString(R.string.tip_9));
        tips.add(context.getString(R.string.tip_10));
        tips.add(context.getString(R.string.tip_11));
        tips.add(context.getString(R.string.tip_12));
        tips.add(context.getString(R.string.tip_13));
        tips.add(context.getString(R.string.tip_14));
        tips.add(context.getString(R.string.tip_15));
        tips.add(context.getString(R.string.tip_16));
        tips.add(context.getString(R.string.tip_17));
        tips.add(context.getString(R.string.tip_18));
        tips.add(context.getString(R.string.tip_19));
        tips.add(context.getString(R.string.tip_20));
        tips.add(context.getString(R.string.tip_21));
        tips.add(context.getString(R.string.tip_22));
        tips.add(context.getString(R.string.tip_23));
        tips.add(context.getString(R.string.tip_24));
        tips.add(context.getString(R.string.tip_25));
        tips.add(context.getString(R.string.tip_26));
        tips.add(context.getString(R.string.tip_27));
        tips.add(context.getString(R.string.tip_28));
        tips.add(context.getString(R.string.tip_29));
        tips.add(context.getString(R.string.tip_30));
        tips.add(context.getString(R.string.tip_31));
        tips.add(context.getString(R.string.tip_32));
        tips.add(context.getString(R.string.tip_33));
        tips.add(context.getString(R.string.tip_34));
        tips.add(context.getString(R.string.tip_35));
        tips.add(context.getString(R.string.tip_36));
        tips.add(context.getString(R.string.tip_37));
        tips.add(context.getString(R.string.tip_38));
        tips.add(context.getString(R.string.tip_39));
        tips.add(context.getString(R.string.tip_40));
        tips.add(context.getString(R.string.tip_41));
        tips.add(context.getString(R.string.tip_42));
        tips.add(context.getString(R.string.tip_43));
        tips.add(context.getString(R.string.tip_44));
        tips.add(context.getString(R.string.tip_45));
        tips.add(context.getString(R.string.tip_46));
        tips.add(context.getString(R.string.tip_47));
        tips.add(context.getString(R.string.tip_48));
        tips.add(context.getString(R.string.tip_49));
        tips.add(context.getString(R.string.tip_50));
        tips.add(context.getString(R.string.tip_51));
        tips.add(context.getString(R.string.tip_52));
        tips.add(context.getString(R.string.tip_53));
        tips.add(context.getString(R.string.tip_54));
        tips.add(context.getString(R.string.tip_55));
        tips.add(context.getString(R.string.tip_56));
        tips.add(context.getString(R.string.tip_57));
        tips.add(context.getString(R.string.tip_58));
        tips.add(context.getString(R.string.tip_59));
        tips.add(context.getString(R.string.tip_60));
        tips.add(context.getString(R.string.tip_61));
        tips.add(context.getString(R.string.tip_62));
        tips.add(context.getString(R.string.tip_63));
        tips.add(context.getString(R.string.tip_64));
        tips.add(context.getString(R.string.tip_65));
        tips.add(context.getString(R.string.tip_66));
        tips.add(context.getString(R.string.tip_67));
        tips.add(context.getString(R.string.tip_68));
        tips.add(context.getString(R.string.tip_69));
        tips.add(context.getString(R.string.tip_70));
        tips.add(context.getString(R.string.tip_71));
        tips.add(context.getString(R.string.tip_72));
        tips.add(context.getString(R.string.tip_73));
        tips.add(context.getString(R.string.tip_74));
        tips.add(context.getString(R.string.tip_75));
        tips.add(context.getString(R.string.tip_76));
        tips.add(context.getString(R.string.tip_77));
        tips.add(context.getString(R.string.tip_78));
        tips.add(context.getString(R.string.tip_79));
        tips.add(context.getString(R.string.tip_80));
        return tips;
    }

    public String getText(String identifier) {
        int resourceId = resources.getIdentifier(identifier, "string", packageName);
        return resourceId > 0 ? resources.getText(resourceId).toString() : identifier;
    }
}
