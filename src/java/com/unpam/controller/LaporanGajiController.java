package com.unpam.controller;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.unpam.model.Gaji;
import com.unpam.view.MainForm;

@WebServlet(name = "LaporanGajiController", urlPatterns = {"/LaporanGajiController"})
public class LaporanGajiController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String[][] formatTypeData = {
            {"PDF (Portable Document Format)", "pdf", "application/pdf"},
            {"XLSX (Microsoft Excel)", "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {"XLS (Microsoft Excel 97-2003)", "xls", "application/vnd.ms-excel"},
            {"DOCX (Microsoft Word)", "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {"ODT (OpenDocument Text)", "odt", "application/vnd.oasis.opendocument.text"},
            {"RTF (Rich Text Format)", "rtf", "text/rtf"}
        };

        HttpSession session = request.getSession(true);
        String userName = "";
        try {
            userName = session.getAttribute("username").toString();
        } catch (Exception ex) {}

        if (userName == null || userName.equals("")) {
            response.sendRedirect("./index.jsp");
            return;
        }

        String tombol = request.getParameter("tombol");
        String opsi = request.getParameter("opsi");
        String ktp = request.getParameter("ktp");
        String ruang = request.getParameter("ruang");
        String formatType = request.getParameter("formatType");

        if (tombol == null) tombol = "";
        if (ktp == null) ktp = "";
        if (opsi == null) opsi = "";
        if (ruang == null) ruang = "0";
        if (formatType == null) formatType = formatTypeData[0][0];

        String keterangan = "<br>";
        int noType = 0;

        for (int i = 0; i < formatTypeData.length; i++) {
            if (formatTypeData[i][0].equals(formatType)) {
                noType = i;
                break;
            }
        }

        boolean pdfStreamed = false;

        if (tombol.equals("Cetak")) {
            Gaji gaji = new Gaji();
            int ruangDipilih = 0;
            try {
                ruangDipilih = Integer.parseInt(ruang);
            } catch (NumberFormatException ex) {}

            String reportPath = getServletConfig().getServletContext().getRealPath("reports/GajiReport.jrxml");

            if (gaji.cetakLaporan(opsi, ktp, ruangDipilih, formatTypeData[noType][1], reportPath)) {
                byte[] pdfasbytes = gaji.getPdfAsBytes();
                if (pdfasbytes != null && pdfasbytes.length > 0) {
                    response.setHeader("Content-Disposition", "inline; filename=GajiReport." + formatTypeData[noType][1]);
                    response.setContentType(formatTypeData[noType][2]);
                    response.setContentLength(pdfasbytes.length);
                    try (OutputStream outStream = response.getOutputStream()) {
                        outStream.write(pdfasbytes, 0, pdfasbytes.length);
                        outStream.flush();
                    }
                    pdfStreamed = true;
                } else {
                    keterangan = "Hasil cetak laporan kosong.";
                }
            } else {
                keterangan = "Gagal cetak laporan: " + gaji.getPesan();
            }
        }

        if (!pdfStreamed) {
            response.setContentType("text/html;charset=UTF-8");
            boolean opsiSelected = false;

            StringBuilder content = new StringBuilder();
            content.append("<div class='crud-container' style='display: flex; justify-content: center; align-items: center; min-height: 300px;'>");
            content.append("<div class='crud-form-section' style='width: 450px; padding: 20px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); border-radius: 8px;'>");
            content.append("<h2 style='text-align: center; margin-bottom: 20px;'>Mencetak Gaji</h2>");
            content.append("<form action='LaporanGajiController' method='post'>");
            content.append("<table border='0' class='form-table' style='width: 100%;'>");

            // Radio Button KTP
            content.append("<tr>");
            if (opsi.equalsIgnoreCase("KTP")) {
                content.append("<td align='right' style='width: 10%;'><input type='radio' checked name='opsi' value='KTP'></td>");
                opsiSelected = true;
            } else {
                content.append("<td align='right' style='width: 10%;'><input type='radio' name='opsi' value='KTP'></td>");
            }
            content.append("<td align='left' style='width: 20%;'>KTP</td>");
            content.append("<td align='left'><input type='text' value='").append(ktp).append("' name='ktp' maxlength='15' size='15' style='width: 80%;'></td>");
            content.append("</tr>");

            // Radio Button Ruang
            content.append("<tr>");
            if (opsi.equalsIgnoreCase("ruang")) {
                content.append("<td align='right'><input type='radio' checked name='opsi' value='ruang'></td>");
                opsiSelected = true;
            } else {
                content.append("<td align='right'><input type='radio' name='opsi' value='ruang'></td>");
            }
            content.append("<td align='left'>Ruang</td>");
            content.append("<td align='left'>");
            content.append("<select name='ruang' style='width: 85%;'>");
            content.append("<option value='0'>Semua</option>");
            for (int i = 1; i <= 14; i++) {
                if (i == Integer.parseInt(ruang)) {
                    content.append("<option selected value='").append(i).append("'>").append(i).append("</option>");
                } else {
                    content.append("<option value='").append(i).append("'>").append(i).append("</option>");
                }
            }
            content.append("</select>");
            content.append("</td>");
            content.append("</tr>");

            // Radio Button Semua
            content.append("<tr>");
            if (!opsiSelected) {
                content.append("<td align='right'><input type='radio' checked name='opsi' value='Semua'></td>");
            } else {
                content.append("<td align='right'><input type='radio' name='opsi' value='Semua'></td>");
            }
            content.append("<td align='left'>Semua</td>");
            content.append("<td></td>");
            content.append("</tr>");

            content.append("<tr><td colspan='3'><hr style='border: 0; border-top: 1px solid #ccc; margin: 15px 0;'></td></tr>");

            // Format Laporan Dropdown
            content.append("<tr>");
            content.append("<td></td>");
            content.append("<td align='left'>Format Laporan</td>");
            content.append("<td align='left'>");
            content.append("<select name='formatType' style='width: 85%;'>");
            for (String[] formatLaporan : formatTypeData) {
                if (formatLaporan[0].equals(formatType)) {
                    content.append("<option selected value='").append(formatLaporan[0]).append("'>").append(formatLaporan[0]).append("</option>");
                } else {
                    content.append("<option value='").append(formatLaporan[0]).append("'>").append(formatLaporan[0]).append("</option>");
                }
            }
            content.append("</select>");
            content.append("</td>");
            content.append("</tr>");

            if (!keterangan.equals("<br>") && !keterangan.equals("")) {
                content.append("<tr><td colspan='3' align='center' style='padding-top: 10px; color: red;'><b>").append(keterangan).append("</b></td></tr>");
            }

            content.append("<tr><td colspan='3' align='center' style='text-align: center; padding-top: 20px;'>");
            content.append("<input type='submit' name='tombol' value='Cetak' class='btn-submit' style='width: 100px;'>");
            content.append("</td></tr>");

            content.append("</table>");
            content.append("</form>");
            content.append("</div>");
            content.append("</div>");

            new MainForm().tampilan(content.toString(), request, response);
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
        return "Laporan Gaji Controller Servlet";
    }
}
