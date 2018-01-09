package com.ls.rxretrofit.http;

import android.content.Context;
import android.net.ParseException;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.ls.rxretrofit.custom.LoadingManager;
import com.ls.rxretrofit.utils.NetworkUtils;
import com.ls.rxretrofit.vo.HttpResult;

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

public abstract class HttpSubscriber<T> implements Observer<HttpResult<T>> {
    private boolean isShowLoading; //是否显示loading框,wrContext!=null时显示；
    private WeakReference<Context> wrContext; //弱引用

    public Context getContext() {
        return wrContext.get();
    }

    public void setWrContext(Context context) {
        this.wrContext = new WeakReference<>(context);
    }

    public HttpSubscriber() {
        this.isShowLoading = false;
    }

    public HttpSubscriber(Context context) {
        setWrContext(context);
        this.isShowLoading = true;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (isShowLoading) {
            LoadingManager.showProgressDialog(getContext());
        }
    }

    @Override
    public void onNext(HttpResult<T> httpResult) {
        onSuccess(httpResult.getResult());
    }

    @Override
    public void onError(Throwable e) {
        if (isShowLoading) {
            LoadingManager.dismissProgressDialog();
        }
        String message;
        if (!NetworkUtils.isConnected(getContext())) {
            message = "没有网络";
        } else if (e instanceof HttpException) {
            message = "http错误码：" + ((HttpException) e).code();
        } else if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
            message = "链接异常";
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            message = "数据解析失败";
        } else if (e instanceof UnknownHostException) {
            message = "域名解析失败";
        } else {
            message = "请求失败";
        }
        if (TextUtils.isEmpty(message)) {
            message = e.getMessage();
        }
        onFailure(message);
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        if (isShowLoading) {
            LoadingManager.dismissProgressDialog();
        }
    }

    protected abstract void onSuccess(T t);

    protected void onFailure(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
