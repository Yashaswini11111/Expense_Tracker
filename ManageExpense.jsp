<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*" %>
<%
    String user = (String) session.getAttribute("user");
    if(user == null){
        response.sendRedirect("signin.jsp");
        return;
    }

    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget", "root", "yashu316378");

    // Handle Add / Update
    String idParam = request.getParameter("id");
    String categoryParam = request.getParameter("category");
    String amountParam = request.getParameter("amount");
    String dateParam = request.getParameter("date");

    if(categoryParam != null && amountParam != null && dateParam != null){
        if(idParam == null || idParam.isEmpty()){
            // Add new expense
            PreparedStatement ps = conn.prepareStatement("INSERT INTO expenses(email, category, amount, date) VALUES(?,?,?,?)");
            ps.setString(1, user);
            ps.setString(2, categoryParam);
            ps.setDouble(3, Double.parseDouble(amountParam));
            ps.setString(4, dateParam);
            ps.executeUpdate();
            ps.close();
        } else {
            // Update existing expense
            PreparedStatement ps = conn.prepareStatement("UPDATE expenses SET category=?, amount=?, date=? WHERE id=? AND email=?");
            ps.setString(1, categoryParam);
            ps.setDouble(2, Double.parseDouble(amountParam));
            ps.setString(3, dateParam);
            ps.setInt(4, Integer.parseInt(idParam));
            ps.setString(5, user);
            ps.executeUpdate();
            ps.close();
        }
        response.sendRedirect("ManageExpenses.jsp");
        return;
    }

    // Handle Delete
    String deleteId = request.getParameter("deleteId");
    if(deleteId != null){
        PreparedStatement ps = conn.prepareStatement("DELETE FROM expenses WHERE id=? AND email=?");
        ps.setInt(1, Integer.parseInt(deleteId));
        ps.setString(2, user);
        ps.executeUpdate();
        ps.close();
        response.sendRedirect("ManageExpenses.jsp");
        return;
    }

    // Handle Edit
    String editId = request.getParameter("editId");
    String editCategory = "";
    String editAmount = "";
    String editDate = "";
    if(editId != null){
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM expenses WHERE id=? AND email=?");
        ps.setInt(1, Integer.parseInt(editId));
        ps.setString(2, user);
        ResultSet rsEdit = ps.executeQuery();
        if(rsEdit.next()){
            editCategory = rsEdit.getString("category");
            editAmount = String.valueOf(rsEdit.getDouble("amount"));
            editDate = rsEdit.getString("date");
        }
        rsEdit.close();
        ps.close();
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Manage Expenses</title>
    <style>
        body {
            font-family: Arial, Helvetica, sans-serif;
            background: linear-gradient(to right, #87ceeb, #e0f7ff);
            margin: 0; padding: 0;
        }
        .container {
            width: 750px;
            margin: 50px auto;
            background: white;
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 8px 25px rgba(0,0,0,0.2);
        }
        h2 { color: #0077cc; text-align: center; }
        input, select, button {
            width: 100%;
            padding: 10px;
            margin: 8px 0;
            border-radius: 8px;
            border: 1px solid #ccc;
            box-sizing: border-box;
        }
        button {
            background: #0077cc; color: white; border: none; font-weight: bold; cursor: pointer; transition: 0.3s;
        }
        button:hover { background: #005fa3; }
        table { width: 100%; border-collapse: collapse; margin-top: 25px; }
        table, th, td { border: 1px solid #ccc; }
        th, td { padding: 10px; text-align: center; }
        th { background-color: #0077cc; color: white; }
        .action-btn { width: auto; padding: 5px 10px; }
        .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h2>Manage Expenses</h2>
        <p>Welcome, <b><%= user %></b></p>
    </div>

    <!-- Add / Update Form -->
    <form method="post" action="ManageExpense.jsp">
        <input type="hidden" name="id" value="<%= editId != null ? editId : "" %>">
        <select name="category" required>
            <option value="">Select Category</option>
            <option value="Food" <%= "Food".equals(editCategory) ? "selected" : "" %>>Food</option>
            <option value="Travel" <%= "Travel".equals(editCategory) ? "selected" : "" %>>Travel</option>
            <option value="Shopping" <%= "Shopping".equals(editCategory) ? "selected" : "" %>>Shopping</option>
            <option value="Others" <%= "Others".equals(editCategory) ? "selected" : "" %>>Others</option>
        </select>
        <input type="number" name="amount" placeholder="Amount" value="<%= editAmount %>" required>
        <input type="date" name="date" value="<%= editDate %>" required>
        <button type="submit"><%= editId != null ? "Update Expense" : "Add Expense" %></button>
    </form>

    <!-- List of Expenses -->
    <h3>Your Expenses</h3>
    <table>
        <tr>
            <th>Category</th>
            <th>Amount</th>
            <th>Date</th>
            <th>Actions</th>
        </tr>
        <%
            PreparedStatement psList = conn.prepareStatement("SELECT * FROM expenses WHERE email=? ORDER BY date DESC");
            psList.setString(1, user);
            ResultSet rs = psList.executeQuery();
            while(rs.next()){
        %>
        <tr>
            <td><%= rs.getString("category") %></td>
            <td><%= rs.getDouble("amount") %></td>
            <td><%= rs.getDate("date") %></td>
            <td>
                <a href="ManageExpense.jsp?editId=<%= rs.getInt("id") %>"><button type="button" class="action-btn">Edit</button></a>
                <a href="ManageExpense.jsp?deleteId=<%= rs.getInt("id") %>" onclick="return confirm('Delete this expense?');">
                    <button type="button" class="action-btn">Delete</button>
                </a>
            </td>
        </tr>
        <% } rs.close(); psList.close(); conn.close(); %>
    </table>
</div>
</body>
</html>