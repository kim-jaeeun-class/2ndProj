package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.ProPlanDTO;


public class ProPlanDAO {
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
	public List<ProPlanDTO> selectAllPP() {
		List<ProPlanDTO> listAll = new ArrayList<ProPlanDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select "
					+ "		cp_id, cp_count, start_date, end_date, cp_rate,"
					+ "		success_rate, defect_rate, bigo, item_code"
					+ "		from create_plan";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				ProPlanDTO dto = new ProPlanDTO();
				
				dto.setCpID(rs.getString("cp_id"));
				dto.setCpCount(rs.getInt("cp_count"));
				dto.setStartDate(rs.getDate("start_date"));
				dto.setEndDate(rs.getDate("end_date"));
				dto.setCpRate(rs.getInt("cp_rate"));
				dto.setSuccessRate(rs.getInt("success_rate"));
				dto.setDefectRate(rs.getInt("defect_rate"));
				dto.setBigo(rs.getString("bigo"));
				dto.setItemCode(rs.getString("item_code"));
				
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
	
	// 필터링 조회 : 계획 시작일(오름차순) 기준으로 조회
	public List<ProPlanDTO> selectPPDateUp(ProPlanDTO proPlanDTO) {
		List<ProPlanDTO> listFilter = new ArrayList<ProPlanDTO>();
		
		try {
			Connection conn = getConn();
			
			String query;
			PreparedStatement ps;
			
			if(proPlanDTO.getStartDate() == null || proPlanDTO.getEndDate() == null) {
				query = "	select "
			    		+ "	cp_id, cp_count, start_date, end_date, cp_rate, "
			    		+ "	success_rate, defect_rate, bigo, item_code "
			    		+ "	from create_plan order by start_date";
				ps = conn.prepareStatement(query);
			}
			else {
				query = "	select "
						+ "	cp_id, cp_count, start_date, end_date, cp_rate,"
						+ "	success_rate, defect_rate, bigo, item_code"
						+ "	from create_plan"
						+ "	where start_date >= ? and start_date <= ?"
						+ "	order by start_date";
				ps = conn.prepareStatement(query);
			}

	
			
			
			ps.setDate(1, proPlanDTO.getStartDate());
			ps.setDate(2, proPlanDTO.getEndDate());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				
				ProPlanDTO dto = new ProPlanDTO();
				
				dto.setCpID(rs.getString("cp_id"));
				dto.setCpCount(rs.getInt("cp_count"));
				dto.setStartDate(rs.getDate("start_date"));
				dto.setEndDate(rs.getDate("end_date"));
				dto.setCpRate(rs.getInt("cp_rate"));
				dto.setSuccessRate(rs.getInt("success_rate"));
				dto.setDefectRate(rs.getInt("defect_rate"));
				dto.setBigo(rs.getString("bigo"));
				dto.setItemCode(rs.getString("item_code"));
				
				listFilter.add(dto);
			}
			
			rs.close();
			ps.close();
			conn.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return listFilter;
		
	}
	
	public List<ProPlanDTO> selectPPDateDown(ProPlanDTO proPlanDTO) {
		List<ProPlanDTO> listFilter = new ArrayList<ProPlanDTO>();
		
		try {
			Connection conn = getConn();
			
			String query;
			PreparedStatement ps;
			
			if(proPlanDTO.getStartDate() == null || proPlanDTO.getEndDate() == null) {
				query = "	select "
			    		+ "	cp_id, cp_count, start_date, end_date, cp_rate, "
			    		+ "	success_rate, defect_rate, bigo, item_code "
			    		+ "	from create_plan order by start_date desc";
				ps = conn.prepareStatement(query);
			}
			else {
				query = "	select "
						+ "	cp_id, cp_count, start_date, end_date, cp_rate,"
						+ "	success_rate, defect_rate, bigo, item_code"
						+ "	from create_plan"
						+ "	where start_date >= ? and start_date <= ?"
						+ "	order by start_date desc";
				ps = conn.prepareStatement(query);
			}
			
			ps.setDate(1, proPlanDTO.getStartDate());
			ps.setDate(2, proPlanDTO.getEndDate());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				
				ProPlanDTO dto = new ProPlanDTO();
				
				dto.setCpID(rs.getString("cp_id"));
				dto.setCpCount(rs.getInt("cp_count"));
				dto.setStartDate(rs.getDate("start_date"));
				dto.setEndDate(rs.getDate("end_date"));
				dto.setCpRate(rs.getInt("cp_rate"));
				dto.setSuccessRate(rs.getInt("success_rate"));
				dto.setDefectRate(rs.getInt("defect_rate"));
				dto.setBigo(rs.getString("bigo"));
				dto.setItemCode(rs.getString("item_code"));
				
				listFilter.add(dto);
			}
			
			rs.close();
			ps.close();
			conn.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return listFilter;
		
	}
	
	// 작성
	
	public int insertPP(ProPlanDTO proPlanDTO) {
		
		int result = -1;
		
		try {
			
			Connection conn = getConn();
			
			String query = "insert into create_plan"
					+ "		(cp_id, cp_count, start_date, end_date, "
					+ "		bigo, item_code)"
					+ "		values (?, ?, ?, ?, ?, ?);";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, proPlanDTO.getCpID());
			ps.setInt(2, proPlanDTO.getCpCount());
			ps.setDate(3, proPlanDTO.getStartDate());
			ps.setDate(4, proPlanDTO.getEndDate());
			ps.setString(5, proPlanDTO.getBigo());
			ps.setString(6, proPlanDTO.getItemCode());
			
			result = ps.executeUpdate();
			
			ps.close();
			conn.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 삭제
	
	public int deletePP(ProPlanDTO proPlanDTO) {
		
		int result = -1;
		try {
			// DB 접속 : 상단에서 생성해둠
			Connection conn = getConn();
			
			// SQL 준비
			String query = "delete from create_plan "
						 + "where cp_id = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, proPlanDTO.getCpID());
			
			// SQL 실행
			result = ps.executeUpdate();

			ps.close();
			conn.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// 수정
	
	public int updatePP(ProPlanDTO proPlanDTO) {
		int result = -1;
		
		try {
			Connection conn = getConn();
			
			String query = "update create_plan"
					+ "		set cp_count = ?, start_date = ?, end_date = ?"
					+ "     bigo = ?, item_code = ?"
					+ "		where cp_id = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setInt(1, proPlanDTO.getCpCount());
			ps.setDate(2, proPlanDTO.getStartDate());
			ps.setDate(3, proPlanDTO.getEndDate());
			ps.setString(4, proPlanDTO.getBigo());
			ps.setString(5, proPlanDTO.getItemCode());
			ps.setString(6, proPlanDTO.getCpID());
			
			result = ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
