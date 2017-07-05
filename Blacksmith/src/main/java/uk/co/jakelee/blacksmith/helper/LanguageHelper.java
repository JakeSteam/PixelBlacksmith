package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Locale;

public class LanguageHelper {
    public static void updateLanguage(Context ctx) {
        String lang = PreferenceManager.getDefaultSharedPreferences(ctx).getString("locale", "");
        updateLanguage(ctx, lang);
    }

    public static void changeLanguage(Context context, int language) {
        updateLanguage(context, getLocaleById(language));
    }

    private static void updateLanguage(Context ctx, String lang) {
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
            case Constants.LANG_ENGLISH:
                return "en";
            case Constants.LANG_FRENCH:
                return "fr";
            case Constants.LANG_RUSSIAN:
                return "ru";
            case Constants.LANG_DUTCH:
                return "nl";
            case Constants.LANG_CHINESE:
                return "zh";
            case Constants.LANG_KOREAN:
                return "ko";
            case Constants.LANG_JAPANESE:
                return "ja";
            case Constants.LANG_SPANISH:
                return "es";
            case Constants.LANG_GERMAN:
                return "de";
            case Constants.LANG_PORT:
                return "pt";
        }
        return "";
    }

    public static int getDefaultLanguage() {
        switch (Locale.getDefault().getLanguage()) {
            case "en":
                return Constants.LANG_ENGLISH;
            case "fr":
                return Constants.LANG_FRENCH;
            case "ru":
                return Constants.LANG_RUSSIAN;
            case "nl":
                return Constants.LANG_DUTCH;
            case "zh":
                return Constants.LANG_CHINESE;
            case "ko":
                return Constants.LANG_KOREAN;
            case "ja":
                return Constants.LANG_JAPANESE;
            case "es":
                return Constants.LANG_SPANISH;
            case "de":
                return Constants.LANG_GERMAN;
            case "pt":
                return Constants.LANG_PORT;
        }
        return Constants.LANG_ENGLISH;
    }

    public static String getLanguageById(Context context, int id) {
        return TextHelper.getInstance(context).getText("language_" + id);
    }

    public static String getFlagById(int id) {
        switch (id) {
            case Constants.LANG_ENGLISH:
                return new String(Character.toChars(0x1F1EC)) + new String(Character.toChars(0x1F1E7));
            case Constants.LANG_FRENCH:
                return new String(Character.toChars(0x1F1EB)) + new String(Character.toChars(0x1F1F7));
            case Constants.LANG_RUSSIAN:
                return new String(Character.toChars(0x1F1F7)) + new String(Character.toChars(0x1F1FA));
            case Constants.LANG_DUTCH:
                return new String(Character.toChars(0x1F1F3)) + new String(Character.toChars(0x1F1F1));
            case Constants.LANG_CHINESE:
                return new String(Character.toChars(0x1F1E8)) + new String(Character.toChars(0x1F1F3));
            case Constants.LANG_KOREAN:
                return new String(Character.toChars(0x1F1F0)) + new String(Character.toChars(0x1F1F7));
            case Constants.LANG_JAPANESE:
                return new String(Character.toChars(0x1F1EF)) + new String(Character.toChars(0x1F1F5));
            case Constants.LANG_SPANISH:
                return new String(Character.toChars(0x1F1EA)) + new String(Character.toChars(0x1F1F8));
            case Constants.LANG_GERMAN:
                return new String(Character.toChars(0x1F1E9)) + new String(Character.toChars(0x1F1EA));
            case Constants.LANG_PORT:
                return new String(Character.toChars(0x1F1E7)) + new String(Character.toChars(0x1F1F7));
            default:
                return "";
        }
    }
}
