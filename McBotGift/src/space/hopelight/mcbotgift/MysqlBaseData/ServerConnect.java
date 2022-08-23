package space.hopelight.mcbotgift.MysqlBaseData;

import space.hopelight.mcbotgift.StartClass;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;


/**
 * @ServerConnect 服务器连接数据操作
 *
 * MC_BOT_DATABASE 用于连接数据库
 * MC_BOT_TABLE_BASE 用于对数据库表 增 | 删 | 改 | 查
 * **/

public class ServerConnect extends MysqlConnect {
    private static final String MC_BOT_USER = StartClass.getStartClass().getConfig().getString("mysqlSetting.mcBotUser");
    private static final String MC_BOT_PASSWORD = StartClass.getStartClass().getConfig().getString("mysqlSetting.mcBotPassword");
    private static final String MC_BOT_DATABASE = StartClass.getStartClass().getConfig().getString("mysqlSetting.mcBotDataBase");
    private static final String MC_BOT_TABLE_BASE = StartClass.getStartClass().getConfig().getString("mysqlSetting.mcBotTableBase");

    private static final Connection connection = connectMysql("jdbc:" + getHost()+MC_BOT_DATABASE+"?useSSL=" +getSSL()+ "&allowPublicKeyRetrieval=true&serverTimezone=UTC",MC_BOT_USER,MC_BOT_PASSWORD);

    public static Connection getConnection() {
        return connection;
    }

    public static String getMcBotTableBase() {
        return MC_BOT_TABLE_BASE;
    }


    public ServerConnect() {
        StartClass.getStartClass().loggerInfo("Server数据库加载完毕!");
        createTable();
    }


    /**
     * @createTable 创建Server数据表
     * player_id 玩家序列
     * player_name 玩家名称
     * player_status 玩家第一次登录记录
     * player_qq 玩家绑定QQ
     * player_flag 绑定奖励
     * ----------------------
     **/
    public void createTable()  {
        try {
            setStatement(getConnection().createStatement());
            Objects.requireNonNull(getStatement()).execute("CREATE TABLE IF NOT EXISTS " + getMcBotTableBase() + "("
                    + "player_id INT NOT NULL AUTO_INCREMENT,"
                    + "player_name VARCHAR(15) NOT NULL,"
                    + "player_status TINYINT(1) NOT NULL,"
                    + "player_qq BIGINT,"
                    + "player_flag TINYINT(1),"
                    + "PRIMARY KEY (player_id));");
            StartClass.getStartClass().loggerInfo("Server数据表搞定");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                getStatement().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @inputText 对新玩家的数据插入
     */
    public void insertNewData(String name,byte flag)  {
        try {
            setPreparedStatement(getConnection().prepareStatement("INSERT INTO " + getMcBotTableBase() + " (player_name,player_status) VALUES(?,?);"));
            getPreparedStatement().setObject(1, name);
            getPreparedStatement().setObject(2, flag);
            getPreparedStatement().execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                getPreparedStatement().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

        /**
     * @search 用于玩家第一次登录检查数据是否存在
     * player_status 不是空
     **/
    public boolean search(String name) {
        boolean flag = false;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT player_status FROM " + getMcBotTableBase() + " WHERE player_name=?;"));
            getPreparedStatement().setObject(1, name);
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
