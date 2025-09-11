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

public class ItemDAO {

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
			String query = " select * from item";
			PreparedStatement ps = con.prepareStatement(query);
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();
			
			// 결과 활용
			while(rs.next()) {
				ItemDTO dto = new ItemDTO();
				
				dto.setItem_code(rs.getString("item_code"));
				dto.setItem_name(rs.getString("item_name"));
				dto.setItem_bigo(rs.getString("item_bigo"));
				dto.setItem_type(rs.getInt("item_type"));
				dto.setItem_price(rs.getString("item_price"));
				
				list.add(dto);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	// 상세페이지 하나만 조회
	public ItemDTO selectOneItem(ItemDTO itemDTO) {
		
		ItemDTO resultDTO = null;
		
		try {
				
			// DB 접속
			Connection conn = getConn();
			
			// SQL 준비
			String query = " select * from item";
				   query += " where item_code = ?";
				   
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, itemDTO.getItem_code());
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();
			
			// 결과 활용
			if(rs.next()) {

				// resultDTO가 null로 되어있어서 new 해줘야함
				resultDTO = new ItemDTO();
				
				resultDTO.setItem_code(rs.getString("item_code"));
				resultDTO.setItem_name(rs.getString("item_name"));
				resultDTO.setItem_bigo(rs.getString("item_bigo"));
				resultDTO.setItem_type(rs.getInt("item_type"));
				resultDTO.setItem_price(rs.getString("item_price"));
			
			}
				
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return resultDTO;
		
	}
	
	// delete
	public int deleteItem(ItemDTO itemDTO) {
		
		int result = -1;
		
		try {
			
			// DB 접속
			Connection conn = getConn();
			
			// SQL 준비
			String query = " delete from item";
				   query += " where item_code = ?";
		   
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, itemDTO.getItem_code());
			
			// SQL 실행
			result = ps.executeUpdate();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// insert
	public int insertItem(ItemDTO itemDTO) {
		
		int result = -1;
		
		try {
			
			// DB 접속
			Connection conn = getConn();
			
			// SQL 준비
			String query = " insert into item (item_code, item_name, item_bigo, item_type, item_price)";
				   query += " values(?, ?, ?, ?, ?)";
			
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, itemDTO.getItem_code());
			ps.setString(2, itemDTO.getItem_name());
			ps.setString(3, itemDTO.getItem_bigo());
			ps.setInt(4, itemDTO.getItem_type());
			ps.setString(5, itemDTO.getItem_price());
			
			// SQL 실행
			result = ps.executeUpdate();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// update
	public int updateItem(ItemDTO itemDTO) {
		
		int result = -1;
		
		try {
			
			// DB 접속
			Connection conn = getConn();
			
			// SQL 준비
			String query = " update from item";
			query += " set item_name = ?, ";
			query += "     item_bigo = ?, ";
			query += "     item_type = ?, ";
			query += "     item_price = ? ";
			query += " where item_code = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, itemDTO.getItem_name());
			ps.setString(2, itemDTO.getItem_bigo());
			ps.setInt(3, itemDTO.getItem_type());
			ps.setString(4, itemDTO.getItem_price());
			ps.setString(5, itemDTO.getItem_code());
			
			// SQL 실행
			result = ps.executeUpdate();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
