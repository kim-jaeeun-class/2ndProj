package Service;

import java.util.List;

import Dao.BOMDAO;
import Dto.BOMDTO;

public class BOMService {
	private BOMDAO dao = new BOMDAO();
	
	// 전체 조회
	public List<BOMDTO> getAllBOM() {
		return dao.selectAllBOM();
	}
	
	// 필터링 조회
	public List<BOMDTO> getFilBOM(BOMDTO dto) {
		return dao.selectFilBOM(dto);
	}
	// 등록
	public int addBOM(BOMDTO dto) {
	    // 오늘 날짜 기준 prefix 생성
	    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyMMdd");
	    String today = sdf.format(new java.util.Date());
	    String prefix = "B" + today; // 예: B250912

	    // DAO에서 마지막 ID 조회
	    String lastID = dao.selectLastBOMID(prefix);
	    String newID;
	    if(lastID == null) {
	        newID = prefix + "01";
	    } else {
	        // 마지막 2자리 숫자를 +1
	        int num = Integer.parseInt(lastID.substring(lastID.length()-2));
	        num++;
	        newID = prefix + String.format("%02d", num);
	    }

	    dto.setBomID(newID);  // DTO에 자동 세팅
	    return dao.insertBOM(dto);
	}

	
	// 삭제
	public int removeBOM(BOMDTO dto) {
		return dao.deleteBOM(dto);
	}
	
	// 수정
	public int editBOM(BOMDTO dto) {
		return dao.updateBOM(dto);
	}
	
	// 수정용
	public BOMDTO getBOMByID(String bomID) {
	    return dao.selectBOMByID(bomID);
	}

}
