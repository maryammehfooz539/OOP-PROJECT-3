<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- rates.jsp: gold, silver and diamond rates (Pakistan) with charts.
     The charts are drawn from the saved rates, so when you save new rates the charts change. --%>
<c:set var="pageTitle" value="Gold, Silver & Diamond Rates"/>
<c:set var="activePage" value="rates"/>
<%@ include file="includes/header.jsp" %>

<c:if test="${not empty latest}">
    <p class="text-muted mb-1">
        <span id="liveStatus" class="badge bg-secondary">Connecting to live rates...</span>
        <span id="liveTime"></span>
        &nbsp;|&nbsp; Date: <strong id="rateDate"><c:out value="${latest.date}"/></strong>
        &nbsp;|&nbsp; Rates in Pakistani Rs. (change is compared with the previous day)
    </p>
    <p class="small text-muted">Gold (24K) and silver are LIVE: the international price is changed into Rs. per tola and
        the page refreshes every minute by itself. Local sarafa market rates can be a little higher.
        There is no free live price for diamonds, so enter the diamond rate in the form below.</p>

    <%-- Three summary cards --%>
    <div class="row g-4 mb-4">
    <div class="col-12 col-md-4">
        <div class="soft-card lift-card">
            <div class="d-flex justify-content-between align-items-center">
                <h5 class="mb-0"><i class="bi bi-gem" style="color: var(--gold);"></i> Gold (24K)</h5>
                <small class="text-muted">per tola</small>
            </div>
            <div class="rate-value my-2" id="goldValue">Rs. <fmt:formatNumber value="${latest.gold}" pattern="#,##0"/></div>
            <div id="goldChangeBox">
            <c:choose>
                <c:when test="${goldChange > 0}">
                    <span class="rate-up"><i class="bi bi-arrow-up-right"></i> Up Rs. <fmt:formatNumber value="${goldChange}" pattern="#,##0"/></span>
                </c:when>
                <c:when test="${goldChange < 0}">
                    <span class="rate-down"><i class="bi bi-arrow-down-right"></i> Down Rs. <fmt:formatNumber value="${-goldChange}" pattern="#,##0"/></span>
                </c:when>
                <c:otherwise>
                    <span class="rate-flat"><i class="bi bi-dash"></i> No change</span>
                </c:otherwise>
            </c:choose>
            </div>
        </div>
    </div>
    <div class="col-12 col-md-4">
        <div class="soft-card lift-card">
            <div class="d-flex justify-content-between align-items-center">
                <h5 class="mb-0"><i class="bi bi-circle-half" style="color: var(--gold);"></i> Silver</h5>
                <small class="text-muted">per tola</small>
            </div>
            <div class="rate-value my-2" id="silverValue">Rs. <fmt:formatNumber value="${latest.silver}" pattern="#,##0"/></div>
            <div id="silverChangeBox">
            <c:choose>
                <c:when test="${silverChange > 0}">
                    <span class="rate-up"><i class="bi bi-arrow-up-right"></i> Up Rs. <fmt:formatNumber value="${silverChange}" pattern="#,##0"/></span>
                </c:when>
                <c:when test="${silverChange < 0}">
                    <span class="rate-down"><i class="bi bi-arrow-down-right"></i> Down Rs. <fmt:formatNumber value="${-silverChange}" pattern="#,##0"/></span>
                </c:when>
                <c:otherwise>
                    <span class="rate-flat"><i class="bi bi-dash"></i> No change</span>
                </c:otherwise>
            </c:choose>
            </div>
        </div>
    </div>
    <div class="col-12 col-md-4">
        <div class="soft-card lift-card">
            <div class="d-flex justify-content-between align-items-center">
                <h5 class="mb-0"><i class="bi bi-diamond" style="color: var(--gold);"></i> Diamond</h5>
                <small class="text-muted">per carat</small>
            </div>
            <div class="rate-value my-2" id="diamondValue">Rs. <fmt:formatNumber value="${latest.diamond}" pattern="#,##0"/></div>
            <div id="diamondChangeBox">
            <c:choose>
                <c:when test="${diamondChange > 0}">
                    <span class="rate-up"><i class="bi bi-arrow-up-right"></i> Up Rs. <fmt:formatNumber value="${diamondChange}" pattern="#,##0"/></span>
                </c:when>
                <c:when test="${diamondChange < 0}">
                    <span class="rate-down"><i class="bi bi-arrow-down-right"></i> Down Rs. <fmt:formatNumber value="${-diamondChange}" pattern="#,##0"/></span>
                </c:when>
                <c:otherwise>
                    <span class="rate-flat"><i class="bi bi-dash"></i> No change</span>
                </c:otherwise>
            </c:choose>
            </div>
        </div>
    </div>
    </div>

    <%-- Three charts --%>
    <div class="row g-4 mb-4">
    <div class="col-12 col-xl-4">
        <div class="soft-card lift-card">
            <h6>Gold rate trend (Rs. per tola)</h6>
            <div class="chart-box"><canvas id="goldChart"></canvas></div>
        </div>
    </div>
    <div class="col-12 col-xl-4">
        <div class="soft-card lift-card">
            <h6>Silver rate trend (Rs. per tola)</h6>
            <div class="chart-box"><canvas id="silverChart"></canvas></div>
        </div>
    </div>
    <div class="col-12 col-xl-4">
        <div class="soft-card lift-card">
            <h6>Diamond rate trend (Rs. per carat)</h6>
            <div class="chart-box"><canvas id="diamondChart"></canvas></div>
        </div>
    </div>
    </div>
</c:if>

<div class="row g-4">
    <%-- Form to save the rates of a day --%>
    <div class="col-12 col-lg-5">
        <div class="soft-card">
            <h5>Update Today's Rates</h5>
            <p class="text-muted small">Use this for the diamond rate (no live source), or when the internet is off. If the date already exists, its rates are replaced. Live gold and silver can replace the values again after a minute.</p>
            <form action="${ctx}/rates" method="post" class="needs-validation">
                <div class="mb-3">
                    <label class="form-label">Date</label>
                    <input type="date" name="date" class="form-control"
                           value="<c:out value='${submitted ? param.date : today}'/>" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Gold 24K (Rs. per tola)</label>
                    <input type="text" name="gold" class="form-control" inputmode="decimal" maxlength="12" autocomplete="off" data-only="decimal" data-minval="0.01" data-maxval="100000000" data-msg="Enter a rate greater than 0 (numbers only)."
                           value="<c:out value='${submitted ? param.gold : latest.gold}'/>" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Silver (Rs. per tola)</label>
                    <input type="text" name="silver" class="form-control" inputmode="decimal" maxlength="12" autocomplete="off" data-only="decimal" data-minval="0.01" data-maxval="100000000" data-msg="Enter a rate greater than 0 (numbers only)."
                           value="<c:out value='${submitted ? param.silver : latest.silver}'/>" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Diamond (Rs. per carat)</label>
                    <input type="text" name="diamond" class="form-control" inputmode="decimal" maxlength="12" autocomplete="off" data-only="decimal" data-minval="0.01" data-maxval="100000000" data-msg="Enter a rate greater than 0 (numbers only)."
                           value="<c:out value='${submitted ? param.diamond : latest.diamond}'/>" required>
                </div>
                <button type="submit" class="btn btn-gold"><i class="bi bi-check-circle"></i> Save Rates</button>
            </form>
        </div>
    </div>

    <%-- History table (newest first) --%>
    <div class="col-12 col-lg-7">
        <div class="table-card lift-card">
            <table class="table align-middle mb-0">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Gold (Rs./tola)</th>
                        <th>Silver (Rs./tola)</th>
                        <th>Diamond (Rs./carat)</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="h" items="${history}">
                        <tr>
                            <td><c:out value="${h.date}"/></td>
                            <td><fmt:formatNumber value="${h.gold}" pattern="#,##0"/></td>
                            <td><fmt:formatNumber value="${h.silver}" pattern="#,##0"/></td>
                            <td><fmt:formatNumber value="${h.diamond}" pattern="#,##0"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty history}">
                        <tr>
                            <td colspan="4" class="text-center text-muted py-4">No rates saved yet.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%-- Chart.js library (draws the charts) --%>
<script src="${pageContext.request.contextPath}/vendor/chartjs/chart.umd.min.js"></script>
<script>
    // Dates and rates come from the saved file (ArrayList) through JSP
    var labels = [<c:forEach var="r" items="${rates}" varStatus="st">'${r.date}'<c:if test="${not st.last}">,</c:if></c:forEach>];
    var goldData = [<c:forEach var="r" items="${rates}" varStatus="st">${r.gold}<c:if test="${not st.last}">,</c:if></c:forEach>];
    var silverData = [<c:forEach var="r" items="${rates}" varStatus="st">${r.silver}<c:if test="${not st.last}">,</c:if></c:forEach>];
    var diamondData = [<c:forEach var="r" items="${rates}" varStatus="st">${r.diamond}<c:if test="${not st.last}">,</c:if></c:forEach>];

    // Draws one line chart
    function drawChart(canvasId, name, data, color) {
        var canvas = document.getElementById(canvasId);
        if (canvas == null) {
            return;
        }
        new Chart(canvas, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: name,
                    data: data,
                    borderColor: color,
                    backgroundColor: color + '33',
                    fill: true,
                    tension: 0.3,
                    pointRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } }
            }
        });
    }

    var goldChart = drawChart('goldChart', 'Gold', goldData, '#D4AF37');
    var silverChart = drawChart('silverChart', 'Silver', silverData, '#8a8d93');
    var diamondChart = drawChart('diamondChart', 'Diamond', diamondData, '#B76E79');

    // ===== LIVE RATES: the page asks the server every minute and updates itself =====

    // Writes a number like Rs. 484,200
    function formatRs(number) {
        return 'Rs. ' + Math.round(number).toLocaleString('en-US');
    }

    // Shows the green up / red down / grey no-change text in a card
    function showChange(boxId, change) {
        var box = document.getElementById(boxId);
        if (box == null) {
            return;
        }
        var amount = Math.round(Math.abs(change)).toLocaleString('en-US');
        if (change > 0) {
            box.innerHTML = '<span class="rate-up"><i class="bi bi-arrow-up-right"></i> Up Rs. ' + amount + '</span>';
        } else if (change < 0) {
            box.innerHTML = '<span class="rate-down"><i class="bi bi-arrow-down-right"></i> Down Rs. ' + amount + '</span>';
        } else {
            box.innerHTML = '<span class="rate-flat"><i class="bi bi-dash"></i> No change</span>';
        }
    }

    // Puts a text inside an element (if the element exists)
    function setText(id, text) {
        var element = document.getElementById(id);
        if (element != null) {
            element.textContent = text;
        }
    }

    // Updates the last point of each chart (or adds a new point for a new day)
    function updateCharts(d) {
        var last = labels.length - 1;
        if (last >= 0 && labels[last] === d.date) {
            goldData[last] = d.gold;
            silverData[last] = d.silver;
            diamondData[last] = d.diamond;
        } else {
            labels.push(d.date);
            goldData.push(d.gold);
            silverData.push(d.silver);
            diamondData.push(d.diamond);
        }
        if (goldChart != null) { goldChart.update(); }
        if (silverChart != null) { silverChart.update(); }
        if (diamondChart != null) { diamondChart.update(); }
    }

    // Asks the server for the live rates and updates the cards and charts
    function loadLiveRates() {
        fetch('${ctx}/rates?action=live')
            .then(function (response) { return response.json(); })
            .then(function (d) {
                var badge = document.getElementById('liveStatus');
                if (d.date) {
                    setText('goldValue', formatRs(d.gold));
                    setText('silverValue', formatRs(d.silver));
                    setText('diamondValue', formatRs(d.diamond));
                    showChange('goldChangeBox', d.goldChange);
                    showChange('silverChangeBox', d.silverChange);
                    showChange('diamondChangeBox', d.diamondChange);
                    setText('rateDate', d.date);
                    updateCharts(d);
                }
                if (badge != null) {
                    if (d.ok) {
                        badge.className = 'badge bg-success';
                        badge.textContent = '\u25CF LIVE';
                    } else {
                        badge.className = 'badge bg-warning text-dark';
                        badge.textContent = d.message ? d.message : 'Offline - showing saved rates';
                    }
                }
                if (d.time) {
                    setText('liveTime', 'Updated at ' + d.time);
                }
            })
            .catch(function () {
                var badge = document.getElementById('liveStatus');
                if (badge != null) {
                    badge.className = 'badge bg-warning text-dark';
                    badge.textContent = 'Offline - showing saved rates';
                }
            });
    }

    loadLiveRates();                       // once when the page opens
    setInterval(loadLiveRates, 60000);     // then every 60 seconds
</script>

<%@ include file="includes/footer.jsp" %>
