package space.hopelight.miraibotgift.Funcation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomChance {
    private int maxValue;
    private double prizeValue;
    private double chance;
    private Random random;

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public double getPrizeValue() {
        return prizeValue;
    }

    public void setPrizeValue(double prizeValue) {
        this.prizeValue = prizeValue;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * @RandomChance 随机几率的对象,在初始化的时候需要给它设置最大值,普通数值,概率。
     * **/
    public RandomChance(int maxValue, double prizeValue, double chance){
        setRandom(new Random());
        setMaxValue(maxValue);
        setPrizeValue(prizeValue);
        setChance(chance);
    }

    /**
     * @chanceValue 用于生成数值的概率
     * **/
    public BigDecimal chanceValue(){
        List<Double> value = new ArrayList<>(Collections.singletonList(getPrizeValue()));
        double num;
        // 0 - 300
        num = random.nextInt(getMaxValue());
        value.add(num);
        //取区间值 ]最大的时候就判断为中间
        if (num != chance) {
            return BigDecimal.valueOf(value.get(1));
        }
        return BigDecimal.valueOf(value.get(0));
    }

}
