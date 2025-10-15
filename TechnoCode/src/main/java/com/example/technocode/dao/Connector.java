package com.example.technocode.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/technotg?useTimezone=true&serverTimezone=UTC", "technocode", "pass123");
    }
}
