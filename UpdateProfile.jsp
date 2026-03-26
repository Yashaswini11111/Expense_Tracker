<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*" %>
<%
    String user = (String) session.getAttribute("user");
    if(user == null){
        response.sendRedirect("signin.jsp");
        return;
    }

    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget", "root", "yashu316378");

    // Handle form submission for Update
    String nameParam = request.getParameter("name");
    String incomeParam = request.getParameter("income");
    String savingsParam = request.getParameter("savings");
    String targetExpenseParam = request.getParameter("target_expense");

    if(nameParam != null && incomeParam != null && savingsParam != null && targetExpenseParam != null){
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE user SET name=?, income=?, savings=?, target_expense=? WHERE email=?"
        );
        ps.setString(1, nameParam);
        ps.setDouble(2, Double.parseDouble(incomeParam));
        ps.setDouble(3, Double.parseDouble(savingsParam));
        ps.setDouble(4, Double.parseDouble(targetExpenseParam));
        ps.setString(5, user);
        ps.executeUpdate();
        ps.close();
        conn.close();
        response.sendRedirect("Dashboard.jsp"); // Redirect after update
        return;
    }

    // Fetch existing user data
    String currentName = "";
    String currentIncome = "";
    String currentSavings = "";
    String currentTarget = "";

    PreparedStatement psFetch = conn.prepareStatement("SELECT * FROM user WHERE email=?");
    psFetch.setString(1, user);
    ResultSet rs = psFetch.executeQuery();
    if(rs.next()){
        currentName = rs.getString("name");
        currentIncome = String.valueOf(rs.getDouble("income"));
        currentSavings = String.valueOf(rs.getDouble("savings"));
        currentTarget = String.valueOf(rs.getDouble("target_expense"));
    }
    rs.close();
    psFetch.close();
    conn.close();
%>

<!DOCTYPE html>
<html>
<head>
    <title>Update Profile</title>
    <style>
        body {
            margin:0; font-family: Arial, Helvetica, sans-serif;
            display:flex; justify-content:center; align-items:center;
            height:100vh;
            background: linear-gradient(to right, #87ceeb, #e0f7ff);
        }
        .container {
            width:450px;
            padding:35px;
            background:white;
            border-radius:15px;
            box-shadow:0 8px 25px rgba(0,0,0,0.2);
            text-align:center;
        }
        h2 { color:#0077cc; margin-bottom:20px; }
        input { width:100%; padding:12px; margin:10px 0; border:1px solid #ccc; border-radius:8px; outline:none; }
        input:focus { border-color:#0077cc; box-shadow:0 0 8px rgba(0,119,204,0.4); }
        button {
            width:100%; padding:12px; margin-top:15px;
            background:#0077cc; color:white; border:none; border-radius:25px;
            font-size:16px; font-weight:bold; cursor:pointer; transition:0.3s;
        }
        button:hover { background:#005fa3; transform:scale(1.03); }
    </style>
</head>
<body>
<div class="container">
    <h2>Update Profile</h2>
    <form method="post" action="Profile.jsp">
        <input type="text" name="name" placeholder="Name" value="<%= currentName %>" required>
        <input type="number" name="income" placeholder="Income" value="<%= currentIncome %>" required>
        <input type="number" name="savings" placeholder="Savings" value="<%= currentSavings %>" required>
        <input type="number" name="target_expense" placeholder="Target Expenses" value="<%= currentTarget %>" required>
        <button type="submit">Update & Submit</button>
    </form>
</div>
</body>
</html>