package com.unpam.view;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "MainForm", urlPatterns = {"/MainForm"})
public class MainForm extends HttpServlet {

    public void tampilan(String konten, HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String menu = "<h2>Master Data</h2>"
                + "<li><a href='./Karyawan'>Karyawan</a></li>"
                + "<li><a href='./Pekerjaan'>Pekerjaan</a></li>"
                + "<h2>Transaksi</h2>"
                + "<li><a href='./Gaji'>Gaji</a></li>"
                + "<h2>Laporan</h2>"
                + "<li><a href='./LaporanGajiController'>Gaji</a></li>"
                + "<li><a href='LoginController?op=in'>Login</a></li>";
        
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
                + "<li><a href='LoginController?op=in'>Login</a></li>"
                + "</ul>";

        String status = ""; 
        String username = "";
        
        HttpSession session = request.getSession(true);
        if (session != null) {
            try {
                username = session.getAttribute("username").toString();
            } catch (Exception e) {}
            
            if (username != null && !username.equals("")) {
                status = "<div>" + username + "</div>";
                
                try {
                    menu = session.getAttribute("menu").toString();
                } catch (Exception e) {}
                
                try {
                    topMenu = session.getAttribute("topmenu").toString();
                } catch (Exception e) {}
            }
        }
        
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
            out.println("<link href=\"style.css?v=2\" rel=\"stylesheet\" type=\"text/css\" />");
            out.println("<title>Informasi Gaji Karyawan PT MAM</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<center>");
            out.println("<div id=\"art-main\" align=\"center\">");
            out.println("<div id=\"art-sheet\" class=\"art-sheet\">");
            out.println("<div id=\"art-header\" class=\"art-header\">");
            out.println("<div id=\"art-header-jpeg\">");
            out.println("Informasi Gaji Karyawan<br>");
            out.println("PT. MAM<br>");
            out.println("<span style=\"font-size: 14px; font-weight: normal;\">Jl. Surya Kencana No. 99 Pamulang, Tangerang Selatan, Banten</span>");
            out.println("</div>");
            out.println("</div>");
            out.println("<div id=\"art-content-layout\" class=\"art-content-layout\">");
            out.println("<div class=\"art-layout-cell art-sidebar1\" align=\"left\">");
            out.println("<div id=\"menu\">");
            out.println(menu);
            out.println("</div>");
            out.println("</div>");
            out.println("<div class=\"art-layout-cell art-content\" align=\"left\">");
            out.println("<center>");
            out.println("<nav>");
            out.println(topMenu);
            out.println("</nav>");
            out.println("</center>");
            if (!status.equals("")) {
                out.println(status);
            }
            out.println("<div class=\"art-post\" align=\"left\">");
            out.println(konten);
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("<div id=\"art-footer\" class=\"art-footer\">");
            out.println("Copyright &copy; 2026 PT. MAM<br>");
            out.println("Jl. Surya Kencana No. 99 Pamulang, Tangerang Selatan, Banten");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("</center>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        tampilan("", request, response);
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
        return "MainForm Template Servlet";
    }
}
