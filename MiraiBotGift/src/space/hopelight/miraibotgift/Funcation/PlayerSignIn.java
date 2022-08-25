package space.hopelight.miraibotgift.Funcation;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import org.jetbrains.annotations.NotNull;
import space.hopelight.miraibotgift.MysqlBaseData.BotDataConnect;
import space.hopelight.miraibotgift.StartClass;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * @PlayerSignIn 它不仅能对金币签到,而且可以实现对任何货币进行签到
 * */
public class PlayerSignIn extends BotDataConnect{

    //假冒构造器
    public PlayerSignIn(){
        super("");
    }

    /**
     * @menu 主要功能
     * **/
    @EventHandler(priority = EventPriority.HIGHEST)
    public void menu(@NotNull GroupMessageEvent event){
            MessageChainBuilder messages = new MessageChainBuilder();
            switch (event.getMessage().contentToString()) {
                case "/菜单" -> {
                    messages.add("=== 田园生活-菜单帮助 === \n");
                    messages.add("/查询 <模块名> [仅支持私聊查询]\n");
                    messages.add("/绑定 <玩家名> [仅支持私聊]\n");
                    messages.add("/田园商城 [仅支持私聊]\n");
                    messages.add("/签到\n");
                    messages.add("/签到排行榜\n");
                    messages.add("/园币排行榜\n");
                    messages.add("=== 田园生活-菜单帮助 === ");
                    event.getGroup().sendMessage(messages.build());
                }
                case "/签到排行榜" -> {
                    long playerQQ = event.getSender().getId();
                    if (!searchData(playerQQ)) {
                        event.getGroup().sendMessage("你还没完成绑定QQ呢,所以根本都没有你的数据！");
                        return;
                    }
                    event.getGroup().sendMessage(new At(playerQQ));
                    event.getGroup().sendMessage(signInDegreeRanKing());
                    event.getGroup().sendMessage("以上就是签到次数排行榜,前十名。");
                }
                case "/园币排行榜" -> {
                    event.getGroup().sendMessage(new At(event.getSender().getId()));
                    event.getGroup().sendMessage(StartClass.getMoneyConnect().moneyRanKingList());
                    event.getGroup().sendMessage("以上就是玩家园币排行榜,前十名。");
                }
            }
        }

    /**
     * @signIn 签到
     * **/
    @EventHandler(priority = EventPriority.MONITOR)
    public void signIn(@NotNull GroupMessageEvent event) {
        if (!event.getMessage().contentToString().equals("/签到")) {
            return;
        }
        Long playerQQ = event.getSender().getId();
        if (!searchData(playerQQ)){
            event.getGroup().sendMessage("你还没完成绑定QQ呢,所以不能随机签到！");
            return;
        }
        //已经绑定了 - 已经签到了
        if (checkIsFinishTask(playerQQ)){
            event.getGroup().sendMessage("你今天已经签到过啦,小笨蛋！距离下次签到要等到,第二天的凌晨哦~");
            return;
        }
        //没签到
        // 处理随机数值
        BigDecimal bigDecimal = StartClass.getRandomChance().chanceValue();
        StringBuilder bot_playerName = new StringBuilder(StartClass.getBotDataConnect().searchPlayerName(playerQQ));
        //判断是否得大奖了
        if (bigDecimal.compareTo(BigDecimal.valueOf(500.0)) == 0){
            event.getGroup().sendMessage(AtAll.INSTANCE);
            event.getGroup().sendMessage("恭喜" + bot_playerName + "在今日签到当中了 500元奖金！");
            event.getGroup().sendMessage(new Face(Face.GU_ZHANG));
        }
        //插入得到的奖励值
        StartClass.getMoneyConnect().updatePlayerMoney(bigDecimal,bot_playerName.toString());
        insertFinishTaskFlag(playerQQ); //插入签到标记
        insertDegree(1,playerQQ); //增加签到累计次数
        event.getGroup().sendMessage(bot_playerName + "你获得了" + bigDecimal + "元");
        event.getGroup().sendMessage("恭喜你" + bot_playerName + "完成了今天的签到~");
    }

    /**
     * @insertFinishTaskFlag 插入完成签到的标志
     * **/
    public void insertFinishTaskFlag(Long bot_qq) {
        try {
            setPreparedStatement(getConnection().prepareStatement("UPDATE " + getBotTableBase() + " SET bot_signFlag=0 WHERE bot_qq=?;"));
            getPreparedStatement().setObject(1,bot_qq);
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
     * @insertDegree 增加签到次数
     * **/
    public void insertDegree(long bot_signDegree,Long bot_qq)  {
        try {
            long degree = searchDegree(bot_qq) + bot_signDegree;
            setPreparedStatement(getConnection().prepareStatement("UPDATE " + getBotTableBase() + " SET bot_signDegree=? WHERE bot_qq=?;"));
            getPreparedStatement().setObject(1,degree);
            getPreparedStatement().setObject(2,bot_qq);
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
     * @serachDegree 查询个人签到次数
     * **/
    public long searchDegree(Long bot_qq){
        long signInDegree = 0;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT bot_signDegree FROM " + getBotTableBase() + " WHERE bot_qq=?;"));
            getPreparedStatement().setObject(1,bot_qq);
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()){
                if (getResultSet().getLong("bot_signDegree") == 0) {
                    return signInDegree;
                }
                    signInDegree = getResultSet().getLong("bot_signDegree");
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
        return signInDegree;
    }

    /**
     *  true > 可以签到 0 > 已经签到
     * 判断 bot_signFlag 是否为1,否则为0的话就是签到了
     *  flag true 已经完成了 false 没完成
     * **/
    public boolean checkIsFinishTask(Long bot_qq){
        boolean flag = false;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT bot_signFlag FROM " + getBotTableBase() + " WHERE bot_qq=?;"));
            getPreparedStatement().setObject(1,bot_qq);
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()){
                if (getResultSet().getInt("bot_signFlag") == 0){
                    flag = true;
                }
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
     * @signInDegreeRanKing 签到次数排行榜
     * **/
    @NotNull
    public MessageChain signInDegreeRanKing() {
        MessageChainBuilder builder = new MessageChainBuilder();
        builder.add("=== 每日签到次数排行榜 ===" + "\n");
        try {
            setStatement(getConnection().createStatement());
            setResultSet(getStatement().executeQuery("SELECT bot_playerName,bot_signDegree FROM " + getBotTableBase() + " ORDER BY bot_signDegree DESC LIMIT 10;"));
            while (getResultSet().next()){
                builder.add(getResultSet().getString("bot_playerName") + " ");
                builder.add(getResultSet().getLong("bot_signDegree") + " 次" + "\n");
            }
        builder.add("========  TOP10 ==========");
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
        return builder.build();
    }


}
