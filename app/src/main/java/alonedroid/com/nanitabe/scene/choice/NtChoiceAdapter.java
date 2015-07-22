package alonedroid.com.nanitabe.scene.choice;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import alonedroid.com.nanitabe.activity.R;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class NtChoiceAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> proposeRecipes;

    private SparseArray<NtChoiceImageFragment> fragments = new SparseArray<>();

    private TextView titleView;

    private Subscription titleSubscription;

    public NtChoiceAdapter(Context context, FragmentManager fm, List<String> urls) {
        super(fm);
        this.proposeRecipes = new ArrayList<>(urls);
    }

    @Override
    public Fragment getItem(int position) {
        this.fragments.put(position, NtChoiceImageFragment.newInstance(this.proposeRecipes.get(position)));

        if (this.titleSubscription == null) {
            observeRecipeTitle(this.fragments.get(position));
        }

        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.proposeRecipes.size();
    }

    public void setRecipeTitle(TextView title, int position) {
        this.titleView = title;

        if (this.fragments.get(position) == null || this.fragments.get(position).getTitle().getValue() == null) {
            this.titleView.setText(R.string.loading);
            if (this.fragments.get(position) != null) {
                observeRecipeTitle(this.fragments.get(position));
            }
        } else {
            this.titleView.setText(this.fragments.get(position).getTitle().getValue());
        }
    }

    public String getId(int position) {
        if (this.proposeRecipes.size() <= position) return "";
        return this.proposeRecipes.get(position);
    }

    private void observeRecipeTitle(NtChoiceImageFragment fragment) {
        if (this.titleSubscription != null) {
            this.titleSubscription.unsubscribe();
        }
        this.titleSubscription = fragment.getTitle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this.titleView::setText);
    }

    public void addItem(String recipe) {
        this.proposeRecipes.add(recipe);
        notifyDataSetChanged();
    }
}