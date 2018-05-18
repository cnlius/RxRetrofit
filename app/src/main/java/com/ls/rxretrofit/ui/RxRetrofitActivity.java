package com.ls.rxretrofit.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ls.rxretrofit.R;
import com.ls.rxretrofit.api.RxRetrofitApi;
import com.ls.rxretrofit.app.Constants;
import com.ls.rxretrofit.databinding.ActivityRxRetrofitBinding;
import com.ls.rxretrofit.http.HttpManager;
import com.ls.rxretrofit.http.HttpSubscriber;
import com.ls.rxretrofit.utils.NetworkUtils;
import com.ls.rxretrofit.vo.HttpResult;
import com.ls.rxretrofit.vo.JokeVo;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Jason on 2018/1/7.
 */

public class RxRetrofitActivity extends RxAppCompatActivity implements View.OnClickListener {
    private ActivityRxRetrofitBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_rx_retrofit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get:
                getJokes();
                break;
        }
    }

    private void getJokes() {
//        HttpManager.getInstance().create(RxRetrofitApi.class)
//                .getJokes(Constants.JUHE_APPKEY_JOKE)
//                .compose(HttpManager.<HttpResult<JokeVo>>handleObservable(this))
//                .subscribe(new HttpSubscriber<JokeVo>(this) {
//                    @Override
//                    protected void onSuccess(JokeVo jokes) {
//                        String content = jokes.getData().get(0).getContent();
//                        Toast.makeText(RxRetrofitActivity.this, content, Toast.LENGTH_SHORT).show();
//                    }
//                });

        HttpManager.getInstance().create(RxRetrofitApi.class)
                .test("17629294728","123456")
                .compose(HttpManager.<HashMap<String,String>>handleObservable(this))
                .subscribe(new Observer<HashMap<String,String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HashMap<String,String> s) {
                        String token=s.get("token");
                        Toast.makeText(RxRetrofitActivity.this, ""+token, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(RxRetrofitActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
