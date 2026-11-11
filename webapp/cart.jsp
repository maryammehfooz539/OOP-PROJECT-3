<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- cart.jsp: the carts page (data comes from CartServlet). EVERY CUSTOMER HAS AN OWN CART.
     On top: the customers who have a cart, and a drop-down to open the cart of any customer.
     Below: the cart of the chosen customer. Here the admin changes the quantity with - and +,
     removes pieces and checks out: checkout saves one sale for every piece for that customer. --%>
<c:set var="pageTitle" value="Customer Carts"/>
<c:set var="activePage" value="cart"/>
<%@ include file="includes/header.jsp" %>

<%-- Customers who have pieces in their cart, and a drop-down to open the cart of any customer --%>
<div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-3">
    <div class="status-tabs mb-0">
        <c:forEach var="sm" items="${summaries}">
            <a href="${ctx}/cart?customerId=${sm.customerId}" class="status-tab ${customer.id == sm.customerId ? 'active' : ''}">
                <i class="bi bi-person"></i> <c:out value="${sm.customerName}"/> <span class="count">${sm.pieces}</span>
            </a>
        </c:forEach>
        <c:if test="${empty summaries}"><span class="text-muted">No customer has pieces in a cart yet.</span></c:if>
    </div>
    <div class="d-flex align-items-center gap-2">
        <label class="mb-0 fw-semibold text-nowrap">Open cart of:</label>
        <select class="form-select form-select-sm" style="min-width: 220px;" aria-label="Open the cart of a customer"
                onchange="if (this.value) { window.location = '${ctx}/cart?customerId=' + this.value; }">
            <option value="">-- Select customer --</option>
            <c:forEach var="cu" items="${customers}">
                <option value="${cu.id}" ${cu.id == customer.id ? 'selected' : ''}><c:out value="${cu.name}"/></option>
            </c:forEach>
        </select>
    </div>
</div>

<c:choose>
    <%-- Nobody chosen yet --%>
    <c:when test="${empty customer}">
        <div class="soft-card cart-empty">
            <i class="bi bi-person-heart"></i>
            <h3 class="mt-3">Choose a customer</h3>
            <p class="text-muted">Every customer has an own cart. Choose a customer above, then add pieces from the Collection.</p>
            <a href="${ctx}/collection" class="btn btn-gold"><i class="bi bi-collection"></i> Go to Collection</a>
        </div>
    </c:when>

    <%-- This customer's cart is empty --%>
    <c:when test="${empty lines}">
        <div class="soft-card cart-empty">
            <i class="bi bi-bag-heart"></i>
            <h3 class="mt-3">The cart of <c:out value="${customer.name}"/> is empty</h3>
            <p class="text-muted">Choose pieces from the Collection and press "Add to Cart".</p>
            <a href="${ctx}/collection" class="btn btn-gold"><i class="bi bi-collection"></i> Go to Collection</a>
        </div>
    </c:when>

    <c:otherwise>
        <h4 class="mb-3"><i class="bi bi-bag-heart"></i> Cart of <c:out value="${customer.name}"/></h4>
        <div class="row g-4">
            <%-- Left: the pieces in the cart --%>
            <div class="col-12 col-xl-8">
                <div class="table-card">
                    <table class="table align-middle mb-0">
                        <thead>
                            <tr><th>Piece</th><th>Price</th><th>Quantity</th><th>Total</th><th></th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="line" items="${lines}">
                                <c:choose>
                                    <c:when test="${line.item.externalImage}"><c:set var="imgSrc" value="${line.item.imagePath}"/></c:when>
                                    <c:otherwise><c:set var="imgSrc" value="${ctx}/${line.item.imagePath}"/></c:otherwise>
                                </c:choose>
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center gap-3">
                                            <img class="cart-thumb" src="<c:out value='${imgSrc}'/>" alt=""
                                                 onerror="this.onerror=null;this.src='${ctx}/${line.item.defaultImage}'">
                                            <div>
                                                <strong><c:out value="${line.item.name}"/></strong><br>
                                                <span class="small text-muted"><c:out value="${line.item.metalType}"/> &middot; In stock: ${line.item.quantity}</span>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="text-nowrap">Rs. <fmt:formatNumber value="${line.item.price}" pattern="#,##0"/></td>
                                    <td>
                                        <%-- - and + change the quantity; the cart saves it by itself --%>
                                        <form action="${ctx}/cart" method="post">
                                            <input type="hidden" name="action" value="update">
                                            <input type="hidden" name="customerId" value="${customer.id}">
                                            <input type="hidden" name="itemId" value="${line.item.id}">
                                            <div class="qty-stepper small" data-min="1" data-max="${line.item.quantity}" data-autosubmit="true">
                                                <button type="button" class="qty-btn" data-step="-1" aria-label="Less">&minus;</button>
                                                <input type="text" name="quantity" class="qty-input" value="${line.quantity}" inputmode="numeric" maxlength="6">
                                                <button type="button" class="qty-btn" data-step="1" aria-label="More">+</button>
                                            </div>
                                        </form>
                                    </td>
                                    <td class="text-nowrap"><strong>Rs. <fmt:formatNumber value="${line.total}" pattern="#,##0"/></strong></td>
                                    <td>
                                        <form action="${ctx}/cart" method="post">
                                            <input type="hidden" name="action" value="remove">
                                            <input type="hidden" name="customerId" value="${customer.id}">
                                            <input type="hidden" name="itemId" value="${line.item.id}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Remove from cart"><i class="bi bi-trash"></i></button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="d-flex flex-wrap gap-2 mt-3">
                    <a href="${ctx}/collection" class="btn btn-outline-secondary"><i class="bi bi-arrow-left"></i> Continue shopping</a>
                    <form action="${ctx}/cart" method="post" class="d-inline">
                        <input type="hidden" name="action" value="clear">
                        <input type="hidden" name="customerId" value="${customer.id}">
                        <button type="submit" class="btn btn-outline-danger"><i class="bi bi-x-circle"></i> Clear this cart</button>
                    </form>
                </div>
            </div>

            <%-- Right: summary and checkout (for this customer) --%>
            <div class="col-12 col-xl-4">
                <div class="soft-card cart-summary">
                    <h4>Order Summary</h4>
                    <div class="d-flex justify-content-between mt-3"><span class="text-muted">Customer</span><strong><c:out value="${customer.name}"/></strong></div>
                    <div class="d-flex justify-content-between"><span class="text-muted">Pieces</span><strong>${cartPieces}</strong></div>
                    <div class="d-flex justify-content-between mb-3"><span class="text-muted">Total</span>
                        <strong class="fs-5">Rs. <fmt:formatNumber value="${cartTotal}" pattern="#,##0"/></strong></div>
                    <hr>
                    <form action="${ctx}/cart" method="post">
                        <input type="hidden" name="action" value="checkout">
                        <input type="hidden" name="customerId" value="${customer.id}">
                        <button type="submit" class="btn btn-gold w-100"><i class="bi bi-bag-check"></i> Checkout for <c:out value="${customer.name}"/></button>
                    </form>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="includes/footer.jsp" %>
