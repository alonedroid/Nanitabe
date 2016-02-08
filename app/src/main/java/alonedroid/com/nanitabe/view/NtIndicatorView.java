package alonedroid.com.nanitabe.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;

import alonedroid.com.nanitabe.activity.R;


@EViewGroup(R.layout.view_nt_indicator)
public class NtIndicatorView extends FrameLayout {

    @ViewById
    protected FrameLayout idcViewRoot;

    @ViewById
    protected TextView idcViewFirst, idcViewCurrent, idcViewLast;

    private int idcRootWidth, idcWidth, idcCount, idcCurrentPostion;

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = this::onGlobalLayout;

    public NtIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    protected void onAfterViews() {
        this.idcViewRoot.getViewTreeObserver().addOnGlobalLayoutListener(this.globalLayoutListener);
    }

    private void setCurrentPosition(int position) {
        animateTranslationX(calcX(position));
    }

    private float calcX(int position) {
        return new BigDecimal(this.idcRootWidth)
                .divide(new BigDecimal(this.idcCount - 1), 20, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(position))
                .intValue();
    }

    private void animateTranslationX(float x) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this.idcViewCurrent, "translationX", this.idcViewCurrent.getTranslationX(), x);
        objectAnimator.setDuration(300);
        objectAnimator.start();
    }

    public void onGlobalLayout() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            this.idcViewRoot.getViewTreeObserver().removeGlobalOnLayoutListener(this.globalLayoutListener);
        } else {
            this.idcViewRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this.globalLayoutListener);
        }

        this.idcWidth = this.idcViewCurrent.getWidth();
        this.idcRootWidth = this.idcViewRoot.getWidth() - this.idcWidth;
    }

    public void setCount(int count) {
        int beforeCount = this.idcCount;
        this.idcCount = count;
        adjustIndicator(beforeCount);
    }

    public int getCount() {
        return this.idcCount;
    }

    private void adjustIndicator(int beforeCount) {
        if (this.idcCount < beforeCount) {
            this.idcCurrentPostion = this.idcCount;
        }
        selectAt(this.idcCurrentPostion);
    }

    public void selectAt(int position) {
        if (this.idcCount < 2 || this.idcCount <= position) return;
        setCurrentPosition(position);
        this.idcCurrentPostion = position;

        this.idcViewFirst.setText("1");
        this.idcViewLast.setText(this.idcCount + "");
        this.idcViewCurrent.setText((this.idcCurrentPostion + 1) + "");
    }
}