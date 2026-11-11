<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- best-sellers.jsp: line graph of the top-selling items, with the ranked list under it.
     The numbers come from the sales history (through BestSellerServlet). --%>
<c:set var="pageTitle" value="Best Sellers"/>
<c:set var="activePage" value="bestsellers"/>
<%@ include file="includes/header.jsp" %>

<c:choose>
    <c:when test="${empty bestSellers}">
        <%-- No sales yet --%>
        <div class="soft-card text-center py-5">
            <i class="bi bi-trophy" style="font-size: 3rem; color: var(--gold);"></i>
            <h4 class="mt-3">No sales yet</h4>
            <p class="text-muted">Make a few sales and the best-selling items will show here as a graph.</p>
            <a href="${ctx}/sales?action=new" class="btn btn-gold"><i class="bi bi-cart-plus"></i> Make a Sale</a>
        </div>
    </c:when>

    <c:otherwise>
        <%-- Three summary cards --%>
        <div class="row g-4 mb-4">
            <div class="col-12 col-md-4">
                <div class="soft-card lift-card stat-card">
                    <div class="stat-icon"><i class="bi bi-trophy-fill"></i></div>
                    <div>
                        <div class="stat-label">Best seller</div>
                        <div class="fw-semibold"><c:out value="${topItem.itemName}"/></div>
                        <small class="text-muted">${topItem.unitsSold} sold</small>
                    </div>
                </div>
            </div>
            <div class="col-12 col-md-4">
                <div class="soft-card lift-card stat-card gold">
                    <div class="stat-icon"><i class="bi bi-bag-check"></i></div>
                    <div>
                        <div class="stat-number">${totalUnits}</div>
                        <div class="stat-label">Total pieces sold</div>
                    </div>
                </div>
            </div>
            <div class="col-12 col-md-4">
                <div class="soft-card lift-card stat-card">
                    <div class="stat-icon"><i class="bi bi-cash-coin"></i></div>
                    <div>
                        <div class="stat-number" style="font-size: 1.5rem;">Rs. <fmt:formatNumber value="${totalRevenue}" pattern="#,##0"/></div>
                        <div class="stat-label">Total sales amount</div>
                    </div>
                </div>
            </div>
        </div>

        <%-- The line graph --%>
        <div class="soft-card mb-4">
            <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
                <h5 class="mb-0"><i class="bi bi-graph-up" style="color: var(--gold);"></i> Top Selling Items</h5>
                <div class="btn-group">
                    <button type="button" id="btnUnits" class="btn btn-gold btn-sm" onclick="setMetric('units')">By Pieces Sold</button>
                    <button type="button" id="btnRevenue" class="btn btn-outline-secondary btn-sm" onclick="setMetric('revenue')">By Sales Amount</button>
                </div>
            </div>
            <div class="chart-box-large"><canvas id="bestChart"></canvas></div>
        </div>

        <%-- Ranked list under the graph --%>
        <div class="table-card lift-card">
            <table class="table align-middle mb-0">
                <thead>
                    <tr>
                        <th>Rank</th>
                        <th>Item</th>
                        <th>Category</th>
                        <th>Pieces Sold</th>
                        <th>Sales Amount (Rs.)</th>
                        <th>Stock Left</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="b" items="${bestSellers}" varStatus="st">
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${st.count == 1}"><span class="rank-badge rank-1"><i class="bi bi-trophy-fill"></i></span></c:when>
                                    <c:when test="${st.count == 2}"><span class="rank-badge rank-2">2</span></c:when>
                                    <c:when test="${st.count == 3}"><span class="rank-badge rank-3">3</span></c:when>
                                    <c:otherwise><span class="rank-badge">${st.count}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td><strong><c:out value="${b.itemName}"/></strong></td>
                            <td><c:out value="${b.category}"/></td>
                            <td>${b.unitsSold}</td>
                            <td><fmt:formatNumber value="${b.revenue}" pattern="#,##0"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not b.inStore}"><span class="text-muted">Removed</span></c:when>
                                    <c:when test="${b.lowStock}"><span class="badge bg-danger">${b.stockLeft} (Low)</span></c:when>
                                    <c:otherwise><span class="badge bg-success">${b.stockLeft}</span></c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <%-- Chart.js library (draws the graph) --%>
        <script src="${pageContext.request.contextPath}/vendor/chartjs/chart.umd.min.js"></script>
        <script>
            // The numbers come from the sales history
            var itemNames = ${chartLabels};
            var unitsData = ${chartUnits};
            var revenueData = ${chartRevenue};
            var metric = 'units';

            // Draws the line graph (a line with round points, not tall bars)
            var bestChart = new Chart(document.getElementById('bestChart'), {
                type: 'line',
                data: {
                    labels: itemNames,
                    datasets: [{
                        label: 'Pieces sold',
                        data: unitsData,
                        borderColor: '#B76E79',
                        backgroundColor: 'rgba(212, 175, 55, 0.25)',
                        pointBackgroundColor: '#D4AF37',
                        pointBorderColor: '#B76E79',
                        pointRadius: 6,
                        pointHoverRadius: 9,
                        borderWidth: 3,
                        fill: true,
                        tension: 0.35
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function (context) {
                                    var i = context.dataIndex;
                                    return [
                                        'Pieces sold: ' + unitsData[i],
                                        'Sales amount: Rs. ' + revenueData[i].toLocaleString('en-US')
                                    ];
                                }
                            }
                        }
                    },
                    scales: {
                        x: { ticks: { autoSkip: false, maxRotation: 40, minRotation: 0 } },
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function (value) {
                                    if (metric === 'units') {
                                        return Number.isInteger(value) ? value : '';
                                    }
                                    return 'Rs. ' + value.toLocaleString('en-US');
                                }
                            }
                        }
                    }
                }
            });

            // Switches the graph between "pieces sold" and "sales amount"
            function setMetric(name) {
                metric = name;
                if (name === 'units') {
                    bestChart.data.datasets[0].data = unitsData;
                    bestChart.data.datasets[0].label = 'Pieces sold';
                    document.getElementById('btnUnits').className = 'btn btn-gold btn-sm';
                    document.getElementById('btnRevenue').className = 'btn btn-outline-secondary btn-sm';
                } else {
                    bestChart.data.datasets[0].data = revenueData;
                    bestChart.data.datasets[0].label = 'Sales amount (Rs.)';
                    document.getElementById('btnUnits').className = 'btn btn-outline-secondary btn-sm';
                    document.getElementById('btnRevenue').className = 'btn btn-gold btn-sm';
                }
                bestChart.update();
            }
        </script>
    </c:otherwise>
</c:choose>

<%@ include file="includes/footer.jsp" %>
