// /Html/asset/07_order_list.js

// 진입점
window.addEventListener('load', init);

function init() {
  bindOrderList();
}

function bindOrderList() {
  const table = document.querySelector('.tables');
  const tbody = document.querySelector('.tables_body');
  if (!table || !tbody) return;

  // UI refs
  const filterForm = document.querySelector('.order_filter');
  const startInput = document.getElementById('start_date');
  const endInput   = document.getElementById('end_date');
  const checkAll   = document.getElementById('check_all');

  const deleteBtn  = document.getElementById('deleteBtn');
  const deleteKeys = document.getElementById('delete_keys');
  const deleteForm = document.getElementById('deleteForm');

  // ===== 유틸 =====
  const thCount = table.querySelectorAll('thead th').length;

  const isVisible = (tr) => tr && tr.style.display !== 'none';
  const visibleRows = () =>
    Array.from(tbody.querySelectorAll('tr'))
      .filter(tr => isVisible(tr) && !tr.classList.contains('empty-row'));

  function renumberVisible() {
    const rows = visibleRows();
    rows.forEach((tr, i) => {
      const noCell = tr.children[1];
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
      td.textContent = '조건에 맞는 발주가 없습니다.';
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
    // 시작일 → 종료일 min
    if (startInput && endInput) {
      endInput.min = startInput.value || '';
      if (startInput.value && endInput.value && endInput.value < startInput.value) {
        endInput.value = startInput.value;
      }
    }
    // 종료일 → 시작일 max
    if (startInput && endInput) {
      startInput.max = endInput.value || '';
      if (startInput.value && endInput.value && startInput.value > endInput.value) {
        startInput.value = endInput.value;
      }
    }
  }

  // ===== 기간 필터 =====
  function applyFilter(evt) {
    if (evt) evt.preventDefault();
    applyDateConstraints();

    const start = startInput && startInput.value ? startInput.value : '';
    const end   = endInput   && endInput.value   ? endInput.value   : '';

    // 날짜는 3번째 컬럼(index 2), yyyy-MM-dd 문자열 비교 가능
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
  if (deleteBtn && deleteKeys && deleteForm) {
    deleteBtn.addEventListener('click', () => {
      const keys = Array.from(tbody.querySelectorAll('.row_check:checked'))
        .map(cb => cb.value)
        .filter(Boolean);

      if (keys.length === 0) {
        alert('삭제할 항목을 선택하세요.');
        return;
      }
      deleteKeys.value = keys.join(',');
      deleteForm.submit();
    });
  }

  // ===== 바인딩 =====
  if (startInput) startInput.addEventListener('change', applyDateConstraints);
  if (endInput)   endInput.addEventListener('change', applyDateConstraints);

  if (filterForm) {
    filterForm.addEventListener('submit', applyFilter);
  }

  // 초기값 존재 시 즉시 필터
  applyDateConstraints();
  if ((startInput && startInput.value) || (endInput && endInput.value)) {
    applyFilter();
  }
}
