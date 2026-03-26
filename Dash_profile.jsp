<%@ page import="java.sql.*" %>
<%
String email = (String) session.getAttribute("user");

if(email == null){
    response.sendRedirect("signin.jsp");
    return;
}

String name = "";
double income = 0;
double savingsTarget = 0;
double totalExpenses = 0;

try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/budget",
        "root",
        "yashu316378"
    );

    // Fetch user details
    PreparedStatement ps1 = con.prepareStatement(
        "SELECT name, email, income, savings_target FROM user WHERE email=?"
    );
    ps1.setString(1, email);
    ResultSet rs1 = ps1.executeQuery();

    if(rs1.next()){
        name = rs1.getString("name");
        income = rs1.getDouble("income");
        savingsTarget = rs1.getDouble("savings_target");
    }

    // Calculate total expenses
    PreparedStatement ps2 = con.prepareStatement(
        "SELECT SUM(amount) FROM expenses WHERE email=?"
    );
    ps2.setString(1, email);
    ResultSet rs2 = ps2.executeQuery();

    if(rs2.next()){
        totalExpenses = rs2.getDouble(1);
    }

    con.close();

} catch(Exception e){
    out.println(e);
}
%>

<!DOCTYPE html>
<html>
<head>
<title>User Profile</title>
<style>
body{
    font-family: Arial;
    background:#f4f6f9;
    display:flex;
    justify-content:center;
    align-items:center;
    height:100vh;
}

.profile-box{
    background:white;
    padding:30px;
    width:400px;
    border-radius:10px;
    box-shadow:0 5px 15px rgba(0,0,0,0.2);
}

h2{
    text-align:center;
    margin-bottom:20px;
}

p{
    font-size:16px;
    margin:10px 0;
}
</style>
</head>

<body>

<div class="profile-box">
<h2>Complete User Profile</h2>

<p><strong>Username:</strong> <%= name %></p>
<p><strong>Email:</strong> <%= email %></p>
<p><strong>Income:</strong> ₹ <%= income %></p>
<p><strong>Savings Target:</strong> ₹ <%= savingsTarget %></p>
<p><strong>Total Expenses:</strong> ₹ <%= totalExpenses %></p>
<p><strong>Remaining Balance:</strong> ₹ <%= income - totalExpenses %></p>

</div>

</body>
</html>