package com.codex.framework.ORM;

import com.codex.framework.ORM.utils.Print;

import java.sql.*;

public class DatabaseConnection {
    private static DatabaseConnection instance ;
    private Connection connection ;
    private static final String URL = "jdbc:postgresql://localhost:5432/red_indians_night";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "admin";
    private static final String DRIVER = "org.postgresql.Driver";

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
