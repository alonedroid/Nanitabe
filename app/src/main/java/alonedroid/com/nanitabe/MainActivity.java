package alonedroid.com.nanitabe;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.scene.choice.NtChoiceFragment;
import alonedroid.com.nanitabe.scene.top.NtTopFragment;
import alonedroid.com.nanitabe.scene.top.NtTopFragment_;
import alonedroid.com.nanitabe.service.urasearch.UraSearchService;
import alonedroid.com.nanitabe.service.urasearch.UraSearchService_;
import alonedroid.com.nanitabe.sharedpreference.NtServicePreference;
import rx.Subscription;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    @Extra
    String argRecipes;

    @Extra
    String argQuery;

    @App
    NtApplication app;

    @Bean
    NtServicePreference sharedPreference;

    Subscription routerSubscribe;

    public static Intent newIntent(Context context, String query) {
        return new MainActivity_.IntentBuilder_(context).argQuery(query).get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @AfterViews
    void initViews() {
        if (TextUtils.isEmpty(this.argQuery) || TextUtils.isEmpty(this.sharedPreference.getRecipes(this.argQuery))) {
            NtApplication.getRouter().onNext(NtTopFragment.newInstance());
        } else {
            UraSearchService.forceStop();
            UraSearchService_.intent(this).stop();
            NtApplication.getRouter().onNext(NtChoiceFragment.newInstance(this.sharedPreference.getRecipes(this.argQuery).split(","), true));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.argQuery = intent.getStringExtra("argQuery");
        initViews();
    }

    @Override
    protected void onResume() {
        this.routerSubscribe = NtApplication.getMainRouter()
                .filter(fragment -> fragment != null)
                .subscribe(this::replaceFragment);
        super.onResume();
        this.app.start();
    }

    @Override
    protected void onPause() {
        this.routerSubscribe.unsubscribe();
        this.app.stop();
        super.onPause();
    }

    private void replaceFragment(Fragment fragment) {
        String tag = tag(fragment);

        if (secondTimeTop(tag)) {
            reset();
        } else if (fragment instanceof NtTopFragment_) {
            replace(fragment, false);
        } else if (TextUtils.isEmpty(this.argRecipes) && TextUtils.isEmpty(this.argQuery)) {
            replace(fragment, true);
        } else {
            replace(fragment, false);
        }
    }

    private String tag(Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    private void reset() {
        int firstId = getFragmentManager().getBackStackEntryAt(0).getId();
        getFragmentManager()
                .popBackStack(firstId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void replace(Fragment fragment, boolean stack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (stack) {
            transaction.addToBackStack(tag(fragment));
        }
        transaction.replace(R.id.fragment, fragment)
                .commit();
    }

    private boolean secondTimeTop(String tag) {
        if (getFragmentManager().getBackStackEntryCount() <= 0) return false;
        return tag.equals(NtTopFragment_.class.getSimpleName());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) return super.onKeyDown(keyCode, event);

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment);
        if (fragment instanceof NtOnKeyDown) {
            if (((NtOnKeyDown) fragment).goBack()) {
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public interface NtOnKeyDown {
        boolean goBack();
    }
}