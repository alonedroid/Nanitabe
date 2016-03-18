package alonedroid.com.nanitabe.scene.uratop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.NtRouter;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.activity.VariableActivity;
import alonedroid.com.nanitabe.service.urasearch.UraSearchService;
import alonedroid.com.nanitabe.service.urasearch.UraSearchService_;
import alonedroid.com.nanitabe.view.NtCardView;
import alonedroid.com.nanitabe.view.NtHistoryItemView;
import alonedroid.com.nanitabe.view.NtInnerShadowView;
import rx.android.schedulers.AndroidSchedulers;

@EFragment(R.layout.fragment_nt_uratop)
public class NtUraTopFragment extends Fragment {

    @App
    NtApplication app;

    @Bean
    NtTrendAnalyzer analyzer;

    @ViewById
    EditText uratopSearchText;

    @ViewById
    NtCardView trend_1;

    @ViewById
    NtHistoryItemView trend_2, trend_3, trend_4;

    @ViewById
    NtInnerShadowView optionNow, optionBack;

    public static NtUraTopFragment newInstance() {
        NtUraTopFragment_.FragmentBuilder_ builder_ = NtUraTopFragment_.builder();
        return builder_.build();
    }

    @AfterViews
    void initViews() {
        this.analyzer.analyze();
        this.analyzer.complete
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setTrendMenu);
    }

    void setTrendMenu(boolean isComplete) {
        if (!isComplete) return;

        TrendRecipe recipe = this.analyzer.getMainTrendRecipe();
        this.trend_1.setImage(recipe.trendImage);
        this.trend_1.setText(recipe.trendMenu);

        TrendRecipe[] pickupRecipes = this.analyzer.getPickUpTrendRecipes();
        this.trend_2.setImage(pickupRecipes[0].trendImage);
        this.trend_2.setTitle(pickupRecipes[0].trendMenu);
        this.trend_3.setImage(pickupRecipes[1].trendImage);
        this.trend_3.setTitle(pickupRecipes[1].trendMenu);
        this.trend_4.setImage(pickupRecipes[2].trendImage);
        this.trend_4.setTitle(pickupRecipes[2].trendMenu);
    }

    @Click
    void trend_1() {
        showDialog(this.trend_1.getText());
    }

    @Click
    void trend_2() {
        showDialog(this.trend_2.getTitle());
    }

    @Click
    void trend_3() {
        showDialog(this.trend_3.getTitle());
    }

    @Click
    void trend_4() {
        showDialog(this.trend_4.getTitle());
    }

    @Click
    void uratopSearch() {

        String query = this.uratopSearchText.getText().toString();
        if (TextUtils.isEmpty(query)) {
            this.app.show(this.app.getString(R.string.no_query));
            return;
        }
        showDialog(query);
    }

    private void showDialog(final String keywords) {
        new DialogFragment() {
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("検索方法の選択");
                builder.setMessage("イマ検索：このまま検索を実行します。\n\n結果通知：検索結果を後ほど通知します。");
                builder.setPositiveButton("結果通知", (dialog, which) -> backSearch(keywords));
                builder.setNegativeButton("イマ検索", (dialog, which) -> nowSearch(keywords));
                return builder.create();
            }
        }.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    void nowSearch(String query) {
        startActivity(VariableActivity.newIntent(getActivity(), NtRouter.getUraSearchMap(query)));
    }

    private void backSearch(String query) {
        UraSearchService_.intent(getActivity()).extra(UraSearchService.EXTRAS_QUERY, query).start();
    }
}
