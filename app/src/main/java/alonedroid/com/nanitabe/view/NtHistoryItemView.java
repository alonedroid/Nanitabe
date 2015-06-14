package alonedroid.com.nanitabe.view;

import android.content.Context;
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

@EViewGroup(R.layout.view_nt_history_item)
public class NtHistoryItemView extends RelativeLayout {

    @App
    NtApplication app;

    @ViewById
    NetworkImageView historyImage;

    @ViewById
    TextView historyTitle;

    @ViewById
    TextView historyDate;

    public static NtHistoryItemView newInstance(Context context, String imageUrl, String title, String date) {
        NtHistoryItemView view = NtHistoryItemView_.build(context);
        view.setImage(imageUrl);
        view.setTitle(title);
        view.setDate(date);

        return view;
    }

    public NtHistoryItemView(Context context) {
        super(context);
    }

    public void setImage(String imageUrl) {
        this.historyImage.setImageUrl(imageUrl, new ImageLoader(this.app.getQueue(), new ImageLruCache()));
    }

    public void setTitle(String title) {
        this.historyTitle.setText(title);
    }

    public void setDate(String date) {
        this.historyDate.setText(date);
    }
}