<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- sales.jsp: sales history table --%>
<c:set var="pageTitle" value="Sales History"/>
<c:set var="activePage" value="sales"/>
<%@ include file="includes/header.jsp" %>

<div class="text-end mb-3">
    <a href="${ctx}/sales?action=new" class="btn btn-gold"><i class="bi bi-cart-plus"></i> New Sale</a>
</div>

<div class="table-card">
    <table class="table align-middle mb-0">
        <thead>
            <tr>
                <th>Bill No</th>
                <th>Date</th>
                <th>Customer</th>
                <th>Item</th>
                <th>Qty</th>
                <th>Unit Price (Rs.)</th>
                <th>Total (Rs.)</th>
                <th>Bill</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="s" items="${sales}">
                <tr>
                    <td>#${s.id}</td>
                    <td><c:out value="${s.date}"/></td>
                    <td><c:out value="${s.customerName}"/></td>
                    <td><c:out value="${s.itemName}"/></td>
                    <td>${s.quantity}</td>
                    <td><fmt:formatNumber value="${s.unitPrice}" pattern="#,##0.00"/></td>
                    <td><strong><fmt:formatNumber value="${s.total}" pattern="#,##0.00"/></strong></td>
                    <td>
                        <a href="${ctx}/sales?action=bill&id=${s.id}" class="btn btn-sm btn-outline-secondary">
                            <i class="bi bi-printer"></i> View
                        </a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty sales}">
                <tr>
                    <td colspan="8" class="text-center text-muted py-4">No sales yet. Make your first sale!</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="includes/footer.jsp" %>
