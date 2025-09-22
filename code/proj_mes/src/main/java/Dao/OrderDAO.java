package Dao;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.OrderDTO;
import Dto.OrderDetDTO;

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

	// ===== 유틸 =====
	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty(); 
	}
  
	private static String safeStr(String s){ 
		return s==null ? "" : s.trim(); 
	}
  
	private static String enforceLen(String s, int max){

	    if (s == null) return null;
	    String v = s.trim();
	    return v.length() <= max ? v : v.substring(0, max);
	}


	/* 목록(총수량/총금액 포함) — item_price는 문자열이라 숫자 변환 */
	public List<OrderDTO> selectAll() throws Exception {
	  List<OrderDTO> list = new ArrayList<>();
	  
	  try {
		  
		  	// DB 접속
		  	Connection con = getConn();
		  
		  	// SQL 준비
		  	String sql 	=  	" SELECT o.order_key, o.order_number, o.order_date, o.order_state, " 
					  	+	"       d.depart_level, w.worker_name, " 
						+  	"       COUNT(od.item_code) AS totalQty, " 
						+  	"       NVL(SUM( NVL(od.quantity,0) * NVL(TO_NUMBER(REGEXP_REPLACE(od.item_price,'[^0-9.]','')),0) ), 0) AS totalAmt " 
						+  	" FROM orders o " 
						+  	" LEFT JOIN department d ON o.dapart_ID2 = d.dapart_ID2 " 
						+  	" LEFT JOIN worker     w ON o.worker_id   = w.worker_id " 
						+  	" LEFT JOIN order_detail od ON TRIM(od.order_key) = TRIM(o.order_key) " 
						+  	" GROUP BY o.order_key, o.order_number, o.order_date, o.order_state, d.depart_level, w.worker_name " 
						+  	" ORDER BY o.order_date DESC, o.order_key DESC";
		  
		  	PreparedStatement ps = con.prepareStatement(sql);
		  
		  	// SQL 실행
		  	ResultSet rs = ps.executeQuery();

	  		while (rs.next()) {
				OrderDTO dto = new OrderDTO();
				
				dto.setOrder_key(rs.getString("order_key"));
				dto.setOrder_number(rs.getString("order_number"));
				dto.setOrder_date(rs.getDate("order_date"));
				dto.setOrder_state(rs.getInt("order_state"));
				dto.setDepart_level(rs.getString("depart_level"));
				dto.setWorker_name(rs.getString("worker_name"));
				dto.setTotalQty(String.valueOf(rs.getLong("totalQty")));
				dto.setTotalAmt(String.valueOf(rs.getLong("totalAmt")));
				
				list.add(dto);
	  		}
			  
			  
	  }catch (Exception e) {
		  e.printStackTrace();
	  }
	  
	  return list;
	}
	
	/* 상세 헤더 1건 — order_key 기준 */
	public OrderDTO selectOneByKey(String orderKey) throws Exception {
	
		OrderDTO resultDTO = null;
		
		try {
			// DB 접속
			Connection con = getConn();

			// SQL 준비
			String sql 	=	" SELECT o.order_key, o.order_number, o.order_date, o.order_pay, o.order_state, o.bigo, " 
						+	"       o.client_id, c.client_name, c.business_number, c.client_phone, " 
						+	"       o.dapart_ID2, d.depart_level, o.worker_id, w.worker_name " 
						+	" FROM orders o " 
						+	" LEFT JOIN department d ON o.dapart_ID2 = d.dapart_ID2 " 
						+	" LEFT JOIN worker     w ON o.worker_id   = w.worker_id " 
						+	" LEFT JOIN client     c ON o.client_id   = c.client_id " 
						+	" WHERE TRIM(o.order_key) = TRIM(?)";
			
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, orderKey);
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();
			
			// 결과 활용
			if (!rs.next()) {
			
			}
			resultDTO = new OrderDTO();
			
			resultDTO.setOrder_key(rs.getString("order_key"));
			resultDTO.setOrder_number(rs.getString("order_number"));
			resultDTO.setOrder_date(rs.getDate("order_date"));
			resultDTO.setOrder_pay(rs.getDate("order_pay"));         
			resultDTO.setOrder_state(rs.getInt("order_state"));
			resultDTO.setBigo(rs.getString("bigo"));
			
			resultDTO.setClient_id(rs.getString("client_id"));
			resultDTO.setClient_name(rs.getString("client_name"));
			resultDTO.setBusiness_number(rs.getString("business_number"));
			resultDTO.setClient_phone(rs.getString("client_phone"));
			
			resultDTO.setDapart_ID2(rs.getString("dapart_ID2"));
			resultDTO.setDepart_level(rs.getString("depart_level"));
			
			resultDTO.setWorker_id(rs.getString("worker_id"));
			resultDTO.setWorker_name(rs.getString("worker_name"));
			
			
		}catch (Exception e) {
			
		}
	   
	      return resultDTO;
	    }

	/** 상세 품목 리스트 — order_key 기준 */
	public List<OrderDetDTO> selectOrderItemsByKey(String orderKey) throws Exception {
		
		List<OrderDetDTO> list = new ArrayList();
		

		try {
			
				Connection con = getConn();
		  
				String sql 	=	" SELECT item_code, item_price, quantity " 
							+	" FROM order_detail " 
							+	" WHERE TRIM(order_key) = TRIM(?) " 
							+	" ORDER BY rowid";

				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, orderKey);
				
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					OrderDetDTO d = new OrderDetDTO();
					d.setItem_code(rs.getString("item_code"));
					d.setItem_price(rs.getString("item_price")); // 문자열 그대로 저장
					d.setQuantity(rs.getInt("quantity"));
			    
					list.add(d);
				}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	/* 신규 등록: 헤더 + 상세 일괄 저장  반환: 생성된 order_key */
	public String insertOrderDet(OrderDTO order, List<OrderDetDTO> details) {

	    String orderKey = null;

	    try {
	        // DB 접속
	        Connection conn = getConn();
	        conn.setAutoCommit(false); // 트랜잭션 시작

	        // 시퀀스 조회 (order_seq.NEXTVAL)
	        String seqSql = "SELECT order_seq.NEXTVAL FROM dual";
	        PreparedStatement ps = conn.prepareStatement(seqSql);
	        ResultSet rs = ps.executeQuery();

	        long nextVal = 0;
	        if (rs.next()) {
	            nextVal = rs.getLong(1);
	        } else {
	            throw new SQLException("시퀀스 조회 실패");
	        }

	        // 오늘 날짜 + 시퀀스로 키 생성
	        String today = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
	        String seq2 = String.format("%02d", nextVal % 100);

	        orderKey = "A" + today + seq2; // PK
	        String orderNumber = "B" + today + seq2; // 발주번호

	        rs.close();
	        ps.close();

	        // 헤더 저장 (orders)
	        String sqlHeader 	= "INSERT INTO orders "
	                			+ "(order_key, order_number, order_date, order_pay, order_state, client_id, worker_id, dapart_ID2, bigo) "
	                			+ "VALUES (?, ?, SYSDATE, ?, ?, ?, ?, ?, ?)";

	        ps = conn.prepareStatement(sqlHeader);
	        ps.setString(1, orderKey);
	        ps.setString(2, orderNumber);
	        ps.setDate(3, order.getOrder_pay());
	        ps.setInt(4, order.getOrder_state());
	        ps.setString(5, order.getClient_id());
	        ps.setString(6, order.getWorker_id());
	        ps.setString(7, order.getDapart_ID2());
	        ps.setString(8, order.getBigo() == null ? "" : order.getBigo());
	        ps.executeUpdate();
	        ps.close();

	        // 상세 저장 (order_detail)
	        if (details != null && !details.isEmpty()) {
	            String sqlDetail 	= " INSERT INTO order_detail "
	                    			+ " (order_key, item_code, item_price, quantity) VALUES (?, ?, ?, ?)";

	            ps = conn.prepareStatement(sqlDetail);

	            for (OrderDetDTO d : details) {
	                if (d == null || d.getItem_code() == null || d.getItem_code().isEmpty()) continue;

	                ps.setString(1, orderKey);
	                ps.setString(2, d.getItem_code());
	                ps.setString(3, d.getItem_price());
	                ps.setInt(4, d.getQuantity());
	                ps.addBatch();
	            }

	            ps.executeBatch();
	            ps.close();
	        }

	        // 커밋
	        conn.commit();
	        conn.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // 생성된 orderKey 반환
	    return orderKey;
	}



	public void deleteOrdersCascade(List<String> orderKeys) throws Exception {
	
		try {
			Connection conn = getConn();
			conn.setAutoCommit(false);
	
			String delDetail 	= " DELETE FROM order_detail"
								+ " WHERE order_key = ?";
			String delHeader 	= " DELETE FROM orders"
								+ " WHERE order_key = ?";
			
			PreparedStatement psDet = conn.prepareStatement(delDetail);
			PreparedStatement psHdr = conn.prepareStatement(delHeader);
	 
			for (String k : orderKeys) {
				if (k == null || k.trim().isEmpty()) continue;
				String key = k.trim();
			 
				 // 상세 먼저
				 psDet.setString(1, key);
				 psDet.addBatch();
	 
		 		 // 헤더 나중
				 psHdr.setString(1, key);
				 psHdr.addBatch();
		
				 psDet.executeBatch();
				 psHdr.executeBatch();
				 conn.commit();
			 }
	 
		 }catch (Exception e) {
		
		 }
	
	}


	/* 헤더 + 상세 전체 업데이트 */
	public void updateOrderFull(OrderDTO order, java.util.List<OrderDetDTO> details) {

	    Connection conn = null;
	    PreparedStatement psU = null; // update header
	    PreparedStatement psD = null; // delete details
	    PreparedStatement psI = null; // insert details

	    try {
	        // DB 접속 + 트랜잭션 시작
	        conn = getConn();
	        conn.setAutoCommit(false);

	        // 헤더 업데이트
	        String updHdr 	=	" UPDATE orders SET " 
	        				+	" order_pay = ?, order_state = ?, client_id = ?, worker_id = ?, dapart_ID2 = ?, bigo = ? " 
	        				+	" WHERE order_key = ?";

	        psU = conn.prepareStatement(updHdr);
	        
	        psU.setDate  (1, order.getOrder_pay());
	        psU.setInt   (2, order.getOrder_state());
	        psU.setString(3, order.getClient_id());
	        psU.setString(4, order.getWorker_id());
	        psU.setString(5, order.getDapart_ID2());
	        psU.setString(6, order.getBigo());
	        psU.setString(7, order.getOrder_key());
	        
	        psU.executeUpdate();

	        // 상세 전량 삭제
	        String delDtl = " DELETE FROM order_detail WHERE order_key = ?";
	        
	        psD = conn.prepareStatement(delDtl);
	        
	        psD.setString(1, order.getOrder_key());
	        
	        psD.executeUpdate();

	        // 상세 재등록(배치)
	        if (details != null && !details.isEmpty()) {
	            String insDtl = "INSERT INTO order_detail (order_key, item_code, item_price, quantity) VALUES (?, ?, ?, ?)";
	            psI = conn.prepareStatement(insDtl);

	            for (OrderDetDTO d : details) {
	                if (d == null) continue;
	                String code = (d.getItem_code() == null) ? "" : d.getItem_code().trim();
	                if (code.isEmpty()) continue;

	                psI.setString(1, order.getOrder_key());
	                psI.setString(2, code);
	                psI.setString(3, (d.getItem_price() == null) ? "0" : d.getItem_price().trim());
	                psI.setInt   (4, d.getQuantity());
	                psI.addBatch();
	            }
	            psI.executeBatch();
	        }

	        // 커밋
	        conn.commit();

	        // 리소스 닫기
	        if (psI != null) psI.close();
	        if (psD != null) psD.close();
	        if (psU != null) psU.close();
	        if (conn != null) conn.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}



	/* 상세 전체 교체 — 기존 삭제 후 새로 삽입 */
	public void replaceOrderDetails(String orderKey, List<OrderDetDTO> details) {

	    Connection conn = null;
	    PreparedStatement psDel = null;
	    PreparedStatement psIns = null;

	    try {
	        // 연결 + 트랜잭션 시작
	        conn = getConn();
	        conn.setAutoCommit(false);

	        // 기존 상세 전부 삭제
	        String delSql = "DELETE FROM order_detail WHERE order_key = ?";
	        
	        psDel = conn.prepareStatement(delSql);
	        psDel.setString(1, orderKey);
	        psDel.executeUpdate();
	        psDel.close();

	        // 신규 상세 삽입 (비어있으면 '전부 삭제' 상태 유지)
	        if (details != null && !details.isEmpty()) {
	            
	        	String insSql = "INSERT INTO order_detail (order_key, item_code, item_price, quantity) VALUES (?, ?, ?, ?)";
	            psIns = conn.prepareStatement(insSql);

	            for (OrderDetDTO d : details) {
	                if (d == null) continue;

	                String code  = (d.getItem_code()  == null) ? ""  : d.getItem_code().trim();
	                if (code.isEmpty()) continue;

	                String price = (d.getItem_price() == null) ? "0" : d.getItem_price().trim();

	                psIns.setString(1, orderKey);
	                psIns.setString(2, code);
	                psIns.setString(3, price);         // 숫자 컬럼이면 setInt/BigDecimal로 변경
	                psIns.setInt   (4, d.getQuantity());
	                psIns.addBatch();
	            }
	            psIns.executeBatch();
	            psIns.close();
	        }

	        // 커밋
	        conn.commit();
	        // 연결 종료
	        conn.close();

	    } catch (Exception e) {
	        // 오류 시 롤백
	        if (conn != null) {
	            try { conn.rollback(); } catch (Exception ignore) {}
	        }
	        
	        e.printStackTrace(); // 콘솔 로깅
	    }
	}

	/* 진행상태 변경 */
	public int updateOrder(String orderKey, int newState) {

	    int result = -1;

	    try {
	        // DB 연결
	    	Connection conn = getConn();

	        // SQL 준비
	        String sql = "UPDATE orders SET order_state = ? WHERE order_key = ?";
	        PreparedStatement ps = conn.prepareStatement(sql);

	        ps.setInt(1, newState);
	        ps.setString(2, orderKey);

	        // 실행
	        result = ps.executeUpdate();

	        // 리소스 닫기
	        ps.close();
	        conn.close();

	    } catch (Exception e) {
	        e.printStackTrace(); 
	    }

	    return result;
	}

	/* 단건 삭제 (상세→헤더) */
	public int deleteOrder(String orderKey) {
		Connection conn = null;
	    int result = -1;

	    try {
	        // DB 연결
	    	conn = getConn();
	        conn.setAutoCommit(false); // 트랜잭션 시작

	        // 상세 삭제
	        String delDtl = "DELETE FROM order_detail WHERE order_key = ?";
	        
	        PreparedStatement psD = conn.prepareStatement(delDtl);
	        psD.setString(1, orderKey);
	        psD.executeUpdate();
	        psD.close();

	        // 헤더 삭제
	        String delHdr = "DELETE FROM orders WHERE order_key = ?";
	        
	        PreparedStatement psH = conn.prepareStatement(delHdr);
	        psH.setString(1, orderKey);
	        result = psH.executeUpdate(); // 삭제된 행 수 반환 (0 또는 1)
	        psH.close();

	        // 커밋
	        conn.commit();
	        conn.close();

	    } catch (Exception e) {
	        // 오류 발생 시 롤백
	        if (conn != null) {
	            try {
	                conn.rollback();
	            } catch (Exception rollbackEx) {
	                rollbackEx.printStackTrace();
	            }
	        }
	        e.printStackTrace(); 
	    }

	    // 헤더 삭제 결과 반환
	    return result;
	}

	/* 부서/담당자 모달 목록 */
	public List<OrderDTO> selectAllDep() {
		
	  List<OrderDTO> list = new ArrayList();
	  
	  try {
		  Connection con = getConn();
		  
		  String sql 	=	" SELECT w.worker_id, w.worker_name, w.dapart_ID2, d.depart_level " 
				  		+	" FROM worker w " 
			  			+	" JOIN department d ON w.dapart_ID2 = d.dapart_ID2 " 
			  			+	" ORDER BY d.depart_level, w.worker_name";
		  
		  PreparedStatement ps = con.prepareStatement(sql);
		  
		  ResultSet rs = ps.executeQuery();
		  
		  while (rs.next()) {
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
