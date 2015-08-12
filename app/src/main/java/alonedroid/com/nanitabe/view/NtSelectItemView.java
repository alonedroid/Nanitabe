package alonedroid.com.nanitabe.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.scene.choice.ImageLruCache;

@EViewGroup(R.layout.view_nt_select_item)
public class NtSelectItemView extends RelativeLayout {

    @App
    NtApplication app;

    @ViewById
    NetworkImageView selectImage;

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
        this.selectImage.setImageUrl(imageUrl, new ImageLoader(this.app.getQueue(), new ImageLruCache()));
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