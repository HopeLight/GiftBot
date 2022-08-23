package space.hopelight.mcbotgift.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import space.hopelight.mcbotgift.StartClass;

/**
 * @PlayerEventListen 玩家事件监听
 * **/
public class PlayerEventListen implements Listener {

    @EventHandler
    public void JoinMessage(PlayerJoinEvent event) {
        StringBuilder playerName = new StringBuilder(event.getPlayer().getPlayerListName());
        if (StartClass.getPlayerBound().search(playerName.toString())){
            return;
        }
        StartClass.getStartClass().loggerInfo("加入缓存数据" + playerName);
        StartClass.getPlayerBound().insertNewData(playerName.toString(),(byte)1);
    }

}
