package com.example.life.efficiency.client;

import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;

public class LifeEfficiencyClientConfiguration {

    private static OkHttpClient OK_HTTP_CLIENT_INSTANCE = null;
    private static LifeEfficiencyClient LIFE_EFFICIENCY_CLIENT_INSTANCE = null;
    private static final String HTTP_ENDPOINT =
            "https://k80y14r3ok.execute-api.eu-west-1.amazonaws.com/Prod/";


    public static OkHttpClient getOkHttpClientInstance() {
        if (OK_HTTP_CLIENT_INSTANCE == null) {
            OK_HTTP_CLIENT_INSTANCE = new OkHttpClient();
        }
        return OK_HTTP_CLIENT_INSTANCE;
    }

    public static LifeEfficiencyClient getLifeEfficiencyClientInstance()
            throws MalformedURLException {
        if (LIFE_EFFICIENCY_CLIENT_INSTANCE == null) {
            LIFE_EFFICIENCY_CLIENT_INSTANCE = new LifeEfficiencyClientHttp(new URL(HTTP_ENDPOINT),
                    getOkHttpClientInstance());
        }
        return LIFE_EFFICIENCY_CLIENT_INSTANCE;
    }

}
