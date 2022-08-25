package space.hopelight.miraibotgift.MysqlBaseData;

import org.jetbrains.annotations.NotNull;
import space.hopelight.miraibotgift.StartClass;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @BotDataConnect
 * Version 1.0
 * QQ绑定问题解决
 *
 * BUG:
 * 子类为 PlayerBind
 * **/

public class BotDataConnect extends DataConnect {
    private static final String BOT_USER = "root";
    private static final String BOT_PASSWORD = "root";
    private static final String BOT_DATABASE = "miraibot";
    private static final String BOT_TABLE_BASE = "miraibotdata";
    private static final String BOT_SHOP_TABLE_BASE = "miraishopdata";
    private static final Connection connection = connectMysql("jdbc:"+getHost()+BOT_DATABASE+"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",BOT_USER,BOT_PASSWORD);

    public static String getBotShopTableBase() {
        return BOT_SHOP_TABLE_BASE;
    }

    public static String getBotTableBase() {
        return BOT_TABLE_BASE;
    }

    public Connection getConnection() {
        return connection;
    }

    public BotDataConnect() {
        setStartTime(System.currentTimeMillis());
        StartClass.INSTANCE.developerMsg("Bot数据库正在连接...");
        createBotTable();
        createShopTable();
        StartClass.INSTANCE.developerMsg("Bot数据库连接成功,本次耗时 " + (System.currentTimeMillis() - getStartTime()) + " ms");
    }

    //假冒构造器
    public BotDataConnect(String msg){
    }

    /**
     * @createTable 创建Bot数据表
     * bot_id 索引ID
     * bot_qq 用户QQ
     * bot_playerName 用户游戏名
     * --------------------------------
     * bot_signFlag 用户签到标记
     * bot_signDegree 用户签到次数
     * bot_consume 用户消费签到值
     * **/

    public void createBotTable() {
        try {
            setStatement(getConnection().createStatement());
            getStatement().execute("CREATE TABLE IF NOT EXISTS " + getBotTableBase() + "("
                    +"bot_id INT NOT NULL AUTO_INCREMENT,"
                    +"bot_qq BIGINT NOT NULL,"
                    +"bot_playerName VARCHAR(16) NOT NULL,"
                    +"bot_signFlag TINYINT(1) DEFAULT 1,"
                    +"bot_signDegree BIGINT DEFAULT 0,"
                    +"bot_signConsume BIGINT DEFAULT 0,"
                    +"PRIMARY KEY(bot_id))"
                    + "ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            StartClass.INSTANCE.developerMsg("Bot数据表搞定~");
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
     * @createShopTable 创建商品数据表
     * commodity_id 商品的ID
     * commodity_name 商品名称
     * commodity_price 商品价格
     * commodity_reward 商品奖励
     * commodity_type 商品类型
     * commodity_salesVolume 商品销量值
     * **/

    public void createShopTable()   {
        try {
            setStatement(getConnection().createStatement());
            getStatement().execute("CREATE TABLE IF NOT EXISTS " + getBotShopTableBase()+ "("
                    +"commodity_id INT NOT NULL AUTO_INCREMENT,"
                    +"commodity_name VARCHAR(20) NOT NULL,"
                    +"commodity_type VARCHAR(4) NOT NULL,"
                    +"commodity_price BIGINT NOT NULL,"
                    +"commodity_reward DOUBLE NOT NULL,"
                    +"commodity_salesVolume BIGINT DEFAULT 0,"
                    +"PRIMARY KEY(commodity_id))"
                    + "ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            StartClass.INSTANCE.developerMsg("Shop数据表搞定~");
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
     * @searchData 查询是否已经绑定了QQ
     * @param bot_qq 用于传递参数进行查询
     *               如果不是空的则就已经绑定
     *               反之则空
     * **/
    public boolean searchData(Long bot_qq){
        boolean flag = false;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT bot_qq FROM " + getBotTableBase() + " WHERE bot_qq=?;"));
            getPreparedStatement().setObject(1,bot_qq);
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()){
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


    /**
     * @searchPlayerName 搜索绑定者的玩家用户名
     * **/
    @NotNull
    public String searchPlayerName(Long bot_qq) {
        StringBuilder playerName = new StringBuilder();
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT bot_playerName FROM " + getBotTableBase() + " WHERE bot_qq=?;"));
            getPreparedStatement().setObject(1,bot_qq);
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()){
                playerName.append(getResultSet().getString("bot_playerName"));
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
        return playerName.toString();
    }


    /**
     * @changeNewSignStatus 改变新的签到状态
     * **/
    public void changeNewSignStatus(){
        try {
            setStatement(getConnection().createStatement());
            getStatement().execute("UPDATE " + getBotTableBase() + " SET bot_signFlag=1 WHERE bot_signFlag=0;");
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
     * @seeAboutTable 对数据表进行查询
     * **/
    public void seeAboutTable(){
        try {
            setStatement(getConnection().createStatement());
            setResultSet(getStatement().executeQuery("SELECT commodity_id FROM " + getBotTableBase()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                getStatement().close();
                getResultSet().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
