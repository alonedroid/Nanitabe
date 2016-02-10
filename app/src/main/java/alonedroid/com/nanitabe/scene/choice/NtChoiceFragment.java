package alonedroid.com.nanitabe.scene.choice;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
import org.json.JSONException;
import org.jsoup.helper.StringUtil;

import java.util.Arrays;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.NtRouter;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.activity.VariableActivity;
import alonedroid.com.nanitabe.utility.NtDataManager;
import alonedroid.com.nanitabe.utility.NtTextUtility;
import alonedroid.com.nanitabe.view.NtIndicatorView;
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

    @App
    NtApplication app;

    @ViewById
    TextView menuName, shareMenu;

    @ViewById
    ViewPager ntRecipePager;

    @ViewById
    ImageView prevMenu, nextMenu;

    @ViewById
    NtIndicatorView ntChoiceIndicator;

    @Bean
    NtDataManager dataManager;

    @Bean
    NtOnPageChangeListener changeListener;

    @Bean
    NtTsukurepoAnalyzer analyzer;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Bean
    NtChoiceAdapter adapter;

    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            addIndicator(NtChoiceFragment.this.adapter.getCount() == 1);
            visibleSelectButton(NtChoiceFragment.this.ntRecipePager.getCurrentItem());
        }
    };

    public static NtChoiceFragment newInstance(String[] ids) {
        NtChoiceFragment_.FragmentBuilder_ builder_ = NtChoiceFragment_.builder();
        builder_.argRecipeIds(ids);
        return builder_.build();
    }

    public static NtChoiceFragment newSearchInstance(String query) {
        NtChoiceFragment_.FragmentBuilder_ builder_ = NtChoiceFragment_.builder();
        builder_.argRecipeIds(new String[0]);
        builder_.argRecipeQuery(query);
        return builder_.build();
    }

    @AfterInject
    void init() {
        this.adapter.addItem(this.argRecipeIds);
        this.adapter.registerDataSetObserver(this.dataSetObserver);
        uraSearch(this.argRecipeQuery);
    }

    private void uraSearch(String query) {
        if (TextUtils.isEmpty(query)) return;

        this.analyzer.getSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this.adapter::addItem);
        this.analyzer.analyze(this.app.getString(R.string.search_url) + NtTextUtility.encode(query));
        AppObservable.bindFragment(this, this.analyzer.getComplete())
                .subscribe(isComplete -> checkRecipes());
    }

    @AfterViews
    void initViews() {
        initLayout();
        initPager();
        initIndicator();
        selectedPosition(0);
    }

    private void initPager() {
        this.ntRecipePager.setAdapter(this.adapter);
        this.ntRecipePager.setOnPageChangeListener(this.changeListener);
    }

    private void initLayout() {
        this.adapter.getTitleSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this.menuName::setText);
        if (!TextUtils.isEmpty(this.argRecipeQuery) || NtApplication.MODE == NtApplication.MODE_SHARE) {
            this.shareMenu.setEnabled(false);
        }
    }

    private void initIndicator() {
        Observable.range(0, this.adapter.getCount())
                .subscribe(idx -> addIndicator(0 == idx))
                .unsubscribe();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.compositeSubscription.add(this.changeListener.getSelectedPosition()
                .subscribe(this::selectedPosition));
    }

    @Override
    public void onStop() {
        this.compositeSubscription.clear();
        this.analyzer.cancel();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        this.adapter.unsubscribe();
        this.adapter.unregisterDataSetObserver(this.dataSetObserver);
        this.adapter = null;
        super.onDestroy();
    }

    private void addIndicator(boolean on) {
        ImageView view = new ImageView(getActivity());
        view.setImageResource(R.drawable.custom_indicator);
        view.setPadding(10, 0, 10, 0);
        view.setEnabled(on);
        this.ntChoiceIndicator.setCount(this.ntChoiceIndicator.getCount() + 1);
    }

    private void selectedPosition(int position) {
        this.adapter.setPosition(position);
        visibleSelectButton(position);
        this.ntChoiceIndicator.selectAt(position);
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

    @Click
    void shareMenu() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "オススメレシピ");
        intent.putExtra(Intent.EXTRA_TEXT,
                "ナニタベを起動して御飯を選んでね♪\nhttp://nanitabe.org?id=" +
                        StringUtil.join(Arrays.asList(this.argRecipeIds), ","));
        startActivity(Intent.createChooser(intent, "シェアするアプリを選択してください。"));
    }

    @Click
    void decideMenu() {
        if (NtApplication.MODE == NtApplication.MODE_SHARE) {
            decideMenuSend();
        } else {
            decideMenuOpen();
        }
    }

    private void decideMenuOpen() {
        String id = this.adapter.getId(this.ntRecipePager.getCurrentItem());
        if (TextUtils.isEmpty(id)) return;
        startActivity(VariableActivity.newIntent(getActivity(), NtRouter.getRecipeOpenMap(id)));
        logHistory(id);
    }

    private void decideMenuSend() {
        String id = this.adapter.getId(this.ntRecipePager.getCurrentItem());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "今日のごはん");
        intent.putExtra(Intent.EXTRA_TEXT, "今日のご飯はコレ！\nhttp://nanitabe.org?id=" + id);
        startActivity(Intent.createChooser(intent, "共有に使用するアプリを選択してください。"));
    }

    @UiThread
    void checkRecipes() {
        if (this.adapter.getCount() == 0) {
            this.menuName.setText(this.app.getString(R.string.no_recipes));
        }
    }

    private void logHistory(String id) {
        try {
            if (TextUtils.isEmpty(this.argRecipeQuery)) {
                this.dataManager.table(NtDataManager.TABLE.HISTORY)
                        .log(this.app.getString(R.string.recipe_url) + id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}