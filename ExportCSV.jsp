<%@ page import="java.sql.*" %>
<%
String email = (String) session.getAttribute("user");

if(email == null){
    response.sendRedirect("signin.jsp");
    return;
}

response.setContentType("text/csv");
response.setHeader("Content-Disposition", "attachment; filename=expenses.csv");

try{
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget","root","yashu316378");

    PreparedStatement ps = con.prepareStatement(
        "SELECT date, category, amount FROM expenses WHERE email=? ORDER BY date DESC"
    );
    ps.setString(1,email);

    ResultSet rs = ps.executeQuery();

    // CSV Header
    out.println("Date,Category,Amount");

    // Data
    while(rs.next()){
        out.println(
            rs.getDate("date") + "," +
            rs.getString("category") + "," +
            rs.getDouble("amount")
        );
    }

    con.close();
}catch(Exception e){
    e.printStackTrace();
}
%>