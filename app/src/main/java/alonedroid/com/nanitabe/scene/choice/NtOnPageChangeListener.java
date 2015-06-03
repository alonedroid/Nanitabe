package alonedroid.com.nanitabe.scene.choice;

import android.support.v4.view.ViewPager;

import org.androidannotations.annotations.EBean;

import lombok.Getter;
import rx.subjects.BehaviorSubject;

@EBean
class NtOnPageChangeListener implements ViewPager.OnPageChangeListener {

    @Getter
    private BehaviorSubject<Integer> selectedPosition = BehaviorSubject.create();

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        this.selectedPosition.onNext(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}