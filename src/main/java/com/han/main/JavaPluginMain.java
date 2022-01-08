package com.han.main;

import com.han.model.BlackList;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 使用 Java 请把
 * {@code /src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin}
 * 文件内容改成 {@code org.example.mirai.plugin.JavaPluginMain} <br/>
 * 也就是当前主类全类名
 * <p>
 * 使用 Java 可以把 kotlin 源集删除且不会对项目有影响
 * <p>
 * 在 {@code settings.gradle.kts} 里改构建的插件名称、依赖库和插件版本
 * <p>
 * 在该示例下的 {@link JvmPluginDescription} 修改插件名称，id 和版本等
 * <p>
 * 可以使用 {@code src/test/kotlin/RunMirai.kt} 在 IDE 里直接调试，
 * 不用复制到 mirai-console-loader 或其他启动器中调试
 */

public final class JavaPluginMain extends JavaPlugin {
    public static JavaPluginMain INSTANCE = new JavaPluginMain();

    private JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder("com.han.main", "0.1.0")
                .info("EG")
                .author("十七")
                .name("sq群管机器人")
                .build());
        INSTANCE = this;
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        super.onLoad($this$onLoad);
        BlackList.getInstance().Load();
    }

    @Override
    public void onEnable() {

        Map<Long, List<Long>> list = BlackList.getInstance().getBlackList();

        EventChannel<Event> eventChannel = GlobalEventChannel.INSTANCE.parentScope(this);
        eventChannel.subscribeAlways(MemberJoinRequestEvent.class, g -> {
            // 加群人QQ号
            System.out.println(g.getFromId());
            // 验证消息
            System.out.println(g.getMessage());
            // 群号
            System.out.println(g.getGroupId());

            if (!list.containsKey(g.getGroupId())) {
                list.put(g.getGroupId(), new ArrayList<>());
            }
            if ((list.get(g.getGroupId())).contains(g.getFromId())) {
                g.reject(Boolean.FALSE, "主动退群不再接受");
            }
        });
        eventChannel.subscribeAlways(MemberLeaveEvent.Quit.class, g -> {
            // 群号
            System.out.println(g.getGroupId());
            // 退群人QQ号
            System.out.println(g.getMember().getId());

            if (!list.containsKey(g.getGroupId())) {
                list.put(g.getGroupId(), new ArrayList<>());
            }
            list.get(g.getGroupId()).add(g.getMember().getId());
            System.out.println(list.get(g.getGroupId()));
            BlackList.getInstance().save();
            g.getGroup().sendMessage(String.format("%s主动退群！已加入黑名单", g.getMember().getId()));
        });

        eventChannel.subscribeAlways(FriendMessageEvent.class, g -> {
            if (g.getFriend().getId() == 839924598) {
                String origin = g.getMessage().contentToString();
                if (origin.contains("黑名单")){
                    g.getFriend().sendMessage(list.toString());
                }
                if (origin.contains("@")) {
                    String[] split = origin.split("@");
                    List<Long> target = list.get(Long.parseLong(split[0]));
                    for (Long qq : target) {
                        if (qq.equals(Long.parseLong(split[1]))){
                            target.remove(qq);
                            break;
                        }
                    }

                    g.getFriend().sendMessage(list.toString());
                }

            }
        });
    }
}
