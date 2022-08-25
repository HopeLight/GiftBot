package space.hopelight.miraibotgift.Task;

import net.mamoe.mirai.message.data.AtAll;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import space.hopelight.miraibotgift.MysqlBaseData.BotDataConnect;
import space.hopelight.miraibotgift.StartClass;

public class SignInTask extends BotDataConnect implements Job {

    public SignInTask(){
        super("");
    }

    private static final MessageChainBuilder singleMessages = new MessageChainBuilder();


    static {
        singleMessages.add(AtAll.INSTANCE);
        singleMessages.add("\n醒醒、醒醒,别睡了,新的一天开始了,又可以开始签到啦~");
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        changeNewSignStatus();
        StartClass.INSTANCE.groupMsg(singleMessages.build());
    }

}
