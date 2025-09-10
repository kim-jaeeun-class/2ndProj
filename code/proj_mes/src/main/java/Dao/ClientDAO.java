package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import Dto.ClientDTO;
import Dto.ItemDTO;

public class ClientDAO {
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

    
    
	 /** 전체 조회 */
    public List<ClientDTO> selectAll() {
        final String sql =
            "SELECT CLIENT_ID, CLIENT_NAME, CLIENT_PHONE, BUSINESS_NUMBER, " +
            "       BUSINESS_ITEM, CLIENT_ADDRESS, INOUT_DIVISION, WORKER_ID " +
            "  FROM CLIENT " +
            " ORDER BY CLIENT_ID";

        List<ClientDTO> list = new ArrayList<>();
        int rowCount = 0;

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
                rowCount++;
            }
            System.out.println("[ClientDAO] fetched rows = " + rowCount);

        } catch (Exception e) {
            System.err.println("[ClientDAO] selectAll error");
            e.printStackTrace();
        }
        return list;
    }

    /** 등록 */
    public int insert(ClientDTO d) {
        // ▶ 시퀀스 사용 버전 (CLIENT_SEQ 존재 가정)
        final String sql =
            "INSERT INTO CLIENT ( " +
            "  CLIENT_ID, CLIENT_NAME, CLIENT_PHONE, BUSINESS_NUMBER, " +
            "  BUSINESS_ITEM, CLIENT_ADDRESS, INOUT_DIVISION, WORKER_ID " +
            ") VALUES ( " +
            "  CLIENT_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ? " +
            ")";

        /* ▶ 시퀀스가 없다면 아래 주석 버전으로 교체 (CLIENT_ID는 트리거/IDENTITY로 생성된다고 가정)
        final String sql =
            "INSERT INTO CLIENT ( " +
            "  CLIENT_NAME, CLIENT_PHONE, BUSINESS_NUMBER, " +
            "  BUSINESS_ITEM, CLIENT_ADDRESS, INOUT_DIVISION, WORKER_ID " +
            ") VALUES ( ?, ?, ?, ?, ?, ?, ? )";
        */

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int i = 1;
            // 시퀀스 버전: 1~7번 파라미터
            // (시퀀스 미사용 버전이라면 i=1부터 동일 순서로 7개 세팅)
            ps.setString(i++, d.getClient_name());
            ps.setString(i++, d.getClient_phone());
            ps.setString(i++, d.getBusiness_number());
            ps.setString(i++, d.getBusiness_item());
            ps.setString(i++, d.getClient_address());
            ps.setString(i++, d.getInout_division());
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
	
	
	
	
	
	
	
	
	
	
	

    
    
    
    

}
