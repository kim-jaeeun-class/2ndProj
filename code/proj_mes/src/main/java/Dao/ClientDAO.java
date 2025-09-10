package Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.ClientDTO;

public class ClientDAO {


    private Connection getConn() throws Exception {
  
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


    /** 등록 (시퀀스 없음, CLIENT_ID VARCHAR2(20) 제약 → 20자 PK 생성) */
    public int insert(ClientDTO d) {
        final String sql =
            "INSERT INTO CLIENT ( " +
            "  CLIENT_ID, CLIENT_NAME, CLIENT_PHONE, BUSINESS_NUMBER, " +
            "  BUSINESS_ITEM, CLIENT_ADDRESS, INOUT_DIVISION, WORKER_ID " +
            ") VALUES ( " +
            "  SUBSTR(RAWTOHEX(SYS_GUID()), 1, 20), ?, ?, ?, ?, ?, ?, ? " +
            ")";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int i = 1;
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
