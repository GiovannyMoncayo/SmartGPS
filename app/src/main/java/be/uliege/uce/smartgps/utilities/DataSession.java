package be.uliege.uce.smartgps.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import static android.content.Context.MODE_PRIVATE;

public class DataSession {

    private static String PREFS_KEYS = "smartGps.preference";

    public static void saveDataSession(@NonNull Context context, String keyPref, String valor){
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEYS, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString(keyPref, valor);
        editor.commit();
    }

    public static String returnDataSession(@NonNull Context context, String keyPref){
        SharedPreferences preference = context.getSharedPreferences(PREFS_KEYS, MODE_PRIVATE);
        return preference.getString(keyPref, null);
    }

    public static boolean onSession(Context context, String keyPref) {
        if(returnDataSession(context, keyPref) == null){
            return false;
        }
        return true;
    }

    public static void deleteDataSession(@NonNull Context context, String keyPref){
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEYS, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.remove(keyPref);
        editor.commit();
    }

    public static void clearDataSession(@NonNull Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEYS, MODE_PRIVATE);
        settings.edit().clear().apply();
    }

}
