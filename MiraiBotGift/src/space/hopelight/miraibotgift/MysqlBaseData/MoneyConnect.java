package space.hopelight.miraibotgift.MysqlBaseData;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import space.hopelight.miraibotgift.StartClass;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class MoneyConnect extends DataConnect{
    private static final String MONEY_USER = "root";
    private static final String MONEY_PASSWORD = "root";
    private static final String MONEY_DATABASE = "money";
    private static final String MONEY_TABLE = "xconomy";
    public static String getMoneyTable() {
        return MONEY_TABLE;
    }
    private static final Connection connection = connectMysql("jdbc:"+getHost()+MONEY_DATABASE+"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",MONEY_USER,MONEY_PASSWORD);
   public Connection getConnection() {
        return connection;
    }

    //假冒构造器
    public MoneyConnect(String msg){}

    public MoneyConnect(){
        setStartTime(System.currentTimeMillis());
        StartClass.INSTANCE.developerMsg("货币数据库正在连接...");
        StartClass.INSTANCE.developerMsg("货币数据库连接成功,本次耗时: " + (System.currentTimeMillis() - getStartTime()) + " ms");
    }

    /**
     * @seeAboutTable 园币数据库查表
     * **/
    public void seeAboutTable(){
        try {
            setStatement(getConnection().createStatement());
            setResultSet(getStatement().executeQuery("SELECT hidden FROM " + getMoneyTable()));
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

    /**
     * new MoneySignIn  > 调用MoneyConnect 父类构造器
     * */

    @SuppressWarnings("all")
    public void updatePlayerMoney(BigDecimal money, String bot_playerName){
        BigDecimal takeMoney = BigDecimal.valueOf(lookPlayerMoneyValue(bot_playerName)).add(money);
        try {
            setPreparedStatement(getConnection().prepareStatement("UPDATE xconomy SET balance=? WHERE player=?;"));
            getPreparedStatement().setObject(1,takeMoney.doubleValue());
            getPreparedStatement().setObject(2,bot_playerName);
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

    @SuppressWarnings("all")
    /**
     * @lookPlayerMoneyValue 查看玩家个人的金币值
     * **/
    public double lookPlayerMoneyValue(String bot_name){
        double playerMoneyValue = 0.0;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT balance FROM xconomy WHERE player=?;"));
            getPreparedStatement().setObject(1,bot_name);
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()){
                playerMoneyValue = getResultSet().getDouble("balance");
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
        return playerMoneyValue;
    }

    @SuppressWarnings("all")
    /**
     * @ranKingList 查询金币数据排行榜
     * **/
    public MessageChain moneyRanKingList() {
        MessageChainBuilder playerMoneyTopRanKingList = new MessageChainBuilder();
        playerMoneyTopRanKingList.add("=== 玩家金币排行榜 ===" + "\n");
        try {
            setStatement(getConnection().createStatement());
            setResultSet(getStatement().executeQuery("SELECT player,balance FROM xconomy ORDER BY balance DESC LIMIT 10;"));
            while (getResultSet().next()) {
                playerMoneyTopRanKingList.add(getResultSet().getString("player") + " ");
                playerMoneyTopRanKingList.add(getResultSet().getDouble("balance") + " 元" + "\n");
            }
            playerMoneyTopRanKingList.add("====== TOP10 ======");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                getStatement().close();
                getResultSet().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return playerMoneyTopRanKingList.build();
    }

}
