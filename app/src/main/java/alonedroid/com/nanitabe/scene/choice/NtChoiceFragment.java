package alonedroid.com.nanitabe.scene.choice;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.json.JSONException;
import org.jsoup.helper.StringUtil;

import java.util.Arrays;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.scene.search.NtSearchFragment;
import alonedroid.com.nanitabe.utility.NtDataManager;
import alonedroid.com.nanitabe.utility.NtTextUtility;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

@EFragment(R.layout.fragment_nt_choice)
public class NtChoiceFragment extends Fragment {

    @FragmentArg
    String[] argRecipeIds;

    @FragmentArg
    String argRecipeQuery;

    @FragmentArg
    boolean argRecipeNoLog;

    @App
    NtApplication app;

    @StringRes
    String noRecipes;

    @StringRes
    String recipeUrl;

    @StringRes
    String searchUrl;

    @StringRes
    String tsukurepoUrl;

    @ViewById
    TextView menuName;

    @ViewById
    LinearLayout ntChoiceIndicator;

    @ViewById
    ViewPager ntRecipePager;

    @ViewById
    ImageView prevMenu;

    @ViewById
    ImageView nextMenu;

    @Bean
    NtDataManager dataManager;

    @Bean
    NtOnPageChangeListener changeListener;

    @Bean
    NtTsukurepoAnalyzer analyzer;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private NtChoiceAdapter adapter;

    private boolean isSelf = true;

    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            addIndicator(NtChoiceFragment.this.adapter.getCount() == 1);
        }
    };

    public static NtChoiceFragment newInstance(String[] ids) {
        return newInstance(ids, false);
    }

    public static NtChoiceFragment newInstance(String[] ids, boolean noLogFlg) {
        NtChoiceFragment_.FragmentBuilder_ builder_ = NtChoiceFragment_.builder();
        builder_.argRecipeIds(ids);
        builder_.argRecipeNoLog(noLogFlg);
        return builder_.build();
    }

    public static NtChoiceFragment newInstance(String query) {
        NtChoiceFragment_.FragmentBuilder_ builder_ = NtChoiceFragment_.builder();
        builder_.argRecipeIds(new String[0]);
        builder_.argRecipeQuery(query);
        return builder_.build();
    }

    @AfterInject
    void init() {
        this.adapter = new NtChoiceAdapter(getActivity(), getChildFragmentManager(), Arrays.asList(this.argRecipeIds));
        this.adapter.registerDataSetObserver(this.dataSetObserver);
        uraSearch(this.argRecipeQuery);
    }

    @AfterViews
    void initViews() {
        initPager();
        initIndicator();
        selectedPosition(0);
    }

    @Override
    public void onStop() {
        this.compositeSubscription.clear();
        this.analyzer.cancel();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        this.adapter.unregisterDataSetObserver(this.dataSetObserver);
        this.adapter = null;
        super.onDestroy();
    }

    private void initPager() {
        this.ntRecipePager.setAdapter(this.adapter);
        this.ntRecipePager.setOnPageChangeListener(this.changeListener);
        this.compositeSubscription.add(this.changeListener.getSelectedPosition()
                .subscribe(this::selectedPosition));
    }

    private void initIndicator() {
        Observable.range(0, this.adapter.getCount())
                .subscribe(idx -> addIndicator(0 == idx))
                .unsubscribe();
    }

    private void addIndicator(boolean on) {
        ImageView view = new ImageView(getActivity());
        view.setImageResource(R.drawable.custom_indicator);
        view.setPadding(10, 0, 10, 0);
        view.setEnabled(on);
        this.ntChoiceIndicator.addView(view);
        selectedPosition(this.ntRecipePager.getCurrentItem());
    }

    private void selectedPosition(int position) {
        this.adapter.setRecipeTitle(this.menuName, position);
        visibleSelectButton(position);
        Observable.range(0, this.adapter.getCount())
                .filter(idx -> this.ntChoiceIndicator.getChildAt(idx) != null)
                .subscribe(idx -> this.ntChoiceIndicator.getChildAt(idx).setEnabled(idx == position))
                .unsubscribe();
    }

    private void visibleSelectButton(int position) {
        int nextButtonView = View.VISIBLE;
        int prevButtonView = View.VISIBLE;

        if (position == 0) {
            prevButtonView = View.INVISIBLE;
        }
        if (this.adapter.getCount() - 1 <= position) {
            nextButtonView = View.INVISIBLE;
        }

        visibleNextPrev(nextButtonView, prevButtonView);
    }

    private void visibleNextPrev(int visibleNext, int visiblePrev) {
        this.nextMenu.setVisibility(visibleNext);
        this.prevMenu.setVisibility(visiblePrev);
    }

    @Click
    void prevMenu() {
        this.ntRecipePager.setCurrentItem(this.ntRecipePager.getCurrentItem() - 1, true);
    }

    @Click
    void nextMenu() {
        this.ntRecipePager.setCurrentItem(this.ntRecipePager.getCurrentItem() + 1, true);
    }

    void shareMenu() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "オススメレシピ");
        intent.putExtra(Intent.EXTRA_TEXT, "http://nanitabe.choice?" + StringUtil.join(Arrays.asList(this.argRecipeIds), ","));
        startActivity(Intent.createChooser(intent, "シェアするアプリを選択してください。"));
    }

    @Click
    void decideMenu() {
        if (isSelf) {
            decideMenuOpen();
        } else {
            decideMenuSend();
        }
    }

    private void decideMenuOpen() {
        String id = this.adapter.getId(this.ntRecipePager.getCurrentItem());
        if (TextUtils.isEmpty(id)) return;
        try {
            if (TextUtils.isEmpty(this.argRecipeQuery) && !this.argRecipeNoLog) {
                this.dataManager.table(NtDataManager.TABLE.HISTORY)
                        .log(this.recipeUrl + id);
            }
            NtApplication.getRouter().onNext(NtSearchFragment.newInstance(null, id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void decideMenuSend() {
        // TODO.Activity側にインスタンスの生成メソッドを実装
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "今日のごはん");
        startActivity(Intent.createChooser(intent, "共有に使用するアプリを選択してください。"));
    }

    private void uraSearch(String query) {
        if (TextUtils.isEmpty(query)) return;

        this.analyzer.getSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this.adapter::addItem);
        this.analyzer.analyze(this.searchUrl + NtTextUtility.encode(query));
        AppObservable.bindFragment(this, this.analyzer.getComplete())
                .subscribe(isComplete -> checkRecipes());
    }

    @UiThread
    void checkRecipes() {
        if (this.adapter.getCount() == 0) {
            this.menuName.setText(this.app.getString(R.string.no_recipes));
        }
    }
}