package alonedroid.com.nanitabe.scene.search;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import lombok.Getter;
import rx.subjects.BehaviorSubject;

@EBean
class NtWebViewClient extends WebViewClient {

    @StringRes
    String topUrl;

    @StringRes
    String errorUrl;

    @Getter
    private BehaviorSubject<Boolean> loading = BehaviorSubject.create(false);

    @Getter
    private BehaviorSubject<String> url = BehaviorSubject.create("");

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        if (url.startsWith(this.topUrl)) {
            this.loading.onNext(true);
            this.url.onNext(url);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        this.loading.onNext(false);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        view.loadUrl(this.errorUrl);
    }
}