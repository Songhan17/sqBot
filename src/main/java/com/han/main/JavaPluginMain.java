package com.han.main;

import com.han.model.BlackList;
import io.ktor.util.collections.CollectionUtilsKt;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.internal.data.CollectionUtilKt;
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

import java.util.*;


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
    public static long PERM = 88888L;

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
//            System.out.println(g.getFromId());
//            // 验证消息
//            System.out.println(g.getMessage());
//            // 群号
//            System.out.println(g.getGroupId());

            if (!list.containsKey(g.getGroupId())) {
                list.put(g.getGroupId(), new ArrayList<>());
            }
            if ((list.get(g.getGroupId())).contains(g.getFromId())) {
                g.reject(Boolean.FALSE, "黑名单用户暂不接受加入");
            }
        });
//        eventChannel.subscribeAlways(MemberLeaveEvent.Quit.class, g -> {
//            // 群号
////            System.out.println(g.getGroupId());
////            // 退群人QQ号
////            System.out.println(g.getMember().getId());
//
//            if (!list.containsKey(g.getGroupId())) {
//                list.put(g.getGroupId(), new ArrayList<>());
//            }
//            list.get(g.getGroupId()).add(g.getMember().getId());
////            System.out.println(list.get(g.getGroupId()));
//            BlackList.getInstance().save();
//            g.getGroup().sendMessage(String.format("%s退群了！", g.getMember().getId()));
//        });


        eventChannel.subscribeAlways(GroupMessageEvent.class, g -> {
            List<Long> perm = list.get(PERM);
            if (perm.contains(g.getSender().getId())) {
                try {
                    String origin = g.getMessage().contentToString();
                    if (origin.contains("操作手册")) {
                        g.getGroup().sendMessage(getFailed());
                        return;
                    }
                    if (origin.contains("添加黑名单#")) {
                        String[] split = origin.split("#");
                        Long number = Long.parseLong(split[1]);
                        if (!list.containsKey(g.getGroup().getId())) {
                            list.put(g.getGroup().getId(), new ArrayList<>());
                        }
                        if ((list.get(g.getGroup().getId())).contains(number)) {
                            g.getGroup().sendMessage(String.format("QQ:%s 已在黑名单中", number));
                            return;
                        } else {
                            list.get(g.getGroup().getId()).add(number);
                        }
                        g.getGroup().sendMessage("成功！");
                        BlackList.getInstance().save();
                        return;
                    }
                    if (origin.contains("移除黑名单#")) {
                        String[] split = origin.split("#");
                        Long number = Long.parseLong(split[1]);
                        if (!list.containsKey(g.getGroup().getId())) {
                            return;
                        }
                        if ((list.get(g.getGroup().getId())).contains(number)) {
                            List<Long> target = list.get(g.getGroup().getId());
                            for (Long qq : target) {
                                if (qq.equals(number)) {
                                    target.remove(qq);
                                    break;
                                }
                            }
                        }
                        g.getGroup().sendMessage("成功！");
                        BlackList.getInstance().save();
                        return;
                    }
                } catch (Exception e) {
                    g.getGroup().sendMessage(getFailed());
                }
            }
        });

        eventChannel.subscribeAlways(FriendMessageEvent.class, g -> {
            if (!list.containsKey(PERM)) {
                list.put(PERM, new ArrayList<>());
                BlackList.getInstance().save();
            }
            if (list.get(PERM).contains(g.getFriend().getId()) || g.getFriend().getId() == 839924598) {
                try {
                    String origin = g.getMessage().contentToString();
                    if (origin.contains("黑名单")) {
                        g.getFriend().sendMessage(getBlack(list));
                        return;
                    }
                    if (origin.contains("@")) {
                        String[] split = origin.split("@");
                        Long group = Long.parseLong(split[0]);
                        Long number = Long.parseLong(split[1]);
                        List<Long> target = list.get(group);
                        for (Long qq : target) {
                            if (qq.equals(number)) {
                                target.remove(qq);
                                break;
                            }
                        }
                        BlackList.getInstance().save();
                        g.getFriend().sendMessage(getBlack(list));
                        return;
                    }
                    if (origin.contains("#")) {
                        String[] split = origin.split("#");
                        Long group = Long.parseLong(split[0]);
                        Long number = Long.parseLong(split[1]);
                        if (!list.containsKey(group)) {
                            list.put(group, new ArrayList<>());
                        }
                        if ((list.get(group)).contains(number)) {
                            g.getFriend().sendMessage(String.format("QQ:%s 已在黑名单中", number));
                            return;
                        } else {
                            list.get(group).add(number);
                        }
                        BlackList.getInstance().save();
                        g.getFriend().sendMessage(getBlack(list));
                        return;
                    }
                    if (origin.contains("增加:")) {
                        String[] split = origin.split(":");
                        Long number = Long.parseLong(split[1]);
                        if ((list.get(PERM)).contains(number)) {
                            g.getFriend().sendMessage(String.format("QQ:%s 已在权限中", number));
                            return;
                        } else {
                            list.get(PERM).add(number);
                        }
                        BlackList.getInstance().save();
                        g.getFriend().sendMessage(getBlack(list));
                        return;
                    }
                    if (origin.contains("移除:")) {
                        String[] split = origin.split(":");
                        Long number = Long.parseLong(split[1]);
                        List<Long> target = list.get(PERM);
                        for (Long qq : target) {
                            if (qq.equals(number)) {
                                target.remove(qq);
                                break;
                            }
                        }
                        BlackList.getInstance().save();
                        g.getFriend().sendMessage(getPermission(list));
                        return;
                    }
                    if (origin.contains("权限")) {
                        g.getFriend().sendMessage(getPermission(list));
                        return;
                    }
                    if (origin.contains("删除key")) {
                        String[] split = origin.split(":");
                        Long number = Long.parseLong(split[0]);
                        list.keySet().removeIf(key -> key == number);
                        g.getFriend().sendMessage(list.toString());
                    }
                } catch (Exception e) {
                    g.getFriend().sendMessage("操作失败，系统异常");
                }
            }
        });
    }

    private static String getBlack(Map<Long, List<Long>> list){
        StringBuffer sb = new StringBuffer();
        if (!list.isEmpty()) {
            for (Map.Entry<Long, List<Long>> entry : list.entrySet()) {
                if (entry.getKey() == PERM) {
                    continue;
                }
                sb.append("群号：").append(entry.getKey()).append("\r\n");
                sb.append("黑名单：").append(entry.getValue()).append("\r\n");
                sb.append("======================================").append("\r\n");
            }
            sb.append("回复   群号@QQ号   移除黑名单。 ex:123456@123456").append("\r\n");
            sb.append("回复   群号#QQ号   加入黑名单。 ex:123456#123456").append("\r\n");
        }
        return sb.toString();
    }

    private static String getFailed(){
        StringBuffer sb = new StringBuffer();
        sb.append("操作:").append("\r\n");
        sb.append("回复   添加黑名单#QQ号   增加权限人员。 例:添加黑名单#123456").append("\r\n");
        sb.append("回复   移除黑名单#QQ号   移除权限人员。 例:移除黑名单#123456");
        return sb.toString();
    }

    private static String getPermission(Map<Long, List<Long>> list){
        StringBuffer sb = new StringBuffer();
        List<Long> longs = list.get(PERM);
        sb.append("权限：").append(longs).append("\r\n");
        sb.append("======================================").append("\r\n");
        sb.append("回复   增加:QQ号   增加权限人员。 ex:增加:123456").append("\r\n");
        sb.append("回复   移除:QQ号   移除权限人员。 ex:移除:123456");
        return sb.toString();
    }

}
