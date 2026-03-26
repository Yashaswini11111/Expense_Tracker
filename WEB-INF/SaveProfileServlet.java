import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SaveProfileServlet")
public class SaveProfileServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String email = (String) request.getSession().getAttribute("user");
            String name = request.getParameter("name");

            String incomeStr = request.getParameter("income");
            String savingsStr = request.getParameter("savings_target");

            double income = 0;
            double savingsTarget = 0;

            if(incomeStr != null && !incomeStr.isEmpty()){
                income = Double.parseDouble(incomeStr);
            }

            if(savingsStr != null && !savingsStr.isEmpty()){
                savingsTarget = Double.parseDouble(savingsStr);
            }

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/budget",
                    "root",
                    "yashu316378");

            String sql = "UPDATE user SET name=?, income=?, savings_target=? WHERE email=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, name);
            ps.setDouble(2, income);
            ps.setDouble(3, savingsTarget);
            ps.setString(4, email);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                response.sendRedirect("Dashboard.jsp");
            } else {
                response.getWriter().println("Profile update failed");
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}