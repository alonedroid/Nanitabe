package alonedroid.com.nanitabe.scene.choice;

import android.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import alonedroid.com.nanitabe.activity.R;
import lombok.Getter;
import rx.subjects.BehaviorSubject;

@EFragment(R.layout.fragment_nt_choice_image)
public class NtChoiceImageFragment extends Fragment {

    @FragmentArg
    String argUrl;

    @ViewById
    ImageView choiceImagePhoto;

    @StringRes
    String recipeUrl;

    @Getter
    private BehaviorSubject<String> title = BehaviorSubject.create();

    public static NtChoiceImageFragment newInstance(String url) {
        NtChoiceImageFragment_.FragmentBuilder_ builder = NtChoiceImageFragment_.builder();
        builder.argUrl(url);
        return builder.build();
    }

    @AfterViews
    void initViews() {
        loadRecipe();
    }

    @Background
    protected void loadRecipe() {
        try {
            Document doc = Jsoup.connect(this.recipeUrl + this.argUrl).get();

            String imageUrl = doc.select(".main_photo").first().getElementsByTag("img").attr("src");
            this.title.onNext(doc.select(".recipe_title").first().text());
            setImage(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    protected void setImage(String imageUrl) {
        if (imageUrl.length() == 0) {
            this.choiceImagePhoto.setImageResource(R.drawable.no_image);
        } else {
            Glide.with(this).load(imageUrl).into(this.choiceImagePhoto);
        }
    }
}