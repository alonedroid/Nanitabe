package alonedroid.com.nanitabe.view;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.activity.R;

@EViewGroup(R.layout.fragment_nt_choice_image)
public class NtChoiceItemView extends RelativeLayout {

    @App
    NtApplication app;

    @ViewById
    NetworkImageView choiceImagePhoto;

    @ViewById
    TextView choiceImageText;

    public static NtChoiceItemView newInstance(Context context) {
        NtChoiceItemView view = NtChoiceItemView_.build(context);
        return view;
    }

    public NtChoiceItemView(Context context) {
        super(context);
    }

    public NetworkImageView getNetworkImageView() {
        return choiceImagePhoto;
    }

    public TextView getTextView() {
        return choiceImageText;
    }
}