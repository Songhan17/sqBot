package com.han.setu;

import com.google.gson.Gson;
import com.han.model.ImgData;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Thread extends java.lang.Thread {
    GroupMessageEvent e;
    List<Long> perm;

    public void newThread(GroupMessageEvent event, List<Long> perm) {
        this.e = event;
        this.perm = perm;
        this.start();
    }

    @Override
    public void run() {
        String msgContent = e.getMessage().contentToString().toLowerCase().replace("来点", "")
                .replace("涩图", "");
        boolean isR18 = false;
        if (perm.contains(e.getSender().getId()) && msgContent.contains("r18")) {
            isR18 = true;
        }
        msgContent = msgContent.replace("r18", "");
        String content = "";
        try {
            content = Connection.getURL(msgContent, isR18);
        } catch (Exception exception) {
            e.getGroup().sendMessage("服务器宕机了惹");
        }
        if (content.equals("")) {
            e.getGroup().sendMessage("xp太怪，搜不到图了惹");
            return;
        }

        Gson gson = new Gson();
        ImgData imgData = gson.fromJson(content, ImgData.class);

        if (!imgData.getError().equals("")){
            e.getGroup().sendMessage("遇到错误了惹: " + imgData.getError());
        }else {
            try {
                InputStream inputStream = httpRequest(imgData.getData().get(0).getUrls().getOriginal().replace("i.pixiv.cat", "i.pixiv.re"));
                Image image;
                image = ExternalResource.uploadAsImage(inputStream, e.getGroup());
                e.getGroup().sendMessage(image);
            } catch (Exception ex) {
                e.getGroup().sendMessage("遇到错误了惹");
            }
        }
    }

    private static InputStream httpRequest(String uri) throws Exception {
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("referer", ""); //这是破解防盗链添加的参数
        conn.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.67");
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);

        return conn.getInputStream();
    }

}