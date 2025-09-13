package Dao;

import java.sql.*;
import java.util.*;
import javax.naming.*;
import javax.sql.DataSource;

import Dto.CategoryDTO;

public class CategoryDAO {

	// DB 접속 메소드
	private Connection getConn() {
		Connection conn = null;
		
		try {
			
			Context ctx = new InitialContext();
			
			DataSource dataFactory = (DataSource)ctx.lookup("java:/comp/env/jdbc/oracle"); 
		
		// DB 접속
			conn = dataFactory.getConnection();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return conn;
	}

    // 대분류 전체
    public List<CategoryDTO> getBigList() {
        List<CategoryDTO> list = new ArrayList<>();
        
        try {
            Connection con = getConn();
            
            String sql = "SELECT BIG_CODE, BIG_NAME FROM TB_BIG_CATEGORY ORDER BY BIG_NAME";
            
            PreparedStatement ps = con.prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                CategoryDTO d = new CategoryDTO();
                
                d.setCode(rs.getString("BIG_CODE"));
                d.setName(rs.getString("BIG_NAME"));
                
                list.add(d);
            }
            rs.close(); 
            ps.close(); 
            con.close();
            
        } catch (Exception e) { 
        	e.printStackTrace(); 
    	}
        
        return list;
    }

    // 특정 대분류에 실제로 존재하는 중분류만
    public List<CategoryDTO> getMidListByBig(String bigCode) {
    	
        List<CategoryDTO> list = new ArrayList<>();
        
        try {
            Connection con = getConn();
            
            String sql 	=	"SELECT DISTINCT m.MID_CODE, m.MID_NAME " 
			            +	"  FROM TB_MID_CATEGORY m " 
			            +	"  JOIN ITEM i ON SUBSTR(i.ITEM_CODE,3,2) = m.MID_CODE " 
			            +	" WHERE SUBSTR(i.ITEM_CODE,1,2) = ? " 
			            +	" ORDER BY m.MID_NAME";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, bigCode);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                CategoryDTO d = new CategoryDTO();
                
                d.setCode(rs.getString("MID_CODE"));
                d.setName(rs.getString("MID_NAME"));
                
                list.add(d);
            }
            rs.close(); 
            ps.close(); 
            con.close();
            
        } catch (Exception e) { 
        	e.printStackTrace(); 
    	}
        
        return list;
    }

    // 특정 중분류에 실제로 존재하는 소분류만
    public List<CategoryDTO> getSmallListByMid(String midCode) {
    	
        List<CategoryDTO> list = new ArrayList<>();
        
        try {
            Connection con = getConn();
            
            String sql 	=	"SELECT DISTINCT s.SM_CODE, s.SM_NAME, s.SM_GROUP " 
			            +	"  FROM TB_SMALL_CATEGORY s " 
			            +	"  JOIN ITEM i ON SUBSTR(i.ITEM_CODE,5,2) = s.SM_CODE " 
			            +	" WHERE SUBSTR(i.ITEM_CODE,3,2) = ? " +" ORDER BY s.SM_NAME";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, midCode);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                CategoryDTO d = new CategoryDTO();
                
                d.setCode(rs.getString("SM_CODE"));
                d.setName(rs.getString("SM_NAME"));
                d.setGroup(rs.getString("SM_GROUP"));
                
                list.add(d);
            }
            rs.close(); 
            ps.close(); 
            con.close();
        } catch (Exception e) { 
        	e.printStackTrace(); 
    	}
        
        return list;
    }
}
