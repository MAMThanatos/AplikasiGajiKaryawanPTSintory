package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperExportManager;

public class Gaji {
    private String ktp = "";
    private String kodePekerjaan = "";
    private double gajiBersih = 0;
    private double gajiKotor = 0;
    private double tunjangan = 0;
    private String pesan = "";
    private Object[][] list = null;
    private final Koneksi koneksi = new Koneksi();
    private byte[] pdfAsBytes = null;

    // Getters and Setters
    public String getKtp() { return ktp; }
    public void setKtp(String ktp) { this.ktp = ktp; }

    public String getKodePekerjaan() { return kodePekerjaan; }
    public void setKodePekerjaan(String kodePekerjaan) { this.kodePekerjaan = kodePekerjaan; }

    public double getGajiBersih() { return gajiBersih; }
    public void setGajiBersih(double gajiBersih) { this.gajiBersih = gajiBersih; }

    public double getGajiKotor() { return gajiKotor; }
    public void setGajiKotor(double gajiKotor) { this.gajiKotor = gajiKotor; }

    public double getTunjangan() { return tunjangan; }
    public void setTunjangan(double tunjangan) { this.tunjangan = tunjangan; }

    public String getPesan() { return pesan; }
    public void setPesan(String pesan) { this.pesan = pesan; }

    public Object[][] getList() { return list; }

    // Save salary details (delete first to update, then insert)
    public boolean simpan() {
        boolean sukses = false;
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            String sqlDelete = "DELETE FROM tbgaji WHERE ktp = ?";
            String sqlInsert = "INSERT INTO tbgaji (ktp, kodepekerjaan, gajibersih, gajikotor, tunjangan) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psDelete = null;
            PreparedStatement psInsert = null;
            try {
                // Delete existing first
                psDelete = connection.prepareStatement(sqlDelete);
                psDelete.setString(1, ktp);
                psDelete.executeUpdate();

                // Insert new record
                psInsert = connection.prepareStatement(sqlInsert);
                psInsert.setString(1, ktp);
                psInsert.setString(2, kodePekerjaan);
                psInsert.setDouble(3, gajiBersih);
                psInsert.setDouble(4, gajiKotor);
                psInsert.setDouble(5, tunjangan);
                psInsert.executeUpdate();
                
                sukses = true;
                pesan = "Data gaji berhasil disimpan";
            } catch (SQLException ex) {
                sukses = false;
                pesan = "Gagal menyimpan data gaji: " + ex.getMessage();
            } finally {
                try { if (psDelete != null) psDelete.close(); } catch (SQLException e) {}
                try { if (psInsert != null) psInsert.close(); } catch (SQLException e) {}
                try { connection.close(); } catch (SQLException e) {}
            }
        } else {
            sukses = false;
            pesan = "Koneksi ke database gagal: " + koneksi.getPesanKesalahan();
        }
        return sukses;
    }

    // Delete record by KTP
    public boolean hapus(String ktp) {
        boolean sukses = false;
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            String sql = "DELETE FROM tbgaji WHERE ktp = ?";
            PreparedStatement ps = null;
            try {
                ps = connection.prepareStatement(sql);
                ps.setString(1, ktp);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    sukses = true;
                    pesan = "Data gaji berhasil dihapus";
                } else {
                    sukses = false;
                    pesan = "Data gaji tidak ditemukan";
                }
            } catch (SQLException ex) {
                sukses = false;
                pesan = "Gagal menghapus data gaji: " + ex.getMessage();
            } finally {
                try { if (ps != null) ps.close(); } catch (SQLException e) {}
                try { connection.close(); } catch (SQLException e) {}
            }
        } else {
            sukses = false;
            pesan = "Koneksi ke database gagal: " + koneksi.getPesanKesalahan();
        }
        return sukses;
    }

    // Read record by KTP
    public boolean baca(String ktp) {
        boolean ditemukan = false;
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            String sql = "SELECT * FROM tbgaji WHERE ktp = ?";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = connection.prepareStatement(sql);
                ps.setString(1, ktp);
                rs = ps.executeQuery();
                if (rs.next()) {
                    this.ktp = rs.getString("ktp");
                    this.kodePekerjaan = rs.getString("kodepekerjaan");
                    this.gajiBersih = rs.getDouble("gajibersih");
                    this.gajiKotor = rs.getDouble("gajikotor");
                    this.tunjangan = rs.getDouble("tunjangan");
                    ditemukan = true;
                    pesan = "Data gaji ditemukan";
                } else {
                    ditemukan = false;
                    pesan = "Data gaji tidak ditemukan untuk KTP " + ktp;
                }
            } catch (SQLException ex) {
                ditemukan = false;
                pesan = "Gagal membaca data gaji: " + ex.getMessage();
            } finally {
                try { if (rs != null) rs.close(); } catch (SQLException e) {}
                try { if (ps != null) ps.close(); } catch (SQLException e) {}
                try { connection.close(); } catch (SQLException e) {}
            }
        } else {
            ditemukan = false;
            pesan = "Koneksi ke database gagal: " + koneksi.getPesanKesalahan();
        }
        return ditemukan;
    }

    // Read all records for listing / pagination
    public boolean bacaSemua(int mulai, int jumlah) {
        boolean sukses = false;
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            String sql = "SELECT g.ktp, k.nama, k.ruang, g.kodepekerjaan, p.namapekerjaan, p.jumlahtugas, g.gajibersih, g.gajikotor, g.tunjangan " +
                         "FROM tbgaji g " +
                         "LEFT JOIN tbkaryawan k ON g.ktp = k.ktp " +
                         "LEFT JOIN tbpekerjaan p ON g.kodepekerjaan = p.kodepekerjaan " +
                         "ORDER BY g.ktp ASC LIMIT ?, ?";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ps.setInt(1, mulai);
                ps.setInt(2, jumlah);
                rs = ps.executeQuery();
                
                rs.last();
                int rowCount = rs.getRow();
                list = new Object[rowCount][9];
                rs.beforeFirst();
                
                int idx = 0;
                while (rs.next()) {
                    list[idx][0] = rs.getString("ktp");
                    list[idx][1] = rs.getString("nama");
                    list[idx][2] = rs.getInt("ruang");
                    list[idx][3] = rs.getString("kodepekerjaan");
                    list[idx][4] = rs.getString("namapekerjaan");
                    list[idx][5] = rs.getInt("jumlahtugas");
                    list[idx][6] = rs.getDouble("gajibersih");
                    list[idx][7] = rs.getDouble("gajikotor");
                    list[idx][8] = rs.getDouble("tunjangan");
                    idx++;
                }
                sukses = true;
            } catch (SQLException ex) {
                sukses = false;
                pesan = "Gagal membaca daftar gaji: " + ex.getMessage();
            } finally {
                try { if (rs != null) rs.close(); } catch (SQLException e) {}
                try { if (ps != null) ps.close(); } catch (SQLException e) {}
                try { connection.close(); } catch (SQLException e) {}
            }
        } else {
            sukses = false;
            pesan = "Koneksi ke database gagal: " + koneksi.getPesanKesalahan();
        }
        return sukses;
    }

    public byte[] getPdfAsBytes() {
        return pdfAsBytes;
    }

    public boolean cetakLaporan(String opsi, String ktp, int ruang, String format, String pathReport) {
        boolean sukses = false;
        Connection connection = koneksi.getConnection();
        if (connection != null) {
            try {
                JasperReport jasperReport = JasperCompileManager.compileReport(pathReport);
                
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("opsi", opsi);
                parameters.put("ktp", ktp);
                parameters.put("ruang", ruang);
                
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
                pdfAsBytes = JasperExportManager.exportReportToPdf(jasperPrint);
                sukses = true;
            } catch (Exception ex) {
                sukses = false;
                pesan = "Gagal cetak laporan: " + ex.getMessage();
                ex.printStackTrace();
            } finally {
                try { connection.close(); } catch (SQLException e) {}
            }
        } else {
            sukses = false;
            pesan = "Koneksi ke database gagal: " + koneksi.getPesanKesalahan();
        }
        return sukses;
    }
}
