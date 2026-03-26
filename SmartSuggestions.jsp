<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page import="java.sql.*"%>

<%
String email = (String) session.getAttribute("user");
if(email == null){
    response.sendRedirect("signin.jsp");
    return;
}

double income = 0, totalExpense = 0, savings = 0;
double food = 0, travel = 0;

// DB
try{
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget","root","yashu316378");

    // income + savings
    PreparedStatement ps1 = con.prepareStatement("SELECT income, savings_target FROM user WHERE email=?");
    ps1.setString(1,email);
    ResultSet rs1 = ps1.executeQuery();
    if(rs1.next()){
        income = rs1.getDouble("income");
        savings = rs1.getDouble("savings_target");
    }

    // total expense
    PreparedStatement ps2 = con.prepareStatement("SELECT SUM(amount) as total FROM expenses WHERE email=?");
    ps2.setString(1,email);
    ResultSet rs2 = ps2.executeQuery();
    if(rs2.next()){
        totalExpense = rs2.getDouble("total");
    }

    // category
    PreparedStatement ps3 = con.prepareStatement("SELECT category, SUM(amount) as total FROM expenses WHERE email=? GROUP BY category");
    ps3.setString(1,email);
    ResultSet rs3 = ps3.executeQuery();
    while(rs3.next()){
        String cat = rs3.getString("category");
        double amt = rs3.getDouble("total");

        if("Food".equals(cat)) food = amt;
        else if("Travel".equals(cat)) travel = amt;
    }

    con.close();
}catch(Exception e){
    e.printStackTrace();
}

double remainingBudget = income - totalExpense;

double savingsProgress = 0;
if(savings > 0){
    savingsProgress = ((income-totalExpense)/savings) * 100;
}
%>

<!DOCTYPE html>
<html>
<head>
<title>Smart Suggestions</title>

<style>
body{
    font-family: Arial;
    background: linear-gradient(to right, #87ceeb, #e0f7ff);
    padding:20px;
}

.container{
    background:white;
    padding:20px;
    border-radius:10px;
    max-width:600px;
    margin:auto;
    box-shadow:0 4px 10px rgba(0,0,0,0.1);
}

h2{
    text-align:center;
}

.suggestion{
    padding:10px;
    margin:10px 0;
    border-radius:5px;
    font-weight:bold;
}

.red{ background:#ffe5e5; color:red; }
.green{ background:#e6ffe6; color:green; }
.yellow{ background:#fff5cc; }
</style>
</head>

<body>

<div class="container">
<h2>Smart Financial Suggestions</h2>

<%
if(totalExpense > income){
%>
<div class="suggestion red">
⚠ Your expenses exceeded your income!
</div>
<%
}

if(food > (0.4 * totalExpense)){
%>
<div class="suggestion yellow">
🍔 You are spending too much on Food.
</div>
<%
}

if(travel > (0.3 * totalExpense)){
%>
<div class="suggestion yellow">
🚗 Travel expenses are high.
</div>
<%
}

if(remainingBudget > 0){
%>
<div class="suggestion green">
💡 You have ₹<%=remainingBudget%> remaining. Good job!
</div>
<%
}

if(savingsProgress < 50){
%>
<div class="suggestion red">
📉 Savings progress is low.
</div>
<%
}

if(savingsProgress >= 80){
%>
<div class="suggestion green">
🎉 You are close to your savings goal!
</div>
<%
}
%>

</div>

</body>
</html>