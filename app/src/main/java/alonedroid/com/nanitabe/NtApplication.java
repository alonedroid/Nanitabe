package alonedroid.com.nanitabe;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.parse.Parse;
import com.parse.ParseCrashReporting;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.UiThread;

import alonedroid.com.nanitabe.activity.R;
import lombok.Getter;

@EApplication
public class NtApplication extends Application {

    @Getter
    private RequestQueue queue;

    public static int MODE;

    public static int MODE_NORMAL = 100;

    public static int MODE_RECEPTION = 200;

    public static int MODE_SHARE = 300;

    Toast toast;

    @UiThread
    public void show(String message) {
        this.toast.setText(message);
        this.toast.show();
    }

    @AfterInject
    void init() {
        this.toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        this.queue = Volley.newRequestQueue(this);
        initMbaas();
    }

    private void initMbaas() {
        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.mbaas_application_id), getString(R.string.mbaas_client_key));
    }

    @Background
    public void start() {
        this.queue.start();
    }

    @Background
    public void stop() {
        this.queue.stop();
    }

    public void hiddelKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
    }

    public void modeReception() {
        NtApplication.MODE = NtApplication.MODE_RECEPTION;
    }

    public void modeNormal() {
        NtApplication.MODE = NtApplication.MODE_NORMAL;
    }

    public void modeShare() {
        NtApplication.MODE = NtApplication.MODE_SHARE;
    }
}