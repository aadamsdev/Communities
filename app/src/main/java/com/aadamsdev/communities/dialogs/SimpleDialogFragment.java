package com.aadamsdev.communities.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.aadamsdev.communities.R;
import com.aadamsdev.communities.utils.DialogUtils;

/**
 * Created by andrewadams on 2017-12-05
 */
public class SimpleDialogFragment extends DialogFragment {
    private static final String ARG_MESSAGE_TEXT = "messageText";
    private static final String ARG_MESSAGE_ID = "messageId";
    private static final String ARG_MODAL = "modal";
    private static final String ARG_TITLE = "title";
    private static final String ARG_TITLE_ID = "titleId";

    private OnDismissListener onDismissListener;
    private String messageText;
    private int id;
    private boolean isModal;
    private String title;
    private int titleId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        messageText = bundle.getString(ARG_MESSAGE_TEXT, "");
        id = bundle.getInt(ARG_MESSAGE_ID);
        isModal = onDismissListener != null || bundle.getBoolean(ARG_MODAL);
        title = bundle.getString(ARG_TITLE, "");
        titleId = bundle.getInt(ARG_TITLE_ID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setPositiveButton(android.R.string.ok, null);

        if (titleId != 0) {
            builder.setTitle(titleId);
        } else {
            builder.setTitle(title);
        }

        if (id != 0) {
            builder.setMessage(id);
        } else {
            builder.setMessage(messageText);
        }

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        if (isModal) {
            DialogUtils.setModal(dialog);
        }
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    public static SimpleDialogFragment newInstance(int messageId) {
        return newInstance(0, messageId);
    }

    public static SimpleDialogFragment newInstance(int titleId, int messageId) {
        return newInstance(titleId, messageId, false);
    }

    public static SimpleDialogFragment newInstance(int titleId, int messageId, boolean modal) {
        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE_ID, messageId);
        args.putBoolean(ARG_MODAL, modal);
        args.putInt(ARG_TITLE_ID, titleId);

        SimpleDialogFragment fragment = new SimpleDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SimpleDialogFragment newInstance(String messageText) {
        return newInstance(0, messageText);
    }

    public static SimpleDialogFragment newInstance(int titleId, String messageText) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE_TEXT, messageText);
        args.putInt(ARG_TITLE_ID, titleId);

        SimpleDialogFragment fragment = new SimpleDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SimpleDialogFragment newInstance(String title, int messageId) {
        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE_ID, messageId);
        args.putString(ARG_TITLE, title);

        SimpleDialogFragment fragment = new SimpleDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SimpleDialogFragment newInstance(String title, String messageText) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE_TEXT, messageText);
        args.putString(ARG_TITLE, title);

        SimpleDialogFragment fragment = new SimpleDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }
}