package Dto;

public class BOMDTO {
	private String bomID;
	private String item_code_1;
	private String item_code_2;
	private int require_amount;
	public String getBomID() {
		return bomID;
	}
	public void setBomID(String bomID) {
		this.bomID = bomID;
	}
	public String getItem_code_1() {
		return item_code_1;
	}
	public void setItem_code_1(String item_code_1) {
		this.item_code_1 = item_code_1;
	}
	public String getItem_code_2() {
		return item_code_2;
	}
	public void setItem_code_2(String item_code_2) {
		this.item_code_2 = item_code_2;
	}
	public int getRequire_amount() {
		return require_amount;
	}
	public void setRequire_amount(int require_amount) {
		this.require_amount = require_amount;
	}
	@Override
	public String toString() {
		return "BOMDTO [bomID=" + bomID + ", item_code_1=" + item_code_1 + ", item_code_2=" + item_code_2
				+ ", require_amount=" + require_amount + "]";
	}
	
	
}
