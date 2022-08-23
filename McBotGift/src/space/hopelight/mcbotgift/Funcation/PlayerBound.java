package space.hopelight.mcbotgift.Funcation;

import space.hopelight.mcbotgift.MysqlBaseData.ServerConnect;

import java.sql.SQLException;

public class PlayerBound extends ServerConnect {
    public PlayerBound(){ //假冒构造器
        super();
    }
    /**
     * @InsertQQ 对玩家进行绑定的QQ
     */
    public void InsertQQ(Long player_qq, String player_name) {
        try {
            setPreparedStatement(getConnection().prepareStatement("UPDATE " + getMcBotTableBase() + " SET player_qq=? WHERE player_name=?;"));
            getPreparedStatement().setObject(1,player_qq);
            getPreparedStatement().setObject(2,player_name);
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
     * @insertGiftFlag 标记玩家是否已经领取过绑定奖励
     */
    public void insertGiftFlag(String player_name) {
        try {
            setPreparedStatement(getConnection().prepareStatement("UPDATE " + getMcBotTableBase() + " SET player_flag=1 WHERE player_name=?;"));
            getPreparedStatement().setObject(1, player_name);
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
     * @isAlredayGetGift 检查玩家是否已经领取过绑定的奖励
     */
    public boolean isAlreadyGetGift(String player_name) {
        boolean flag = false;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT player_flag FROM " + getMcBotTableBase() + " WHERE player_name=?;"));
            getPreparedStatement().setObject(1, player_name);
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()) {
                if (getResultSet().getString("player_flag") == null){
                    return flag;
                }
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
     * @checkIsNotNullQQ 检查玩家是否已经绑定过QQ
     * 把玩家名传入，判断返回值不能是空,如果不是空就绑定了，否则的话就是没绑定。
     */
    public boolean checkIsNotNullQQ(String player_name) {
        boolean flag = false;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT player_qq FROM " + getMcBotTableBase() + " WHERE player_name=?;"));
            getPreparedStatement().setObject(1, player_name);
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()){
                if (getResultSet().getString("player_qq") == null){
                    return flag;
                }
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
     * @checkIsSame 检查要绑定的QQ是否被其他玩家绑定了
     * 怎么知道QQ是否被其他玩家绑定了?
     * 上述已经建立在没有绑定的情况了
     * 把需要绑定的QQ传入,如果能够查询到数据，说明被人家绑定了,否则的话就没绑定。
     */
    public boolean checkIsSame(Long player_qq) {
        boolean flag = false;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT player_name FROM " + getMcBotTableBase() +" WHERE player_qq=?;"));
            getPreparedStatement().setObject(1, player_qq);
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

}
