package alonedroid.com.nanitabe.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;

import org.androidannotations.annotations.EBean;

@EBean
public class NtServicePreference {

    private SharedPreferences sharedPreferences;

    public NtServicePreference(Context context) {
        this.sharedPreferences = context.getSharedPreferences(NtServicePreference.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    public void putRecipes(String key, String value) {
        this.sharedPreferences.edit()
                .putString(key, value)
                .commit();
    }

    public String getRecipes(String key) {
        return this.sharedPreferences.getString(key, "");
    }
}