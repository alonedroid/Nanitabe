package alonedroid.com.nanitabe.scene.select;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;

import lombok.Getter;
import rx.subjects.BehaviorSubject;

@EFragment
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

    public static NtDialogFragment newInstance() {
        NtDialogFragment_.FragmentBuilder_ builder_ = NtDialogFragment_.builder();
        return builder_.build();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(this.dialogTitle)
                .setMessage(this.dialogMessage)
                .setPositiveButton(this.dialogPositive, (dialog, which) -> this.which.onNext(which))
                .setNegativeButton(this.dialogNegative, (dialog, which) -> this.which.onNext(which))
                .create();
    }
}