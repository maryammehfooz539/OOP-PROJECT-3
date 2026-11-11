<%-- REFERENCES (libraries we did not write, bundled in webapp/vendor/ so the site works offline):
     Bootstrap 5.3.3 (getbootstrap.com, MIT licence), Bootstrap Icons 1.11.3 (icons.getbootstrap.com, MIT licence),
     Chart.js 4.4.3 (chartjs.org, MIT licence). Fonts: Google Fonts "Playfair Display" and "Poppins" (needs internet, normal fonts are used otherwise). --%>
<%-- header.jsp: top of every page (protects the page, loads CSS, opens the layout) --%>
<%-- Each page must declare the JSTL "c" taglib BEFORE including this file --%>
<c:if test="${empty sessionScope.admin}">
    <c:redirect url="/login"/>
</c:if>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${pageTitle}"/> | Gemora</title>
    <link href="${pageContext.request.contextPath}/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/vendor/bootstrap-icons/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;700&family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="icon" type="image/svg+xml" href="${ctx}/images/favicon.svg">
    <link rel="alternate icon" type="image/png" href="${ctx}/images/favicon.png">
    <link href="${ctx}/css/style.css" rel="stylesheet">
</head>
<body>
<%-- remembers if the sidebar was closed (desktop) so it does not jump when a new page opens --%>
<script>try { if (window.innerWidth > 991 && localStorage.getItem('gemoraSidebarHidden') === '1') { document.body.classList.add('sidebar-hidden'); } } catch (e) { }</script>
<div class="sidebar-backdrop" id="sidebarBackdrop"></div>
<div class="app-wrapper">

    <%@ include file="sidebar.jsp" %>

    <main class="main-content">
        <div class="top-bar">
            <div class="top-bar-left">
                <%-- the 3-line (hamburger) button: opens and closes the sidebar menu --%>
                <button type="button" class="menu-toggle" id="menuToggle" aria-label="Open or close the menu">
                    <i class="bi bi-list"></i>
                </button>
                <%-- Back button (not on the dashboard). It goes to the page you came from; if there is none,
                     it opens the page given in backHref (the list page of a form) or the dashboard. --%>
                <c:if test="${activePage != 'dashboard'}">
                    <c:choose>
                        <c:when test="${not empty backHref}"><c:set var="backTarget" value="${backHref}"/></c:when>
                        <c:otherwise><c:set var="backTarget" value="${ctx}/dashboard"/></c:otherwise>
                    </c:choose>
                    <a href="${backTarget}" class="back-btn" id="backBtn" title="Back" aria-label="Back"><i class="bi bi-arrow-left"></i></a>
                </c:if>
                <h1 class="page-title"><c:out value="${pageTitle}"/></h1>
            </div>
            <span class="welcome-text"><i class="bi bi-gem"></i> Welcome to Gemora</span>
        </div>

        <%-- Success / error messages --%>
        <c:if test="${not empty sessionScope.flashSuccess}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="bi bi-check-circle"></i> <c:out value="${sessionScope.flashSuccess}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="flashSuccess" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="bi bi-exclamation-triangle"></i> <c:out value="${sessionScope.flashError}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="flashError" scope="session"/>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <div class="alert alert-danger">
                <i class="bi bi-exclamation-triangle"></i> <c:out value="${requestScope.error}"/>
            </div>
        </c:if>
