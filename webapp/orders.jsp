<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- orders.jsp: "Orders & Repairs" = customer order tracking (data comes from OrderServlet).
     The buttons on top filter by status; each row has a progress bar and a quick status change. --%>
<c:set var="pageTitle" value="Orders & Repairs"/>
<c:set var="activePage" value="orders"/>
<%@ include file="includes/header.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <p class="text-muted mb-0">Track custom orders and repairs from the day they are received until they are delivered.</p>
    <a href="${ctx}/orders?action=new" class="btn btn-gold"><i class="bi bi-plus-circle"></i> New Order / Repair</a>
</div>

<%-- Status filter buttons with the number of orders in each status --%>
<div class="status-tabs">
    <c:forEach var="entry" items="${counts}">
        <c:url var="tabUrl" value="/orders">
            <c:if test="${entry.key != 'All'}"><c:param name="status" value="${entry.key}"/></c:if>
        </c:url>
        <a href="${tabUrl}" class="status-tab ${filter == entry.key ? 'active' : ''}">
            <c:out value="${entry.key}"/> <span class="count">${entry.value}</span>
        </a>
    </c:forEach>
</div>

<div class="table-card">
    <table class="table align-middle mb-0">
        <thead>
            <tr>
                <th>#</th>
                <th>Customer</th>
                <th>Job</th>
                <th>Due Date</th>
                <th>Money (Rs.)</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="o" items="${orders}">
                <tr>
                    <td>${o.id}</td>
                    <td><c:out value="${o.customerName}"/></td>
                    <td style="min-width: 240px;">
                        <span class="badge ${o.type == 'Repair' ? 'text-bg-secondary' : 'text-bg-dark'}"><c:out value="${o.type}"/></span>
                        <div class="small mt-1"><c:out value="${o.description}"/></div>
                        <%-- progress bar: shows how far the job is --%>
                        <div class="progress mt-2" style="height: 6px;">
                            <div class="progress-bar bg-${o.statusColor}" style="width: ${o.progress}%"></div>
                        </div>
                    </td>
                    <td class="text-nowrap">
                        <c:out value="${o.dueDate}"/>
                        <c:if test="${o.overdue}"><br><span class="badge text-bg-danger">Overdue</span></c:if>
                    </td>
                    <td class="small text-nowrap">
                        Total: <strong><fmt:formatNumber value="${o.totalPrice}" pattern="#,##0"/></strong><br>
                        Paid: <fmt:formatNumber value="${o.advance}" pattern="#,##0"/><br>
                        <c:choose>
                            <c:when test="${o.balance > 0}"><span class="text-danger">Balance: <fmt:formatNumber value="${o.balance}" pattern="#,##0"/></span></c:when>
                            <c:otherwise><span class="text-success">Fully paid</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td style="min-width: 150px;">
                        <span class="badge text-bg-${o.statusColor}"><c:out value="${o.status}"/></span>
                        <%-- quick change of the status --%>
                        <form action="${ctx}/orders" method="post" class="mt-1">
                            <input type="hidden" name="action" value="status">
                            <input type="hidden" name="id" value="${o.id}">
                            <input type="hidden" name="filter" value="${filter}">
                            <select name="status" class="form-select form-select-sm" aria-label="Change status"
                                    onchange="this.form.submit()">
                                <c:forEach var="s" items="${statuses}">
                                    <option value="${s}" ${s == o.status ? 'selected' : ''}><c:out value="${s}"/></option>
                                </c:forEach>
                            </select>
                        </form>
                    </td>
                    <td class="text-nowrap">
                        <a href="${ctx}/orders?action=edit&id=${o.id}" class="btn btn-sm btn-outline-primary" title="Edit">
                            <i class="bi bi-pencil"></i>
                        </a>
                        <a href="${ctx}/orders?action=delete&id=${o.id}" class="btn btn-sm btn-outline-danger" title="Delete"
                           onclick="return confirm('Are you sure you want to delete this order?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty orders}">
                <tr>
                    <td colspan="7" class="text-center text-muted py-4">No orders found here.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="includes/footer.jsp" %>
