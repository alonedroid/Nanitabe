package alonedroid.com.nanitabe.service.urasearch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;

import java.util.ArrayList;

import alonedroid.com.nanitabe.NtApplication;
import alonedroid.com.nanitabe.activity.R;
import alonedroid.com.nanitabe.activity.ReceptionActivity;
import alonedroid.com.nanitabe.scene.choice.NtTsukurepoAnalyzer;
import alonedroid.com.nanitabe.sharedpreference.NtServicePreference;
import alonedroid.com.nanitabe.utility.NtTextUtility;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

@EService
public class UraSearchService extends Service {

    public static final int ANALIZE_SERVICE_MAX_RECIPES = 30;

    public static final String EXTRAS_QUERY = "extrasQuery";

    public static final String SEARCH_SERVICE_COMPLETE_TICKER = "検索が完了しました";

    public static final String SEARCH_SERVICE_STARTING_TICKER = "%1$sでレシピを検索します";
    public static final String SEARCH_SERVICE_STARTING_TITLE = "レシピを検索しています";
    public static final String SEARCH_SERVICE_STARTING_CONTENT = "処理を中断できます";
    public static final String SEARCH_SERVICE_WORKING_TITLE = "レシピが見つかりました";
    public static final String SEARCH_SERVICE_WORKING_CONTENT = "処理を中断してレシピを見れます";
    public static final String SEARCH_SERVICE_COMPLETE_TITLE = "レシピの検索が完了しています";
    public static final String SEARCH_SERVICE_COMPLETE_CONTENT = "%1$sの検索結果は%2$s件です";


    @App
    NtApplication app;

    @SystemService
    NotificationManager notificationManager;

    @Bean
    NtTsukurepoAnalyzer analyzer;

    @Bean
    NtServicePreference sharedPreference;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private String query;

    private ArrayList<String> recipes;

    public static void forceStop() {
        sStop.onNext(true);
    }

    private static PublishSubject<Boolean> sStop = PublishSubject.create();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.query = intent.getStringExtra(EXTRAS_QUERY);
        String query = NtTextUtility.encode(this.query);
        if (TextUtils.isEmpty(query)) {
            stopSelf();
        } else {
            analyze(query);
        }

        return START_NOT_STICKY;
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
                .subscribe(this::addAndReport, this::onError, this::onLimitComplete));
        this.analyzer.setShowLog(false);
        this.analyzer.analyze(this.app.getString(R.string.search_url) + query);
        notifyRecipes(String.format(SEARCH_SERVICE_STARTING_TICKER, this.query)
                , SEARCH_SERVICE_STARTING_TITLE, SEARCH_SERVICE_STARTING_CONTENT);
    }

    private void addAndReport(String recipe) {
        this.recipes.add(recipe);
        if (this.recipes.size() == 1) {
            notifyRecipes(null, SEARCH_SERVICE_WORKING_TITLE, SEARCH_SERVICE_WORKING_CONTENT);
        }

        String recipes = Observable.from(this.recipes).reduce((ids, id) -> ids + "," + id).toBlocking().single();
        this.sharedPreference.putRecipes(this.query, recipes);
    }

    private void onError(Throwable throwable) {

    }

    private void onLimitComplete() {
        this.analyzer.cancel();
    }

    private void onComplete() {
        String content = String.format(SEARCH_SERVICE_COMPLETE_CONTENT, this.query, this.recipes.size());
        notifyRecipes(SEARCH_SERVICE_COMPLETE_TICKER, SEARCH_SERVICE_COMPLETE_TITLE, content);
        this.compositeSubscription.clear();
    }

    void notifyRecipes(String ticker, String title, String content) {
        Intent intent = ReceptionActivity.newIntent(this, this.query);
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

        this.notificationManager.cancelAll();

        if (SEARCH_SERVICE_COMPLETE_TICKER.equals(ticker)) {
            notification.vibrate = new long[]{100, 200, 100, 200};
            this.notificationManager.notify(R.string.app_name, notification);
        } else {
            startForeground(R.string.app_name, notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}