package space.hopelight.miraibotgift.MysqlBaseData;

import space.hopelight.miraibotgift.StartClass;

import java.sql.Connection;
import java.sql.SQLException;

public class ServerConnect extends DataConnect {
    private static final String MC_BOT_USER = "root";
    private static final String MC_BOT_PASSWORD = "root";
    private static final String MC_BOT_DATABASE = "mcserver";
    private static final String MC_BOT_TABLE_BASE = "mcbotdata";
    private static final Connection connection = connectMysql("jdbc:"+getHost()+MC_BOT_DATABASE+"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",MC_BOT_USER,MC_BOT_PASSWORD);
    public Connection getConnection() {
        return connection;
    }
    public static String getMcBotTableBase() {
        return MC_BOT_TABLE_BASE;
    }

    public ServerConnect(){
        setStartTime(System.currentTimeMillis());
        StartClass.INSTANCE.developerMsg("Server数据库正在连接...");
        StartClass.INSTANCE.developerMsg("Server数据库连接成功,本次耗时: " + (System.currentTimeMillis() - getStartTime()) + " ms");
    }

    public ServerConnect(String msg){}
    /**
     * @lookIsTherePeople 查看是否有这玩家
     */
    public boolean lookIsTherePeople(String player_name) {
        boolean flag = false;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT player_name FROM " + getMcBotTableBase() + " WHERE player_name=?;"));
            getPreparedStatement().setObject(1, player_name);
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
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
     * @redisplayBind 检查要绑定的玩家名, 是否在服务器那边已经设置QQ号相对应的。
     * 如果qq不是空的,代表就是绑定了
     * flag true 已经绑定 | false 未绑定
     */
    public boolean isAlreadySet(String player_name) {
        boolean flag = false;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT player_qq FROM " + getMcBotTableBase() +" WHERE player_name=?;"));
            getPreparedStatement().setObject(1, player_name);
            setResultSet(getPreparedStatement().executeQuery());
            //如果有数据的话就为真
            if (getResultSet().next()){
                if (getResultSet().getString("player_qq") == null){
                    return flag;
                }
                flag = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
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
     * @isNotSame 检查是否绑定的号主不相同
     * 建立在已经完成了游戏里的绑定 -> 这个玩家已经有了一个QQ
     * <p>
     * 把玩家名字传入 查出QQ
     * 如果玩家设置的QQ和自身的QQ不同则视为不一样的号主
     */
    public boolean isNotSame(String player_name,Long bot_qq) {
        boolean flag = false;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT player_qq FROM " + getMcBotTableBase() + " WHERE player_name=?;"));
            getPreparedStatement().setObject(1, player_name);
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()) {
                if (getResultSet().getLong("player_qq") == bot_qq) {
                    return flag;
                }
                flag = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
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
     * @seeAboutTable 服务器数据库查表
     * **/
    public void seeAboutTable(){
        try {
            setStatement(getConnection().createStatement());
            setResultSet(getStatement().executeQuery("SELECT player_ID FROM " + getMcBotTableBase()));
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
