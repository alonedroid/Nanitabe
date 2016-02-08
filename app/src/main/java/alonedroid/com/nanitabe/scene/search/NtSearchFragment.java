package alonedroid.com.nanitabe.scene.search;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.activity.VariableActivity;
import alonedroid.com.nanitabe.utility.NtDataManager;
import rx.subscriptions.CompositeSubscription;

@EFragment(R.layout.fragment_nt_search)
public class NtSearchFragment extends Fragment implements VariableActivity.NtOnKeyDown {

    @App
    NtApplication app;

    @FragmentArg
    String argQuery;

    @FragmentArg
    String argRecipeId;

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

    public static NtSearchFragment newSearchInstance(String query) {
        NtSearchFragment_.FragmentBuilder_ builder_ = NtSearchFragment_.builder();
        builder_.argQuery(query);
        return builder_.build();
    }

    public static NtSearchFragment newOpenInstance(String id) {
        NtSearchFragment_.FragmentBuilder_ builder_ = NtSearchFragment_.builder();
        builder_.argRecipeId(id);
        return builder_.build();
    }

    @AfterViews
    void initViews() {
        try {
            this.dataManager.table(NtDataManager.TABLE.FAVORITE);
        } catch (JSONException e) {
            this.app.show(this.app.getString(R.string.error));
            getActivity().finish();
        }
        initSubject();
        initWebView(generateUrl());
    }

    private void initSubject() {
        this.compositeSubscription.add(this.webClient.getUrl()
                .map(url -> url.matches(this.app.getString(R.string.top_url) + "/recipe/[0-9]{1,9}"))
                .subscribe(this.ntSearchFavorite::setEnabled));
        this.compositeSubscription.add(this.webClient.getUrl()
                .map(this.dataManager::exists)
                .subscribe(this::setFavoriteActionImage));
        this.compositeSubscription.add(this.webClient.getLoading()
                .subscribe(this.dialog.getLoading()::onNext));
    }

    private void setFavoriteActionImage(boolean isFavorite) {
        this.ntSearchFavorite.setSelected(isFavorite);
        this.ntSearchFavorite.setText(isFavorite ? this.app.getString(R.string.not_favorite) : this.app.getString(R.string.already_favorite));
    }

    private String generateUrl() {
        if (!TextUtils.isEmpty(this.argRecipeId)) return this.app.getString(R.string.recipe_url) + this.argRecipeId;
        if (!TextUtils.isEmpty(this.argQuery)) return this.app.getString(R.string.search_url) + this.argQuery;
        return this.app.getString(R.string.top_url);
    }

    private void initWebView(String initUrl) {
        this.ntSearchWeb.getSettings().setJavaScriptEnabled(true);
        this.ntSearchWeb.setWebViewClient(this.webClient);
        this.ntSearchWeb.setWebChromeClient(this.chromeClient);
        this.ntSearchWeb.loadUrl(initUrl);
    }

    @Click
    void ntSearchFavorite() {
        setFavoriteActionImage(!this.dataManager.exists(this.webClient.getUrl().getValue()));

        if (this.dataManager.exists(this.webClient.getUrl().getValue())) {
            removeFavorite();
            this.app.show(this.app.getString(R.string.message_remove_favorite));
        } else {
            addFavorite();
            this.app.show(this.app.getString(R.string.message_add_favorite));
        }
    }

    private void addFavorite() {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:alert(")
                .append("document.querySelector('.recipe_title').innerText").append("+','+")
                .append("document.querySelector('.photo').src").append("+','+")
                .append("location.href")
                .append(");");

        this.ntSearchWeb.loadUrl(sb.toString());
    }

    private void removeFavorite() {
        try {
            this.dataManager.table(NtDataManager.TABLE.FAVORITE)
                    .delete(this.webClient.getUrl().getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Click
    void ntSearchTop() {
        getActivity().finish();
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

    @Override
    public boolean goBack() {
        if (this.ntSearchWeb.canGoBack()) {
            this.ntSearchWeb.goBack();
            return true;
        }
        return false;
    }
}