package space.hopelight.miraibotgift.Funcation;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;
import space.hopelight.miraibotgift.StartClass;

public class PlayerInfo extends PlayerSignIn {
    public PlayerInfo(){
        super();
    }

    /**
     * @seeAbout 查询信息
     * **/
    @EventHandler(priority = EventPriority.MONITOR)
    public void seeAbout(@NotNull FriendMessageEvent event){
        if (!event.getMessage().contentToString().contains("/查询")) {
            return;
        }
        long playerQQ = event.getSender().getId();
        if (!searchData(playerQQ)){
            event.getFriend().sendMessage("你还没完成绑定QQ呢,所以不能使用查询功能！");
            return;
        }
        StringBuilder playerName = new StringBuilder(searchPlayerName(playerQQ));;
        MessageChainBuilder singleMessages = new MessageChainBuilder();
        switch (event.getMessage().contentToString().replace("/查询","").trim()) {
            case "个人信息" -> {
                singleMessages.add("=== 玩家绑定信息 ===\n");
                singleMessages.add("您的游戏名为: " + playerName + "\n");
                singleMessages.add("您绑定的QQ号为: " + playerQQ + "\n");
                singleMessages.add("=== 玩家绑定信息 ===");
                event.getFriend().sendMessage(singleMessages.build());
            }
            case "个人园币" -> event.getFriend().sendMessage("你有: " + StartClass.getMoneyConnect().lookPlayerMoneyValue(playerName.toString()) + " 园币");
            case "个人签到次数" -> event.getFriend().sendMessage("你有:" + searchDegree(playerQQ) + "次,签到次数.");
            default ->{
                singleMessages.add("=== 玩家信息查询系统 === \n");
                singleMessages.add("/查询 个人信息\n");
                singleMessages.add("/查询 个人园币\n");
                singleMessages.add("/查询 个人签到次数\n");
                singleMessages.add("=== 玩家信息查询系统 === \n");
                event.getFriend().sendMessage(singleMessages.build());
            }
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
