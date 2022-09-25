package com.aiven.updateapp.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.aiven.updateapp.R;

public class LayoutDialog {

    private Context mContext;
    private AlertDialog alertDialog;
    private View rootView;

    public LayoutDialog(Context context, int layoutId) {

        this(context, layoutId, R.style.mydialog);
    }

    public LayoutDialog(Context context, int layoutId, int style) {

        mContext = context;
        rootView = LayoutInflater.from(mContext).inflate(layoutId, null);
        alertDialog = new AlertDialog.Builder(mContext, style).setView(rootView).create();
        alertDialog.getWindow().setWindowAnimations(R.style.DialogShowHideStyle);
    }

    public LayoutDialog setCancelable(boolean flag) {

        alertDialog.setCancelable(flag);
        return this;
    }

    public void show() {

        if (!alertDialog.isShowing()) {

            alertDialog.show();
        }
    }

    public void dismiss() {

        if (alertDialog.isShowing()) {

            alertDialog.dismiss();
        }
    }

    public View getRootView() {

        return rootView;
    }

    public boolean isShowing() {
        return alertDialog.isShowing();
    }
}
