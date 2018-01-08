package com.ls.rxretrofit.api;

import android.support.annotation.NonNull;

import com.ls.rxretrofit.vo.JokeVo;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * retrofit接口api定义
 * <p>
 * 包含内容：
 * 1> get
 * 2> post
 * 3> http
 * 4> 文件上传和获取
 * 5> header设置
 * 结果数据：
 * 1> 原始数据类型：ResponseBody；
 * 2> Call<T> T为json转换后的数据类型；
 * Created by liusong on 2018/1/3.
 */

public interface RetrofitApi {

    //-------------------------------------------
    //【get请求】
    //一般的get请求
    @GET("http://japi.juhe.cn/joke/content/text.from")
    Call<JokeVo> getJoke(
            @NonNull @Query("key") String appKey
    );

    //get请求，返回原始数据
    @GET("http://japi.juhe.cn/joke/content/text.from")
    Call<ResponseBody> getJokeString(
            @NonNull @Query("key") String appKey
    );

    //get请求，参数多时使用map
    @GET("http://japi.juhe.cn/joke/content/text.from")
    Call<JokeVo> getJokeUseMap(
            @QueryMap Map<String, String> params
    );

    //get请求，@Query参数，一个键对应多个值
    @GET("http://japi.juhe.cn/joke/content/text.from")
    Call<JokeVo> getJokeUseMultiValue(
            @NonNull @Query("key") String... appKey
    );

    //@Url注解，调用的时候再填充url
    @GET
    Call<JokeVo> getJokeUseUrl(
            @Url String url,
            @NonNull @Query("key") String appKey
    );

    //path中使用变量占位符(http://japi.juhe.cn/joke/content/text.from)
    @GET("http://japi.juhe.cn/joke/content/{suffix}")
    Call<JokeVo> getJokeUseVar(
            @Path("suffix") String suffix,
            @NonNull @Query("key") String appKey
    );

    //-------------------------------------------
    //【post请求】
    //一般的post请求（post请求以表单的形式）
    @FormUrlEncoded
    @POST("http://japi.juhe.cn/joke/content/text.from")
    Call<JokeVo> postJoke(
            @NonNull @Field("key") String appKey
    );

    //post请求,Filed集合
    @FormUrlEncoded
    @POST("test")
    Call<ResponseBody> testPost(@FieldMap Map<String, String> map);

    //非表单json请求体,参数可以是一个bean类数据
    @POST("test")
    Call<ResponseBody> testBody(@Body RequestBody body);
    //-------------------------------------------
    //【文件相关】

    //获取图片文件（@Streaming：返回数据较大时，如文件流，需要使用该注解）
    @Streaming
    @FormUrlEncoded
    @POST("getImage")
    Call<ResponseBody> downloadImage(
            @NonNull @Field("test") String test
    );

    //图文上传，多文件上传
    @POST("uploadImages")
    Call<ResponseBody> uploadImages(@Body RequestBody Body);

    //Multipart：图文上传
    @Multipart
    @POST("postSingleImage")
    Call<ResponseBody> postTextImage(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    //Multipart：图文上传,多文件
    @Multipart
    @POST("test")
    Call<ResponseBody> postTextImages(
            @Part("description") RequestBody description,
            @PartMap Map<String, RequestBody> images
    );

    //-------------------------------------------
    //【http请求】

    /**
     * method  请求方法，不区分大小写
     * path    路径
     * hasBody 是否有请求体
     */
    @HTTP(method = "get", path = "test/{id}", hasBody = false)
    Call<ResponseBody> testHttp(@Path("id") int id);

    //-------------------------------------------
    //【Header】
    //静态添加header
    @Headers("key: value")
    @GET("test")
    Call<ResponseBody> testStaticHeader();

    //静态添加多个header
    @Headers({
            "Accept: application/vnd.github.v3.full+json",
            "User-Agent: Retrofit-Sample-App"
    })
    @GET("test")
    Call<ResponseBody> testStaticHeaders();

    //动态加header
    @GET("test")
    Call<ResponseBody> testHeader(@Header("cer12306") String header);

    //动态加headers
    @GET("test")
    Call<ResponseBody> testHeaderMap(@HeaderMap Map<String, String> headers);
    //-------------------------------------------
    //【https】
    @GET("https://kyfw.12306.cn/otn/")
    Call<ResponseBody> getHttps();
}
