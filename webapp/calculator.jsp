<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- calculator.jsp: the Price Calculator (data comes from CalculatorServlet, maths is in the PriceQuote class).
     It uses the latest gold, silver and diamond rates saved on the Rates page. --%>
<c:set var="pageTitle" value="Price Calculator"/>
<c:set var="activePage" value="calculator"/>
<%@ include file="includes/header.jsp" %>

<%-- latest rates as plain numbers (used by the small script at the bottom to fill the rate box) --%>
<c:if test="${not empty latestRate}">
    <fmt:formatNumber var="goldRate" value="${latestRate.gold}" groupingUsed="false" maxFractionDigits="2"/>
    <fmt:formatNumber var="silverRate" value="${latestRate.silver}" groupingUsed="false" maxFractionDigits="2"/>
</c:if>

<c:if test="${not empty error}">
    <div class="alert alert-danger"><i class="bi bi-exclamation-circle"></i> <c:out value="${error}"/></div>
</c:if>

<div class="row g-4">
    <%-- Left: what to calculate --%>
    <div class="col-12 col-xl-6">
        <div class="soft-card">
            <h4>Price Details</h4>
            <p class="text-muted small">
                Price = weight x gold rate + making charges + stones - discount.
                <c:if test="${not empty latestRate}">Latest saved rates are from <strong><c:out value="${latestRate.date}"/></strong>
                    (change them on the <a href="${ctx}/rates">Rates page</a>).</c:if>
            </p>

            <form action="${ctx}/calculator" method="post" class="needs-validation" id="calcForm">
                <div class="mb-3">
                    <label class="form-label">Pick an item (optional, fills weight and metal)</label>
                    <select name="itemId" class="form-select"
                            onchange="if (this.value) { window.location = '${ctx}/calculator?itemId=' + this.value; }">
                        <option value="">-- Type the weight myself --</option>
                        <c:forEach var="it" items="${items}">
                            <option value="${it.id}" ${selectedItem.id == it.id ? 'selected' : ''}>
                                <c:out value="${it.name}"/> (${it.weight} g)
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Metal</label>
                        <select name="metal" id="metal" class="form-select" required data-msg="Please choose a metal."
                                data-gold="${goldRate}" data-silver="${silverRate}">
                            <c:forEach var="m" items="${metals}">
                                <option value="${m}" ${m == f_metal ? 'selected' : ''}><c:out value="${m}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Weight (grams)</label>
                        <input type="text" name="weight" class="form-control" value="<c:out value='${f_weight}'/>"
                               required inputmode="decimal" maxlength="8" autocomplete="off"
                               data-only="decimal" data-minval="0.01" data-maxval="10000"
                               data-msg="Weight must be a number from 0.01 to 10000.">
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label">Rate per tola (Rs.) <span class="text-muted small">- 24K gold rate, or silver rate</span></label>
                    <input type="text" name="ratePerTola" id="ratePerTola" class="form-control" value="<c:out value='${f_rate}'/>"
                           required inputmode="decimal" maxlength="12" autocomplete="off"
                           data-only="decimal" data-minval="1" data-maxval="100000000"
                           data-msg="Rate must be a number more than 0.">
                    <div class="form-text">Karat is handled automatically (for example 22K = 22/24 of the 24K rate).</div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Making charges are</label>
                        <select name="makingType" class="form-select" required>
                            <option value="percent" ${f_makingType == 'percent' ? 'selected' : ''}>Percent (%) of metal</option>
                            <option value="perGram" ${f_makingType == 'perGram' ? 'selected' : ''}>Rs. per gram</option>
                            <option value="flat" ${f_makingType == 'flat' ? 'selected' : ''}>Flat amount (Rs.)</option>
                        </select>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Making charges value</label>
                        <input type="text" name="makingValue" class="form-control"
                               value="<c:out value='${empty f_makingValue ? 10 : f_makingValue}'/>"
                               required inputmode="decimal" maxlength="12" autocomplete="off"
                               data-only="decimal" data-minval="0" data-maxval="100000000"
                               data-msg="Enter a number (0 if there are no making charges).">
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Diamond / stones (carats)</label>
                        <input type="text" name="carats" class="form-control"
                               value="<c:out value='${empty f_carats ? 0 : f_carats}'/>"
                               required inputmode="decimal" maxlength="8" autocomplete="off"
                               data-only="decimal" data-minval="0" data-maxval="10000"
                               data-msg="Enter the carats (0 if there are no stones).">
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Diamond rate (Rs. per carat)</label>
                        <input type="text" name="diamondRate" class="form-control"
                               value="<c:out value='${empty f_diamondRate ? 0 : f_diamondRate}'/>"
                               required inputmode="decimal" maxlength="12" autocomplete="off"
                               data-only="decimal" data-minval="0" data-maxval="100000000"
                               data-msg="Enter a number (0 if not needed).">
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-4">
                        <label class="form-label">Other charges (Rs.)</label>
                        <input type="text" name="otherCharges" class="form-control"
                               value="<c:out value='${empty f_other ? 0 : f_other}'/>"
                               required inputmode="decimal" maxlength="12" autocomplete="off"
                               data-only="decimal" data-minval="0" data-maxval="100000000"
                               data-msg="Enter a number (0 if there are none).">
                    </div>
                    <div class="col-md-6 mb-4">
                        <label class="form-label">Discount (%)</label>
                        <input type="text" name="discount" class="form-control"
                               value="<c:out value='${empty f_discount ? 0 : f_discount}'/>"
                               required inputmode="decimal" maxlength="6" autocomplete="off"
                               data-only="decimal" data-minval="0" data-maxval="100"
                               data-msg="Discount must be between 0 and 100.">
                    </div>
                </div>

                <button type="submit" class="btn btn-gold"><i class="bi bi-calculator"></i> Calculate Price</button>
                <a href="${ctx}/calculator" class="btn btn-outline-secondary">Reset</a>
            </form>
        </div>
    </div>

    <%-- Right: the result --%>
    <div class="col-12 col-xl-6">
        <div class="soft-card cart-summary">
            <h4>Price Breakdown</h4>
            <c:choose>
                <c:when test="${empty quote}">
                    <p class="text-muted mb-0">Fill in the details and press <strong>Calculate Price</strong>. The full breakdown appears here.</p>
                </c:when>
                <c:otherwise>
                    <table class="table align-middle mb-3">
                        <tbody>
                            <tr>
                                <td>Metal price<br>
                                    <span class="small text-muted"><c:out value="${quote.metal}"/> &middot;
                                        <fmt:formatNumber value="${quote.weight}" pattern="#,##0.##"/> g =
                                        <fmt:formatNumber value="${quote.tola}" pattern="0.000"/> tola</span></td>
                                <td class="text-end text-nowrap">Rs. <fmt:formatNumber value="${quote.metalCost}" pattern="#,##0"/></td>
                            </tr>
                            <tr>
                                <td>Making charges</td>
                                <td class="text-end text-nowrap">Rs. <fmt:formatNumber value="${quote.makingCharges}" pattern="#,##0"/></td>
                            </tr>
                            <tr>
                                <td>Diamond / stones</td>
                                <td class="text-end text-nowrap">Rs. <fmt:formatNumber value="${quote.stoneCharges}" pattern="#,##0"/></td>
                            </tr>
                            <tr>
                                <td>Other charges</td>
                                <td class="text-end text-nowrap">Rs. <fmt:formatNumber value="${quote.otherCharges}" pattern="#,##0"/></td>
                            </tr>
                            <tr>
                                <td><strong>Subtotal</strong></td>
                                <td class="text-end text-nowrap"><strong>Rs. <fmt:formatNumber value="${quote.subtotal}" pattern="#,##0"/></strong></td>
                            </tr>
                            <tr>
                                <td>Discount (<fmt:formatNumber value="${quote.discountPercent}" pattern="#,##0.##"/>%)</td>
                                <td class="text-end text-nowrap text-danger">- Rs. <fmt:formatNumber value="${quote.discountAmount}" pattern="#,##0"/></td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="d-flex justify-content-between align-items-center p-3 rounded-3" style="background: var(--blush);">
                        <span class="fs-5">Selling Price</span>
                        <strong class="fs-3">Rs. <fmt:formatNumber value="${quote.total}" pattern="#,##0"/></strong>
                    </div>
                    <c:if test="${not empty selectedItem}">
                        <p class="small text-muted mt-3 mb-0">
                            The price listed for <strong><c:out value="${selectedItem.name}"/></strong> is
                            Rs. <fmt:formatNumber value="${selectedItem.price}" pattern="#,##0"/>.
                        </p>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script>
    // when the metal changes, the rate box gets the saved rate of that metal (silver or gold)
    document.getElementById('metal').addEventListener('change', function () {
        var rate = this.value === 'Silver' ? this.getAttribute('data-silver') : this.getAttribute('data-gold');
        if (rate) { document.getElementById('ratePerTola').value = rate; }
    });
</script>

<%@ include file="includes/footer.jsp" %>
