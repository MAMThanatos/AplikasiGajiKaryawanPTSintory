package com.unpam.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.unpam.model.Karyawan;
import com.unpam.model.Enkripsi;
import com.unpam.view.MainForm;

@WebServlet(name = "KaryawanController", urlPatterns = {"/Karyawan"})
public class KaryawanController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(true);
        Karyawan karyawan = new Karyawan();
        Enkripsi enkripsi = new Enkripsi();
        String username = "";

        String tombol = request.getParameter("tombol");
        String nip = request.getParameter("nip");
        String nama = request.getParameter("nama");
        String ruang = request.getParameter("ruang");
        String password = request.getParameter("password");
        String mulaiParameter = request.getParameter("mulai");
        String jumlahParameter = request.getParameter("jumlah");
        String nipDipilih = request.getParameter("nipDipilih");

        if (tombol == null) tombol = "";
        if (nip == null) nip = "";
        if (nama == null) nama = "";
        if (ruang == null) ruang = "";
        if (password == null) password = "";
        if (nipDipilih == null) nipDipilih = "";

        int mulai = 0, jumlah = 10;

        try {
            mulai = Integer.parseInt(mulaiParameter);
        } catch (NumberFormatException ex) {}

        try {
            jumlah = Integer.parseInt(jumlahParameter);
        } catch (NumberFormatException ex) {}

        String keterangan = "<br>";

        try {
            username = session.getAttribute("username").toString();
        } catch (Exception ex) {}

        if (username != null && !username.equals("")) {
            if (tombol.equals("Simpan")) {
                if (!nip.equals("")) {
                    karyawan.setNip(nip);
                    karyawan.setNama(nama);
                    try {
                        karyawan.setRuang(Integer.parseInt(ruang));
                    } catch (NumberFormatException e) {
                        karyawan.setRuang(0);
                    }
                    String passwordEnkripsi = "";
                    try {
                        passwordEnkripsi = enkripsi.hashMD5(password);
                    } catch (Exception ex) {}
                    karyawan.setPassword(passwordEnkripsi);
                    
                    if (karyawan.simpan() == false) {
                        nip = "";
                        nama = "";
                        ruang = "";
                        password = "";
                        keterangan = "Sudah tersimpan";
                    } else {
                        keterangan = karyawan.getPesan();
                    }
                } else {
                    keterangan = "NIP tidak boleh kosong";
                }
            } else if (tombol.equals("Hapus")) {
                if (!nip.equals("")) {
                    if (karyawan.hapus(nip) == false) {
                        nip = "";
                        nama = "";
                        ruang = "";
                        password = "";
                        keterangan = "Data sudah dihapus";
                    } else {
                        keterangan = karyawan.getPesan();
                    }
                } else {
                    keterangan = "NIP masih kosong";
                }
            } else if (tombol.equals("Cari")) {
                if (!nip.equals("")) {
                    if (karyawan.baca(nip) == true) {
                        nip = karyawan.getNip();
                        nama = karyawan.getNama();
                        ruang = Integer.toString(karyawan.getRuang());
                        password = karyawan.getPassword();
                        keterangan = "<br>";
                    } else {
                        keterangan = "NIP '" + nip + "' tidak ada";
                    }
                } else {
                    keterangan = "NIP harus diisi";
                }
            } else if (tombol.equals("Pilih")) {
                if (nipDipilih != null) {
                    nama = "";
                    ruang = "";
                    if (!nipDipilih.equals("")) {
                        if (karyawan.baca(nipDipilih) == true) {
                            nip = karyawan.getNip();
                            nama = karyawan.getNama();
                            ruang = Integer.toString(karyawan.getRuang());
                            password = karyawan.getPassword();
                            keterangan = "<br>";
                        }
                    }
                }
            } else {
                keterangan = "<br>";
            }

            boolean tampilkanTabel = tombol.equals("Lihat") || tombol.equals("Pilih") || 
                                     mulaiParameter != null || jumlahParameter != null || 
                                     !nipDipilih.equals("");

            // Build Ruang select options
            StringBuilder ruangSelect = new StringBuilder();
            ruangSelect.append("<select name='ruang'>");
            for (int i = 1; i <= 5; i++) {
                ruangSelect.append("<option value='").append(i).append("'");
                if (ruang.equals(Integer.toString(i))) {
                    ruangSelect.append(" selected");
                }
                ruangSelect.append(">").append(i).append("</option>");
            }
            ruangSelect.append("</select>");

            // Generate HTML using custom CSS classes
            StringBuilder content = new StringBuilder();
            content.append("<div class='crud-container'>");
            
            // Form Section
            content.append("<div class='crud-form-section'>");
            content.append("<h2>Master Data Karyawan</h2>");
            content.append("<form action='Karyawan' method='post'>");
            content.append("<table border='0' class='form-table'>");
            content.append("<tr><td>KTP</td><td>:</td><td>");
            content.append("<input type='text' name='nip' value='").append(nip).append("' size='15' style='width: 110px;'>");
            content.append("<input type='submit' name='tombol' value='Cari' class='btn-submit btn-inline'>");
            content.append("</td></tr>");
            content.append("<tr><td>Nama</td><td>:</td><td><input type='text' name='nama' value='").append(nama).append("' size='35'></td></tr>");
            content.append("<tr><td>Ruang</td><td>:</td><td>").append(ruangSelect.toString()).append("</td></tr>");
            content.append("<tr><td>Password</td><td>:</td><td><input type='password' name='password' value='").append(password).append("' size='20'></td></tr>");
            content.append("<tr><td colspan='3' align='center' style='text-align: center; padding-top: 15px;'>");
            content.append("<input type='submit' name='tombol' value='Simpan'>&nbsp;");
            content.append("<input type='submit' name='tombol' value='Hapus'>&nbsp;");
            content.append("<input type='submit' name='tombol' value='Lihat'>");
            content.append("</td></tr>");
            content.append("</table>");
            content.append("</form>");
            if (!keterangan.equals("<br>") && !keterangan.equals("")) {
                content.append("<br><font color='red'>").append(keterangan).append("</font>");
            }
            content.append("</div>"); // End of form section
            
            // Table Section (only if Lihat clicked or paginating)
            if (tampilkanTabel) {
                karyawan.bacaSemua(mulai, jumlah);
                Object[][] listKaryawan = karyawan.getList();

                content.append("<div class='crud-table-section'>");
                content.append("<table class='data-table'>");
                content.append("<tr>");
                content.append("<th width='5%'>No</th>");
                content.append("<th width='15%'>KTP</th>");
                content.append("<th width='30%'>Nama</th>");
                content.append("<th width='10%'>Ruang</th>");
                content.append("<th width='30%'>Password Hash</th>");
                content.append("<th width='10%'>Aksi</th>");
                content.append("</tr>");
     
                if (listKaryawan != null && listKaryawan.length > 0) {
                    for (int i = 0; i < listKaryawan.length; i++) {
                        content.append("<tr>");
                        content.append("<td align='center'>").append(mulai + i + 1).append("</td>");
                        content.append("<td>").append(listKaryawan[i][0]).append("</td>");
                        content.append("<td>").append(listKaryawan[i][1]).append("</td>");
                        content.append("<td align='center'>").append(listKaryawan[i][2]).append("</td>");
                        content.append("<td class='hash-cell'>").append(listKaryawan[i][3]).append("</td>");
                        content.append("<td align='center'><a href='Karyawan?tombol=Pilih&nipDipilih=").append(listKaryawan[i][0]).append("&mulai=").append(mulai).append("&jumlah=").append(jumlah).append("'>Pilih</a></td>");
                        content.append("</tr>");
                    }
                } else {
                    content.append("<tr><td colspan='6' align='center'>Data kosong</td></tr>");
                }
                content.append("</table><br>");
     
                // Pagination
                content.append("<center>");
                if (mulai > 0) {
                    int mulaiSebelumnya = mulai - jumlah;
                    if (mulaiSebelumnya < 0) mulaiSebelumnya = 0;
                    content.append("<a href='Karyawan?tombol=Lihat&mulai=").append(mulaiSebelumnya).append("&jumlah=").append(jumlah).append("'>Sebelumnya</a>&nbsp;|&nbsp;");
                }
                int mulaiBerikutnya = mulai + jumlah;
                content.append("<a href='Karyawan?tombol=Lihat&mulai=").append(mulaiBerikutnya).append("&jumlah=").append(jumlah).append("'>Berikutnya</a>");
                content.append("</center>");
                content.append("</div>"); // End of table section
            }
            
            content.append("</div>");

            new MainForm().tampilan(content.toString(), request, response);
        } else {
            response.sendRedirect("./index.jsp");
        }
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
        return "Karyawan Controller Servlet";
    }
}
