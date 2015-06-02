package alonedroid.com.nanitabe.scene.select;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import lombok.Getter;
import rx.subjects.BehaviorSubject;

@EBean
public class NtDialogFragment extends DialogFragment {

    @StringRes
    String dialogTitle;

    @StringRes
    String dialogMessage;

    @StringRes
    String dialogPositive;

    @StringRes
    String dialogNegative;

    @Getter
    private BehaviorSubject<Integer> which = BehaviorSubject.create(0);

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(this.dialogTitle)
                .setMessage(this.dialogMessage)
                .setPositiveButton(this.dialogPositive, (dialog, which) -> this.which.onNext(which))
                .setNegativeButton(this.dialogNegative, (dialog, which) -> this.which.onNext(which))
                .create();
    }
}