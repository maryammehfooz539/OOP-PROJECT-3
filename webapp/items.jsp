<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- items.jsp: table of all jewellery items with search, edit and delete --%>
<c:set var="pageTitle" value="Jewellery Items"/>
<c:set var="activePage" value="items"/>
<%@ include file="includes/header.jsp" %>

<div class="row g-2 mb-3">
    <div class="col-12 col-md-8">
        <form action="${ctx}/items" method="get" class="d-flex gap-2">
            <input type="text" name="q" class="form-control" placeholder="Search by item name..." maxlength="50"
                   value="<c:out value='${query}'/>">
            <button type="submit" class="btn btn-rose"><i class="bi bi-search"></i></button>
            <a href="${ctx}/items" class="btn btn-outline-secondary">Reset</a>
        </form>
    </div>
    <div class="col-12 col-md-4 text-md-end">
        <a href="${ctx}/items?action=new" class="btn btn-gold"><i class="bi bi-plus-circle"></i> Add New Item</a>
    </div>
</div>

<div class="table-card">
    <table class="table align-middle mb-0">
        <thead>
            <tr>
                <th>#</th>
                <th>Photo</th>
                <th>Name</th>
                <th>Category</th>
                <th>Metal</th>
                <th>Weight (g)</th>
                <th>Price (Rs.)</th>
                <th>Qty</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%-- varStatus gives the row number: the table is numbered 1, 2, 3 ... in the order shown (category by category),
                 so when a piece is added all the numbers after it change automatically. The real id stays hidden
                 and is only used for Edit and Delete. --%>
            <c:forEach var="i" items="${items}" varStatus="st">
                <tr>
                    <td>${st.count}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty i.imageUrl}">
                                <img src="<c:out value='${i.imageUrl}'/>" alt="" width="46" height="46"
                                     style="object-fit: cover; border-radius: 10px;" onerror="this.style.display='none'">
                            </c:when>
                            <c:otherwise><i class="bi bi-gem" style="color: var(--rose);"></i></c:otherwise>
                        </c:choose>
                    </td>
                    <td><c:out value="${i.name}"/></td>
                    <td><c:out value="${i.category}"/></td>
                    <td><c:out value="${i.metalType}"/></td>
                    <td><fmt:formatNumber value="${i.weight}" minFractionDigits="1" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${i.price}" pattern="#,##0.00"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${i.lowStock}">
                                <span class="badge bg-danger">${i.quantity} (Low)</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-success">${i.quantity}</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="text-nowrap">
                        <a href="${ctx}/items?action=edit&id=${i.id}" class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-pencil"></i>
                        </a>
                        <a href="${ctx}/items?action=delete&id=${i.id}" class="btn btn-sm btn-outline-danger"
                           onclick="return confirm('Are you sure you want to delete this item?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty items}">
                <tr>
                    <td colspan="9" class="text-center text-muted py-4">No items found.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="includes/footer.jsp" %>
