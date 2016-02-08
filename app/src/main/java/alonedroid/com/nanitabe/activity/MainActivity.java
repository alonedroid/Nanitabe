package alonedroid.com.nanitabe.activity;

import android.support.v4.app.FragmentActivity;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.NtRouter;

@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity {

    @App
    NtApplication app;

    @AfterInject
    protected void init() {
        this.app.modeNormal();
    }

    @AfterViews
    protected void initViews() {
        startActivity(VariableActivity.newIntent(this, NtRouter.getTopMap()));
        finish();
    }
}