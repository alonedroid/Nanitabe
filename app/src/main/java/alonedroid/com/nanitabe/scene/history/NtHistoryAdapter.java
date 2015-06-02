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

    private LayoutInflater inflater;

    public NtHistoryAdapter(Context context, int resource, List<NtRecipeItem> list) {
        super(context, resource, list);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        return view;
    }
}