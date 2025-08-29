package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.Login_Dto;

public class Login_Dao {
	
	 
	private Connection getConn() throws Exception {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/oracle");
		return ds.getConnection();
	}

	
	
		  // 로그인: id + pw 일치하면 사용자 1명 반환, 아니면 null
	public Login_Dto findByIdAndPw(String id, String pw) {
		  String sql = "SELECT login_id, password, grade FROM operators WHERE login_id = ? AND password = ?";

		  try (Connection con = getConn();
				  PreparedStatement ps = con.prepareStatement(sql)) {
			  
			  		ps.setString(1, id);
			  		ps.setString(2, pw);
			  		

			  		try (ResultSet rs = ps.executeQuery()) { // 문자열 인자 없이 실행
		        
			  			 if (rs.next()) {
			                    String loginid = rs.getString("login_id");
			                    int usgrade = rs.getInt("grade"); 
			                    
			                    return new Login_Dto(loginid, usgrade);
			                }
			  			rs.close();
				      }
			  		
				      ps.close();
				      con.close();
		    
		      
		      
		    } catch (Exception e) {
		      e.printStackTrace(); // 실서비스는 로깅 권장
		    }

		    
		    return null;
		  }
		
}
	
