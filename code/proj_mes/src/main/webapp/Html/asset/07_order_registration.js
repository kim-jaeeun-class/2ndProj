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
  if (!form) return;

  const stateInput = document.querySelector('#order_state');
  const saveBtn    = document.querySelector('#save');
  const approveBtn = document.querySelector('#approve');
  const cancelBtn  = document.querySelector('#recall');

  // --- 납기일: 오늘 이전 선택 불가(+ 기존 값 보정) + 즉시 경고 ---
  const dueInput = document.querySelector('input[name="order_pay"]');
  let todayStr = '';
  if (dueInput && !dueInput.readOnly) {
    const now = new Date();
    const yyyy = String(now.getFullYear());
    const mm   = String(now.getMonth() + 1).padStart(2, '0');
    const dd   = String(now.getDate()).padStart(2, '0');
    todayStr   = yyyy + '-' + mm + '-' + dd;

    // 날짜 피커에서 과거 선택 자체를 막음
    dueInput.min = todayStr;

    // 기존 값이 과거면 즉시 오늘로 보정
    if (dueInput.value && dueInput.value < todayStr) {
      dueInput.value = todayStr;
    }

    // 사용자가 과거일로 변경/입력하면 즉시 경고 + 보정
    function guardDue() {
      const v = String(dueInput.value || '').trim();
      if (!v) return; // 미입력은 제출 시에 별도 검증
      if (v < todayStr) {
        alert('납기일은 오늘(' + todayStr + ') 이후 날짜만 선택할 수 있습니다.');
        dueInput.value = todayStr;
        dueInput.focus();
      }
    }
    dueInput.addEventListener('change', guardDue);
    dueInput.addEventListener('input',  guardDue);
    dueInput.addEventListener('blur',   guardDue);
  }

  // --- 공통 제출 함수: requestSubmit 사용(검증/submit 이벤트 타도록) ---
  function safeRequestSubmit() {
    if (typeof form.requestSubmit === 'function') {
      form.requestSubmit();
    } else {
      // 폴백: 가짜 submit 버튼 클릭으로 submit 이벤트/검증 유도
      const tmp = document.createElement('button');
      tmp.type = 'submit';
      tmp.style.display = 'none';
      form.appendChild(tmp);
      tmp.click();
      form.removeChild(tmp);
    }
  }

  // --- 제출 전 최종 검증(납기일 필수 + 과거 금지 포함) ---
  function validateInputs() {
    // ① 납기일 필수 + 과거 금지
    const due = document.querySelector('input[name="order_pay"]');
    if (due && !due.readOnly) {
      const v = String(due.value || '').trim();
      if (!v) {
        alert('납기일을 입력하세요.');
        due.focus();
        return false;
      }
      // todayStr이 비어 있을 수 있어 다시 계산(안전)
      if (!todayStr) {
        const now = new Date();
        const yyyy = String(now.getFullYear());
        const mm   = String(now.getMonth() + 1).padStart(2, '0');
        const dd   = String(now.getDate()).padStart(2, '0');
        todayStr   = yyyy + '-' + mm + '-' + dd;
      }
      if (v < todayStr) {
        alert('납기일은 오늘(' + todayStr + ') 이후 날짜만 선택할 수 있습니다.');
        due.focus();
        return false;
      }
    }

    // ② (기존) 다른 입력 공란 검사 — readonly는 패스
    const inputs = document.querySelectorAll('.main_input');
    for (let i = 0; i < inputs.length; i++) {
      const el = inputs[i];
      if (el.readOnly) continue;
      if (String(el.value || '').trim() === '') {
        const label = el.previousElementSibling ? el.previousElementSibling.innerText : '필수';
        alert(label + ' 항목을 입력하세요.');
        el.focus();
        return false;
      }
    }

    // ③ (기존) 거래처 선택 여부 방어
    const clientHidden = document.querySelector('#client_id');
    if (!clientHidden || !String(clientHidden.value || '').trim()) {
      alert('거래처를 선택해 주세요.');
      return false;
    }

    return true;
  }

  // --- 폼의 submit 이벤트에도 방어(엔터 제출/다른 submit 버튼 대비) ---
  form.addEventListener('submit', function (e) {
    if (!validateInputs()) {
      e.preventDefault();
      return;
    }
  });

  // 페이지 로드 시 버튼 노출 상태 반영
  updateActionButtons();

  // 임시저장
  if (saveBtn) {
    saveBtn.addEventListener('click', function () {
      if (!validateInputs()) return;
      setOrderStatus(0);
      safeRequestSubmit(); // form.submit()는 쓰지 않음
    });
  }

  // 승인요청
  if (approveBtn) {
    approveBtn.addEventListener('click', function () {
      if (!validateInputs()) return;
      setOrderStatus(1);
      safeRequestSubmit();
    });
  }

  // 회수
  if (cancelBtn) {
    cancelBtn.addEventListener('click', function () {
      setOrderStatus(0);
      safeRequestSubmit();
    });
  }

  // 상태값 업데이트
  function setOrderStatus(newStatus) {
    if (stateInput) stateInput.value = newStatus;
    updateActionButtons();
  }

  // 버튼 상태 갱신
  function updateActionButtons() {
    const status = parseInt((stateInput && stateInput.value) || '0', 10);
    if (!saveBtn || !approveBtn || !cancelBtn) return;

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
    // === 납기일: 오늘 이전 강제 차단 ===
    (function lockDueDate() {
    const due = document.querySelector('input[name="order_pay"]');
    if (!due || due.readOnly) return;

    const todayStr = () => {
        const now = new Date();
        const yyyy = String(now.getFullYear());
        const mm   = String(now.getMonth() + 1).padStart(2, '0');
        const dd   = String(now.getDate()).padStart(2, '0');
        return `${yyyy}-${mm}-${dd}`;
    };

    const applyMin = () => {
        const t = todayStr();
        due.min = t;                             // 달력에서 과거 선택 자체 차단
        if (due.value && due.value < t) {        // 기존값이 과거면 즉시 보정
        alert(`납기일은 오늘(${t}) 이후만 가능합니다.`);
        due.value = t;
        due.focus();
        }
    };

    // 최초/포커스 시점마다 min 재설정(자정 경과 대비)
    applyMin();
    due.addEventListener('focus', applyMin);

    // 수동 입력/선택으로 과거가 들어오면 즉시 차단 + 보정
    const guard = () => {
        const t = due.min || todayStr();
        const v = String(due.value || '').trim();
        if (v && v < t) {
        alert(`납기일은 오늘(${t}) 이후만 가능합니다.`);
        due.value = t;
        due.focus();
        }
    };
    due.addEventListener('change', guard);
    due.addEventListener('input',  guard);
    due.addEventListener('blur',   guard);
    })();

}




// 발주 상세(품목) 체크박스 전체선택 + 삭제 
function bindCheckboxDelete() {
  const tableBody = document.querySelector('.order_list');
  let   header    = document.getElementById('checkAll'); // 교체될 수 있으니 let

  if (!tableBody || !header) return;

  const ROW_CB_SEL = '.row_check:not([disabled])';
  const getBoxes   = () => tableBody.querySelectorAll(ROW_CB_SEL);
  const getChecked = () => tableBody.querySelectorAll(ROW_CB_SEL + ':checked');

  // 헤더 이벤트 부착(교체 후에도 재사용)
  const attachHeaderHandler = () => {
    header.addEventListener('change', () => {
      const v = header.checked;
      getBoxes().forEach(cb => { cb.checked = v; });
      syncHeader(); // 상태 재동기화
    });
  };

  // 헤더를 완전히 새로 생성해 교체(잔여 indeterminate/리스너/스타일 초기화)
  const hardResetHeader = (checked = false) => {
    const newHeader = header.cloneNode(true);
    newHeader.checked = !!checked;
    // indeterminate 강제 해제
    newHeader.indeterminate = false;

    header.replaceWith(newHeader);
    header = newHeader;         // 참조 업데이트
    attachHeaderHandler();      // 리스너 재부착
  };

  // 헤더 상태 동기화: 전부 체크일 때만 체크, 나머지는 완전 해제
  const syncHeader = () => {
    const total   = getBoxes().length;
    const checked = getChecked().length;

    if (total > 0 && checked === total) {
      // 모두 체크 → 헤더 체크(필요시 하드 리셋하여 혼합 상태 제거)
      if (!header.checked || header.indeterminate) hardResetHeader(true);
      else {
        header.indeterminate = false;
        header.checked = true;
      }
    } else {
      // 일부 또는 0개 → 헤더 완전 해제
      if (header.checked || header.indeterminate) hardResetHeader(false);
      else {
        header.indeterminate = false;
        header.checked = false;
      }
    }
  };

  // 개별 체크(동적 행 포함) → 헤더 상태 동기화
  tableBody.addEventListener('change', (e) => {
    const t = e.target;
    if (!(t instanceof HTMLInputElement) || !t.classList.contains('row_check')) return;
    syncHeader();
  });

  // 선택 삭제 버튼
  const deleteBtn = document.querySelector('.item_del') 
                 || document.querySelector('.list_action .item_del') 
                 || document.querySelector('.list_action button[type="button"]');
  if (deleteBtn) {
    deleteBtn.addEventListener('click', () => {
      const selected = Array.from(getChecked());
      if (selected.length === 0) {
        alert('삭제할 항목을 선택하세요.');
        return;
      }
      if (!confirm(`선택한 ${selected.length}개의 항목을 삭제하시겠습니까?`)) return;

      selected.forEach(cb => cb.closest('tr') && cb.closest('tr').remove());

      // 번호/합계 갱신(있으면 호출)
      if (typeof renumberRows === 'function') renumberRows();
      if (typeof recalcFooter === 'function') recalcFooter();

      // 헤더는 완전 해제 상태로
      hardResetHeader(false);
      syncHeader();
    });
  }

  // 초기: 헤더 리스너 부착 + 상태 동기화
  attachHeaderHandler();
  syncHeader();
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



