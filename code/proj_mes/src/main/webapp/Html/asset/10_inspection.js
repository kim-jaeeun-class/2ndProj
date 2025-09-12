document.addEventListener('DOMContentLoaded', init);

function init() {

    const inspTabBody = document.querySelector('.insp_tab tbody');
    const saveBtn = document.querySelector('.saveBtn');
    const workerInput = document.getElementById('workerInput');

    /* 테이블 항목 전체 선택 */
    const allCheck = document.getElementById("allCheck");
    const checkboxes = document.querySelectorAll('.insp_tab tbody input[type="checkbox"]');

    // 전체 선택 / 해제
    allCheck.addEventListener('change', function(){
        checkboxes.forEach(cb => cb.checked = this.checked);
    })

    // 개별 체크박스 클릭 시 전체 체크 상태 반영
    checkboxes.forEach(cb => {
        cb.addEventListener('change', function() {
        // 모든 체크박스가 체크되어 있으면 allCheck 체크, 아니면 해제
        allCheck.checked = Array.from(checkboxes).every(cb => cb.checked);
        });
    });

    /* 삭제 버튼 클릭 시 체크된 행 삭제 */
    const deleteBtn = document.querySelector('.deleteBtn');

    deleteBtn.addEventListener('click', function() {
        const inspTabBody = document.querySelector('.insp_tab tbody');
        const checkedRows = inspTabBody.querySelectorAll('input[type="checkbox"]:checked');

        checkedRows.forEach(cb => {
            const row = cb.closest('tr');
            // 첫 번째 행(ex) 제외하고 삭제
            if (!row.querySelector('td').textContent.includes('ex)')) {
                row.remove();
            }
        });

        // 전체 선택 체크박스 초기화
        allCheck.checked = false;
    });
}
