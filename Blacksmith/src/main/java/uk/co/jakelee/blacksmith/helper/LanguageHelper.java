package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Locale;

public class LanguageHelper {
    public static void updateLanguage(Context ctx)
    {
        String lang = PreferenceManager.getDefaultSharedPreferences(ctx).getString("locale", "");
        updateLanguage(ctx, lang);
    }

    public static void changeLanguage(Context context, int language) {
        updateLanguage(context, getLocaleById(language));
    }

    private static void updateLanguage(Context ctx, String lang)
    {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("locale", lang).apply();
        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang)) {
            cfg.locale = new Locale(lang);
        } else {
            cfg.locale = Locale.getDefault();
        }

        ctx.getResources().updateConfiguration(cfg, null);
    }

    private static String getLocaleById(int id) {
        switch (id) {
            case 1: return "en";
            case 2: return "fr";
            case 3: return "ru";
        }
        return "";
    }

    public static String getLanguageById(Context context, int id) {
        return TextHelper.getInstance(context).getText("language_" + id);
    }

    public static String getFlagById(int id) {
        switch (id) {
            case 1:
                return new String(Character.toChars(0x1F1EC)) + new String(Character.toChars(0x1F1E7));
            case 2:
                return new String(Character.toChars(0x1F1EB)) + new String(Character.toChars(0x1F1F7));
            case 3:
                return new String(Character.toChars(0x1F1F7)) + new String(Character.toChars(0x1F1FA));
            default:
                return "";
        }
    }
}
