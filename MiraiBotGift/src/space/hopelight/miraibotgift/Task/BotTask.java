package space.hopelight.miraibotgift.Task;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @SignTask 用于每天凌晨进行执行,把flag设置为true
 * 如果flag已经是true的就跳过,如果是false的话再设置。
 * **/
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

    public BotTask(String eveyDay) throws SchedulerException {
        //创建scheduler
        setScheduler(StdSchedulerFactory.getDefaultScheduler());
        //对数据库查询的操作
        JobDetail botSeeJob = JobBuilder.newJob(BotSeeAboutTask.class).withIdentity("botCron").storeDurably(true).build();
        JobDetail severSeeJob = JobBuilder.newJob(ServerSeeAboutTask.class).withIdentity("serverCron").storeDurably(true).build();
        //查表触发器
        CronTrigger botTrigger = TriggerBuilder.newTrigger().withIdentity("botTrigger").withSchedule(CronScheduleBuilder.cronSchedule(eveyDay)).build();
        CronTrigger serverTrigger = TriggerBuilder.newTrigger().withIdentity("serverTrigger").withSchedule(CronScheduleBuilder.cronSchedule(eveyDay)).build();
        //对表查询任务
        getScheduler().scheduleJob(botSeeJob,botTrigger);
        getScheduler().scheduleJob(severSeeJob,serverTrigger);
        //启动scheduler
        getScheduler().start();
    }

}
