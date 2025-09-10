document.addEventListener('DOMContentLoaded', init);

function init() {
	const updateProcessBtn = document.querySelector('.updateProcessBtn'); // 수정 버튼
	const newProcessBtn = document.querySelector('.newProcessBtn'); 	  // 신규 버튼
	
	// 현재 페이지의 컨텍스트 경로를 가져옴
	const contextPath = location.pathname.substring(0, location.pathname.indexOf("/", 1));
	
	// 수정 버튼을 누르면 <공정 등록/수정/삭제> 페이지로 이동
	updateProcessBtn.addEventListener('click', function(){
		// 선택된 라디오 버튼 가져오기
		const selectedRadio = document.querySelector('input[name="selectedProcId"]:checked');
		
		if (selectedRadio) {
			const procId = selectedRadio.value; // radio 버튼의 value에서 procId를 직접 가져옴
			const row = selectedRadio.closest('tr');
			const departLevel = row.querySelector('.departLevelCell').textContent.trim();

			// 쿼리 파라미터로 데이터를 넘겨주며 페이지 이동
			window.open(contextPath + `/process?mode=update&procId=${procId}&departLevel=${departLevel}`, "_blank");
		} else {
			alert("수정할 항목을 선택해주세요.");
		}
	});

	// 신규 버튼을 누르면 <공정 등록/수정/삭제> 페이지로 이동
	newProcessBtn.addEventListener('click', function(){
		window.open(contextPath + "/process?mode=new", "_blank");
	});
}