package com.rationledger.servlet;
import com.rationledger.dao.Dao;
import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;
public class UserServlet extends HttpServlet{
 private Dao dao=new Dao();
 protected void service(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{HttpSession s=r.getSession(false);if(s==null||!"USER".equals(s.getAttribute("role"))){p.sendRedirect(r.getContextPath()+"/index.jsp");return;}super.service(r,p);}
 protected void doGet(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{
  
  try{
   int uid=((Number)((java.util.Map)r.getSession().getAttribute("user")).get("user_id")).intValue();
   var b=dao.beneficiaryByUser(uid);if(b==null){r.setAttribute("error","No beneficiary profile is linked to this account.");r.getRequestDispatcher("/WEB-INF/views/user/dashboard.jsp").forward(r,p);return;}
   String path=r.getPathInfo();if(path==null)path="/dashboard";
   if(path.equals("/dashboard")){r.setAttribute("b",b);r.setAttribute("a",dao.currentAllocation());r.setAttribute("announcements",dao.announcements(false));r.setAttribute("history",dao.userHistory((int)b.get("beneficiary_id")));forward(r,p,"/WEB-INF/views/user/dashboard.jsp");}
   else if(path.equals("/history")){r.setAttribute("b",b);r.setAttribute("history",dao.userHistory((int)b.get("beneficiary_id")));forward(r,p,"/WEB-INF/views/user/history.jsp");}
   else if(path.equals("/announcements")){r.setAttribute("announcements",dao.announcements(false));forward(r,p,"/WEB-INF/views/user/announcements.jsp");}
   else if(path.equals("/profile")){r.setAttribute("b",b);forward(r,p,"/WEB-INF/views/user/profile.jsp");}
   else if(path.equals("/receipt")){r.setAttribute("b",b);var h=dao.userHistory((int)b.get("beneficiary_id"));r.setAttribute("receipt",h.isEmpty()?null:h.get(0));forward(r,p,"/WEB-INF/views/user/receipt.jsp");}
   else p.sendError(404);
  }catch(Exception e){throw new ServletException(e);}
 }
 protected void doPost(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException{try{int uid=((Number)((java.util.Map)r.getSession().getAttribute("user")).get("user_id")).intValue();if("/profile".equals(r.getPathInfo()))dao.update("UPDATE beneficiaries SET mobile=?,email=?,address=? WHERE user_id=?",r.getParameter("mobile"),r.getParameter("email"),r.getParameter("address"),uid);p.sendRedirect(r.getContextPath()+"/user/profile");}catch(Exception e){throw new ServletException(e);}}
 private void forward(HttpServletRequest r,HttpServletResponse p,String v)throws ServletException,IOException{r.getRequestDispatcher(v).forward(r,p);}
}
