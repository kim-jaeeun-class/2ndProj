package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.GonjiDTO;
import Dto.OrderDTO;

public class GonjiDAO {
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


		// select
		public List selectAll() {
			
			List list = new ArrayList();
			
			try {
				
				// DB 접속
				Connection con = getConn();
				
				// SQL 준비
				String query = " select * from board";
				PreparedStatement ps = con.prepareStatement(query);
				
				// SQL 실행
				ResultSet rs = ps.executeQuery();
				
				// 결과 활용
				while(rs.next()) {
					GonjiDTO dto = new GonjiDTO();
					
					dto.setBoard_id(rs.getString("board_id"));
					dto.setBoard_title(rs.getString("board_title"));
					dto.setBoard_con(rs.getString("board_con"));
					dto.setBoard_date(rs.getString("board_date"));
					dto.setBoard_attach(rs.getInt("board_attach"));
					dto.setWorker_id(rs.getString("worker_id"));
					dto.setBoard_type(rs.getInt("board_type"));
					
					
					list.add(dto);
				}
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return list;
		}
	
}
