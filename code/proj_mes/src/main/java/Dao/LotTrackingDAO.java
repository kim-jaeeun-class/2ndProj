package Dao;

import java.sql.*;
import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.LotTrackingDTO;
import Dto.ItemDTO;
import Dto.ProcessDTO;

public class LotTrackingDAO {
    
    // ================= DB 연결 =================
    private Connection getConnection() throws Exception {
        Context ctx = new InitialContext();
        DataSource dataFactory = (DataSource) ctx.lookup("java:/comp/env/jdbc/oracle");
        return dataFactory.getConnection();
    }

    // ================= Create =================
    public int insertLotTracking(LotTrackingDTO dto) {
        String sql = "INSERT INTO STOCK (stock_id, stock_date, stock_loc, stock_div, stock_stat, stock_number, item_code) "
                   + "VALUES (?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dto.getLotId());
            pstmt.setString(2, dto.getStartTime()); 
            pstmt.setInt(3, 1); // location 더미
            pstmt.setInt(4, 0); // division 더미
            pstmt.setInt(5, 0); // status 더미
            pstmt.setInt(6, dto.getQuantity());
            pstmt.setString(7, dto.getItemCode());

            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ================= Read =================

    // LOT 메인 목록 (중복 없는 LOT만)
    public List<LotTrackingDTO> selectLotTrackingList(String itemCode, String date) {
        List<LotTrackingDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT s.stock_id AS lot_id, ");
        sql.append("       i.item_code, i.item_name, ");
        sql.append("       NVL(s.stock_number,0) AS quantity, ");
        sql.append("       TO_CHAR(s.stock_date,'YYYY-MM-DD') AS start_time ");
        sql.append("FROM STOCK s ");
        sql.append("JOIN ITEM i ON s.item_code = i.item_code ");
        sql.append("WHERE 1=1 ");
        
        if (itemCode != null && !itemCode.isEmpty()) {
            sql.append(" AND i.item_code = ? ");
        }
        if (date != null && !date.isEmpty()) {
            sql.append(" AND TO_CHAR(s.stock_date, 'YYYY-MM-DD') = ? ");
        }
        
        sql.append(" ORDER BY s.stock_date DESC ");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int idx = 1;
            if (itemCode != null && !itemCode.isEmpty()) pstmt.setString(idx++, itemCode);
            if (date != null && !date.isEmpty()) pstmt.setString(idx++, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LotTrackingDTO dto = new LotTrackingDTO();
                    dto.setLotId(rs.getString("lot_id"));
                    dto.setItemCode(rs.getString("item_code"));
                    dto.setItemName(rs.getString("item_name"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setStartTime(rs.getString("start_time")); // stock_date를 가공 시작일자로 표시
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 공정 이력
    public List<LotTrackingDTO> selectProcessHistoryByLot(String lotId) {
        List<LotTrackingDTO> list = new ArrayList<>();
        String sql = "SELECT p.proc_name, " +
                     "       TO_CHAR(ir.start_time,'YYYY-MM-DD HH24:MI') AS start_time, " +
                     "       TO_CHAR(ir.end_time,'YYYY-MM-DD HH24:MI') AS end_time, " +
                     "       ir.worker_id " +
                     "FROM INSPECTION_RESULTS ir " +
                     "JOIN PROCESS p ON p.item_code = (SELECT item_code FROM STOCK WHERE stock_id = ?) " +
                     "WHERE ir.cp_id = ? " +
                     "ORDER BY ir.start_time";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lotId);
            pstmt.setString(2, lotId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LotTrackingDTO dto = new LotTrackingDTO();
                    dto.setProcName(rs.getString("proc_name"));
                    dto.setStartTime(rs.getString("start_time"));
                    dto.setEndTime(rs.getString("end_time"));
                    dto.setWorkerId(rs.getString("worker_id"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 검사 결과
    public List<LotTrackingDTO> selectInspectionResultByLot(String lotId) {
        List<LotTrackingDTO> list = new ArrayList<>();
        String sql = "SELECT p.proc_name, " +
                     "       CASE ir.ir_type WHEN 1 THEN '전수' WHEN 2 THEN '샘플' ELSE '재검' END AS ir_type, " +
                     "       dr.dr_name, ir.bd_quantity, " +
                     "       ir.worker_id, " +
                     "       TO_CHAR(ir.end_time,'YYYY-MM-DD HH24:MI') AS inspect_time " +
                     "FROM INSPECTION_RESULTS ir " +
                     "JOIN STOCK s ON s.stock_id = ir.cp_id " +
                     "LEFT JOIN PROCESS p ON p.item_code = s.item_code " +
                     "LEFT JOIN DEFECT_REASON dr ON dr.ir_id = ir.ir_id " +
                     "WHERE ir.cp_id = ? " +
                     "ORDER BY ir.end_time";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, lotId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LotTrackingDTO dto = new LotTrackingDTO();
                    dto.setProcName(rs.getString("proc_name"));
                    dto.setIrType(rs.getString("ir_type"));
                    dto.setDefectName(rs.getString("dr_name"));
                    dto.setQuantity(rs.getInt("bd_quantity"));
                    dto.setWorkerId(rs.getString("worker_id"));
                    dto.setEndTime(rs.getString("inspect_time"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 품목 목록 
    public List<ItemDTO> selectItemList() {
        List<ItemDTO> list = new ArrayList<>();
        String sql = "SELECT DISTINCT item_code, item_name FROM ITEM ORDER BY item_code";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ItemDTO dto = new ItemDTO();
                dto.setItem_code(rs.getString("item_code"));
                dto.setItem_name(rs.getString("item_name"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 전체 공정 목록
    public List<ProcessDTO> selectAllProcessList() {
        List<ProcessDTO> list = new ArrayList<>();
        String sql = "SELECT DISTINCT proc_id, proc_name FROM PROCESS ORDER BY proc_name";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ProcessDTO dto = new ProcessDTO();
                dto.setProc_id(rs.getString("proc_id"));
                dto.setProc_name(rs.getString("proc_name"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================= Update =================
    public int updateLotTracking(LotTrackingDTO dto) {
        String sql = "UPDATE STOCK SET stock_number = ? WHERE stock_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dto.getQuantity());
            pstmt.setString(2, dto.getLotId());
            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ================= Delete =================
    public int deleteLotTracking(String lotId) {
        String sql = "DELETE FROM STOCK WHERE stock_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lotId);
            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}