package alonedroid.com.nanitabe.scene.choice;

import android.app.Fragment;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ClearCacheRequest;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.scene.search.NtSearchFragment;
import alonedroid.com.nanitabe.utility.NtDataManager;
import rx.Observable;
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
        NtChoiceFragment_.FragmentBuilder_ builder_ = NtChoiceFragment_.builder();
        builder_.argRecipeIds(ids);
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
        this.adapter = new NtChoiceAdapter(getActivity(), getFragmentManager(), Arrays.asList(this.argRecipeIds));
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
        if (this.argRecipeIds.length - 1 <= position) {
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
            if (TextUtils.isEmpty(this.argRecipeQuery)) {
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
        this.analyzer.analyze(this.searchUrl + encode(query));
    }

    private String encode(String query) {
        try {
            String encode = "UTF-8";
            return URLEncoder.encode(query, encode);
        } catch (UnsupportedEncodingException e) {
            return query;
        }
    }
}