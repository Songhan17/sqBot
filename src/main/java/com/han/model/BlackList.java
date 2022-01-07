package com.han.model;

/**
 * @author zhanghanlin
 * @date 2022-01-07
 */

import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JAutoSavePluginData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


// MyPluginData.java
public class BlackList extends JAutoSavePluginData {

    public static final BlackList INSTANCE = new BlackList();

    public final Value<Map<Long, List<Long>>> blackList = typedValue(
        createKType(Map.class, createKType(Long.class), createKType(Object.class)),
        new HashMap<>()
    );

    public BlackList() {
        super();
    }
}
