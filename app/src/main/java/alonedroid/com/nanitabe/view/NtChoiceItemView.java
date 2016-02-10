package alonedroid.com.nanitabe.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.annotations.res.StringRes;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.activity.R;
import lombok.Setter;

@EViewGroup(R.layout.view_nt_choice_image)
public class NtChoiceItemView extends RelativeLayout {

    @App
    NtApplication app;
    @ViewById
    NetworkImageView choiceImagePhoto;
    @ViewById
    TextView choiceImageText;
    @ViewById
    ScrollView choiceImageTextArea;
    @DimensionPixelOffsetRes
    int choiceImageWidth;
    @DimensionPixelOffsetRes
    int choiceImageSideWidth;
    @StringRes
    String ingredientNavi;
    @Setter
    String ingredient;
    private String mIngredientText;


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
    void choiceImageText() {
        toggleIngredientList();
    }

    @Click
    void choiceImageRoot() {
        toggleIngredientList();
    }

    private void toggleIngredientList() {
        int currentWidth = choiceImageTextArea.getLayoutParams().width;
        ValueAnimator widthValueAnimator;
        if (currentWidth <= choiceImageSideWidth) {
            widthValueAnimator = ValueAnimator.ofInt(choiceImageSideWidth, choiceImageWidth);
            mIngredientText = ingredient;
        } else {
            widthValueAnimator = ValueAnimator.ofInt(choiceImageWidth, choiceImageSideWidth);
            mIngredientText = ingredientNavi;
        }
        widthValueAnimator.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams layoutParams = choiceImageTextArea.getLayoutParams();
            layoutParams.width = (Integer) valueAnimator.getAnimatedValue();
            choiceImageTextArea.setLayoutParams(layoutParams);
        });
        widthValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                choiceImageText.setText("");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                choiceImageText.setText(mIngredientText);
            }
        });
        widthValueAnimator.setDuration(300);
        widthValueAnimator.setInterpolator(new DecelerateInterpolator());
        widthValueAnimator.start();
    }
}