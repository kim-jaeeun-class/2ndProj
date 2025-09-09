package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.WorkOrderDTO;

public class WorkOrderDAO {
	// 여기에서 DB 연결 및 SQL 실행
	// SQL 실행은 CONTROLLER가 바로 호출해서 실행하는 게 아니라 SERVICE를 사이에 두고 수행
	
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
	public List<WorkOrderDTO> selectAllWO() {
		List<WorkOrderDTO> listAll = new ArrayList<WorkOrderDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select"
					+ "     wo_num, wo_date, wo_duedate, wo_pq, wo_aq, "
					+ "     wo_ps, worker_id, item_code"
					+ "     from work_order";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				WorkOrderDTO dto = new WorkOrderDTO();
				
				dto.setWoNum(rs.getInt("wo_num"));
				dto.setWoDate(rs.getDate("wo_date"));
				dto.setWoDuedate(rs.getDate("wo_duedate"));
				dto.setWoPQ(rs.getInt("wo_pq"));
				dto.setWoAQ(rs.getInt("wo_aq"));
				dto.setWoPS(rs.getString("wo_ps"));
				dto.setWorkerID(rs.getString("worker_id"));
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
	
	// 필터링 조회 : 미진행/진행/완료(결제중, 미확인은 어디서 확인해야 하나...)
	public List<WorkOrderDTO> selectOrderWO() {
		List<WorkOrderDTO> listFilter = new ArrayList<WorkOrderDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select"
					+ "     wo_num, wo_date, wo_duedate, wo_pq, wo_aq, "
					+ "     wo_ps, worker_id, item_code"
					+ "     from work_order"
					+ "		where wo_ps = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			
			// 여기부터가 좀... 아닌 것 같은데.
			// 기존 전체 조회에서는 if문 안에 new WorkOrderDTO()를 넣었는데,
			// 이렇게 빼도 되나? -> 안 된다 진짜...
			// TODO : setString이 고민...
			
			ps.setString(1, WorkOrderDTO.getWoPS());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				
				WorkOrderDTO dto = new WorkOrderDTO();
				
				dto.setWoNum(rs.getInt("wo_num"));
				dto.setWoDate(rs.getDate("wo_date"));
				dto.setWoDuedate(rs.getDate("wo_duedate"));
				dto.setWoPQ(rs.getInt("wo_pq"));
				dto.setWoAQ(rs.getInt("wo_aq"));
				dto.setWoPS(rs.getString("wo_ps"));
				dto.setWorkerID(rs.getString("worker_id"));
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
}
