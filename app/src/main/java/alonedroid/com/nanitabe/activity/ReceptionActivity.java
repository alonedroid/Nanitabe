package alonedroid.com.nanitabe.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.NtRouter;
import alonedroid.com.nanitabe.service.urasearch.UraSearchService;
import alonedroid.com.nanitabe.service.urasearch.UraSearchService_;
import alonedroid.com.nanitabe.sharedpreference.NtServicePreference;

@EActivity(R.layout.activity_main)
public class ReceptionActivity extends FragmentActivity {

    @App
    NtApplication app;

    @Extra
    String argQuery;

    @Bean
    NtServicePreference sharedPreference;

    public static Intent newIntent(Context context, String query) {
        return new ReceptionActivity_.IntentBuilder_(context).argQuery(query).get();
    }

    @AfterInject
    protected void init() {
        this.app.modeReception();
    }


    @AfterViews
    protected void onAfterViews() {
        UraSearchService.forceStop();
        UraSearchService_.intent(this).stop();
        startActivity(VariableActivity.newIntent(this, NtRouter.getChoiceMap(this.sharedPreference.getRecipes(this.argQuery))));
        finish();
    }
}
