package com.leon.patmore.life.efficiency.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class LifeEfficiencyClientConfiguration {

    private static OkHttpClient OK_HTTP_CLIENT_INSTANCE = null;
    private static LifeEfficiencyClient LIFE_EFFICIENCY_CLIENT_INSTANCE = null;
    private static final String HTTP_ENDPOINT =
            "https://k80y14r3ok.execute-api.eu-west-1.amazonaws.com/Prod/";


    public static OkHttpClient getOkHttpClientInstance() {
        if (OK_HTTP_CLIENT_INSTANCE == null) {
            OK_HTTP_CLIENT_INSTANCE = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
        }
        return OK_HTTP_CLIENT_INSTANCE;
    }

    public static LifeEfficiencyClient getLifeEfficiencyClientInstance() {
        if (LIFE_EFFICIENCY_CLIENT_INSTANCE == null) {
            try {
                LIFE_EFFICIENCY_CLIENT_INSTANCE = new LifeEfficiencyClientHttp(new URL(HTTP_ENDPOINT),
                        getOkHttpClientInstance());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return LIFE_EFFICIENCY_CLIENT_INSTANCE;
    }

}
