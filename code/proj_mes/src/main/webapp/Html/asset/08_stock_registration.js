window.addEventListener('load', init);
function init() {
  bindstock_reg();
}

/* 유틸 */
function toInt(v) {
  const n = parseInt(String(v).replace(/[^0-9.-]/g, ''), 10);
  return isNaN(n) ? 0 : n;
}

// 표에서 문자열 검색
function filterTableRows(tableList, query) {
  if (!tableList) return;
  const q = (query || '').toLowerCase();
  const rows = tableList.querySelectorAll('tr');
  let any = false;

  // 기존 안내행 제거
  const oldEmpty = tableList.querySelector('.empty-row');
  if (oldEmpty) oldEmpty.remove();

  rows.forEach((tr) => {
    const text  = (tr.textContent || '').toLowerCase();
    const match = !q || text.includes(q);
    tr.style.display = match ? '' : 'none';

    if (!match) {
      const r = tr.querySelector('input[type="radio"]');
      if (r) r.checked = false;
    }
    if (match) any = true;
  });

  if (!any) {
    const table   = tableList.closest('table');
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

/* 공통 모달 핸들러 */
function modalHandler(openBtn, modal, onConfirm) {
  if (!openBtn || !modal) return;

  const searchInput = modal.querySelector('.m_search');
  const searchBtn   = modal.querySelector('.m_button'); 
  const list        = modal.querySelector('.list');
  const Confirm     = modal.querySelector('.confirm');
  const Cancel      = modal.querySelector('.cancel');
  const qtyInput    = modal.querySelector('.m_quantity');

  // 열기
  openBtn.addEventListener('click', () => {
    modal.classList.remove('hide');
    if (searchInput) {
      searchInput.value = '';
      filterTableRows(list, '');
    }
    if (qtyInput) qtyInput.value = '1';
  });

  // 검색(입력/버튼)
  if (searchInput) {
    searchInput.addEventListener('input', () => {
      filterTableRows(list, searchInput.value);
    });
  }
  if (searchBtn && searchInput) {
    searchBtn.addEventListener('click', () => {
      filterTableRows(list, searchInput.value);
    });
  }

  // 행 클릭 → 라디오 체크
  if (list) {
    list.addEventListener('click', (e) => {
      const tr = e.target.closest('tr');
      if (!tr || tr.style.display === 'none') return;
      const r = tr.querySelector('input[type="radio"]');
      if (r) r.checked = true;
    });
  }

  // 확인/취소
  if (Confirm) {
    Confirm.addEventListener('click', () => {
      const checked = list ? list.querySelector('input[type="radio"]:checked') : null;
      if (!checked) { alert('항목을 선택하세요.'); return; }
      onConfirm(checked.closest('tr'));
      modal.classList.add('hide');
    });
  }
  if (Cancel) {
    Cancel.addEventListener('click', () => modal.classList.add('hide'));
  }
}

/*  메인 바인딩  */
function bindstock_reg() {
  const form = document.getElementById('mainForm');
  if (!form) return;

  // 버튼 기반 모드판단: 저장 버튼이 있으면 add/edit, 없으면 view
  const isEditing = !!document.getElementById('btnsave');

  const itemCodeInput = document.getElementById('item_input');        
  const itemNameInput = document.getElementById('item_name_input');
  const priceInput    = document.getElementById('stock_price_input');
  const qtyInput      = document.getElementById('stock_number_input');
  const itemModal     = document.getElementById('item_modal');

  if (isEditing) {
    // 수정/등록 화면에서만 모달 동작
    modalHandler(itemCodeInput, itemModal, function (tr) {
      // [0]라디오 [1]코드 [2]이름 [3]유형 [4]단가(콤마 가능)
      const code      = tr.children[1].textContent.trim();
      const name      = tr.children[2].textContent.trim();
      const unitPrice = toInt(tr.children[4].textContent.trim());

      const qtyInModal = itemModal.querySelector('.m_quantity');
      let qty = toInt(qtyInModal ? qtyInModal.value : '1');
      if (!qty || qty <= 0) qty = 1;

      if (itemCodeInput) itemCodeInput.value = code;
      if (itemNameInput) itemNameInput.value = name;
      if (priceInput)    priceInput.value    = String(unitPrice);
      if (qtyInput)      qtyInput.value      = String(qty);
    });
  } else {
    // 상세(view) 화면: 모달 금지
    if (itemCodeInput) {
      itemCodeInput.addEventListener('click', (e) => e.preventDefault());
      itemCodeInput.addEventListener('focus', (e) => e.target.blur());
      itemCodeInput.style.cursor = 'default';
    }
  }

  // 제출 전 필수값 검사
  form.addEventListener('submit', function (e) {
    const inputs = form.querySelectorAll('.main_input');
    for (let i = 0; i < inputs.length; i++) {
      const el = inputs[i];
      if (el.readOnly) continue;
      if (String(el.value || '').trim() === '') {
        const label = el.previousElementSibling ? el.previousElementSibling.innerText : '필수';
        alert(label + ' 항목을 입력하세요.');
        el.focus();
        e.preventDefault();
        return;
      }
    }

    if (qtyInput) {
      const n = toInt(qtyInput.value);
      if (!n || n <= 0) {
        alert('수량은 1 이상 숫자로 입력하세요.');
        qtyInput.focus();
        e.preventDefault();
        return;
      }
    }

    if (itemCodeInput && !String(itemCodeInput.value || '').trim()) {
      alert('품목을 선택하세요.');
      itemCodeInput.focus();
      e.preventDefault();
      return;
    }
  });
}
