package Dto;

public class StandardDTO {
	public class processDto{
	// 공정 - process
	private String proc_id; //공정 ID
	private String proc_name; //공정 이름
	private int process_check;//검사여부
	private String depart_id2;//부서 아이디
	private String proc_info;
	private int proc_seq;
	private String item_code;
	}
	
	
	
	//BOM
	public class BomDto {
	    /** BOM ID (bom_id) */
	    private String bomId;

	    /** 사용용도 (field) */
	    private String field;

	    /** 단가 (price) — 스키마상 varchar2 */
	    private String price;

	    /** 단위 (unit) — NUMBER */
	    private int unit;

	    /** 소요량 (require_amount) — NUMBER */
	    private int requireAmount;

	    /** 품목코드 (item_code) */
	    private String itemCode;
	
}
	



}
