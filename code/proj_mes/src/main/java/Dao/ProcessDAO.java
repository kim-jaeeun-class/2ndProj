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
	
	// Create (등록) =========================

    // createProcess - 새로운 공정 등록
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
    
    
    // Read (조회) =========================
    
    // getProcessAllWithDepartLevel - 공정 전체 + 부서 레벨 조회
    public List<ProcessDTO> getProcessAllWithDepartLevel() {
        List<ProcessDTO> processList = new ArrayList<>();
        String query = "SELECT d.depart_level, p.* FROM process p INNER JOIN department d ON p.dapart_id2 = d.dapart_id2";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                processList.add(createProcessDTO(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return processList;
    }
    
    // getUniqueDepartLevels - 유니크 부서 목록 조회
    public List<String> getUniqueDepartLevels() {
        List<String> departLevels = new ArrayList<>();
        String query = "SELECT depart_level FROM department WHERE dapart_id2 IN ('D003','D004','D005')";
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

    // getProcessesByDepartLevel - 특정 부서의 공정 조회
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
    
    // getProcessesByDepartAndName - 부서 + 공정명으로 조회
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

    // getAllProcesses - 전체 공정 조회
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
    
    // getUniqueProcNamesByDepart - 부서별 유니크 공정명 조회
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

    // getUniqueProcNames - 전체 유니크 공정명 조회
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

    // getProcessByIdAndDepart - 공정ID + 부서로 조회
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
    
    // getProcessById - 공정ID 단건 조회
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
    
    // getUniqueItemCodes - 유니크 품목코드 조회
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

    // getDepartLevelsByItemCode - 품목코드로 부서 조회
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

    // getProcNamesByItemAndDepart - 품목코드 + 부서로 공정명 조회
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

    // getProcessesBySearch - 복합조건 검색
    public List<ProcessDTO> getProcessesBySearch(String itemCode, String departLevel, String procName) {
        List<ProcessDTO> processes = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.*, d.depart_level FROM process p JOIN department d ON p.dapart_id2 = d.dapart_id2 WHERE 1=1");
        if (itemCode != null && !itemCode.isEmpty()) sql.append(" AND p.item_code = ?");
        if (departLevel != null && !departLevel.isEmpty()) sql.append(" AND d.depart_level = ?");
        if (procName != null && !procName.isEmpty()) sql.append(" AND p.proc_name = ?");
        
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (itemCode != null && !itemCode.isEmpty()) pstmt.setString(index++, itemCode);
            if (departLevel != null && !departLevel.isEmpty()) pstmt.setString(index++, departLevel);
            if (procName != null && !procName.isEmpty()) pstmt.setString(index++, procName);
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
    
    // getProcNamesByDepart - 부서코드(dapart_id2)로 공정명 조회
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

    // getProcessByItemCode - 품목코드 단건 조회
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
    
    
    // Update (수정) ========================= 
    
    // updateProcess - 공정 수정
    public boolean updateProcess(ProcessDTO dto) {
        String sql = "UPDATE process SET proc_name = ?, dapart_id2 = ?, proc_info = ?, proc_img = ? WHERE proc_id = ?";
        int result = 0;
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dto.getProc_name());
            pstmt.setString(2, dto.getDapart_id2());
            pstmt.setString(3, dto.getProc_info());
            pstmt.setString(4, dto.getProc_img());
            pstmt.setString(5, dto.getProc_id());
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
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, procId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("공정 삭제 중 오류 발생: " + procId, e);
        }
    }
    
    
    // Helper Method =========================
    
    // createProcessDTO - ResultSet → DTO 변환
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
}

