document.addEventListener("DOMContentLoaded",()=>{
 const tabs=document.querySelectorAll(".auth-tab"), role=document.getElementById("expectedRole"), submit=document.querySelector(".auth-submit"), prompt=document.getElementById("signupPrompt");
 tabs.forEach(tab=>tab.addEventListener("click",()=>{
   tabs.forEach(t=>t.classList.remove("active"));tab.classList.add("active");
   const admin=tab.dataset.role==="ADMIN";role.value=admin?"ADMIN":"USER";
   submit.innerHTML=(admin?"Sign in as Administrator":"Sign in as Beneficiary")+' <span>→</span>';
   if(prompt)prompt.style.display=admin?"none":"block";
 }));
 const toggle=document.getElementById("togglePassword"), pass=document.getElementById("password");
 if(toggle&&pass)toggle.addEventListener("click",()=>{pass.type=pass.type==="password"?"text":"password";toggle.textContent=pass.type==="password"?"Show":"Hide";});
 const form=document.getElementById("loginForm");if(form)form.addEventListener("submit",()=>{if(form.checkValidity()){submit.disabled=true;submit.innerHTML="Signing in…";}});
});