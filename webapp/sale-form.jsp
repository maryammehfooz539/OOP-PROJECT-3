<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- sale-form.jsp: choose a customer, an item and a quantity --%>
<c:set var="pageTitle" value="New Sale"/>
<c:set var="activePage" value="sales"/>
<c:set var="backHref" value="${pageContext.request.contextPath}/sales"/>
<%@ include file="includes/header.jsp" %>

<div class="soft-card form-card narrow">
    <form action="${ctx}/sales" method="post" class="needs-validation">

        <div class="mb-3">
            <label class="form-label">Customer</label>
            <select name="customerId" class="form-select" required data-msg="Please select a customer.">
                <option value="">-- Select customer --</option>
                <c:forEach var="c" items="${customers}">
                    <option value="${c.id}" ${submitted and param.customerId == c.id ? 'selected' : ''}>
                        <c:out value="${c.name}"/> (<c:out value="${c.phone}"/>)
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Jewellery Item</label>
            <select name="itemId" class="form-select" required data-msg="Please select an item.">
                <option value="">-- Select item --</option>
                <c:forEach var="i" items="${items}">
                    <option value="${i.id}" ${param.itemId == i.id ? 'selected' : ''}>
                        <c:out value="${i.name}"/> - Rs. <fmt:formatNumber value="${i.price}" pattern="#,##0"/> (Stock: ${i.quantity})
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="mb-4">
            <label class="form-label">Quantity</label>
            <br>
            <%-- Quantity box with - and + buttons --%>
            <div class="qty-stepper" data-min="1" data-max="100000">
                <button type="button" class="qty-btn" data-step="-1" aria-label="Less">&minus;</button>
                <input type="text" name="quantity" class="qty-input" value="<c:out value='${submitted ? param.quantity : 1}'/>"
                       inputmode="numeric" maxlength="6" required autocomplete="off" data-msg="Quantity must be 1 or more.">
                <button type="button" class="qty-btn" data-step="1" aria-label="More">+</button>
            </div>
        </div>

        <button type="submit" class="btn btn-gold"><i class="bi bi-check-circle"></i> Save Sale</button>
        <a href="${ctx}/sales" class="btn btn-outline-secondary">Cancel</a>
    </form>
</div>

<%@ include file="includes/footer.jsp" %>
