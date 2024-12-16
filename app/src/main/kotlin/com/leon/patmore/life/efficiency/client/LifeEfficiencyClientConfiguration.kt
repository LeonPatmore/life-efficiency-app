package com.leon.patmore.life.efficiency.client

import okhttp3.OkHttpClient
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.TimeUnit

object LifeEfficiencyClientConfiguration {
    private var okHttpClient: OkHttpClient? = null
    private var lifeEfficiencyClient: LifeEfficiencyClient? = null
    private const val HTTP_ENDPOINT = "https://k80y14r3ok.execute-api.eu-west-1.amazonaws.com/Prod/"

    private fun getOkHttpClientInstance(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient =
                OkHttpClient
                    .Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build()
        }
        return okHttpClient!!
    }

    fun getLifeEfficiencyClientInstance(): LifeEfficiencyClient {
        if (lifeEfficiencyClient == null) {
            try {
                lifeEfficiencyClient =
                    LifeEfficiencyClientHttp(
                        URL(HTTP_ENDPOINT),
                        getOkHttpClientInstance(),
                    )
            } catch (e: MalformedURLException) {
                throw RuntimeException(e)
            }
        }
        return lifeEfficiencyClient!!
    }
}
