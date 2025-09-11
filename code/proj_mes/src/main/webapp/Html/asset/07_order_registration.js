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
    const deptHidden = document.querySelector('#dept_id');
    const workerHidden = document.querySelector('#worker_id');


    modalHandler(deptInput, document.querySelector('#dept_modal'), function (tr) {

        // [0:라디오][1:부서번호][2:부서명][3:사원번호][4:이름]
        const deptId      = tr.children[1].textContent.trim();
        const deptName    = tr.children[2].textContent.trim();
        const workerId    = tr.children[3].textContent.trim();
        const managerName = tr.children[4].textContent.trim();

        deptInput.value = deptName;
        nameInput.value = managerName;

        deptHidden.value = deptId;
        workerHidden.value = workerId;
        
        deptInput.readOnly = true;
        nameInput.readOnly = true;
    });

    // 거래처 모달
    const clientInput   = document.querySelector('#client_input');
    const businessInput = document.querySelector('#business_input');
    const numberInput   = document.querySelector('#number_input');
    const clientHidden  = document.querySelector('#client_id');
    
    modalHandler(clientInput, document.querySelector('#client_modal'), function (tr) {

        // [0:라디오][1:거래처코드][2:거래처명][3:사업자번호][4:연락처][5:담당자]
        const clientId        = tr.children[1].textContent.trim(); 
        const clientName     = tr.children[2].textContent.trim();
        const businessNumber = tr.children[3].textContent.trim();
        const contactNumber  = tr.children[4].textContent.trim();

        clientInput.value   = clientName;
        businessInput.value = businessNumber;
        numberInput.value   = contactNumber;

        clientHidden.value = clientId;

        clientInput.readOnly   = true;
        businessInput.readOnly = true;
        numberInput.readOnly   = true;
    });

    // 품목 추가 모달: '추가' 버튼 → item_modal → 선택행 추가
    const addBtn = document.querySelector('.item_add');
    const itemModal = document.querySelector('#item_modal');

    modalHandler(addBtn, itemModal, function (tr) {
        // [0:라디오][1:품목코드][2:품목명][3:유형][4:단가]
        const itemCode   = tr.children[1].textContent.trim();
        const itemName   = tr.children[2].textContent.trim();
        const unitPrice  = toInt(tr.children[4].textContent.trim()); // "1,000" → 1000
        const qtyInput  = itemModal.querySelector('.m_quantity');

        let   qty       = toInt(qtyInput?.value || '1');
        if (!qty || qty <= 0) qty = 1;

        addOrMergeRow({ code: itemCode, name: itemName, qty, unitPrice });     
        if (qtyInput) qtyInput.value = '1';                           // 다음 추가 대비 초기화
    });
}

// 같은 코드 행 찾기
function getRowByCode(code){
  const rows = document.querySelectorAll('.order_list tr');
  for (const tr of rows) {
    if (tr.dataset.code === code) return tr;
  }
  return null;
}
// (보조) 코드가 없을 때 이름으로 찾기
function getRowByName(name){
  const rows = document.querySelectorAll('.order_list tr');
  for (const tr of rows) {
    const nameCell = tr.querySelector('.col-name') || tr.children[2];
    if (nameCell && nameCell.textContent.trim() === name) return tr;
  }
  return null;
}

// 동일 품목이면 수량만 +, 아니면 새 행 추가
function addOrMergeRow({ code, name, qty, unitPrice }) {
  let tr = code ? getRowByCode(code) : null;
  if (!tr) tr = getRowByName(name); // 초기 데이터에 data-code 없을 수 있어 fallback

  if (tr) {
    // 기존 행 업데이트
    const qtyCell  = tr.querySelector('.col-qty')  || tr.children[3];
    const priceCell= tr.querySelector('.col-price')|| tr.children[4];
    const sumCell  = tr.querySelector('.col-sum')  || tr.children[5];

    const currentQty = toInt(qtyCell.textContent);
    const unit       = toInt(priceCell.textContent); // 가격이 다르면 기존가로 유지 (업무정책에 맞게 조정 가능)
    const newQty     = currentQty + qty;

    qtyCell.textContent  = comma(newQty);
    sumCell.textContent  = comma(newQty * unit);

    // 체크박스 상태는 그대로, NO 재정렬 필요 없음
    recalcFooter();
    return;
  }

  // 새 행 추가
  addRowToOrderStatic({ code, name, qty, unitPrice });
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
    // ★ form id가 없으므로 action으로 안전하게 찾기
    const form = document.querySelector('form[action$="/orderAdd"]'); 
    if (!form) return; // 폼이 없으면 더 진행하지 않음

    const stateInput = document.querySelector('#order_state'); 
    const saveBtn    = document.querySelector('#save');
    const approveBtn = document.querySelector('#approve');
    const cancelBtn  = document.querySelector('#recall');

    // 페이지 로드 시 초기 상태값 반영
    updateActionButtons();

    // 임시저장
    saveBtn.addEventListener('click', function() {
        if (!validateInputs()) return; // 빈 값 체크
        setOrderStatus(0);
        form.submit();
        alert('임시저장되었습니다.');
    });

    // 승인요청
    approveBtn.addEventListener('click', function() {
        if (!validateInputs()) return; // 빈 값 체크
        setOrderStatus(1);
        form.submit();
        alert('승인요청 되었습니다.');
    });

    // 회수
    cancelBtn.addEventListener('click', function() {
        setOrderStatus(0); 
        form.submit();
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
            saveBtn.classList.remove('hide');
            approveBtn.classList.remove('hide');
            cancelBtn.classList.add('hide');
        } else if (status === 2 || status === 3) {
            saveBtn.classList.add('hide');
            approveBtn.classList.add('hide');
            cancelBtn.classList.remove('hide');
        } else if (status === 1) {
            saveBtn.classList.add('hide');
            approveBtn.classList.add('hide');
            cancelBtn.classList.add('hide');
        }
    }

    // 빈칸 방지 (★ readonly는 모달로 채워지므로 검사에서 제외)
    function validateInputs() {
        const inputs = document.querySelectorAll('.main_input');

        for (let i = 0; i < inputs.length; i++) {
            const el = inputs[i];

            // ★ 모달 선택으로만 채우는 칸은 readonly → 비어 있어도 여기서 막지 않음
            if (el.readOnly) continue;

            if (el.value.trim() === '') {
                alert(`${el.previousElementSibling?.innerText || '필수'} 항목을 입력하세요.`);
                el.focus();
                return false;
            }
        }

        // ★ 최종 방어: client_id 숨김필드가 비면 Oracle에서 NULL 취급됨 → 전송 금지
        const clientHidden = document.querySelector('#client_id');
        if (!clientHidden || !String(clientHidden.value || '').trim()) {
            alert('거래처를 선택해 주세요.');
            return false;
        }

        return true;
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
function addRowToOrderStatic({ code, name, qty, unitPrice }) {
    const tbody = document.querySelector('.order_list');
    const sum   = qty * unitPrice;
    const tr = document.createElement('tr');
    if (code) tr.dataset.code = code;           // 코드 저장(중복 합치기용)

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

  // 1) 행 개수(품목 수)와 총금액 계산
  const rows = document.querySelectorAll('.order_list tr');
  const itemCount = rows.length;   // 수량 칸엔 '행 개수'만
  let totalAmt = 0;

  rows.forEach(tr => {
    const qty   = toInt(tr.querySelector('.col-qty')  ?.textContent || '0');
    const price = toInt(tr.querySelector('.col-price')?.textContent || '0');
    totalAmt += qty * price;
  });

  // 2) 상세 테이블의 tfoot 갱신 (colspan 대응)
  const orderTbody  = document.querySelector('.order_list');
  const detailTable = orderTbody?.closest('table');
  const footRow     = detailTable?.querySelector('tfoot tr');
  if (!footRow) return;

  const cells = footRow.cells;
  const last  = cells.length - 1;  // 마지막 셀 = 총 금액
  const prev  = last - 1;          // 그 앞 셀 = 품목 개수(수량 칸)
  if (prev < 0) return;

  cells[prev].textContent = comma(itemCount); // 품목 개수
  cells[last].textContent = comma(totalAmt);  // 총 금액
}



