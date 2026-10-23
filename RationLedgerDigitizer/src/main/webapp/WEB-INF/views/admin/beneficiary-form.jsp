<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %><%@ include file="../common/header.jsp" %>
<h1>${empty b?'Add Beneficiary':'Edit Beneficiary'}</h1><div class="panel"><form method="post" action="beneficiary/save" class="form-grid">
<input type="hidden" name="id" value="${b.beneficiary_id}">
<label>Full Name<input name="name" value="${b.full_name}" required></label><label>Ration Card Number<input name="card" value="${b.ration_card_number}" ${not empty b?'readonly':''} required></label>
<label>Mobile<input name="mobile" value="${b.mobile}" required pattern="[0-9]{10}"></label><label>Email<input type="email" name="email" value="${b.email}"></label>
<label>Family Size<input type="number" min="1" name="family" value="${empty b?1:b.family_size}" required></label><label>Card Type<select name="cardType"><option ${b.card_type=='PHH'?'selected':''}>PHH</option><option ${b.card_type=='AAY'?'selected':''}>AAY</option><option ${b.card_type=='NPHH'?'selected':''}>NPHH</option></select></label>
<label class="wide">Address<textarea name="address">${b.address}</textarea></label>
<c:if test="${empty b}"><label>Username<input name="username" required></label><label>Password<input type="password" name="password" required minlength="6"></label></c:if>
<div class="wide"><button class="btn primary">Save Beneficiary</button> <a class="btn" href="beneficiaries">Cancel</a></div></form></div><%@ include file="../common/footer.jsp" %>