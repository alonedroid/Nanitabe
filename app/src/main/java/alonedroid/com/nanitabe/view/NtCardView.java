package alonedroid.com.nanitabe.view;

import android.content.Context;
import android.util.AttributeSet;
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

@EViewGroup(R.layout.view_nt_card)
public class NtCardView extends RelativeLayout {

    @App
    NtApplication app;
    @ViewById
    NetworkImageView cardPhoto;
    @ViewById
    TextView cardText;

    public NtCardView(Context context) {
        super(context);
    }

    public NtCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImage(String imageUrl) {
        this.cardPhoto.setImageUrl(imageUrl, new ImageLoader(this.app.getQueue(), new ImageLruCache()));
    }

    public void setText(String name) {
        this.cardText.setText(name);
    }

    public String getText() {
        return this.cardText.getText().toString();
    }
}
