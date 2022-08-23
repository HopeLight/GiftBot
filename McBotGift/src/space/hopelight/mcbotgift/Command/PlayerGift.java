package space.hopelight.mcbotgift.Command;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import space.hopelight.mcbotgift.StartClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @PlayerGift 用于绑定QQ,领取QQ礼物。
 * **/
public class PlayerGift implements CommandExecutor, TabCompleter {
    private static final List<String> ITEM_STACK = StartClass.getStartClass().getConfig().getStringList("botGift.itemStack");
    private static final List<String> COMMAND= StartClass.getStartClass().getConfig().getStringList("botGift.command");
    private static final List<String> MC_BOT_HELP = StartClass.getStartClass().getConfig().getStringList("mcBotInfo.commandHelp");
    private static final List<String> TAB_TIPS = new ArrayList<>(Arrays.asList("get","init"));
    public static List<String> getMcBotHelp() {
        return MC_BOT_HELP;
    }

    public static List<String> getCOMMAND() {
        return COMMAND;
    }

    public static List<String> getItemStack() {
        return ITEM_STACK;
    }

    // /gift receive
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player p = (commandSender instanceof Player) ? (Player) commandSender : null;
        if (p == null){
            StartClass.getStartClass().loggerInfo("控制台不能使用此指令！");
            return true;
        }
        if (s.equalsIgnoreCase("gift")) {
            if (strings.length == 0) {
                for (String help : getMcBotHelp()) {
                    p.sendMessage(help.replace("&","§"));
                }
                StartClass.getStartClass().playerSound(p.getPlayerListName(), Sound.ENTITY_CAT_STRAY_AMBIENT);
                return true;
            }
            switch (strings[0]) {
                case "get" -> {
                    if (!StartClass.getPlayerBound().checkIsNotNullQQ(p.getPlayerListName())) {
                        p.sendMessage(Objects.requireNonNull(StartClass.getStartClass().getConfig().getString("mcBotInfo.notFinishQQBound")));
                        StartClass.getStartClass().playerSound(p.getPlayerListName(), Sound.ENTITY_CAT_STRAY_AMBIENT);
                        return true;
                    }
                    if (!StartClass.getBotDataConnect().isAlreadyBound(p.getPlayerListName())) {
                        p.sendMessage(Objects.requireNonNull(StartClass.getStartClass().getConfig().getString("mcBotInfo.notFinishBotBound")));
                        StartClass.getStartClass().playerSound(p.getPlayerListName(), Sound.ENTITY_CAT_STRAY_AMBIENT);
                        return true;
                    }
                    if (StartClass.getPlayerBound().isAlreadyGetGift(p.getPlayerListName())) {
                        p.sendMessage(Objects.requireNonNull(StartClass.getStartClass().getConfig().getString("mcBotInfo.alreadyGotGift")));
                        StartClass.getStartClass().playerSound(p.getPlayerListName(), Sound.ENTITY_CAT_STRAY_AMBIENT);
                        return true;
                    }
                    StartClass.getPlayerBound().insertGiftFlag(p.getPlayerListName()); //插入标记
                    StartClass.getStartClass().executeCommand(getCOMMAND(),p); //执行命令
                    StartClass.getStartClass().giveItemStack(getItemStack(),p); //给予物品
                    p.sendMessage(Objects.requireNonNull(StartClass.getStartClass().getConfig().getString("mcBotInfo.finishSentGift")));
                    StartClass.getStartClass().playerSound(p.getPlayerListName(), Sound.ENTITY_CAT_STRAY_AMBIENT);
                    return true;
                }
                case "init" -> {
                    if (strings.length == 1) {
                        p.sendMessage(Objects.requireNonNull(StartClass.getStartClass().getConfig().getString("mcBotInfo.notFinishInputQQ")));
                        StartClass.getStartClass().playerSound(p.getPlayerListName(),Sound.ENTITY_CAT_STRAY_AMBIENT);
                        return true;
                    }
                    if (!strings[1].matches("^[1-9][0-9]{3,9}") || strings[1].contains(".")) {
                        p.sendMessage(Objects.requireNonNull(StartClass.getStartClass().getConfig().getString("mcBotInfo.checkYourQQFormat")));
                        StartClass.getStartClass().playerSound(p.getPlayerListName(),Sound.ENTITY_CHICKEN_EGG);
                        return true;
                    }
                    if (StartClass.getPlayerBound().checkIsNotNullQQ(p.getPlayerListName())) {
                        p.sendMessage(Objects.requireNonNull(StartClass.getStartClass().getConfig().getString("mcBotInfo.alreadyBoundedQQ")));
                        StartClass.getStartClass().playerSound(p.getPlayerListName(),Sound.ENTITY_CHICKEN_EGG);
                        return true;
                    }
                    //检查绑定的QQ,是否已经被人绑定过了
                    if (StartClass.getPlayerBound().checkIsSame(Long.valueOf(strings[1]))) {
                        p.sendMessage(Objects.requireNonNull(StartClass.getStartClass().getConfig().getString("mcBotInfo.checkYourQQBound")));
                        StartClass.getStartClass().playerSound(p.getPlayerListName(), Sound.ENTITY_CAT_STRAY_AMBIENT);
                        return true;
                    }
                    StartClass.getPlayerBound().InsertQQ(Long.parseLong(strings[1]),p.getPlayerListName());
                    p.sendMessage(Objects.requireNonNull(StartClass.getStartClass().getConfig().getString("mcBotInfo.finishBoundedBound")));
                    StartClass.getStartClass().playerSound(p.getPlayerListName(), Sound.ENTITY_CAT_STRAY_AMBIENT);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!s.equalsIgnoreCase("gift")) {
            return null;
        }
        if (strings.length != 1){
            return null;
        }
        return TAB_TIPS;
    }


}
