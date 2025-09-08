window.addEventListener('load', init);

function init() {
    bindorder_reg();
    bindOrderReg();
    bindCheckboxDelete();
}

function bindorder_reg() {

    // 공통 함수: 모달 오픈, 검색, 클로즈 
    function modalHandler(openBtn, modal, onConfirm) {
        const searchInput = modal.querySelector('.m_search');
        const list       = modal.querySelector('.list');
        const Confirm  = modal.querySelector('.confirm');
        const Cancel   = modal.querySelector('.cancel');
        const qtyInput    = modal.querySelector('.m_quantity');

        // 열기
        openBtn.addEventListener('click', () => {
            modal.classList.remove('hide');
            if (searchInput) {
                searchInput.value = '';
                filterTableRows(list, '');
            }
            if (qtyInput) qtyInput.value = '1'; // 수량 기본값=1
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

    // 품목 추가 모달: '추가' 버튼 → item_modal → 선택행 추가
    const addBtn = document.querySelector('.item_add');
    const itemModal = document.querySelector('#item_modal');

    modalHandler(addBtn, itemModal, function (tr) {
        // [0:라디오][1:품목코드][2:품목명][3:유형][4:단가]
        const itemName   = tr.children[2].textContent.trim();
        const unitPrice  = toInt(tr.children[4].textContent.trim()); // "1,000" → 1000
        const qtyInput  = itemModal.querySelector('.m_quantity');

        let   qty       = toInt(qtyInput?.value || '1');
        if (!qty || qty <= 0) qty = 1;

        addRowToOrderStatic({ name: itemName, qty, unitPrice });      // 텍스트로 추가
            if (qtyInput) qtyInput.value = '1';                           // 다음 추가 대비 초기화
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

// 발주 상세(품목) 체크박스 전체선택 + 삭제 기능
function bindCheckboxDelete() {
    const checkAll  = document.getElementById("checkAll");
    const tableBody = document.querySelector(".order_list");           
    const deleteBtn = document.querySelector(".list_action button[type='button']");

    // 전체선택
    checkAll.addEventListener("change", function () {
        const checkBoxes = tableBody.querySelectorAll(".row_check");
        checkBoxes.forEach(cb => cb.checked = checkAll.checked);
    });

    // 개별 체크박스 변화 시 전체선택 동기화
    tableBody.addEventListener("change", function (e) {
        if (e.target.classList.contains("row_check")) {
            const checkBoxes   = tableBody.querySelectorAll(".row_check");
            const checkedBoxes = tableBody.querySelectorAll(".row_check:checked");
            checkAll.checked = checkBoxes.length === checkedBoxes.length;
        }
    });

    // 삭제
    deleteBtn.addEventListener("click", function () {
        const checkedRows = tableBody.querySelectorAll(".row_check:checked");
        if (checkedRows.length === 0) {
            alert("삭제할 항목을 선택하세요.");
            return;
        }

        const ok = confirm(`선택한 ${checkedRows.length}개의 항목을 정말 삭제하시겠습니까?`);
        if (!ok) return;

        checkedRows.forEach(cb => cb.closest("tr").remove());

        checkAll.checked = false;
        renumberRows();  // 번호 다시
        recalcFooter();  // 합계 다시
    });
}

// 숫자 유틸
function toInt(v) {
    return parseInt((v + '').replace(/[^\d]/g, ''), 10) || 0;
}
function comma(n) {
    return (n ?? 0).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// 행 추가
function addRowToOrderStatic({ name, qty, unitPrice }) {
    const tbody = document.querySelector('.order_list');
    const sum   = qty * unitPrice;
    const tr = document.createElement('tr');
    tr.innerHTML = `
        <td><input type="checkbox" class="row_check"></td>
        <td class="col-no"></td>
        <td class="col-name">${name}</td>
        <td class="col-price">${comma(unitPrice)}</td>
        <td class="col-qty">${comma(qty)}</td>
        <td class="col-sum">${comma(sum)}</td>
    `;
    tbody.appendChild(tr);
    renumberRows();
    recalcFooter();
}

// 번호 다시 매기기
function renumberRows(){
  document.querySelectorAll('.order_list tr').forEach((tr,i)=>{
    const noCell = tr.querySelector('.col-no') || tr.children[1];
    if (noCell){
      noCell.textContent = i + 1;
      noCell.classList.add('col-no');
    }
  });
}


// 푸터(총계) 재계산
function recalcFooter(){
  // 1) 합계 계산
  const rows = document.querySelectorAll('.order_list tr');
  let totalQty = 0, totalAmt = 0;

  rows.forEach(tr => {
    const qtyText   = (tr.querySelector('.col-qty')  ?.textContent) || tr.children[3]?.textContent || '0';
    const priceText = (tr.querySelector('.col-price')?.textContent) || tr.children[4]?.textContent || '0';
    const qty   = toInt(qtyText);
    const price = toInt(priceText);
    totalQty += qty;
    totalAmt += qty * price;
  });

  // 2) 상세 테이블의 tfoot만 갱신 (colspan 대응)
  const orderTbody = document.querySelector('.order_list');
  const detailTable = orderTbody?.closest('table');
  const footRow = detailTable?.querySelector('tfoot tr');
  if (!footRow) return;

  const cells = footRow.cells;
  const last  = cells.length - 1;      // 마지막 칸(총 금액)
  const prev  = last - 1;              // 그 앞 칸(총 수량)
  if (prev < 0) return;                // 방어

  cells[prev].textContent = comma(totalQty); // 총 수량
  cells[last].textContent = comma(totalAmt); // 총 금액
}



