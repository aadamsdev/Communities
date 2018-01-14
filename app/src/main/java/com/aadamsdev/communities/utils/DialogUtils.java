package com.aadamsdev.communities.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

/**
 * Created by andrewadams on 2017-12-05
 */
public final class DialogUtils {
    public static DialogInterface.OnKeyListener DISABLE_BACK_KEY_LISTENER = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            return keyCode == KeyEvent.KEYCODE_BACK;
        }
    };

    public static void dismiss(AppCompatActivity activity, String dialogTag) {
        dismiss(activity.getSupportFragmentManager(), dialogTag);
    }

    public static void dismiss(Fragment fragment, String dialogTag) {
        dismiss(fragment.getFragmentManager(), dialogTag);
    }

    public static void setModal(Dialog dialog) {
        dialog.setOnKeyListener(DISABLE_BACK_KEY_LISTENER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    public static void show(AppCompatActivity activity, DialogFragment dialog, String dialogTag) {
        show(activity.getSupportFragmentManager(), dialog, dialogTag);
    }

    public static void show(Fragment fragment, DialogFragment dialog, String dialogTag) {
        show(fragment.getFragmentManager(), dialog, dialogTag);
    }

    private static void dismiss(FragmentManager manager, String dialogTag) {
        if (manager == null) {
            return;
        }

        DialogFragment dialog = (DialogFragment) manager.findFragmentByTag(dialogTag);
        if (dialog == null) {
            return;
        }

        manager.beginTransaction()
                .remove(dialog)
                .commitAllowingStateLoss();
    }

    private static void show(FragmentManager manager, DialogFragment dialog, String dialogTag) {
        if (manager == null || manager.findFragmentByTag(dialogTag) != null) {
            return;
        }

        manager.beginTransaction()
                .add(dialog, dialogTag)
                .commitAllowingStateLoss();
    }

    private DialogUtils() {
    }
}