package alonedroid.com.nanitabe.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import alonedroid.com.nanitabe.NtRouter;

@EActivity(R.layout.activity_variable)
public class VariableActivity extends FragmentActivity {

    @Extra
    protected String map;

    public static Intent newIntent(Context context, String map) {
        VariableActivity_.IntentBuilder_ builder_ = VariableActivity_.intent(context);
        builder_.map(map);
        return builder_.get();
    }

    @AfterViews
    protected void onAfterViews() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, NtRouter.route(this.map))
                .commit();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) return super.onKeyDown(keyCode, event);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
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