<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
String userEmail = (String) session.getAttribute("user");
if(userEmail == null){
    response.sendRedirect("signin.jsp");
    return;
}

// Default values
String name = "";
double income = 0, savingsTarget = 0;

try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget", "root", "yashu316378");

    PreparedStatement ps = con.prepareStatement("SELECT name, income, savings_target FROM user WHERE email=?");
    ps.setString(1, userEmail);
    ResultSet rs = ps.executeQuery();
    if(rs.next()){
        name = rs.getString("name");
        income = rs.getDouble("income");
        savingsTarget = rs.getDouble("savings_target");
    }

    con.close();
} catch(Exception e) {
    e.printStackTrace();
}
%>
<!DOCTYPE html>
<html>
<head>
<title>Profile</title>
<style>
body{
    margin:0;
    font-family: Arial, Helvetica, sans-serif;
    display:flex;
    justify-content:center;
    align-items:center;
    height:100vh;
    background: linear-gradient(to right, #e0f7ff, #87ceeb);
}
.container{
    width:400px;
    padding:35px;
    background:white;
    border-radius:15px;
    box-shadow:0 8px 25px rgba(0,0,0,0.2);
    text-align:center;
}
h2{
    color:#0077cc;
    margin-bottom:20px;
}
input{
    width:100%;
    padding:12px;
    margin:10px 0;
    border:1px solid #ccc;
    border-radius:8px;
    outline:none;
}
input:focus{
    border-color:#0077cc;
    box-shadow:0 0 8px rgba(0,119,204,0.4);
}
button{
    width:48%;
    padding:12px;
    margin-top:15px;
    background:#0077cc;
    color:white;
    border:none;
    border-radius:25px;
    font-size:16px;
    font-weight:bold;
    cursor:pointer;
}
button:hover{
    background:#005fa3;
}
.buttons{
    display:flex;
    justify-content:space-between;
}
</style>
</head>
<body>
<div class="container">
<h2>Profile</h2>
<form method="post">
    <input type="text" name="name" placeholder="Username" value="<%=name%>" required>
    <input type="number" name="income" placeholder="Income" value="<%=income%>" required>
    <input type="number" name="savings_target" placeholder="Savings Target" value="<%=savingsTarget%>" required>
    <div class="buttons">
        <button type="submit" name="action" value="submit">Submit</button>
        <button type="submit" name="action" value="update">Update</button>
    </div>
</form>

<%
if(request.getMethod().equalsIgnoreCase("POST")){
    String action = request.getParameter("action");
    String newName = request.getParameter("name");
    double newIncome = Double.parseDouble(request.getParameter("income"));
    double newSavings = Double.parseDouble(request.getParameter("savings_target"));

    try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget", "root", "yashu316378");
        PreparedStatement ps;

        if(action.equals("submit")){
            ps = con.prepareStatement("INSERT INTO user (email,name,income,savings_target) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE name=?, income=?, savings_target=?");
            ps.setString(1, userEmail);
            ps.setString(2, newName);
            ps.setDouble(3, newIncome);
            ps.setDouble(4, newSavings);
            ps.setString(5, newName);
            ps.setDouble(6, newIncome);
            ps.setDouble(7, newSavings);
        } else {
            ps = con.prepareStatement("UPDATE user SET name=?, income=?, savings_target=? WHERE email=?");
            ps.setString(1, newName);
            ps.setDouble(2, newIncome);
            ps.setDouble(3, newSavings);
            ps.setString(4, userEmail);
        }

        ps.executeUpdate();
        con.close();

        response.sendRedirect("Dashboard.jsp"); // ✅ redirect to dashboard after submit/update
    } catch(Exception e){
        out.println("Error: "+e.getMessage());
        e.printStackTrace();
    }
}
%>

</div>
</body>
</html>