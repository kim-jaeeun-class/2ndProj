window.addEventListener('load', init);

function init() {
  bindOrder_list();
}

function bindOrder_list() {
  const table = document.querySelector('.tables');
  if (!table) return;

  const thead = table.querySelector('thead');
  const tbody = table.querySelector('tbody.tables_body');

  // 필터 UI
  const startDate = document.getElementById('start_date');
  const endDate   = document.getElementById('end_date');
  const stateSel  = document.getElementById('state_select');
  const filterBtn = document.getElementById('filter_btn');

  // 액션 버튼
  const deleteBtn = document.querySelector('.action .delete');

  // 헤더 전체선택 체크박스
  const headerCb = thead ? thead.querySelector('input[type="checkbox"]') : null;

  // ---------- 유틸 ----------
  const isVisible = (el) => el.style.display !== 'none';

  // 상태 문자열에서 공백 제거
  function normalizeState(str) {
    return (str || '').replace(/\s/g, '');
  }

  // NO 다시 번호 매기기
  function renumberVisible() {
    let n = 1;
    tbody.querySelectorAll('tr').forEach(tr => {
      if (isVisible(tr)) {
        tr.children[1].textContent = n++;
      }
    });
  }

  // 전체선택 체크박스 해제
  function clearSelectAll() {
    if (headerCb) headerCb.checked = false;
  }

  // ---------- 회수 버튼 표시/숨김 ----------
  function updateRecallButtonVisibility(tr) {
    const stateCell = tr.querySelector('.row_state');
    const recallBtn = tr.querySelector('.recall');
    if (!stateCell || !recallBtn) return;

    const state = normalizeState(stateCell.textContent.trim());
    // 승인 또는 임시저장 상태 → 회수 버튼 숨김
    if (state === '승인' || state === '임시저장') {
      recallBtn.style.display = 'none';
    } else {
      recallBtn.style.display = '';
    }
  }

  // 테이블 전체에 대해 초기 상태 확인
  function updateAllRecallButtons() {
    tbody.querySelectorAll('tr').forEach(updateRecallButtonVisibility);
  }

  // ---------- 필터 ----------
  function applyFilter() {
    const start = startDate && startDate.value ? startDate.value : ''; // yyyy-mm-dd
    const end   = endDate && endDate.value ? endDate.value : '';
    const sel   = stateSel && stateSel.value ? stateSel.value : '전체';
    const selNorm = normalizeState(sel);

    tbody.querySelectorAll('tr').forEach(tr => {
      const dateCell  = tr.querySelector('.row_date');
      const stateCell = tr.querySelector('.row_state');

      const dateStr   = dateCell ? dateCell.textContent.trim() : '';
      const stateStr  = stateCell ? stateCell.textContent.trim() : '';
      const stateNorm = normalizeState(stateStr);

      let show = true;

      // 상태 필터
      if (sel !== '전체') {
        show = (stateNorm === selNorm);
      }

      // 기간 필터
      if (start) show = show && (dateStr >= start);
      if (end)   show = show && (dateStr <= end);

      tr.style.display = show ? '' : 'none';

      // 회수 버튼 노출 조건 다시 확인
      updateRecallButtonVisibility(tr);
    });

    clearSelectAll();
    renumberVisible();
  }

  // 조회 버튼
  if (filterBtn) {
    filterBtn.addEventListener('click', applyFilter);
  }

  // 전체선택 → 보이는 행만 체크/해제
  if (headerCb) {
    headerCb.addEventListener('change', () => {
      const checked = headerCb.checked;
      tbody.querySelectorAll('tr').forEach(tr => {
        if (!isVisible(tr)) return;
        const cb = tr.querySelector('input.row_check[type="checkbox"]');
        if (cb) cb.checked = checked;
      });
    });
  }

  // 개별 체크 → 헤더 체크 상태 동기화
  tbody.addEventListener('change', (e) => {
    const target = e.target;
    if (!(target instanceof HTMLInputElement) || target.type !== 'checkbox') return;

    const visibleRows = Array.from(tbody.querySelectorAll('tr')).filter(isVisible);
    let allChecked = true;

    for (let i = 0; i < visibleRows.length; i++) {
      const cb = visibleRows[i].querySelector('input.row_check[type="checkbox"]');
      if (!cb || !cb.checked) {
        allChecked = false;
        break;
      }
    }
    if (headerCb) headerCb.checked = allChecked && visibleRows.length > 0;
  });

  // 삭제 버튼 → 체크된 행 삭제
  if (deleteBtn) {
    deleteBtn.addEventListener('click', () => {
      const checkedRows = Array.from(
        tbody.querySelectorAll('input.row_check[type="checkbox"]:checked')
      ).map(cb => cb.closest('tr'));

      if (checkedRows.length === 0) {
        alert('삭제할 항목을 선택하세요.');
        return;
      }

      checkedRows.forEach(tr => tr.remove());
      clearSelectAll();
      renumberVisible();
    });
  }

  // 회수 버튼 클릭 → 승인대기 또는 반려 → 임시저장으로 변경
  tbody.addEventListener('click', (e) => {
    const btn = e.target;
    if (!(btn instanceof HTMLButtonElement)) return;
    if (!btn.classList.contains('recall')) return;

    const tr = btn.closest('tr');
    const statusCell = tr.querySelector('.row_state');
    if (!statusCell) return;

    const cur = normalizeState(statusCell.textContent.trim());

    // 승인대기 또는 반려 상태만 회수 가능
    if (cur === '승인대기' || cur === '반려') {
      statusCell.textContent = '임시저장';
      updateRecallButtonVisibility(tr); // 버튼 숨기기
    }
  });

  // 초기 상태에서 회수 버튼 숨김 처리
  updateAllRecallButtons();
}
