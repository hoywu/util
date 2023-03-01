package com.devccv.util.network;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class SimpleHttps {
    /**
     * 发送GET请求，仅返回响应body
     *
     * @param url 请求地址
     */
    public static RequestResult GET(String url) {
        return send(HTTP_METHOD.GET, url, null, null, false, true);
    }

    /**
     * 发送GET请求，使用指定请求头，仅返回响应body
     *
     * @param url             请求地址
     * @param requestProperty HTTP请求头
     */
    public static RequestResult GET(String url, Map<String, String> requestProperty) {
        return send(HTTP_METHOD.GET, url, requestProperty, null, false, true);
    }

    /**
     * 发送GET请求，指定是否需要响应body
     *
     * @param url          请求地址
     * @param needResponse 是否需要返回响应body
     */
    public static RequestResult GET(String url, boolean needResponse) {
        return send(HTTP_METHOD.GET, url, null, null, false, needResponse);
    }

    /**
     * 发送GET请求，指定是否需要响应body和响应头
     *
     * @param url              请求地址
     * @param needResponse     是否需要返回响应body
     * @param needHeaderFields 是否需要返回响应头
     */
    public static RequestResult GET(String url, boolean needResponse, boolean needHeaderFields) {
        return send(HTTP_METHOD.GET, url, null, null, needHeaderFields, needResponse);
    }

    /**
     * 发送GET请求，使用指定请求头，指定是否需要响应body和响应头
     *
     * @param url              请求地址
     * @param requestProperty  HTTP请求头
     * @param needResponse     是否需要返回响应body
     * @param needHeaderFields 是否需要返回响应头
     */
    public static RequestResult GET(String url, Map<String, String> requestProperty, boolean needResponse, boolean needHeaderFields) {
        return send(HTTP_METHOD.GET, url, requestProperty, null, needHeaderFields, needResponse);
    }

    /**
     * 发送POST请求，无请求主体，仅返回响应body
     *
     * @param url 请求地址
     */
    public static RequestResult POST(String url) {
        return send(HTTP_METHOD.POST, url, null, null, false, true);
    }

    /**
     * 发送POST请求，使用指定请求头，无请求主体，仅返回响应body
     *
     * @param url             请求地址
     * @param requestProperty HTTP请求头
     */
    public static RequestResult POST(String url, Map<String, String> requestProperty) {
        return send(HTTP_METHOD.POST, url, requestProperty, null, false, true);
    }

    /**
     * 发送POST请求，无请求主体，指定是否需要响应body
     *
     * @param url          请求地址
     * @param needResponse 是否需要返回响应body
     */
    public static RequestResult POST(String url, boolean needResponse) {
        return send(HTTP_METHOD.POST, url, null, null, false, needResponse);
    }

    /**
     * 发送POST请求，无请求主体，指定是否需要响应body和响应头
     *
     * @param url              请求地址
     * @param needResponse     是否需要返回响应body
     * @param needHeaderFields 是否需要返回响应头
     */
    public static RequestResult POST(String url, boolean needResponse, boolean needHeaderFields) {
        return send(HTTP_METHOD.POST, url, null, null, needHeaderFields, needResponse);
    }

    /**
     * 发送POST请求，带请求主体，仅返回响应body
     *
     * @param url      请求地址
     * @param postData POST body
     */
    public static RequestResult POST(String url, byte[] postData) {
        return send(HTTP_METHOD.POST, url, null, postData, false, true);
    }

    /**
     * 发送POST请求，带请求主体，指定是否需要响应body和响应头
     *
     * @param url              请求地址
     * @param postData         POST body
     * @param needResponse     是否需要返回响应body
     * @param needHeaderFields 是否需要返回响应头
     */
    public static RequestResult POST(String url, byte[] postData, boolean needResponse, boolean needHeaderFields) {
        return send(HTTP_METHOD.POST, url, null, postData, needHeaderFields, needResponse);
    }

    /**
     * 发送POST请求，使用指定请求头，带请求主体，仅返回响应body
     *
     * @param url             请求地址
     * @param requestProperty HTTP请求头
     * @param postData        POST body
     */
    public static RequestResult POST(String url, Map<String, String> requestProperty, byte[] postData) {
        return send(HTTP_METHOD.POST, url, requestProperty, postData, false, true);
    }

    /**
     * 发送POST请求，使用指定请求头，带请求主体，指定是否需要响应body和响应头
     *
     * @param url              请求地址
     * @param requestProperty  HTTP请求头
     * @param postData         POST body
     * @param needResponse     是否需要返回响应body
     * @param needHeaderFields 是否需要返回响应头
     */
    public static RequestResult POST(String url, Map<String, String> requestProperty, byte[] postData, boolean needResponse, boolean needHeaderFields) {
        return send(HTTP_METHOD.POST, url, requestProperty, postData, needHeaderFields, needResponse);
    }

    private static RequestResult send(HTTP_METHOD method, String url, Map<String, String> requestProperty, byte[] postData, boolean needHeaderFields, boolean needResponse) {
        try {
            HttpsURLConnection httpsURLConnection = getHttpsURLConnection(method, url, requestProperty);

            if (method == HTTP_METHOD.POST && postData != null) {
                try (OutputStream outputStream = httpsURLConnection.getOutputStream()) {
                    //getOutputStream() 和 getInputStream() 隐式调用 connect()
                    outputStream.write(postData);
                    outputStream.flush();
                }
            }

            Map<String, List<String>> headerFields = null;
            if (needHeaderFields) {
                headerFields = httpsURLConnection.getHeaderFields();
            }

            StringBuilder rawData = null;
            if (needResponse) {
                rawData = new StringBuilder();
                try (InputStream inputStream = httpsURLConnection.getInputStream();
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String data;
                    while ((data = bufferedReader.readLine()) != null) {
                        rawData.append(data).append('\n');
                    }
                }
            }
            String rawDataString = rawData != null ? rawData.toString() : null;

            return new RequestResult(rawDataString, headerFields);
        } catch (IOException e) {
            return new RequestResult(e);
        }
    }

    private static HttpsURLConnection getHttpsURLConnection(HTTP_METHOD method, String urlString, Map<String, String> requestProperty) throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        httpsURLConnection.setConnectTimeout(5000);
        httpsURLConnection.setReadTimeout(5000);
        if (method == HTTP_METHOD.POST) {
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setDoOutput(true);
        }
        //默认请求属性，如传入新值会覆盖
        httpsURLConnection.setRequestProperty("Connection", "Keep-Alive");
        //Keep-Alive时关闭输入流不会disconnect()连接
        //https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/HttpURLConnection.html
        //每个HttpURLConnection实例用于发出单个请求，但是与HTTP服务器的基础网络连接可以由其他实例透明地共享。
        //在请求之后调用HttpURLConnection的InputStream或OutputStream上的close()方法可以释放与此实例关联的网络资源，
        // 但不会影响任何共享持久连接。
        //如果此时持久连接处于空闲状态，则调用disconnect()方法可能会关闭底层套接字。
        httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.50");
        if (requestProperty != null) {
            for (Map.Entry<String, String> entry : requestProperty.entrySet()) {
                httpsURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return httpsURLConnection;
    }
}
