package be.uliege.uce.smartgps.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class DataSession {

    private static String PREFS_KEYS = "smartmobuce.preference";

    public static void saveDataSession(Context context, String keyPref, String valor){
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEYS, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString(keyPref, valor);
        editor.commit();
    }

    public static String returnDataSession(Context context, String keyPref){
        SharedPreferences preference = context.getSharedPreferences(PREFS_KEYS, MODE_PRIVATE);
        return preference.getString(keyPref, null);
    }


    public static void deleteDataSession(Context context, String keyPref){
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEYS, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.remove(keyPref);
        editor.commit();
    }

    public static void clearDataSession(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEYS, MODE_PRIVATE);
        settings.edit().clear().apply();
    }

    public static boolean onSession(Context context, String keyPref) {
        if(returnDataSession(context, keyPref) == null){
            return false;
        }
        return true;
    }
}
