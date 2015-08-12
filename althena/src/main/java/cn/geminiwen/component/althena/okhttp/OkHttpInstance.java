package cn.geminiwen.component.althena.okhttp;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by geminiwen on 15/8/12.
 */
public class OkHttpInstance {
    private static final long DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000L; // 20s
    private static final long DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000L; // 20s
    private static final long DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000L; // 15s

    private static OkHttpClient sInstance;
    public synchronized static OkHttpClient getHttpClient() {
        if (sInstance == null) {
            sInstance = new OkHttpClient();
            sInstance.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            sInstance.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            sInstance.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        }
        return sInstance;
    }
}
