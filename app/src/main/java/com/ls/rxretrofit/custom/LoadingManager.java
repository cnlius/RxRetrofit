package com.ls.rxretrofit.custom;

import android.content.Context;

import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Created by Administrator on 2016/10/12 0012.
 */

public class LoadingManager {
    private static KProgressHUD mKProgressHUD;

    public static void showProgressDialog(Context context){
        showProgressDialog(context,null);
    }

    public static void showProgressDialog(Context context, String label) {
        if (context == null) {
            throw new NullPointerException("context==null");
        }
        if (mKProgressHUD != null && mKProgressHUD.isShowing()) {
            mKProgressHUD.show();
        } else {
            mKProgressHUD = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(label)
                    .setCancellable(false)
                    //.setAnimationSpeed(1)
                    .setDimAmount(0.5f)
                    .show();
        }
    }

    public static void dismissProgressDialog() {
        if (mKProgressHUD != null) {
            mKProgressHUD.dismiss();
            mKProgressHUD = null;
        }
    }
}
