<%@ page language="java" import="java.sql.*" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String userEmail = (String) session.getAttribute("user");
    if(userEmail == null){
        response.sendRedirect("signin.jsp");
        return;
    }

    String id = request.getParameter("id");
    String category = request.getParameter("category");
    String amountStr = request.getParameter("amount");
    String date = request.getParameter("date");

    if(category != null && amountStr != null && date != null){
        double amount = Double.parseDouble(amountStr);
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/budget", "root", "yashu316378"
            );
            PreparedStatement ps;
            if(id != null && !id.isEmpty()){
                // Update
                ps = conn.prepareStatement(
                    "UPDATE expenses SET category=?, amount=?, date=? WHERE id=? AND email=?"
                );
                ps.setString(1, category);
                ps.setDouble(2, amount);
                ps.setString(3, date);
                ps.setInt(4, Integer.parseInt(id));
                ps.setString(5, userEmail);
            } else {
                // Add
                ps = conn.prepareStatement(
                    "INSERT INTO expenses (email, category, amount, date) VALUES (?, ?, ?, ?)"
                );
                ps.setString(1, userEmail);
                ps.setString(2, category);
                ps.setDouble(3, amount);
                ps.setString(4, date);
            }
            ps.executeUpdate();
            conn.close();
            response.sendRedirect("ManageExpense.jsp");
        }catch(Exception e){
            out.println("Error: " + e.getMessage());
        }
    } else {
        response.sendRedirect("ManageExpense.jsp");
    }
%>