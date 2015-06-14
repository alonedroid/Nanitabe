package alonedroid.com.nanitabe.scene.uratop;

import android.app.Fragment;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.scene.choice.NtChoiceFragment;

@EFragment(R.layout.fragment_nt_uratop)
public class NtUraTopFragment extends Fragment {

    @App
    NtApplication app;

    @ViewById
    TextView ntTopUrasearchText;

    public static NtUraTopFragment newInstance() {
        NtUraTopFragment_.FragmentBuilder_ builder_ = NtUraTopFragment_.builder();
        return builder_.build();
    }

    @Click
    void ntTopUrasearch(View view) {
        NtApplication.getRouter().onNext(NtChoiceFragment.newInstance(this.ntTopUrasearchText.getText().toString()));
        this.app.hiddelKeyboard(view);
    }
}