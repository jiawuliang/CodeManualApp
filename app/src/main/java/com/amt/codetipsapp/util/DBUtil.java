package com.amt.codetipsapp.util;

import android.util.Log;

import com.amt.codetipsapp.consts.DBConst;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static android.content.ContentValues.TAG;

/**
 * 数据库工具类：连接数据库用、获取数据库数据用、
 * 相关操作数据库的方法均可写在该类
 */
public class DBUtil {

    private static Connection conn = null;

    static class DBUtilSingle {
        private static DBUtil instance = new DBUtil();
    }

    private DBUtil() {
        super();
        if (conn == null) {
            conn = getConn();
        }
    }

    private Connection getConn() {
        Connection connection = null;
        try {
            Class.forName(DBConst.DRIVER);// 动态加载类
            // 尝试建立到给定数据库URL的连接
            connection = DriverManager.getConnection(DBConst.URL, DBConst.USERNAME, DBConst.PASSWORD);
            Log.d("DBUtil.getConn", "getConn: 连接Mysql成功！");
        } catch (Exception e) {
            Log.d("DBUtil.getConn", "getConn: 连接Mysql失败");
            e.printStackTrace();
        }

        return connection;
    }

    public static DBUtil getInstance() {
        return DBUtilSingle.instance;
    }

    public ResultSet executeQuery(String selectSql) {

        try {
            if (conn != null) {
                Statement statement = conn.createStatement();

                if (statement != null) {
                    // 执行sql查询语句并返回结果集
                    return statement.executeQuery(selectSql);
                }
            } else {
                Log.d(TAG, "selectWithSql: conn为空，连接异常！");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int executeUpdate(String updateSql) {
        return 0;
    }

}
