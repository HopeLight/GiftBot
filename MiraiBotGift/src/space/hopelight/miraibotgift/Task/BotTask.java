package space.hopelight.miraibotgift.Task;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

@SuppressWarnings("All")
public class BotTask {
    private static Scheduler scheduler;
    private static CronTrigger signTrigger;

    public Scheduler getScheduler() {
        return scheduler;
    }
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public BotTask(String newDay,String eveyDay) throws SchedulerException {
        //创建scheduler
        setScheduler(StdSchedulerFactory.getDefaultScheduler());
        //对签到标记刷新
        JobDetail signInJob = JobBuilder.newJob(SignInTask.class).withIdentity("signCron").storeDurably(true).build();
        //对数据库查询的操作
        JobDetail botSeeJob = JobBuilder.newJob(BotSeeAboutTask.class).withIdentity("botCron").storeDurably(true).build();
        JobDetail severSeeJob = JobBuilder.newJob(ServerSeeAboutTask.class).withIdentity("serverCron").storeDurably(true).build();
        JobDetail moneySeeJob = JobBuilder.newJob(MoneySeeAboutTask.class).withIdentity("moneyCron").storeDurably(true).build();
        //每日任务触发器
        CronTrigger signTrigger = TriggerBuilder.newTrigger().withIdentity("signTrigger").withSchedule(CronScheduleBuilder.cronSchedule(newDay)).build();
        //查表触发器
        CronTrigger botTrigger = TriggerBuilder.newTrigger().withIdentity("botTrigger").withSchedule(CronScheduleBuilder.cronSchedule(eveyDay)).build();
        CronTrigger serverTrigger = TriggerBuilder.newTrigger().withIdentity("serverTrigger").withSchedule(CronScheduleBuilder.cronSchedule(eveyDay)).build();
        CronTrigger moneyTrigger = TriggerBuilder.newTrigger().withIdentity("moneyTrigger").withSchedule(CronScheduleBuilder.cronSchedule(eveyDay)).build();
        //对表查询任务
        getScheduler().scheduleJob(signInJob,signTrigger);
        getScheduler().scheduleJob(botSeeJob,botTrigger);
        getScheduler().scheduleJob(severSeeJob,serverTrigger);
        getScheduler().scheduleJob(moneySeeJob,moneyTrigger);
        //启动scheduler
        getScheduler().start();
    }

}
