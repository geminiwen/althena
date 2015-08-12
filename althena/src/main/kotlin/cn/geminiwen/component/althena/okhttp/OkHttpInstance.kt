package cn.geminiwen.component.althena.okhttp

import com.squareup.okhttp.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Created by geminiwen on 8/12/15.
 */
public object OkHttpInstance {

    private val DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000L // 20s
    private val DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000L // 20s
    private val DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000L // 15s
    private var okHttpClient: OkHttpClient? = null

    fun getHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient()
            okHttpClient?.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            okHttpClient?.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            okHttpClient?.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)

        }
        return okHttpClient!!
    }
}