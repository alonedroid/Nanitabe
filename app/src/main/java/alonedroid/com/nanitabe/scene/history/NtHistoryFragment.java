package alonedroid.com.nanitabe.scene.history;

import android.app.Fragment;
import android.widget.GridView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.R;
import alonedroid.com.nanitabe.utility.NtDataManager;
import alonedroid.com.nanitabe.utility.NtRecipeItem;
import rx.Observable;

@EFragment(R.layout.fragment_nt_history)
public class NtHistoryFragment extends Fragment {

    @App
    NtApplication app;

    @ViewById
    GridView ntHistoryList;

    @Bean
    NtDataManager dataManager;

    private NtHistoryAdapter adapter;

    private boolean self = true;

    public static NtHistoryFragment newInstance() {
        NtHistoryFragment_.FragmentBuilder_ builder_ = NtHistoryFragment_.builder();
        return builder_.build();
    }

    @AfterViews
    void initViews() {
        initListData();
    }

    private void initListData() {
        List<String> keys = this.dataManager.getHistory();
        List<NtRecipeItem> items = Observable.from(keys.toArray(new String[keys.size()]))
                .map(this.dataManager::get)
                .map(NtRecipeItem::newInstance)
                .toList().toBlocking().single();

        this.adapter = new NtHistoryAdapter(getActivity(), R.layout.view_nt_history_item, items);
        this.ntHistoryList.setAdapter(this.adapter);
    }
}