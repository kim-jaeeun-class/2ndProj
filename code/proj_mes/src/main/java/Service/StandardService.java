package Service;

import java.util.*;
import Dao.OrderDAO;
import Dao.ProPlanDAO;
import Dao.ProcessDAO;
import Dao.StockDAO;
import Dto.OrderDTO;
import Dto.ProPlanDTO;
import Dto.ProcessDTO;
import Dto.StockDTO;
//import Service.StandardService.TableProvider;

/**
 * 선택된 분류(category)에 맞춰
 * - thead 컬럼 메타(List<Column>)
 * - tbody 데이터(List<Map<String,Object>>)
 * 를 한 번에 만들어주는 통합 서비스.
 */
public class StandardService {

    /** 화면용 컬럼 메타 */
    public static class Column {
        private final String key;   // row Map에서 읽을 키(= DTO/DAO 필드명/컬럼명과 1:1)
        private final String title; // 헤더에 표시할 이름
        public Column(String key, String title) { this.key = key; this.title = title; }
        public String getKey() { return key; }
        public String getTitle() { return title; }
    }

    
    
    /** 결과 DTO */
    public static class TableResult {
        private final String category;
        private final List<Column> columns;
        private final List<Map<String,Object>> data;
        public TableResult(String category, List<Column> columns, List<Map<String,Object>> data) {
            this.category = category;
            this.columns = columns;
            this.data = data;
        }
        public String getCategory() { return category; }
        public List<Column> getColumns() { return columns; }
        public List<Map<String,Object>> getData() { return data; }
    }
    
    
    
    /** 카테고리별 제공자 인터페이스 */
    public interface TableProvider {
        List<Column> columns();
        List<Map<String,Object>> data();
    }

    /** 카테고리 → Provider 매핑 */
    private final Map<String, TableProvider> providers = new HashMap<>();

    public StandardService() {
        // 기본 등록: 발주(orders)
        providers.put("발주", new OrderProvider());
        providers.put("재고", new StockProvider());
        providers.put("공정", new ProcProvider());
//        providers.put("BOM", new ProcProvider());
//        providers.put("생산", new ProplanProvider());
//        providers.put("품질", new ProcProvider());

        // 추후 공정/BOM/재고/생산/품질도 같은 패턴으로 providers.put("공정", new ProcessProvider()); 식으로 추가

    }

    /** 확장용 수동 등록 API(원하면 외부에서 주입) */
    public void register(String category, TableProvider provider) {
        providers.put(category, provider);
    }

    /** 메인 엔트리: 카테고리 입력 → 테이블 결과 반환 */
    public TableResult buildTable(String category) {
        TableProvider p = providers.get(category);
        if (p == null) {
            // 미선택/미등록 카테고리는 빈 결과
            return new TableResult(category, Collections.emptyList(), Collections.emptyList());
        }
        return new TableResult(category, p.columns(), p.data());
    }

    /* =========================
       Provider 구현: 발주(orders)
       ========================= */
    private static class OrderProvider implements TableProvider {
        private final OrderDAO dao = new OrderDAO(); // 주어진 DAO 그대로 사용

        @Override
        public List<Column> columns() {
            List<Column> cols = new ArrayList<>();
            cols.add(new Column("order_key",   "발주키"));
            cols.add(new Column("order_number","발주번호"));
            cols.add(new Column("order_date",  "발주일"));
            cols.add(new Column("order_pay",   "결제일"));
            cols.add(new Column("order_state", "상태"));
            cols.add(new Column("client_id",   "거래처ID"));
            cols.add(new Column("worker_id",   "사번"));
            cols.add(new Column("dapart_ID2",  "부서ID")); // DTO/DAO 필드명 그대로
            return cols;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Map<String, Object>> data() {
            List<Map<String,Object>> rows = new ArrayList<>();

            // dao.selectAll()이 throws Exception 이므로 여기서 처리
            final List<OrderDTO> list;
            try {
                // DAO가 raw List를 반환한다면 캐스팅 필요
                list = (List<OrderDTO>) dao.selectAll();
            } catch (Exception e) {

                throw new IllegalStateException("발주 목록 조회 중 오류", e);
            }

            // 제네릭이 있으니 캐스팅 없이 바로 사용
            for (OrderDTO d : list) {
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("order_key",    nz(d.getOrder_key()));
                row.put("order_number", nz(d.getOrder_number()));
                row.put("order_date",   d.getOrder_date()); // java.sql.Date 그대로
                row.put("order_pay",    d.getOrder_pay());
                row.put("order_state",  d.getOrder_state());
                row.put("client_id",    nz(d.getClient_id()));
                row.put("worker_id",    nz(d.getWorker_id()));
                row.put("dapart_ID2",   nz(d.getDapart_ID2()));
                rows.add(row);
            }
            return rows;
        }

        private static String nz(String s) { return s == null ? "" : s; }
    }
    
    /* =========================
    Provider 구현: 재고(Stock)
    ========================= */ 
    
    private static class StockProvider implements TableProvider {
        private final StockDAO dao = new StockDAO(); // 주어진 DAO 그대로 사용
        
        @Override
        public List<Column> columns() {
            List<Column> cols = new ArrayList<>();
            cols.add(new Column("stock_id",   "재고ID"));
            cols.add(new Column("stock_date","등록일"));
            cols.add(new Column("stock_loc",  "위치"));
            cols.add(new Column("stock_div",   "구분"));
            cols.add(new Column("stock_number", "수량"));
            cols.add(new Column("item_code",   "품목코드"));

            return cols;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Map<String, Object>> data() {
            List<Map<String,Object>> rows = new ArrayList<>();
            // dao.selectAll()이 throws Exception 이므로 여기서 처리
            final List<StockDTO> list;

           
            try {
                // DAO가 raw List를 반환한다면 캐스팅 필요
                list = (List<StockDTO>) dao.selectAll();
            } catch (Exception e) {

                throw new IllegalStateException("발주 목록 조회 중 오류", e);
            }

            
            for (Object o : list) {
            	StockDTO d = (StockDTO) o;
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("stock_id",    nz(d.getStock_id()));
                row.put("stock_loc", d.getStock_loc());
                row.put("stock_date",   d.getStock_date()); // java.sql.Date 그대로
                row.put("stock_div",    d.getStock_div());
                row.put("stock_number",  d.getStock_number());
                row.put("item_code",    nz(d.getItem_code()));

                rows.add(row);
            }
            return rows;
        }

        private static String nz(String s) { return s == null ? "" : s; }
    }
    

    
    
    /* =========================
    Provider 구현: 공정(Process)
    ========================= */    

    private static class ProcProvider implements TableProvider {
        private final ProcessDAO dao = new ProcessDAO();

        @Override
        public List<Column> columns() {
            List<Column> cols = new ArrayList<>();
            cols.add(new Column("proc_id",     "공정ID"));
            cols.add(new Column("proc_name",   "공정이름"));
            cols.add(new Column("item_code",   "품목코드"));
            cols.add(new Column("dapart_id2",  "부서ID"));   // 키 이름 통일!
            cols.add(new Column("proc_info",   "공정정보"));
            return cols;
        }

        @Override
        public List<Map<String, Object>> data() {
            List<Map<String, Object>> rows = new ArrayList<>();

            // ① ProcessDAO에 getAllProcesses()가 있으면 이 라인 사용
            List<ProcessDTO> list = dao.getAllProcesses();

            // ② 없다면 이 라인으로 전체 조회 대체 가능 (INNER JOIN 기준)
            // List<ProcessDTO> list = dao.getProcessesBySearch(null, null, null);

            for (ProcessDTO d : list) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("proc_id",     nz(d.getProc_id()));
                row.put("proc_name",   nz(d.getProc_name()));
                row.put("item_code",   nz(d.getItem_code()));
                row.put("dapart_id2",  nz(d.getDapart_id2())); // columns()와 동일 키
                row.put("proc_info",   nz(d.getProc_info()));
                rows.add(row);
            }
            return rows;
        }

        private static String nz(String s) { return s == null ? "" : s; }
    }
 
    
    
   
    
    
    
    /* =========================
    Provider 구현: 생산계획(ProPlan)
    ========================= */    

    
    
    
    private static class ProplanProvider implements TableProvider {
        private final ProPlanDAO dao = new ProPlanDAO(); // 주어진 DAO 그대로 사용
        
        @Override
        public List<Column> columns() {
            List<Column> cols = new ArrayList<>();
            cols.add(new Column("cp_id",   "재고ID"));
            cols.add(new Column("cp_count","공정이름"));
            cols.add(new Column("star_date",   "품목 코드"));
            cols.add(new Column("end_date",   "부서"));
            cols.add(new Column("cp_rate", "공정정보"));            
            cols.add(new Column("success_rate", "공정정보"));            
            cols.add(new Column("defect_rate", "공정정보"));            
            cols.add(new Column("bigo", "공정정보"));            
            cols.add(new Column("item_code", "공정정보"));            
           
           
            
            return cols;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Map<String, Object>> data() {
            List<Map<String,Object>> rows = new ArrayList<>();
            List<?> list = dao.selectAllPP(); // 원 DAO 시그니처 유지(raw List)
            for (Object o : list) {
            	ProPlanDTO d = (ProPlanDTO) o;
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("cp_id",    nz(d.getCpID()));
                row.put("cp_count", d.getCpCount());
                row.put("start_date",   d.getStartDate()); // java.sql.Date 그대로
                row.put("end_date",    d.getEndDate());
                row.put("cp_rate",  d.getCpRate());
                row.put("success_rate",  d.getSuccessRate());
                row.put("defect_rate",  d.getDefectRate());
                row.put("bigo",  d.getBigo());
                row.put("item_code",  d.getItemCode());


                rows.add(row);
            }
            return rows;
        }

        private static String nz(String s) { return s == null ? "" : s; }
    }    
    
    
    
    
    
    
    
    
    
    
    
}

    
   

    
    
    

    
 

    
    
    
    /* =========================
    Provider 구현: 생산계획(ProPlan)
    ========================= */    

    
    
    
//    private static class ProplanProvider implements TableProvider {
//        private final ProPlanDAO dao = new ProPlanDAO(); // 주어진 DAO 그대로 사용
//        
//        @Override
//        public List<Column> columns() {
//            List<Column> cols = new ArrayList<>();
//            cols.add(new Column("proc_id",   "재고ID"));
//            cols.add(new Column("proc_name","공정이름"));
//            cols.add(new Column("item_code",   "품목 코드"));
//            cols.add(new Column("dapart_id2",   "부서"));
//            cols.add(new Column("proc_info", "공정정보"));            
//           
//           
//            
//            return cols;
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        public List<Map<String, Object>> data() {
//            List<Map<String,Object>> rows = new ArrayList<>();
//            List<?> list = dao.getAllProcesses(); // 원 DAO 시그니처 유지(raw List)
//            for (Object o : list) {
//            	ProcessDTO d = (ProcessDTO) o;
//                Map<String,Object> row = new LinkedHashMap<>();
//                row.put("proc_id",    nz(d.getProc_id()));
//                row.put("proc_name", d.getProc_name());
//                row.put("item_code",   d.getItem_code()); // java.sql.Date 그대로
//                row.put("depart_id2",    d.getDapart_id2());
//                row.put("proc_info",  d.getProc_info());
//
//
//                rows.add(row);
//            }
//            return rows;
//        }
//
//        private static String nz(String s) { return s == null ? "" : s; }
//    }    
//    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

