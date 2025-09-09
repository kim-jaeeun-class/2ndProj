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
	public List<ProPlanDTO> selectAllWO() {
		List<ProPlanDTO> listAll = new ArrayList<ProPlanDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select"
					+ "     cp_id, cp_count, cp_stat, start_date, end_date,"
					+ "     cp_rate, success_rate, defect_rate, bigo, item_code"
					+ "		from create_plan";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				ProPlanDTO dto = new ProPlanDTO();
				
				dto.setCpID(rs.getString("cp_id"));
				dto.setCpCount(rs.getInt("cp_count"));
				dto.setCpStat(rs.getInt("cp_stat"));
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
}
