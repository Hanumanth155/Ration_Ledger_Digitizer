package com.rationledger.servlet;
import com.rationledger.dao.Dao; import com.rationledger.util.*;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException; import java.util.*;
public class AdminServlet extends HttpServlet {
 private Dao dao=new Dao();
 protected void service(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  HttpSession s=req.getSession(false); if(s==null||!"ADMIN".equals(s.getAttribute("role"))){resp.sendRedirect(req.getContextPath()+"/index.jsp");return;}
  super.service(req,resp);
 }
 protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  try{
   String path=req.getPathInfo(); if(path==null)path="/dashboard";
   switch(path){
    case "/dashboard": req.setAttribute("stats",dao.stats()); req.setAttribute("announcements",dao.announcements(true)); req.setAttribute("allocation",dao.currentAllocation()); forward(req,resp,"/WEB-INF/views/admin/dashboard.jsp"); break;
    case "/beneficiaries": req.setAttribute("list",dao.beneficiaries(req.getParameter("search"),req.getParameter("status"))); forward(req,resp,"/WEB-INF/views/admin/beneficiaries.jsp");break;
    case "/beneficiary": req.setAttribute("b",dao.beneficiaryById(Integer.parseInt(req.getParameter("id")))); forward(req,resp,"/WEB-INF/views/admin/beneficiary-form.jsp");break;
    case "/allocations": req.setAttribute("list",dao.allocations());forward(req,resp,"/WEB-INF/views/admin/allocations.jsp");break;
    case "/allocation": req.setAttribute("a",req.getParameter("id")==null?null:dao.allocationById(Integer.parseInt(req.getParameter("id"))));forward(req,resp,"/WEB-INF/views/admin/allocation-form.jsp");break;
    case "/announcements":req.setAttribute("list",dao.announcements(true));forward(req,resp,"/WEB-INF/views/admin/announcements.jsp");break;
    case "/stock":req.setAttribute("list",dao.stock());forward(req,resp,"/WEB-INF/views/admin/stock.jsp");break;
    case "/distributions":req.setAttribute("list",dao.distributions(req.getParameter("month"),req.getParameter("status")));forward(req,resp,"/WEB-INF/views/admin/distributions.jsp");break;
    case "/distribution":req.setAttribute("beneficiaries",dao.beneficiaries(null,"ACTIVE"));req.setAttribute("allocations",dao.allocations());forward(req,resp,"/WEB-INF/views/admin/distribution-form.jsp");break;
    case "/reports":req.setAttribute("stats",dao.stats());req.setAttribute("list",dao.distributions(req.getParameter("month"),null));forward(req,resp,"/WEB-INF/views/admin/reports.jsp");break;
    default:resp.sendError(404);
   }
  }catch(Exception e){throw new ServletException(e);}
 }
 protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  try{
   String path=req.getPathInfo();
   switch(path){
    case "/beneficiary/save": saveBeneficiary(req);break;
    case "/beneficiary/toggle": dao.update("UPDATE beneficiaries SET status=IF(status='ACTIVE','INACTIVE','ACTIVE') WHERE beneficiary_id=?",Integer.parseInt(req.getParameter("id")));break;
    case "/allocation/save": saveAllocation(req);break;
    case "/announcement/save": saveAnnouncement(req);break;
    case "/announcement/delete": dao.update("DELETE FROM announcements WHERE announcement_id=?",Integer.parseInt(req.getParameter("id")));break;
    case "/distribution/save": saveDistribution(req);break;
    case "/stock/update": dao.update("UPDATE stock SET received_stock=received_stock+? WHERE item_name=?",Double.parseDouble(req.getParameter("qty")),req.getParameter("item"));break;
   }
   resp.sendRedirect(req.getContextPath()+"/admin/"+(path.startsWith("/beneficiary")?"beneficiaries":path.startsWith("/allocation")?"allocations":path.startsWith("/announcement")?"announcements":path.startsWith("/distribution")?"distributions":"stock"));
  }catch(Exception e){ if("DUPLICATE".equals(e.getMessage())){req.setAttribute("error","This beneficiary has already collected this month's ration.");doGet(req,resp);}else throw new ServletException(e);}
 }
 private void saveBeneficiary(HttpServletRequest r)throws Exception{
  int id=parse(r.getParameter("id"),0);String name=r.getParameter("name"),card=r.getParameter("card"),mobile=r.getParameter("mobile"),email=r.getParameter("email"),address=r.getParameter("address"),type=r.getParameter("cardType");int family=Integer.parseInt(r.getParameter("family"));
  if(id==0){
   String u=r.getParameter("username");String pass=r.getParameter("password");int uid=dao.update("INSERT INTO users(username,password_hash,role) VALUES(?,?, 'USER')",u,PasswordUtil.sha256(pass));
   dao.update("INSERT INTO beneficiaries(user_id,ration_card_number,full_name,mobile,email,address,family_size,card_type) VALUES(?,?,?,?,?,?,?,?)",uid,card,name,mobile,email,address,family,type);
  }else dao.update("UPDATE beneficiaries SET full_name=?,mobile=?,email=?,address=?,family_size=?,card_type=? WHERE beneficiary_id=?",name,mobile,email,address,family,type,id);
 }
 private void saveAllocation(HttpServletRequest r)throws Exception{
  int id=parse(r.getParameter("id"),0);int mon=Integer.parseInt(r.getParameter("month")),year=Integer.parseInt(r.getParameter("year"));double rice=d(r,"rice"),wheat=d(r,"wheat"),sugar=d(r,"sugar"),dal=d(r,"dal"),other=d(r,"other");String item=r.getParameter("otherItem"),status=r.getParameter("status");
  if(id==0){id=dao.update("INSERT INTO ration_allocations(allocation_month,allocation_year,rice_kg,wheat_kg,sugar_kg,dal_kg,other_item,other_kg,status) VALUES(?,?,?,?,?,?,?,?,?)",mon,year,rice,wheat,sugar,dal,item,other,status);dao.update("INSERT INTO distribution_schedule(allocation_id,start_date,end_date,start_time,end_time,location,notes) VALUES(?,?,?,?,?,?,?)",id,r.getParameter("startDate"),r.getParameter("endDate"),r.getParameter("startTime"),r.getParameter("endTime"),r.getParameter("location"),r.getParameter("notes"));}else dao.update("UPDATE ration_allocations SET rice_kg=?,wheat_kg=?,sugar_kg=?,dal_kg=?,other_item=?,other_kg=?,status=? WHERE allocation_id=?",rice,wheat,sugar,dal,item,other,status,id);
 }
 private void saveAnnouncement(HttpServletRequest r)throws Exception{dao.update("INSERT INTO announcements(title,description,announcement_type,published_date,expiry_date,is_published) VALUES(?,?,?,?,?,?)",r.getParameter("title"),r.getParameter("description"),r.getParameter("type"),r.getParameter("publishedDate"),r.getParameter("expiryDate"),"on".equals(r.getParameter("published")));}
 private void saveDistribution(HttpServletRequest r)throws Exception{int bid=Integer.parseInt(r.getParameter("beneficiaryId")),aid=Integer.parseInt(r.getParameter("allocationId"));Map<String,Object>a=dao.allocationById(aid);dao.distribute(bid,aid,dv(a,"rice_kg"),dv(a,"wheat_kg"),dv(a,"sugar_kg"),dv(a,"dal_kg"),dv(a,"other_kg"),r.getParameter("remarks"),(String)r.getSession().getAttribute("username"));}
 private double dv(Map<String,Object>m,String k){Object o=m.get(k);return o==null?0:((Number)o).doubleValue();}private double d(HttpServletRequest r,String k){String v=r.getParameter(k);return v==null||v.isBlank()?0:Double.parseDouble(v);}private int parse(String s,int d){return s==null||s.isBlank()?d:Integer.parseInt(s);}private void forward(HttpServletRequest r,HttpServletResponse p,String v)throws ServletException,IOException{r.getRequestDispatcher(v).forward(r,p);}
}
