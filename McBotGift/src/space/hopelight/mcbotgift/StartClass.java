package space.hopelight.mcbotgift;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import space.hopelight.mcbotgift.Command.PlayerGift;
import space.hopelight.mcbotgift.Funcation.PlayerBound;
import space.hopelight.mcbotgift.Listener.PlayerEventListen;
import space.hopelight.mcbotgift.MysqlBaseData.BotDataConnect;
import space.hopelight.mcbotgift.MysqlBaseData.MysqlConnect;
import space.hopelight.mcbotgift.MysqlBaseData.ServerConnect;

import java.util.List;
import java.util.Objects;

public class StartClass extends JavaPlugin implements CommandExecutor {
    private static StartClass startClass;
    private static BotDataConnect botDataConnect;
    private static PlayerBound playerBound;

    public static BotDataConnect getBotDataConnect() {
        return botDataConnect;
    }

    public static PlayerBound getPlayerBound() {
        return playerBound;
    }

    public static StartClass getStartClass() {
        return startClass;
    }

    @Override
    public void onEnable() {
        startClass = this;
        saveDefaultConfig();
        playerBound = new PlayerBound();
        botDataConnect = new BotDataConnect();
        Bukkit.getPluginManager().registerEvents(new PlayerEventListen(),this);
        Objects.requireNonNull(Bukkit.getPluginCommand("gift")).setExecutor(new PlayerGift());
        Objects.requireNonNull(Bukkit.getPluginCommand("gift")).setTabCompleter(new PlayerGift());
    }

    @Override
    public void onDisable() {
        loggerInfo("关闭数据库操作,开始!");
        MysqlConnect.closeMysql(BotDataConnect.getConnection());
        MysqlConnect.closeMysql(ServerConnect.getConnection());
        loggerInfo("数据库,关闭成功~");
    }

    public void loggerInfo(String msg){
        Bukkit.getConsoleSender().sendMessage("§7[§aMcBotGift§7]§f " + msg);
    }

    public boolean isEmptyItemStack(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR || stack.getAmount() < 1;
    }

    public void giveItem(HumanEntity player, ItemStack stack) {
        if (isEmptyItemStack(stack)) {
            return;
        }
        World world = player.getWorld();
        for (ItemStack dropItem : player.getInventory().addItem(new ItemStack[]{stack}).values()) {
            world.dropItem(player.getLocation(), dropItem);
        }
    }

    public void giveItemStack(List<String> itemStack, Player p){
        for (String s1 : itemStack) {
            giveItem(p.getPlayer(),new ItemStack(Material.valueOf(s1.replaceAll("\\d","").trim()),
                    Integer.parseInt(s1.replaceAll("[A-Z]+","")
                            .replace("_","").trim())));
        }
    }

    public void executeCommand(List<String> executeCommand,Player p){
        for (String s1 : executeCommand) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),s1.replace("%player%",p.getPlayerListName()));
        }
    }

    public void playerSound(String name, Sound sound){
        Player player = Bukkit.getOfflinePlayer(name).getPlayer();
        Objects.requireNonNull(player).playSound(player.getLocation(),sound,2F, 0F);
    }

}
