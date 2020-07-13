package igniter.dependencies.interceptors;
/**
 * @package com.trioangle.igniter
 * @subpackage dependencies.interceptors
 * @category AuthTokenInterceptor
 * @author Trioangle Product Team
 * @version 1.0
 **/

import java.io.IOException;

import igniter.configs.SessionManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/*****************************************************************
 Auth Token Interceptor
 ****************************************************************/
public class AuthTokenInterceptor implements Interceptor {
    private Request.Builder requestBuilder;
    private SessionManager sessionManager;

    public AuthTokenInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            Request original = chain.request();

            if (sessionManager.getToken() != null) {
                // Request customization: add request headers
                requestBuilder = original.newBuilder()
                        .header("Authorization", sessionManager.getToken())
                        .method(original.method(), original.body());
            } else {
                // Request customization: add request headers
                requestBuilder = original.newBuilder().method(original.method(), original.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
