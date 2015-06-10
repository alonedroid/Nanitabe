package alonedroid.com.nanitabe.scene.search;

import android.content.Context;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.res.StringRes;
import org.json.JSONException;

import alonedroid.com.nanitabe.utility.NtDataManager;
import alonedroid.com.nanitabe.utility.NtDataManager_;

@EBean
public class NtWebChromeClient extends WebChromeClient {

    @RootContext
    Context context;

    @StringRes
    String topUrl;

    NtDataManager dataManager;

    @AfterInject
    void init() {
        this.dataManager = NtDataManager_.getInstance_(this.context);
    }

    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        result.confirm();

        try {
            String[] prms = message.split(",");
            String title = prms[0].split("by")[0].trim();
            String img = prms[1];
            String key = prms[2];

            this.dataManager.put(key, title, img);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}