package alonedroid.com.nanitabe.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.NtRouter;

@EActivity(R.layout.activity_main)
public class ShareActivity extends FragmentActivity {

    @App
    NtApplication app;

    @AfterInject
    protected void init() {
        this.app.modeShare();
    }

    @AfterViews
    protected void initViews() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            nextAction(intent.getData().getQueryParameter("id"));
        }
        finish();
    }

    private void nextAction(String recipe) {
        if (TextUtils.isEmpty(recipe)) {
            Toast.makeText(this, "レシピが見つかりませんでした。", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        String[] recipes = recipe.split(",");
        if (recipes.length == 1) {
            startActivity(VariableActivity.newIntent(this, NtRouter.getTopSearchMap(recipe)));
        } else {
            startActivity(VariableActivity.newIntent(this, NtRouter.getChoiceMap(recipe)));
        }
    }
}
