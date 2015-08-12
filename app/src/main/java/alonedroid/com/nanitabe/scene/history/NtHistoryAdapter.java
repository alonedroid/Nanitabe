package alonedroid.com.nanitabe.scene.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import alonedroid.com.nanitabe.utility.NtRecipeItem;
import alonedroid.com.nanitabe.view.NtHistoryItemView;

public class NtHistoryAdapter extends ArrayAdapter<NtRecipeItem> {

    private OnItemClickListener callback;

    public NtHistoryAdapter(Context context, int resource, List<NtRecipeItem> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NtRecipeItem recipe = getItem(position);
        NtHistoryItemView view;
        if (convertView == null) {
            view = NtHistoryItemView.newInstance(parent.getContext(), recipe.getImageUrl(), recipe.getTitle(), recipe.getDate());
        } else {
            view = (NtHistoryItemView) convertView;
        }
        view.setImage(recipe.getImageUrl());
        view.setTitle(recipe.getTitle());
        view.setDate(recipe.getDate());
        view.setTag(recipe.getUrl());
        view.setOnClickListener(clickView -> this.callback.onClick(String.valueOf(clickView.getTag())));

        return view;
    }

    public void setOnItemClickListener(OnItemClickListener callback){
        this.callback = callback;
    }

    public interface OnItemClickListener {
        void onClick(String url);
    }
}