package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.unpam.view.PesanDialog;

public class Pekerjaan {
    private String kodePekerjaan, namaPekerjaan;
    private int jumlahTugas;
    private String pesan;
    private Object[][] list;
    private final Koneksi koneksi = new Koneksi();
    private final PesanDialog pesanDialog = new PesanDialog();
    
    public String getKodePekerjaan() {
        return kodePekerjaan;
    }
    
    public void setKodePekerjaan(String kodePekerjaan) {
        this.kodePekerjaan = kodePekerjaan;
    }
    
    public String getNamaPekerjaan() {
        return namaPekerjaan;
    }
    
    public void setNamaPekerjaan(String namaPekerjaan) {
        this.namaPekerjaan = namaPekerjaan;
    }
    
    public int getJumlahTugas() {
        return jumlahTugas;
    }
    
    public void setJumlahTugas(int jumlahTugas) {
        this.jumlahTugas = jumlahTugas;
    }
    
    public String getPesan() {
        return pesan;
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
            Statement sta = null;
            
            try {
                simpan = true;
                SQLStatement = "insert into tbpekerjaan values('" + kodePekerjaan + "','" + namaPekerjaan + "'," + jumlahTugas + ")";
                sta = connection.createStatement();
                jumlahSimpan = sta.executeUpdate(SQLStatement);
            } catch (SQLException ex) {
                adakesalahan = true;
                pesan = "Tidak dapat membuka tabel tbpekerjaan\n" + ex + "\n" + SQLStatement;
            }
            
            if (simpan) {
                if (jumlahSimpan < 1) {
                    adakesalahan = true;
                    pesan = "Gagal menyimpan data pekerjaan";
                }
            }
            
            try {
                if (sta != null) {
                    sta.close();
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
                SQLStatement = "update tbpekerjaan set " + kolom + " = ? where kodePekerjaan = ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, data[0]);
                preparedStatement.setString(2, data[1]);
                jumlahUpdate = preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                adakesalahan = true;
                pesan = "Tidak dapat membuka tabel tbpekerjaan\n" + ex + "\n" + SQLStatement;
            }
            if (!adakesalahan) {
                if (jumlahUpdate < 1) {
                    adakesalahan = true;
                    pesan = "Gagal memperbaharui data pekerjaan";
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

    public boolean hapus(String kodePekerjaan) {
        boolean adakesalahan = false;
        Connection connection;
        if ((connection = koneksi.getConnection()) != null) {
            int jumlahHapus = 0;
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;
            try {
                SQLStatement = "delete from tbpekerjaan where kodePekerjaan = ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, kodePekerjaan);
                jumlahHapus = preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                adakesalahan = true;
                pesan = "Tidak dapat membuka tabel tbpekerjaan\n" + ex + "\n" + SQLStatement;
            }
            if (!adakesalahan) {
                if (jumlahHapus < 1) {
                    adakesalahan = true;
                    pesan = "Gagal menghapus data pekerjaan";
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

    public boolean baca(String kodePekerjaan) {
        boolean ditemukan = false;
        Connection connection;
        if ((connection = koneksi.getConnection()) != null) {
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                SQLStatement = "select kodePekerjaan, namaPekerjaan, jumlahTugas from tbpekerjaan where kodePekerjaan = ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, kodePekerjaan);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    ditemukan = true;
                    this.kodePekerjaan = resultSet.getString("kodePekerjaan");
                    this.namaPekerjaan = resultSet.getString("namaPekerjaan");
                    this.jumlahTugas = resultSet.getInt("jumlahTugas");
                }
            } catch (SQLException ex) {
                pesan = "Tidak dapat membuka tabel tbpekerjaan\n" + ex + "\n" + SQLStatement;
            }
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                connection.close();
            } catch (SQLException ex) {}
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
                SQLStatement = "select kodePekerjaan, namaPekerjaan, jumlahTugas from tbpekerjaan order by kodePekerjaan limit ?, ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setInt(1, mulai);
                preparedStatement.setInt(2, jumlah);
                resultSet = preparedStatement.executeQuery();
                
                java.util.List<Object[]> temp = new java.util.ArrayList<Object[]>();
                while (resultSet.next()) {
                    Object[] row = new Object[3];
                    row[0] = resultSet.getString("kodePekerjaan");
                    row[1] = resultSet.getString("namaPekerjaan");
                    row[2] = resultSet.getInt("jumlahTugas");
                    temp.add(row);
                }
                list = temp.toArray(new Object[temp.size()][]);
            } catch (SQLException ex) {
                pesan = "Tidak dapat membuka tabel tbpekerjaan\n" + ex + "\n" + SQLStatement;
            }
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                connection.close();
            } catch (SQLException ex) {}
        }
    }
}
