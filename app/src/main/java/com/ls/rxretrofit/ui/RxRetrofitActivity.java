package com.ls.rxretrofit.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;
import com.ls.rxretrofit.R;
import com.ls.rxretrofit.api.RxRetrofitApi;
import com.ls.rxretrofit.app.Constants;
import com.ls.rxretrofit.databinding.ActivityRxRetrofitBinding;
import com.ls.rxretrofit.http.HttpManager;
import com.ls.rxretrofit.http.HttpSubscriber;
import com.ls.rxretrofit.vo.JokeVo;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

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
        HttpManager.getInstance().create(RxRetrofitApi.class)
                .getJoke(Constants.JUHE_APPKEY_JOKE)
                .compose(HttpManager.<JokeVo>handleObservable(this))
                .subscribe(new HttpSubscriber<JokeVo>() {
                    @Override
                    protected void onSuccess(JokeVo jokeVo) {
                        String content = jokeVo.getResult().getData().get(0).getContent();
                        Toast.makeText(RxRetrofitActivity.this, content, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}