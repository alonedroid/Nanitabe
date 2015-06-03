package alonedroid.com.nanitabe.scene.select;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import alonedroid.com.nanitabe.utility.NtRecipeItem;
import alonedroid.com.nanitabe.view.NtSelectItemView;
import rx.Observable;

public class NtSelectAdapter extends ArrayAdapter<NtRecipeItem> {

    private List<NtRecipeItem> items;

    public NtSelectAdapter(Context context, int resource, List<NtRecipeItem> items) {
        super(context, resource, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NtRecipeItem recipe = this.items.get(position);
        NtSelectItemView view;
        if (convertView == null) {
            view = NtSelectItemView.newInstance(parent.getContext(), recipe.getImageUrl(), recipe.getTitle());
        } else {
            view = (NtSelectItemView) convertView;
        }
        view.setImage(recipe.getImageUrl());
        view.setTitle(recipe.getTitle());
        view.setCheck(recipe.isChecked());
        view.setOnClickListener(item -> {
            ((NtSelectItemView) item).toggleCheck();
            clickItem(position);
        });
        return view;
    }

    public void clickItem(int position) {
        this.items.get(position).setChecked(!this.items.get(position).isChecked());
    }

    public String[] getSelectedItems() {
        List<String> keys = Observable.from(this.items.toArray(new NtRecipeItem[this.items.size()]))
                .filter(NtRecipeItem::isChecked)
                .map(NtRecipeItem::getUrl)
                .toList().toBlocking().single();

        return keys.toArray(new String[keys.size()]);
    }
}