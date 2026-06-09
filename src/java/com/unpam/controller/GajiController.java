package com.unpam.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.unpam.model.Gaji;
import com.unpam.model.Karyawan;
import com.unpam.model.Pekerjaan;
import com.unpam.view.MainForm;

@WebServlet(name = "GajiController", urlPatterns = {"/Gaji", "/GajiController"})
public class GajiController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(true);
        String username = "";
        try {
            username = session.getAttribute("username").toString();
        } catch (Exception ex) {}

        if (username == null || username.equals("")) {
            response.sendRedirect("./index.jsp");
            return;
        }

        Karyawan karyawan = new Karyawan();
        Pekerjaan pekerjaan = new Pekerjaan();
        Gaji gaji = new Gaji();

        // Get HTTP Parameters
        String tombol = request.getParameter("tombol");
        String tombolKtp = request.getParameter("tombolKtp");
        String tombolPekerjaan = request.getParameter("tombolPekerjaan");
        
        String ktp = request.getParameter("ktp");
        String nama = request.getParameter("nama");
        String ruang = request.getParameter("ruang");
        
        String kodePekerjaan = request.getParameter("kodePekerjaan");
        String namaPekerjaan = request.getParameter("namaPekerjaan");
        String jumlahTugas = request.getParameter("jumlahTugas");
        
        String gajibersih = request.getParameter("gajibersih");
        String gajikotor = request.getParameter("gajikotor");
        String tunjangan = request.getParameter("tunjangan");

        // Selected from lookups
        String pilihKtp = request.getParameter("pilihKtp");
        String pilihPekerjaan = request.getParameter("pilihPekerjaan");

        // Pagination parameters
        String mulaiKaryawanParam = request.getParameter("mulaiKaryawan");
        String mulaiPekerjaanParam = request.getParameter("mulaiPekerjaan");

        if (tombol == null) tombol = "";
        if (tombolKtp == null) tombolKtp = "";
        if (tombolPekerjaan == null) tombolPekerjaan = "";
        if (ktp == null) ktp = "";
        if (nama == null) nama = "";
        if (ruang == null) ruang = "";
        if (kodePekerjaan == null) kodePekerjaan = "";
        if (namaPekerjaan == null) namaPekerjaan = "";
        if (jumlahTugas == null) jumlahTugas = "";
        if (gajibersih == null) gajibersih = "";
        if (gajikotor == null) gajikotor = "";
        if (tunjangan == null) tunjangan = "";

        int mulaiKaryawan = 0;
        int mulaiPekerjaan = 0;
        int jumlahLimit = 5;

        try {
            if (mulaiKaryawanParam != null) mulaiKaryawan = Integer.parseInt(mulaiKaryawanParam);
        } catch (NumberFormatException ex) {}

        try {
            if (mulaiPekerjaanParam != null) mulaiPekerjaan = Integer.parseInt(mulaiPekerjaanParam);
        } catch (NumberFormatException ex) {}

        String keterangan = "<br>";
        boolean showKaryawanLookup = tombolKtp.equals("Lihat") || mulaiKaryawanParam != null;
        boolean showPekerjaanLookup = tombolPekerjaan.equals("Lihat") || mulaiPekerjaanParam != null;

        // 1. Handle KTP Lookup selection
        if (pilihKtp != null && !pilihKtp.equals("")) {
            ktp = pilihKtp;
            tombolKtp = "Cari";
        }

        // 2. Handle Pekerjaan Lookup selection
        if (pilihPekerjaan != null && !pilihPekerjaan.equals("")) {
            kodePekerjaan = pilihPekerjaan;
            tombolPekerjaan = "Cari";
        }

        // 3. Process KTP Search
        if (tombolKtp.equals("Cari")) {
            if (!ktp.equals("")) {
                if (karyawan.baca(ktp)) {
                    nama = karyawan.getNama();
                    ruang = Integer.toString(karyawan.getRuang());
                    
                    // Try to read existing salary details
                    if (gaji.baca(ktp)) {
                        kodePekerjaan = gaji.getKodePekerjaan();
                        gajibersih = Double.toString(gaji.getGajiBersih());
                        gajikotor = Double.toString(gaji.getGajiKotor());
                        tunjangan = Double.toString(gaji.getTunjangan());
                        
                        // Populate Pekerjaan details too
                        if (pekerjaan.baca(kodePekerjaan)) {
                            namaPekerjaan = pekerjaan.getNamaPekerjaan();
                            jumlahTugas = Integer.toString(pekerjaan.getJumlahTugas());
                        }
                    } else {
                        // Clear salary inputs if no record exists
                        kodePekerjaan = "";
                        namaPekerjaan = "";
                        jumlahTugas = "";
                        gajibersih = "";
                        gajikotor = "";
                        tunjangan = "";
                    }
                    keterangan = "Data karyawan ditemukan";
                } else {
                    keterangan = "KTP '" + ktp + "' tidak terdaftar";
                    nama = "";
                    ruang = "";
                }
            } else {
                keterangan = "KTP/NIP tidak boleh kosong untuk pencarian";
            }
        }

        // 4. Process Pekerjaan Search
        if (tombolPekerjaan.equals("Cari")) {
            if (!kodePekerjaan.equals("")) {
                if (pekerjaan.baca(kodePekerjaan)) {
                    namaPekerjaan = pekerjaan.getNamaPekerjaan();
                    jumlahTugas = Integer.toString(pekerjaan.getJumlahTugas());
                    keterangan = "Data pekerjaan ditemukan";
                } else {
                    keterangan = "Kode Pekerjaan '" + kodePekerjaan + "' tidak terdaftar";
                    namaPekerjaan = "";
                    jumlahTugas = "";
                }
            } else {
                keterangan = "Kode Pekerjaan tidak boleh kosong untuk pencarian";
            }
        }

        // 5. Save Salary details
        if (tombol.equals("Simpan")) {
            if (!ktp.equals("") && !kodePekerjaan.equals("")) {
                gaji.setKtp(ktp);
                gaji.setKodePekerjaan(kodePekerjaan);
                
                try {
                    gaji.setGajiBersih(Double.parseDouble(gajibersih));
                } catch (NumberFormatException e) {
                    gaji.setGajiBersih(0);
                }
                
                try {
                    gaji.setGajiKotor(Double.parseDouble(gajikotor));
                } catch (NumberFormatException e) {
                    gaji.setGajiKotor(0);
                }
                
                try {
                    gaji.setTunjangan(Double.parseDouble(tunjangan));
                } catch (NumberFormatException e) {
                    gaji.setTunjangan(0);
                }

                if (gaji.simpan()) {
                    keterangan = "Data gaji berhasil disimpan";
                } else {
                    keterangan = gaji.getPesan();
                }
            } else {
                keterangan = "KTP dan Kode Pekerjaan harus terisi untuk menyimpan";
            }
        }

        // 6. Delete Salary details
        if (tombol.equals("Hapus")) {
            if (!ktp.equals("")) {
                if (gaji.hapus(ktp)) {
                    keterangan = "Data gaji berhasil dihapus";
                    kodePekerjaan = "";
                    namaPekerjaan = "";
                    jumlahTugas = "";
                    gajibersih = "";
                    gajikotor = "";
                    tunjangan = "";
                } else {
                    keterangan = gaji.getPesan();
                }
            } else {
                keterangan = "Pilih/Isi KTP yang akan dihapus gajinya";
            }
        }

        // Generate HTML Output
        StringBuilder content = new StringBuilder();
        content.append("<div class='crud-container'>");
        
        // Form Section
        content.append("<div class='crud-form-section' style='max-width: 500px;'>");
        content.append("<h2>Input Gaji Karyawan</h2>");
        content.append("<form action='Gaji' method='post'>");
        content.append("<table border='0' class='form-table'>");
        
        // KTP Row with inline Cari & Lihat
        content.append("<tr><td>KTP</td><td>:</td><td>");
        content.append("<input type='text' name='ktp' value='").append(ktp).append("' size='15' style='width: 110px;'>");
        content.append("<input type='submit' name='tombolKtp' value='Cari' class='btn-submit btn-inline'>");
        content.append("<input type='submit' name='tombolKtp' value='Lihat' class='btn-submit btn-inline'>");
        content.append("</td></tr>");
        
        content.append("<tr><td>Nama</td><td>:</td><td><input type='text' name='nama' value='").append(nama).append("' size='30' readonly style='background-color: #f1f1f1;'></td></tr>");
        content.append("<tr><td>Ruang</td><td>:</td><td><input type='text' name='ruang' value='").append(ruang).append("' size='5' readonly style='background-color: #f1f1f1; width: 60px;'></td></tr>");
        
        // Kode Pekerjaan Row with inline Cari & Lihat
        content.append("<tr><td>Kode Pekerjaan</td><td>:</td><td>");
        content.append("<input type='text' name='kodePekerjaan' value='").append(kodePekerjaan).append("' size='15' style='width: 110px;'>");
        content.append("<input type='submit' name='tombolPekerjaan' value='Cari' class='btn-submit btn-inline'>");
        content.append("<input type='submit' name='tombolPekerjaan' value='Lihat' class='btn-submit btn-inline'>");
        content.append("</td></tr>");
        
        content.append("<tr><td>Nama Pekerjaan</td><td>:</td><td><input type='text' name='namaPekerjaan' value='").append(namaPekerjaan).append("' size='30' readonly style='background-color: #f1f1f1;'></td></tr>");
        content.append("<tr><td>Jumlah Tugas</td><td>:</td><td><input type='text' name='jumlahTugas' value='").append(jumlahTugas).append("' size='5' readonly style='background-color: #f1f1f1; width: 60px;'></td></tr>");
        
        // Gaji fields
        content.append("<tr><td>Gaji GajiBersih</td><td>:</td><td><input type='text' name='gajibersih' value='").append(gajibersih).append("' size='15'></td></tr>");
        content.append("<tr><td>Gaji GajiKotor</td><td>:</td><td><input type='text' name='gajikotor' value='").append(gajikotor).append("' size='15'></td></tr>");
        content.append("<tr><td>Gaji Tunjangan</td><td>:</td><td><input type='text' name='tunjangan' value='").append(tunjangan).append("' size='15'></td></tr>");
        
        // Form Action Buttons at bottom (Simpan, Hapus)
        content.append("<tr><td colspan='3' align='center' style='text-align: center; padding-top: 15px;'>");
        content.append("<input type='submit' name='tombol' value='Simpan'>&nbsp;");
        content.append("<input type='submit' name='tombol' value='Hapus'>");
        content.append("</td></tr>");
        
        content.append("</table>");
        content.append("</form>");
        if (!keterangan.equals("<br>") && !keterangan.equals("")) {
            content.append("<br><font color='red'>").append(keterangan).append("</font>");
        }
        content.append("</div>"); // End of form section

        // 7. Render Karyawan Lookup Table if showKaryawanLookup
        if (showKaryawanLookup) {
            content.append("<div class='crud-table-section' style='max-width: 600px; margin-bottom: 20px;'>");
            content.append("<h3>Pilih Karyawan</h3>");
            content.append("<table class='data-table'>");
            content.append("<tr><th>KTP</th><th>Nama</th><th>Ruang</th><th>Aksi</th></tr>");
            
            karyawan.bacaSemua(mulaiKaryawan, jumlahLimit);
            Object[][] listK = karyawan.getList();
            if (listK != null && listK.length > 0) {
                for (int i = 0; i < listK.length; i++) {
                    content.append("<tr>");
                    content.append("<td>").append(listK[i][0]).append("</td>");
                    content.append("<td>").append(listK[i][1]).append("</td>");
                    content.append("<td align='center'>").append(listK[i][2]).append("</td>");
                    content.append("<td align='center'><a href='Gaji?pilihKtp=").append(listK[i][0])
                           .append("&nama=").append(nama)
                           .append("&ruang=").append(ruang)
                           .append("&kodePekerjaan=").append(kodePekerjaan)
                           .append("&namaPekerjaan=").append(namaPekerjaan)
                           .append("&jumlahTugas=").append(jumlahTugas)
                           .append("&gajibersih=").append(gajibersih)
                           .append("&gajikotor=").append(gajikotor)
                           .append("&tunjangan=").append(tunjangan)
                           .append("'>Pilih</a></td>");
                    content.append("</tr>");
                }
            } else {
                content.append("<tr><td colspan='4' align='center'>Data karyawan kosong</td></tr>");
            }
            content.append("</table><br>");
            
            // Pagination Karyawan
            content.append("<center>");
            if (mulaiKaryawan > 0) {
                int prev = mulaiKaryawan - jumlahLimit;
                if (prev < 0) prev = 0;
                content.append("<a href='Gaji?tombolKtp=Lihat&mulaiKaryawan=").append(prev)
                       .append("&ktp=").append(ktp).append("&nama=").append(nama).append("&ruang=").append(ruang)
                       .append("&kodePekerjaan=").append(kodePekerjaan).append("&gajibersih=").append(gajibersih)
                       .append("'>Sebelumnya</a>&nbsp;|&nbsp;");
            }
            int next = mulaiKaryawan + jumlahLimit;
            content.append("<a href='Gaji?tombolKtp=Lihat&mulaiKaryawan=").append(next)
                   .append("&ktp=").append(ktp).append("&nama=").append(nama).append("&ruang=").append(ruang)
                   .append("&kodePekerjaan=").append(kodePekerjaan).append("&gajibersih=").append(gajibersih)
                   .append("'>Berikutnya</a>");
            content.append("</center>");
            content.append("</div>");
        }

        // 8. Render Pekerjaan Lookup Table if showPekerjaanLookup
        if (showPekerjaanLookup) {
            content.append("<div class='crud-table-section' style='max-width: 600px; margin-bottom: 20px;'>");
            content.append("<h3>Pilih Pekerjaan</h3>");
            content.append("<table class='data-table'>");
            content.append("<tr><th>Kode</th><th>Nama Pekerjaan</th><th>Jumlah Tugas</th><th>Aksi</th></tr>");
            
            pekerjaan.bacaSemua(mulaiPekerjaan, jumlahLimit);
            Object[][] listP = pekerjaan.getList();
            if (listP != null && listP.length > 0) {
                for (int i = 0; i < listP.length; i++) {
                    content.append("<tr>");
                    content.append("<td>").append(listP[i][0]).append("</td>");
                    content.append("<td>").append(listP[i][1]).append("</td>");
                    content.append("<td align='center'>").append(listP[i][2]).append("</td>");
                    content.append("<td align='center'><a href='Gaji?pilihPekerjaan=").append(listP[i][0])
                           .append("&ktp=").append(ktp)
                           .append("&nama=").append(nama)
                           .append("&ruang=").append(ruang)
                           .append("&gajibersih=").append(gajibersih)
                           .append("&gajikotor=").append(gajikotor)
                           .append("&tunjangan=").append(tunjangan)
                           .append("'>Pilih</a></td>");
                    content.append("</tr>");
                }
            } else {
                content.append("<tr><td colspan='4' align='center'>Data pekerjaan kosong</td></tr>");
            }
            content.append("</table><br>");
            
            // Pagination Pekerjaan
            content.append("<center>");
            if (mulaiPekerjaan > 0) {
                int prev = mulaiPekerjaan - jumlahLimit;
                if (prev < 0) prev = 0;
                content.append("<a href='Gaji?tombolPekerjaan=Lihat&mulaiPekerjaan=").append(prev)
                       .append("&ktp=").append(ktp).append("&nama=").append(nama).append("&ruang=").append(ruang)
                       .append("&kodePekerjaan=").append(kodePekerjaan).append("&gajibersih=").append(gajibersih)
                       .append("'>Sebelumnya</a>&nbsp;|&nbsp;");
            }
            int next = mulaiPekerjaan + jumlahLimit;
            content.append("<a href='Gaji?tombolPekerjaan=Lihat&mulaiPekerjaan=").append(next)
                   .append("&ktp=").append(ktp).append("&nama=").append(nama).append("&ruang=").append(ruang)
                   .append("&kodePekerjaan=").append(kodePekerjaan).append("&gajibersih=").append(gajibersih)
                   .append("'>Berikutnya</a>");
            content.append("</center>");
            content.append("</div>");
        }

        content.append("</div>"); // End of crud-container
        
        new MainForm().tampilan(content.toString(), request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Gaji Controller Servlet";
    }
}
