package space.hopelight.miraibotgift.Task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import space.hopelight.miraibotgift.MysqlBaseData.BotDataConnect;
import space.hopelight.miraibotgift.StartClass;


public class BotSeeAboutTask extends BotDataConnect implements Job {
    public BotSeeAboutTask() {
        super("");
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        seeAboutTable();
        StartClass.INSTANCE.developerMsg("查询Bot数据表工作完成~");
    }

}
