<%-- sidebar.jsp: the menu on the left. The 3-line button in the top bar opens and closes it. --%>
<aside class="sidebar">
    <div class="brand">
        <img src="${ctx}/images/favicon.svg" alt="logo" width="54" height="54">
        <h3 class="brand-name">GEMORA</h3>
    </div>

    <nav class="nav flex-column">
        <a class="nav-link ${activePage == 'dashboard' ? 'active' : ''}" href="${ctx}/dashboard">
            <i class="bi bi-speedometer2"></i> Dashboard
        </a>
        <a class="nav-link ${activePage == 'items' ? 'active' : ''}" href="${ctx}/items">
            <i class="bi bi-gem"></i> Jewellery Items
        </a>
        <a class="nav-link ${activePage == 'collection' ? 'active' : ''}" href="${ctx}/collection">
            <i class="bi bi-collection"></i> Collection
        </a>
        <a class="nav-link ${activePage == 'cart' ? 'active' : ''}" href="${ctx}/cart">
            <i class="bi bi-bag-heart"></i> Cart
            <span class="nav-badge cart-count"><c:out value="${empty sessionScope.cartCount ? 0 : sessionScope.cartCount}"/></span>
        </a>
        <a class="nav-link ${activePage == 'customers' ? 'active' : ''}" href="${ctx}/customers">
            <i class="bi bi-people"></i> Customers
        </a>
        <a class="nav-link ${activePage == 'sales' ? 'active' : ''}" href="${ctx}/sales">
            <i class="bi bi-receipt"></i> Sales
        </a>
        <a class="nav-link ${activePage == 'orders' ? 'active' : ''}" href="${ctx}/orders">
            <i class="bi bi-tools"></i> Orders &amp; Repairs
        </a>
        <a class="nav-link ${activePage == 'bestsellers' ? 'active' : ''}" href="${ctx}/bestsellers">
            <i class="bi bi-trophy"></i> Best Sellers
        </a>
        <a class="nav-link ${activePage == 'rates' ? 'active' : ''}" href="${ctx}/rates">
            <i class="bi bi-graph-up-arrow"></i> Rates
        </a>
        <a class="nav-link ${activePage == 'calculator' ? 'active' : ''}" href="${ctx}/calculator">
            <i class="bi bi-calculator"></i> Price Calculator
        </a>
    </nav>

    <a class="nav-link logout-link" href="${ctx}/logout">
        <i class="bi bi-box-arrow-left"></i> Logout
    </a>
</aside>
