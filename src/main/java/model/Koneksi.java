package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/dbaplikasigajikaryawan";
    private static final String user = "root";
    private static final String password = "";
    
    private Connection connection;
    private String pesanKesalahan;

    public String getPesanKesalahan() {
        return pesanKesalahan;
    }

    public Connection getConnection() {
        connection = null;
        pesanKesalahan = "";
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException ex) {
            pesanKesalahan = "JDBC Driver tidak ditemukan atau rusak\n" + ex;
        } catch (SQLException ex) {
            pesanKesalahan = "Koneksi ke database gagal\n" + ex;
        }
        return connection;
    }
}