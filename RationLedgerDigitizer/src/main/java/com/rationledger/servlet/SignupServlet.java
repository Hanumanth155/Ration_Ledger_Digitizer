package com.rationledger.servlet;

import com.rationledger.util.DBConnection;
import com.rationledger.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class SignupServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name=req.getParameter("name"), card=req.getParameter("card"), mobile=req.getParameter("mobile"),
               email=req.getParameter("email"), address=req.getParameter("address"), cardType=req.getParameter("cardType"),
               username=req.getParameter("username"), password=req.getParameter("password"), confirm=req.getParameter("confirmPassword");
        int family;
        try { family=Integer.parseInt(req.getParameter("family")); } catch(Exception e) { family=0; }
        if(blank(name)||blank(card)||blank(mobile)||blank(address)||blank(username)||password==null||password.length()<8||!password.equals(confirm)||!mobile.matches("[0-9]{10}")||family<1||family>30||username.length()<4){
            req.setAttribute("error","Please check your details. Passwords must match and contain at least 8 characters.");
            req.getRequestDispatcher("/signup.jsp").forward(req,resp);return;
        }
        try(Connection c=DBConnection.getConnection()){
            c.setAutoCommit(false);
            try {
                try(PreparedStatement check=c.prepareStatement("SELECT user_id FROM users WHERE username=?")) {
                    check.setString(1,username);try(ResultSet rs=check.executeQuery()){if(rs.next())throw new IllegalArgumentException("That username is already taken.");}
                }
                try(PreparedStatement check=c.prepareStatement("SELECT beneficiary_id FROM beneficiaries WHERE ration_card_number=?")) {
                    check.setString(1,card);try(ResultSet rs=check.executeQuery()){if(rs.next())throw new IllegalArgumentException("That ration card is already registered. Please contact the administrator if you need help.");}
                }
                int userId;
                try(PreparedStatement p=c.prepareStatement("INSERT INTO users(username,password_hash,role,status) VALUES(?,?,'USER','ACTIVE')",Statement.RETURN_GENERATED_KEYS)){
                    p.setString(1,username);p.setString(2,PasswordUtil.sha256(password));p.executeUpdate();
                    try(ResultSet keys=p.getGeneratedKeys()){if(!keys.next())throw new SQLException("Could not create user account");userId=keys.getInt(1);}
                }
                try(PreparedStatement p=c.prepareStatement("INSERT INTO beneficiaries(user_id,ration_card_number,full_name,mobile,email,address,family_size,card_type,status) VALUES(?,?,?,?,?,?,?,?,'ACTIVE')")){
                    p.setInt(1,userId);p.setString(2,card);p.setString(3,name);p.setString(4,mobile);p.setString(5,blank(email)?null:email);p.setString(6,address);p.setInt(7,family);p.setString(8,cardType);p.executeUpdate();
                }
                c.commit();
                req.setAttribute("success","Account created successfully. You can now sign in.");
                resp.sendRedirect(req.getContextPath()+"/index.jsp?registered=1");
            } catch(IllegalArgumentException e){c.rollback();req.setAttribute("error",e.getMessage());req.getRequestDispatcher("/signup.jsp").forward(req,resp);}
              catch(Exception e){c.rollback();throw e;}
        } catch(Exception e){throw new ServletException("Could not create beneficiary account. Check database connection and unique card/username values.",e);}
    }
    private boolean blank(String s){return s==null||s.isBlank();}
}