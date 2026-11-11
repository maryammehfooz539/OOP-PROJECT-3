<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- dashboard.jsp: the 4 summary cards (all the same size) (numbers come from DashboardServlet).
     Each card is a link: click it to open the matching page. --%>
<c:set var="pageTitle" value="Dashboard"/>
<c:set var="activePage" value="dashboard"/>
<%@ include file="includes/header.jsp" %>

<div class="row g-4 mb-4 align-items-stretch">
    <div class="col-12 col-sm-6 col-xl-3">
        <a href="${ctx}/items" class="stat-link">
            <div class="soft-card lift-card stat-card">
                <div class="stat-icon"><i class="bi bi-gem"></i></div>
                <div>
                    <div class="stat-number">${totalItems}</div>
                    <div class="stat-label">Total Items</div>
                </div>
            </div>
        </a>
    </div>
    <div class="col-12 col-sm-6 col-xl-3">
        <a href="${ctx}/customers" class="stat-link">
            <div class="soft-card lift-card stat-card gold">
                <div class="stat-icon"><i class="bi bi-people"></i></div>
                <div>
                    <div class="stat-number">${totalCustomers}</div>
                    <div class="stat-label">Total Customers</div>
                </div>
            </div>
        </a>
    </div>
    <div class="col-12 col-sm-6 col-xl-3">
        <a href="${ctx}/sales" class="stat-link">
            <div class="soft-card lift-card stat-card">
                <div class="stat-icon"><i class="bi bi-receipt"></i></div>
                <div>
                    <div class="stat-number">${totalSales}</div>
                    <div class="stat-label">Total Sales</div>
                </div>
            </div>
        </a>
    </div>
    <div class="col-12 col-sm-6 col-xl-3">
        <a href="${ctx}/items?filter=low" class="stat-link">
            <div class="soft-card lift-card stat-card gold">
                <div class="stat-icon"><i class="bi bi-exclamation-triangle"></i></div>
                <div>
                    <div class="stat-number">${lowStock}</div>
                    <div class="stat-label">Low Stock Items</div>
                </div>
            </div>
        </a>
    </div>
</div>

<%-- Below the cards: welcome + quick actions on the left, the model picture on the right (fills the empty space) --%>
<div class="row g-4 align-items-stretch">
    <div class="col-12 col-lg-7 d-flex flex-column gap-4">
        <div class="soft-card dash-hero">
            <h2 class="mb-2">Welcome to Gemora</h2>
            <p class="text-muted mb-0">Timeless jewellery, beautifully managed. Add pieces, serve your customers,
                make sales and watch the gold rate, all from one place.</p>
            <%-- open custom orders / repairs --%>
            <c:if test="${openOrders > 0}">
                <a href="${ctx}/orders" class="btn btn-sm btn-outline-secondary mt-3">
                    <i class="bi bi-tools"></i> ${openOrders} open order(s) / repair(s)
                    <c:if test="${overdueOrders > 0}"><span class="badge text-bg-danger ms-1">${overdueOrders} late</span></c:if>
                </a>
            </c:if>
        </div>
        <div class="soft-card flex-grow-1">
            <h4>Quick Actions</h4>
            <p class="text-muted">Jump straight to the most common jobs.</p>
            <a href="${ctx}/items?action=new" class="btn btn-gold me-2 mb-2"><i class="bi bi-plus-circle"></i> Add Item</a>
            <a href="${ctx}/customers?action=new" class="btn btn-rose me-2 mb-2"><i class="bi bi-person-plus"></i> Add Customer</a>
            <a href="${ctx}/sales?action=new" class="btn btn-gold me-2 mb-2"><i class="bi bi-receipt"></i> New Sale</a>
            <a href="${ctx}/orders?action=new" class="btn btn-rose me-2 mb-2"><i class="bi bi-tools"></i> New Order / Repair</a>
            <a href="${ctx}/calculator" class="btn btn-gold me-2 mb-2"><i class="bi bi-calculator"></i> Price Calculator</a>
            <a href="${ctx}/collection" class="btn btn-rose mb-2"><i class="bi bi-collection"></i> Open Collection</a>
        </div>
    </div>
    <div class="col-12 col-lg-5">
        <%-- Dashboard video (plays by itself, silent, in a loop). To use your own video, save it in webapp/images/ as
             dashboard.mp4 (and dashboard.webm if you have it); it replaces this one. If no video can play,
             the picture model.svg is shown instead. --%>
        <div class="soft-card model-card">
            <video class="model-video" autoplay muted loop playsinline preload="auto"
                   poster="${ctx}/images/model.svg" aria-label="Model wearing Gemora jewellery">
                <source src="${ctx}/images/dashboard.mp4" type="video/mp4">
            <source src="${ctx}/images/dashboard.webm" type="video/webm">
            </video>
            <div class="model-caption">Wear the story of Gemora</div>
        </div>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>
