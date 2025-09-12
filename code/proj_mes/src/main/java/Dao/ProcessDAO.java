package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.ProcessDTO;

public class ProcessDAO {
	
	// DB 접속하는 메소드
	private Connection getConn() {
		Connection conn = null;
		
		try {
			Context ctx = new InitialContext();
			DataSource dataFactory = (DataSource) ctx.lookup("java:/comp/env/jdbc/oracle");
			conn = dataFactory.getConnection();
			System.out.println("데이터베이스 연결 성공");
		} catch (Exception e) {
			System.err.println("데이터베이스 연결 실패");
			e.printStackTrace();
		}
		return conn;
	}
	
	// Create (등록) =========================
	
	// createProcess - 새로운 공정 등록
	public boolean createProcess(ProcessDTO dto) {
	    String sql = "INSERT INTO process"
	               + " (proc_id, item_code, dapart_id2, proc_name, proc_seq, proc_info, proc_img, process_check)"
	               + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	    try (Connection conn = getConn();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, dto.getProc_id());      
	        ps.setString(2, dto.getItem_code());
	        ps.setString(3, dto.getDapart_id2());
	        ps.setString(4, dto.getProc_name());
	        ps.setInt(5, dto.getProc_seq());
	        ps.setString(6, dto.getProc_info());
	        ps.setString(7, dto.getProc_img());
	        ps.setInt(8, dto.getProcess_check());

	        int result = ps.executeUpdate();
	        return result > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	
	// Read (조회) =========================
	
	// item 테이블에서 item_code 모두 조회
	public List<String> getAllItemCodes() {
		List<String> itemCodes = new ArrayList<>();
		String sql = "SELECT item_code FROM item";
		try (Connection conn = getConn();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					itemCodes.add(rs.getString("item_code"));
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return itemCodes;
	}
	
	// getProcessesBySearch - 품목 코드, 부서, 공정명으로 공정 목록 조회
	public List<ProcessDTO> getProcessesBySearch(String itemCode, String departLevel, String procName) {
		List<ProcessDTO> processes = new ArrayList<>();
		StringBuilder sql = new StringBuilder(
		        "SELECT p.*, d.depart_level FROM process p" 
		        + " INNER JOIN department d ON p.dapart_id2 = d.dapart_id2 WHERE 1=1" );

		if (departLevel != null && !departLevel.isEmpty()) {
			sql.append(" AND d.depart_level = ?");
		}
		if (itemCode != null && !itemCode.isEmpty()) {
			sql.append(" AND p.item_code = ?");
		}
		if (procName != null && !procName.isEmpty()) {
			sql.append(" AND p.proc_name = ?");
		}
		
		// 품목코드 전체 선택 시 공정 코드(proc_id) 기준 정렬
	    if (itemCode == null || itemCode.isEmpty()) {
	        sql.append(" ORDER BY p.proc_id");
	    } else {
	        sql.append(" ORDER BY p.proc_seq"); // 기존 정렬 유지
	    }
		
		try (Connection conn = getConn();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
			
				int paramIndex = 1;
		        if (departLevel != null && !departLevel.isEmpty()) pstmt.setString(paramIndex++, departLevel);
		        if (itemCode != null && !itemCode.isEmpty()) pstmt.setString(paramIndex++, itemCode);
		        if (procName != null && !procName.isEmpty()) pstmt.setString(paramIndex++, procName);

		        try (ResultSet rs = pstmt.executeQuery()) {
		            while (rs.next()) {
		                processes.add(createProcessDTO(rs));
		            }
		        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return processes;
	}
	
	// getProcessById - 공정ID 단건 조회
	public ProcessDTO getProcessById(String procId) {
		String sql = "SELECT p.*, d.depart_level, d.dapart_id2"
				   + " FROM process p"
				   + " INNER JOIN department d ON p.dapart_id2 = d.dapart_id2"
				   + " WHERE p.proc_id = ?";
		try (Connection conn = getConn();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, procId);
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return createProcessDTO(rs);
					}
				}
			} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// getProcessByItemCode - 품목코드 단건 조회
	public ProcessDTO getProcessByItemCode(String itemCode) {
		String sql = "SELECT * FROM process WHERE item_code = ?";
		try (Connection conn = getConn();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, itemCode);
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return createProcessDTO(rs);
					}
				}
		} catch (SQLException e) {
				e.printStackTrace();
		} 
		return null;
	}
	
	// getUniqueItemCodes - 유니크 품목코드 조회
	public List<String> getUniqueItemCodes() {
		List<String> itemCodes = new ArrayList<>();
		String sql = "SELECT DISTINCT item_code FROM process"
				   + " WHERE item_code IS NOT NULL ORDER BY item_code";
		try (Connection conn = getConn();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				itemCodes.add(rs.getString("item_code"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return itemCodes;
	}

	// getUniqueDepartLevels - 유니크 부서 레벨 조회
	public List<String> getUniqueDepartLevels() {
	    List<String> departLevels = new ArrayList<>();
	    
	    // process에 존재하는 부서만 가져오고, 제외할 부서는 필터링
	    String sql = "SELECT DISTINCT d.depart_level"
	               + " FROM department d"
	               + " JOIN (SELECT DISTINCT dapart_id2 FROM process) p ON d.dapart_id2 = p.dapart_id2"
	               + " WHERE d.depart_level NOT IN ('인사부', '자재관리')"
	               + " AND d.dapart_id2 IN (SELECT DISTINCT p.dapart_id2 FROM process p)"
	               + " ORDER BY d.depart_level";
	    
	    try (Connection conn = getConn();
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {
	        
	        while (rs.next()) {
	            departLevels.add(rs.getString("depart_level"));
	            System.out.println("테스트: " + rs.getString("depart_level"));
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return departLevels;
	}
	
	// getDepartLevelsByItemCode - 품목 코드에 해당하는 부서 목록을 조회 (update 모드 전용)
	public List<String> getDepartLevelsByItemCode(String itemCode) {
	    List<String> list = new ArrayList<>();
	    String sql = "SELECT DISTINCT d.depart_level " +
	                 "FROM process p " +
	                 "JOIN department d ON p.dapart_id2 = d.dapart_id2 " +
	                 "WHERE p.item_code = ? AND p.dapart_id2 IS NOT NULL";

	    try (Connection conn = getConn();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, itemCode);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                list.add(rs.getString("depart_level"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	// getProcNamesFromProcTable - proc 테이블에서 전체 공정명 조회 (new 모드 전용)
	public List<String> getUniqueProcNamesFromProcTable() {
	    List<String> procNames = new ArrayList<>();
	    String sql = "SELECT DISTINCT proc_name FROM process ORDER BY proc_name";
	    try (Connection conn = getConn();
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {
	        while (rs.next()) {
	            procNames.add(rs.getString("proc_name"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return procNames;
	}
	
	// getUniqueProcNames - 전체 유니크 공정명 조회 (기존 로직 유지, update 모드에 적합)
	public List<String> getUniqueProcNames() {
		List<String> procNames = new ArrayList<>();
		String sql = "SELECT DISTINCT proc_name FROM process WHERE proc_name IS NOT NULL";
		try (Connection conn = getConn();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					procNames.add(rs.getString("proc_name"));
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return procNames;
	}

	// getUniqueProcNamesByDepart - 부서별 유니크 공정명 조회
	public List<String> getUniqueProcNamesByDepart(String departLevel) {
	    List<String> procNames = new ArrayList<>();
	    String sql = "SELECT DISTINCT T1.proc_name " +
	                 "FROM process T1 " +
	                 "JOIN department T2 ON T1.dapart_id2 = T2.dapart_id2 " +
	                 "WHERE T2.depart_level = ?";

	    try (Connection conn = getConn();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, departLevel);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                procNames.add(rs.getString("proc_name"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return procNames;
	}
	
	// getProcNamesByItemAndDepart - 품목코드 + 부서로 공정명 조회
	public List<ProcessDTO> getProcNamesByItemAndDepart(String itemCode, String departLevel) {
	    List<ProcessDTO> list = new ArrayList<>();
	    
	    String sql = "SELECT p.proc_name, d.depart_level, p.item_code" 
	               + " FROM process p " 
	    		   + " LEFT JOIN department d ON p.dapart_id2 = d.dapart_id2" 
	               + " WHERE p.item_code = ?" 
	    		   + " AND d.depart_level = ?" 
	               + " AND p.proc_name IS NOT NULL" 
	    		   + " ORDER BY p.proc_seq";

	    try (Connection conn = getConn();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setString(1, itemCode);
	        pstmt.setString(2, departLevel);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                ProcessDTO dto = new ProcessDTO();
	                dto.setProc_name(rs.getString("proc_name"));
	                dto.setDepart_level(rs.getString("depart_level"));
	                dto.setItem_code(rs.getString("item_code"));
	                list.add(dto);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return list;
	}

	public String getDepartIdByLevel(String departLevel) {
		String dapartId = null;
		String sql = "SELECT dapart_id2 FROM department WHERE depart_level = ?";
		try (Connection conn = getConn();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, departLevel);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					dapartId = rs.getString("dapart_id2");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dapartId;
	}

	// Update (수정) =========================

	// updateProcess - 공정 수정
	public boolean updateProcess(ProcessDTO dto) {
		String sql = "UPDATE process SET proc_name = ?, process_check = ?, dapart_id2 = ?, proc_img = ?, proc_info = ?, proc_seq = ?, item_code = ?"
				   + " WHERE proc_id = ?";
		int result = 0;
		try (Connection conn = getConn();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
				pstmt.setString(1, dto.getProc_name());
				pstmt.setInt(2, dto.getProcess_check());
				pstmt.setString(3, dto.getDapart_id2()); 
				pstmt.setString(4, dto.getProc_img());
				pstmt.setString(5, dto.getProc_info());
				pstmt.setInt(6, dto.getProc_seq());
				pstmt.setString(7, dto.getItem_code());
				pstmt.setString(8, dto.getProc_id());
				
				result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result > 0;
	}
	
	// Delete (삭제) =========================
	
	// deleteProcess - 공정 삭제
	public boolean deleteProcess(String procId) {
		String sql = "DELETE FROM process WHERE proc_id = ?";
		int result = 0;
		try (Connection conn = getConn();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, procId);
				result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}return result > 0;
	}


	// Helper Method =========================

	// createProcessDTO - ResultSet → DTO 변환
	private ProcessDTO createProcessDTO(ResultSet rs) throws SQLException {
		ProcessDTO dto = new ProcessDTO();
		dto.setProc_id(rs.getString("proc_id"));
		dto.setProc_name(rs.getString("proc_name"));
		dto.setProc_info(rs.getString("proc_info"));
		dto.setProcess_check(rs.getInt("process_check"));
		dto.setDapart_id2(rs.getString("dapart_id2")); // 부서 코드도 필요
		dto.setProc_img(rs.getString("proc_img"));
		dto.setProc_seq(rs.getInt("proc_seq"));
		dto.setItem_code(rs.getString("item_code"));
		
		try {
			dto.setDepart_level(rs.getString("depart_level"));
		} catch (SQLException e) {
			// 컬럼이 존재하지 않으면 무시
		}
		return dto;
	}
}