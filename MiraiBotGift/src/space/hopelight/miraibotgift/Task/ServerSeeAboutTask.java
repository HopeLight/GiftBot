package space.hopelight.miraibotgift.Task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import space.hopelight.miraibotgift.MysqlBaseData.ServerConnect;
import space.hopelight.miraibotgift.StartClass;

public class ServerSeeAboutTask extends ServerConnect implements Job {
    public ServerSeeAboutTask(){
        super("");
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        seeAboutTable();
        StartClass.INSTANCE.developerMsg("查询Server数据表工作完成~");
    }

}
