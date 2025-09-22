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
	// 1. 전체 조회할 때 실제 화면 기준 거래처명도 필요한데 join 조건 어떻게 줄지, 테이블에 연결할 속성 없는데 FK라 치고 넣어버려야 하는지 고민.
	// 우선 지금은 작업 지시서 테이블의 모든 컬럼 조회 기준으로 진행함 -> 그냥 거래처(납품처) 날렸다!
	// 2. 고민... 계획처럼 상세 조회가 없어서 품목 테이블이랑 join해서 조회하는 기능을 안 넣었는데,
	// 상식적으로 품목이랑 join 해서 전체 조회를 해야? 맞는 게 아닌지?
	// 아예 상세 페이지 모달을 추가해서 품목을... 볼 수 있게 해야 하는 게 아닌지?
	// 아무래도 MES 이해도 부족 문제인 듯
	// 위쪽 전부 갈아엎고 해결함
	public List<WorkOrderDTO> selectAllWO() {
		List<WorkOrderDTO> listAll = new ArrayList<WorkOrderDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select wo_num, wo_date, wo_duedate,"
					+ "		wo_pq, wo_aq, worker_id, item_code"
					+ "		from work_order";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				WorkOrderDTO dto = new WorkOrderDTO();
				
				dto.setWoNum(rs.getString("wo_num"));
				dto.setWoDate(rs.getDate("wo_date"));
				dto.setWoDuedate(rs.getDate("wo_duedate"));
				dto.setWoPQ(rs.getInt("wo_pq"));
				dto.setWoAQ(rs.getInt("wo_aq"));
				dto.setWorkerID(rs.getString("worker_id"));
				dto.setItem_code(rs.getString("item_code"));
				
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
	
	// 필터링 조회 : 작업 지시일 기준으로 조회
	public List<WorkOrderDTO> selectOrderWO(WorkOrderDTO workOrderDTO) {
		List<WorkOrderDTO> listFilter = new ArrayList<WorkOrderDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select"
					+ "     wo_num, wo_date, wo_duedate, wo_pq, wo_aq, "
					+ "     worker_id, item_code"
					+ "     from work_order"
					+ "		where wo_date = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			
			
			ps.setDate(1, workOrderDTO.getWoDate());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				
				WorkOrderDTO dto = new WorkOrderDTO();
				
				dto.setWoNum(rs.getString("wo_num"));
				dto.setWoDate(rs.getDate("wo_date"));
				dto.setWoDuedate(rs.getDate("wo_duedate"));
				dto.setWoPQ(rs.getInt("wo_pq"));
				dto.setWoAQ(rs.getInt("wo_aq"));
				dto.setWorkerID(rs.getString("worker_id"));
				dto.setItem_code(rs.getString("item_code"));
				
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
	// 실질적으로 필요한 내용은 품목 코드, 품목 이름만이라 우선 두 개만
	// 아니다 수량 넣어야 함 -> 이거 슬라이딩 입력창에 넣어버림. 그럼 여기 테이블에서 품목 수량을 날려야 하지 않나?
	public List<WorkOrderDTO> selectItemWO() {
		List<WorkOrderDTO> list = new ArrayList<WorkOrderDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select "
					+ "		item_code, item_name"
					+ "		from item";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				WorkOrderDTO dto = new WorkOrderDTO();
				
				dto.setItem_code(rs.getString("item_code"));
				dto.setItem_name(rs.getString("item_name"));
				
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
	
	// 조회 : 상세에서 bom 정보 확인용
	public List<WorkOrderDTO> selectBOM(WorkOrderDTO dto) {
		List<WorkOrderDTO> list = new ArrayList<WorkOrderDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select bom_id, item_code_2, require_amount"
					+ "		from bom"
					+ "		where item_code_2 = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, dto.getItem_code());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
		           WorkOrderDTO tempDto = new WorkOrderDTO();
		            tempDto.setBom_id(rs.getString("bom_id"));       // DTO에 bom_id 추가 필요
		            tempDto.setItem_code(rs.getString("item_code"));
		            tempDto.setBom_reqAm(rs.getInt("require_amount"));
		            

		            list.add(tempDto);
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
	
	// 조회 : 상세에서 공정 정보 조회용
	public List<WorkOrderDTO> selectProc(WorkOrderDTO dto) {
		List<WorkOrderDTO> list = new ArrayList<WorkOrderDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select proc_id, proc_name"
					+ "		from process"
					+ "		where item_code = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, dto.getItem_code());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
		           WorkOrderDTO tempDto = new WorkOrderDTO();
		            tempDto.setProc_id(rs.getString("proc_id"));       // DTO에 bom_id 추가 필요
		            tempDto.setProc_name(rs.getString("proc_name"));
		            tempDto.setItem_code(rs.getString("item_code"));

		            list.add(tempDto);
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
	
	// 등록 - 작업지시서 번호 변경용
    public String generateWoNum(String date) throws Exception {
        String woNum = null;
        String query = "select count(*) from work_order where wo_date = ?";
        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            if (rs.next()) count = rs.getInt(1);
            String seq = String.format("%02d", count + 1);
            woNum = date.replace("-", "") + "-" + seq;
        }
        return woNum;
    }
	
	// 등록
	//	- 입력되는 값
	//		+ 지시일, 작업지시NO, 담당자, 납기일, 지시 수량, 첨부, 품목(품목 테이블과 join)
    // 		TODO + 품목 목록을 봐야 하니 품목 테이블 join도 필요할 듯. 그리고 js에서 지시 수량 입력 제한 추가해야 함.
	public int insertWO(WorkOrderDTO dto) {
		
		int result = -1;
		try {
			// DB 접속 : 상단에서 생성해둠
			Connection conn = getConn();
			
			// TODO SQL 준비 : 지금 거래처 날리면서 여기도 수정해야함... 아마 됨?
			String query = "INSERT INTO WORK_ORDER"
					+ "     (wo_num, wo_date, wo_duedate, wo_pq,"
					+ "		worker_id, item_code, bom_id, proc_id)"
					+ "	    VALUES"
					+ "     (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(query);

			ps.setString(1, dto.getWoNum());
			ps.setDate(2, dto.getWoDate());
			ps.setDate(3, dto.getWoDuedate());
			ps.setInt(4, dto.getWoPQ());
			ps.setString(5, dto.getWorkerID());
			ps.setString(6, dto.getItem_code());
			// bom_id
			if (dto.getBom_id() == null || dto.getBom_id().isEmpty()) {
			    ps.setString(7, "0");
			} else {
			    ps.setString(7, dto.getBom_id());
			}

			// proc_id
			if (dto.getProc_id() == null || dto.getProc_id().isEmpty()) {
			    ps.setString(8, "0");
			} else {
			    ps.setString(8, dto.getProc_id());
			}
			
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
						 + "where wo_num = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, workOrderDTO.getWoNum());
			
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
	
	// 수정 : 전체 수정
	public int updateAllWO(WorkOrderDTO workOrderDTO) {
		int result = -1;
		try {
			Connection conn = getConn();
			
			String query = "update work_order"
					+ "		set wo_duedate = ?,"
					+ "    	wo_pq = ?, worker_id = ?, item_code = ?"
					+ "		where wo_num = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setDate(1, workOrderDTO.getWoDuedate());
			ps.setInt(2, workOrderDTO.getWoPQ());
			ps.setString(3, workOrderDTO.getWorkerID());
			ps.setString(4, workOrderDTO.getItem_code());
			ps.setString(5, workOrderDTO.getWoNum());
			
			result = ps.executeUpdate();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	// 수정 : 실제 생산량만 수정
	public int updateAQWO(WorkOrderDTO workOrderDTO) {
		int result = -1;
		try {
			Connection conn = getConn();
			
			String query = "update work_order"
					+ "		set wo_aq = ?"
					+ "		where wo_num = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setInt(1, workOrderDTO.getWoAQ());
			ps.setString(2, workOrderDTO.getWoNum());
			
			result = ps.executeUpdate();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 단일 작업지시 조회 (wo_num 기준)
	public WorkOrderDTO selectWOByNum(String woNum) {
	    WorkOrderDTO dto = null;
	    try {
	        Connection conn = getConn();
	        String query = "select wo_num, wo_date, wo_duedate, "
	        		+ "		wo_pq, wo_aq, worker_id, item_code, bom_id, proc_id"
	                + " 	from work_order where wo_num = ?";
	        PreparedStatement ps = conn.prepareStatement(query);
	        ps.setString(1, woNum);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            dto = new WorkOrderDTO();
	            dto.setWoNum(rs.getString("wo_num"));
	            dto.setWoDate(rs.getDate("wo_date"));
	            dto.setWoDuedate(rs.getDate("wo_duedate"));
	            dto.setWoPQ(rs.getInt("wo_pq"));
	            dto.setWoAQ(rs.getInt("wo_aq"));
	            dto.setWorkerID(rs.getString("worker_id"));
	            dto.setItem_code(rs.getString("item_code"));
	            dto.setBom_id(rs.getString("bom_id"));
	            dto.setProc_id(rs.getString("proc_id"));
	        }
	        rs.close();
	        ps.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return dto;
	}

	// 생산수량만 업데이트 (wo_aq)
	public int updateAQByWoNum(String woNum, int woAQ) {
	    int result = -1;
	    try {
	        Connection conn = getConn();
	        String query = "update work_order "
	        		+ "		set wo_aq = ?"
	        		+ "		where wo_num = ?";
	        PreparedStatement ps = conn.prepareStatement(query);
	        ps.setInt(1, woAQ);
	        ps.setString(2, woNum);
	        result = ps.executeUpdate();
	        ps.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
}
