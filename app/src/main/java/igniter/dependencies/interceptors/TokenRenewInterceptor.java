package igniter.dependencies.interceptors;

/**
 * @package com.trioangle.igniter
 * @subpackage dependencies.interceptors
 * @category AuthTokenInterceptor
 * @author Trioangle Product Team
 * @version 1.0
 **/


import androidx.annotation.Nullable;

import java.io.IOException;

import igniter.configs.SessionManager;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/*****************************************************************
 Auth Token Interceptor
 ****************************************************************/
public class TokenRenewInterceptor implements Authenticator {
    private SessionManager sessionManager;

    public TokenRenewInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Nullable
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        System.out.println("Authenticating for response: " + response);
        System.out.println("Authenticating for Token: " + sessionManager.getToken());
        System.out.println("Challenges: " + response.challenges());
        System.out.println("Success: " + response.isSuccessful());
        System.out.println("Body: " + response.body().string());
        System.out.println("Body: " + response.request().header("Authorization"));

        if (response.request().header("Authorization") != null) {
            return null; // Give up, we've already attempted to authenticate.
        }


        String credential = sessionManager.getToken();

        /*if (credential.equals(response.request().header("Authorization"))) {
            return null; // If we already failed with these credentials, don't retry.
        }*/
        if (responseCount(response) >= 3) {
            return null; // If we've failed 3 times, give up.
        }

        return response.request().newBuilder()
                .header("Authorization", credential)
                .build();
        //return null;
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
   /* @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        // if 'x-auth-token' is available into the response header
        // save the new token into session.The header key can be
        // different upon implementation of backend.
        LogManager.e("Check Siva "+response);
        LogManager.e("Check Siva "+response.body().string());
        LogManager.e("Check Siva "+response.message());
        LogManager.e("Check Siva "+response.isSuccessful());

        String newToken = response.header("refresh_token");
        if (newToken != null) {
            sessionManager.setToken(newToken);
        }

        return response;
    }*/
}
