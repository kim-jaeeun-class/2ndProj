window.addEventListener('load', init);

function init() {
  bindorder_reg();
  bindOrderReg();

}

function bindorder_reg() {

    // 공통 함수: 모달 오픈, 검색, 클로즈 
    function modalHandler(openBtn, modal, onConfirm) {
        const searchInput = modal.querySelector('.m_search');
        const list       = modal.querySelector('.list');
        const Confirm  = modal.querySelector('.confirm');
        const Cancel   = modal.querySelector('.cancel');

        // 열기
        openBtn.addEventListener('click', () => {
            modal.classList.remove('hide');
            if (searchInput) {
                searchInput.value = '';
                filterTableRows(list, '');
            }
        });

        // 검색
        if (searchInput) {
            searchInput.addEventListener('input', () => {
                filterTableRows(list, searchInput.value);
            });
        }

        // 행 클릭 라디오 체크
        list.addEventListener('click', (e) => {

            const tr = e.target.closest('tr');
            if (!tr || tr.style.display === 'none') return;

            const r = tr.querySelector('input[type="radio"]');
            if (r) r.checked = true;
        });

        // 확인
        Confirm.addEventListener('click', () => {
            const checked = list.querySelector('input[type="radio"]:checked');
            if (!checked) { alert('항목을 선택하세요.'); return; }

            onConfirm(checked.closest('tr'));
            modal.classList.add('hide');
        });

        // 취소
        Cancel.addEventListener('click', () => modal.classList.add('hide'));
 

    }

    // 부서 모달
    const deptInput = document.querySelector('#dept_input');
    const nameInput = document.querySelector('#name_input');

    modalHandler(deptInput, document.querySelector('#dept_modal'), function (tr) {

        // [0:라디오][1:부서번호][2:부서명][3:사원번호][4:이름]
        const deptName    = tr.children[2].textContent.trim();
        const managerName = tr.children[4].textContent.trim();

        deptInput.value = deptName;
        nameInput.value = managerName;

        deptInput.readOnly = true;
        nameInput.readOnly = true;
    });

    // 거래처 모달
    const clientInput   = document.querySelector('#client_input');
    const businessInput = document.querySelector('#business_input');
    const numberInput   = document.querySelector('#number_input');

    modalHandler(clientInput, document.querySelector('#client_modal'), function (tr) {

        // [0:라디오][1:거래처코드][2:거래처명][3:사업자번호][4:연락처][5:담당자]
        const clientName     = tr.children[2].textContent.trim();
        const businessNumber = tr.children[3].textContent.trim();
        const contactNumber  = tr.children[4].textContent.trim();

        clientInput.value   = clientName;
        businessInput.value = businessNumber;
        numberInput.value   = contactNumber;

        clientInput.readOnly   = true;
        businessInput.readOnly = true;
        numberInput.readOnly   = true;
    });
}

// 테이블에서 검색해서 가져오기
function filterTableRows(tableList, query) {
    const q = (query || '').toLowerCase();
    const rows = tableList.querySelectorAll('tr');
    let any = false;

    // 안내행 제거
    const oldEmpty = tableList.querySelector('.empty-row');
    if (oldEmpty) oldEmpty.remove();

    rows.forEach((tr) => {
        const text = tr.textContent.toLowerCase();
        const match = q === '' || text.includes(q);

        tr.style.display = match ? '' : 'none';

        if (!match) {
            const r = tr.querySelector('input[type="radio"]');
            if (r) r.checked = false;
        }

        if (match) any = true;
    });

    if (!any) {
        const table = tableList.closest('table');
        const thCount = table ? table.querySelectorAll('thead th').length : 1;
        const trEmpty = document.createElement('tr');
        trEmpty.className = 'empty-row';

        const td = document.createElement('td');
        td.colSpan = thCount;
        td.style.textAlign = 'center';
        td.textContent = '검색 결과가 없습니다.';

        trEmpty.appendChild(td);
        tableList.appendChild(trEmpty);
    }
}

function bindOrderReg() {
    const stateInput = document.querySelector('#order_state'); 
    const saveBtn = document.querySelector('#save');
    const submitBtn = document.querySelector('#approve');
    const cancelBtn = document.querySelector('#recall');

    // 페이지 로드 시 초기 상태값 반영
    updateActionButtons();

    // 임시저장
    saveBtn.addEventListener('click', function() {
        if (!validateInputs()) return; // 빈 값 체크

        setOrderStatus(0);
        alert('임시저장되었습니다.');
    });

    // 승인요청
    submitBtn.addEventListener('click', function() {
        if (!validateInputs()) return; // 빈 값 체크
        
        setOrderStatus(1);
        alert('승인요청 되었습니다.');
    });

    // 회수
    cancelBtn.addEventListener('click', function() {
        setOrderStatus(0); 
        alert('회수되었습니다.');
    });

    // 상태값 업데이트
    function setOrderStatus(newStatus) {
        stateInput.value = newStatus;
        updateActionButtons();
    }

    // 버튼 상태 갱신
    function updateActionButtons() {
        const status = parseInt(stateInput.value || "0", 10); // NaN 방지

        if (status === 0) {
            // 임시저장
            saveBtn.classList.remove('hide');
            submitBtn.classList.remove('hide');
            cancelBtn.classList.add('hide');
        } else if (status === 1 || status === 3) {
            // 승인대기 or 반려
            saveBtn.classList.add('hide');
            submitBtn.classList.add('hide');
            cancelBtn.classList.remove('hide');
        } else if (status === 2) {
            // 승인 완료
            saveBtn.classList.add('hide');
            submitBtn.classList.add('hide');
            cancelBtn.classList.add('hide');
        }
    }

    // 빈칸 방지
    function validateInputs() {
        // 모든 input 중 비고(#note_input) 제외
        const inputs = document.querySelectorAll('.main_input');

        for (let i = 0; i < inputs.length; i++) {
            if (inputs[i].value.trim() === '') {
                alert(`${inputs[i].previousElementSibling.innerText} 항목을 입력하세요.`);
                inputs[i].focus();
                return false;
            }
        }
        return true; // 모든 값이 채워져 있으면 true
    }
}
