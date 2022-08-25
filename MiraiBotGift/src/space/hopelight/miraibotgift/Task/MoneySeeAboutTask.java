package space.hopelight.miraibotgift.Task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import space.hopelight.miraibotgift.MysqlBaseData.MoneyConnect;
import space.hopelight.miraibotgift.StartClass;

public class MoneySeeAboutTask extends MoneyConnect implements Job {
    public MoneySeeAboutTask(){
        super("");
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        seeAboutTable();
        StartClass.INSTANCE.developerMsg("查询Money数据表工作完成~");
    }

}
