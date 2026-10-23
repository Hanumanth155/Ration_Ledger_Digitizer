package com.rationledger.servlet;
import com.rationledger.dao.Dao; import com.rationledger.util.PasswordUtil;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException; import java.util.Map;
public class LoginServlet extends HttpServlet {
 protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  try{
   String u=req.getParameter("username"), p=req.getParameter("password");
   Map<String,Object> user=new Dao().login(u,PasswordUtil.sha256(p));
   String expectedRole=req.getParameter("expectedRole");
   if(user!=null && expectedRole!=null && !expectedRole.equals(user.get("role"))) user=null;
   if(user==null){req.setAttribute("error","Invalid username or password");req.getRequestDispatcher("/index.jsp").forward(req,resp);return;}
   HttpSession s=req.getSession();s.setAttribute("user",user);s.setAttribute("username",u);s.setAttribute("role",user.get("role"));
   if("ADMIN".equals(user.get("role")))resp.sendRedirect(req.getContextPath()+"/admin/dashboard");
   else resp.sendRedirect(req.getContextPath()+"/user/dashboard");
  }catch(Exception e){throw new ServletException(e);}
 }
}
