package alonedroid.com.nanitabe;

import android.app.Application;
import android.app.Fragment;
import android.widget.Toast;

import org.androidannotations.annotations.EApplication;

import lombok.Getter;
import rx.subjects.PublishSubject;

@EApplication
public class NtApplication extends Application {

    @Getter
    private static PublishSubject<Fragment> router = PublishSubject.create();

    public void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}