package alonedroid.com.nanitabe.scene.choice;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.util.ArrayList;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.view.NtChoiceItemView;
import lombok.Getter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

@EBean
public class NtChoiceAdapter extends PagerAdapter {

    @App
    NtApplication app;

    @RootContext
    Context context;

    @SystemService
    LayoutInflater layoutInflater;

    @Getter
    private PublishSubject<String> titleSubject = PublishSubject.create();

    private Subscription[] subscriptions = new Subscription[3];

    private ArrayList<NtVisual> recipes = new ArrayList<>();

    private SparseArray<NtChoiceItemView> imageViews = new SparseArray<>();

    @Override
    public int getCount() {
        return this.recipes.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        NtChoiceItemView view = NtChoiceItemView.newInstance(context);
        container.addView(view);
        this.imageViews.put(position, view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof NetworkImageView) {
            if (((BitmapDrawable) ((NetworkImageView) object).getDrawable()).getBitmap() == null)
                return;
            ((BitmapDrawable) ((NetworkImageView) object).getDrawable()).getBitmap().recycle();
        }
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object.equals(view);
    }

    void setPosition(int position) {
        if (this.recipes.size() <= position) return;

        NtVisual visual = this.recipes.get(position);
        if (visual.isPrepare()) {
            this.titleSubject.onNext(visual.getTitle().getValue());
            this.imageViews.get(position).getNetworkImageView()
                    .setImageUrl(
                            visual.getImage().getValue(),
                            new ImageLoader(this.app.getQueue(), new ImageLruCache()));
            this.imageViews.get(position).setIngredient(visual.getIngredients().getValue());
        } else {
            subscribe(position);
        }
    }

    private void subscribe(int position) {
        unsubscribe();

        NtVisual visual = this.recipes.get(position);
        this.subscriptions[0] = visual.getTitle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this.titleSubject::onNext);
        this.subscriptions[1] = visual.getImage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(image -> getItem(position)
                        .getNetworkImageView()
                        .setImageUrl(
                                visual.getImage().getValue(),
                                new ImageLoader(this.app.getQueue(), new ImageLruCache())));
        this.subscriptions[2] = visual.getIngredients()
                .subscribe(text -> getItem(position).setIngredient(text));
    }

    void unsubscribe() {
        for (Subscription sub : this.subscriptions) {
            unsubscribe(sub);
        }
    }

    private void unsubscribe(Subscription subscription) {
        if (subscription == null || subscription.isUnsubscribed()) return;
        subscription.unsubscribe();
    }

    private NtChoiceItemView getItem(int position) {
        NtChoiceItemView view = this.imageViews.get(position);
        if (view != null) return view;
        return (NtChoiceItemView) instantiateItem(new LinearLayout(this.context), position);
    }

    public void addItem(String[] ids) {
        Observable.from(ids).forEach(this::addItem);
    }

    public void addItem(String id) {
        NtVisual visual = NtVisual_.getInstance_(this.context);
        visual.analyze(id);
        this.recipes.add(visual);
        notifyDataSetChanged();
        if (this.recipes.size() == 1) {
            setPosition(0);
        }
    }

    public String getId(int position) {
        if (this.recipes.size() <= position) return "";
        return this.recipes.get(position).getId();
    }
}