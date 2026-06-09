<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="style.css?v=2" rel="stylesheet" type="text/css" />
        <title>Informasi Gaji Karyawan PT MAM</title>
    </head>
    <body bgcolor="#000000">
        <%
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
        %>
        
        <center>
            <div id="art-main" align="center">
                <div id="art-sheet" class="art-sheet">
                    <div id="art-header" class="art-header">
                        <div id="art-header-jpeg">
                            Informasi Gaji Karyawan
                            <br>
                            PT. MAM
                            <br>
                            <span style="font-size: 14px; font-weight: normal;">Jl. Surya Kencana No. 99 Pamulang, Tangerang Selatan, Banten</span>
                        </div>
                    </div>
                    <div id="art-content-layout" class="art-content-layout">
                        <div class="art-layout-cell art-sidebar1" align="left">
                            <div id="menu">
                                <%=menu%>
                            </div>
                        </div>
                        <div class="art-layout-cell art-content" align="left">
                            <center>
                                <nav>
                                    <%=topMenu%>
                                </nav>
                            </center>
                            
                            <% if (status != "") { %>
                                <%=status%>
                            <% } %>
                            
                            <div class="art-post" align="center">
                                <h1 style="font-family: 'Times New Roman', Times, serif; font-size: 32px; font-weight: bold; color: #000000; margin-top: 50px; margin-bottom: 10px; text-align: center;">Selamat Datang</h1>
                                <% if (username != null && !username.equals("")) { %>
                                    <h2 style="font-family: 'Times New Roman', Times, serif; font-size: 24px; font-weight: bold; color: #000000; margin-top: 0; margin-bottom: 50px; text-align: center;"><%=username%></h2>
                                <% } %>
                            </div>
                        </div>
                    </div>
                    <div id="art-footer" class="art-footer">
                        Copyright &copy; 2026 PT. MAM
                        <br>
                        Jl. Surya Kencana No. 99 Pamulang, Tangerang Selatan, Banten
                    </div>
                </div>
            </div>
        </center>
    </body>
</html>
