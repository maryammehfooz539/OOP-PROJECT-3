<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- customer-form.jsp: one form used for both Add and Edit --%>
<c:set var="pageTitle" value="${not empty (submitted ? param.id : customer.id) ? 'Edit Customer' : 'Add New Customer'}"/>
<c:set var="activePage" value="customers"/>
<c:set var="backHref" value="${pageContext.request.contextPath}/customers"/>
<%@ include file="includes/header.jsp" %>

<c:set var="f_id" value="${submitted ? param.id : customer.id}"/>
<c:set var="f_name" value="${submitted ? param.name : customer.name}"/>
<c:set var="f_phone" value="${submitted ? param.phone : customer.phone}"/>
<c:set var="f_address" value="${submitted ? param.address : customer.address}"/>

<%-- The form card is centred in the page (class form-card). Rules: name = letters only, phone = digits only. --%>
<div class="soft-card form-card narrow">
    <form action="${ctx}/customers" method="post" class="needs-validation">
        <input type="hidden" name="id" value="<c:out value='${f_id}'/>">

        <div class="mb-3">
            <label class="form-label">Customer Name</label>
            <input type="text" name="name" class="form-control" value="<c:out value='${f_name}'/>"
                   required minlength="2" maxlength="50" autocomplete="off"
                   data-allow="\p{L} .'-"
                   data-warn="You can not enter numbers or symbols in the name. Only letters are allowed."
                   data-msg="Name must be 2 to 50 letters (no numbers).">
        </div>
        <div class="mb-3">
            <label class="form-label">Phone Number</label>
            <%-- digits only: letters and symbols are removed while typing, and the form checks 10 to 13 digits --%>
            <input type="text" name="phone" class="form-control" value="<c:out value='${f_phone}'/>"
                   required inputmode="numeric" maxlength="13" pattern="[0-9]{10,13}" autocomplete="off"
                   data-only="digits"
                   data-msg="Phone must be 10 to 13 digits (numbers only).">
        </div>
        <div class="mb-4">
            <label class="form-label">Address</label>
            <input type="text" name="address" class="form-control" value="<c:out value='${f_address}'/>"
                   required minlength="5" maxlength="100" autocomplete="off"
                   data-allow="\p{L}0-9 .#/'-"
                   data-msg="Address must be 5 to 100 characters (letters, numbers, spaces and . # / -).">
        </div>

        <button type="submit" class="btn btn-gold"><i class="bi bi-check-circle"></i> Save Customer</button>
        <a href="${ctx}/customers" class="btn btn-outline-secondary">Cancel</a>
    </form>
</div>

<%@ include file="includes/footer.jsp" %>
