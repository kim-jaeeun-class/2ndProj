package Service;

import java.util.*;

import Dao.BOMDAO;
import Dao.OrderDAO;
import Dao.ProPlanDAO;
import Dao.ProcessDAO;
import Dao.StockDAO;
import Dto.BOMDTO;
import Dto.OrderDTO;
import Dto.ProPlanDTO;
import Dto.ProcessDTO;
import Dto.StockDTO;

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

    /** 카테고리 → Provider 매핑 (등록 순서 유지) */
    private final Map<String, TableProvider> providers = new LinkedHashMap<>();

    public StandardService() {
        // 기본 등록
        providers.put("발주", new OrderProvider());
        providers.put("재고", new StockProvider());
        providers.put("공정", new ProcProvider());
        providers.put("BOM",  new BOMProvider());
        providers.put("생산", new ProplanProvider());
        // providers.put("품질", new QualityProvider()); // 추후 추가
    }

    /** 확장용 수동 등록 API(원하면 외부에서 주입) */
    public void register(String category, TableProvider provider) {
        providers.put(category, provider);
    }

    /** 메인 엔트리: 카테고리 입력 → 테이블 결과 반환 (단일 카테고리) */
    public TableResult buildTable(String category) {
        TableProvider p = providers.get(category);
        if (p == null) {
            // 미선택/미등록 카테고리는 빈 결과
            return new TableResult(category, Collections.emptyList(), Collections.emptyList());
        }
        return new TableResult(category, p.columns(), p.data());
    }

    // =========================
    // ★ 전체(요약) : 번호/분야/아이디/상세 로 평탄화한 단일 표
    // =========================
    public TableResult buildSummaryAll() {
        // 표 헤더(번호는 JSP에서 자동 번호로 렌더링되므로 3개만 내려줌)
        List<Column> cols = Arrays.asList(
            new Column("field",  "분야"),
            new Column("id",     "아이디"),
            new Column("detail", "상세")
        );

        List<Map<String,Object>> rows = new ArrayList<>();

        for (Map.Entry<String, TableProvider> e : providers.entrySet()) {
            String cat = e.getKey();
            TableProvider p = e.getValue();
            List<Map<String,Object>> list = p.data();

            switch (cat) {
                case "공정":
                    for (Map<String,Object> r : list)
                        addRow(rows, "공정", r.get("proc_id"), "공정이름 : " + nz(r.get("proc_name")));
                    break;
                case "발주":
                    for (Map<String,Object> r : list)
                        addRow(rows, "발주", r.get("order_key"), "발주번호 : " + nz(r.get("order_number")));
                    break;
                case "재고":
                    for (Map<String,Object> r : list)
                        addRow(rows, "재고", r.get("stock_id"), "품목코드 : " + nz(r.get("item_code")));
                    break;
                case "생산":
                    for (Map<String,Object> r : list)
                        addRow(rows, "생산", r.get("cp_id"), "달성률 : " + nz(r.get("success_rate")));
                    break;
                case "BOM":
                    for (Map<String,Object> r : list)
                        addRow(rows, "BOM", r.get("bom_id"), "소요량 : " + nz(r.get("require_amount")));
                    break;
                case "품질":
                    // 품질 Provider 구현 시, 아이디/상세 키에 맞춰 아래를 추가
                    // addRow(rows, "품질", r.get("qc_id"), "품질 : " + nz(r.get("qc_name")));
                    break;
                default:
                    break;
            }
        }

        return new TableResult("전체(요약)", cols, rows);
    }

    private static void addRow(List<Map<String,Object>> rows, String field, Object idVal, String detail) {
        Map<String,Object> row = new LinkedHashMap<>();
        row.put("field",  field);
        row.put("id",     idVal == null ? "" : idVal);
        row.put("detail", detail == null ? "" : detail);
        rows.add(row);
    }

    private static String nz(Object v) { return v == null ? "" : String.valueOf(v); }

    /* =========================
       Provider 구현: 발주(orders)
       ========================= */
    private static class OrderProvider implements TableProvider {
        private final OrderDAO dao = new OrderDAO();

        @Override
        public List<Column> columns() {
            List<Column> cols = new ArrayList<>();
            cols.add(new Column("order_key",   "발주키"));
            cols.add(new Column("order_number","발주번호"));
            cols.add(new Column("order_date",  "발주일"));
            cols.add(new Column("depart_level","부서"));
            cols.add(new Column("worker_name", "담당자"));
//            cols.add(new Column("totalQty",    "품목 수"));
//            cols.add(new Column("totalAmt",    "총금액"));
            return cols;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Map<String, Object>> data() {
            List<Map<String,Object>> rows = new ArrayList<>();
            final List<OrderDTO> list;
            try {
                list = (List<OrderDTO>) dao.selectAll();
            } catch (Exception e) {
                throw new IllegalStateException("발주 목록 조회 중 오류", e);
            }

            for (OrderDTO d : list) {
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("order_key",    nz(d.getOrder_key()));
                row.put("order_number", nz(d.getOrder_number()));
                row.put("order_date",   d.getOrder_date());
                row.put("depart_level", d.getDepart_level());
                row.put("worker_name",  nz(d.getWorker_name()));
//                row.put("totalQty",     nz(d.getTotalQty()));
//                row.put("totalAmt",     nz(d.getTotalAmt()));
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
        private final StockDAO dao = new StockDAO();

        @Override
        public List<Column> columns() {
            List<Column> cols = new ArrayList<>();
            cols.add(new Column("stock_id",     "재고ID"));
            cols.add(new Column("stock_date",   "등록일"));
            cols.add(new Column("stock_loc",    "위치"));
            cols.add(new Column("stock_number", "수량"));
            cols.add(new Column("item_code",    "품목코드"));
            return cols;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Map<String, Object>> data() {
            List<Map<String,Object>> rows = new ArrayList<>();
            final List<StockDTO> list;
            try {
                list = (List<StockDTO>) dao.selectAll();
            } catch (Exception e) {
                throw new IllegalStateException("재고 목록 조회 중 오류", e);
            }

            for (StockDTO d : list) {
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("stock_id",     nz(d.getStock_id()));
                row.put("stock_loc",    d.getStock_loc());
                row.put("stock_date",   d.getStock_date());
                row.put("stock_number", d.getStock_number());
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
            cols.add(new Column("proc_id",    "공정ID"));
            cols.add(new Column("proc_name",  "공정이름"));
            cols.add(new Column("item_code",  "품목코드"));
            cols.add(new Column("dapart_id2", "담당부서ID"));
//            cols.add(new Column("proc_info",  "공정정보"));
            return cols;
        }

        @Override
        public List<Map<String, Object>> data() {
            List<Map<String, Object>> rows = new ArrayList<>();
            List<ProcessDTO> list = dao.getAllProcesses();
            for (ProcessDTO d : list) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("proc_id",    nz(d.getProc_id()));
                row.put("proc_name",  nz(d.getProc_name()));
                row.put("item_code",  nz(d.getItem_code()));
                row.put("dapart_id2", nz(d.getDapart_id2()));
//                row.put("proc_info",  nz(d.getProc_info()));
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
        private final ProPlanDAO dao = new ProPlanDAO();

        @Override
        public List<Column> columns() {
            List<Column> cols = new ArrayList<>();
            cols.add(new Column("cp_id",        "생산계획ID"));
            cols.add(new Column("cp_count",     "계획수량"));
            cols.add(new Column("start_date",   "계획 시작일"));
            cols.add(new Column("end_date",     "계획 종료일"));
//            cols.add(new Column("cp_rate",      "진행률"));
            cols.add(new Column("success_rate", "달성률"));
//            cols.add(new Column("defect_rate",  "불량률"));
//            cols.add(new Column("bigo",         "비고"));
//            cols.add(new Column("item_code",    "품목코드"));
            return cols;
        }

        @Override
        public List<Map<String, Object>> data() {
            List<Map<String,Object>> rows = new ArrayList<>();
            List<?> list = dao.selectAllPP();
            for (Object o : list) {
                ProPlanDTO d = (ProPlanDTO) o;
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("cp_id",        nz(d.getCpID()));
                row.put("cp_count",     d.getCpCount());
                row.put("start_date",   d.getStartDate());
                row.put("end_date",     d.getEndDate());
//                row.put("cp_rate",      d.getCpRate());
                row.put("success_rate", d.getSuccessRate());
//                row.put("defect_rate",  d.getDefectRate());
//                row.put("bigo",         d.getBigo());
//                row.put("item_code",    d.getItemCode());
                rows.add(row);
            }
            return rows;
        }

        private static String nz(String s) { return s == null ? "" : s; }
    }

    /* =========================
       Provider 구현: BOM
       ========================= */
    private static class BOMProvider implements TableProvider {
        private final BOMDAO dao = new BOMDAO();

        @Override
        public List<Column> columns() {
            List<Column> cols = new ArrayList<>();
            cols.add(new Column("bom_id",        "bom_id"));
            cols.add(new Column("item_code_1",   "품목코드-완제"));
            cols.add(new Column("item_code_2",   "품목 코드-재료"));
            cols.add(new Column("require_amount","소요량"));
            return cols;
        }

        @Override
        public List<Map<String, Object>> data() {
            List<Map<String,Object>> rows = new ArrayList<>();
            List<?> list = dao.selectAllBOM();
            for (Object o : list) {
                BOMDTO d = (BOMDTO) o;
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("bom_id",         nz(d.getBomID()));
                row.put("item_code_1",    d.getItem_code_1());
                row.put("item_code_2",    d.getItem_code_2());
                row.put("require_amount", d.getRequire_amount());
                rows.add(row);
            }
            return rows;
        }

        private static String nz(String s) { return s == null ? "" : s; }
    }
}
