package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.OrderDTO;
import Dto.StockDTO;

public class StockDAO {

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
	
	

//	select s.STOCK_ID, s.STOCK_DATE, s.STOCK_LOC, s.STOCK_DIV, s.STOCK_STAT, s.STOCK_NUMBER, i.ITEM_CODE, i.item_price
//	from stock s
//	join item i on s.item_code = i.item_code;

	
	
	//////////////////////////////////  stock_list .html////////////////////////////////////////////////////////////
	
	//분류의 기준은 분명 품목 코드일 것이다.
	//그럼 이걸 품목에서 추가해 주어야 하는가??
	//그럼 이걸 품목에서 비고로 넣는건 어떨까??
	//차라리 품목 코드를 가져온후 글자2의 크기로 잘라 맵핑하자 2/2/2+@
	
//	아니면 그냥 조건이 주어지면 대분류 + 중분류 + 소분류 = 품목코드니까 문자열 위치 찾기 같은 기능으로 분류가 가능했을듯....?
			
	
	
//	select s.STOCK_ID, s.STOCK_DATE, s.STOCK_LOC, s.STOCK_DIV, s.STOCK_STAT, s.STOCK_NUMBER, i.ITEM_CODE, i.item_price
//	from stock s
//	join item i on s.item_code = i.item_code;
	
	
	// select
	public List stock_list_menu() {
		
		List list = new ArrayList();
		
		try {
			
			// DB 접속
			Connection con = getConn();
			
			// SQL 준비
			//itemcode를 기반으로 item table 조인
			//차라리 품목 코드를 가져온후 글자2의 크기로 잘라 맵핑하자 2/2/2+@
			//목록에서 조회를 할떄 
			String query = 
					" SELECT "
					+ "  s.STOCK_ID, s.STOCK_DATE, s.STOCK_LOC, s.STOCK_DIV, s.STOCK_STAT, s.STOCK_NUMBER, "
					+ "  i.ITEM_CODE, i.ITEM_PRICE, "
					+ "  CASE WHEN LENGTH(i.ITEM_CODE)    >= 2 THEN SUBSTR(i.ITEM_CODE, 1, 2) END AS big_category, "
					+ "  CASE WHEN LENGTH(i.ITEM_CODE)    >= 4 THEN SUBSTR(i.ITEM_CODE, 3, 2) END AS mid_category, "
					+ "  CASE WHEN LENGTH(i.ITEM_CODE)    >= 5 THEN SUBSTR(i.ITEM_CODE, 5)     END AS small_category "
					+ "FROM stock s "
					+ "JOIN item  i ON s.item_code = i.item_code";
			PreparedStatement ps = con.prepareStatement(query);
			
	
			
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();

			// 결과 활용
			while (rs.next()) {
			    StockDTO dto = new StockDTO();

			    dto.setStock_id(rs.getString("stock_id"));
			    dto.setStock_date(rs.getDate("stock_date"));
			    dto.setStock_loc(rs.getInt("stock_loc"));
			    dto.setStock_div(rs.getInt("stock_div"));
			    dto.setStock_number(rs.getInt("stock_number"));
			    dto.setItem_code(rs.getString("item_code"));

		
			 		    
			    
			    
			    
			    dto.setItem_price(rs.getString("item_price"));

			    // 새로 추가된 파생 컬럼들
			    dto.setBigCategory(rs.getString("big_category"));
			    dto.setMidCategory(rs.getString("mid_category"));
			    dto.setSmallCategory(rs.getString("small_category"));

			    list.add(dto);
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
	

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	// select
	public List selectAll() {
		
		List list = new ArrayList();
		
		try {
			
			// DB 접속
			Connection con = getConn();
			
			// SQL 준비
			String query = " select * from stock";
			PreparedStatement ps = con.prepareStatement(query);
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();
			
			// 결과 활용
			while(rs.next()) {
				StockDTO dto = new StockDTO();
				
				dto.setStock_id(rs.getString("stock_id"));
				dto.setStock_date(rs.getDate("stock_date"));
				dto.setStock_loc(rs.getInt("stock_loc"));
				dto.setStock_div(rs.getInt("stock_div"));
				dto.setStock_number(rs.getInt("stock_number"));
				dto.setItem_code(rs.getString("item_code"));
				
				list.add(dto);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	// 상세페이지 하나만 조회
	public StockDTO selectOneStock(StockDTO stockDTO) {
		
		StockDTO resultDTO = null;
		
		try {
				
			// DB 접속
			Connection conn = getConn();
			
			// SQL 준비
			String query = " select * from stock";
				   query += " where stock_id = ?";
				   
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, stockDTO.getStock_id());
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();
			
			// 결과 활용
			if(rs.next()) {

				// resultDTO가 null로 되어있어서 new 해줘야함
				resultDTO = new StockDTO();
				
				resultDTO.setStock_id(rs.getString("stock_id"));
				resultDTO.setStock_date(rs.getDate("stock_date"));
				resultDTO.setStock_loc(rs.getInt("stock_loc"));
				resultDTO.setStock_div(rs.getInt("stock_div"));
				resultDTO.setStock_number(rs.getInt("stock_number"));
				resultDTO.setItem_code(rs.getString("item_code"));
			
			}
				
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return resultDTO;
		
	}
	
	// delete
	public int deleteStock(StockDTO stockDTO) {
		
		int result = -1;
		
		try {
			
			// DB 접속
			Connection conn = getConn();
			
			// SQL 준비
			String query = " delete from stock";
				   query += " where stock_id = ?";
		   
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, stockDTO.getStock_id());
			
			// SQL 실행
			result = ps.executeUpdate();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// insert
	public int insertStock(StockDTO stockDTO) {
		
		int result = -1;
		
		try {
			
			// DB 접속
			Connection conn = getConn();
			
			// SQL 준비
			String query = " insert into stock (stock_id, stock_date, stock_loc, stock_div, stock_number, item_code)";
				   query += " values(?, ?, ?, ?, ?, ?)";
			
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, stockDTO.getStock_id());
			ps.setDate(2, stockDTO.getStock_date());
			ps.setInt(3, stockDTO.getStock_loc());
			ps.setInt(4, stockDTO.getStock_div());
			ps.setInt(5, stockDTO.getStock_number());
			ps.setString(6, stockDTO.getItem_code());
			
			// SQL 실행
			result = ps.executeUpdate();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// update
	public int updateStock(StockDTO stockDTO) {
		
		int result = -1;
		
		try {
			
			// DB 접속
			Connection conn = getConn();
			
			// SQL 준비
			String query = " update from stock";
			query += " set stock_date = ?, ";
			query += "     stock_loc = ?, ";
			query += "     stock_div = ?, ";
			query += "     stock_number = ?, ";
			query += "     item_code = ? ";
			query += " where stock_id = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setDate(1, stockDTO.getStock_date());
			ps.setInt(2, stockDTO.getStock_loc());
			ps.setInt(3, stockDTO.getStock_div());
			ps.setInt(4, stockDTO.getStock_number());
			ps.setString(5, stockDTO.getItem_code());
			ps.setString(6, stockDTO.getStock_id());
			
			// SQL 실행
			result = ps.executeUpdate();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
