package ru.alta.svd.client.core.util;

public class SvdProtocolUtil {
    public static String buildUrl(String url){
        if (url.startsWith("http://")){
            return url.replaceFirst("http:\\/\\/", "http4://");
        }
        if (url.startsWith("https://")){
            return url.replaceFirst("https:\\/\\/", "https4://");
        }
        return "http4://" + url;
    }
}
