package Dao;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class WorkOrderDAO {
	// 여기에서 DB 연결 및 SQL 실행
	// SQL 실행은 CONTROLLER가 바로 호출해서 실행하는 게 아니라 SERVICE를 사이에 두고 수행
	
	Connection getConn() {
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			DataSource dataFactory = (DataSource) ctx.lookup("java:/comp/env/jdbc/oracle");
			
			conn = dataFactory.getConnection();			
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return conn;
	}
}
