package Dao;

import java.sql.*;
import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.StockDTO;

public class StockDAO {

	// DB 접속 메소드
	private Connection getConn() {
		Connection conn = null;
		
		try {
			
			Context ctx = new InitialContext();
			
			DataSource dataFactory = (DataSource)ctx.lookup("java:/comp/env/jdbc/oracle"); 
		
		// DB 접속
			conn = dataFactory.getConnection();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return conn;
	}

    // 목록 
    public List<StockDTO> selectAll() throws Exception {

        List<StockDTO> list = new ArrayList();
        
        try {
        	Connection con = getConn();
        	
        	String sql 	=	" SELECT s.stock_id, s.stock_date, s.stock_loc, s.stock_div, s.stock_stat, " 
        			+   "       s.stock_number, s.item_code, NVL(i.item_name, s.item_code) AS item_name " 
        			+   " FROM stock s " 
        			+   " LEFT JOIN item i ON i.item_code = s.item_code " 
        			+   " ORDER BY s.stock_date DESC, s.stock_id DESC";
        	
        	PreparedStatement ps = con.prepareStatement(sql);
        	
        	ResultSet rs = ps.executeQuery();
        		
    		while (rs.next()) {
    			StockDTO d = new StockDTO();
    			d.setStock_id(rs.getString("stock_id"));
    			d.setStock_date(rs.getDate("stock_date"));
    			d.setStock_loc(rs.getInt("stock_loc"));
    			d.setStock_div(rs.getInt("stock_div"));
    			d.setStock_stat(rs.getInt("stock_stat"));
    			d.setStock_number(rs.getInt("stock_number"));
    			d.setItem_code(rs.getString("item_code"));
    			
    			// 파생표시용
    			try { 
    				d.setItem_name(rs.getString("item_name")); 
				} catch (Throwable ignore) {
					
				}
    			list.add(d);
    		}
        		
        }catch (Exception e) {
        	e.printStackTrace();
        }
            
        return list;
    }

    // 하나 조회
    public StockDTO selectOneStock(String stockId) throws Exception {

		StockDTO resultDTO = null;

        try {
        	Connection conn = getConn();
        	
        	String sql 	=	" SELECT " 
        				+	"  s.stock_id, s.stock_date, s.stock_loc, s.stock_div, s.stock_number, s.item_code, " 
        				+	"  i.item_name, i.item_price "             // 단가/품명은 ITEM 테이블에서
        				+	" FROM stock s " 
        				+ 	" LEFT JOIN ITEM i ON i.item_code = s.item_code "  
        				+	" WHERE TRIM(s.stock_id) = TRIM(?)";
        	
        	PreparedStatement ps = conn.prepareStatement(sql);
        	ps.setString(1, stockId);
        	
        	ResultSet rs = ps.executeQuery();
        	
        	if (rs.next()) {
        		
        		resultDTO = new StockDTO();
        		
        		resultDTO.setStock_id(rs.getString("stock_id"));
        		resultDTO.setStock_date(rs.getDate("stock_date"));
        		resultDTO.setStock_loc(rs.getInt("stock_loc"));
        		resultDTO.setStock_div(rs.getInt("stock_div"));
        		resultDTO.setStock_number(rs.getInt("stock_number"));
        		resultDTO.setItem_code(rs.getString("item_code"));
        		
        		// ITEM 테이블에서 가져온 값들
        		resultDTO.setItem_name(rs.getString("item_name"));
        		
        		// item_price 컬럼
        		resultDTO.setItem_price(rs.getString("item_price"));
        	}
        	
        

        
        }catch (Exception e) {
        	e.printStackTrace();
        }
        return resultDTO;
    }

    // 등록 
    // stock_id = item_code + 4자리 (시퀀스 기반), stock_date = SYSDATE
    public int insertStock(StockDTO dto) {

        if (dto == null || dto.getItem_code() == null || dto.getItem_code().trim().isEmpty()) {
            throw new IllegalArgumentException("item_code 필요");
        }

        int result = -1;

        try {
            // 연결
        	Connection conn = getConn();

            // 시퀀스(접미사 4자리) 조회
            String seqSql = "SELECT LPAD(MOD(stock_seq.NEXTVAL, 10000), 4, '0') AS suf FROM dual";
            
            PreparedStatement psSeq = conn.prepareStatement(seqSql);
            
            ResultSet rs = psSeq.executeQuery();

            if (!rs.next()) {
                throw new SQLException("시퀀스 조회 실패");
            }
            
            String suffix  = rs.getString("suf");                 // 예: "0042"
            String itemCd  = dto.getItem_code().trim();
            String stockId = itemCd + suffix;                     // 최종 PK

            // 리소스 정리(다음 쿼리 전)
            rs.close();
            psSeq.close();

            // INSERT 준비
            String insSql 	=	" INSERT INTO stock " 
            				+	" (stock_id, stock_date, stock_loc, stock_div, stock_stat, stock_number, item_code) " 
            				+	" VALUES (?, SYSDATE, ?, ?, ?, ?, ?)";

            PreparedStatement psIns = conn.prepareStatement(insSql);

            // dto의 정수형이 null일 수도 있으니 0으로 기본값 처리
            Integer loc    = dto.getStock_loc();
            Integer div    = dto.getStock_div();
            Integer stat   = dto.getStock_stat();
            Integer number = dto.getStock_number();

            psIns.setString(1, stockId);
            psIns.setInt(2,  (loc    == null ? 0 : loc));
            psIns.setInt(3,  (div    == null ? 0 : div));
            psIns.setInt(4,  (stat   == null ? 0 : stat));
            psIns.setInt(5,  (number == null ? 0 : number));
            psIns.setString(6, itemCd);

            // 실행
            result = psIns.executeUpdate();

            // 호출자도 새 ID를 알 수 있게 DTO에 채워넣기 (예외는 무시)
            try { dto.setStock_id(stockId); } catch (Throwable ignore) {}

            // 리소스 닫기
            psIns.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace(); 
        }

        return result;
    }


    // 수정 
    public int updateStock(StockDTO dto) throws Exception {

    	int result = -1;
    	
        try {
        	Connection con = getConn();
        	
        	String sql 	=	" UPDATE stock SET " 
        				+	"  stock_loc = ?, stock_div = ?, stock_stat = ?, stock_number = ?, item_code = ? " 
        				+	" WHERE TRIM(stock_id) = TRIM(?)";
        	
        	PreparedStatement ps = con.prepareStatement(sql);
        	
    		ps.setInt(1, nz(dto.getStock_loc()));
    		ps.setInt(2, nz(dto.getStock_div()));
    		ps.setInt(3, nz(dto.getStock_stat()));
    		ps.setInt(4, nz(dto.getStock_number()));
    		ps.setString(5, nvl(dto.getItem_code()));
    		ps.setString(6, nvl(dto.getStock_id()));
    		
    		result = ps.executeUpdate();
    		
        }catch (Exception e) {
        	e.printStackTrace();
        }
        return result; 
        
    }

    // 삭제 
    public int deleteStock(StockDTO dto) throws Exception {
        
    	int result = -1;
    	try {
        	Connection con = getConn();

        	String sql = "DELETE FROM stock WHERE TRIM(stock_id) = TRIM(?)";
        	
        	PreparedStatement ps = con.prepareStatement(sql);
        	ps.setString(1, dto.getStock_id());
        	
        	ps.executeUpdate();
        	
        }catch (Exception e) {
        	e.printStackTrace();
        }
        return result;
        
    }



    /* 대/중/소(코드 기준) + 기간 검색 */
    public List<StockDTO> searchStocks(String big, String mid, String small, java.sql.Date from, java.sql.Date to) {
        
    	List<StockDTO> list = new ArrayList<>();
    	
        try {
            Connection con = getConn();

            String base =	"SELECT s.STOCK_ID, s.STOCK_DATE, s.STOCK_LOC, s.STOCK_DIV, s.STOCK_STAT, s.STOCK_NUMBER, " 
        				+	"       i.ITEM_CODE, NVL(i.ITEM_NAME, i.ITEM_CODE) AS ITEM_NAME, i.ITEM_PRICE, " 
        				+	"       b.BIG_NAME, m.MID_NAME, sm.SM_NAME " 
        				+	"  FROM STOCK s " 
        				+	"  JOIN ITEM i ON i.ITEM_CODE = s.ITEM_CODE " 
        				+	"  LEFT JOIN TB_BIG_CATEGORY   b  ON b.BIG_CODE = SUBSTR(i.ITEM_CODE,1,2) " 
        				+	"  LEFT JOIN TB_MID_CATEGORY   m  ON m.MID_CODE = SUBSTR(i.ITEM_CODE,3,2) " 
        				+	"  LEFT JOIN TB_SMALL_CATEGORY sm ON sm.SM_CODE = SUBSTR(i.ITEM_CODE,5,2) " 
        				+	" WHERE 1=1 ";

            StringBuilder where = new StringBuilder();
            List<Object> params = new ArrayList<>();

            if (nz(big))   { 
            	where.append(" AND SUBSTR(i.ITEM_CODE,1,2) = ?"); 
            	params.add(big); 
        	}
            if (nz(mid))   { 
            	where.append(" AND SUBSTR(i.ITEM_CODE,3,2) = ?"); 
            	params.add(mid); 
        	}
            if (nz(small)) { 
            	where.append(" AND SUBSTR(i.ITEM_CODE,5,2) = ?"); 
            	params.add(small); 
        	}

            if (from != null && to != null) {
                where.append(" AND TRUNC(s.STOCK_DATE) BETWEEN ? AND ? ");
                params.add(from); 
                params.add(to);
            } else if (from != null) {
                where.append(" AND TRUNC(s.STOCK_DATE) >= ? ");
                params.add(from);
            } else if (to != null) {
                where.append(" AND TRUNC(s.STOCK_DATE) <= ? ");
                params.add(to);
            }

            String sql = base + where + " ORDER BY s.STOCK_DATE DESC, s.STOCK_ID DESC";

            PreparedStatement ps = con.prepareStatement(sql);

            int idx = 1;
            for (Object p : params) {
                if (p instanceof java.sql.Date) ps.setDate(idx++, (java.sql.Date)p);
                else                            ps.setString(idx++, p.toString());
            }

            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                StockDTO d = new StockDTO();
                
                d.setStock_id(rs.getString("STOCK_ID"));
                d.setStock_date(rs.getDate("STOCK_DATE"));
                d.setStock_loc(rs.getInt("STOCK_LOC"));
                d.setStock_div(rs.getInt("STOCK_DIV"));
                d.setStock_stat(rs.getInt("STOCK_STAT"));
                d.setStock_number(rs.getInt("STOCK_NUMBER"));

                d.setItem_code(rs.getString("ITEM_CODE"));
                d.setItem_name(rs.getString("ITEM_NAME"));
                d.setItem_price(rs.getString("ITEM_PRICE"));

                d.setBigCategory(rs.getString("BIG_NAME"));
                d.setMidCategory(rs.getString("MID_NAME"));
                d.setSmallCategory(rs.getString("SM_NAME"));

                list.add(d);
            }
            rs.close(); 
            ps.close(); 
            con.close();
        } catch (Exception e) { 
        	e.printStackTrace(); 
    	}
        
        return list;
    }

    private static boolean nz(String s){ return s != null && !s.trim().isEmpty(); }
    // 유틸 
    private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
    private static String nvl(String s){ return s == null ? "" : s.trim(); }
    private static int nz(Integer i){ return i == null ? 0 : i; }
}
