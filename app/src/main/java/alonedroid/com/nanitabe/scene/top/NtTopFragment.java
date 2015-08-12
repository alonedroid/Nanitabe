package alonedroid.com.nanitabe.scene.top;

import android.app.Fragment;
import android.widget.TextView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.scene.history.NtHistoryFragment;
import alonedroid.com.nanitabe.scene.search.NtSearchFragment;
import alonedroid.com.nanitabe.scene.select.NtSelectFragment;
import alonedroid.com.nanitabe.scene.uratop.NtUraTopFragment;

@EFragment(R.layout.fragment_nt_top)
public class NtTopFragment extends Fragment {

    @ViewById
    TextView ntTopSearchText;

    public static NtTopFragment newInstance() {
        NtTopFragment_.FragmentBuilder_ builder_ = NtTopFragment_.builder();
        return builder_.build();
    }

    @Click
    void modeChange() {
        NtApplication.getRouter().onNext(NtUraTopFragment.newInstance());
    }

    @Click
    void ntTopSearch() {
        NtApplication.getRouter().onNext(NtSearchFragment.newInstance(this.ntTopSearchText.getText().toString(), null));
    }

    @Click
    void ntTopList() {
        NtApplication.getRouter().onNext(NtSelectFragment.newInstance());
    }

    @Click
    void ntTopHistory() {
        NtApplication.getRouter().onNext(NtHistoryFragment.newInstance());
    }
}