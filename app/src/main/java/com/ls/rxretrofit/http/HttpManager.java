package com.ls.rxretrofit.http;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.RxFragment;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.ref.WeakReference;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liusong on 2018/1/5.
 */

public class HttpManager {
    private volatile static HttpManager INSTANCE;
    private static Retrofit retrofit;
    //是否有必要缓存apiService???
    private static final Map<Class<?>, Object> SERVICE_MAP = new ArrayMap<>();

    //构造方法私有
    private HttpManager() {
    }

    //获取单例
    public static HttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpManager();
                    initNetWork();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化网络配置
     */
    private static void initNetWork() {
        //cookie管理
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        //okHttpBuilder
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
//                .readTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(60,TimeUnit.SECONDS)
//                .connectTimeout(10,TimeUnit.SECONDS); //超时设置
//                .addNetworkInterceptor(new StethoInterceptor());//chrome中http请求拦截
//                .addInterceptor(new RequestInterceptor()) //自定义请求拦截，加请求头，加密处理等操作
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)) //请求http请求信息log
                .cookieJar(new JavaNetCookieJar(cookieManager)); //自动管理cookie
        //----------------------------------------------------------------------
        //使用CertificatePinner限制哪个认证中心被信任
//        okHttpBuilder.certificatePinner(new CertificatePinner.Builder()
//                .add("cer12306.com", "sha1/DmxUShsZuNiqPQsX2Oi9uv2sCnw=")
//                .add("cer12306.com", "sha1/SXxoaOSEzPC6BgGmxAt/EAcsajw=")
//                .build()
//        );
        //----------------------------------------------------------------------
        //【https】
//        HttpsUtils.SslParams sslParams = HttpsUtils.initSslParams(null, null, null); //设置可访问所有的https网站
//        InputStream cerIs = App.getApp().getResources().openRawResource(R.raw.cer12306); //设置具体的证书（res/raw目录中）
//        try {
//            //设置具体的证书（assets目录中）
//            InputStream cerIs = App.getApp().getResources().getAssets().open("cer12306.cer");
//            HttpsUtils.SslParams sslParams = HttpsUtils.initSslParams(new InputStream[]{cerIs}, null, null);
//            //okHttpBuilder.sslSocketFactory(sslParams.sSLSocketFactory); //废弃
//            okHttpBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //----------------------------------------------------------------------
        retrofit = new Retrofit.Builder()
                .baseUrl("https://test")
                .client(okHttpBuilder.build())
//                .addConverterFactory(ScalarsConverterFactory.create()) //返回数据为String;
                .addConverterFactory(GsonConverterFactory.create()) //返回数据使用Gson解析;
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())) //发起请求在io线程
                .build();
    }

    /**
     * 创建接口api
     *
     * @param service
     * @param <T>
     * @return
     */
    public <T> T create(@NonNull Class<T> service) {
        Object o = SERVICE_MAP.get(service);
        if (o != null) {
            return (T) o;
        } else {
            T t = retrofit.create(service);
            SERVICE_MAP.put(service, t);
            return t;
        }
    }

    /**
     * Http请求的Observable处理
     * 1> 处理线程调度；
     * 2> 生命周期与组件生命周期绑定；
     *
     * @return
     */
    public static <T> ObservableTransformer<T, T> handleObservable(final Object component) {
        WeakReference<Object> srComponent = new WeakReference<>(component);
        LifecycleTransformer<T> lifecycleTransformer;
        if (component instanceof RxAppCompatActivity) {
//            lifecycleTransformer = ((RxAppCompatActivity) srComponent.get()).bindUntilEvent(ActivityEvent.PAUSE);
            lifecycleTransformer = ((RxAppCompatActivity) srComponent.get()).bindToLifecycle();
        } else if (component instanceof RxFragment) {
//            lifecycleTransformer = ((RxFragment) srComponent.get()).bindUntilEvent(FragmentEvent.PAUSE);
            lifecycleTransformer = ((RxFragment) srComponent.get()).bindToLifecycle();
        } else {
            throw new NullPointerException("lifecycleTransformer is not null");
        }
        final LifecycleTransformer<T> bindLifecycle = lifecycleTransformer;
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> observable) {
                return observable.compose(bindLifecycle)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io());
            }
        };
    }
}
