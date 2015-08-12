package alonedroid.com.nanitabe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EViewGroup;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.activity.R;

@EViewGroup
public class NtInnerShadowView extends RelativeLayout {

    @App
    NtApplication app;

    TextView viewText;

    View topShadow, leftShadow, rightShadow, bottomShadow;

    private Context context;

    private boolean defaultSelected;

    private String defaultText;

    public static NtInnerShadowView newInstance(Context context, String text) {
        NtInnerShadowView view = NtInnerShadowView_.build(context);
        view.viewText.setText(text);
        return view;
    }

    public NtInnerShadowView(Context context) {
        this(context, null);
    }

    public NtInnerShadowView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.ntInnerShadowViewStyle);
    }

    public NtInnerShadowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        getAttributes(context, attrs);
        findViews(context);
        setDefault();
    }

    private void getAttributes(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NtInnerShadowView_);
        this.defaultSelected = array.getBoolean(R.styleable.NtInnerShadowView__selected, false);
        this.defaultText = array.getString(R.styleable.NtInnerShadowView__text);
        array.recycle();
    }

    private void findViews(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_nt_inner_shadow, this);
        this.viewText = (TextView) rootView.findViewById(R.id.view_text);
        this.topShadow = rootView.findViewById(R.id.top_shadow);
        this.leftShadow = rootView.findViewById(R.id.left_shadow);
        this.rightShadow = rootView.findViewById(R.id.right_shadow);
        this.bottomShadow = rootView.findViewById(R.id.bottom_shadow);
    }

    private void setDefault() {
        this.viewText.setText(this.defaultText);
        setDefaultShadow(this.defaultSelected);
    }

    private void setDefaultShadow(boolean selected) {
        if (selected) {
            select();
        } else {
            unselect();
        }
    }

    public void select() {
        this.viewText.setTextColor(Color.WHITE);
        this.viewText.setSelected(true);
        this.topShadow.setVisibility(View.VISIBLE);
        this.leftShadow.setVisibility(View.VISIBLE);
        this.rightShadow.setVisibility(View.VISIBLE);
        this.bottomShadow.setVisibility(View.VISIBLE);
    }

    public void unselect() {
        this.viewText.setTextColor(this.context.getResources().getColor(R.color.grey_600));
        this.viewText.setSelected(false);
        this.topShadow.setVisibility(View.GONE);
        this.leftShadow.setVisibility(View.GONE);
        this.rightShadow.setVisibility(View.GONE);
        this.bottomShadow.setVisibility(View.GONE);
    }

    public boolean isSelected() {
        return this.viewText.isSelected();
    }
}