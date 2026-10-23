<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../common/header.jsp" %>
<p class="muted">CONTROL CENTRE / OVERVIEW</p><h1>Admin Dashboard</h1><p class="muted">Overview of ration operations and beneficiary service.</p>
<div class="cards">
<div class="stat"><span>Total Beneficiaries</span><b>${stats.beneficiaries}</b></div><div class="stat"><span>Served This Month</span><b>${stats.served}</b></div><div class="stat"><span>Pending</span><b>${stats.pending}</b></div><div class="stat"><span>Announcements</span><b>${stats.announcements}</b></div>
</div>
<div class="grid2"><section class="panel"><div class="panel-head"><h2>Current Allocation</h2><a class="btn small" href="allocation">Manage</a></div>
<c:choose><c:when test="${not empty allocation}"><div class="ration-grid"><div>Rice <b>${allocation.rice_kg} KG</b></div><div>Wheat <b>${allocation.wheat_kg} KG</b></div><div>Sugar <b>${allocation.sugar_kg} KG</b></div><div>Dal <b>${allocation.dal_kg} KG</b></div></div><p><b>Distribution:</b> ${allocation.start_date} to ${allocation.end_date} at ${allocation.location}</p></c:when><c:otherwise><p>No published allocation.</p></c:otherwise></c:choose></section>
<section class="panel"><div class="panel-head"><h2>Quick Actions</h2></div><div class="quick"><a href="beneficiary">+ Add Beneficiary</a><a href="allocation">+ Monthly Allocation</a><a href="distribution">Record Collection</a><a href="announcements">Post Announcement</a></div></section></div>
<section class="panel"><div class="panel-head"><h2>Recent Announcements</h2><a href="announcements">View all</a></div><c:forEach var="a" items="${announcements}" begin="0" end="4"><div class="notice"><span class="badge ${a.announcement_type}">${a.announcement_type}</span><div><b>${a.title}</b><p>${a.description}</p></div></div></c:forEach></section>
<%@ include file="../common/footer.jsp" %>