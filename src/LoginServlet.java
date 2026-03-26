import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to Database
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/budget",
                "root",
                "yashu316378"    
            );

            // Check if user exists
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM user WHERE email=? AND password=?"
            );

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // ✅ Login Success
                HttpSession session = request.getSession();
                session.setAttribute("user", email);

                response.sendRedirect(request.getContextPath() + "/Profile.jsp");

            } else {
                // ❌ Login Failed
                response.getWriter().println("Invalid Email or Password!");
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}