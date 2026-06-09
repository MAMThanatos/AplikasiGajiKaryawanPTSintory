package com.unpam.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.unpam.model.Pekerjaan;
import com.unpam.view.MainForm;

@WebServlet(name = "PekerjaanController", urlPatterns = {"/Pekerjaan"})
public class PekerjaanController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(true);
        Pekerjaan pekerjaan = new Pekerjaan();
        String username = "";

        String tombol = request.getParameter("tombol");
        String kodePekerjaan = request.getParameter("kodePekerjaan");
        String namaPekerjaan = request.getParameter("namaPekerjaan");
        String jumlahTugas = request.getParameter("jumlahTugas");
        String mulaiParameter = request.getParameter("mulai");
        String jumlahParameter = request.getParameter("jumlah");
        String kodePekerjaanDipilih = request.getParameter("kodePekerjaanDipilih");

        if (tombol == null) tombol = "";
        if (kodePekerjaan == null) kodePekerjaan = "";
        if (namaPekerjaan == null) namaPekerjaan = "";
        if (jumlahTugas == null) jumlahTugas = "";
        if (kodePekerjaanDipilih == null) kodePekerjaanDipilih = "";

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
                if (!kodePekerjaan.equals("")) {
                    pekerjaan.setKodePekerjaan(kodePekerjaan);
                    pekerjaan.setNamaPekerjaan(namaPekerjaan);
                    try {
                        pekerjaan.setJumlahTugas(Integer.parseInt(jumlahTugas));
                    } catch (NumberFormatException e) {
                        pekerjaan.setJumlahTugas(0);
                    }
                    
                    if (pekerjaan.simpan() == false) {
                        kodePekerjaan = "";
                        namaPekerjaan = "";
                        jumlahTugas = "";
                        keterangan = "Sudah tersimpan";
                    } else {
                        keterangan = pekerjaan.getPesan();
                    }
                } else {
                    keterangan = "Kode pekerjaan tidak boleh kosong";
                }
            } else if (tombol.equals("Hapus")) {
                if (!kodePekerjaan.equals("")) {
                    if (pekerjaan.hapus(kodePekerjaan) == false) {
                        kodePekerjaan = "";
                        namaPekerjaan = "";
                        jumlahTugas = "";
                        keterangan = "Data sudah dihapus";
                    } else {
                        keterangan = pekerjaan.getPesan();
                    }
                } else {
                    keterangan = "Kode pekerjaan masih kosong";
                }
            } else if (tombol.equals("Cari")) {
                if (!kodePekerjaan.equals("")) {
                    if (pekerjaan.baca(kodePekerjaan) == true) {
                        kodePekerjaan = pekerjaan.getKodePekerjaan();
                        namaPekerjaan = pekerjaan.getNamaPekerjaan();
                        jumlahTugas = Integer.toString(pekerjaan.getJumlahTugas());
                        keterangan = "<br>";
                    } else {
                        keterangan = "Kode Pekerjaan '" + kodePekerjaan + "' tidak ada";
                    }
                } else {
                    keterangan = "Kode Pekerjaan harus diisi";
                }
            } else if (tombol.equals("Pilih")) {
                if (kodePekerjaanDipilih != null) {
                    namaPekerjaan = "";
                    jumlahTugas = "";
                    if (!kodePekerjaanDipilih.equals("")) {
                        if (pekerjaan.baca(kodePekerjaanDipilih) == true) {
                            kodePekerjaan = pekerjaan.getKodePekerjaan();
                            namaPekerjaan = pekerjaan.getNamaPekerjaan();
                            jumlahTugas = Integer.toString(pekerjaan.getJumlahTugas());
                            keterangan = "<br>";
                        }
                    }
                }
            } else {
                keterangan = "<br>";
            }

            boolean tampilkanTabel = tombol.equals("Lihat") || tombol.equals("Pilih") || 
                                     mulaiParameter != null || jumlahParameter != null || 
                                     !kodePekerjaanDipilih.equals("");

            // Build Jumlah Tugas select options
            StringBuilder tugasSelect = new StringBuilder();
            tugasSelect.append("<select name='jumlahTugas'>");
            for (int i = 1; i <= 10; i++) {
                tugasSelect.append("<option value='").append(i).append("'");
                if (jumlahTugas.equals(Integer.toString(i))) {
                    tugasSelect.append(" selected");
                }
                tugasSelect.append(">").append(i).append("</option>");
            }
            tugasSelect.append("</select>");

            // Generate HTML using custom CSS classes
            StringBuilder content = new StringBuilder();
            content.append("<div class='crud-container'>");
            
            // Form Section
            content.append("<div class='crud-form-section'>");
            content.append("<h2>Master Data Pekerjaan</h2>");
            content.append("<form action='Pekerjaan' method='post'>");
            content.append("<table border='0' class='form-table'>");
            content.append("<tr><td>Kode Pekerjaan</td><td>:</td><td>");
            content.append("<input type='text' name='kodePekerjaan' value='").append(kodePekerjaan).append("' size='15' style='width: 110px;'>");
            content.append("<input type='submit' name='tombol' value='Cari' class='btn-submit btn-inline'>");
            content.append("</td></tr>");
            content.append("<tr><td>Nama Pekerjaan</td><td>:</td><td><input type='text' name='namaPekerjaan' value='").append(namaPekerjaan).append("' size='35'></td></tr>");
            content.append("<tr><td>Jumlah Tugas</td><td>:</td><td>").append(tugasSelect.toString()).append("</td></tr>");
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
                pekerjaan.bacaSemua(mulai, jumlah);
                Object[][] listPekerjaan = pekerjaan.getList();

                content.append("<div class='crud-table-section'>");
                content.append("<table class='data-table'>");
                content.append("<tr>");
                content.append("<th width='5%'>No</th>");
                content.append("<th width='25%'>Kode Pekerjaan</th>");
                content.append("<th width='40%'>Nama Pekerjaan</th>");
                content.append("<th width='15%'>Jumlah Tugas</th>");
                content.append("<th width='15%'>Aksi</th>");
                content.append("</tr>");
     
                if (listPekerjaan != null && listPekerjaan.length > 0) {
                    for (int i = 0; i < listPekerjaan.length; i++) {
                        content.append("<tr>");
                        content.append("<td align='center'>").append(mulai + i + 1).append("</td>");
                        content.append("<td>").append(listPekerjaan[i][0]).append("</td>");
                        content.append("<td>").append(listPekerjaan[i][1]).append("</td>");
                        content.append("<td align='center'>").append(listPekerjaan[i][2]).append("</td>");
                        content.append("<td align='center'><a href='Pekerjaan?tombol=Pilih&kodePekerjaanDipilih=").append(listPekerjaan[i][0]).append("&mulai=").append(mulai).append("&jumlah=").append(jumlah).append("'>Pilih</a></td>");
                        content.append("</tr>");
                    }
                } else {
                    content.append("<tr><td colspan='5' align='center'>Data kosong</td></tr>");
                }
                content.append("</table><br>");
     
                // Pagination
                content.append("<center>");
                if (mulai > 0) {
                    int mulaiSebelumnya = mulai - jumlah;
                    if (mulaiSebelumnya < 0) mulaiSebelumnya = 0;
                    content.append("<a href='Pekerjaan?tombol=Lihat&mulai=").append(mulaiSebelumnya).append("&jumlah=").append(jumlah).append("'>Sebelumnya</a>&nbsp;|&nbsp;");
                }
                int mulaiBerikutnya = mulai + jumlah;
                content.append("<a href='Pekerjaan?tombol=Lihat&mulai=").append(mulaiBerikutnya).append("&jumlah=").append(jumlah).append("'>Berikutnya</a>");
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
        return "Pekerjaan Controller Servlet";
    }
}
