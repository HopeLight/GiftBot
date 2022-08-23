package space.hopelight.miraibotgift.Funcation;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import space.hopelight.miraibotgift.MysqlBaseData.BotDataConnect;

public class PlayerInfo extends BotDataConnect {
    public PlayerInfo(){
        super("");
    }

    /**
     * @seeAbout 查询信息
     * **/
    @EventHandler(priority = EventPriority.MONITOR)
    public void seeAbout(FriendMessageEvent event){
        if (!event.getMessage().contentToString().contains("/查询")) {
            return;
        }
        long playerQQ = event.getSender().getId();
        if (!searchData(playerQQ)){
            event.getFriend().sendMessage("你还没完成绑定QQ呢,所以不能使用查询功能！");
            return;
        }
        StringBuilder playerName = new StringBuilder(searchPlayerName(playerQQ));;
        MessageChainBuilder singleMessages = new MessageChainBuilder();;
        if ("个人信息".equals(event.getMessage().contentToString().replace("/查询", "").trim())) {
            singleMessages.add("=== 玩家绑定信息 ===\n");
            singleMessages.add("您的游戏名为: " + playerName + "\n");
            singleMessages.add("您绑定的QQ号为: " + playerQQ + "\n");
            singleMessages.add("=== 玩家绑定信息 ===");
            event.getFriend().sendMessage(singleMessages.build());
        } else {
            singleMessages.add("=== 玩家信息查询系统 === \n");
            singleMessages.add("/查询 个人信息\n");
            singleMessages.add("=== 玩家信息查询系统 === \n");
            event.getFriend().sendMessage(singleMessages.build());
        }
    }

    /**
     *
     * autoAccept 自动同意添加好友
     * **/
    @EventHandler(priority = EventPriority.MONITOR)
    public void autoAccept(NewFriendRequestEvent requestEvent){
        requestEvent.accept();
    }

}
