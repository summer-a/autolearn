package com.autolearn.icve.utils;

import com.autolearn.icve.entity.field.UrlFields;
import com.xiaoleilu.hutool.http.HttpRequest;
import com.xiaoleilu.hutool.http.HttpResponse;
import com.xiaoleilu.hutool.http.HttpStatus;
import com.xiaoleilu.hutool.json.JSONObject;
import org.springframework.util.StringUtils;

import java.net.HttpCookie;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 胡江斌
 * @version 1.0
 * @title: HttpUtil
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/7 11:10
 */
public class HttpUtil {

    private static Map<String, List<String>> headers = new HashMap<>();

    static {
        headers.put("User-Agent", Arrays.asList("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.129 Safari/537.36"));
        headers.put("Accept", Arrays.asList("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"));
        headers.put("Accept-Encoding", Arrays.asList("gzip, deflate, br"));
        headers.put("Accept-Language", Arrays.asList("zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7"));
        headers.put("X-Requested-With" , Arrays.asList("XMLHttpRequest"));
        headers.put("Sec-Fetch-Dest", Arrays.asList("empty"));
        headers.put("Sec-Fetch-Mode", Arrays.asList("cors"));
        headers.put("Sec-Fetch-Site", Arrays.asList("same-origin"));
    }

    private HttpUtil() {
    }

    public static HttpResponse get(String url) {
        return get(url, null);
    }

    public static HttpResponse get(String url, String cookie) {
        HttpResponse response = HttpRequest.get(url)
                .header(headers)
                .cookie(cookie)
                .setFollowRedirects(false)
                .execute();
        return response;
    }

    public static HttpResponse post(String url) {
        return post(url, null, null);
    }

    public static HttpResponse post(String url, String cookie) {
        return post(url, cookie, null);
    }

    public static HttpResponse post(String url, Map<String, Object> formMap) {
        return post(url, null, formMap);
    }

    public static HttpResponse post(String url, String cookie, Map<String, Object> formMap) {
        HttpResponse response = HttpRequest.post(url)
                .header(headers)
                .cookie(cookie)
                .form(formMap)
                .setFollowRedirects(false)
                .execute();
        return response;
    }

    public static HttpResponse post(String url, String cookie, Map<String, Object> formMap, Map<String, List<String>> header) {
        Map<String, List<String>> newHeaders = new HashMap<>(headers);
        newHeaders.putAll(header);

        HttpResponse response = HttpRequest.post(url)
                .header(newHeaders)
                .cookie(cookie)
                .form(formMap)
                .setFollowRedirects(false)
                .execute();
        return response;
    }


    /**
     * 请求响应对象
     * @param url
     * @param cookie
     * @param param
     * @param classType
     * @param <T>
     * @return
     */
    public static <T> T postBean(String url, String cookie, Map<String, Object> param, Class<T> classType) {
        JSONObject jsonObject = postJson(url, cookie, param);
        return jsonObject.toBean(classType, true);
    }

    /**
     * 请求响应json
     * @param url
     * @param cookie
     * @param param
     * @return
     */
    public static JSONObject postJson(String url, String cookie, Map<String, Object> param) {
        HttpResponse response = HttpUtil.post(url, cookie, param);
        String body = response.body();
        if (response.getStatus() == HttpStatus.HTTP_OK  && !StringUtils.isEmpty(body)) {
            return new JSONObject(body);
        }
        return new JSONObject();
    }
}
