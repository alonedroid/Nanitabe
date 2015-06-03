package alonedroid.com.nanitabe.utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class NtRecipeItem {

    public static final String URL = "url";

    public static final String IMAGE = "image";

    public static final String TITLE = "title";

    public static final String DATE = "date";

    @Getter
    @Setter
    private boolean checked;

    JSONObject root;

    public static NtRecipeItem newInstance(String root) {
        return new NtRecipeItem(root);
    }

    public NtRecipeItem(String root) {
        this.root = json(root);
    }

    public NtRecipeItem(JSONObject root) {
        this.root = root;
    }

    public String getImageUrl() {
        return getValue(IMAGE);
    }

    public String getUrl() {
        return getValue(URL);
    }

    public String getTitle() {
        return getValue(TITLE);
    }

    public String getDate() {
        return getValue(DATE);
    }

    public void setDate() {
        setValue(DATE, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    private String getValue(String str) {
        try {
            return this.root.getString(str);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.root.toString();
    }

    private void setValue(String key, String value) {
        try {
            this.root.put(key, value);
        } catch (JSONException e) {

        }
    }

    private JSONObject json(String str) {
        try {
            return new JSONObject(str);
        } catch (JSONException e) {
            return null;
        }
    }
}