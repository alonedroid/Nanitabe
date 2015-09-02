package alonedroid.com.nanitabe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import alonedroid.com.nanitabe.activity.R;

public class ShareActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextAction();
    }

    private void nextAction() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Intent newIntent = MainActivity.newIntentRecipes(getApplicationContext(), intent.getDataString().split("\\?")[1]);
            newIntent.putExtra("share", true);
            startActivity(newIntent);
            finish();
        }
    }
}
