package Dao;

import Dto.InspectionDTO;
import Dto.ProcessDTO;
import Dto.StockDTO;
import Dto.WorkerDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class InspectionDAO {

    // DB 접속을 메소드
    private Connection getConn() throws Exception {
        Context ctx = new InitialContext();
        DataSource dataFactory = (DataSource) ctx.lookup("java:/comp/env/jdbc/oracle");
        return dataFactory.getConnection();
    }

    // Connection, Statement, ResultSet 자원을 닫는 메소드
    public void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //create 메소드 -----------------------------	
    
    // 새로운 검사 결과를 데이터베이스에 등록
    public int insertInspectionResult(InspectionDTO dto) {
        String sql = "INSERT INTO inspection_results (IR_ID, IR_TYPE, QUALITY_STATE, GD_QUANTITY, BD_QUANTITY, END_TIME, START_TIME, WORKER_ID, CP_ID, BD_REASON)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int result = 0;
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dto.getIr_id());
            pstmt.setInt(2, dto.getIr_type());
            pstmt.setInt(3, dto.getQuality_state());
            pstmt.setInt(4, dto.getGd_quantity());
            pstmt.setInt(5, dto.getBd_quantity());
            pstmt.setTimestamp(6, dto.getEnd_time());
            pstmt.setTimestamp(7, dto.getStart_time());
            pstmt.setString(8, dto.getWorker_id());
            pstmt.setString(9, dto.getCp_id());
            pstmt.setString(10, dto.getBd_reason());
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 새로운 검사 ID를 생성하여 반환
    public String generateInspectionId() {
        String sql = "SELECT COUNT(*) FROM inspection_results WHERE IR_ID LIKE ?";
        String today = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, today + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1) + 1;
                    return today + "-" + String.format("%03d", count);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return today + "-001";
    }

    // 모든 LOT 번호 목록을 조회
    public List<StockDTO> getLotNumbers() {
        List<StockDTO> lotNumbers = new ArrayList<>();
        String sql = "SELECT STOCK_ID, ITEM_CODE FROM stock";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                StockDTO stock = new StockDTO();
                stock.setStock_id(rs.getString("STOCK_ID"));
                stock.setItem_code(rs.getString("ITEM_CODE"));
                lotNumbers.add(stock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lotNumbers;
    }

    // 특정 품목 코드를 가진 공정 목록을 조회 
    public List<ProcessDTO> getProcessNames(String itemCode) {
        List<ProcessDTO> processList = new ArrayList<>();
        String sql = "SELECT PROC_ID, PROC_NAME FROM process WHERE ITEM_CODE = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProcessDTO process = new ProcessDTO();
                    process.setProc_id(rs.getString("PROC_ID"));
                    process.setProc_name(rs.getString("PROC_NAME"));
                    System.out.println(rs.getString("PROC_ID"));
                    System.out.println(rs.getString("PROC_NAME"));
                    processList.add(process);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processList;
    }

    // 모든 공정 목록을 조회
    public List<ProcessDTO> getAllProcesses() {
        List<ProcessDTO> processList = new ArrayList<>();
        String sql = "SELECT PROC_ID, PROC_NAME FROM process ORDER BY PROC_ID";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ProcessDTO process = new ProcessDTO();
                process.setProc_id(rs.getString("PROC_ID"));
                process.setProc_name(rs.getString("PROC_NAME"));
                processList.add(process);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processList;
    }

    // 모든 작업자 목록을 조회
    public List<WorkerDTO> getWorkers() {
        List<WorkerDTO> workerList = new ArrayList<>();
        String sql = "SELECT WORKER_ID, WORKER_NAME FROM worker";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                WorkerDTO worker = new WorkerDTO();
                worker.setWorker_id(rs.getString("WORKER_ID"));
                worker.setWorker_name(rs.getString("WORKER_NAME"));
                workerList.add(worker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workerList;
    }

    // 특정 ID에 해당하는 검사 결과를 조회
    public InspectionDTO getInspectionResultById(String irId) {
        String sql = "SELECT * FROM inspection_results WHERE IR_ID = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, irId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    InspectionDTO dto = new InspectionDTO();
                    dto.setIr_id(rs.getString("IR_ID"));
                    dto.setIr_type(rs.getInt("IR_TYPE"));
                    dto.setQuality_state(rs.getInt("QUALITY_STATE"));
                    dto.setGd_quantity(rs.getInt("GD_QUANTITY"));
                    dto.setBd_quantity(rs.getInt("BD_QUANTITY"));
                    dto.setEnd_time(rs.getTimestamp("END_TIME"));
                    dto.setStart_time(rs.getTimestamp("START_TIME"));
                    dto.setWorker_id(rs.getString("WORKER_ID"));
                    dto.setCp_id(rs.getString("CP_ID"));
                    dto.setBd_reason(rs.getString("BD_REASON"));
                    return dto;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // LOT ID로 품목 코드를 찾아 공정 목록을 반환하는 기능
    public List<ProcessDTO> getProcessesByLotId(String lotId) {
        List<ProcessDTO> processList = new ArrayList<>();
        String itemCode = null;
        String sql1 = "SELECT ITEM_CODE FROM stock WHERE STOCK_ID = ?";
        String sql2 = "SELECT PROC_ID, PROC_NAME FROM process WHERE ITEM_CODE = ?";
        
        try (Connection conn = getConn()) {
            // 1. LOT ID로 품목 코드 조회
            try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                pstmt.setString(1, lotId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        itemCode = rs.getString("ITEM_CODE");
                        System.out.println("lotId = " + lotId);
                    }
                }
            }
            
            // 2. 품목 코드로 공정 목록 조회
            if (itemCode != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                    pstmt.setString(1, itemCode);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            ProcessDTO process = new ProcessDTO();
                            process.setProc_id(rs.getString("PROC_ID"));
                            process.setProc_name(rs.getString("PROC_NAME"));
                            processList.add(process);
                            System.out.println("itemCode = " + itemCode);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processList;
    }
    
    // LOT ID로 검사 결과를 조회
    public List<InspectionDTO> getResultsByLotId(String lotId) {
        List<InspectionDTO> resultList = new ArrayList<>();
        String sql = "SELECT ir.IR_ID, ir.IR_TYPE, ir.START_TIME, ir.END_TIME,"
                   + " ir.BD_QUANTITY, ir.BD_REASON, ir.CP_ID,"
                   + " p.PROC_NAME, w.WORKER_NAME"
                   + " FROM inspection_results ir"
                   + " LEFT JOIN process p ON ir.CP_ID = p.PROC_ID"
                   + " LEFT JOIN worker w ON ir.WORKER_ID = w.WORKER_ID"
                   + " LEFT JOIN stock s ON s.ITEM_CODE = p.ITEM_CODE"
                   + " WHERE s.STOCK_ID = ? "
                   + " ORDER BY ir.START_TIME DESC";
        
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lotId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    InspectionDTO dto = new InspectionDTO();
                    dto.setIr_id(rs.getString("IR_ID"));
                    dto.setIr_type(rs.getInt("IR_TYPE"));
                    dto.setStart_time(rs.getTimestamp("START_TIME"));
                    dto.setEnd_time(rs.getTimestamp("END_TIME"));
                    dto.setBd_quantity(rs.getInt("BD_QUANTITY"));
                    dto.setBd_reason(rs.getString("BD_REASON"));
                    dto.setCp_id(rs.getString("CP_ID"));
                    dto.setProc_name(rs.getString("PROC_NAME"));
                    dto.setWorker_name(rs.getString("WORKER_NAME"));
                    resultList.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
    
    // 모든 검사 결과를 조회
    public List<InspectionDTO> getInspectionResults() {
        List<InspectionDTO> resultList = new ArrayList<>();
        String sql = "SELECT ir.IR_ID, ir.IR_TYPE, ir.START_TIME, ir.END_TIME, ir.BD_QUANTITY, ir.BD_REASON,"
                + " ir.CP_ID, p.PROC_NAME, w.WORKER_NAME"
                + " FROM inspection_results ir"
                + " LEFT JOIN process p ON ir.CP_ID = p.PROC_ID"
                + " LEFT JOIN worker w ON ir.WORKER_ID = w.WORKER_ID"
                + " ORDER BY ir.START_TIME DESC";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                InspectionDTO dto = new InspectionDTO();
                dto.setIr_id(rs.getString("IR_ID"));
                dto.setIr_type(rs.getInt("IR_TYPE"));
                dto.setStart_time(rs.getTimestamp("START_TIME"));
                dto.setEnd_time(rs.getTimestamp("END_TIME"));
                dto.setBd_quantity(rs.getInt("BD_QUANTITY"));
                dto.setBd_reason(rs.getString("BD_REASON"));
                dto.setCp_id(rs.getString("CP_ID"));
                dto.setProc_name(rs.getString("PROC_NAME"));
                dto.setWorker_name(rs.getString("WORKER_NAME"));
                resultList.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    // update 메소드 ---------------------------------

    // 기존 검사 결과를 수정
    public int updateInspectionResult(InspectionDTO dto) {
        String sql = "UPDATE inspection_results SET IR_TYPE=?, QUALITY_STATE=?, GD_QUANTITY=?, BD_QUANTITY=?, END_TIME=?, START_TIME=?, WORKER_ID=?, CP_ID=?, BD_REASON=? WHERE IR_ID=?";
        int result = 0;
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dto.getIr_type());
            pstmt.setInt(2, dto.getQuality_state());
            pstmt.setInt(3, dto.getGd_quantity());
            pstmt.setInt(4, dto.getBd_quantity());
            pstmt.setTimestamp(5, dto.getEnd_time());
            pstmt.setTimestamp(6, dto.getStart_time());
            pstmt.setString(7, dto.getWorker_id());
            pstmt.setString(8, dto.getCp_id());
            pstmt.setString(9, dto.getBd_reason());
            pstmt.setString(10, dto.getIr_id());
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // delete 메소드 -----------------
    
    // 특정 ID에 해당하는 검사 결과를 삭제
    public int deleteInspectionResult(String irId) {
        String sql = "DELETE FROM inspection_results WHERE IR_ID = ?";
        int result = 0;
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, irId);
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}