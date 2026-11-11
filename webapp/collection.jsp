<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- collection.jsp: the collection page (Add to Cart puts the piece in the cart, see cart.jsp). The pieces come from the Jewellery Items list
     (through CollectionServlet), so there is nothing to type here.
     To add a piece: Jewellery Items -> Add New Item (choose a category and paste the picture link).
     Each section is a slider: the Back / Next buttons move the cards sideways. The newest piece comes first. --%>
<c:set var="pageTitle" value="Our Collection"/>
<c:set var="activePage" value="collection"/>
<%@ include file="includes/header.jsp" %>
<%-- Jewellery icons (one small drawing for each section). They are used below with <use href="#icon-..."> --%>
<svg width="0" height="0" style="position:absolute" aria-hidden="true">
    <symbol id="icon-rings" viewBox="0 0 24 24">
        <path d="M9 6 L10.5 3.5 H13.5 L15 6 L12 9.3 Z M9 6 H15"/>
        <circle cx="12" cy="15.5" r="6"/>
    </symbol>
    <symbol id="icon-necklaces" viewBox="0 0 24 24">
        <path d="M4 3.5 C4 11.5 8 15 12 15 C16 15 20 11.5 20 3.5"/>
        <path d="M12 15 L9.6 18 L12 21.5 L14.4 18 Z"/>
    </symbol>
    <symbol id="icon-earrings" viewBox="0 0 24 24">
        <circle cx="12" cy="4.8" r="1.8"/>
        <path d="M12 6.6 V9.5"/>
        <path d="M12 9.5 C8.6 13.3 8.6 17 12 20.5 C15.4 17 15.4 13.3 12 9.5 Z"/>
    </symbol>
    <symbol id="icon-bracelets" viewBox="0 0 24 24">
        <ellipse cx="12" cy="10" rx="9" ry="5.5"/>
        <path d="M6 8.2 C9 6.6 15 6.6 18 8.2"/>
        <path d="M12 15.5 V17"/>
        <circle cx="12" cy="19" r="2"/>
    </symbol>
    <symbol id="icon-anklets" viewBox="0 0 24 24">
        <path d="M3 8 Q12 18 21 8"/>
        <path d="M7.5 11.9 V13.2 M12 13.1 V14.4 M16.5 11.9 V13.2"/>
        <circle cx="7.5" cy="14.6" r="1.4"/>
        <circle cx="12" cy="15.8" r="1.4"/>
        <circle cx="16.5" cy="14.6" r="1.4"/>
    </symbol>
    <symbol id="icon-nosepins" viewBox="0 0 24 24">
        <circle cx="12" cy="12" r="3.2"/>
        <path d="M12 3.5 V6.3 M12 17.7 V20.5 M3.5 12 H6.3 M17.7 12 H20.5 M6 6 L8 8 M16 16 L18 18 M18 6 L16 8 M8 16 L6 18"/>
    </symbol>
    <symbol id="icon-armcuffs" viewBox="0 0 24 24">
        <ellipse cx="12" cy="7.5" rx="7.5" ry="2.7"/>
        <path d="M4.5 7.5 V16.5 C4.5 19.3 19.5 19.3 19.5 16.5 V7.5"/>
        <circle cx="12" cy="14.5" r="1.4"/>
    </symbol>
    <symbol id="icon-bangles" viewBox="0 0 24 24">
        <ellipse cx="12" cy="6.5" rx="8.5" ry="3"/>
        <ellipse cx="12" cy="12" rx="8.5" ry="3"/>
        <ellipse cx="12" cy="17.5" rx="8.5" ry="3"/>
    </symbol>
    <symbol id="icon-bridalsets" viewBox="0 0 24 24">
        <path d="M3 18 L4.8 8.5 L9.3 13 L12 6 L14.7 13 L19.2 8.5 L21 18 Z"/>
        <path d="M3 21 H21"/>
        <circle cx="4.8" cy="6.6" r="1.1"/>
        <circle cx="12" cy="4" r="1.1"/>
        <circle cx="19.2" cy="6.6" r="1.1"/>
    </symbol>
    <symbol id="icon-maangtikkas" viewBox="0 0 24 24">
        <path d="M12 3 C8 3.4 5 5.6 3.8 9 M12 3 C16 3.4 19 5.6 20.2 9"/>
        <circle cx="3.8" cy="10.3" r="1.2"/>
        <circle cx="20.2" cy="10.3" r="1.2"/>
        <circle cx="12" cy="8.6" r="2.4"/>
        <path d="M12 11 V12.4"/>
        <path d="M12 12.4 C9.6 15 9.6 17.8 12 20.5 C14.4 17.8 14.4 15 12 12.4 Z"/>
    </symbol>
    <symbol id="icon-others" viewBox="0 0 24 24">
        <path d="M6 3 H18 L22 9 L12 21 L2 9 Z M2 9 H22 M9 3 L7 9 L12 21 M15 3 L17 9 L12 21"/>
    </symbol>
</svg>

<%-- Section buttons: always 2 equal rows (10 sections = 5 on top, 5 below), spread over the full width --%>
<c:if test="${!cartView}">
<div class="section-pills" style="--cols: ${pillColumns};">
    <c:forEach var="sec" items="${sections}">
        <a href="#${sec.id}" class="section-pill">
            <svg class="jicon"><use href="#icon-${sec.id}"/></svg>
            <c:out value="${sec.title}"/> <span class="count">${sec.count}</span>
        </a>
    </c:forEach>
</div>
</c:if>
<div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
    <%-- Every customer has an own cart: choose whose cart the "Add to Cart" buttons fill --%>
    <form action="${ctx}/cart" method="post" class="d-flex align-items-center gap-2">
        <input type="hidden" name="action" value="select">
        <input type="hidden" name="back" value="collection">
        <label class="mb-0 fw-semibold text-nowrap"><i class="bi bi-person-heart"></i> Cart for:</label>
        <select name="customerId" class="form-select form-select-sm" style="min-width: 230px;"
                aria-label="Choose the customer" onchange="this.form.submit()">
            <option value="">-- Select customer --</option>
            <c:forEach var="cu" items="${customers}">
                <option value="${cu.id}" ${cu.id == cartCustomerId ? 'selected' : ''}><c:out value="${cu.name}"/></option>
            </c:forEach>
        </select>
    </form>
    <c:if test="${!cartView}"><span class="text-muted">${totalPieces} pieces in the collection</span></c:if>
    <c:if test="${cartView}"><span class="text-muted">Showing only the pieces in this customer's cart</span></c:if>
    <div class="d-flex flex-wrap gap-2">
        <c:choose>
            <c:when test="${cartView}">
                <a href="${ctx}/collection" class="btn btn-outline-secondary btn-sm"><i class="bi bi-grid"></i> Browse full collection</a>
                <c:if test="${not empty cartLines}">
                    <a href="${ctx}/cart" class="btn btn-gold btn-sm"><i class="bi bi-bag-check"></i> Open Cart / Checkout</a>
                </c:if>
            </c:when>
            <c:otherwise>
                <a href="${ctx}/collection?view=cart" class="btn btn-rose btn-sm">
                    <i class="bi bi-bag-heart"></i> View Cart (<span class="cart-count-mine">${cartMine}</span>)
                </a>
                <a href="${ctx}/items?action=new" class="btn btn-gold btn-sm"><i class="bi bi-plus-circle"></i> Add New Piece</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<c:choose>
<%-- ===== Cart view: only the pieces of the chosen customer's cart (or "Cart is empty") ===== --%>
<c:when test="${cartView}">
    <c:choose>
        <c:when test="${empty cartLines}">
            <div class="soft-card cart-empty">
                <i class="bi bi-bag-heart"></i>
                <h3 class="mt-3">Cart is empty</h3>
                <p class="text-muted"><c:out value="${cartCustomerName}"/> has no pieces in the cart yet.</p>
                <a href="${ctx}/collection" class="btn btn-gold"><i class="bi bi-collection"></i> Browse Collection to add pieces</a>
            </div>
        </c:when>
        <c:otherwise>
            <h3 class="collection-title">
                <i class="bi bi-bag-heart"></i> Cart of <c:out value="${cartCustomerName}"/>
                <span class="add-piece-link">Total: Rs. <fmt:formatNumber value="${cartTotal}" pattern="#,##0"/></span>
            </h3>
            <div class="cart-cards">
                <c:forEach var="line" items="${cartLines}">
                    <c:set var="i" value="${line.item}"/>
                    <c:choose>
                        <c:when test="${i.externalImage}"><c:set var="imgSrc" value="${i.imagePath}"/></c:when>
                        <c:otherwise><c:set var="imgSrc" value="${ctx}/${i.imagePath}"/></c:otherwise>
                    </c:choose>
                    <div class="product-card">
                        <div class="product-img">
                            <img src="<c:out value='${imgSrc}'/>" alt="<c:out value='${i.name}'/>"
                                 onerror="this.onerror=null;this.src='${ctx}/${i.defaultImage}'">
                        </div>
                        <div class="product-body">
                            <h5><c:out value="${i.name}"/></h5>
                            <p class="small text-muted mb-1">
                                <c:out value="${i.metalType}"/> &middot;
                                <fmt:formatNumber value="${i.weight}" minFractionDigits="1" maxFractionDigits="2"/> g
                            </p>
                            <p class="product-price mb-1">Rs. <fmt:formatNumber value="${i.price}" pattern="#,##0"/></p>
                            <p class="small mb-2"><span class="badge bg-dark">In cart: ${line.quantity}</span>
                                <span class="text-muted">= Rs. <fmt:formatNumber value="${line.total}" pattern="#,##0"/></span></p>
                            <form action="${ctx}/cart" method="post">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="back" value="cartview">
                                <input type="hidden" name="customerId" value="${cartCustomerId}">
                                <input type="hidden" name="itemId" value="${i.id}">
                                <button type="submit" class="btn btn-outline-danger btn-sm w-100"><i class="bi bi-trash"></i> Remove from cart</button>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</c:when>

<%-- ===== Full collection (all pieces, section by section) ===== --%>
<c:otherwise>
<c:forEach var="sec" items="${sections}">
    <c:url var="addUrl" value="/items">
        <c:param name="action" value="new"/>
        <c:param name="category" value="${sec.addCategory}"/>
    </c:url>
    <h3 class="collection-title" id="${sec.id}">
        <svg class="jicon"><use href="#icon-${sec.id}"/></svg> <c:out value="${sec.title}"/>
        <a href="${addUrl}" class="add-piece-link"><i class="bi bi-plus-circle"></i> Add piece</a>
    </h3>

    <c:if test="${sec.count == 0}">
        <p class="text-muted mb-5">No pieces here yet. Click "Add piece" and it will appear here.</p>
    </c:if>

    <c:if test="${sec.count > 0}">
        <%-- One slider: Back button, a row of cards, Next button --%>
        <div class="slider mb-5">
            <button type="button" class="slide-btn slide-prev" aria-label="Back"><i class="bi bi-chevron-left"></i></button>

            <div class="slide-track">
                <c:forEach var="i" items="${sec.items}">
                    <div class="slide-card">
                        <div class="product-card">
                            <div class="product-img">
                                <svg class="jicon jicon-big"><use href="#icon-${sec.id}"/></svg>
                                <%-- Picture: an internet link is used as it is; pictures inside the project need the website path in front.
                                     If the picture cannot load, the ready-made picture of the category is shown instead. --%>
                                <c:choose>
                                    <c:when test="${i.externalImage}"><c:set var="imgSrc" value="${i.imagePath}"/></c:when>
                                    <c:otherwise><c:set var="imgSrc" value="${ctx}/${i.imagePath}"/></c:otherwise>
                                </c:choose>
                                <img src="<c:out value='${imgSrc}'/>" alt="<c:out value='${i.name}'/>"
                                     onerror="this.onerror=null;this.src='${ctx}/${i.defaultImage}'">
                            </div>
                            <div class="product-body">
                                <h5><c:out value="${i.name}"/></h5>
                                <p class="small text-muted mb-1">
                                    <c:out value="${i.metalType}"/> &middot;
                                    <fmt:formatNumber value="${i.weight}" minFractionDigits="1" maxFractionDigits="2"/> g
                                </p>
                                <p class="product-price mb-1">Rs. <fmt:formatNumber value="${i.price}" pattern="#,##0"/></p>
                                <p class="small mb-2">
                                    <c:choose>
                                        <c:when test="${i.quantity <= 0}"><span class="badge bg-secondary">Out of stock</span></c:when>
                                        <c:when test="${i.lowStock}"><span class="badge bg-danger">Only ${i.quantity} left</span></c:when>
                                        <c:otherwise><span class="badge bg-success">In stock: ${i.quantity}</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <div class="d-grid gap-2">
                                    <c:choose>
                                        <c:when test="${i.quantity > 0}">
                                            <%-- Add to Cart: sent to CartServlet in the background (js/app.js), the page does not reload --%>
                                            <form action="${ctx}/cart" method="post" class="js-add-cart">
                                                <input type="hidden" name="action" value="add">
                                                <input type="hidden" name="itemId" value="${i.id}">
                                                <input type="hidden" name="back" value="collection">
                                                <button type="submit" class="btn btn-rose btn-sm w-100">
                                                    <i class="bi bi-cart-plus"></i> Add to Cart
                                                </button>
                                            </form>
                                            <a href="${ctx}/sales?action=new&itemId=${i.id}" class="btn btn-gold btn-sm">Buy Now</a>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn btn-secondary btn-sm" disabled>Out of Stock</button>
                                        </c:otherwise>
                                    </c:choose>
                                    <a href="${ctx}/items?action=edit&id=${i.id}" class="btn btn-outline-secondary btn-sm">
                                        <i class="bi bi-pencil"></i> Edit / Add Picture
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <button type="button" class="slide-btn slide-next" aria-label="Next"><i class="bi bi-chevron-right"></i></button>
        </div>
    </c:if>
</c:forEach>
</c:otherwise>
</c:choose>

<script>
    // Slider buttons: Back and Next move the cards of that section sideways
    var sliders = document.querySelectorAll(".slider");
    for (var s = 0; s < sliders.length; s++) {
        setupSlider(sliders[s]);
    }

    function setupSlider(slider) {
        var track = slider.querySelector(".slide-track");
        var backButton = slider.querySelector(".slide-prev");
        var nextButton = slider.querySelector(".slide-next");

        // greys out a button when there is nothing more to see, hides both if all cards fit
        function updateButtons() {
            var maxScroll = track.scrollWidth - track.clientWidth;
            if (maxScroll <= 2) {
                slider.classList.add("no-scroll");
            } else {
                slider.classList.remove("no-scroll");
            }
            backButton.disabled = track.scrollLeft <= 2;
            nextButton.disabled = track.scrollLeft >= maxScroll - 2;
        }

        backButton.addEventListener("click", function () {
            track.scrollBy({ left: -track.clientWidth * 0.8, behavior: "smooth" });
        });
        nextButton.addEventListener("click", function () {
            track.scrollBy({ left: track.clientWidth * 0.8, behavior: "smooth" });
        });
        track.addEventListener("scroll", updateButtons);
        window.addEventListener("resize", updateButtons);
        updateButtons();
    }
</script>

<%@ include file="includes/footer.jsp" %>
