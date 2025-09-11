package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.ItemDTO;
import Dto.OrderDTO;



public class OrderDAO {

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
			String query = "select * from orders";
			PreparedStatement ps = con.prepareStatement(query);
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();
			
			// 결과 활용
			while(rs.next()) {
				OrderDTO dto = new OrderDTO();
				
				dto.setOrder_key(rs.getString("order_key"));
				dto.setOrder_date(rs.getDate("order_date"));
				dto.setOrder_number(rs.getString("order_number"));
				
//				dto.setDapart_ID2(rs.getString("dapart_ID2"));
				dto.setDepart_level(rs.getString("depart_level"));
				
//				dto.setWorker_name(rs.getString("worker_id"));
				dto.setWorker_name(rs.getString("worker_name"));
				
				dto.setTotalQty(rs.getString("totalQty"));
				dto.setTotalAmt(rs.getString("totalAmt"));
				
				dto.setOrder_state(rs.getInt("order_state"));
//				dto.setOrder_pay(rs.getDate("order_pay"));
				
//				dto.setClient_id(rs.getString("client_id"));
//				dto.setBusiness_number(rs.getString("business_number"));
//				dto.setClient_phone(rs.getString("client_phone"));
//				
//				dto.setBigo(rs.getString("bigo"));

				
				
				list.add(dto);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	// 상세페이지
	// 상세페이지: 헤더(기본정보) 1건 조회
	// 상세페이지 하나만 조회 (order_key로)
	public OrderDTO selectOneOrder(OrderDTO orderDTO) {

	    OrderDTO resultDTO = null;

	    try {
	        // DB 접속
	        Connection conn = getConn();

	        // SQL 준비
	        String query  = " select * from orders ";
	               query += " where order_key = ? ";

	        PreparedStatement ps = conn.prepareStatement(query);
	        ps.setString(1, orderDTO.getOrder_key());  

	        // SQL 실행
	        ResultSet rs = ps.executeQuery();

	        // 결과 활용
	        if (rs.next()) {
	            resultDTO = new OrderDTO();

	            resultDTO.setWorker_id(rs.getString("worker_id"));
	            resultDTO.setWorker_name(rs.getString("worker_name"));
	            resultDTO.setOrder_pay(rs.getDate("order_pay"));
	            resultDTO.setClient_id(rs.getString("client_id"));
	            resultDTO.setBusiness_number(rs.getString("business_number"));
	            resultDTO.setClient_phone(rs.getString("client_phone"));
	            resultDTO.setBigo(rs.getString("bigo"));

	            resultDTO.setOrder_key(rs.getString("order_key"));
	            resultDTO.setOrder_number(rs.getString("order_number"));
	            resultDTO.setOrder_date(rs.getDate("order_date"));
	            resultDTO.setOrder_state(rs.getInt("order_state"));
	            resultDTO.setDapart_ID2(rs.getString("dapart_ID2"));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return resultDTO;
	}

	
	// 상세페이지: 상세(품목) 목록 조회 - (order_key, item_code) PK 기준
	public List<ItemDTO> selectOrderItems(OrderDTO orderDTO) {

	    List<ItemDTO> list = new ArrayList<>();

	    try {
	        // DB 접속
	        Connection conn = getConn();

	        // SQL 준비
	        String query  = " select ";
	               query += "   od.item_code, ";
	               query += "   nvl(i.item_name, od.item_code) as item_name, ";
	               query += "   od.item_price, ";
	               query += "   od.quantity ";
	               query += " from order_detail od ";
	               query += " left join items i on i.item_code = od.item_code ";
	               query += " where od.order_key = ? ";
	               query += " order by od.item_code ";

	        PreparedStatement ps = conn.prepareStatement(query);
	        ps.setString(1, orderDTO.getOrder_key());  

	        // SQL 실행
	        ResultSet rs = ps.executeQuery();

	        // 결과 활용
	        while (rs.next()) {
	            ItemDTO dto = new ItemDTO();
	            dto.setItem_code(rs.getString("item_code"));
	            dto.setItem_name(rs.getString("item_name"));
	            dto.setItem_price(rs.getString("item_price"));
	            dto.setQuantity(rs.getInt("quantity"));
	            
	            list.add(dto);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}


	
	// insert
	public int insertOrder(OrderDTO orderDTO) {
		
		 String seqSql = "SELECT order_seq.NEXTVAL FROM dual";
		    String insSql = "INSERT INTO orders "
		                  + " (order_key, order_number, order_date, order_state, client_id, worker_id, dapart_ID2) "
		                  + " VALUES (?, ?, SYSDATE, ?, ?, ?, ?)";

		    try (Connection conn = getConn();
		         PreparedStatement psSeq = conn.prepareStatement(seqSql)) {

		        long nextVal = 0L;
		        try (ResultSet rs = psSeq.executeQuery()) {
		            if (rs.next()) nextVal = rs.getLong(1);
		        }

		        // ★ 자바에서 안전하게 발주번호 생성: B + 오늘(YYYYMMDD) + 시퀀스 2자리
		        String today = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
		        String seq2  = String.format("%02d", nextVal % 100);
		        String orderNumber = "B" + today + seq2;

		        try (PreparedStatement ps = conn.prepareStatement(insSql)) {
		            // 1) PK
		            ps.setLong(1, nextVal);
		            // 2) 주문번호
		            ps.setString(2, orderNumber);
		            // 3) 상태(0=대기)
		            ps.setInt(3, orderDTO.getOrder_state());
		            // 4) 거래처 (NOT NULL)
		            ps.setString(4, orderDTO.getClient_id());
		            // 5) 사번
		            ps.setString(5, orderDTO.getWorker_id());
		            // 6) 부서ID (DTO 오탈자 유지: dapart_ID2)
		            ps.setString(6, orderDTO.getDapart_ID2());

		            // 실행
		            return ps.executeUpdate();
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        return 0;
		    }
	}
	
	
	
	// update
	public int updateOrder(String orderKey, int newState) {
		
		
		try {
			
			// DB 접속
			Connection conn = getConn();
			
			// SQL 준비
			String query = " update from orders";
				   query += " set order_state = ? ";
				   query += " where order_key = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setInt(1, newState);     // 1 또는 3
		    ps.setString(2, orderKey);
			
			// SQL 실행
		    return ps.executeUpdate();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	// delete
		public int deleteOrder(OrderDTO orderDTO) {
			
			int result = -1;
			
			try {
				
				// DB 접속
				Connection conn = getConn();
				
				// SQL 준비
				String query = " delete from orders";
					   query += " where order_key = ?";
			   
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, orderDTO.getOrder_key());
				
				// SQL 실행
				result = ps.executeUpdate();
			
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return result;
		}
		
	public List selectAllDep() {
			
		List list = new ArrayList();
		
		try {
			
			// DB 접속
			Connection con = getConn();
			
			// SQL 준비
			String query = " select w.worker_id, w.worker_name, w.dapart_ID2, d.depart_level"; 
				   query += " from worker w"; 
				   query += " join department d on w.dapart_ID2 = d.dapart_ID2";
				   
			PreparedStatement ps = con.prepareStatement(query);
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();
			
			// 결과 활용
			while(rs.next()) {
				OrderDTO dto = new OrderDTO();
				
				dto.setWorker_id(rs.getString("worker_id"));
				dto.setWorker_name(rs.getString("worker_name"));
				dto.setDapart_ID2(rs.getString("dapart_ID2"));
				dto.setDepart_level(rs.getString("depart_level"));
				
				list.add(dto);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}		
	
	
}
