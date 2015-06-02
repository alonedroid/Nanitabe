package alonedroid.com.nanitabe.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import alonedroid.com.nanitabe.R;

@EViewGroup(R.layout.view_nt_history_item)
public class NtHistoryItemView extends RelativeLayout {

    @ViewById
    ImageView historyImage;

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
        Glide.with(getContext()).load(imageUrl).into(this.historyImage);
    }

    public void setTitle(String title) {
        this.historyTitle.setText(title);
    }

    public void setDate(String date) {
        this.historyDate.setText(date);
    }
}