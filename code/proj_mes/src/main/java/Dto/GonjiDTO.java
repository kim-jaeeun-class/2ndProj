package Dto;

public class GonjiDTO {
	private String board_id;
	private String board_title;
	private String board_con;
	private String board_date;
	private int board_attach;
	private String worker_id;
	private int board_type;
	public String getBoard_id() {
		return board_id;
	}
	public void setBoard_id(String board_id) {
		this.board_id = board_id;
	}
	public String getBoard_title() {
		return board_title;
	}
	public void setBoard_title(String board_title) {
		this.board_title = board_title;
	}
	public String getBoard_con() {
		return board_con;
	}
	public void setBoard_con(String board_con) {
		this.board_con = board_con;
	}
	public String getBoard_date() {
		return board_date;
	}
	public void setBoard_date(String board_date) {
		this.board_date = board_date;
	}
	public int getBoard_attach() {
		return board_attach;
	}
	public void setBoard_attach(int board_attach) {
		this.board_attach = board_attach;
	}
	public String getWorker_id() {
		return worker_id;
	}
	public void setWorker_id(String worker_id) {
		this.worker_id = worker_id;
	}
	public int getBoard_type() {
		return board_type;
	}
	public void setBoard_type(int board_type) {
		this.board_type = board_type;
	}
	
	
	
	@Override
	public String toString() {
		return "GonjiDTO [board_id=" + board_id + ", board_title=" + board_title + ", board_con=" + board_con
				+ ", board_date=" + board_date + ", board_attach=" + board_attach + ", worker_id=" + worker_id
				+ ", board_type=" + board_type + "]";
	}
	
	
	
	
	
}
