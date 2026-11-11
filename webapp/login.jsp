<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- login.jsp: the login page (shown first). The page opens ONLY with the correct password. --%>
<c:if test="${not empty sessionScope.admin}">
    <c:redirect url="/dashboard"/>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login | Gemora</title>
    <link href="${pageContext.request.contextPath}/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/vendor/bootstrap-icons/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;700&family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/favicon.svg">
    <link rel="alternate icon" type="image/png" href="${pageContext.request.contextPath}/images/favicon.png">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body class="login-page">
    <div class="login-card">
        <div class="text-center mb-4">
            <img src="${pageContext.request.contextPath}/images/favicon.svg" alt="logo" width="64" height="64" class="mb-2">
            <h2>GEMORA</h2>
            <p class="text-muted mb-0">Enter the password to continue</p>
        </div>

        <c:if test="${not empty requestScope.error}">
            <div class="alert alert-danger py-2">
                <i class="bi bi-exclamation-triangle"></i> <c:out value="${requestScope.error}"/>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post" class="needs-validation">
            <div class="mb-4">
                <label class="form-label" for="password">Password</label>
                <div class="input-group">
                    <input type="password" id="password" name="password" class="form-control" placeholder="Enter password"
                           maxlength="30" required autofocus autocomplete="current-password"
                           data-msg="Please enter the password.">
                    <button type="button" class="btn btn-outline-secondary" data-toggle-password="password"
                            aria-label="Show or hide the password"><i class="bi bi-eye"></i></button>
                </div>
            </div>
            <button type="submit" class="btn btn-gold w-100">
                <i class="bi bi-box-arrow-in-right"></i> Login
            </button>
        </form>
    </div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
