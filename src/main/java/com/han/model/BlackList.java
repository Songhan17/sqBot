package com.han.model;

/**
 * @author zhanghanlin
 * @date 2022-01-07
 */

import kotlinx.serialization.Serializable;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JAutoSavePluginData;

import java.util.HashMap;
import java.util.Map;


// MyPluginData.java
@Serializable
public class BlackList extends JAutoSavePluginData {

    public static final BlackList INSTANCE = new BlackList();

    public final Value<Map<Long, Object>> blackList = typedValue(
            createKType(Map.class, createKType(Long.class), createKType(Object.class)),
            new HashMap<Long, Object>() {{ // 带默认值
                put(123L, "ok");
            }}
    );

    public BlackList() {
        super("blackSettings");
    }
}
