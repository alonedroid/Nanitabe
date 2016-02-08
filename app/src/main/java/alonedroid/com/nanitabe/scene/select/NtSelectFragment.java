package alonedroid.com.nanitabe.scene.select;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.GridView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.jsoup.helper.StringUtil;

import java.util.List;

import alonedroid.com.nanitabe.NtRouter;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.activity.VariableActivity;
import alonedroid.com.nanitabe.utility.NtDataManager;
import alonedroid.com.nanitabe.utility.NtRecipeItem;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

@EFragment(R.layout.fragment_nt_select)
public class NtSelectFragment extends Fragment {

    @ViewById
    GridView ntSelectList;

    @Bean
    NtDataManager dataManager;

    NtDialogFragment dialog;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private NtSelectAdapter adapter;

    private boolean self = true;

    public static NtSelectFragment newInstance() {
        NtSelectFragment_.FragmentBuilder_ builder_ = NtSelectFragment_.builder();
        return builder_.build();
    }

    @AfterViews
    void initViews() {
        initDialog();
        initListData();
        this.ntSelectList.setOnItemClickListener((parent, view, position, time) -> this.adapter.clickItem(position));
    }

    @Override
    public void onStop() {
        this.compositeSubscription.clear();
        super.onStop();
    }

    private void initListData() {
        try {
            List<NtRecipeItem> items = this.dataManager.table(NtDataManager.TABLE.FAVORITE).selectAll();
            this.adapter = new NtSelectAdapter(getActivity(), R.layout.view_nt_select_item, items);
            this.ntSelectList.setAdapter(this.adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initDialog() {
        this.dialog = NtDialogFragment.newInstance();
        this.compositeSubscription.add(this.dialog.getWhich()
                .filter(which -> which == DialogInterface.BUTTON_POSITIVE)
                .subscribe(which -> dialogPositive()));
    }

    private void dialogPositive() {
        Observable.from(this.adapter.getSelectedItems())
                .subscribe(this::removeFavorite,
                        this::onError,
                        this::onComplete);
    }

    private void removeFavorite(String key) {
        try {
            this.dataManager.table(NtDataManager.TABLE.FAVORITE).delete(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        if (idList.size() == 1) {
            openRecipe(idList.get(0));
            return;
        }
        startActivity(VariableActivity.newIntent(getActivity(), NtRouter.getChoiceMap(StringUtil.join(idList, ","))));
    }

    private void openRecipe(String id) {
        startActivity(VariableActivity.newIntent(getActivity(), NtRouter.getRecipeOpenMap(id)));
    }

    private void ntSelectSend() {
        if (this.adapter.getSelectedItems().length == 0) return;

        String ids = collectSelectedIds();
        // TODO.Activity側にインスタンスの生成メソッドを実装
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "ごはん候補");
        intent.putExtra(Intent.EXTRA_TEXT, "http://nanitabe.choice?id=" + ids);
        startActivity(Intent.createChooser(intent, "共有に使用するアプリを選択してください。"));
    }
}