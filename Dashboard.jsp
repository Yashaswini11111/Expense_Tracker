<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
String email = (String) session.getAttribute("user");
if(email == null){
    response.sendRedirect("signin.jsp");
    return;
}

// Fetch data
String name = "";
double income = 0, savings = 0, totalExpense = 0;
double remainingBudget = 0;

// Category-wise expenses for pie chart
double food = 0, travel = 0, shopping = 0, others = 0;

// Monthly expenses for bar chart
double jan=0,feb=0,mar=0,apr=0,may=0,jun=0,jul=0,aug=0,sep=0,oct=0,nov=0,dec=0;

try{
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget","root","yashu316378");

    // User info
    PreparedStatement ps1 = con.prepareStatement("SELECT name, income, savings_target FROM user WHERE email=?");
    ps1.setString(1,email);
    ResultSet rs1 = ps1.executeQuery();
    if(rs1.next()){
        name = rs1.getString("name");
        income = rs1.getDouble("income");
        savings = rs1.getDouble("savings_target");
    } else {
        name = "User";
    }

    // Total expenses
    PreparedStatement ps2 = con.prepareStatement("SELECT SUM(amount) as total FROM expenses WHERE email=?");
    ps2.setString(1,email);
    ResultSet rs2 = ps2.executeQuery();
    if(rs2.next()){
        totalExpense = rs2.getDouble("total");
    }

    // Remaining Budget Calculation
    remainingBudget = income - totalExpense;

    // Category-wise for pie chart
    PreparedStatement ps3 = con.prepareStatement("SELECT category, SUM(amount) as total FROM expenses WHERE email=? GROUP BY category");
    ps3.setString(1,email);
    ResultSet rs3 = ps3.executeQuery();
    while(rs3.next()){
        String cat = rs3.getString("category");
        double amt = rs3.getDouble("total");
        if("Food".equals(cat)) food = amt;
        else if("Travel".equals(cat)) travel = amt;
        else if("Shopping".equals(cat)) shopping = amt;
        else others = amt;
    }

    // Monthly expenses for bar chart
    PreparedStatement ps4 = con.prepareStatement(
        "SELECT MONTH(date) as mon, SUM(amount) as total FROM expenses WHERE email=? GROUP BY MONTH(date)");
    ps4.setString(1,email);
    ResultSet rs4 = ps4.executeQuery();
    while(rs4.next()){
        int mon = rs4.getInt("mon");
        double amt = rs4.getDouble("total");
        switch(mon){
            case 1: jan=amt; break;
            case 2: feb=amt; break;
            case 3: mar=amt; break;
            case 4: apr=amt; break;
            case 5: may=amt; break;
            case 6: jun=amt; break;
            case 7: jul=amt; break;
            case 8: aug=amt; break;
            case 9: sep=amt; break;
            case 10: oct=amt; break;
            case 11: nov=amt; break;
            case 12: dec=amt; break;
        }
    }

    con.close();
}catch(Exception e){
    e.printStackTrace();
}

// Savings progress %
double savingsProgress = 0;
if(savings > 0){
    savingsProgress = ((income-totalExpense)/savings) * 100;
    if(savingsProgress > 100) savingsProgress = 100;
    if(savingsProgress<0) savingsProgress = 0;
}
%>

<!DOCTYPE html>
<html>
<head>
<title>Dashboard</title>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>

<script type="text/javascript">
google.charts.load('current', {'packages':['corechart','bar']});
google.charts.setOnLoadCallback(drawCharts);

function drawCharts() {

    var dataPie = google.visualization.arrayToDataTable([
        ['Category', 'Amount'],
        ['Food', <%=food%>],
        ['Travel', <%=travel%>],
        ['Shopping', <%=shopping%>],
        ['Others', <%=others%>]
    ]);

    var optionsPie = {
        title: 'Expense Breakdown',
        pieHole: 0.4,
        colors: ['#ff6384', '#36a2eb', '#ffcd56', '#4bc0c0'],
        chartArea: {width:'90%', height:'70%'},
        legend: {position: 'bottom'}
    };

    var chartPie = new google.visualization.PieChart(document.getElementById('piechart'));
    chartPie.draw(dataPie, optionsPie);

    var dataBar = google.visualization.arrayToDataTable([
        ['Month', 'Expenses', { role: 'style' }],
        ['Jan', <%=jan%>, '#ff6384'],
        ['Feb', <%=feb%>, '#36a2eb'],
        ['Mar', <%=mar%>, '#ffcd56'],
        ['Apr', <%=apr%>, '#4bc0c0'],
        ['May', <%=may%>, '#ff6384'],
        ['Jun', <%=jun%>, '#36a2eb'],
        ['Jul', <%=jul%>, '#ffcd56'],
        ['Aug', <%=aug%>, '#4bc0c0'],
        ['Sep', <%=sep%>, '#ff6384'],
        ['Oct', <%=oct%>, '#36a2eb'],
        ['Nov', <%=nov%>, '#ffcd56'],
        ['Dec', <%=dec%>, '#4bc0c0']
    ]);

    var optionsBar = {
        title: 'Monthly Expenses',
        legend: { position: 'none' },
        chartArea: {width:'80%', height:'70%'},
        hAxis: {title: 'Month'},
        vAxis: {title: 'Amount (₹)'}
    };

    var chartBar = new google.visualization.ColumnChart(document.getElementById('barchart'));
    chartBar.draw(dataBar, optionsBar);

    var dataIncomeExpense = google.visualization.arrayToDataTable([
        ['Type', 'Amount'],
        ['Income', <%=income%>],
        ['Expenses', <%=totalExpense%>]
    ]);

    var optionsIncomeExpense = {
        title: 'Income vs Expenses',
        chartArea: {width:'70%', height:'70%'},
        hAxis: {title: 'Type'},
        vAxis: {title: 'Amount (₹)'},
        colors: ['#36a2eb']
    };

    var chartIncomeExpense = new google.visualization.ColumnChart(document.getElementById('incomeExpenseChart'));
    chartIncomeExpense.draw(dataIncomeExpense, optionsIncomeExpense);
}
</script>

<style>
/* NO CHANGES */
body{
    margin:0;
    font-family: Arial, Helvetica, sans-serif;
    background: linear-gradient(to right, #87ceeb, #e0f7ff);
}
.dashboard{
    display:flex;
    height:100vh;
}
.sidebar{
    width:220px;
    background:#0077cc;
    color:white;
    padding:20px;
}
.sidebar h3{ margin-bottom:20px; }
.sidebar a{
    display:block;
    color:white;
    text-decoration:none;
    padding:10px;
    border-radius:5px;
    margin-bottom:10px;
}
.sidebar a:hover{ background:#005fa3; }
.main{
    flex:1;
    padding:20px;
}
.header{
    background:white;
    padding:15px;
    border-radius:8px;
    box-shadow:0 2px 8px rgba(0,0,0,0.1);
    margin-bottom:20px;
}
.cards{
    display:flex;
    gap:20px;
    flex-wrap:wrap;
    margin-bottom:20px;
}
.card{
    background:white;
    width:180px;
    height:100px;
    display:flex;
    justify-content:center;
    align-items:center;
    border-radius:10px;
    box-shadow:0 4px 10px rgba(0,0,0,0.1);
    font-weight:bold;
    cursor:pointer;
    transition:0.3s;
    text-align:center;
}
.card:hover{
    transform:translateY(-5px);
    background:#e6f2ff;
}
#piechart, #barchart, #incomeExpenseChart{
    width: 100%;
    max-width: 500px;
    height: 300px;
    margin: 0 auto 20px auto;
}
.progress-bar-container{
    background:#e0f7ff;
    border-radius:25px;
    overflow:hidden;
    margin:10px 0 20px 0;
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

<div class="dashboard">

<div class="sidebar">
<h3>Menu</h3>
<a href="ProfilePage.jsp">Profile</a>
<a href="ManageExpense.jsp">Manage Expenses</a>
<a href="TrackExpenses.jsp">Track Expenses</a>
<a href="ExportCSV.jsp">Export CSV</a>
<a href="SmartSuggestions.jsp">Smart Suggestions</a>
</div>

<div class="main">

<div class="header">

<h2>Welcome, <%=name%>!</h2>

<p>
Income: ₹<%=income%> |
Savings Target: ₹<%=savings%> |
Expenses: ₹<%=totalExpense%> |
Remaining Budget: ₹<%=remainingBudget%>
</p>

<%
if(remainingBudget < 0){
%>
<p style="color:red; font-weight:bold;">
⚠ Warning: Your expenses exceeded your income!
</p>
<%
}
%>

<h3>Savings Goal Progress</h3>

<div class="progress-bar-container">
<div class="progress-bar" style="width:<%= (int)savingsProgress %>%;
background: linear-gradient(90deg,#ff6384,#36a2eb,#ffcd56,#4bc0c0);">
<%= (int)savingsProgress %>%
</div>
</div>

<div class="cards">
<a href="ManageExpense.jsp"><div class="card">Add Expense</div></a>
<a href="ViewExpense.jsp"><div class="card">View Expenses</div></a>
<a href="ProfilePage.jsp"><div class="card">Update Profile</div></a>
<a href="ExportCSV.jsp"><div class="card">Export CSV</div></a>
<a href="SmartSuggestions.jsp"><div class="card">Smart Suggestions</div></a>
</div>

<div id="piechart"></div>
<div id="barchart"></div>
<div id="incomeExpenseChart"></div>



</div>
</div>
</div>

</body>
</html>