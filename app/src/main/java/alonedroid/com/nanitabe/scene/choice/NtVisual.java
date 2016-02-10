package alonedroid.com.nanitabe.scene.choice;

import android.util.Log;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.activity.R;
import lombok.Getter;
import rx.subjects.BehaviorSubject;

@EBean
public class NtVisual {
    @App
    NtApplication app;
    @Getter
    private String id;
    @Getter
    private BehaviorSubject<String> title = BehaviorSubject.create("取得中...");
    @Getter
    private BehaviorSubject<String> image = BehaviorSubject.create("");
    @Getter
    private BehaviorSubject<String> ingredients = BehaviorSubject.create("");
    @Getter
    private boolean prepare;

    @Background
    void analyze(String id) {
        try {
            Document doc = Jsoup.connect(this.app.getString(R.string.recipe_url) + id).get();
            this.id = id;
            this.image.onNext(doc.select(".main_photo").first().getElementsByTag("img").attr("src"));
            this.title.onNext(doc.select(".recipe_title").first().text());
            this.ingredients.onNext(getServings(doc) + getIngredients(doc));
            this.prepare = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.d("Nanitabe", "error recipe id:" + id);
            e.printStackTrace();
        }
    }

    private String ingredientFormat(String _ingredient, String _quantity) {
        return (_ingredient + "　　　　　　　　　　　　").substring(0, 12) + "・・・" + _quantity;
    }

    private String getServings(Document doc) {
        StringBuffer sb = new StringBuffer();

        Elements servings = doc.select(".servings");
        if (servings == null || servings.first() == null) {
            sb.append("　----------------------------------\n")
                    .append("　(1回分)")
                    .append("\n　----------------------------------\n");
        } else {
            sb.append("　----------------------------------\n")
                    .append(servings.first().text())
                    .append("\n　----------------------------------\n");
        }

        return sb.toString();
    }

    private String getIngredients(Document doc) {
        StringBuffer sb = new StringBuffer();

        Elements ingredientsList = doc.select("#ingredients-list");
        if (ingredientsList == null || ingredientsList.first() == null) {
            sb.append("取得失敗");
        } else {
            Elements ingredients = ingredientsList.first().getElementsByTag("dl");
            for (Element ing : ingredients) {
                sb.append("\n　").append(ingredientFormat(
                        ing.getElementsByTag("dt").first().text(),
                        ing.getElementsByTag("dd").first().text()));
            }
        }

        return sb.toString();
    }
}
