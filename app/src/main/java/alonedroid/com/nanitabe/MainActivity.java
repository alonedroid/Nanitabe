package alonedroid.com.nanitabe;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import alonedroid.com.nanitabe.scene.top.NtTopFragment;
import alonedroid.com.nanitabe.scene.top.NtTopFragment_;
import rx.Subscription;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    Subscription routerSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @AfterInject
    void init() {
        this.routerSubscribe = NtApplication.getRouter().subscribe(this::replaceFragment);
    }

    @AfterViews
    void initViews() {
        NtApplication.getRouter().onNext(NtTopFragment.newInstance());
    }

    private void replaceFragment(Fragment fragment) {
        String tag = tag(fragment);
        if (needBack(tag)) {
            getFragmentManager().popBackStack(tag, 0);
        } else if (fragment instanceof NtTopFragment_) {
            replace(fragment, false);
        } else {
            replace(fragment, true);
        }
    }

    private String tag(Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    private void replace(Fragment fragment, boolean stack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (stack) {
            transaction.addToBackStack(tag(fragment));
        }
        transaction.replace(R.id.fragment, fragment)
                .commit();
    }

    private boolean needBack(String tag) {
        int checkIndex = getFragmentManager().getBackStackEntryCount() - 1;
        if (checkIndex < 0) return false;
        String checkTag = getFragmentManager().getBackStackEntryAt(checkIndex).getName();
        return tag.equals(checkTag);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) return super.onKeyDown(keyCode, event);

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment);
        if (fragment instanceof NtOnKeyDown) {
            return ((NtOnKeyDown) fragment).goBack();
        }

        return super.onKeyDown(keyCode, event);
    }

    public interface NtOnKeyDown {
        boolean goBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.routerSubscribe.unsubscribe();
    }
}