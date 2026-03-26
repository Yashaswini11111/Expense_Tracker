<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<head>
<title>Sign-In</title>
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
    color:#00aaff;
    margin-bottom:20px;
}

input{
    width:100%;
    padding:12px;
    margin:10px 0;
    border:1px solid #ccc;
    border-radius:8px;
    outline:none;
    transition:0.3s;
}

input:focus{
    border-color:#00aaff;
    box-shadow:0 0 8px rgba(0,170,255,0.4);
}

button{
    width:100%;
    padding:12px;
    margin-top:15px;
    background:#00aaff;
    color:white;
    border:none;
    border-radius:25px;
    font-size:16px;
    font-weight:bold;
    cursor:pointer;
    transition:0.3s;
}

button:hover{
    background:#0077cc;
    transform:scale(1.03);
}
</style>
</head>
<body>
<div class="container">
<form action="<%= request.getContextPath() %>/LoginServlet" method="post">
<h2>Sign-In</h2>
<input type="email" name="email" placeholder="Email" required>
<br>
<br>
<input type="password" name="password" placeholder="Password" required>
<br>
<br>
<button type="submit">Sign-In</button>
</body>