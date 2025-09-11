package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.OrderListDTO;



public class OrderListDAO {

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
			String query = " SELECT"
					+ "    o.order_key,"
					+ "    o.order_number,"
					+ "    o.order_date,"
					+ "    o.worker_id,"
					+ "    w.worker_name,    "
					+ "    d.depart_level,   "
					+ "    NVL(SUM(od.quantity), 0) AS total_qty,"
					+ "    NVL(SUM(od.quantity * od.unit_price), 0) AS total_amt"
					+ " FROM orders o"
					+ " LEFT JOIN order_detail od "
					+ "       ON o.order_key = od.order_key    "
					+ " LEFT JOIN workers w "
					+ "       ON o.worker_id = w.worker_id     "
					+ " LEFT JOIN department d "
					+ "       ON o.dapart_ID2 = d.dapart_ID2   "
					+ " GROUP BY "
					+ "    o.order_key, o.order_number, o.order_date, o.worker_id,"
					+ "    w.worker_name, d.depart_level"
					+ " ORDER BY "
					+ "    o.order_date DESC";
			PreparedStatement ps = con.prepareStatement(query);
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();
			
			// 결과 활용
			while(rs.next()) {
				OrderListDTO dto = new OrderListDTO();
				
				dto.setOrder_date(rs.getDate("order_date"));
				dto.setOrder_number(rs.getString("order_number"));
				dto.setDepart_level(rs.getString("depart_level"));
				dto.setWorker_name(rs.getString("worker_name"));
				dto.setTotalQty(rs.getString("totalQty"));
				dto.setTotalAmt(rs.getString("totalAmt"));
				
				
				list.add(dto);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	// 상세페이지 하나만 조회
//	public OrderListDTO selectOneOrderList(OrderListDTO orderListDTO) {
//		
//		OrderListDTO resultDTO = null;
//		
//		try {
//				
//			// DB 접속
//			Connection conn = getConn();
//			
//			// SQL 준비
//			String query = " select * from orders";
//				   query += " where order_index = ?";
//				   
//			PreparedStatement ps = conn.prepareStatement(query);
//			ps.setInt(1, OrderListDTO.getOrder_index());
//			
//			// SQL 실행
//			ResultSet rs = ps.executeQuery();
//			
//			// 결과 활용
//			if(rs.next()) {
//
//				// resultDTO가 null로 되어있어서 new 해줘야함
//				resultDTO = new OrderListDTO();
//				
//				resultDTO.setOrder_index(rs.getInt("order_index"));
//				resultDTO.setQuantity(rs.getInt("quantity"));
//				resultDTO.setItem_code(rs.getString("item_code"));
//				resultDTO.setOrder_key(rs.getString("order_key"));
//				resultDTO.setOrder_takeday(rs.getDate("order_takeday"));
//			
//			
//			}
//				
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		
//		return resultDTO;
//		
//	}
//	
//	// delete
//	public int deleteOrderList(OrderListDTO OrderListDTO) {
//		
//		int result = -1;
//		
//		try {
//			
//			// DB 접속
//			Connection conn = getConn();
//			
//			// SQL 준비
//			String query = " delete from order_detail";
//				   query += " where order_index = ?";
//
//			PreparedStatement ps = conn.prepareStatement(query);
//			ps.setInt(1, OrderListDTO.getOrder_index());
//			
//			// SQL 실행
//			result = ps.executeUpdate();
//		
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//	}
//	
//	// insert
//	public int insertOrderList(OrderListDTO OrderListDTO) {
//		
//		int result = -1;
//		
//		try {
//			
//			// DB 접속
//			Connection conn = getConn();
//			
//			// SQL 준비
//			String query = " insert into order_detail (order_index, quantity, item_code, order_key, order_takeday)";
//				   query += " values(?, ?, ?, ?, ?, ?, ?, ?)";
//			
//			PreparedStatement ps = conn.prepareStatement(query);
//			ps.setInt(1, OrderListDTO.getOrder_index());
//			ps.setInt(2, OrderListDTO.getQuantity());
//			ps.setString(3, OrderListDTO.getItem_code());
//			ps.setString(4, OrderListDTO.getOrder_key());
//			ps.setDate(5, OrderListDTO.getOrder_takeday());
//		
//			// SQL 실행
//			result = ps.executeUpdate();
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//	}
//	
//	// update
//	public int updateOrderList(OrderListDTO OrderListDTO) {
//		
//		int result = -1;
//		
//		try {
//			
//			// DB 접속
//			Connection conn = getConn();
//			
//			// SQL 준비
//			String query = " update from order_detail";
//			query += " set quantity = ?, ";
//			query += "     item_code = ?, ";
//			query += "     order_key = ?, ";
//			query += "     order_takeday = ?";
//			query += " where order_index = ?";
//			
//			PreparedStatement ps = conn.prepareStatement(query);
//			ps.setInt(1, OrderListDTO.getQuantity());
//			ps.setString(2, OrderListDTO.getItem_code());
//			ps.setString(3, OrderListDTO.getOrder_key());
//			ps.setDate(4, OrderListDTO.getOrder_takeday());
//			ps.setInt(5, OrderListDTO.getOrder_index());
//		
//			
//			// SQL 실행
//			result = ps.executeUpdate();
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//	}
//		
				
}
