package igniter.dependencies.module;
/**
 * @package com.trioangle.igniter
 * @subpackage dependencies.module
 * @category NetworkModule
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import igniter.configs.SessionManager;
import igniter.dependencies.interceptors.AuthTokenInterceptor;
import igniter.dependencies.interceptors.NetworkInterceptor;
import igniter.interfaces.ApiService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*****************************************************************
 NetWork Module
 ****************************************************************/
@Module
public class NetworkModule {
    private String mBaseUrl;

    @Inject
    public NetworkModule(String baseUrl) {
        this.mBaseUrl = baseUrl;
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor providesHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return loggingInterceptor;
    }

    @Provides
    @Singleton
    Gson providesGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    OkHttpClient.Builder providesOkHttpClient(Context context, HttpLoggingInterceptor httpLoggingInterceptor, SessionManager sessionManager) {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES);
        client.addInterceptor(httpLoggingInterceptor);
        client.addInterceptor(new NetworkInterceptor(context));
        client.addInterceptor(new AuthTokenInterceptor(sessionManager));
        //  client.authenticator(new TokenRenewInterceptor(sessionManager));

        return client;
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofitService(OkHttpClient.Builder okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient.build())
                .build();
    }

    @Provides
    @Singleton
    public ApiService providesApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
