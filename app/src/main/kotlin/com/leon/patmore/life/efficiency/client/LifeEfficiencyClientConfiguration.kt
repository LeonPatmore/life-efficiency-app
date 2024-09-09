package com.leon.patmore.life.efficiency.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

object LifeEfficiencyClientConfiguration {

    private var OK_HTTP_CLIENT_INSTANCE: OkHttpClient? = null
    private var LIFE_EFFICIENCY_CLIENT_INSTANCE: LifeEfficiencyClient? = null
    private const val HTTP_ENDPOINT = "https://k80y14r3ok.execute-api.eu-west-1.amazonaws.com/Prod/"

    private fun getOkHttpClientInstance(): OkHttpClient {
        if (OK_HTTP_CLIENT_INSTANCE == null) {
            OK_HTTP_CLIENT_INSTANCE = OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build()
        }
        return OK_HTTP_CLIENT_INSTANCE!!
    }

    fun getLifeEfficiencyClientInstance(): LifeEfficiencyClient {
        if (LIFE_EFFICIENCY_CLIENT_INSTANCE == null) {
            try {
                LIFE_EFFICIENCY_CLIENT_INSTANCE = LifeEfficiencyClientHttp(
                    URL(HTTP_ENDPOINT), getOkHttpClientInstance())
            } catch (e: MalformedURLException) {
                throw RuntimeException(e)
            }
        }
        return LIFE_EFFICIENCY_CLIENT_INSTANCE!!
    }

}
