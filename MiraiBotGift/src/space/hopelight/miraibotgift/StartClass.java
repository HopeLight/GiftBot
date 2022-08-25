package space.hopelight.miraibotgift;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.BotConfiguration;
import org.quartz.SchedulerException;
import space.hopelight.miraibotgift.Funcation.*;
import space.hopelight.miraibotgift.MysqlBaseData.BotDataConnect;
import space.hopelight.miraibotgift.MysqlBaseData.DataConnect;
import space.hopelight.miraibotgift.MysqlBaseData.MoneyConnect;
import space.hopelight.miraibotgift.MysqlBaseData.ServerConnect;
import space.hopelight.miraibotgift.Task.BotTask;

import java.util.Objects;


/**
 * @StartClass 启动类
 * DEVELOPER_QQ -> 开发者QQ 用途: 给开发者发送插件调试相关的信息
 * QQ_USER  Bot账号
 * QQ_PASSWORD Bot密码
 * **/
public class StartClass extends JavaPlugin {
    private static ServerConnect serverConnect;
    private static BotDataConnect botDataConnect;
    private static MoneyConnect moneyConnect;
    private static BotTask botTask;
    private static Bot bot;
    private static RandomChance randomChance;
    public static final StartClass INSTANCE = new StartClass();
    private static final int GROUP = 59414876;
    private static final long DEVELOPER_QQ = 2622099140L; //开发者请改这里
    private static final long QQ_USER = 0L; //机器人账号
    private static final String QQ_PASSWORD = ""; //机器人密码

    public static BotTask getBotTask() {
        return botTask;
    }

    public static RandomChance getRandomChance() {
        return randomChance;
    }

    public static BotDataConnect getBotDataConnect() {
        return botDataConnect;
    }

    public static ServerConnect getServerConnect() {
        return serverConnect;
    }

    public static MoneyConnect getMoneyConnect() {
        return moneyConnect;
    }

    public static Bot getBot() {
        return bot;
    }

    public static long getDeveloperQq() {
        return DEVELOPER_QQ;
    }

    public static long getQqUser() {
        return QQ_USER;
    }

    public static String getQqPassword() {
        return QQ_PASSWORD;
    }

    @SuppressWarnings("all")
    public StartClass() {
        super(new JvmPluginDescriptionBuilder("space.hopelight.miraibotgift",
                "1.0.0")
                .name("MiraiBotGift")
                .author("HopeLight")
                .build());
    }


    @Override
    public void onEnable(){
        bot = BotFactory.INSTANCE.newBot(getQqUser(), getQqPassword(), new BotConfiguration() {
            {
                // 使用平板协议登录
                setProtocol(MiraiProtocol.ANDROID_PAD);
                // 指定设备信息文件路径，文件不存在将自动生成一个默认的，存在就读取
                fileBasedDeviceInfo("deviceInfo_114514.json");
            }
        });
        bot.login();
        //加载驱动
        DataConnect.loadMysqlDrive();
        //Bot数据库加载
        botDataConnect = new BotDataConnect();
        serverConnect = new ServerConnect();
        moneyConnect  = new MoneyConnect();
        //事件监听
        registerEvent(new PlayerBound());
        registerEvent(new PlayerSignIn());
        registerEvent(new PlayerSignShop());
        registerEvent(new PlayerInfo());
        //注册随机签到
        randomChance = new RandomChance(300,500,300.0);
        //线程注册
        try {
            botTask = new BotTask("0 0 0 * * ?","0 0 0/7 * * ?");
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        loggerInfo("开始关闭数据操作!");
        loggerInfo("关闭线程操作开始!");
        DataConnect.closeMysql(getBotDataConnect().getConnection());
        DataConnect.closeMysql(getServerConnect().getConnection());
        DataConnect.closeMysql(getMoneyConnect().getConnection());
        try {
            getBotTask().getScheduler().shutdown(true);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("all")
    public void loggerInfo(String msg){
        getLogger().info("MiraiBotGif -> " + msg);
    }

    public void developerMsg(String msg){
        Objects.requireNonNull(getBot().getFriend(getDeveloperQq())).sendMessage(msg);
    }

    public void registerEvent(ListenerHost event){
        bot.getEventChannel().registerListenerHost(event);
    }

    public void groupMsg(String msg){
        Objects.requireNonNull(getBot().getGroup(GROUP)).sendMessage(msg);
    }
    public void groupMsg(MessageChain msg){
        Objects.requireNonNull(getBot().getGroup(GROUP)).sendMessage(msg);
    }

}
