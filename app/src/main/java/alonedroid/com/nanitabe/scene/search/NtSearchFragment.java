package alonedroid.com.nanitabe.scene.search;

import android.app.Fragment;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.R;
import alonedroid.com.nanitabe.scene.top.NtTopFragment;
import alonedroid.com.nanitabe.utility.NtDataManager;
import rx.subscriptions.CompositeSubscription;

@EFragment(R.layout.fragment_nt_search)
public class NtSearchFragment extends Fragment {

    @FragmentArg
    String argQuery;

    @FragmentArg
    String argRecipeId;

    @StringRes
    String searchUrl;

    @StringRes
    String recipeUrl;

    @StringRes
    String topUrl;

    @StringRes
    String notFavorite;

    @StringRes
    String alreadyFavorite;

    @ViewById
    WebView ntSearchWeb;

    @ViewById
    TextView ntSearchFavorite;

    @Bean
    NtWebViewClient webClient;

    @Bean
    NtWebChromeClient chromeClient;

    @Bean
    NtDataManager dataManager;

    @Bean
    NtSearchDialog dialog;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public static NtSearchFragment newInstance(String query, String id) {
        NtSearchFragment_.FragmentBuilder_ builder_ = NtSearchFragment_.builder();
        builder_.argQuery(query);
        builder_.argRecipeId(id);
        return builder_.build();
    }

    @AfterViews
    void initViews() {
        initSubject();
        initWebView(generateUrl());
    }

    private void initSubject() {
        this.compositeSubscription.add(this.webClient.getUrl()
                .map(url -> url.matches(this.topUrl + "/recipe/[0-9]{1,9}"))
                .subscribe(this.ntSearchFavorite::setEnabled));
        this.compositeSubscription.add(this.webClient.getUrl()
                .map(this.dataManager::exists)
                .subscribe(existsUrl -> {
                    this.ntSearchFavorite.setEnabled(!existsUrl);
                    this.ntSearchFavorite.setText(existsUrl ? this.notFavorite : this.alreadyFavorite);
                }));
        this.compositeSubscription.add(this.webClient.getLoading()
                .subscribe(this.dialog.getLoading()::onNext));
    }

    private String generateUrl() {
        if (!TextUtils.isEmpty(this.argRecipeId)) return this.recipeUrl + this.argRecipeId;
        if (!TextUtils.isEmpty(this.argQuery)) return this.searchUrl + this.argQuery;
        return this.topUrl;
    }

    private void initWebView(String initUrl) {
        this.ntSearchWeb.getSettings().setJavaScriptEnabled(true);
        this.ntSearchWeb.setWebViewClient(this.webClient);
        this.ntSearchWeb.setWebChromeClient(this.chromeClient);
        this.ntSearchWeb.loadUrl(initUrl);
    }

    @Click
    void ntSearchFavorite() {
        if (this.dataManager.exists(this.webClient.getUrl().getValue())) {
            removeFavorite();
        } else {
            addFavorite();
        }
    }

    private void addFavorite() {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:alert(")
                .append("document.querySelector('.recipe_title').innerText").append("+','+")
                .append("document.querySelector('.photo').src")
                .append(");");

        this.ntSearchWeb.loadUrl(sb.toString());
    }

    private void removeFavorite() {
        this.dataManager.remove(this.webClient.getUrl().getValue());
    }

    @Click
    void ntSearchTop() {
        NtApplication.getRouter().onNext(NtTopFragment.newInstance());
    }

    @Override
    public void onStop() {
        this.compositeSubscription.clear();
        this.dialog.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        this.ntSearchWeb.loadUrl("about:blank");
        this.ntSearchWeb.stopLoading();
        this.ntSearchWeb.clearCache(true);
        this.ntSearchWeb.clearFormData();
        this.ntSearchWeb.clearHistory();
        this.ntSearchWeb.setWebChromeClient(null);
        this.ntSearchWeb.setWebViewClient(null);
        ((ViewGroup) this.ntSearchWeb.getParent()).removeView(this.ntSearchWeb);
        this.ntSearchWeb.removeAllViews();
        this.ntSearchWeb.destroy();
        this.ntSearchWeb = null;
        super.onDestroy();
    }
}