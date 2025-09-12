window.addEventListener('load', init);

function init() {
  bindstock_reg();
}

function bindstock_reg() {
  // JSP에서 내려준 모드 플래그
  const RO      = !!window.RO;       // view 모드일 때 true
  const IS_EDIT = !!window.IS_EDIT;  // edit 모드일 때 true

  // 숫자 유틸
  const toInt = (v) => {
    const n = parseInt(String(v).replace(/[^0-9.-]/g, ''), 10);
    return isNaN(n) ? 0 : n;
  };

  // 요소
  const itemInput     = document.querySelector('#item_input');
  const itemNameInput = document.querySelector('#item_name_input');
  const priceInput    = document.querySelector('#stock_price_input');   // name=stock_stat
  const qtyInput      = document.querySelector('#stock_number_input');
  const itemModal     = document.querySelector('#item_modal');

  // ===== View 모드 보호 =====
  // - 어떤 값도 변경/포맷하지 않음
  // - 품목코드 필드는 클릭/포커스 둘 다 막음(모달 X)
  if (RO) {
    if (itemInput) {
      itemInput.readOnly = true;
      itemInput.style.pointerEvents = 'none';
      itemInput.style.cursor = 'default';
    }
    if (qtyInput)   qtyInput.readOnly   = true;
    if (priceInput) priceInput.readOnly = true;
    return; // ← 여기서 끝! (아무 로직도 수행하지 않음)
  }

  // ===== Edit/Add 모드에서만 모달 연결 =====
  if (!IS_EDIT && !window.IS_ADD) return;

  // 공용: 테이블 필터
  function filterTableRows(tableList, query) {
    const q = (query || '').toLowerCase();
    const rows = tableList.querySelectorAll('tr');
    let any = false;

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

  function bindModal(openBtn, modal, onConfirm) {
    if (!openBtn || !modal) return;

    const searchInput = modal.querySelector('.m_search');
    const list        = modal.querySelector('.list');
    const btnOk       = modal.querySelector('#item_modal_confirm');
    const btnCancel   = modal.querySelector('#item_modal_cancel');
    const qtyInModal  = modal.querySelector('.m_quantity');

    const open = () => {
      modal.classList.remove('hide');
      modal.style.display = 'block';
      if (searchInput) {
        searchInput.value = '';
        filterTableRows(list, '');
      }
      if (qtyInModal) qtyInModal.value = '1';
    };

    openBtn.addEventListener('click', open);
    openBtn.addEventListener('focus', open);

    if (list) {
      list.addEventListener('click', (e) => {
        const tr = e.target.closest('tr');
        if (!tr || tr.style.display === 'none') return;
        const r = tr.querySelector('input[type="radio"]');
        if (r) r.checked = true;
      });
    }

    btnOk.addEventListener('click', () => {
      const checked = list ? list.querySelector('input[type="radio"]:checked') : null;
      if (!checked) { alert('항목을 선택하세요.'); return; }
      onConfirm(checked.closest('tr'));
      modal.classList.add('hide');
      modal.style.display = 'none';
    });

    btnCancel.addEventListener('click', () => {
      modal.classList.add('hide');
      modal.style.display = 'none';
    });
  }

  // 모달 연결 (Edit/Add 모드에서만)
  bindModal(itemInput, itemModal, function (tr) {
    // [0]라디오 [1]코드 [2]이름 [3]유형 [4]단가(천단위 콤마 가능)
    const code      = tr.children[1].textContent.trim();
    const name      = tr.children[2].textContent.trim();
    const unitPrice = toInt(tr.children[4].textContent.trim());

    const qtyFromModalEl = itemModal.querySelector('.m_quantity');
    let qty = toInt(qtyFromModalEl ? qtyFromModalEl.value : '');
    if (!qty || qty <= 0) qty = 1;

    if (itemInput)     itemInput.value     = code;
    if (itemNameInput) itemNameInput.value = name;
    if (priceInput)    priceInput.value    = String(unitPrice); // 서버로 숫자 전달
    if (qtyInput)      qtyInput.value      = String(qty);
  });
}
