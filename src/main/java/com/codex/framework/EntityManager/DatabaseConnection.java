package com.codex.framework.EntityManager;

import com.codex.framework.EntityManager.utils.Print;
import com.codex.framework.EntityManager.utils.env;

import java.sql.*;

public class DatabaseConnection {
    private static DatabaseConnection instance ;
    private Connection connection ;
    private static final String URL = env.get("DB_URL");
    private static final String USERNAME = env.get("DB_USERNAME");
    private static final String PASSWORD = env.get("DB_PASSWORD");
    private static final String DRIVER = env.get("DB_DRIVER");

    private DatabaseConnection() throws SQLException{
        try {
            Class.forName(DRIVER);
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Print.log("Connection established.");
        }catch (ClassNotFoundException e){
            Print.log("Database Connection Creation Failed : " + e.getMessage());
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection (){
        return connection;
    }
    public static boolean closeConnection() {
        if (instance == null) {
            return false;
        }

        try {
            instance.getConnection().close();
            instance = null;
            return true;
        } catch (SQLException e) {
            Print.log(e.toString());
            return false;
        }
    }
}
