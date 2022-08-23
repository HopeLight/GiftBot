package space.hopelight.mcbotgift.MysqlBaseData;

import space.hopelight.mcbotgift.StartClass;

import java.sql.*;

public abstract class MysqlConnect {
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private static final String HOST = StartClass.getStartClass().getConfig().getString("mysqlSetting.host");
    private static final String SSL = StartClass.getStartClass().getConfig().getString("mysqlSetting.ssl");
    public static String getHost() {
        return HOST;
    }

    public static String getSSL() {
        return SSL;
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

    /**
     * @connectMysql 用于进行数据库的连接
     * **/
    public static Connection connectMysql(String url,String user,String passWord){
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
            if (connection == null || connection.isClosed()){
                return;
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
