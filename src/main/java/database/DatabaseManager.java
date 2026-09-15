/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

/**
 *
 * @author edoar
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    // connessione al db
    private static final String URL = "jdbc:mysql://localhost:3306/progetto_agile_pianificatore_attivita?noAccessToProcedureBodies=true&serverTimezone=Europe/Rome";
    private static final String USER = "root"; //username mysql
    private static final String PASSWORD = "12345"; // password mysql

    
    //  metodo per la cnessione al db
     
    public static Connection getConnection() throws SQLException {
        // DriverManager restituisce un oggetto Connection pronto all'uso
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


