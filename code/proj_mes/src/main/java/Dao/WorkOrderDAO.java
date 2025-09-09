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
	// TODO : 전체 조회할 때 실제 화면 기준 거래처명도 필요한데 join 조건 어떻게 줄지, 테이블에 연결할 속성 없는데 FK라 치고 넣어버려야 하는지 고민.
	//		  우선 지금은 작업 지시서 테이블의 모든 컬럼 조회 기준으로 진행함
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
	// 그냥 결제중 미확인을 안 넣으면 해결? 되지 않는지?? 편하게 살자
	public List<WorkOrderDTO> selectOrderWO(String woPS) {
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
			// setString이 고민... -> 해결함... 애초에 입력 받아야 하는데 ()를 왜 비웠냐
			
			ps.setString(1, woPS);
			
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
	
	// 조회
	// 품목 목록 확인용!
	public List<WorkOrderDTO> selectItemWO() {
		List<WorkOrderDTO> list = new ArrayList<WorkOrderDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select "
					+ "		item_code, item_name, item_bigo, "
					+ "		item_type, item_unit, item_price"
					+ "		from item;";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				WorkOrderDTO dto = new WorkOrderDTO();
				
				dto.setItemName(rs.getString("item_name"));
				dto.setItemBigo(rs.getString("item_bigo"));
				dto.setItemType(rs.getInt("item_type"));
				dto.setItemUnit(rs.getInt("item_unit"));
				dto.setItemPrice(rs.getString("item_price"));
				
				list.add(dto);
			}
			
			rs.close();
			ps.close();
			conn.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return list;
		
	}
	
	
	// 등록
	//	- 입력되는 값
	//		+ 지시일, 작업지시NO, 납품처, 담당자, 납기일, 첨부, 품목(품목 테이블과 join)
    // 		TODO + 품목 목록을 봐야 하니 품목 테이블 join도 필요할 듯
	public int insertEmp(WorkOrderDTO workOrderDTO) {
		
		int result = -1;
		try {
			// DB 접속 : 상단에서 생성해둠
			Connection conn = getConn();
			
			// SQL 준비
			String query = "INSERT INTO WORK_ORDER"
					+ "     (wo_num, wo_date, wo_duedate, wo_pq,"
					+ "		wo_aq, worker_id, item_code)"
					+ "	    VALUES"
					+ "     (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(query);

			ps.setInt(1, workOrderDTO.getWoNum());
			ps.setDate(2, workOrderDTO.getWoDate());
			ps.setDate(3, workOrderDTO.getWoDuedate());
			ps.setInt(4, workOrderDTO.getWoPQ());
			ps.setInt(5, workOrderDTO.getWoAQ());
			ps.setString(6, workOrderDTO.getWorkerID());
			ps.setString(7, workOrderDTO.getItemCode());
			
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
	
	// 삭제(다중 삭제도 가능)
	public int deleteWO(WorkOrderDTO workOrderDTO) {
		
		int result = -1;
		try {
			// DB 접속 : 상단에서 생성해둠
			Connection conn = getConn();
			
			// SQL 준비
			String query = "delete from work_order "
						 + "where wo_num = ? and wo_date = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setInt(1, workOrderDTO.getWoNum());
			ps.setDate(2, workOrderDTO.getWoDate());
			
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
}
