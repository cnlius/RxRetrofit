package com.ls.rxretrofit.api;

import android.support.annotation.NonNull;

import com.ls.rxretrofit.vo.HttpResult;
import com.ls.rxretrofit.vo.JokeVo;
import com.ls.rxretrofit.vo.Jokes;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by liusong on 2018/1/3.
 */

public interface RxRetrofitApi {

    //test
    @GET("http://japi.juhe.cn/joke/content/text.from")
    Observable<HttpResult<JokeVo>> getJokes(
            @NonNull @Query("key") String appKey
    );

    @GET("http://japi.juhe.cn/joke/content/text.from")
    Observable<Jokes> get(
            @Url String url,
            @FieldMap Map<String, String> params
    );

    @FormUrlEncoded
    @POST("http://api.snakepiperobot.com/user/user/login")
    Observable<HashMap<String,String>> test(
            @Field("phone") String phone,
            @Field("password_hash") String password_hash
    );

}
