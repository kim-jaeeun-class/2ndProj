window.onload = function() {
	init();
}

function init() {
	const updateProcessBtn = document.querySelector('.updateProcessBtn'); // 수정 버튼
	const newProcessBtn = document.querySelector('.newProcessBtn'); // 신규 버튼

	// 수정 버튼을 누르면 <공정 등록/수정/삭제> 페이지로 이동
	updateProcessBtn.addEventListener('click', function(){
		window.open("./05_process_CUD.jsp?mode=update", "_blank");
	})

	// 신규 버튼을 누르면 <공정 등록/수정/삭제> 페이지로 이동
	newProcessBtn.addEventListener('click', function(){
		window.open("./05_process_CUD.jsp?mode=new", "_blank");
	})
}