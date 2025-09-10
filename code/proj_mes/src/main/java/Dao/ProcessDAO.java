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
	    		 ProcessDTO dto = new ProcessDTO();
	    		 
	    		 // 쿼리 결과 컬럼들을 DTO 필드에 매핑
	    		 dto.setProc_id(rs.getString("proc_id"));
	    		 dto.setProc_name(rs.getString("proc_name"));
	    		 dto.setProcess_check(rs.getInt("process_check"));
	    		 dto.setDapart_id2(rs.getString("dapart_id2"));
	    		 dto.setProc_img(rs.getString("proc_img"));
	    		 dto.setProc_info(rs.getString("proc_info"));
	    		 dto.setProc_seq(rs.getInt("proc_seq"));
	    		 dto.setItem_code(rs.getString("item_code"));
	    		 
	    		 // 조인 결과인 depart_level 필드 매핑
	    		 dto.setDepart_level(rs.getString("depart_level"));
	    		 
	    		 // DTO 객체를 리스트에 추가
	    		 processList.add(dto);
	    	 }
	    	 
	     } catch (SQLException e) {
	    	 e.printStackTrace();
	     }
	     
	     return processList;
	}
	
	// 데이터베이스에서 모든 유니크한 담당 부서 목록을 가져오는 메서드 추가
    public List<String> getUniqueDepartLevels() {
        List<String> departLevels = new ArrayList<>();
        String query = "SELECT DISTINCT d.depart_level FROM department d ORDER BY d.depart_level";
        
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

    // 특정 담당 부서에 해당하는 공정 목록을 가져오는 메서드 추가
    public List<ProcessDTO> getProcessesByDepartLevel(String departLevel) {
        List<ProcessDTO> processList = new ArrayList<>();
        String query = "SELECT p.*, d.depart_level FROM process p JOIN department d ON p.dapart_id2 = d.depart_id2 WHERE d.depart_level = ?";
        
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, departLevel);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProcessDTO dto = new ProcessDTO();
                    dto.setProc_id(rs.getString("proc_id"));
                    dto.setProc_name(rs.getString("proc_name"));
                    dto.setProcess_check(rs.getInt("process_check"));
                    dto.setDapart_id2(rs.getString("dapart_id2"));
                    dto.setProc_img(rs.getString("proc_img"));
                    dto.setProc_info(rs.getString("proc_info"));
                    dto.setProc_seq(rs.getInt("proc_seq"));
                    dto.setItem_code(rs.getString("item_code"));
                    dto.setDepart_level(rs.getString("depart_level"));
                    processList.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return processList;
    }

}
