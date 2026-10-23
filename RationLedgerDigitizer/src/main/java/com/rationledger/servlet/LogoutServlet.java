package com.rationledger.servlet;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException;
public class LogoutServlet extends HttpServlet{
 protected void doGet(HttpServletRequest r,HttpServletResponse p)throws IOException{r.getSession().invalidate();p.sendRedirect(r.getContextPath()+"/index.jsp");}
}
