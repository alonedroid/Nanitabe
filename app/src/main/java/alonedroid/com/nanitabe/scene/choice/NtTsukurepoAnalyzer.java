package alonedroid.com.nanitabe.scene.choice;

import android.text.TextUtils;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import alonedroid.com.nanitabe.NtApplication;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;
import rx.subjects.PublishSubject;

@EBean
public class NtTsukurepoAnalyzer {

    private static final int FILTER_MIN_TSUKUREPO = 3;

    private static final int SHOW_RECIPE_NUM = 30;

    private static final String RECIPE_PAGING = "a[href*=recipe_hit]";

    private static final String RECIPE_LIST_ROOT = "li.card.recipe";

    private static final String RECIPE_ID_TAG = "a[data-recipe-id]";

    private static final String RECIPE_ID = "data-recipe-id";

    private static final String TSUKUREPO_ROOT = "div.tsukurepo_count";

    private static final String TSUKUREPO_ELEMENT = "span.bold";

    @App
    NtApplication app;

    @Setter
    private int maxSize;

    @StringRes
    String tsukurepoUrl;

    @StringRes
    String pagingAttr;

    @Getter
    PublishSubject<String> subject = PublishSubject.create();

    @Getter
    PublishSubject<Boolean> complete = PublishSubject.create();

    private String queryUrl;

    private int maxPage;

    private int totalRecipe;

    private boolean cancel;

    @Background
    public void analyze(String url) {
        this.queryUrl = url;
        Document root = connect(url);
        this.totalRecipe = findTotalRecipe(root);
        this.maxPage = (int) (this.totalRecipe / SHOW_RECIPE_NUM + 0.5);
        Observable.range(1, this.maxPage)
                .filter(index -> !this.cancel)
                .map(this::generateUrl)
                .subscribe(this::analyzePage, this::onError, () -> this.complete.onNext(true))
                .unsubscribe();
    }

    private String generateUrl(int index) {
        if (index == 1) return this.queryUrl;
        this.app.show(index + " / " + this.maxPage + " 解析中");
        return this.queryUrl + String.format(this.pagingAttr, index, this.totalRecipe);
    }

    private void analyzePage(String url) {
        Observable.from(connect(url).select(RECIPE_LIST_ROOT))
                .map(this::findRecipeId)
                .filter(id -> !TextUtils.isEmpty(id))
                .filter(this::minTsukurepo)
                .subscribe(this.subject::onNext)
                .unsubscribe();
    }

    private int findTotalRecipe(Document document) {
        try {
            return Integer.parseInt(document.select(RECIPE_PAGING).attr("href").split("&")[1].split("=")[1]);
        } catch (Exception e) {
            return 1;
        }
    }

    private String findRecipeId(Element recipe) {
        try {
            return recipe.select(RECIPE_ID_TAG).first().attr(RECIPE_ID);
        } catch (NullPointerException e) {
            return null;
        }
    }

    private boolean minTsukurepo(String id) {
        Elements div = connect(generateTsukurepoUrl(id)).select(TSUKUREPO_ROOT);
        if (div.size() == 0) return false;

        int tsukurepo = Integer.parseInt(div.first().select(TSUKUREPO_ELEMENT).text());
        return tsukurepo >= FILTER_MIN_TSUKUREPO;
    }

    private String generateTsukurepoUrl(String id) {
        return this.tsukurepoUrl.replace("$1", id);
    }

    private Document connect(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            return new Document("");
        }
    }

    public void cancel() {
        this.cancel = true;
    }

    public void onError(Throwable throwable) {

    }
}