<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- order-form.jsp: add or edit a custom order / repair (the form is checked in the browser by js/app.js
     and again in OrderServlet) --%>
<c:set var="pageTitle" value="${empty order ? 'New Order / Repair' : 'Edit Order / Repair'}"/>
<c:set var="activePage" value="orders"/>
<c:set var="backHref" value="${pageContext.request.contextPath}/orders"/>
<%@ include file="includes/header.jsp" %>

<%-- the values shown in the form: typed values after an error, the saved order when editing, or defaults --%>
<c:choose>
    <c:when test="${submitted}">
        <c:set var="f_id" value="${param.id}"/>
        <c:set var="f_customer" value="${param.customerId}"/>
        <c:set var="f_type" value="${param.type}"/>
        <c:set var="f_desc" value="${param.description}"/>
        <c:set var="f_received" value="${param.receivedDate}"/>
        <c:set var="f_due" value="${param.dueDate}"/>
        <c:set var="f_total" value="${param.totalPrice}"/>
        <c:set var="f_advance" value="${param.advance}"/>
        <c:set var="f_status" value="${param.status}"/>
    </c:when>
    <c:when test="${not empty order}">
        <c:set var="f_id" value="${order.id}"/>
        <c:set var="f_customer" value="${order.customerId}"/>
        <c:set var="f_type" value="${order.type}"/>
        <c:set var="f_desc" value="${order.description}"/>
        <c:set var="f_received" value="${order.receivedDate}"/>
        <c:set var="f_due" value="${order.dueDate}"/>
        <fmt:formatNumber var="f_total" value="${order.totalPrice}" groupingUsed="false" maxFractionDigits="2"/>
        <fmt:formatNumber var="f_advance" value="${order.advance}" groupingUsed="false" maxFractionDigits="2"/>
        <c:set var="f_status" value="${order.status}"/>
    </c:when>
    <c:otherwise>
        <c:set var="f_received" value="${today}"/>
        <c:set var="f_advance" value="0"/>
        <c:set var="f_status" value="Received"/>
    </c:otherwise>
</c:choose>

<c:if test="${not empty error}">
    <div class="alert alert-danger"><i class="bi bi-exclamation-circle"></i> <c:out value="${error}"/></div>
</c:if>

<div class="soft-card form-card">
    <c:choose>
        <c:when test="${empty customers}">
            <p class="text-muted">You need a customer first. Add a customer, then come back to save the order.</p>
            <a href="${ctx}/customers?action=new" class="btn btn-gold"><i class="bi bi-person-plus"></i> Add Customer</a>
        </c:when>
        <c:otherwise>
    <form action="${ctx}/orders" method="post" class="needs-validation">
        <input type="hidden" name="id" value="<c:out value='${f_id}'/>">

        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Customer</label>
                <select name="customerId" class="form-select" required data-msg="Please select a customer.">
                    <option value="">-- Select customer --</option>
                    <c:forEach var="cu" items="${customers}">
                        <option value="${cu.id}" ${cu.id == f_customer ? 'selected' : ''}><c:out value="${cu.name}"/> (<c:out value="${cu.phone}"/>)</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Type</label>
                <select name="type" class="form-select" required data-msg="Please choose the type.">
                    <c:forEach var="t" items="${types}">
                        <option value="${t}" ${t == f_type ? 'selected' : ''}><c:out value="${t}"/></option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label">What has to be done?</label>
            <input type="text" name="description" class="form-control" value="<c:out value='${f_desc}'/>"
                   required minlength="5" maxlength="150" autocomplete="off"
                   data-allow="\p{L}0-9 .#/'()&amp;-"
                   data-warn="You can not enter these symbols here (no commas or special symbols)."
                   data-msg="Description must be 5 to 150 characters.">
            <div class="form-text">Example: Resize ring from size 7 to 9, or Gold bridal set with kundan work.</div>
        </div>

        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Received Date</label>
                <input type="date" name="receivedDate" class="form-control" value="<c:out value='${f_received}'/>"
                       required data-msg="Please choose the received date.">
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Promised (Due) Date</label>
                <input type="date" name="dueDate" class="form-control" value="<c:out value='${f_due}'/>"
                       required data-msg="Please choose the due date.">
            </div>
        </div>

        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Total Price (Rs.)</label>
                <input type="text" name="totalPrice" class="form-control" value="<c:out value='${f_total}'/>"
                       required inputmode="decimal" maxlength="12" autocomplete="off"
                       data-only="decimal" data-minval="1" data-maxval="100000000"
                       data-msg="Price must be a number from 1 to 100,000,000.">
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Advance Paid (Rs.)</label>
                <input type="text" name="advance" class="form-control" value="<c:out value='${f_advance}'/>"
                       required inputmode="decimal" maxlength="12" autocomplete="off"
                       data-only="decimal" data-minval="0" data-maxval="100000000"
                       data-msg="Advance must be a number (use 0 if nothing was paid).">
            </div>
        </div>

        <div class="mb-4">
            <label class="form-label">Status</label>
            <select name="status" class="form-select" required data-msg="Please choose the status.">
                <c:forEach var="s" items="${statuses}">
                    <option value="${s}" ${s == f_status ? 'selected' : ''}><c:out value="${s}"/></option>
                </c:forEach>
            </select>
        </div>

        <button type="submit" class="btn btn-gold"><i class="bi bi-check-circle"></i> Save</button>
        <a href="${ctx}/orders" class="btn btn-outline-secondary">Cancel</a>
    </form>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="includes/footer.jsp" %>
