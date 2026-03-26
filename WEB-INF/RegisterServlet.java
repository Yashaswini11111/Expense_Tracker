import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
       String password = request.getParameter("password");
       String confirmpassword=request.getParameter("confirmpassword");

       if(!password.equals(confirmpassword)){
        response.getWriter().println("Password do not match");
        return;
       }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
             "jdbc:mysql://localhost:3306/budget",
                "root",
                "yashu316378"
            );

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO user(email,password) VALUES(?,?)"
            );

            ps.setString(1, email);
            ps.setString(2, password);

            ps.executeUpdate();

            // 🔥 Create session immediately after signup
            HttpSession session = request.getSession();
            session.setAttribute("user", email);

            response.sendRedirect("ProfilePage.jsp");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}