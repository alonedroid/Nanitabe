package alonedroid.com.nanitabe.scene.uratop;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.scene.choice.NtChoiceFragment;
import alonedroid.com.nanitabe.service.urasearch.UraSearchService;
import alonedroid.com.nanitabe.service.urasearch.UraSearchService_;
import alonedroid.com.nanitabe.view.NtInnerShadowView;

@EFragment(R.layout.fragment_nt_uratop)
public class NtUraTopFragment extends Fragment {

    @App
    NtApplication app;

    @ViewById
    TextView ntTopUrasearchText, ntTopSearchHint;

    @ViewById
    NtInnerShadowView optionNow, optionBack;

    public static NtUraTopFragment newInstance() {
        NtUraTopFragment_.FragmentBuilder_ builder_ = NtUraTopFragment_.builder();
        return builder_.build();
    }

    @AfterViews
    void initViews() {
        this.app.showKeyboard(this.ntTopUrasearchText);
    }

    @Click
    void optionNow() {
        this.optionNow.select();
        this.optionBack.unselect();
        this.ntTopSearchHint.setText(R.string.now_search_hint);
    }

    @Click
    void optionBack() {
        this.optionNow.unselect();
        this.optionBack.select();
        this.ntTopSearchHint.setText(R.string.back_search_hint);
    }

    @Click
    void ntTopUrasearch(View view) {
        if (TextUtils.isEmpty(this.ntTopUrasearchText.getText())) {
            this.app.show(this.app.getString(R.string.no_query));
            return;
        }
        if (this.optionBack.isSelected()) {
            UraSearchService_.intent(getActivity()).extra(UraSearchService.EXTRAS_QUERY, this.ntTopUrasearchText.getText().toString()).start();
        } else {
            NtApplication.getRouter().onNext(NtChoiceFragment.newInstance(this.ntTopUrasearchText.getText().toString()));
            this.app.hiddelKeyboard(view);
        }
    }
}