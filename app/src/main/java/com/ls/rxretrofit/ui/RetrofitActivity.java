package com.ls.rxretrofit.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ls.rxretrofit.R;
import com.ls.rxretrofit.api.RetrofitApi;
import com.ls.rxretrofit.app.Constants;
import com.ls.rxretrofit.databinding.ActivityRetrofitBinding;
import com.ls.rxretrofit.utils.FileUtils;
import com.ls.rxretrofit.vo.JokeVo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * retrofit原始用法
 * Created by liusong on 2018/1/2.
 */

public class RetrofitActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityRetrofitBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_retrofit);
    }

    @Override
    public void onClick(View v) {
        mBinding.tvResult.setText("结果展示");
        switch (v.getId()) {
            case R.id.btn_get:
                useGetOrPost();
                break;
            default:
                break;
        }
    }

    /**
     * get请求
     * 同步和异步
     */
    public void useGetOrPost() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                //使用CertificatePinner限制哪个认证中心被信任
//                .certificatePinner(new CertificatePinner.Builder()
//                        .add("cer12306.com", "sha1/DmxUShsZuNiqPQsX2Oi9uv2sCnw=")
//                        .add("cer12306.com", "sha1/SXxoaOSEzPC6BgGmxAt/EAcsajw=")
//                        .build()
//                )
//                .addInterceptor(new RequestInterceptor()) //自定义请求拦截，加请求头，加密处理等操作
//                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        //----------------------------------------------------------------------
        //【https】
        //设置可访问所有的https网站
//        HttpsUtils.SslParams sslParams = HttpsUtils.initSslParams(null, null, null);
        //设置具体的证书（res/raw目录中）
//        InputStream cerIs = getResources().openRawResource(R.raw.cer12306);
//        try {
//            //assets目录中
//            InputStream cerIs = getResources().getAssets().open("cer12306.cer");
//            HttpsUtils.SslParams sslParams = HttpsUtils.initSslParams(new InputStream[]{cerIs}, null, null);
////            okHttpClient.sslSocketFactory(sslParams.sSLSocketFactory); //废弃
//            okHttpClient.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //----------------------------------------------------------------------
        //【retrofit】
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test")
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        //-------------------------------------------------
//        call.cancel(); //取消请求
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //【Get请求】
        //一般的get请求
        //异步
//        getJoke(retrofitApi);
        //-------------------------------------------------
        //同步(同步会造成线程阻塞，避免使用同步，以下展示为了得到结果用了线程)
//        getJokeStringSync(retrofitApi);
        //-------------------------------------------------
        //@Url注解，调用的时候再填充url
//        getJokeUseUrl(retrofitApi);
        //-------------------------------------------------
        //get请求，参数多时使用map
//        getJokeUseMap(retrofitApi);
        //-------------------------------------------------
        //get请求一个参数键，对应...paramType多个值
//        getJokeUseMultiValue(retrofitApi);
        //get请求，path中使用变量占位符
//        getJokeUseVar(retrofitApi);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //【Post请求】
        //一般的post请求
        postJoke(retrofitApi);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //【Https】
//        getHttps(retrofitApi);
    }

    /**
     * https请求
     *
     * @param retrofitApi
     */
    private void getHttps(RetrofitApi retrofitApi) {
        Call<ResponseBody> call = retrofitApi.getHttps();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(RetrofitActivity.this, "成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RetrofitActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //【Get请求】~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * 一般的get请求
     *
     * @param retrofitApi
     */
    private void getJoke(RetrofitApi retrofitApi) {
        Call<JokeVo> call = retrofitApi.getJoke(Constants.JUHE_APPKEY_JOKE);
        call.enqueue(new Callback<JokeVo>() {
            @Override
            public void onResponse(Call<JokeVo> call, Response<JokeVo> response) {
                if (response.isSuccessful()) {
                    List<JokeVo.ResultBean.DataBean> data = response.body().getResult().getData();
                    if (data.size() > 0) {
                        String body = data.get(0).getContent();
                        mBinding.tvResult.setText("一般的get请求 \n" + body);
                    }
                }
            }

            @Override
            public void onFailure(Call<JokeVo> call, Throwable t) {
                Toast.makeText(RetrofitActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * get请求获取string result,同步执行
     * 同步(同步会造成线程阻塞，避免使用同步，以下展示为了得到结果用了线程)
     *
     * @param retrofitApi
     */
    private void getJokeStringSync(RetrofitApi retrofitApi) {
        final Call<ResponseBody> call = retrofitApi.getJokeString(Constants.JUHE_APPKEY_JOKE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<ResponseBody> response = call.execute();
                    final String result = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBinding.tvResult.setText("get请求，同步 \n" + result);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(RetrofitActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    /**
     * 使用Url注解，调用的时候再填充url
     *
     * @param retrofitApi
     */
    private void getJokeUseUrl(RetrofitApi retrofitApi) {
        Call<JokeVo> call = retrofitApi.getJokeUseUrl("http://japi.juhe.cn/joke/content/text.from", Constants.JUHE_APPKEY_JOKE);
        call.enqueue(new Callback<JokeVo>() {
            @Override
            public void onResponse(Call<JokeVo> call, Response<JokeVo> response) {
                if (response.isSuccessful()) {
                    List<JokeVo.ResultBean.DataBean> data = response.body().getResult().getData();
                    if (data.size() > 0) {
                        String body = data.get(0).getContent();
                        mBinding.tvResult.setText("@Url注解，调用的时候再填充url \n" + body);
                    }
                }
            }

            @Override
            public void onFailure(Call<JokeVo> call, Throwable t) {
                Toast.makeText(RetrofitActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * get请求，参数多时使用map
     *
     * @param retrofitApi
     */
    private void getJokeUseMap(RetrofitApi retrofitApi) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", Constants.JUHE_APPKEY_JOKE);
        Call<JokeVo> call = retrofitApi.getJokeUseMap(params);
        call.enqueue(new Callback<JokeVo>() {
            @Override
            public void onResponse(Call<JokeVo> call, Response<JokeVo> response) {
                if (response.isSuccessful()) {
                    List<JokeVo.ResultBean.DataBean> data = response.body().getResult().getData();
                    if (data.size() > 0) {
                        String body = data.get(0).getContent();
                        mBinding.tvResult.setText("get请求，参数多时使用map \n" + body);
                    }
                }
            }

            @Override
            public void onFailure(Call<JokeVo> call, Throwable t) {
                Toast.makeText(RetrofitActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * get请求一个参数键，对应...paramType多个值
     *
     * @param retrofitApi
     */
    private void getJokeUseMultiValue(RetrofitApi retrofitApi) {
        Call<JokeVo> call = retrofitApi.getJokeUseMultiValue(Constants.JUHE_APPKEY_JOKE);
        call.enqueue(new Callback<JokeVo>() {
            @Override
            public void onResponse(Call<JokeVo> call, Response<JokeVo> response) {
                if (response.isSuccessful()) {
                    List<JokeVo.ResultBean.DataBean> data = response.body().getResult().getData();
                    if (data.size() > 0) {
                        String body = data.get(0).getContent();
                        mBinding.tvResult.setText("get请求一个参数键，对应...paramType多个值 \n" + body);
                    }
                }
            }

            @Override
            public void onFailure(Call<JokeVo> call, Throwable t) {
                Toast.makeText(RetrofitActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * get请求，path中使用变量占位符
     *
     * @param retrofitApi
     */
    private void getJokeUseVar(RetrofitApi retrofitApi) {
        Call<JokeVo> call = retrofitApi.getJokeUseVar("text.from", Constants.JUHE_APPKEY_JOKE);
        call.enqueue(new Callback<JokeVo>() {
            @Override
            public void onResponse(Call<JokeVo> call, Response<JokeVo> response) {
                if (response.isSuccessful()) {
                    List<JokeVo.ResultBean.DataBean> data = response.body().getResult().getData();
                    if (data.size() > 0) {
                        String body = data.get(0).getContent();
                        mBinding.tvResult.setText("get请求，path中使用变量占位符 \n" + body);
                    }
                }
            }

            @Override
            public void onFailure(Call<JokeVo> call, Throwable t) {
                Toast.makeText(RetrofitActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //【Post请求】~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * 一般的post请求
     *
     * @param retrofitApi
     */
    private void postJoke(RetrofitApi retrofitApi) {
        Call<JokeVo> call = retrofitApi.postJoke(Constants.JUHE_APPKEY_JOKE);
        call.enqueue(new Callback<JokeVo>() {
            @Override
            public void onResponse(Call<JokeVo> call, Response<JokeVo> response) {
                if (response.isSuccessful()) {
                    List<JokeVo.ResultBean.DataBean> data = response.body().getResult().getData();
                    if (data.size() > 0) {
                        String body = data.get(0).getContent();
                        mBinding.tvResult.setText("一般的post请求 \n" + body);
                    }
                }
            }

            @Override
            public void onFailure(Call<JokeVo> call, Throwable t) {
                Toast.makeText(RetrofitActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //【文件相关】~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * 单图、文上传方法1
     *
     * @param retrofitApi
     */
    private void uploadTextImage(RetrofitApi retrofitApi) {
        String fileUrl = "xxxx"; //文件路径集合
        File image = new File(fileUrl);
        //文件参数
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        //文字
        requestBody.addFormDataPart("nickName", "testName");
        //图片（参数名，文件名，文件）
        requestBody.addFormDataPart("avatarImgFile", image.getName(), RequestBody.create(MediaType.parse("image/*"), image));
        MultipartBody params = requestBody.build();

        Call<ResponseBody> call = retrofitApi.uploadImages(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * 多图、文上传方法1
     *
     * @param retrofitApi
     */
    private void uploadImages(RetrofitApi retrofitApi) {
        List<String> filePaths = new ArrayList<>(); //文件路径集合
        //文件参数
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        //文字
        requestBody.addFormDataPart("nickName", "testName");
        for (int i = 0; i < filePaths.size(); i++) {
            //参数名，文件名，文件
            requestBody.addFormDataPart(
                    "files",
                    new File(filePaths.get(i)).getName(),
                    RequestBody.create(MediaType.parse("image/*"), new File(filePaths.get(i)))
            );
        }
        MultipartBody params = requestBody.build();

        Call<ResponseBody> call = retrofitApi.uploadImages(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * 单图、文上传方法2
     * multipart:单图、文上传
     *
     * @param retrofitApi
     */
    private void postTextImage(RetrofitApi retrofitApi) {
        String fileUrl = "xxxx"; //文件路径集合
        File image = new File(fileUrl);

//        RequestBody textBody = RequestBody.create(null, "testName");
        RequestBody textBody = RequestBody.create(MediaType.parse("text*"), "testName");
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), image);
        MultipartBody.Part picturePart = MultipartBody.Part.createFormData("picture", image.getName(), fileBody);

        Call<ResponseBody> call = retrofitApi.postTextImage(textBody, picturePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * 多图、文上传方法2
     * multipart:多图、文上传
     *
     * @param retrofitApi
     */
    private void postTextImages(RetrofitApi retrofitApi) {
        List<String> filePaths = new ArrayList<>(); //文件路径集合
        RequestBody textBody = RequestBody.create(MediaType.parse("text*"), "testName");
        Map<String, RequestBody> images = new HashMap<>();
        for (int i = 0; i < filePaths.size(); i++) {
            String path = filePaths.get(i);
            File image = new File(path);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), image);
            //对应：Content-Disposition: form-data; name="files"; filename="cer12306.png",files为参数名；
            images.put("files\"; filename=\"" + image.getName(), fileBody);
        }

        Call<ResponseBody> call = retrofitApi.postTextImages(textBody, images);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * 保存图片到本地（获取文件流保存）
     *
     * @param retrofitApi
     */
    private void downloadImage(RetrofitApi retrofitApi) {
        Call<ResponseBody> call = retrofitApi.downloadImage("test");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String imagePath = FileUtils.saveImageStream2Local(RetrofitActivity.this, response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


}
