<%@ page language="java" import="java.sql.*" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String userEmail = (String) session.getAttribute("user");
    if(userEmail == null){
        response.sendRedirect("signin.jsp");
        return;
    }

    String category = request.getParameter("category");
    String amountStr = request.getParameter("amount");
    String date = request.getParameter("date");

    if(category != null && amountStr != null && date != null){
        double amount = Double.parseDouble(amountStr);
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/budget",
                "root",
                "yashu316378"
            );
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO expenses (email, category, amount, date) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, userEmail);
            ps.setString(2, category);
            ps.setDouble(3, amount);
            ps.setString(4, date);
            ps.executeUpdate();
            conn.close();

            response.sendRedirect("ManageExpense.jsp"); // Redirect back to manage expenses page

        } catch(Exception e){
            out.println("Error: " + e.getMessage());
        }
    } else {
        response.sendRedirect("ManageExpenses.jsp");
    }
%>