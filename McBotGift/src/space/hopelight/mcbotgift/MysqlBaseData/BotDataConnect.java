package space.hopelight.mcbotgift.MysqlBaseData;


import space.hopelight.mcbotgift.StartClass;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @BotDataConnect 机器人数据库连接
 * **/
public class BotDataConnect extends MysqlConnect {
    private static final String BOT_USER = StartClass.getStartClass().getConfig().getString("mysqlSetting.miraiBotUser");
    private static final String BOT_PASSWORD = StartClass.getStartClass().getConfig().getString("mysqlSetting.miraiBotPassword");
    private static final String BOT_DATABASE = StartClass.getStartClass().getConfig().getString("mysqlSetting.miraiDataBase");
    private static final String BOT_TABLE_BASE = StartClass.getStartClass().getConfig().getString("mysqlSetting.miraiTableBase");
    private static final Connection connection = connectMysql("jdbc:" + getHost()+ BOT_DATABASE + "?useSSL=" +getSSL()+ "&allowPublicKeyRetrieval=true&serverTimezone=UTC",BOT_USER,BOT_PASSWORD);
    public static Connection getConnection() {
        return connection;
    }

    public static String getBotTableBase() {
        return BOT_TABLE_BASE;
    }

    public BotDataConnect(){
        StartClass.getStartClass().loggerInfo("Bot数据库加载完毕!");
    }
    /***
     *
     * @isAlreadyBind 对Bot数据库发起请求,查看玩家是否已经完成了QQ的绑定
     * 如果是空的话,则代表没有完成绑定
     * 不是空的话,则代表已经完成
     * */
    public boolean isAlreadyBound(String player_name) {
        boolean flag = false;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT bot_playerName FROM " + getBotTableBase() + " WHERE bot_playerName=?;"));
            getPreparedStatement().setObject(1, player_name);
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                getPreparedStatement().close();
                getResultSet().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return flag;
    }

}
