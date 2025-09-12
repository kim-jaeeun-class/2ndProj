package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.BOMDTO;

public class BOMDAO {
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
	public List<BOMDTO> selectAllBOM() {
		List<BOMDTO> list = new ArrayList<BOMDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select"
					+ "     bom_id, item_code_1, item_code_2, require_amount"
					+ "		from bom";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				BOMDTO dto = new BOMDTO();
				
				dto.setBomID(rs.getString("bom_id"));
				dto.setItem_code_1(rs.getString("item_code_1"));
				dto.setItem_code_2(rs.getString("item_code_2"));
				dto.setRequire_amount(rs.getInt("require_amount"));
				
				list.add(dto);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	// TODO 필터링 조회 : 품목 코드, 품목명 기준(이거 완제품 코드, 완제품명 기준으로 수정하기)
	// 그런데 애초에 품목 코드, 품목명 나누지 말고 실제 뷰에서는 코드(이름) 이런 식으로 보여주기도 가능하지 않을까?
	// 지금 생산계획 코드 생성했던 것처럼 -> 참고해서 가능할 듯하니 고민해보기
	public List<BOMDTO> selectFilBOM(BOMDTO dto) {
		List<BOMDTO> list = new ArrayList<BOMDTO>();
		
		try {
			Connection conn = getConn();
			
			String query = "select"
					+ "     bom_id, item_code_1, item_code_2, require_amount"
					+ "		from bom"
					+ "		where item_code_1 = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, dto.getItem_code_1());
			ResultSet rs = ps.executeQuery();
			
			
			while(rs.next()) {
				BOMDTO filDto = new BOMDTO();
				
				filDto.setBomID(rs.getString("bom_id"));
				filDto.setItem_code_1(rs.getString("item_code_1"));
				filDto.setItem_code_2(rs.getString("item_code_2"));
				filDto.setRequire_amount(rs.getInt("require_amount"));
				
				list.add(filDto);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	// 삭제 : 체크박스 필요할 듯. BOM ID 기준 삭제.
	public int deleteBOM(BOMDTO dto) {
		
		int result = -1;
		try {
			// DB 접속 : 상단에서 생성해둠
			Connection conn = getConn();
			
			// SQL 준비
			String query = "delete from bom"
					+ "		where bom_id = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, dto.getBomID());
			
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
	
	// 수정 : BOM ID 기준 수정
	public int updateBOM(BOMDTO dto) {
		int result = -1;
		try {
			Connection conn = getConn();
			
			String query = "update bom"
					+ "		set"
					+ "    	item_code_1 = ?, item_code_2 = ?, require_amount = ?"
					+ "		where bom_id = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, dto.getItem_code_1());
			ps.setString(2, dto.getItem_code_2());
			ps.setInt(3, dto.getRequire_amount());
			ps.setString(4, dto.getBomID());
			
			result = ps.executeUpdate();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// TODO 등록 : bom id 제외 입력 필요. bomid는 B+오늘 날짜 6자리+2자리 숫자 구분자 넣기.
	// 생산 계획 DAO 참고해서 작업 진행하기
	public int insertBOM(BOMDTO dto) {
		
		int result = -1;
		try {
			
			Connection conn = getConn();
			
			String query = "insert into bom"
					+ "		(bom_id, item_code_1, item_code_2, require_amount)"
					+ "		values (?, ?, ?, ?)";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, dto.getBomID());
			ps.setString(2, dto.getItem_code_1());
			ps.setString(3, dto.getItem_code_2());
			ps.setInt(4, dto.getRequire_amount());
			
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
	
	// 오늘 날짜 prefix로 마지막 BOM ID 조회
	public String selectLastBOMID(String datePrefix) {
	    String lastID = null;
	    try {
	        Connection conn = getConn();
	        String query = "select max(bom_id) as last_id from bom where bom_id like ?";
	        PreparedStatement ps = conn.prepareStatement(query);
	        ps.setString(1, datePrefix + "%");
	        ResultSet rs = ps.executeQuery();
	        if(rs.next()) {
	            lastID = rs.getString("last_id");
	        }
	        ps.close();
	        conn.close();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    return lastID;
	}

	// 수정용
	public BOMDTO selectBOMByID(String bomID) {
	    BOMDTO dto = null;
	    try {
	        Connection conn = getConn();
	        String query = "SELECT bom_id, item_code_1, item_code_2, require_amount FROM bom WHERE bom_id = ?";
	        PreparedStatement ps = conn.prepareStatement(query);
	        ps.setString(1, bomID);
	        ResultSet rs = ps.executeQuery();
	        if(rs.next()) {
	            dto = new BOMDTO();
	            dto.setBomID(rs.getString("bom_id"));
	            dto.setItem_code_1(rs.getString("item_code_1"));
	            dto.setItem_code_2(rs.getString("item_code_2"));
	            dto.setRequire_amount(rs.getInt("require_amount"));
	        }
	        rs.close();
	        ps.close();
	        conn.close();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    return dto;
	}

}
