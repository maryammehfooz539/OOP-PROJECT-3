<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- customers.jsp: table of all customers with edit and delete --%>
<c:set var="pageTitle" value="Customers"/>
<c:set var="activePage" value="customers"/>
<%@ include file="includes/header.jsp" %>

<div class="text-end mb-3">
    <a href="${ctx}/customers?action=new" class="btn btn-gold"><i class="bi bi-person-plus"></i> Add New Customer</a>
</div>

<div class="table-card">
    <table class="table align-middle mb-0">
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Phone</th>
                <th>Address</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="c" items="${customers}">
                <tr>
                    <td>${c.id}</td>
                    <td><c:out value="${c.name}"/></td>
                    <td><c:out value="${c.phone}"/></td>
                    <td><c:out value="${c.address}"/></td>
                    <td class="text-nowrap">
                        <a href="${ctx}/customers?action=edit&id=${c.id}" class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-pencil"></i>
                        </a>
                        <a href="${ctx}/customers?action=delete&id=${c.id}" class="btn btn-sm btn-outline-danger"
                           onclick="return confirm('Are you sure you want to delete this customer?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty customers}">
                <tr>
                    <td colspan="5" class="text-center text-muted py-4">No customers found.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="includes/footer.jsp" %>
