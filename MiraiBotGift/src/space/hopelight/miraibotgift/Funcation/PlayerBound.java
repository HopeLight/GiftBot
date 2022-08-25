package space.hopelight.miraibotgift.Funcation;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import org.jetbrains.annotations.NotNull;
import space.hopelight.miraibotgift.MysqlBaseData.BotDataConnect;
import space.hopelight.miraibotgift.StartClass;

import java.sql.SQLException;


/**
 * version 1.1 做出改进 通过发现群会导致账号冻结
 * 因此改为好友 -> 优点: 不容易被冻结,速度提升
 * **/
public class PlayerBound extends BotDataConnect {

    public PlayerBound() {
        super("");
    }

    /**
     * @bindData 绑定玩家数据
     **/
    @EventHandler(priority = EventPriority.MONITOR)
    public void boundData(@NotNull FriendMessageEvent event) {
        if (!event.getMessage().contentToString().contains("/绑定")) {
            return;
        }
        event.getFriend().sendMessage("请等待,正在开始执行操作...");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(event.getMessage().contentToString().replace("/绑定", "").trim());
        Long playerQQ = event.getSender().getId();
        if (stringBuilder.toString().isEmpty()) {
            event.getFriend().sendMessage("你还没有输入要绑定账号的ID呢！");
            return;
        }
        if (searchData(playerQQ)) {
            event.getFriend().sendMessage("你已经绑定过QQ啦,请不要尝试重复绑定哦！");
            return;
        }
        if (!StartClass.getServerConnect().lookIsTherePeople(stringBuilder.toString())) {
            event.getFriend().sendMessage("很抱歉,绑定的这个玩家从来没有玩过服务器,所以不能被绑定成功！");
            return;
        }
        if (!StartClass.getServerConnect().isAlreadySet(stringBuilder.toString())) {
            event.getFriend().sendMessage("很抱歉,你还没有在游戏内完成QQ号的绑定设置,所以无法完成QQ的绑定");
            return;
        }
        if (StartClass.getServerConnect().isNotSame(stringBuilder.toString(), playerQQ)) {
            event.getFriend().sendMessage("错误的绑定,这不是号主本人！所以无法绑定");
            return;
        }
        insertData(playerQQ, stringBuilder.toString());
        event.getFriend().sendMessage("恭喜你,绑定" + stringBuilder + "完成");
    }

    /**
     * @inserData 对玩家数据绑定的插入
     * **/
    public void insertData(Long bot_qq,String bot_playerName) {
        try {
            setPreparedStatement(getConnection().prepareStatement("INSERT INTO " + getBotTableBase() + " (bot_qq,bot_playerName) VALUES(?,?);"));
            getPreparedStatement().setObject(1,bot_qq);
            getPreparedStatement().setObject(2,bot_playerName);
            getPreparedStatement().execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                getPreparedStatement().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}