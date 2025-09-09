document.addEventListener('DOMContentLoaded', init);

function init() {
    const addItemBtn = document.querySelector('.addItemBtn'); // 항목 추가 버튼
    const delItemBtn = document.querySelector('.delItemBtn'); // 항목 삭제 버튼
    const deleteBtn = document.querySelector('.deleteBtn'); // 삭제 버튼
    const updateBtn = document.querySelector('.updateBtn'); // 수정 버튼
    const createBtn = document.querySelector('.createBtn'); // 등록

    /* 06_bom_list.html에서 신규로 넘어왔나, 수정으로 넘어왔나 */
    const params = new URLSearchParams(window.location.search);
    const mode = params.get("mode");

    // 06_bom_list.html에서 신규로 넘어왔거나 직접 들어온 경우 '수정' 버튼 안보이기
    if (mode === "new" || !mode) {
        updateBtn.style.display = "none"; 
    // 06_bom_list.html에서 수정으로 넘어왔으면 '등록' 버튼 안보이기
    } else if (mode === "update") {
        createBtn.style.display = "none"; // 버튼 숨기기
    }
    
    /* 페이지를 벗어나면 수정되는 내용이 날라가니 한번 더 묻기 */
    window.addEventListener("beforeunload", function (e) {
        e.preventDefault();
        e.returnValue = ""; 
    });

    /* 항목 추가 버튼 누르면 행이 추가됨 */
    const bomTableBody = document.querySelector('.bom_tab tbody');

    addItemBtn.addEventListener('click', function() {
        // 현재 행 개수
        const rowCount = bomTableBody.rows.length;

        // 새 tr 생성
        const newRow = document.createElement('tr');

        // tr 안에 td 채우기
        newRow.innerHTML = `
            <td><input type="checkbox"></td>
            <td>${rowCount + 1}</td>
            <td>원재료명</td>
            <td>용도</td>
            <td>자재 코드</td>
            <td>단가</td>
            <td>단위</td>
            <td>EA</td>
            <td>입고처</td>
        `;

        // tbody에 추가
        bomTableBody.appendChild(newRow);
    });

    /* 항목 삭제 */
    delItemBtn.addEventListener('click', function() {
        const rows = Array.from(bomTableBody.rows);
        let deleted = false;

        rows.forEach(row => {
            const checkbox = row.querySelector('input[type="checkbox"]');
            if (checkbox && checkbox.checked) {
                bomTableBody.removeChild(row);
                deleted = true;
            }
        });

        if (!deleted) {
            alert("삭제할 항목을 선택해주세요.");
        } else {
            // 삭제 후 No. 열 번호 다시 재정렬
            Array.from(bomTableBody.rows).forEach((row, index) => {
                row.cells[1].textContent = index + 1;
            });
        }
    });


}