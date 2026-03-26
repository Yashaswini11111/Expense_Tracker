<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%
// Check if user is logged in
String email = (String) session.getAttribute("user");
if(email == null){
response.sendRedirect("signin.jsp");
return;
}

// Fetch user data
String name = "";
double income = 0, savingsTarget = 0, totalExpense = 0, remainingBudget = 0;
Map<String, Double> categoryExpenses = new LinkedHashMap<>(); // for charts

try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget","root","yashu316378");

    // User info
    PreparedStatement psUser = con.prepareStatement("SELECT name, income, savings_target FROM user WHERE email=?");
    psUser.setString(1,email);
    ResultSet rsUser = psUser.executeQuery();
    if(rsUser.next()){
        name = rsUser.getString("name");
        income = rsUser.getDouble("income");
        savingsTarget = rsUser.getDouble("savings_target");
    }
    rsUser.close();
    psUser.close();

    // Total expenses
    PreparedStatement psTotal = con.prepareStatement("SELECT SUM(amount) as total FROM expenses WHERE email=?");
    psTotal.setString(1,email);
    ResultSet rsTotal = psTotal.executeQuery();
    if(rsTotal.next()){
        totalExpense = rsTotal.getDouble("total");
    }
    rsTotal.close();
    psTotal.close();

    // Remaining Budget calculation
    remainingBudget = income - totalExpense;

    // Expenses by category
    PreparedStatement psCat = con.prepareStatement(
        "SELECT category, SUM(amount) as total FROM expenses WHERE email=? GROUP BY category"
    );
    psCat.setString(1,email);
    ResultSet rsCat = psCat.executeQuery();
    while(rsCat.next()){
        categoryExpenses.put(rsCat.getString("category"), rsCat.getDouble("total"));
    }
    rsCat.close();
    psCat.close();

    con.close();
} catch(Exception e){
    e.printStackTrace();
}

// Prepare data for Chart.js
StringBuilder catLabels = new StringBuilder();
StringBuilder catData = new StringBuilder();
for(Map.Entry<String, Double> entry : categoryExpenses.entrySet()){
    if(catLabels.length() > 0){
        catLabels.append(",");
        catData.append(",");
    }
    catLabels.append("'").append(entry.getKey()).append("'");
    catData.append(entry.getValue());
}

// ===== Savings progress based on income and savings target =====
double savingsProgress = 0;
if(savingsTarget > 0){
    savingsProgress = ((income - totalExpense) / savingsTarget) * 100;
    if(savingsProgress > 100) savingsProgress = 100;
    if(savingsProgress < 0) savingsProgress = 0;
}

%>

<!DOCTYPE html><html>
<head>
<title>Track Expenses</title>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<style>
body{
    margin:0;
    font-family: Arial, Helvetica, sans-serif;
    background: linear-gradient(to right, #87ceeb, #e0f7ff);
}
.container{
    max-width:900px;
    margin:40px auto;
    background:white;
    padding:20px;
    border-radius:15px;
    box-shadow:0 8px 25px rgba(0,0,0,0.2);
}
h2, h3{color:#0077cc; text-align:center;}
.chart-container{
    display:flex;
    justify-content:space-around;
    flex-wrap:wrap;
    gap:30px;
    margin-top:30px;
}
.chart-box{
    width: 400px;
    height: 300px;
    background:#f2f9ff;
    padding:20px;
    border-radius:10px;
    box-shadow:0 4px 10px rgba(0,0,0,0.1);
}
.progress-bar-container{
    background:#e0f7ff;
    border-radius:25px;
    overflow:hidden;
    margin:20px 0;
}
.progress-bar{
    height:25px;
    line-height:25px;
    color:white;
    text-align:center;
    font-weight:bold;
    border-radius:25px;
}
</style>
</head>
<body>
<div class="container"><h2>Welcome, <%=name%>!</h2><p style="text-align:center;">
Income: ₹<%=income%> | 
Savings Target: ₹<%=savingsTarget%> | 
Total Expenses: ₹<%=totalExpense%> | 
Remaining Budget: ₹<%=remainingBudget%>
</p><h3>Savings Goal Progress</h3>
<div class="progress-bar-container">
    <div class="progress-bar" style="width:<%= (int)savingsProgress %>%;
         background: linear-gradient(90deg,#ff6384,#36a2eb,#ffcd56,#4bc0c0);">
        <%= (int)savingsProgress %>%
    </div>
</div><div class="chart-container">
    <div class="chart-box">
        <h3>Expenses by Category (Pie Chart)</h3>
        <canvas id="pieChart"></canvas>
    </div>
    <div class="chart-box">
        <h3>Expenses by Category (Bar Chart)</h3>
        <canvas id="barChart"></canvas>
    </div>
</div>
</div><script>
const pieCtx = document.getElementById('pieChart').getContext('2d');
const pieChart = new Chart(pieCtx, {
    type: 'pie',
    data: {
        labels: [<%=catLabels.toString()%>],
        datasets: [{
            data: [<%=catData.toString()%>],
            backgroundColor: [
                '#0077cc','#00aaff','#87ceeb','#cce6ff','#99ccff','#66b2ff'
            ]
        }]
    },
    options: {
        responsive:true,
        plugins:{
            legend:{ position:'bottom' }
        }
    }
});

const barCtx = document.getElementById('barChart').getContext('2d');
const barChart = new Chart(barCtx, {
    type: 'bar',
    data: {
        labels: [<%=catLabels.toString()%>],
        datasets: [{
            label: 'Expenses ₹',
            data: [<%=catData.toString()%>],
            backgroundColor: [
                '#0077cc','#00aaff','#87ceeb','#cce6ff','#99ccff','#66b2ff'
            ]
        }]
    },
    options:{
        responsive:true,
        scales:{
            y:{ beginAtZero:true }
        },
        plugins:{
            legend:{ display:false }
        }
    }
});
</script></body>
</html>