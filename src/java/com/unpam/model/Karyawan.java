package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.unpam.view.PesanDialog;

public class Karyawan {
    private String nip, nama, password;
    private int ruang;
    private String pesan;
    private Object[][] list;
    private final Koneksi koneksi = new Koneksi();
    private final PesanDialog pesanDialog = new PesanDialog();
    
    public String getNip() {
        return nip;
    }
    
    public void setNip(String nip) {
        this.nip = nip;
    }
    
    public String getNama() {
        return nama;
    }
    
    public void setNama(String nama) {
        this.nama = nama;
    }
    
    public int getRuang() {
        return ruang;
    }
    
    public void setRuang(int ruang) {
        this.ruang = ruang;
    }
    
    public String getPesan() {
        return pesan;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Object[][] getList() {
        return list;
    }
    
    public void setList(Object[][] list) {
        this.list = list;
    }
    
    public boolean simpan() {
        boolean adakesalahan = false;
        Connection connection;
        
        if ((connection = koneksi.getConnection()) != null) {
            int jumlahSimpan = 0;
            boolean simpan = false;
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;
            
            try {
                simpan = true;
                SQLStatement = "insert into tbkaryawan(ktp, nama, ruang, password) values(?,?,?,?)";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, nip);
                preparedStatement.setString(2, nama);
                preparedStatement.setInt(3, ruang);
                preparedStatement.setString(4, password);
                
                jumlahSimpan = preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                adakesalahan = true;
                pesan = "Tidak dapat membuka tabel tbkaryawan\n" + ex + "\n" + SQLStatement;
            }
            
            if (simpan) {
                if (jumlahSimpan < 1) {
                    adakesalahan = true;
                    pesan = "Gagal menyimpan data karyawan";
                }
            }
            
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.close();
            } catch (SQLException ex) {}
            
        } else {
            adakesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }
        return adakesalahan;
    }

    public boolean update(String kolom, String[] data) {
        boolean adakesalahan = false;
        Connection connection;
        if ((connection = koneksi.getConnection()) != null) {
            int jumlahUpdate = 0;
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;
            try {
                String dbKolom = kolom.equalsIgnoreCase("nip") ? "ktp" : kolom;
                SQLStatement = "update tbkaryawan set " + dbKolom + " = ? where ktp = ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, data[0]);
                preparedStatement.setString(2, data[1]);
                jumlahUpdate = preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                adakesalahan = true;
                pesan = "Tidak dapat membuka tabel tbkaryawan\n" + ex + "\n" + SQLStatement;
            }
            if (!adakesalahan) {
                if (jumlahUpdate < 1) {
                    adakesalahan = true;
                    pesan = "Gagal memperbaharui data karyawan";
                }
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.close();
            } catch (SQLException ex) {}
        } else {
            adakesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }
        return adakesalahan;
    }

    public boolean hapus(String nip) {
        boolean adakesalahan = false;
        Connection connection;
        if ((connection = koneksi.getConnection()) != null) {
            int jumlahHapus = 0;
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;
            try {
                SQLStatement = "delete from tbkaryawan where ktp = ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, nip);
                jumlahHapus = preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                adakesalahan = true;
                pesan = "Tidak dapat membuka tabel tbkaryawan\n" + ex + "\n" + SQLStatement;
            }
            if (!adakesalahan) {
                if (jumlahHapus < 1) {
                    adakesalahan = true;
                    pesan = "Gagal menghapus data karyawan";
                }
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                connection.close();
            } catch (SQLException ex) {}
        } else {
            adakesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }
        return adakesalahan;
    }

    public boolean baca(String nip) {
        boolean ditemukan = false;
        Connection connection;
        if ((connection = koneksi.getConnection()) != null) {
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                SQLStatement = "select ktp, nama, ruang, password from tbkaryawan where ktp = ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, nip);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    ditemukan = true;
                    this.nip = resultSet.getString("ktp");
                    this.nama = resultSet.getString("nama");
                    this.ruang = resultSet.getInt("ruang");
                    this.password = resultSet.getString("password");
                }
            } catch (SQLException ex) {
                pesan = "Tidak dapat membuka tabel tbkaryawan\n" + ex + "\n" + SQLStatement;
            }
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                connection.close();
            } catch (SQLException ex) {}
        } else {
            pesan = "Koneksi database gagal: " + koneksi.getPesanKesalahan();
        }
        return ditemukan;
    }

    public void bacaSemua(int mulai, int jumlah) {
        Connection connection;
        if ((connection = koneksi.getConnection()) != null) {
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                SQLStatement = "select ktp, nama, ruang, password from tbkaryawan order by ktp limit ?, ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setInt(1, mulai);
                preparedStatement.setInt(2, jumlah);
                resultSet = preparedStatement.executeQuery();
                
                java.util.List<Object[]> temp = new java.util.ArrayList<Object[]>();
                while (resultSet.next()) {
                    Object[] row = new Object[4];
                    row[0] = resultSet.getString("ktp");
                    row[1] = resultSet.getString("nama");
                    row[2] = resultSet.getInt("ruang");
                    row[3] = resultSet.getString("password");
                    temp.add(row);
                }
                list = temp.toArray(new Object[temp.size()][]);
            } catch (SQLException ex) {
                pesan = "Tidak dapat membuka tabel tbkaryawan\n" + ex + "\n" + SQLStatement;
            }
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                connection.close();
            } catch (SQLException ex) {}
        }
    }
}
