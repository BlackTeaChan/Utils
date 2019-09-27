package com.zjars.hmcs.utils;

import okhttp3.*;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp 工具类
 * get的同步异步请求
 * post的json字符串同步异步上传
 * post的键值对同步异步上传
 * post文件异步上传，回调结果以及进度
 * @author blackteachan
 * @date 2019-09-27 13:16
 */
public class OkHttpUtil {

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("Content-Type: binary/octet-stream");

    private OkHttpClient mOkHttpClient;

    private OkHttpUtil() {
        this.mOkHttpClient = getUnsafeOkHttpClient();
    }

    public static final OkHttpUtil getInstance() {
        return SingleFactory.manger;
    }

    private static final class SingleFactory {
        private static final OkHttpUtil manger = new OkHttpUtil();
    }

    /**
     * 同步get请求
     *
     * @param url 地址
     * @return Response 返回数据
     */
    public Response getSync(final String url) throws IOException {
        final Request request = new Request.Builder().url(url).build();
        final Call call = mOkHttpClient.newCall(request);
        return call.execute();
    }

    /**
     * post同步请求，提交Json数据
     *
     * @param url  地址
     * @param json json格式的字符串
     * @return Response
     */
    public Response postSyncJson(String url, String json) throws IOException {
        final RequestBody requestBody = RequestBody.create(JSON_TYPE, json);
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的文件上传
     *
     * @param url     地址
     * file    提交的文件
     * @param fileKey 提交的文件key
     * @return Response
     * @throws IOException
     */
    public Response uploadSync(String url, String filepath, String fileKey) throws IOException {
        File file = new File(filepath);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        return response;
    }

    /**
     * 同步基于post的文件上传
     *
     * @param url     地址
     * file    提交的文件
     * @param bytes
     * @return Response
     * @throws IOException
     */
    public Response uploadImage(String url, byte[] bytes) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN,bytes))
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        return response;
    }

    /**
     * 同步基于post的文件下载
     * @param fileUrl 下载地址
     * @return Response
     * @throws IOException
     */
    public Response downLoadFile(String fileUrl) throws IOException{
        Request request = new Request.Builder()
                .url(fileUrl)
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        return response;
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                            boolean test = UrlConstants.isDebug;
//                            if (test){
//                                return;
//                            }
                            if (chain == null) {
                                throw new IllegalArgumentException("checkServerTrusted: X509Certificate array is null");
                            }
                            if (!(chain.length > 0)) {
                                throw new IllegalArgumentException("checkServerTrusted: X509Certificate is empty");
                            }
                            try {
                                TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                                tmf.init((KeyStore) null);
                                for (TrustManager trustManager : tmf.getTrustManagers()) {
                                    ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
                                }
                            } catch (Exception e) {
                                throw new CertificateException(e);
                            }
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            //设置避免被第三方使用代理抓包
            builder.proxy(Proxy.NO_PROXY);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.connectTimeout(60, TimeUnit.SECONDS).
                    writeTimeout(30, TimeUnit.SECONDS).
                    readTimeout(60, TimeUnit.SECONDS).
                    //设置失败重试请求为不可用
                            retryOnConnectionFailure(false).
                            build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
