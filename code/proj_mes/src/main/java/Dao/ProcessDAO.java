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
			// JNDI : 글씨로 가져오는 방식
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
	
	// 공정 정보 전체와 부서 레벨을 데이터베이스에서 가져오는 메소드
	public List<ProcessDTO> getProcessAllWithDepartLevel() {
		 List<ProcessDTO> processList = new ArrayList<>();
		 String query = "SELECT d.depart_level, p.* FROM process p INNER JOIN department d ON p.dapart_id2 = d.dapart_id2";
		
		try (Connection conn = getConn();
			 PreparedStatement pstmt = conn.prepareStatement(query);
			 ResultSet rs = pstmt.executeQuery()) {
			
			// 결과 집합을 한 줄씩 반복하며 DTO에 담기
			while (rs.next()) {
				processList.add(createProcessDTO(rs));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return processList;
	}
	
	// 데이터베이스에서 모든 유니크한 담당 부서 목록을 가져오는 메소드
	public List<String> getUniqueDepartLevels() {
        List<String> departLevels = new ArrayList<>();
        String query = "select depart_level from department"
        			 + " where dapart_id2 in ('D003', 'D004', 'D005')";
        
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                departLevels.add(rs.getString("depart_level"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departLevels;
    }

    // 특정 담당 부서에 해당하는 공정 목록을 가져오는 메소드
	public List<ProcessDTO> getProcessesByDepartLevel(String departLevel) {
        List<ProcessDTO> processes = new ArrayList<>();
        String query = "SELECT p.*, d.depart_level FROM process p JOIN department d ON p.dapart_id2 = d.dapart_id2 WHERE d.depart_level = ?";
        
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, departLevel);
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
    
    // 부서와 공정명으로 필터링하는 메소드
	public List<ProcessDTO> getProcessesByDepartAndName(String departLevel, String procName) {
    	List<ProcessDTO> processes = new ArrayList<>();
    	String sql = "SELECT p.*, d.depart_level FROM process p JOIN department d ON p.dapart_id2 = d.dapart_id2 WHERE d.depart_level = ? AND p.proc_name = ?";

    	try (Connection conn = getConn();
    		PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		pstmt.setString(1, departLevel);
    		pstmt.setString(2, procName);
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

    // 모든 공정 데이터를 가져오는 메소드
    public List<ProcessDTO> getAllProcesses() {
        List<ProcessDTO> processes = new ArrayList<>();
        String sql = "SELECT p.*, d.depart_level FROM process p JOIN department d ON p.dapart_id2 = d.dapart_id2";
        
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                processes.add(createProcessDTO(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return processes;
    }
    
    // 부서별 유니크한 공정명 목록을 가져오는 메소드
    public List<String> getUniqueProcNamesByDepart(String departLevel) {
        List<String> procNames = new ArrayList<>();
        String sql = "SELECT DISTINCT p.proc_name FROM process p JOIN department d ON p.dapart_id2 = d.dapart_id2 WHERE d.depart_level = ?";
        
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

    // 모든 유니크한 공정명 목록을 가져오는 메서드
    public List<String> getUniqueProcNames() {
        List<String> procNames = new ArrayList<>();
        String sql = "SELECT DISTINCT p.proc_name FROM process p JOIN department d ON p.dapart_id2 = d.dapart_id2";
        
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

    // ResultSet에서 데이터를 읽어 ProcessDTO 객체를 생성하는 헬퍼 메소드
    private ProcessDTO createProcessDTO(ResultSet rs) throws SQLException {
    	ProcessDTO dto = new ProcessDTO();
    	dto.setProc_id(rs.getString("proc_id"));
    	dto.setProc_name(rs.getString("proc_name"));
    	dto.setDepart_level(rs.getString("depart_level"));
    	dto.setProc_info(rs.getString("proc_info"));
    	dto.setProcess_check(rs.getInt("process_check"));
    	dto.setDapart_id2(rs.getString("dapart_id2"));
    	dto.setProc_img(rs.getString("proc_img"));
    	dto.setProc_seq(rs.getInt("proc_seq"));
    	dto.setItem_code(rs.getString("item_code"));
    	return dto;
    }
    
    /* CUD 관련 메소드 */
    
    // 공정 ID와 부서를 기반으로 단일 공정의 상세 정보를 가져오는 메소드
    public ProcessDTO getProcessByIdAndDepart(String procId, String departLevel) {
        String sql = "SELECT p.*, d.depart_level FROM process p JOIN department d ON p.dapart_id2 = d.dapart_id2 WHERE p.proc_id = ? AND d.depart_level = ?";
        
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, procId);
            pstmt.setString(2, departLevel);
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
    
    // 새로운 공정 정보를 데이터베이스에 등록하는 메소드
    public boolean createProcess(ProcessDTO dto) {
        String sql = "INSERT INTO process (proc_id, proc_name, dapart_id2, proc_info) VALUES (?, ?, ?, ?)";
        int result = 0;
        
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dto.getProc_id());
            pstmt.setString(2, dto.getProc_name());
            pstmt.setString(3, dto.getDapart_id2());
            pstmt.setString(4, dto.getProc_info());
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result > 0;
    }
    
    // 기존 공정 정보 업데이트(수정) 메서드 (이미지 포함)
    public boolean updateProcess(ProcessDTO dto) {
        String sql = "UPDATE process SET proc_name = ?, dapart_id2 = ?, proc_info = ?, proc_img = ? WHERE proc_id = ?";
        int result = 0;
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            	pstmt.setString(1, dto.getProc_name());
            	pstmt.setString(2, dto.getDapart_id2());
            	pstmt.setString(3, dto.getProc_info());
            	pstmt.setString(4, dto.getProc_img()); // 이미지 경로 업데이트
            	pstmt.setString(5, dto.getProc_id());
            	result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result > 0;
    }
    
	 // 공정 정보를 삭제하는 메서드
    public boolean deleteProcess(String procId) {
        String sql = "DELETE FROM process_tb WHERE proc_id = ?";
        
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, procId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public ProcessDTO getProcessById(String procId) {
        String sql = "SELECT p.*, d.depart_level FROM process p JOIN department d ON p.dapart_id2 = d.dapart_id2 WHERE p.proc_id = ?";
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

    // 유니크한 품목 코드 목록을 가져오는 메서드
    public List<String> getUniqueItemCodes() {
        List<String> itemCodes = new ArrayList<>();
        String sql = "SELECT DISTINCT item_code FROM process WHERE item_code IS NOT NULL ORDER BY item_code";
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
    
    // 특정 품목 코드에 해당하는 부서 목록을 가져오는 메서드
    public List<String> getDepartLevelsByItemCode(String itemCode) {
        List<String> departLevels = new ArrayList<>();
        String sql = "SELECT DISTINCT d.depart_level FROM process p JOIN department d ON p.dapart_id2 = d.dapart_id2 WHERE p.item_code = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    departLevels.add(rs.getString("depart_level"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departLevels;
    }

    // 특정 품목 코드와 부서에 해당하는 공정명 목록을 가져오는 메서드
    public List<String> getProcNamesByItemAndDepart(String itemCode, String departLevel) {
        List<String> procNames = new ArrayList<>();
        String sql = "SELECT DISTINCT p.proc_name FROM process p JOIN department d ON p.dapart_id2 = d.dapart_id2 WHERE p.item_code = ? AND d.depart_level = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemCode);
            pstmt.setString(2, departLevel);
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
    
    // 복합 조건으로 공정 정보를 조회하는 메서드
    public List<ProcessDTO> getProcessesBySearch(String itemCode, String departLevel, String procName) {
        List<ProcessDTO> processes = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.*, d.depart_level FROM process p JOIN department d ON p.dapart_id2 = d.dapart_id2 WHERE 1=1");

        if (itemCode != null && !itemCode.isEmpty()) {
            sql.append(" AND p.item_code = ?");
        }
        if (departLevel != null && !departLevel.isEmpty()) {
            sql.append(" AND d.depart_level = ?");
        }
        if (procName != null && !procName.isEmpty()) {
            sql.append(" AND p.proc_name = ?");
        }

        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (itemCode != null && !itemCode.isEmpty()) {
                pstmt.setString(index++, itemCode);
            }
            if (departLevel != null && !departLevel.isEmpty()) {
                pstmt.setString(index++, departLevel);
            }
            if (procName != null && !procName.isEmpty()) {
                pstmt.setString(index++, procName);
            }
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
    
    // 소속 부서에 따라 공정명을 가져오는 메서드
    public List<String> getProcNamesByDepart(String departLevel) {
        List<String> procNames = new ArrayList<>();
        String sql = "SELECT DISTINCT proc_name FROM process_tb WHERE dapart_id2 = ?";
        
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

    // DAO를 통해 특정 품목 코드에 해당하는 공정 정보를 가져오는 메소드
    public ProcessDTO getProcessByItemCode(String itemCode) {
        String sql = "SELECT p.*, d.depart_level FROM process p JOIN department d ON p.dapart_id2 = d.dapart_id2 WHERE p.item_code = ?";

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
    

}
