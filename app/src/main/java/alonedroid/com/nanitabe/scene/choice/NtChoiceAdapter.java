package alonedroid.com.nanitabe.scene.choice;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.widget.TextView;

import alonedroid.com.nanitabe.R;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class NtChoiceAdapter extends FragmentStatePagerAdapter {

    private String[] proposeRecipes;

    private SparseArray<NtChoiceImageFragment> fragments = new SparseArray<>();

    private TextView titleView;

    private Subscription titleSubscription;

    public NtChoiceAdapter(Context context, FragmentManager fm, String[] urls) {
        super(fm);
        this.proposeRecipes = urls;
    }

    @Override
    public Fragment getItem(int position) {
        if (this.fragments.get(position) == null) {
            this.fragments.put(position, NtChoiceImageFragment.newInstance(this.proposeRecipes[position]));
        }

        if (this.titleSubscription == null) {
            promise(this.fragments.get(position));
        }

        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.proposeRecipes.length;
    }

    public void setRecipeTitle(TextView title, int position) {
        this.titleView = title;

        if (this.fragments.get(position) == null || this.fragments.get(position).getTitle().getValue() == null) {
            this.titleView.setText(R.string.loading);
            if (this.fragments.get(position) != null) {
                promise(this.fragments.get(position));
            }
        } else {
            this.titleView.setText(this.fragments.get(position).getTitle().getValue());
        }
    }

    private void promise(NtChoiceImageFragment fragment) {
        if (this.titleSubscription != null) {
            this.titleSubscription.unsubscribe();
        }
        this.titleSubscription = fragment.getTitle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this.titleView::setText);
    }
}