package com.github.mjd507.util.http;

import com.github.mjd507.util.util.JsonUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created by mjd on 2020/5/16 12:39
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();

    private static OkHttpClient okClient = okBuilder.build();


//  public static void main(String args[]) {
//    HashMap<String, String> params = new HashMap<>();
//    params.put("a", "1");
//    params.put("b", "2");
//    HttpRequest request = new HttpRequest();
//    doRequest(request);
//  }

    public static String doRequest(HttpRequest request) {
        return doRequest(request, String.class);
    }

    public static <T> T doRequest(HttpRequest request, Class<T> clazz) {
        Request okRequest = buildOkRequest(request);
        return handleResponse(okRequest, clazz);
    }

    private static Request buildOkRequest(HttpRequest request) {
        String url = request.getUrl();
        String method = request.getMethod();
        Map<String, String> headers = request.getHeaders();
        Map<String, Object> params = request.getParams();

        Request.Builder requestBuilder = new Request.Builder();

        boolean isGet = method == null || method.equalsIgnoreCase(HttpMethod.GET);
        if (isGet) {
            url = buildGetUrl(url, params);
        }
        requestBuilder.url(url);
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }
        if (!isGet && params != null) { // 针对 post 请求
            String jsonStr = JsonUtil.toJsonStr(params);
            RequestBody body = RequestBody.create(JSON, jsonStr);
            requestBuilder.post(body);
        }
        return requestBuilder.build();
    }

    private static String buildGetUrl(String url, Map<String, Object> params) {
        if (params == null) {
            return url;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(url).append("?");
        params.forEach((key, value) -> {
            builder.append(key).append("=").append(value).append("&");
        });
        String strWithDirtyEnd = builder.toString();
        return strWithDirtyEnd.substring(0, strWithDirtyEnd.length() - 1);
    }

    private static <T> T handleResponse(Request request, Class<T> clazz) {
        try {
            Response response = okClient.newCall(request).execute();
            ResponseBody body = response.body();
            T t = null;
            if (body != null) {
                if (clazz == String.class) {
                    return (T) body.string();
                }
                t = JsonUtil.toObj(body.string(), clazz);
            }
            return t;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
