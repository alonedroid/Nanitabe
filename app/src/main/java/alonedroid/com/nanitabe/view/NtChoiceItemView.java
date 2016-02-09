package alonedroid.com.nanitabe.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;

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

    @DimensionPixelOffsetRes
    int choiceImageWidth;

    @DimensionPixelOffsetRes
    int choiceImageSideWidth;

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

    @Click
    void choiceImageRoot() {
        int currentWidth = choiceImageText.getLayoutParams().width;
        ValueAnimator widthValueAnimator;
        if (currentWidth <= choiceImageSideWidth) {
            widthValueAnimator = ValueAnimator.ofInt(choiceImageSideWidth, choiceImageWidth);
        } else {
            widthValueAnimator = ValueAnimator.ofInt(choiceImageWidth, choiceImageSideWidth);
        }
        widthValueAnimator.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams layoutParams = choiceImageText.getLayoutParams();
            layoutParams.width = (Integer) valueAnimator.getAnimatedValue();
            choiceImageText.setLayoutParams(layoutParams);

        });
        widthValueAnimator.setDuration(300);
        widthValueAnimator.setInterpolator(new DecelerateInterpolator());
        widthValueAnimator.start();
    }
}