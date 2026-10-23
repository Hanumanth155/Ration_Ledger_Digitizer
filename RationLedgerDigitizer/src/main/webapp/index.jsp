<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html><html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Ration Ledger | Sign in</title><link rel="stylesheet" href="<%=request.getContextPath()%>/css/app.css"></head>
<body class="auth-page">
<div class="auth-shell">
  <section class="auth-story">
    <div class="auth-brand"><span class="brand-mark">RL</span><div><b>Ration Ledger</b><small>PUBLIC DISTRIBUTION PORTAL</small></div></div>
    <div class="story-copy"><span class="eyebrow">SMARTER · CLEARER · CONNECTED</span><h1>Ration services,<br><em>made simpler.</em></h1><p>One trusted place to check monthly allocations, follow distribution dates, read important notices and keep every collection record in order.</p>
      <div class="story-points"><div><span>01</span><div><b>Know your allocation</b><small>Monthly items and quantities in one view.</small></div></div><div><span>02</span><div><b>Never miss an update</b><small>Schedules and important notices in real time.</small></div></div><div><span>03</span><div><b>Keep a clear record</b><small>Review your collection history and receipts.</small></div></div></div>
    </div><div class="story-foot">DIGITAL RECORDS · BETTER SERVICE</div>
  </section>
  <section class="auth-form-side">
    <div class="auth-form-card">
      <div class="mobile-brand"><span class="brand-mark">RL</span><b>Ration Ledger</b></div>
      <span class="eyebrow green">WELCOME BACK</span><h2>Sign in to continue</h2><p class="auth-sub">Choose your account type and enter your credentials.</p>
      <% if("1".equals(request.getParameter("registered"))){%><div class="alert success">Account created successfully. Please sign in.</div><%}%>
      <% if(request.getAttribute("error")!=null){%><div class="alert danger"><%=request.getAttribute("error")%></div><%}%>
      <div class="auth-tabs"><button type="button" class="auth-tab active" data-role="USER">Beneficiary</button><button type="button" class="auth-tab" data-role="ADMIN">Administrator</button></div>
      <form method="post" action="<%=request.getContextPath()%>/login" id="loginForm">
        <input type="hidden" name="expectedRole" id="expectedRole" value="USER">
        <label>Username<input name="username" placeholder="Enter your username" autocomplete="username" required></label>
        <label>Password<div class="password-wrap"><input id="password" type="password" name="password" placeholder="Enter your password" autocomplete="current-password" required><button type="button" class="show-pass" id="togglePassword">Show</button></div></label>
        <button class="btn primary auth-submit" type="submit">Sign in as Beneficiary <span>→</span></button>
      </form>
      <div class="signup-prompt" id="signupPrompt">New to the portal? <a href="<%=request.getContextPath()%>/signup.jsp">Create a beneficiary account</a></div>
      <div class="auth-note"><span>●</span> Your account is protected by role-based access.</div>
      <div class="demo-note"><b>Demo accounts</b><span>Beneficiary: <code>user1 / user123</code></span><span>Admin: <code>admin / admin123</code></span></div>
    </div><footer class="auth-footer">© Ration Ledger Digitizer · Digital ration record management</footer>
  </section>
</div><script src="<%=request.getContextPath()%>/js/auth.js"></script></body></html>