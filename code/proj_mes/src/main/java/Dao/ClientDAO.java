package Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.ClientDTO;

public class ClientDAO {

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
    /** 목록 조회 */
    public List<ClientDTO> selectAll() {
        final String sql =
            "SELECT CLIENT_ID, CLIENT_NAME, CLIENT_PHONE, BUSINESS_NUMBER, " +
            "       BUSINESS_ITEM, CLIENT_ADDRESS, INOUT_DIVISION, WORKER_ID " +
            "  FROM CLIENT ORDER BY CLIENT_ID";

        List<ClientDTO> list = new ArrayList<>();
        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ClientDTO d = new ClientDTO();
                d.setClient_id(rs.getString("CLIENT_ID"));
                d.setClient_name(rs.getString("CLIENT_NAME"));
                d.setClient_phone(rs.getString("CLIENT_PHONE"));
                d.setBusiness_number(rs.getString("BUSINESS_NUMBER"));
                d.setBusiness_item(rs.getString("BUSINESS_ITEM"));
                d.setClient_address(rs.getString("CLIENT_ADDRESS"));
                d.setInout_division(rs.getString("INOUT_DIVISION"));
                d.setWorker_id(rs.getString("WORKER_ID"));
                list.add(d);
            }
            System.out.println("[ClientDAO] fetched rows = " + list.size());
        } catch (Exception e) {
            System.err.println("[ClientDAO] selectAll error");
            e.printStackTrace();
        }
        return list;
    }

    /** 등록: PK는 20자, INOUT_DIVISION(NUMBER)은 setInt로 바인딩 */
    public int insert(ClientDTO d) {
    	
    	
        //자동키 생성 규칙을 고민해야 함
        //지금은 자동 생성으로 키값을 생성하도록 구현됨
        //sql전체의 길이를 구해서 1000 + 길이 -1 의 숫자를 구한다
        //cl + 위에서 구한 숫자를 더한다. -> 최종형태는 문자열
    	
    	
    	final String sql =
    		    "INSERT INTO CLIENT ( " +
    		    "  CLIENT_ID, CLIENT_NAME, CLIENT_PHONE, BUSINESS_NUMBER, " +
    		    "  BUSINESS_ITEM, CLIENT_ADDRESS, INOUT_DIVISION, WORKER_ID " +
    		    ") VALUES ( " +
    		    "  'cl' || TO_CHAR(1000 + (SELECT COUNT(*) FROM CLIENT))" +
    		    " , ?, ?, ?, ?, ?, ?, ? " +
    		    ")";

//        final String sql =
//            "INSERT INTO CLIENT ( " +
//            "  CLIENT_ID, CLIENT_NAME, CLIENT_PHONE, BUSINESS_NUMBER, " +
//            "  BUSINESS_ITEM, CLIENT_ADDRESS, INOUT_DIVISION, WORKER_ID " +
//            ") VALUES ( " +
//             "cl"+ (1000 + " (SELECT COUNT(*) AS total_rows FROM CLIENT)")
//            + " , ?, ?, ?, ?, ?, ?, ? " +
//            ")";
        
        
        
        
        


        
//        public int insert(ClientDTO d) {
//            final String sql =
//                "INSERT INTO CLIENT ( " +
//                "  CLIENT_ID, CLIENT_NAME, CLIENT_PHONE, BUSINESS_NUMBER, " +
//                "  BUSINESS_ITEM, CLIENT_ADDRESS, INOUT_DIVISION, WORKER_ID " +
//                ") VALUES ( " +
//                "  SUBSTR(RAWTOHEX(SYS_GUID()), 1, 20), ?, ?, ?, ?, ?, ?, ? " +
//                ")";
            
        

        
        
        
        

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int i = 1;
            ps.setString(i++, d.getClient_name());
            ps.setString(i++, d.getClient_phone());     // 전화번호는 VARCHAR2 권장
            ps.setString(i++, d.getBusiness_number());  // 사업자번호도 VARCHAR2 권장
            ps.setString(i++, d.getBusiness_item());
            ps.setString(i++, d.getClient_address());

            // 출고=-1, 공통=0, 발주=1 (NUMBER 컬럼)
            if (d.getInout_division() == null || d.getInout_division().isBlank()) {
                ps.setNull(i++, java.sql.Types.NUMERIC);
            } else {
                ps.setInt(i++, Integer.parseInt(d.getInout_division()));
            }

            ps.setString(i++, d.getWorker_id());

            int r = ps.executeUpdate();
            System.out.println("[ClientDAO] insert rows = " + r);
            return r;

        } catch (Exception e) {
            System.err.println("[ClientDAO] insert error");
            e.printStackTrace();
            return 0;
        }
    }

    /** 실제 DB에서 여러 건 삭제(트랜잭션/배치) */
    public int deleteByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return 0;

        final String sql = "DELETE FROM CLIENT WHERE CLIENT_ID = ?";
        Connection con = null;
        int sum = 0;

        try {
            con = getConn();
            con.setAutoCommit(false); // 한꺼번에 처리(원자성)

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (String id : ids) {
                    ps.setString(1, id);
                    ps.addBatch();
                }
                int[] res = ps.executeBatch();

                // 영향 받은 행 수 합산
                for (int n : res) {
                    if (n == Statement.SUCCESS_NO_INFO) sum += 1;
                    else if (n == Statement.EXECUTE_FAILED) sum += 0;
                    else sum += n;
                }
            }

            con.commit();
            System.out.println("[ClientDAO] delete rows = " + sum);
            return sum;

        } catch (SQLIntegrityConstraintViolationException e) {
            // FK 제약 위반(ORA-02292 등) 시 롤백
            safeRollback(con);
            System.err.println("[ClientDAO] deleteByIds FK violation: " + e.getMessage());
            return 0;
        } catch (Exception e) {
            safeRollback(con);
            System.err.println("[ClientDAO] deleteByIds error");
            e.printStackTrace();
            return 0;
        } finally {
            safeClose(con);
        }
    }

    /** 단건 삭제(옵션) */
    public int deleteById(String id) {
        if (id == null || id.isBlank()) return 0;
        final String sql = "DELETE FROM CLIENT WHERE CLIENT_ID = ?";
        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            int r = ps.executeUpdate();
            System.out.println("[ClientDAO] deleteById rows = " + r);
            return r;
        } catch (Exception e) {
            System.err.println("[ClientDAO] deleteById error");
            e.printStackTrace();
            return 0;
        }
    }

    // --- 유틸 ---
    private void safeRollback(Connection con) {
        if (con != null) try { con.rollback(); } catch (Exception ignore) {}
    }
    private void safeClose(Connection con) {
        if (con != null) try { con.setAutoCommit(true); con.close(); } catch (Exception ignore) {}
    }
}
