package alonedroid.com.nanitabe.scene.select;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.GridView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.R;
import alonedroid.com.nanitabe.scene.choice.NtChoiceFragment;
import alonedroid.com.nanitabe.utility.NtDataManager;
import alonedroid.com.nanitabe.utility.NtRecipeItem;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

@EFragment(R.layout.fragment_nt_select)
public class NtSelectFragment extends Fragment {

    @App
    NtApplication app;

    @ViewById
    GridView ntSelectList;

    @Bean
    NtDataManager dataManager;

    @Bean
    NtDialogFragment dialog;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private NtSelectAdapter adapter;

    private boolean self = true;

    public static NtSelectFragment newInstance() {
        NtSelectFragment_.FragmentBuilder_ builder_ = NtSelectFragment_.builder();
        return builder_.build();
    }

    @AfterInject
    void init() {
        initDialog();
    }

    @AfterViews
    void initViews() {
        initListData();
        this.ntSelectList.setOnItemClickListener((parent, view, position, time) -> this.adapter.clickItem(position));
    }

    private void initListData() {
        List<String> keys = this.dataManager.getKeys();
        List<NtRecipeItem> items = Observable.from(keys.toArray(new String[keys.size()]))
                .map(key -> NtRecipeItem.newInstance(this.dataManager.get(key)))
                .toList().toBlocking().single();
        this.adapter = new NtSelectAdapter(getActivity(), R.layout.view_nt_select_item, items);
        this.ntSelectList.setAdapter(this.adapter);
    }

    private void initDialog() {
        this.compositeSubscription.add(this.dialog.getWhich()
                .filter(which -> which == DialogInterface.BUTTON_POSITIVE)
                .subscribe(which -> dialogPositive()));
    }

    private void dialogPositive() {
        Observable.from(this.adapter.getSelectedItems())
                .subscribe(this.dataManager::remove,
                        this::onError,
                        this::onComplete);
    }

    private void onError(Throwable t) {

    }

    private void onComplete() {
        initListData();
        this.adapter.notifyDataSetChanged();
    }

    @Click
    void ntSelectDelete() {
        this.dialog.show(getFragmentManager(), null);
    }

    @Click
    void ntSelectDone() {
        if (this.self) {
            ntSelectOpen();
        } else {
            ntSelectSend();
        }
    }

    private String collectSelectedIds() {
        return (String) Observable.from(this.adapter.getSelectedItems())
                .map(url -> Observable.from(url.split("/")).last())
                .reduce(null, (ids, id) -> (ids == null) ? id : ids + "," + id)
                .toBlocking().single();
    }

    private List<String> collectSelectedIdsArray() {
        return Observable.from(this.adapter.getSelectedItems())
                .map(url -> Observable.from(url.split("/")).toBlocking().last())
                .toList().toBlocking().single();
    }

    private void ntSelectOpen() {
        if (this.adapter.getSelectedItems().length == 0) return;

        List<String> idList = collectSelectedIdsArray();
        NtApplication.getRouter().onNext(NtChoiceFragment.newInstance(idList.toArray(new String[idList.size()])));
    }

    private void ntSelectSend() {
        if (this.adapter.getSelectedItems().length == 0) return;

        String ids = collectSelectedIds();
        // TODO.Activity側にインスタンスの生成メソッドを実装
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "ごはん候補");
        intent.putExtra(Intent.EXTRA_TEXT, "http://nanitabe.choice?" + ids);
        startActivity(Intent.createChooser(intent, "共有に使用するアプリを選択してください。"));
    }

    @Override
    public void onStop() {
        super.onStop();
        this.compositeSubscription.clear();
    }
}