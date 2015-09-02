package alonedroid.com.nanitabe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import alonedroid.com.nanitabe.scene.choice.NtChoiceFragment_;
import alonedroid.com.nanitabe.scene.search.NtSearchFragment;
import alonedroid.com.nanitabe.scene.top.NtTopFragment;
import alonedroid.com.nanitabe.scene.top.NtTopFragment_;
import alonedroid.com.nanitabe.service.urasearch.UraSearchService;
import alonedroid.com.nanitabe.service.urasearch.UraSearchService_;
import alonedroid.com.nanitabe.sharedpreference.NtServicePreference;
import rx.Subscription;

@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity {

    @Extra
    String argRecipes;

    @Extra
    String argQuery;

    @Extra
    boolean share;

    @App
    NtApplication app;

    @Bean
    NtServicePreference sharedPreference;

    Subscription routerSubscribe;

    public static Intent newIntent(Context context, String query) {
        return new MainActivity_.IntentBuilder_(context).argQuery(query).get();
    }

    public static Intent newIntentRecipes(Context context, String recipes) {
        return new MainActivity_.IntentBuilder_(context).argRecipes(recipes).get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.share = getIntent().getBooleanExtra("share", false);
    }

    @AfterViews
    void initViews() {
        if (!TextUtils.isEmpty(this.argRecipes)) {
            String[] recipes = this.argRecipes.split(",");
            if (recipes.length == 1) {
                NtApplication.getRouter().onNext(NtSearchFragment.newInstance(null, recipes[0]));
            } else {
                NtApplication.getRouter().onNext(NtChoiceFragment.newInstance(recipes, true, true));
            }
        } else if (TextUtils.isEmpty(this.argQuery) || TextUtils.isEmpty(this.sharedPreference.getRecipes(this.argQuery))) {
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
            reset(fragment);
        } else if (fragment instanceof NtTopFragment_) {
            replace(fragment, false);
        } else if (needBackStackReplace(fragment)) {
            replace(fragment, true);
        } else {
            replace(fragment, false);
        }
    }

    private boolean needBackStackReplace(Fragment fragment) {
        if (fragment instanceof NtChoiceFragment_) {
            return TextUtils.isEmpty(this.argRecipes) && TextUtils.isEmpty(this.argQuery);
        }
        return true;
    }

    private String tag(Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    private void reset(Fragment fragment) {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            replace(fragment, false);
            return;
        }
        int firstId = getFragmentManager().getBackStackEntryAt(0).getId();
        getFragmentManager()
                .popBackStack(firstId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void replace(Fragment fragment, boolean stack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (stack) {
            transaction.addToBackStack(tag(fragment));
        }
        transaction.replace(R.id.fragment, fragment)
                .commit();
    }

    private boolean secondTimeTop(String tag) {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 0) return false;
        return tag.equals(NtTopFragment_.class.getSimpleName());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) return super.onKeyDown(keyCode, event);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (fragment instanceof NtOnKeyDown) {
            if (((NtOnKeyDown) fragment).goBack()) {
                return false;
            }
        }
        if (this.share) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    public interface NtOnKeyDown {
        boolean goBack();
    }
}