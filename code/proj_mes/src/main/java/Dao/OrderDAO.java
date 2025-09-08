package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import dto.OrderDTO;

public class OrderDAO {

	// DB 접속 메소드
	private Connection getCon() {
		Connection con = null;
		
		try {
			
			Context ctx = new InitialContext();
			
			DataSource dataFactory = (DataSource)ctx.lookup("java:/comp/env/jdbc/oracle"); 
			
			// DB 접속
			con = dataFactory.getConnection();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return con;
	}


	// select
	public List selectAll() {
		
		List list = new ArrayList();
		
		try {
			
			// DB 접속
			Connection con = getCon();
			
			// SQL 준비
			String query = " select * from orders";
			PreparedStatement ps = con.prepareStatement(query);
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();
			
			// 결과 활용
			while(rs.next()) {
				OrderDTO dto = new OrderDTO();
				
				dto.setOrder_key(rs.getString("order_key"));
				dto.setOrder_number(rs.getString("order_number"));
				dto.setOrder_date(rs.getDate("order_date"));
				dto.setOrder_pay(rs.getDate("order_pay"));
				dto.setOrder_state(rs.getInt("order_state"));
				dto.setClient_id(rs.getString("client_id"));
				dto.setWorker_id(rs.getString("worker_id"));
				dto.setDapart_ID2(rs.getString("dapart_ID2"));
				
				list.add(dto);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	// 상세페이지 하나만 조회
	public OrderDTO selectOneOrder(OrderDTO orderDTO) {
		
		OrderDTO resultDTO = null;
		
		try {
				
			// DB 접속
			Connection conn = getCon();
			
			// SQL 준비
			String query = " select * from orders";
				   query += " where order_number = ?";
				   
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, orderDTO.getOrder_number());
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();
			
			// 결과 활용
			if(rs.next()) {

				// resultDTO가 null로 되어있어서 new 해줘야함
				resultDTO = new OrderDTO();
				
				resultDTO.setOrder_key(rs.getString("order_key"));
				resultDTO.setOrder_number(rs.getString("order_number"));
				resultDTO.setOrder_date(rs.getDate("order_date"));
				resultDTO.setOrder_pay(rs.getDate("order_pay"));
				resultDTO.setOrder_state(rs.getInt("order_state"));
				resultDTO.setClient_id(rs.getString("client_id"));
				resultDTO.setWorker_id(rs.getString("worker_id"));
				resultDTO.setDapart_ID2(rs.getString("dapart_ID2"));
			
			}
				
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return resultDTO;
		
	}
	
	
	
}
