package com.ls.rxretrofit.http;

import android.content.Context;
import android.net.ParseException;
import android.text.TextUtils;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Created by liusong on 2018/1/8.
 */

public abstract class HttpSubscriber<T> implements Observer<T> {
    private WeakReference<Context> context;
    private boolean isHasProgress; //是否显示loading框,context!=null时显示；

    public HttpSubscriber() {
        this.isHasProgress = false;
    }

    public HttpSubscriber(Context context) {
        this.isHasProgress = true;
        setContext(context);
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (isHasProgress) {
            LoadingManager.showProgressDialog(getContext());
        }
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        String message;
        if (e instanceof HttpException) {
            message = "http错误码：" + ((HttpException) e).code();
        } else if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
            message = "链接异常";
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            message = "解析异常";
        } else if (e instanceof UnknownHostException) {
            message = "解析域名异常";
        } else {
            message = "未知异常";
        }
        e.printStackTrace();
        if (TextUtils.isEmpty(message)) {
            message = e.getMessage();
        }
        onFailure(message);
    }

    @Override
    public void onComplete() {
        if (isHasProgress) {
            LoadingManager.dismissProgressDialog();
        }
    }

    protected abstract void onSuccess(T t);

    protected void onFailure(String message) {

    }

    public void setContext(Context context) {
        this.context = new WeakReference<>(context);
    }

    public Context getContext() {
        return context.get();
    }
}
