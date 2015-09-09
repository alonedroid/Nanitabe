package alonedroid.com.nanitabe.scene.history;

import android.support.v4.app.Fragment;
import android.widget.GridView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

import java.util.Collections;
import java.util.List;

import alonedroid.com.nanitabe.NtRouter;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.activity.VariableActivity;
import alonedroid.com.nanitabe.utility.NtDataManager;
import alonedroid.com.nanitabe.utility.NtRecipeItem;
import rx.Observable;

@EFragment(R.layout.fragment_nt_history)
public class NtHistoryFragment extends Fragment {

    @ViewById
    GridView ntHistoryList;

    @Bean
    NtDataManager dataManager;

    public static NtHistoryFragment newInstance() {
        NtHistoryFragment_.FragmentBuilder_ builder_ = NtHistoryFragment_.builder();
        return builder_.build();
    }

    @AfterViews
    void initViews() {
        initListData();
    }

    private void initListData() {
        try {
            List<NtRecipeItem> items = this.dataManager.table(NtDataManager.TABLE.HISTORY).selectAll();
            Collections.sort(items, (before, after) -> (after.getDate().compareTo(before.getDate())));
            NtHistoryAdapter adapter = new NtHistoryAdapter(getActivity(), R.layout.view_nt_history_item, items);
            adapter.setOnItemClickListener(this::openUrl);
            this.ntHistoryList.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openUrl(String url) {
        String id = Observable.from(url.split("/")).last().toBlocking().single();
        startActivity(VariableActivity.newIntent(getActivity(), NtRouter.getRecipeOpenMap(id)));
    }
}