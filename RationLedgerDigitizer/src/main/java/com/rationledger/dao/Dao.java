package com.rationledger.dao;

import com.rationledger.util.DBConnection;
import com.rationledger.util.LogUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class Dao {
    public Map<String,Object> login(String username, String hash) throws SQLException {
        String sql="SELECT user_id,username,role,status FROM users WHERE username=? AND password_hash=? AND status='ACTIVE'";
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,username); p.setString(2,hash);
            try(ResultSet r=p.executeQuery()){ if(r.next()){
                Map<String,Object> m=new HashMap<>(); m.put("user_id",r.getInt(1));m.put("username",r.getString(2));m.put("role",r.getString(3));return m;
            }}
        } return null;
    }

    public List<Map<String,Object>> query(String sql,Object... args) throws SQLException {
        List<Map<String,Object>> out=new ArrayList<>();
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            for(int i=0;i<args.length;i++) p.setObject(i+1,args[i]);
            try(ResultSet r=p.executeQuery()){
                ResultSetMetaData md=r.getMetaData(); int n=md.getColumnCount();
                while(r.next()){ Map<String,Object> m=new LinkedHashMap<>(); for(int i=1;i<=n;i++) m.put(md.getColumnLabel(i),r.getObject(i)); out.add(m); }
            }
        } return out;
    }
    public int update(String sql,Object... args) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            for(int i=0;i<args.length;i++) p.setObject(i+1,args[i]);
            int n=p.executeUpdate();
            try(ResultSet k=p.getGeneratedKeys()){ if(k.next()) return k.getInt(1); }
            return n;
        }
    }
    public boolean exists(String sql,Object... args) throws SQLException { return !query(sql,args).isEmpty(); }

    public List<Map<String,Object>> beneficiaries(String search,String status) throws SQLException {
        String q="SELECT b.*,u.username FROM beneficiaries b LEFT JOIN users u ON b.user_id=u.user_id WHERE 1=1";
        List<Object> a=new ArrayList<>();
        if(search!=null&&!search.isBlank()){q+=" AND (b.full_name LIKE ? OR b.ration_card_number LIKE ? OR b.mobile LIKE ?)";String s="%"+search+"%";a.add(s);a.add(s);a.add(s);}
        if(status!=null&&!status.isBlank()){q+=" AND b.status=?";a.add(status);}
        q+=" ORDER BY b.beneficiary_id DESC"; return query(q,a.toArray());
    }

    public Map<String,Object> beneficiaryById(int id) throws SQLException {
        List<Map<String,Object>> l=query("SELECT b.*,u.username FROM beneficiaries b LEFT JOIN users u ON b.user_id=u.user_id WHERE b.beneficiary_id=?",id);
        return l.isEmpty()?null:l.get(0);
    }

    public Map<String,Object> beneficiaryByUser(int uid) throws SQLException {
        List<Map<String,Object>> l=query("SELECT b.*,u.username FROM beneficiaries b LEFT JOIN users u ON b.user_id=u.user_id WHERE b.user_id=?",uid);
        return l.isEmpty()?null:l.get(0);
    }

    public List<Map<String,Object>> announcements(boolean admin) throws SQLException {
        if(admin) return query("SELECT * FROM announcements ORDER BY announcement_id DESC");
        return query("SELECT * FROM announcements WHERE is_published=1 AND (expiry_date IS NULL OR expiry_date>=CURDATE()) ORDER BY announcement_id DESC");
    }

    public Map<String,Object> currentAllocation() throws SQLException {
        List<Map<String,Object>> l=query("SELECT a.*,s.start_date,s.end_date,s.start_time,s.end_time,s.location,s.notes FROM ration_allocations a LEFT JOIN distribution_schedule s ON a.allocation_id=s.allocation_id WHERE a.status='PUBLISHED' ORDER BY a.allocation_year DESC,a.allocation_month DESC LIMIT 1");
        return l.isEmpty()?null:l.get(0);
    }

    public Map<String,Object> allocationById(int id) throws SQLException {
        List<Map<String,Object>> l=query("SELECT a.*,s.start_date,s.end_date,s.start_time,s.end_time,s.location,s.notes FROM ration_allocations a LEFT JOIN distribution_schedule s ON a.allocation_id=s.allocation_id WHERE a.allocation_id=?",id);
        return l.isEmpty()?null:l.get(0);
    }

    public List<Map<String,Object>> allocations() throws SQLException {
        return query("SELECT a.*,s.start_date,s.end_date,s.location FROM ration_allocations a LEFT JOIN distribution_schedule s ON a.allocation_id=s.allocation_id ORDER BY a.allocation_year DESC,a.allocation_month DESC");
    }

    public boolean alreadyCollected(int beneficiaryId,int allocationId) throws SQLException {
        return exists("SELECT distribution_id FROM ration_distributions WHERE beneficiary_id=? AND allocation_id=? AND status='COLLECTED'",beneficiaryId,allocationId);
    }

    public List<Map<String,Object>> userHistory(int beneficiaryId) throws SQLException {
        return query("SELECT d.*,a.allocation_month,a.allocation_year FROM ration_distributions d JOIN ration_allocations a ON d.allocation_id=a.allocation_id WHERE d.beneficiary_id=? ORDER BY d.distribution_date DESC",beneficiaryId);
    }

    public List<Map<String,Object>> distributions(String month,String status) throws SQLException {
        String q="SELECT d.*,b.full_name,b.ration_card_number,a.allocation_month,a.allocation_year FROM ration_distributions d JOIN beneficiaries b ON d.beneficiary_id=b.beneficiary_id JOIN ration_allocations a ON d.allocation_id=a.allocation_id WHERE 1=1";
        List<Object> a=new ArrayList<>();
        if(month!=null&&!month.isBlank()){q+=" AND CONCAT(a.allocation_year,'-',LPAD(a.allocation_month,2,'0'))=?";a.add(month);}
        if(status!=null&&!status.isBlank()){q+=" AND d.status=?";a.add(status);}
        q+=" ORDER BY d.distribution_date DESC,d.distribution_id DESC";return query(q,a.toArray());
    }

    public Map<String,Object> stats() throws SQLException {
        Map<String,Object> m=new HashMap<>();
        m.put("beneficiaries", scalar("SELECT COUNT(*) FROM beneficiaries WHERE status='ACTIVE'"));
        m.put("served", scalar("SELECT COUNT(*) FROM ration_distributions d JOIN ration_allocations a ON d.allocation_id=a.allocation_id WHERE a.allocation_month=MONTH(CURDATE()) AND a.allocation_year=YEAR(CURDATE()) AND d.status='COLLECTED'"));
        m.put("pending", scalar("SELECT COUNT(*) FROM beneficiaries b WHERE b.status='ACTIVE' AND NOT EXISTS (SELECT 1 FROM ration_distributions d JOIN ration_allocations a ON d.allocation_id=a.allocation_id WHERE d.beneficiary_id=b.beneficiary_id AND a.allocation_month=MONTH(CURDATE()) AND a.allocation_year=YEAR(CURDATE()) AND d.status='COLLECTED')"));
        m.put("announcements", scalar("SELECT COUNT(*) FROM announcements WHERE is_published=1"));
        m.put("rice", scalar("SELECT COALESCE(SUM(rice_kg),0) FROM ration_distributions WHERE status='COLLECTED' AND MONTH(distribution_date)=MONTH(CURDATE()) AND YEAR(distribution_date)=YEAR(CURDATE())"));
        m.put("wheat", scalar("SELECT COALESCE(SUM(wheat_kg),0) FROM ration_distributions WHERE status='COLLECTED' AND MONTH(distribution_date)=MONTH(CURDATE()) AND YEAR(distribution_date)=YEAR(CURDATE())"));
        return m;
    }
    private Object scalar(String sql) throws SQLException { List<Map<String,Object>> l=query(sql);return l.get(0).values().iterator().next(); }

    public List<Map<String,Object>> stock() throws SQLException {
        return query("SELECT *, (opening_stock+received_stock-distributed_stock) remaining_stock FROM stock ORDER BY item_name");
    }

    public void distribute(int bid,int aid,double rice,double wheat,double sugar,double dal,double other,String remarks,String user) throws SQLException {
        if(alreadyCollected(bid,aid)) throw new SQLException("DUPLICATE");
        try(Connection c=DBConnection.getConnection()){
            c.setAutoCommit(false);
            try(PreparedStatement p=c.prepareStatement("INSERT INTO ration_distributions(beneficiary_id,allocation_id,rice_kg,wheat_kg,sugar_kg,dal_kg,other_kg,distribution_date,status,receipt_no,remarks) VALUES(?,?,?,?,?,?,?,CURDATE(),'COLLECTED',?,?)")){
                p.setInt(1,bid);p.setInt(2,aid);p.setDouble(3,rice);p.setDouble(4,wheat);p.setDouble(5,sugar);p.setDouble(6,dal);p.setDouble(7,other);
                p.setString(8,"RL-"+System.currentTimeMillis());p.setString(9,remarks);p.executeUpdate();
            }
            updateStock(c,"Rice",rice); updateStock(c,"Wheat",wheat); updateStock(c,"Sugar",sugar); updateStock(c,"Dal",dal); updateStock(c,"Salt",other);
            c.commit();
        } catch(SQLException e){ throw e; }
        LogUtil.log(user,"RATION_DISTRIBUTED","Beneficiary "+bid+", allocation "+aid);
    }
    private void updateStock(Connection c,String item,double qty) throws SQLException {
        if(qty<=0)return;
        try(PreparedStatement p=c.prepareStatement("UPDATE stock SET distributed_stock=distributed_stock+? WHERE item_name=?")){p.setDouble(1,qty);p.setString(2,item);p.executeUpdate();}
    }
}
