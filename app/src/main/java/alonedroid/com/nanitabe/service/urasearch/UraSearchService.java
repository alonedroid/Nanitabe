package alonedroid.com.nanitabe.service.urasearch;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;

import alonedroid.com.nanitabe.MainActivity;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.scene.choice.NtTsukurepoAnalyzer;
import alonedroid.com.nanitabe.sharedpreference.NtServicePreference;
import alonedroid.com.nanitabe.utility.NtTextUtility;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

@EService
public class UraSearchService extends IntentService {

    public static final int ANALIZE_SERVICE_MAX_RECIPES = 30;

    public static final String EXTRAS_QUERY = "extrasQuery";

    @SystemService
    NotificationManager notificationManager;

    @Bean
    NtTsukurepoAnalyzer analyzer;

    @Bean
    NtServicePreference sharedPreference;

    @StringRes
    String searchUrl;

    @StringRes
    String searchServiceStartingTicker;

    @StringRes
    String searchServiceStartingTitle;

    @StringRes
    String searchServiceStartingContent;

    @StringRes
    String searchServiceWorkingTitle;

    @StringRes
    String searchServiceWorkingContent;

    @StringRes
    String searchServiceCompleteTicker;

    @StringRes
    String searchServiceCompleteTitle;

    @StringRes
    String searchServiceCompleteContent;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private String query;

    private ArrayList<String> recipes;

    public UraSearchService() {
        super(UraSearchService.class.getSimpleName());
    }

    public static void forceStop() {
        sStop.onNext(true);
    }

    private static PublishSubject<Boolean> sStop = PublishSubject.create();

    @Override
    protected void onHandleIntent(Intent intent) {
        this.query = intent.getStringExtra(EXTRAS_QUERY);
        String query = NtTextUtility.encode(this.query);
        if (TextUtils.isEmpty(query)) return;

        analyze(query);
    }

    private void analyze(String query) {
        this.recipes = new ArrayList<>();

        this.compositeSubscription.add((this.analyzer.getComplete()
                .subscribe(isComplete -> onComplete())));
        this.compositeSubscription.add(sStop
                .filter(stop -> stop)
                .subscribe(stop -> this.analyzer.cancel()));
        this.compositeSubscription.add(this.analyzer.getSubject()
                .limit(ANALIZE_SERVICE_MAX_RECIPES)
                .subscribe(this::addAndReport));
        this.analyzer.analyze(this.searchUrl + query);
        notifyRecipes(String.format(this.searchServiceStartingTicker, this.query), this.searchServiceStartingTitle, this.searchServiceStartingContent);
    }

    private void addAndReport(String recipe) {
        this.recipes.add(recipe);
        if (this.recipes.size() == 1) {
            notifyRecipes(null, this.searchServiceWorkingTitle, this.searchServiceWorkingContent);
        }

        String recipes = Observable.from(this.recipes).reduce((ids, id) -> ids + "," + id).toBlocking().single();
        this.sharedPreference.putRecipes(this.query, recipes);
    }

    private void onComplete() {
        String content = String.format(this.searchServiceCompleteContent, this.query, this.recipes.size());
        notifyRecipes(this.searchServiceCompleteTicker, this.searchServiceCompleteTitle, content);
        this.compositeSubscription.clear();
    }

    @UiThread
    void notifyRecipes(String ticker, String title, String content) {
        Intent intent = MainActivity.newIntent(this, this.query);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        if (this.searchServiceCompleteTicker.equals(ticker)) {
            notification.vibrate = new long[]{100, 200, 100, 200};
        }

        this.notificationManager.cancelAll();
        this.notificationManager.notify(R.string.app_name, notification);
    }
}