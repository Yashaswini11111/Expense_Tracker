<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*" %>
<%
String email = (String) session.getAttribute("user");
if(email == null){
    response.sendRedirect("signin.jsp");
    return;
}

double income = 0, savings = 0, target = 0;
try{
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget","root","yashu316378");
    PreparedStatement ps = con.prepareStatement("SELECT income,savings_target,target_expenses FROM user WHERE email=?");
    ps.setString(1,email);
    ResultSet rs = ps.executeQuery();
    if(rs.next()){
        income = rs.getDouble("income");
        savings = rs.getDouble("savings_target");
        target = rs.getDouble("target_expenses");
    }
    rs.close(); ps.close(); con.close();
}catch(Exception e){ e.printStackTrace(); }
%>
<!DOCTYPE html>
<html>
<head>
<title>Savings</title>
<style>
body{ font-family: Arial; background:#f0f8ff; padding:30px; }
h2{ color:#0077cc; margin-bottom:20px; }
.card{ background:white; padding:20px; border-radius:10px; box-shadow:0 4px 12px rgba(0,0,0,0.1); margin-bottom:20px; }
.card p{ font-size:18px; font-weight:bold; }
a{ color:#0077cc; text-decoration:none; }
a:hover{ text-decoration:underline; }
</style>
</head>
<body>
<h2>Your Savings Overview</h2>
<div class="card">
<p>Income: ₹<%=income%></p>
<p>Savings Target: ₹<%=savings%></p>
<p>Target Expenses: ₹<%=target%></p>
</div>
<a href="Dashboard.jsp">Back to Dashboard</a>
</body>
</html>