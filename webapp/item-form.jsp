<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- item-form.jsp: one form used for both Add and Edit --%>
<c:set var="pageTitle" value="${not empty (submitted ? param.id : item.id) ? 'Edit Item' : 'Add New Item'}"/>
<c:set var="activePage" value="items"/>
<c:set var="backHref" value="${pageContext.request.contextPath}/items"/>
<%@ include file="includes/header.jsp" %>

<%-- If the form was submitted with an error, show what the user typed. Otherwise show the item (edit) or empty (add). --%>
<c:set var="f_id" value="${submitted ? param.id : item.id}"/>
<c:set var="f_name" value="${submitted ? param.name : item.name}"/>
<c:set var="f_category" value="${submitted ? param.category : (empty item ? param.category : item.category)}"/>
<c:set var="f_metal" value="${submitted ? param.metalType : item.metalType}"/>
<%-- weight and price are written as plain numbers (no 1.0E7 style) so the number check accepts them --%>
<c:choose>
    <c:when test="${submitted}">
        <c:set var="f_weight" value="${param.weight}"/>
        <c:set var="f_price" value="${param.price}"/>
        <c:set var="f_qty" value="${param.quantity}"/>
    </c:when>
    <c:when test="${not empty item}">
        <fmt:formatNumber var="f_weight" value="${item.weight}" groupingUsed="false" maxFractionDigits="2"/>
        <fmt:formatNumber var="f_price" value="${item.price}" groupingUsed="false" maxFractionDigits="2"/>
        <c:set var="f_qty" value="${item.quantity}"/>
    </c:when>
    <c:otherwise>
        <c:set var="f_weight" value=""/>
        <c:set var="f_price" value=""/>
        <c:set var="f_qty" value="1"/>
    </c:otherwise>
</c:choose>
<c:set var="f_image" value="${submitted ? param.imageUrl : item.imageUrl}"/>

<%-- Centred form card. Every box is checked in the browser (js/app.js) and again in ItemServlet. --%>
<div class="soft-card form-card">
    <form action="${ctx}/items" method="post" enctype="multipart/form-data" class="needs-validation">
        <input type="hidden" name="id" value="<c:out value='${f_id}'/>">

        <div class="mb-3">
            <label class="form-label">Item Name</label>
            <input type="text" name="name" class="form-control" value="<c:out value='${f_name}'/>"
                   required minlength="2" maxlength="50" autocomplete="off"
                   data-allow="\p{L}0-9 &amp;.'()-"
                   data-msg="Item name must be 2 to 50 characters.">
        </div>

        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Category</label>
                <input type="text" name="category" list="categoryList" class="form-control"
                       placeholder="Choose or type..." value="<c:out value='${f_category}'/>"
                       required minlength="2" maxlength="30" autocomplete="off"
                       data-allow="\p{L} "
                       data-warn="You can not enter numbers or symbols in the category. Only letters are allowed."
                       data-msg="Category must be 2 to 30 letters (no numbers).">
                <datalist id="categoryList">
                    <option value="Ring"></option>
                    <option value="Necklace"></option>
                    <option value="Earring"></option>
                    <option value="Bracelet"></option>
                    <option value="Anklet"></option>
                    <option value="Nose Pin"></option>
                    <option value="Arm Cuff"></option>
                    <option value="Bangle"></option>
                    <option value="Bridal Set"></option>
                    <option value="Maang Tikka"></option>
                </datalist>
                <div class="form-text">The Collection page puts the piece in the matching section.</div>
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Metal Type</label>
                <input type="text" name="metalType" class="form-control" placeholder="Gold 22K, Silver..."
                       value="<c:out value='${f_metal}'/>"
                       required minlength="2" maxlength="30" autocomplete="off"
                       data-allow="\p{L}0-9 .-"
                       data-msg="Metal type must be 2 to 30 characters, like Gold 22K.">
            </div>
        </div>

        <div class="row">
            <div class="col-md-4 mb-3">
                <label class="form-label">Weight (grams)</label>
                <input type="text" name="weight" class="form-control" value="<c:out value='${f_weight}'/>"
                       required inputmode="decimal" maxlength="8" autocomplete="off"
                       data-only="decimal" data-minval="0.01" data-maxval="10000"
                       data-msg="Weight must be a number from 0.01 to 10000.">
            </div>
            <div class="col-md-4 mb-3">
                <label class="form-label">Price (Rs.)</label>
                <input type="text" name="price" class="form-control" value="<c:out value='${f_price}'/>"
                       required inputmode="decimal" maxlength="12" autocomplete="off"
                       data-only="decimal" data-minval="1" data-maxval="100000000"
                       data-msg="Price must be a number from 1 to 100,000,000.">
            </div>
            <div class="col-md-4 mb-3">
                <label class="form-label">Quantity (stock)</label>
                <br>
                <%-- Quantity box with - and + buttons (no small arrows) --%>
                <div class="qty-stepper" data-min="0" data-max="100000">
                    <button type="button" class="qty-btn" data-step="-1" aria-label="Less">&minus;</button>
                    <input type="text" name="quantity" class="qty-input" value="<c:out value='${f_qty}'/>"
                           inputmode="numeric" maxlength="6" required autocomplete="off" data-msg="Quantity must be a whole number.">
                    <button type="button" class="qty-btn" data-step="1" aria-label="More">+</button>
                </div>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label">Picture from your computer (optional)</label>
            <input type="file" name="imageFile" class="form-control"
                   accept=".jpg,.jpeg,.png,.gif,.webp,.svg,image/*">
            <div class="form-text">JPG, PNG, GIF, WEBP or SVG, up to 3 MB. The picture is saved inside the project, so it shows on every computer.</div>
        </div>

        <div class="mb-4">
            <label class="form-label">Or a Picture Link (optional)</label>
            <input type="text" name="imageUrl" class="form-control" placeholder="https://..."
                   value="<c:out value='${f_image}'/>" maxlength="300"
                   pattern="(https?://[^ ]+|images/[^ ]+|uploads/[^ ]+)?"
                   data-msg="The link must start with http:// or https:// and have no spaces.">
            <div class="form-text">Paste an internet link of the picture (for example from Pinterest: right click the picture, "Copy image address"). The program downloads the picture and saves it inside the project, so it keeps showing even without internet. Needs internet at that moment. If you choose a file above, the file is used. If nothing is given, a ready-made picture of the category is shown.</div>
        </div>

        <button type="submit" class="btn btn-gold"><i class="bi bi-check-circle"></i> Save Item</button>
        <a href="${ctx}/items" class="btn btn-outline-secondary">Cancel</a>
    </form>
</div>

<%@ include file="includes/footer.jsp" %>
