package alonedroid.com.nanitabe.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import alonedroid.com.nanitabe.activity.R;

@EViewGroup(R.layout.view_nt_select_item)
public class NtSelectItemView extends RelativeLayout {

    @ViewById
    ImageView selectImage;

    @ViewById
    TextView selectTitle;

    @ViewById
    ImageView selectCheck;

    public static NtSelectItemView newInstance(Context context, String imageUrl, String title) {
        NtSelectItemView view = NtSelectItemView_.build(context);
        view.setImage(imageUrl);
        view.setTitle(title);

        return view;
    }

    public NtSelectItemView(Context context) {
        super(context);
    }

    public void setImage(String imageUrl) {
        Glide.with(getContext()).load(imageUrl).into(this.selectImage);
    }

    public void setTitle(String title) {
        this.selectTitle.setText(title);
    }

    public void toggleCheck() {
        setCheck(!this.selectCheck.isEnabled());
    }

    public void setCheck(boolean checked) {
        this.selectCheck.setEnabled(checked);
    }
}