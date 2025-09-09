package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.ProPerfDTO;


public class ProPerfDAO {
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
	
	// 전체 조회
	public List<ProPerfDTO> selectAllPerf() {
		List<ProPerfDTO> listAll = new ArrayList<ProPerfDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select"
					+ "     cs_id, wo_num, wo_date, cs_stat,"
					+ "     cs_date, cs_intype, cs_outtype, stock_id"
					+ "		from create_siljeok";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				ProPerfDTO dto = new ProPerfDTO();
				
				dto.setCsID(rs.getString("cs_id"));
				dto.setWoNum(rs.getInt("wo_num"));
				dto.setCsStat(rs.getInt("wo_date"));
				dto.setCsStat(rs.getInt("cs_stat"));
				dto.setCsDate(rs.getDate("cs_date"));
				dto.setCsInType(rs.getString("cs_intype"));
				dto.setCsOutType(rs.getString("cs_outtype"));
				dto.setStockID(rs.getString("stock_id"));
				
				listAll.add(dto);
			}
			
			rs.close();
			ps.close();
			conn.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return listAll;
		
	}
	// 필터 : 작업지시번호, 일자, 공정

	
	// 정렬(그룹화 및 정렬. 아마 내림차순?) : 작업지시별, 일자별, 작업공정별
	// 작업지시별
	public List<ProPerfDTO> selectWOPerf() {
		List<ProPerfDTO> listAll = new ArrayList<ProPerfDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select"
					+ "     cs_id, wo_num, wo_date, cs_stat,"
					+ "     cs_date, cs_intype, cs_outtype, stock_id"
					+ "		from create_siljeok"
					+ "		order by wo_date, wo_num";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				ProPerfDTO dto = new ProPerfDTO();
				
				dto.setCsID(rs.getString("cs_id"));
				dto.setWoNum(rs.getInt("wo_num"));
				dto.setCsStat(rs.getInt("wo_date"));
				dto.setCsStat(rs.getInt("cs_stat"));
				dto.setCsDate(rs.getDate("cs_date"));
				dto.setCsInType(rs.getString("cs_intype"));
				dto.setCsOutType(rs.getString("cs_outtype"));
				dto.setStockID(rs.getString("stock_id"));
				
				listAll.add(dto);
			}
			
			rs.close();
			ps.close();
			conn.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return listAll;
		
	}
	
	// 일자별
	public List<ProPerfDTO> selectDatePerf() {
		List<ProPerfDTO> listAll = new ArrayList<ProPerfDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select"
					+ "     cs_id, wo_num, wo_date, cs_stat,"
					+ "     cs_date, cs_intype, cs_outtype, stock_id"
					+ "		from create_siljeok"
					+ "		order by cs_date";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				ProPerfDTO dto = new ProPerfDTO();
				
				dto.setCsID(rs.getString("cs_id"));
				dto.setWoNum(rs.getInt("wo_num"));
				dto.setCsStat(rs.getInt("wo_date"));
				dto.setCsStat(rs.getInt("cs_stat"));
				dto.setCsDate(rs.getDate("cs_date"));
				dto.setCsInType(rs.getString("cs_intype"));
				dto.setCsOutType(rs.getString("cs_outtype"));
				dto.setStockID(rs.getString("stock_id"));
				
				listAll.add(dto);
			}
			
			rs.close();
			ps.close();
			conn.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return listAll;
		
	}
	
	
	// 삭제
	
	// 등록
	// input되는 요소 : 작업지시번호(작업지시서 테이블 참조), 지시상태, 
	// 				  LOT 번호(LOT-재고 테이블 참조), 입고 형태, 출고 형태
}
