package com.rationledger.filter;
import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;
public class AuthFilter implements Filter{
 public void doFilter(ServletRequest rq,ServletResponse rp,FilterChain c)throws IOException,ServletException{
  HttpServletRequest r=(HttpServletRequest)rq;HttpServletResponse p=(HttpServletResponse)rp;String uri=r.getRequestURI();
  if(uri.endsWith("index.jsp")||uri.endsWith("signup.jsp")||uri.endsWith("/signup")||uri.contains("/login")||uri.contains("/css/")||uri.contains("/js/")||uri.contains("/images/")||uri.endsWith(".ico")){c.doFilter(rq,rp);return;}
  HttpSession s=r.getSession(false);if(s==null||s.getAttribute("role")==null){p.sendRedirect(r.getContextPath()+"/index.jsp");return;}c.doFilter(rq,rp);
 }
}
