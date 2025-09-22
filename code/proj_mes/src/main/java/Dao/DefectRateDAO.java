package Dao;

import java.sql.*;
import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.DefectRateDTO;
import Dto.ItemDTO;
import Dto.ProcessDTO;

public class DefectRateDAO {

    // DB 연결
    private Connection getConnection() throws Exception {
        Context ctx = new InitialContext();
        DataSource dataFactory = (DataSource) ctx.lookup("java:/comp/env/jdbc/oracle");
        return dataFactory.getConnection();
    }

    // 불량률 리스트 조회
    public List<DefectRateDTO> getDefectRateList(String procId, String itemCode, String date) {
        List<DefectRateDTO> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.proc_id, p.proc_name, ");
        sql.append("       TO_CHAR(ir.start_time, 'YYYY-MM-DD') AS inspection_date, ");
        sql.append("       i.item_code, i.item_name, ");
        sql.append("       (NVL(ir.gd_quantity,0) + NVL(ir.bd_quantity,0)) AS production_qty, ");
        sql.append("       NVL(ir.bd_quantity,0) AS defect_qty, ");
        sql.append("       ROUND(CASE WHEN (NVL(ir.gd_quantity,0) + NVL(ir.bd_quantity,0)) = 0 ");
        sql.append("            THEN 0 ");
        sql.append("            ELSE (ir.bd_quantity * 100.0 / (ir.gd_quantity + ir.bd_quantity)) END, 2) AS defect_rate ");
        sql.append("FROM INSPECTION_RESULTS ir ");
        sql.append("LEFT JOIN CREATE_PLAN cp ON ir.cp_id = cp.cp_id ");
        sql.append("LEFT JOIN ITEM_MAP im ON cp.item_code = im.plan_item_code OR ir.cp_id = im.plan_item_code ");
        sql.append("LEFT JOIN ITEM i ON im.item_code = i.item_code ");
        sql.append("LEFT JOIN PROCESS p ON i.item_code = p.item_code ");
        sql.append("WHERE 1=1 ");

        if (procId != null && !procId.isEmpty()) {
            sql.append(" AND p.proc_id = ? ");
        }
        if (itemCode != null && !itemCode.isEmpty()) {
            sql.append(" AND i.item_code = ? ");
        }
        if (date != null && !date.isEmpty()) {
            sql.append(" AND TO_CHAR(ir.start_time, 'YYYY-MM-DD') = ? ");
        }

        sql.append(" ORDER BY ir.start_time DESC ");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (procId != null && !procId.isEmpty()) pstmt.setString(idx++, procId);
            if (itemCode != null && !itemCode.isEmpty()) pstmt.setString(idx++, itemCode);
            if (date != null && !date.isEmpty()) pstmt.setString(idx++, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DefectRateDTO dto = new DefectRateDTO();
                    dto.setProcId(rs.getString("proc_id"));
                    dto.setProcName(rs.getString("proc_name"));
                    dto.setInspectionDate(rs.getString("inspection_date"));
                    dto.setItemCode(rs.getString("item_code"));
                    dto.setItemName(rs.getString("item_name"));
                    dto.setProductionQty(rs.getInt("production_qty"));
                    dto.setDefectQty(rs.getInt("defect_qty"));
                    dto.setDefectRate(rs.getDouble("defect_rate"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

 // 공정 목록 조회 (공정명 기준으로 중복 제거)
    public List<ProcessDTO> getProcList() {
        List<ProcessDTO> list = new ArrayList<>();
        String sql = "SELECT MIN(proc_id) AS proc_id, proc_name " +
                     "FROM process " +
                     "WHERE proc_name IS NOT NULL " +
                     "GROUP BY proc_name " +
                     "ORDER BY proc_name";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ProcessDTO dto = new ProcessDTO();
                dto.setProc_id(rs.getString("proc_id"));   // ✅ 대표 proc_id
                dto.setProc_name(rs.getString("proc_name"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 품목 목록 조회
    public List<ItemDTO> getItemList() {
        List<ItemDTO> list = new ArrayList<>();
        String sql = "SELECT DISTINCT item_code, item_name FROM item WHERE item_name IS NOT NULL ORDER BY item_name";

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
}