document.addEventListener('DOMContentLoaded', init);

function init() {
    const updateProcessBtn = document.querySelector('.updateProcessBtn'); // 수정 버튼
    const newProcessBtn = document.querySelector('.newProcessBtn');     // 신규 버튼
    
    // 현재 페이지의 컨텍스트 경로를 가져옴
    const contextPath = location.pathname.substring(0, location.pathname.indexOf("/", 1));
    
    // '수정' 버튼 클릭 이벤트 리스너
    updateProcessBtn.addEventListener('click', function() {
        // 선택된 라디오 버튼을 찾음
        const selectedRadio = document.querySelector('input[name="selectedProcId"]:checked');
        
        if (selectedRadio) {
            // 선택된 라디오 버튼의 value에서 공정 ID를 가져옴
            const procId = selectedRadio.value;
            
            // `closest('tr')`을 사용하여 라디오 버튼이 속한 행(<tr>)을 찾음
            const row = selectedRadio.closest('tr');
            
            // 해당 행에서 부서명 셀의 텍스트를 가져옴
            const departLevel = row.querySelector('.departLevelCell').textContent.trim();

            // 쿼리 파라미터로 데이터를 넘겨주며 새 탭에서 공정 등록/수정 페이지를 염
            // encodeURIComponent를 사용하여 URL에 한글이나 특수문자가 포함될 경우를 대비
            const url = `${contextPath}/process?mode=update&procId=${procId}&departLevel=${encodeURIComponent(departLevel)}`;
            window.open(url, "_blank");
        } else {
            // 선택된 항목이 없으면 경고 메시지를 표시
            alert("수정할 항목을 선택해주세요.");
        }
    });

    // '신규' 버튼 클릭 이벤트 리스너
    newProcessBtn.addEventListener('click', function() {
        // 새 탭에서 신규 공정 등록 페이지를 염
        const url = `${contextPath}/process?mode=new`;
        window.open(url, "_blank");
    });
}