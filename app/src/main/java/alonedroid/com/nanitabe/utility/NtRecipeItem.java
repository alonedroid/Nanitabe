package alonedroid.com.nanitabe.utility;

import org.json.JSONException;
import org.json.JSONObject;

public class NtRecipeItem {

    private static final String IMAGE = "image";

    private static final String TITLE = "title";

    private static final String DATE = "date";

    JSONObject root;

    public static NtRecipeItem newInstance(String root) {
        return new NtRecipeItem(root);
    }

    public NtRecipeItem(String root) {
        this.root = json(root);
    }

    public String getImageUrl() {
        return getValue(IMAGE);
    }

    public String getTitle() {
        return getValue(TITLE);
    }

    public String getDate() {
        return getValue(DATE);
    }

    private String getValue(String str) {
        try {
            return this.root.getString(str);
        } catch (JSONException e) {
            return null;
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