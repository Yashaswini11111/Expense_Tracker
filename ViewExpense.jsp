<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*" %>
<%
    String user = (String) session.getAttribute("user");
    if(user == null){
        response.sendRedirect("signin.jsp");
        return;
    }

    double totalExpense = 0;
%>
<!DOCTYPE html>
<html>
<head>
    <title>View Expenses</title>
    <style>
        body {
            margin:0;
            font-family: Arial, Helvetica, sans-serif;
            background: linear-gradient(to right, #87ceeb, #e0f7ff);
        }
        .container {
            width: 800px;
            margin: 50px auto;
            background: white;
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 8px 25px rgba(0,0,0,0.2);
        }
        h2 { color: #0077cc; text-align: center; margin-bottom: 20px; }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        table, th, td { border: 1px solid #ccc; }
        th, td { padding: 12px; text-align: center; }
        th { background-color: #0077cc; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        .total {
            text-align: right;
            font-weight: bold;
            margin-top: 10px;
            font-size: 18px;
            color: #0077cc;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Your Expenses</h2>
    <table>
        <tr>
            <th>Category</th>
            <th>Amount (₹)</th>
            <th>Date</th>
        </tr>
        <%
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/budget","root","yashu316378"
                );
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM expenses WHERE email=? ORDER BY date DESC"
                );
                ps.setString(1, user);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    totalExpense += rs.getDouble("amount");
        %>
        <tr>
            <td><%= rs.getString("category") %></td>
            <td><%= rs.getDouble("amount") %></td>
            <td><%= rs.getDate("date") %></td>
        </tr>
        <%
                }
                rs.close();
                ps.close();
                conn.close();
            }catch(Exception e){
                out.println("<tr><td colspan='3'>Error: " + e.getMessage() + "</td></tr>");
            }
        %>
    </table>
    <p class="total">Total Expenses: ₹<%= totalExpense %></p>
</div>
</body>
</html>