// 08_stock_list.js (교체본)
window.addEventListener('load', init);

function init() {
  bindstock_list();
  bindStockFilter();
}

function bindstock_list() {
  const table = document.querySelector('.tables');
  const tbody = document.querySelector('.tables_body');
  if (!table || !tbody) return;

  // UI refs
  const filterForm = document.getElementById('filterForm'); // ✅ 폼을 정확히 선택
  const startInput = document.getElementById('start_date');
  const endInput   = document.getElementById('end_date');
  const checkAll   = document.getElementById('check_all');

  const deleteBtn  = document.getElementById('deleteBtn');
  const deleteForm = document.getElementById('deleteForm');
  const deleteIds  = document.getElementById('delete_ids');

  // ===== 유틸 =====
  const thCount = table.querySelectorAll('thead th').length;

  const isVisible = (tr) => tr && tr.style.display !== 'none';
  const visibleRows = () =>
    Array.from(tbody.querySelectorAll('tr'))
      .filter(tr => isVisible(tr) && !tr.classList.contains('empty-row'));

  function renumberVisible() {
    const rows = visibleRows();
    rows.forEach((tr, i) => {
      const noCell = tr.children[1]; // NO 컬럼
      if (noCell) noCell.textContent = i + 1;
    });
  }

  function removeEmptyRow() {
    const old = tbody.querySelector('.empty-row');
    if (old) old.remove();
  }

  function showEmptyRowIfNeeded() {
    const anyVisible = visibleRows().length > 0;
    removeEmptyRow();
    if (!anyVisible) {
      const tr = document.createElement('tr');
      tr.className = 'empty-row';
      const td = document.createElement('td');
      td.colSpan = thCount;
      td.style.textAlign = 'center';
      td.textContent = '조건에 맞는 재고가 없습니다.';
      tr.appendChild(td);
      tbody.appendChild(tr);
    }
  }

  function clearHeaderSelect() {
    if (checkAll) {
      checkAll.checked = false;
      checkAll.indeterminate = false;
    }
  }

  // ===== 날짜 min/max 제약 =====
  function applyDateConstraints() {
    if (startInput && endInput) {
      // 시작일 → 종료일 min
      endInput.min = startInput.value || '';
      if (startInput.value && endInput.value && endInput.value < startInput.value) {
        endInput.value = startInput.value;
      }
      // 종료일 → 시작일 max
      startInput.max = endInput.value || '';
      if (startInput.value && endInput.value && startInput.value > endInput.value) {
        startInput.value = endInput.value;
      }
    }
  }

  // ===== 기간 필터(화면에서만 가려보기) =====
  function applyFilter(evt) {
    // 참고: 이 함수는 '로컬 미리보기용'입니다. submit에 붙이지 않습니다.
    if (evt) evt.preventDefault();
    applyDateConstraints();

    const start = startInput && startInput.value ? startInput.value : '';
    const end   = endInput   && endInput.value   ? endInput.value   : '';

    // 날짜는 3번째 컬럼(index 2), yyyy-MM-dd 가정
    tbody.querySelectorAll('tr').forEach(tr => {
      if (tr.classList.contains('empty-row')) return;

      const dateCell = tr.children[2];
      const dateStr  = dateCell ? dateCell.textContent.trim() : '';
      let show = true;

      if (start) show = show && (dateStr >= start);
      if (end)   show = show && (dateStr <= end);

      tr.style.display = show ? '' : 'none';
    });

    clearHeaderSelect();
    renumberVisible();
    showEmptyRowIfNeeded();
  }

  // ===== 전체선택(보이는 행만) =====
  if (checkAll) {
    checkAll.addEventListener('change', () => {
      const checked = checkAll.checked;
      visibleRows().forEach(tr => {
        const cb = tr.querySelector('input.row_check[type="checkbox"]');
        if (cb) cb.checked = checked;
      });
    });
  }

  // 개별 체크 → 헤더 체크 동기화
  tbody.addEventListener('change', (e) => {
    const t = e.target;
    if (!(t instanceof HTMLInputElement) || t.type !== 'checkbox' || !t.classList.contains('row_check')) return;

    if (!checkAll) return;
    const rows = visibleRows();
    const allChecked = rows.length > 0 && rows.every(tr => {
      const cb = tr.querySelector('input.row_check[type="checkbox"]');
      return cb && cb.checked;
    });
    checkAll.checked = allChecked;
    checkAll.indeterminate = false;
  });

  // ===== 삭제(서버 전송: CSV) =====
  if (deleteBtn && deleteForm && deleteIds) {
    deleteBtn.addEventListener('click', (e) => {
      e.preventDefault();
      const ids = Array.from(tbody.querySelectorAll('.row_check:checked'))
        .map(cb => cb.value)
        .filter(Boolean);

      if (ids.length === 0) {
        alert('삭제할 항목을 선택하세요.');
        return;
      }
      deleteIds.value = ids.join(',');
      deleteForm.submit();
    });
  }

  // ===== 바인딩 =====
  if (startInput) startInput.addEventListener('change', applyDateConstraints);
  if (endInput)   endInput.addEventListener('change', applyDateConstraints);

  // ✅ 조회 버튼(폼 제출) 때는 서버로 보냄 — preventDefault 안 함
  if (filterForm) {
    filterForm.addEventListener('submit', () => {
      applyDateConstraints(); // 제출 전에 값 정리만
      // 여기서 기본 동작을 막지 않습니다. (서버로 GET 제출)
    });
  }

  // 초기값 존재 시 화면에서만 날짜 미리 필터
  applyDateConstraints();
  if ((startInput && startInput.value) || (endInput && endInput.value)) {
    applyFilter(); // 이벤트 없이 호출 → 로컬 미리보기
  }
}

function bindStockFilter(){
  const form = document.getElementById('filterForm');
  if(!form) return;

  const big   = form.querySelector('select[name="big"]');
  const mid   = form.querySelector('select[name="mid"]');
  const small = form.querySelector('select[name="small"]');

  // 대분류 변경 → 중/소 초기화 (자동 제출 없음)
  if (big) {
    big.addEventListener('change', () => {
      if (mid)   mid.value = '';
      if (small) small.value = '';
    });
  }

  // 중분류 변경 → 소분류 초기화 (자동 제출 없음)
  if (mid) {
    mid.addEventListener('change', () => {
      if (small) small.value = '';
    });
  }

  // 소분류 변경 — 자동 제출 없음
  if (small) {
    small.addEventListener('change', () => {
      // no-op
    });
  }
}
