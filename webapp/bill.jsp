<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- bill.jsp: simple printable bill (no sidebar) --%>
<c:if test="${empty sessionScope.admin}">
    <c:redirect url="/login"/>
</c:if>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bill #${sale.id} | Jewellery Management System</title>
    <link href="${pageContext.request.contextPath}/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/vendor/bootstrap-icons/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;700&family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="icon" type="image/svg+xml" href="${ctx}/images/favicon.svg">
    <link rel="alternate icon" type="image/png" href="${ctx}/images/favicon.png">
    <link href="${ctx}/css/style.css" rel="stylesheet">
</head>
<body>
<div class="container">

    <%-- Success message (hidden when printing) --%>
    <c:if test="${not empty sessionScope.flashSuccess}">
        <div class="alert alert-success mt-3 no-print">
            <i class="bi bi-check-circle"></i> <c:out value="${sessionScope.flashSuccess}"/>
        </div>
        <c:remove var="flashSuccess" scope="session"/>
    </c:if>

    <div class="bill-box">
        <div class="text-center mb-4">
            <h2><i class="bi bi-gem"></i> Jewellery Shop</h2>
            <p class="text-muted mb-0">Sales Bill</p>
        </div>

        <div class="row mb-3">
            <div class="col-6">
                <strong>Bill No:</strong> #${sale.id}<br>
                <strong>Date:</strong> <c:out value="${sale.date}"/>
            </div>
            <div class="col-6 text-end">
                <strong>Customer:</strong> <c:out value="${sale.customerName}"/><br>
                <c:if test="${not empty customer}">
                    <strong>Phone:</strong> <c:out value="${customer.phone}"/><br>
                    <strong>Address:</strong> <c:out value="${customer.address}"/>
                </c:if>
            </div>
        </div>

        <table class="table">
            <thead>
                <tr>
                    <th>Item</th>
                    <th class="text-end">Qty</th>
                    <th class="text-end">Unit Price (Rs.)</th>
                    <th class="text-end">Amount (Rs.)</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><c:out value="${sale.itemName}"/></td>
                    <td class="text-end">${sale.quantity}</td>
                    <td class="text-end"><fmt:formatNumber value="${sale.unitPrice}" pattern="#,##0.00"/></td>
                    <td class="text-end"><fmt:formatNumber value="${sale.total}" pattern="#,##0.00"/></td>
                </tr>
                <tr class="total-row">
                    <td colspan="3" class="text-end">Grand Total</td>
                    <td class="text-end">Rs. <fmt:formatNumber value="${sale.total}" pattern="#,##0.00"/></td>
                </tr>
            </tbody>
        </table>

        <p class="text-center text-muted mt-4 mb-0">Thank you for shopping with us!</p>
    </div>

    <div class="text-center mb-5 no-print">
        <button onclick="window.print()" class="btn btn-gold"><i class="bi bi-printer"></i> Print Bill</button>
        <a href="${ctx}/sales" class="btn btn-outline-secondary">Back to Sales</a>
        <a href="${ctx}/sales?action=new" class="btn btn-rose">New Sale</a>
    </div>
</div>
</body>
</html>
