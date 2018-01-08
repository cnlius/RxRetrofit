package com.ls.rxretrofit.http;

import android.text.TextUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.ByteString;

/**
 * okHttp请求拦截器:
 * 内容：
 * 1> 加请求头;
 * 2> 给所有请求加参数sign（签名）;
 *
 * 相关拓展：
 * 1> 可获取request;
 * 2> 重构request;
 * 3> 重构url(可移除指定的参数);
 * Created by liusong on 2018/1/4.
 */

public class RequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        //原始request对象；
        Request request = chain.request();
//      boolean isHttps = request.isHttps();
        String method = request.method();
        String url = request.url().toString();

        //重构request对象；
        Request.Builder requestBuilder = request.newBuilder();
        //给url增加sign参数
        String[] split = url.split("\\?");
        if (split.length == 1) { //没有? 可能是get也可能是post(这里没有考虑其他的请求方式)
            //如果是get请求直接添加查询参数；
            if (TextUtils.equals("GET", method)) { //get请求没有携带参数
                HttpUrl newUrl = request.url()
                        .newBuilder()
                        .addQueryParameter("sign", generateSignedParameters(""))
                        .build();
                requestBuilder.url(newUrl);
            } else if (TextUtils.equals("POST", method)) { //post请求
                String parameters = convertString(request.body());
                RequestBody requestBodyAdd = new FormBody.Builder()
                        .add("sign", generateSignedParameters(parameters))
                        .build();
                //post需要sign参数和原来的拼接
                RequestBody body = RequestBody.create(
                        MediaType.parse("application/x-www-form-urlencoded"),
                        parameters + "&" + convertString(requestBodyAdd)
                );
                requestBuilder.method(request.method(), body);
            }
        } else if (split.length == 2) { //有一个?，说明是get请求,有参数
            HttpUrl newUrl = request.url()
                    .newBuilder()
//                    .removeAllQueryParameters("key") //先移除原来的参数，再添加加密后的参数；
                    .addQueryParameter("sign", generateSignedParameters(split[1]))
                    .build();
            requestBuilder.url(newUrl);
        }

        //header处理
        requestBuilder.addHeader("User-Agent", "android");
        requestBuilder.addHeader("appNo", "1.4.3");
        Response response = chain.proceed(requestBuilder.build());
        return response;
    }

    /**
     * 签名构建
     * 拼接所有参数->md5加密->16进制
     *
     * @param parameters
     * @return
     */
    private String generateSignedParameters(String parameters) {
        Set<String> set = new TreeSet<>(Arrays.asList(parameters.split("&")));
        String sign = "";
        for (String string : set) {
            sign += "&" + string;
        }
        sign = sign.replaceFirst("&", "");
        //md5加密->16进制
        return ByteString.encodeUtf8(sign).md5().hex();
    }

    /**
     * request 转 string
     *
     * @param request
     * @return
     * @throws IOException
     */
    private String convertString(RequestBody request) throws IOException {
        Buffer buffer = new Buffer();
        request.writeTo(buffer);
        return buffer.readUtf8();
    }
}
