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
}
