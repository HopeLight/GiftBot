package space.hopelight.miraibotgift.Funcation;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.AtAll;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;
import space.hopelight.miraibotgift.StartClass;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * @PlayerSignShop 玩家签到在线商场系统
 * 通过数据库模仿后台可以进行添货
 * version 1.0:
 *
 *     可以查询商品的销售价格 √
 *     可以查询商品是否存在 √
 *     可以购买商品 √
 *     玩家的签到次数消费排行榜 √
 *     查看商品销售量 √
 *     修改商品的销售量 √
 *     商品销售量排行榜 √
 *     检测商城内是否为空 √
 *
 *version 2.0:
 * 1. 在线上货功能
 * /商城管理 上货 商品 [园币] 货币 价格 奖励
 * /商品管理 设置 商品名称 xxx
 * /商品管理 设置 商品类型 xx
 * /商品管理 设置 商品价格 xx
 * /商品管理 设置 商品奖励 xx
 * /商品管理 下架 商品名
 * 2. 派发奖励时,根据商品的类型决定.
 * 3. 限制商品货源,设置定期的商品数量值
 *
 * 对商品类型增加了 货币 | 权限
 * 权限:
 *  飞行时间
 *  增 查
 * BUG解决问题:
 * 1. 将int > long
 * 2. 把Connection 对象封装为静态的
 *
 *version 3.0:
 * 群 -> 个人 优点: 大大提高速度,减少冻结概率
 * **/

public class PlayerSignShop extends PlayerSignIn {

    @EventHandler(priority = EventPriority.MONITOR)
    public void signPlayerShop(@NotNull FriendMessageEvent event) {
        /*
        /签到商城 > 有哪些商品
        /购买方式 > 查看怎么购买
        /签到消费排行榜
        /商品销售量排行榜
        * **/
        if (event.getMessage().contentToString().isEmpty()){
            return;
        }
            switch (event.getMessage().contentToString()) {
                case "/田园商城" -> {
                    if (!searchData(event.getSender().getId())) {
                        event.getFriend().sendMessage("你还没完成绑定QQ呢,所以不能使用商城任何功能！");
                        return;
                    }
                    MessageChainBuilder shopHelpList = new MessageChainBuilder();
                    shopHelpList.add("=== 田园生活在线购物商城 ===\n");
                    shopHelpList.add("/商品查看\n");
                    shopHelpList.add("/查看购买方式\n");
                    shopHelpList.add("/签到消费排行榜\n");
                    shopHelpList.add("/商品销售量排行榜\n");
                    shopHelpList.add("/消费查询\n");
                    shopHelpList.add("=== 田园生活在线购物商城 ===");
                    event.getFriend().sendMessage(shopHelpList.build());
                }
                case "/商品查看" -> {
                    if (!searchData(event.getSender().getId())) {
                        event.getFriend().sendMessage("你还没完成绑定QQ呢,所以不能使用商城任何功能！");
                        return;
                    }
                    if (!shopListIsNull()) {
                        event.getFriend().sendMessage("很抱歉,开发者还没给商城内上任何新的商品内容~");
                        return;
                    }
                    event.getFriend().sendMessage("Hi,你好欢迎来到,田园生活在线购物商城.");
                    event.getFriend().sendMessage(seeShopCommodityList());
                }
                case "/查看购买方式" -> {
                    if (!searchData(event.getSender().getId())) {
                        event.getFriend().sendMessage("你还没完成绑定QQ呢,所以不能使用商城任何功能！");
                        return;
                    }
                    MessageChainBuilder helpList = new MessageChainBuilder();
                    //操作讲解
                    helpList.add("操作方式:  " + "\n");
                    helpList.add(" /购买 xx" + "\n");
                    helpList.add(" -------------------" + "\n");
                    helpList.add("例如: " + "\n");
                    helpList.add(" /购买 600园币" + "\n");
                    helpList.add("--------------------" + "\n");
                    event.getFriend().sendMessage(helpList.build());
                }
                case "/签到消费排行榜" -> {
                    if (!searchData(event.getSender().getId())) {
                        event.getFriend().sendMessage("你还没完成绑定QQ呢,所以不能使用商城任何功能！");
                        return;
                    }
                    event.getFriend().sendMessage(seeAboutTakeRankingList());
                }
                case "/商品销售量排行榜" -> {
                    if (!searchData(event.getSender().getId())) {
                        event.getFriend().sendMessage("你还没完成绑定QQ呢,所以不能使用商城任何功能！");
                        return;
                    }
                    event.getFriend().sendMessage(commoditySalesVolumeRanKing());
                }
                case "/消费查询" -> {
                    long qq = event.getSender().getId();
                    if (!searchData(qq)) {
                        event.getFriend().sendMessage("你还没完成绑定QQ呢,所以不能使用商城任何功能！");
                        return;
                    }
                    event.getFriend().sendMessage("尊敬的" + searchPlayerName(qq) + "到目前为止,你总共消费了" + seeAboutTake(qq) + "签到次数值");
                }
            }
        }
    @EventHandler(priority = EventPriority.MONITOR)
    public void buyShop(@NotNull FriendMessageEvent event){
        if (!event.getMessage().contentToString().contains("/购买")) {
            return;
        }
          long qq = event.getSender().getId();
            if (!searchData(qq)) {
                event.getFriend().sendMessage("你还没完成绑定QQ呢,所以不能使用商城任何功能！");
                return;
            }
            StringBuilder buyThings = new StringBuilder(event.getMessage().contentToString().replace("/购买", "").trim());
            //已经绑定QQ
            if (buyThings.toString().isEmpty()) {
                event.getFriend().sendMessage("你还没有输入想买的东西呢！");
                return;
            }
            event.getFriend().sendMessage("正在执行商品的下单操作,请开始耐心等待");
            buySomeThings(event, qq, buyThings);
   }

    @SuppressWarnings("All")
    /**
     * @buySomeThings 封装方法,对于要兑换多少商品的封装
     * 由于大部分派发都是数值
     * @param Player 用于获取绑定的游戏内账户名
     * @param commodityName 商品名称
     * 玩家绑定
     * 搜索商品名
     * 商品存在
     * 有足够的签到值
     * 完成兑换
     * **/

    public void buySomeThings(@NotNull FriendMessageEvent event,Long Player,@NotNull StringBuilder commodityName) {
        /**
         * 1. 检测这个商品是否存在
         * 2. 获取商品的价格
         * 3. 获取这个商品的奖励值
         * 4. 查看是否有足够的钱
         * 5. 派发奖励
         * 6. 加入消费累计
         * 7. 增加商品的销售量
         * **/
        if (!seeAboutCommodityIsNotNull(commodityName)){
            event.getFriend().sendMessage("很抱歉,这个商品不存在");
            return;
        }
        //获取商品的价格
        long signTick = seeCommodityPrice(commodityName);
        //检查是否有足够的钱
        if (!checkIsEnoughMoney(Player,signTick)){
            event.getFriend().sendMessage("你现在只有%user_tick%次签到累计数,你需要 %tick% 次签到累计数,才可以购买！"
                    .replace("%user_tick%",String.valueOf(searchDegree(Player)))
                    .replace("%tick%",String.valueOf(signTick)));
            return;
        }
        //获取商品的奖励
        BigDecimal reward = BigDecimal.valueOf(seeCommodityReward(commodityName));
        //获取玩家名
        StringBuilder player_name = new StringBuilder(searchPlayerName(Player));
        StringBuilder shopType = new StringBuilder(seeAboutCommodityType(commodityName));
        //获取商品的类型
        switch (shopType.toString()) {
            case "货币" ->
                //派发奖品
                    StartClass.getMoneyConnect().updatePlayerMoney(reward, player_name.toString());
        }
        //扣除签到数值
        takeManySignInDegree(Player,signTick);
        //加入消费累计
        takeTotal(Player,signTick);
        //增加商品的销售量
        changeCommoditySalesVolume(seeAboutCommoditySalesVolume(commodityName),commodityName);
        //完成信息
        MessageChainBuilder finishExChange = new MessageChainBuilder();
        finishExChange.add(new At(Player));
        finishExChange.add(" 恭喜你完成了购物,购买了%commodityName%这个商品 \n".replace("%commodityName%",commodityName.toString()));
        finishExChange.add("本次总共花费了%tick%次签到次数值\n".replace("%tick%",String.valueOf(signTick)));
        switch (shopType.toString()) {
            case "货币" ->
                    finishExChange.add("商品奖励 %reward% 元,已经派送到你的%user%账户上.\n".replace("%reward%", String.valueOf(reward.doubleValue())).replace("%user%", player_name));
        }
        finishExChange.add("记得注意查收哦~");
        event.getFriend().sendMessage(finishExChange.build());
    }

    @SuppressWarnings("all")
    /**
     * @shopListIsNull 查看商城是否为空?
     * **/
    public boolean shopListIsNull(){
        boolean flag = false;
        try {
            setStatement(getConnection().createStatement());
            setResultSet(getStatement().executeQuery("SELECT commodity_id FROM " + getBotShopTableBase()));
            if (getResultSet().next()){
                flag = true;
            }
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
        return flag;
    }

    /**
     * @seeShopCommodityList 查看商城商品列表内容
     * **/
    @NotNull
    public MessageChain seeShopCommodityList(){
        MessageChainBuilder shopList = new MessageChainBuilder();
        shopList.add("商品名称 | 商品类型 | 商品价格 | 商品奖励 \n");
        try {
            setStatement(getConnection().createStatement());
            setResultSet(getStatement().executeQuery("SELECT commodity_name,commodity_type,commodity_price,commodity_reward FROM " + getBotShopTableBase() +";"));
            while (getResultSet().next()){
              shopList.add(getResultSet().getString("commodity_name") +"\t  " + getResultSet().getString("commodity_type") + "     \t" + getResultSet().getLong("commodity_price") + "    \t " + getResultSet().getDouble("commodity_reward") + "\n");
            }
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
        return shopList.build();
    }

    @SuppressWarnings("all")
    /**
     * @seeAboutCommodityIsNotNull 查看商品是否存在
     * commodity_name 商品名称
     * **/

    public boolean seeAboutCommodityIsNotNull(StringBuilder commodity_name){
        boolean flag = false;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT commodity_name FROM " + getBotShopTableBase() +"  WHERE commodity_name=?;"));
            getPreparedStatement().setObject(1,commodity_name.toString());
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
     * @seeAboutCommodityType 查看商品类型
     * @return 返回商品的类型
     * **/
    @NotNull
    public StringBuilder seeAboutCommodityType(@NotNull StringBuilder commodity_name){
        StringBuilder commodityType = new StringBuilder();
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT commodity_type FROM " + getBotShopTableBase() +"  WHERE commodity_name=?;"));
            getPreparedStatement().setObject(1,commodity_name.toString());
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()){
                commodityType.append(getResultSet().getString("commodity_type"));
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
        return commodityType;
    }

    /**
     * @seeCommodityPrice 查看商品的价格
     * **/
    public long seeCommodityPrice(@NotNull StringBuilder commodity_name){
        long commodity_price =0;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT commodity_price FROM " + getBotShopTableBase() + " WHERE commodity_name=?;"));
            getPreparedStatement().setObject(1,commodity_name.toString());
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()){
                commodity_price = getResultSet().getLong("commodity_price");
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
        return commodity_price;
    }

    /**
     * @seeCommodityReward 查看商品奖励值
     * **/
    public double seeCommodityReward(@NotNull StringBuilder commodity_name){
        double commodity_reward = 0.0;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT commodity_reward FROM " + getBotShopTableBase() +" WHERE commodity_name=?;"));
            getPreparedStatement().setObject(1,commodity_name.toString());
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()){
                commodity_reward = getResultSet().getDouble("commodity_reward");
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
        return commodity_reward;
    }

    @SuppressWarnings("ALL")
    /**
     * @commoditySalesVolume 商品销售量数据排行
     * **/
    @NotNull
    public MessageChain commoditySalesVolumeRanKing(){
        MessageChainBuilder ranKingList = new MessageChainBuilder();
        ranKingList.add("商品名称 | 商品销售量 " + "\n");
        try {
            setStatement(getConnection().createStatement());
            setResultSet(getStatement().executeQuery("SELECT commodity_name,commodity_salesVolume FROM " + getBotShopTableBase() +" ORDER BY commodity_salesVolume DESC LIMIT 10;"));
            while (getResultSet().next()){
                ranKingList.add(getResultSet().getString("commodity_name") + "\t      " + getResultSet().getLong("commodity_salesVolume") + " 次\n");
            }
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
        return ranKingList.build();
    }

    /**
     * @changeCommoditySalesVolume 更改商品的销售量
     * **/
    public void changeCommoditySalesVolume(Long commodity_salesVolume, @NotNull StringBuilder commodityName){
        try {
            long changeValue = commodity_salesVolume + 1;
            setPreparedStatement(getConnection().prepareStatement("UPDATE " + getBotShopTableBase() + " SET commodity_salesVolume=? WHERE commodity_name=?;"));
            getPreparedStatement().setObject(1,changeValue);
            getPreparedStatement().setObject(2,commodityName.toString());
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
     * @seeAboutCommoditySalesVolume 查看商品的销售量
     * **/
    public long seeAboutCommoditySalesVolume(@NotNull StringBuilder commodityName){
        long SalesVolume = 0L;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT commodity_salesVolume FROM " + getBotShopTableBase() +" WHERE commodity_name=?;"));
            getPreparedStatement().setObject(1,commodityName.toString());
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()){
                if (getResultSet().getLong("commodity_salesVolume") == 0L){
                    return SalesVolume;
                }
                SalesVolume = getResultSet().getLong("commodity_salesVolume");
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
        return SalesVolume;
    }

    /**
     * @checkIsEnoughMoney 查看是否有足够的签到次数进行购买商店商品
     * **/
    public boolean checkIsEnoughMoney(Long bot_qq,Long shop_signDegree){
        boolean flag = false;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT bot_signDegree FROM " + getBotTableBase() +" WHERE bot_qq=?;"));
            getPreparedStatement().setObject(1,bot_qq);
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()){
                if (getResultSet().getInt("bot_signDegree") >= shop_signDegree){
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
     * @takeManySignInDegree 扣除指定的签到次数值
     * **/
    public void takeManySignInDegree(Long bot_qq,Long shop_signDegree)  {
        long tick = searchDegree(bot_qq) - shop_signDegree;
        try {
            setPreparedStatement(getConnection().prepareStatement("UPDATE " + getBotShopTableBase() + " SET bot_signDegree=? WHERE bot_qq=?;"));
            getPreparedStatement().setObject(1,tick);
            getPreparedStatement().setObject(2,bot_qq);
            getPreparedStatement().execute();
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
    }

    @SuppressWarnings("all")
    /**
     * @takeTotal 消费累加
     * 指的是 消费累加  = 查询的消费记录 + 本次消费
     * **/
    public void takeTotal(Long bot_qq,Long shop_signDegree) {
        try {
            long total = seeAboutTake(bot_qq) + shop_signDegree;
            setPreparedStatement(getConnection().prepareStatement("UPDATE " + getBotTableBase() +" SET bot_signConsume=? WHERE bot_qq=?;"));
            getPreparedStatement().setObject(1,total);
            getPreparedStatement().setObject(2,bot_qq);
            getPreparedStatement().execute();
            getPreparedStatement().close();
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
     * @seeAbout 查看签到次数玩家消费总值
     * **/
    public long seeAboutTake(Long bot_qq)  {
        long takeTotal = 0;
        try {
            setPreparedStatement(getConnection().prepareStatement("SELECT bot_signConsume FROM " + getBotTableBase() + " WHERE bot_qq=?;"));
            getPreparedStatement().setObject(1,bot_qq);
            setResultSet(getPreparedStatement().executeQuery());
            if (getResultSet().next()){
                if (getResultSet().getInt("bot_signConsume") == 0) {
                    return takeTotal;
                }
                takeTotal = getResultSet().getLong("bot_signConsume");
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
        return takeTotal;
    }

    @SuppressWarnings("all")
    /**
     * @seeAboutTakeRanKingList 查看玩家签到消费记录排行榜
     * **/
    @NotNull
    public MessageChain seeAboutTakeRankingList(){
        MessageChainBuilder takeRanKingList = new MessageChainBuilder();
        takeRanKingList.add("=== 签到次数消费排行榜 ===" + "\n");
        try {
            setStatement(getConnection().createStatement());
            setResultSet(getStatement().executeQuery("SELECT bot_playerName,bot_signConsume FROM " +getBotTableBase() +" ORDER BY bot_signConsume DESC LIMIT 10;"));
            while (getResultSet().next()){
                takeRanKingList.add(getResultSet().getString("bot_playerName") + " " + getResultSet().getLong("bot_signConsume") + " 次\n");
            }
            takeRanKingList.add("========  TOP10 ==========");
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
        return takeRanKingList.build();
    }

}
