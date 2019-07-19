package com.ab.hicarerun;

import android.app.Application;

import com.ab.hicarerun.database.realm.RealmString;
import com.ab.hicarerun.database.realm.RealmStringListTypeAdapter;
import com.ab.hicarerun.network.HeaderInterceptor;
import com.ab.hicarerun.network.IRetrofit;
import com.ab.hicarerun.network.RequestHeader;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.utils.PicassoImageLoadingService;
import com.ab.hicarerun.utils.notifications.OneSIgnalHelper;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ss.com.bannerslider.Slider;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends Application {

    private static volatile IRetrofit IRETROFIT = null;
    private static volatile Realm REALM = null;
    private OneSIgnalHelper mOneSignalHelper;


    public static synchronized Realm getRealm() {
        if (REALM != null) {
            return REALM;
        } else {
            RealmConfiguration realmConfig =
                    new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(realmConfig);
            REALM = Realm.getDefaultInstance();
            return REALM;
        }
    }

    public static synchronized IRetrofit getRetrofitAPI(boolean autohrised) {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        gsonBuilder.registerTypeAdapter(new TypeToken<RealmList<RealmString>>() {
        }.getType(), RealmStringListTypeAdapter.INSTANCE);

        Gson gson = gsonBuilder.create();

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS);


        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }
        if (autohrised) httpClientBuilder.addInterceptor(new HeaderInterceptor(getHeader()));

        IRETROFIT = new Retrofit.Builder().baseUrl(IRetrofit.BASE_URL)

                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(httpClientBuilder.build())
                .build()
                .create(IRetrofit.class);

        return IRETROFIT;
    }

    private static RequestHeader getHeader() {
        RequestHeader header = null;
        RealmResults<LoginResponse> query =
                BaseApplication.getRealm().where(LoginResponse.class).findAll();
        if (query != null && query.size() > 0) {
            header = new RequestHeader();
            header.setHeaderName("Authorization");
            header.setHeaderValue(query.get(0).getTokenType() + " " + query.get(0).getAccessToken());
        }
        return header;
    }

    public static IRetrofit getExotelApi() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        gsonBuilder.registerTypeAdapter(new TypeToken<RealmList<RealmString>>() {
        }.getType(), RealmStringListTypeAdapter.INSTANCE);

        Gson gson = gsonBuilder.create();

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }

        IRetrofit retrofit = new Retrofit.Builder().baseUrl(IRetrofit.EXOTEL_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(httpClientBuilder.build())
                .build()
                .create(IRetrofit.class);

        return retrofit;
    }

    public static IRetrofit getLoggerApi() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        gsonBuilder.registerTypeAdapter(new TypeToken<RealmList<RealmString>>() {
        }.getType(), RealmStringListTypeAdapter.INSTANCE);

        Gson gson = gsonBuilder.create();

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }

        IRetrofit retrofit = new Retrofit.Builder().baseUrl(IRetrofit.ERROR_LOG_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(httpClientBuilder.build())
                .build()
                .create(IRetrofit.class);

        return retrofit;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mOneSignalHelper = new OneSIgnalHelper(this);

        Slider.init(new PicassoImageLoadingService(this));
        // initialise the realm database
        try {
            Realm.init(this);
            RealmConfiguration realmConfiguration =
                    new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(realmConfiguration);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // configuring the font for calligraphy
        try {
            CalligraphyConfig.initDefault(
                    new CalligraphyConfig.Builder().setDefaultFontPath("fonts/font.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
