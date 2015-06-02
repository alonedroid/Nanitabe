package alonedroid.com.nanitabe.utility;

import android.util.Log;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import alonedroid.com.nanitabe.sharedpreference.FavoriteData_;
import rx.Observable;

@EBean
public class NtDataManager {

    private static final String TITLE = "title";

    private static final String IMAGE = "image";

    @Pref
    FavoriteData_ favorite;

    private JSONObject rootObject;

    private JSONObject historyObject;

    @AfterInject
    void init() {
        try {
            this.rootObject = json(this.favorite.all().get());
            this.historyObject = json(this.favorite.history().get());
        } catch (JSONException e) {
            this.rootObject = null;
        }
    }

    public boolean has(String url) {
        return this.rootObject.has(url);
    }

    public void addHistory(String url) {
        try {
            this.historyObject.put(url, this.rootObject.getJSONObject(url));
            saveHistory();
        } catch (JSONException e) {
        }
    }

    public void put(String url, String title, String image) throws JSONException {
        JSONObject json = simpleJson();
        json.put(TITLE, title);
        json.put(IMAGE, image);
        this.rootObject.put(url, json);
        save();
    }

    public void remove(String url) {
        this.rootObject.remove(url);
        save();
    }

    public boolean exists(String url) {
        return this.rootObject.has(url);
    }

    public List<String> getKeys() {
        return arrayToString(this.rootObject.names());
    }

    public List<String> getHistory() {
        return arrayToString(this.historyObject.names());
    }

    private List<String> arrayToString(JSONArray array) {
        if (array == null) return new ArrayList<>();

        return Observable.range(0, array.length())
                .map(idx -> get(array, idx))
                .filter(url -> url != null)
                .toList().toBlocking().single();
    }

    public String get(JSONArray array, int position) {
        try {
            return (String) array.get(position);
        } catch (JSONException e) {
            return null;
        }
    }

    public String get(String key) {
        return value(this.rootObject, key);
    }

    public String getTitle(String key) {
        return value(recipe(key), TITLE);
    }

    public String getImage(String key) {
        return value(recipe(key), IMAGE);
    }

    private JSONObject recipe(String key) {
        try {
            return this.rootObject.getJSONObject(key);
        } catch (JSONException e) {
            return null;
        }
    }

    private String value(JSONObject json, String key) {
        if (json == null) return null;
        try {
            return json.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public void save() {
        this.favorite.all().put(this.rootObject.toString());
    }

    public void saveHistory() {
        this.favorite.history().put(this.historyObject.toString());
    }

    private JSONObject json(String str) throws JSONException {
        return new JSONObject(str);
    }

    private JSONObject simpleJson() throws JSONException {
        return json("{}");
    }
}