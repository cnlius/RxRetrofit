package com.ls.rxretrofit.http;

import android.net.ParseException;
import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.ls.rxretrofit.app.App;
import com.ls.rxretrofit.custom.LoadingManager;
import com.ls.rxretrofit.utils.NetworkUtils;
import com.ls.rxretrofit.vo.HttpResult;

import org.json.JSONException;

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
    private boolean isShowLoading; //是否显示loading框,context!=null时显示；

    public HttpSubscriber() {
        this.isShowLoading = false;
    }

    public HttpSubscriber(boolean isShowLoading) {
        this.isShowLoading = isShowLoading;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (isShowLoading) {
            LoadingManager.showProgressDialog(App.mApp.getContext());
        }
    }

    @Override
    public void onNext(HttpResult<T> httpResult) {
        onSuccess(httpResult.getResult());
    }

    @Override
    public void onError(Throwable e) {
        String message;
        if (!NetworkUtils.isConnected(App.mApp.getContext())) {
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

    }
}
