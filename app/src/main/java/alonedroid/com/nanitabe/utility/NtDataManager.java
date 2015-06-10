package alonedroid.com.nanitabe.utility;

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

@EBean(scope = EBean.Scope.Singleton)
public class NtDataManager {

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
            NtRecipeItem item = new NtRecipeItem(this.rootObject.getJSONObject(url));
            item.setDate();
            this.historyObject.put(item.getDate() + url, item.toString());
            saveHistory();
        } catch (JSONException e) {
        }
    }

    public void put(String url, String title, String image) throws JSONException {
        JSONObject json = simpleJson();
        json.put(NtRecipeItem.URL, url);
        json.put(NtRecipeItem.TITLE, title);
        json.put(NtRecipeItem.IMAGE, image);
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

    public String getHistory(String key) {
        return value(this.historyObject, key);
    }

    public String getUrl(String key) {
        return value(recipe(key), NtRecipeItem.URL);
    }

    public String getTitle(String key) {
        return value(recipe(key), NtRecipeItem.TITLE);
    }

    public String getImage(String key) {
        return value(recipe(key), NtRecipeItem.IMAGE);
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