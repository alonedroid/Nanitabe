package alonedroid.com.nanitabe.scene.uratop;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import alonedroid.com.nanitabe.NtApplication;
import lombok.Getter;
import rx.subjects.BehaviorSubject;

@EBean
public class NtTrendAnalyzer {

    private static final String TREND_URL = "http://cookpad.com/s/trend_keywords";

    private static final String RANK_AND_KEYWORD = "div.rank_and_keyword";

    private static final String TREND_KEYWORD = "a.trend_keyword_search_link";

    private static final String TREND_IMAGE = ".trend_keyword_searchable_recipe a img";

    @App
    NtApplication app;

    @Getter
    BehaviorSubject<Boolean> complete = BehaviorSubject.create(false);

    private TrendRecipe[] trendRecipes = new TrendRecipe[10];

    @Background
    public void analyze() {
        Document root = connect(TREND_URL);
        findTopTrendMenu(root);
    }

    private void findTopTrendMenu(Document document) {
        try {
            int index = 0;
            for (Element element : document.select(RANK_AND_KEYWORD)) {
                trendRecipes[index] = new TrendRecipe();
                trendRecipes[index].trendMenu = element.select(TREND_KEYWORD).text();
                trendRecipes[index].trendUrl = element.select(TREND_KEYWORD).attr("href");
                trendRecipes[index].trendImage = element.select(TREND_IMAGE).attr("src");
                index++;
            }

            this.complete.onNext(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TrendRecipe getMainTrendRecipe() {
        return this.trendRecipes[0];
    }

    public TrendRecipe[] getPickUpTrendRecipes() {
        TrendRecipe[] recipes = new TrendRecipe[3];
        int indexBase = (int) (Math.random() * 7.0 + 1);
        for (int i = 0; i < 3; i++) {
            recipes[i] = this.trendRecipes[indexBase++];
        }
        return recipes;
    }

    private Document connect(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            return new Document("");
        }
    }
}