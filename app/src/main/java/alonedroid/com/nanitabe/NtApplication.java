package alonedroid.com.nanitabe;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parse.Parse;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.UiThread;

import lombok.Getter;
import rx.Subscription;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

@EApplication
public class NtApplication extends Application {

    @Getter
    private static PublishSubject<Fragment> mainRouter = PublishSubject.create();

    @Getter
    private static BehaviorSubject<Fragment> router = BehaviorSubject.create();

    @Getter
    private RequestQueue queue;

    private Subscription routerSubscription;

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
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "0vu3h6v603HXARCYzJbXAbUr6m6Ka4cOrC0MV29T", "i8CjRU0b0cr98cugViPAx6lu8xoUm84VaUkUs3fi");
    }

    @Background
    public void start() {
        this.queue.start();
        this.routerSubscription = router.subscribe(mainRouter::onNext);
    }

    @Background
    public void stop() {
        this.queue.stop();
        this.routerSubscription.unsubscribe();
        mainRouter.onNext(null);
    }

    public void request(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this.queue.add(new StringRequest(Request.Method.GET, url, listener, errorListener));
    }

    public void hiddelKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
    }

    public int getColor(int color) {
        return getResources().getColor(color);
    }
}