package alonedroid.com.nanitabe.sharedpreference;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface FavoriteData {

    @DefaultString("{}")
    String all();

    @DefaultString("{}")
    String history();
}