package com.han.setu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Connection {
    public static String getURL(String keyword, Boolean isR18) throws Exception {
        URL url;
        String address = "https://api.lolicon.app/setu/v2";
        if (isR18) {
            address += "?r18=1";
        } else {
            address += "?r18=0";
        }
        if (!keyword.equals("")) {
            String newFileName = URLEncoder.encode(keyword, "utf-8");
            address += "&keyword=" + newFileName;
        }
        url = new URL(address);
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
        httpURLConnection.setRequestProperty("referer", ""); //这是破解防盗链添加的参数
        httpURLConnection.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +
                " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.67");
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setConnectTimeout(5 * 1000);
        if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return "";
        }
        InputStream inputStream = httpURLConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String reading, content = "";
        while ((reading = bufferedReader.readLine()) != null) {
            content = content.concat(reading);
        }
        content = content.replaceFirst("\n", "");
        inputStream.close();
        inputStreamReader.close();
        bufferedReader.close();
        return content;
    }
}
