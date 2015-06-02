package alonedroid.com.nanitabe.scene.search;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.Window;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import lombok.Getter;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

@EBean
public class NtSearchDialog {

    @RootContext
    Context context;

    @Getter
    BehaviorSubject<Boolean> loading = BehaviorSubject.create(false);

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private Dialog dialog;

    @AfterInject
    void init() {
        this.dialog = new Dialog(this.context);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog.setContentView(new NtWaitSurfaceView(this.context));
        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.setOnKeyListener((dialog, keyCode, event) -> filterEvent(keyCode));
    }

    @AfterViews
    void initViews() {
        this.compositeSubscription.add(this.loading
                .filter(loading -> loading)
                .subscribe(loading -> this.dialog.show()));
        this.compositeSubscription.add(this.loading
                .filter(loading -> !loading)
                .subscribe(loading -> this.dialog.dismiss()));
    }

    private boolean filterEvent(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_SEARCH:
                return true;
            default:
                return false;
        }
    }

    public void clear() {
        this.compositeSubscription.clear();
    }
}