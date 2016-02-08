package alonedroid.com.nanitabe.scene.top;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.NtRouter;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.activity.VariableActivity;

@EFragment(R.layout.fragment_nt_top)
public class NtTopFragment extends Fragment {

    @App
    NtApplication app;

    @ViewById
    TextView ntTopSearchText;

    public static NtTopFragment newInstance() {
        NtTopFragment_.FragmentBuilder_ builder_ = NtTopFragment_.builder();
        return builder_.build();
    }

    @AfterViews
    void onAfterViews() {
        this.ntTopSearchText.setOnKeyListener((v, keyCode, event) -> onKey(keyCode, event));
    }

    @Click
    void modeChange() {
        startActivity(VariableActivity.newIntent(getActivity(), NtRouter.getUraTopMap()));
    }

    @Click
    void ntTopSearch() {
        String query = this.ntTopSearchText.getText().toString();
        if (TextUtils.isEmpty(query)) {
            this.app.show(this.app.getString(R.string.no_query));
            return;
        }

        startActivity(VariableActivity.newIntent(getActivity(), NtRouter.getTopSearchMap(query)));
    }

    @Click
    void ntTopList() {
        startActivity(VariableActivity.newIntent(getActivity(), NtRouter.getSelectMap()));
    }

    @Click
    void ntTopHistory() {
        startActivity(VariableActivity.newIntent(getActivity(), NtRouter.getHistoryMap()));
    }

    boolean onKey(int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            ntTopSearch();
            return true;
        }
        return false;
    }
}