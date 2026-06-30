package it.univaq.disim.progettoagile.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
// Sostituisci la riga dell'URL con questa:
private static final String URL = "jdbc:mysql://localhost:3306/progetto_agile_pianificatore_attivita";
    private static final String USER = "root";
    
    private static final String PASSWORD = "12345";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trovato nel progetto.", e);
        }
    }
}