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

@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(true);
        Karyawan karyawan = new Karyawan();
        Enkripsi enkripsi = new Enkripsi();
        String username = "";

        String op = request.getParameter("op");
        String tombol = request.getParameter("tombol");
        String nip = request.getParameter("nip");
        String password = request.getParameter("password");

        if (op == null) op = "";
        if (tombol == null) tombol = "";
        if (nip == null) nip = "";
        if (password == null) password = "";

        String pesan = "";

        try {
            username = session.getAttribute("username").toString();
        } catch (Exception ex) {}

        if (op.equals("in")) {
            if (tombol.equals("Login")) {
                if (!nip.equals("") && !password.equals("")) {
                    if (karyawan.baca(nip) == true) {
                        String passwordEnkripsi = "";
                        try {
                            passwordEnkripsi = enkripsi.hashMD5(password);
                        } catch (Exception ex) {}
                        
                        if (karyawan.getPassword().equals(passwordEnkripsi)) {
                            // Set session variables
                            session.setAttribute("username", karyawan.getNama());
                            
                            // Set menu with Logout mapping
                            String menu = "<h2>Master Data</h2>"
                                    + "<li><a href='./Karyawan'>Karyawan</a></li>"
                                    + "<li><a href='./Pekerjaan'>Pekerjaan</a></li>"
                                    + "<h2>Transaksi</h2>"
                                    + "<li><a href='./Gaji'>Gaji</a></li>"
                                    + "<h2>Laporan</h2>"
                                    + "<li><a href='./LaporanGajiController'>Gaji</a></li>"
                                    + "<li><a href='./Logout'>Logout</a></li>";
                            
                            String topMenu = "<ul>"
                                    + "<li><a href='./'>Home</a></li>"
                                    + "<li><a href='#'>Master Data</a>"
                                    + "<ul>"
                                    + "<li><a href='./Karyawan'>Karyawan</a></li>"
                                    + "<li><a href='./Pekerjaan'>Pekerjaan</a></li>"
                                    + "</ul>"
                                    + "</li>"
                                    + "<li><a href='#'>Transaksi</a>"
                                    + "<ul>"
                                    + "<li><a href='./Gaji'>Gaji</a></li>"
                                    + "</ul>"
                                    + "</li>"
                                    + "<li><a href='#'>Laporan</a>"
                                    + "<ul>"
                                    + "<li><a href='./LaporanGajiController'>Gaji</a></li>"
                                    + "</ul>"
                                    + "</li>"
                                    + "<li><a href='./Logout'>Logout</a></li>"
                                    + "</ul>";
                                    
                            session.setAttribute("menu", menu);
                            session.setAttribute("topmenu", topMenu);
                            
                            response.sendRedirect("./index.jsp");
                            return;
                        } else {
                            pesan = "Password salah";
                        }
                    } else {
                        if (karyawan.getPesan() != null && !karyawan.getPesan().equals("")) {
                            pesan = karyawan.getPesan();
                        } else {
                            pesan = "NIP '" + nip + "' tidak terdaftar";
                        }
                    }
                } else {
                    pesan = "NIP dan Password harus diisi";
                }
            }
            
            // Render Login Form inside MainForm template using custom CSS classes
            StringBuilder content = new StringBuilder();
            content.append("<div class='login-container'>");
            content.append("<h2>Login Form</h2>");
            content.append("<form action='LoginController?op=in' method='post'>");
            content.append("<table border='0' class='form-table'>");
            content.append("<tr><td>KTP</td><td>:</td><td><input type='text' name='nip' value='").append(nip).append("' size='15'></td></tr>");
            content.append("<tr><td>Password</td><td>:</td><td><input type='password' name='password' value='' size='15'></td></tr>");
            content.append("<tr><td colspan='3' align='center' style='text-align: center; padding-top: 15px;'>");
            content.append("<input type='submit' name='tombol' value='Login' class='btn-submit' style='width: 100px;'>");
            content.append("</td></tr>");
            content.append("</table>");
            content.append("</form>");
            if (!pesan.equals("")) {
                content.append("<br><font color='red'>").append(pesan).append("</font>");
            }
            content.append("</div>");
            
            new MainForm().tampilan(content.toString(), request, response);
            
        } else if (op.equals("out")) {
            session.invalidate();
            response.sendRedirect("./index.jsp");
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
        return "Login Controller Servlet";
    }
}
