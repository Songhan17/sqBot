package com.han.model;

/**
 * @author zhanghanlin
 * @date 2022-01-07
 */

import kotlinx.serialization.Serializable;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JAutoSavePluginData;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// MyPluginData.java
public class BlackList {

    public Map<Long, List<Long>> getBlackList() {
        return blackList;
    }

    public void setBlackList(Map<Long, List<Long>> blackList) {
        this.blackList = blackList;
    }

    Map<Long, List<Long>> blackList;
    String path = System.getProperty("user.dir") + File.separator + "data" + File.separator + "sqData.json";

    private final static BlackList INSTANCE = new BlackList();

    public static BlackList getInstance() {
        return INSTANCE;
    }

    public BlackList() {
        blackList = new HashMap<>();
    }

    public void Load() {
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            try {
                ObjectInputStream ois;
                FileInputStream fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                // 代表文件是否还有内容
                while (fis.available() > 0) {
                    // 从流中读出来
                    blackList = (Map<Long, List<Long>>) ois.readObject();
                }
                ois.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            init();
        }
    }

    public void save() {
        try {
            File file = new File(path);
            FileOutputStream fo = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fo);
            oos.writeObject(blackList);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        File file = new File(path);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
