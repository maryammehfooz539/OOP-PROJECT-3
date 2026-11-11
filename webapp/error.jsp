<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- error.jsp: friendly page shown when something goes wrong (for example a data file cannot be saved,
     or a page does not exist). web.xml sends all errors here. --%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Something went wrong | Jewellery Management System</title>
    <link href="${pageContext.request.contextPath}/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/vendor/bootstrap-icons/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<div class="container py-5" style="max-width: 640px;">
    <div class="soft-card text-center">
        <h2 class="mb-3"><i class="bi bi-exclamation-triangle"></i> Something went wrong</h2>
        <p class="text-muted">
            The page could not be shown, or a data file could not be read or saved.
            Please check that the <code>data</code> folder can be written to and try again.
        </p>
        <%-- the technical message (empty for a simple "page not found") --%>
        <c:if test="${not empty requestScope['javax.servlet.error.exception']}">
            <p class="small text-danger"><c:out value="${requestScope['javax.servlet.error.exception'].message}"/></p>
        </c:if>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-gold mt-2">
            <i class="bi bi-house"></i> Back to Dashboard
        </a>
    </div>
</div>
</body>
</html>
