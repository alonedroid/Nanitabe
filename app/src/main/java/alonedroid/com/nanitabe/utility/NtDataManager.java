package alonedroid.com.nanitabe.utility;

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

    public enum TABLE {
        FAVORITE, HISTORY
    }

    @Pref
    FavoriteData_ favorite;

    private JSONObject root;

    private TABLE table;

    public NtDataManager table(TABLE table) throws JSONException {
        this.table = table;

        if (table == TABLE.FAVORITE) {
            this.root = json(this.favorite.all().get());
        }
        if (table == TABLE.HISTORY) {
            this.root = json(this.favorite.history().get());
        }
        return this;
    }

    public NtDataManager find(String key) throws JSONException {
        this.root = this.root.getJSONObject(key);
        return this;
    }

    public List<String> selectKeys() {
        return arrayToString(this.root.names());
    }

    public List<NtRecipeItem> selectAll() {
        return Observable.from(selectKeys())
                .map(this::item)
                .toList().toBlocking().single();
    }

    private NtRecipeItem item(String key) {
        try {
            return new NtRecipeItem(this.root.getString(key));
        } catch (JSONException e) {
            return new NtRecipeItem("{}");
        }
    }

    public boolean exists(String url) {
        return this.root.has(url);
    }

    public void insert(String url, String title, String image) throws JSONException {
        JSONObject json = simpleJson();
        json.put(NtRecipeItem.URL, url);
        json.put(NtRecipeItem.TITLE, title);
        json.put(NtRecipeItem.IMAGE, image);
        this.root.put(url, json);
        save();
    }

    public void log(String key) throws JSONException {
        NtRecipeItem item = new NtRecipeItem(json(this.favorite.all().get()).getJSONObject(key));
        item.addDate();
        this.root.put(item.getDate() + key, item.toString());
        save();
    }

    public void delete(String url) {
        this.root.remove(url);
        save();
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

    private void save() {
        if (this.table == TABLE.FAVORITE) {
            this.favorite.all().put(this.root.toString());
        }
        if (this.table == TABLE.HISTORY) {
            this.favorite.history().put(this.root.toString());
        }
    }

    private JSONObject json(String str) throws JSONException {
        return new JSONObject(str);
    }

    private JSONObject simpleJson() throws JSONException {
        return json("{}");
    }
}