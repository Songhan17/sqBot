package com.han.setu;

import com.google.gson.Gson;
import com.han.main.JavaPluginMain;
import com.han.model.ImgData;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.han.main.JavaPluginMain.coolCount;

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
        if (e.getMessage().contentToString().contains("三次元")) {
            sendImage(e, "https://api.vvhan.com/api/girl", "333",
                String.valueOf(System.currentTimeMillis()), "png");
            return;
        }
        String msgContent = e.getMessage().contentToString().toLowerCase().replace("来点", "")
            .replace("涩图", "").replace("色图", "");
        boolean isR18 = false;
        if (perm.contains(e.getSender().getId()) && msgContent.contains("r18")) {
            isR18 = true;
        }
        msgContent = msgContent.replace("r18", "");
        String content = "";
        try {
            content = Connection.getURL(msgContent, isR18);
        } catch (Exception exception) {
//            e.getGroup().sendMessage("服务器宕机了惹");
            return;
        }
        if (content.equals("")) {
//            e.getGroup().sendMessage("xp太怪，搜不到图了惹");
            return;
        }

        Gson gson = new Gson();
        ImgData imgData = gson.fromJson(content, ImgData.class);
        if (imgData.getData().isEmpty()) {
//            e.getGroup().sendMessage("没有找到对应的结果呢，是不是xp太怪了惹");
            return;
        }
        if (!imgData.getError().equals("")) {
//            e.getGroup().sendMessage("遇到错误了惹: " + imgData.getError());
            return;
        } else {
            sendImage(e, imgData.getData().get(0).getUrls().getOriginal()
                    .replace("i.pixiv.cat", "i.pixiv.re"),
                "222", imgData.getData().get(0).getUid(),
                imgData.getData().get(0).getExt());
        }
    }

    private static void sendImage(GroupMessageEvent e, String url, String dir, String uid, String ext) {
        try {
            FileInputStream is = new FileInputStream(httpRequest(url, dir, uid, ext));
            Image image;
            image = ExternalResource.uploadAsImage(is, e.getGroup());
            e.getGroup().sendMessage(image);
            coolCount.put(e.getGroup().getId(), coolCount.get(e.getGroup().getId()) + 1);
        } catch (Exception ignored) {
        }
    }

    private static String httpRequest(String uri, String dir, String uid, String ext) throws Exception {
        String path = "./data/Image/" + LocalDate.now() + "/" + dir + "/";
        String filePath = path + uid + "." + ext;
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("referer", ""); //这是破解防盗链添加的参数
        conn.addRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.67");
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);
        InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
        readInputStream(inStream, filePath, path);
        return filePath;
    }

    /*
    lolicon保存图片
     */
    private static void readInputStream(InputStream inStream, String filePath, String path) throws Exception {
        File file = new File(path);
        File file1 = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!file1.exists()) {
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            byte[] buffer = new byte[102400];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            inStream.close();
            fos.flush();
            fos.close();
        } else {
            inStream.close();
        }
    }

}