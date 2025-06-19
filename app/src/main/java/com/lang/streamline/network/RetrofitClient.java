package com.lang.streamline.network;

import android.os.Build;
import android.util.Log;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static volatile RetrofitClient instance;
    private Retrofit retrofit;
    private String baseUrl = "";
    private OkHttpClient okHttpClient;
    private final Map<String, String> headers = new HashMap<>();
    private static final String TAG = "RetrofitClient";

    private static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "-----END PUBLIC KEY-----";

    private RetrofitClient() {
        init("https://biginfos.com/gatm")
                .addHeader("dev-id", "303cb34720813d7a")
                .addHeader("trace-id", UUID.randomUUID().toString());
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
            }
        }
        return instance;
    }

    public RetrofitClient init(String baseUrl) {
        if (baseUrl != null && !baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        this.baseUrl = baseUrl;
        return this;
    }

    public RetrofitClient addHeader(String name, String value) {
        headers.put(name, value);
        okHttpClient = null;
        retrofit = null;
        return this;
    }

    public <T> T createService(Class<T> serviceClass) {
        if (retrofit == null) {
            retrofit = createRetrofit();
        }
        return retrofit.create(serviceClass);
    }

    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = createDefaultOkHttpClient();
        }
        return okHttpClient;
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private OkHttpClient createDefaultOkHttpClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);

        if (!headers.isEmpty()) {
            builder.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();

                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder.header(entry.getKey(), entry.getValue());
                }

                requestBuilder.header("ts", System.currentTimeMillis() + "");
                requestBuilder.header("model", Build.MODEL);
                requestBuilder.header("brand", Build.BRAND);
                requestBuilder.header("os", String.valueOf(Build.VERSION.RELEASE));

                Request request = requestBuilder.build();
                return chain.proceed(request);
            });
        }

        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            // 传入 null 表示使用系统默认的 KeyStore（即 Android 系统内置的 root CA 列表）
            tmf.init((KeyStore) null);

            // 从 tmf 中找到真正的 X509TrustManager（通常只有一个）
            X509TrustManager defaultTm = null;
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    defaultTm = (X509TrustManager) tm;
                    break;
                }
            }
            X509TrustManager finalDefaultTm = defaultTm;
            X509TrustManager customTrustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    Log.i("CertUtils", String.valueOf(chain.length));
                    try {
                        finalDefaultTm.checkServerTrusted(chain, authType);
                    } catch (CertificateException e) {
                        throw new RuntimeException(e);
                    }

                    for (X509Certificate ch: chain) {
                        CertificateUtils.dumpCertificate(ch);
                    }
                    if (chain != null && chain.length > 0) {
                        X509Certificate serverCert = chain[0];

                        String serverPublicKeyB64;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            serverPublicKeyB64 = Base64.getEncoder().encodeToString(serverCert.getPublicKey().getEncoded());
                        } else {
                            serverPublicKeyB64 = android.util.Base64.encodeToString(serverCert.getPublicKey().getEncoded(), android.util.Base64.DEFAULT);
                        }

                        String expectedPublicKey = PUBLIC_KEY
                                .replace("-----BEGIN PUBLIC KEY-----", "")
                                .replace("-----END PUBLIC KEY-----", "")
                                .replaceAll("\\s+", "");

                        if (!serverPublicKeyB64.equals(expectedPublicKey)) {
                            Log.e(TAG, "checkServerTrusted error not equal");
                            throw new RuntimeException("checkServerTrusted error not equal");
                        }
                    }
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{customTrustManager}, new java.security.SecureRandom());

            HostnameVerifier allowAllHostnames =  (hostname, session) -> true ;

            builder.sslSocketFactory(sslContext.getSocketFactory(), customTrustManager)
                    .hostnameVerifier(allowAllHostnames);

        } catch (Exception e) {
            Log.e(TAG, "createDefaultOkHttpClient: " + e.getMessage(), e);
        }

        return builder.build();
    }

}