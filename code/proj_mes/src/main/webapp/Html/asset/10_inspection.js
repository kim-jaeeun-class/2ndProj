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


    /* 모달 */
    const modal = document.getElementById("procModal");
    const searchProc = document.getElementById("searchProc");
    const closeBtn = document.querySelector(".close");
    const dateBtn = document.querySelector('.dateBtn');
    const modalInfo = document.querySelector('.modalInfo');
    const btmBtn = document.querySelector('.btmBtn');
    const cancelBtn = document.querySelector('.cancleBtn'); // 선택 해제 버튼
    const saveBtn2 = document.querySelector('.saveBtn2'); // 모달 내 저장 버튼

    // 클릭하면 모달 열기
    searchProc.onclick = function(e) {
        e.preventDefault();
        modal.style.display = "block";
    }

    // 닫기 버튼 클릭 시 닫기
    closeBtn.onclick = function() {
    modal.style.display = "none";
    }

    // 모달 내 상세 내용을 '조회' 버튼을 눌러야지만 보이게
    dateBtn.addEventListener('click', function() {
        const dateInput = document.querySelector('.dateLookup input[type="date"]');
        if (!dateInput.value) {
            alert("날짜를 선택해주세요.");
            return;
        }

        // 테이블과 버튼 보이게
        modalInfo.style.display = 'table'; // 테이블 형식 유지
        btmBtn.style.display = 'flex';     // 버튼은 flex로 정렬
    });

    // 선택 해제
    cancelBtn.addEventListener('click', function() {
        const checkboxes = modalInfo.querySelectorAll('input[type="checkbox"]');
        checkboxes.forEach(cb => cb.checked = false);
    });

    // 모달 내 '저장' 버튼 클릭 시 선택된 행 가져오기 및 테이블 insp_tab에 추가
    saveBtn2.addEventListener('click', function() {
        // 모달에서 체크된 행만 선택
        const selectedRows = modalInfo.querySelectorAll('tbody input[type="checkbox"]:checked');
        
        selectedRows.forEach(cb => {
            const row = cb.closest('tr');
            const procName = row.children[1].textContent; // 공정명
            const lotNum = row.children[2].textContent;   // LOT 번호
            const productName = row.children[3].textContent;   // 품명

            // insp_tab에 새로운 <tr> 생성
            const newRow = document.createElement('tr');
            newRow.innerHTML = `
                <td><input type="checkbox"></td>
                <td>
                    <select name="inspMethod">
                        <option value="전수" checked="checked">전수</option>
                        <option value="샘플">샘플</option>
                        <option value="재검">재검</option>
                    </select>
                </td>
                <td>${lotNum}</td>
                <td>${productName}</td>
                <td>${procName}</td>
                <td><input type="number" min="0" max="120"></td>
                <td><input type="number" min="0" max="120"></td>
                <td>
                    <select name="defective">
                        <option value="불량명1">불량명1</option>
                        <option value="불량명2">불량명2</option>
                        <option value="불량명3">불량명3</option>
                    </select>
                </td>
                <td><input type="text"></td>
            `;
            inspTabBody.appendChild(newRow);
        });

        // 모달 닫기 및 체크박스 초기화
        modal.style.display = 'none';
        modalInfo.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);
        btmBtn.style.display = 'none';
        modalInfo.style.display = 'none';
    });



    /* 불량 입력이 없거나 작업자 입력안하면 저장 안 되게 */
    saveBtn.addEventListener('click', function() {
        let messages = [];

        // 1️⃣ 작업자 입력 체크
        if (!workerInput.value.trim()) {
            messages.push("작업자를 입력하세요.");
        }

        // 2️⃣ 테이블 실제 데이터 있는지 확인 (첫 번째 행 제외, 체크박스와 비고 제외)
        const dataRows = Array.from(inspTabBody.rows).slice(1).filter(row => {
            const lotNum = row.cells[2].textContent.trim();
            const productName = row.cells[3].textContent.trim();
            const procName = row.cells[4].textContent.trim();
            // LOT, 품명, 공정 중 하나라도 값이 있으면 데이터 존재
            return lotNum !== "" || productName !== "" || procName !== "";
        });

        if (dataRows.length === 0) {
            messages.push("불량 사항을 입력하세요.");
        } else {
            // 3️⃣ 양품 수량 체크
            dataRows.forEach((row, index) => {
                const goodQty = row.cells[5].querySelector('input')?.value;

                if (goodQty === "" || Number(goodQty) < 0) {
                    messages.push(`행 ${index + 2}: 양품 수량을 0 이상 입력하세요.`);
                }
            });
        }


        // 조건 중 하나라도 만족하지 않으면 alert
        if (messages.length > 0) {
            alert(messages.join("\n"));
            return; // 저장 중단
        }

        // 모든 조건 통과 시 저장
        alert("저장이 완료되었습니다");
    });
}
