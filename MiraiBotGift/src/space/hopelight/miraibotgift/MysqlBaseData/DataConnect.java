package space.hopelight.miraibotgift.MysqlBaseData;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.SimpleListenerHost;
import org.jetbrains.annotations.NotNull;
import space.hopelight.miraibotgift.StartClass;

import java.sql.*;

public class DataConnect extends SimpleListenerHost {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String HOST = "mysql://localhost:3306/";
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private Long StartTime;

    public static String getHost() {
        return HOST;
    }

    public static String getJdbcDriver() {
        return JDBC_DRIVER;
    }

    public Long getStartTime() {
        return StartTime;
    }

    public void setStartTime(Long startTime) {
        StartTime = startTime;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public void setPreparedStatement(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception){
        // 处理事件处理时抛出的异常
    }

    /**
     * @loadMysqlDrive 用于数据库的的驱动加载
     * **/
    public static void loadMysqlDrive(){
        try {
            Class.forName(getJdbcDriver()); //加载驱动
            StartClass.INSTANCE.developerMsg("加载驱动成功~");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @connectMysql 用于进行数据库的连接
     * **/
    public static Connection connectMysql(String url, String user, String passWord) {
        try {
            return DriverManager.getConnection(url,user,passWord);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @closeMysql 用于数据库的关闭
     * **/
    public static void closeMysql(Connection connection){
        try {
            if (connection == null || connection.isClosed())
                return;
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
