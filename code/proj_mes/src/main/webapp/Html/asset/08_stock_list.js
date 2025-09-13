// /Html/asset/08_stock_list.js
window.addEventListener('load', init);

function init() {
  bindstock_list();
}

function bindstock_list() {
  const table = document.querySelector('.tables');
  if (!table) return;

  // tbody는 .tables_body 우선, 없으면 첫 번째 TBODY
  const tbody = table.querySelector('.tables_body') || table.tBodies[0];
  if (!tbody) return;

  // 헤더 체크박스: #check_all 우선, 없으면 thead의 첫 번째 체크박스
  const headerCb =
    document.getElementById('check_all') ||
    table.querySelector('thead input[type="checkbox"]');

  const deleteForm   = document.getElementById('deleteForm');
  const deleteHidden = document.getElementById('delete_ids');
  const deleteBtn    = document.getElementById('deleteBtn');

  // tbody 안의 사용 가능한(비활성 아님) 행 체크박스들
  const rowBoxes = () =>
    Array.from(tbody.querySelectorAll('input.row_check[type="checkbox"]'))
      .filter(cb => !cb.disabled);

  // 모두 체크일 때만 헤더 체크, 아니면 해제 (indeterminate 미사용)
  const syncHeaderStrict = () => {
    if (!headerCb) return;
    const boxes = rowBoxes();
    headerCb.checked = boxes.length > 0 && boxes.every(cb => cb.checked);
  };

  // 헤더 → 전체 토글
  if (headerCb) {
    headerCb.addEventListener('change', () => {
      const v = headerCb.checked;
      rowBoxes().forEach(cb => { cb.checked = v; });
      syncHeaderStrict();
    });
  }

  // 개별 체크 → 헤더 동기화 (이벤트 위임)
  tbody.addEventListener('change', (e) => {
    const t = e.target;
    if (!(t instanceof HTMLInputElement)) return;
    if (t.type !== 'checkbox' || !t.classList.contains('row_check')) return;
    syncHeaderStrict();
  });

  // 삭제: 체크된 값들을 CSV로 합쳐 hidden에 넣고 제출
  if (deleteBtn && deleteForm && deleteHidden) {
    deleteBtn.addEventListener('click', (e) => {
      e.preventDefault();
      const ids = rowBoxes()
        .filter(cb => cb.checked)
        .map(cb => cb.value)
        .filter(Boolean);

      if (ids.length === 0) {
        alert('삭제할 항목을 선택하세요.');
        return;
      }
      deleteHidden.value = ids.join(',');
      deleteForm.submit();
    });
  }

  // 초기 1회 헤더 상태 정합
  syncHeaderStrict();
}



// window.addEventListener('load', init);

// function init() {
//   bindstock_list();
// }

// function bindstock_list() {
//   const table = document.querySelector('.tables');
//   if (!table) return;

//   const tbody = table.querySelector('tbody');
//   const tfoot = table.querySelector('tfoot');
//   if (!tbody) return;

//   // 헤더 전체선택 체크박스 (thead > input[type=checkbox] 또는 #check_all 지원)
//   const headerCb = table.querySelector('thead input[type="checkbox"]') || document.getElementById('check_all');

//   // --- 필터 select ---
//   const majorSelect  = document.querySelector('.big');      // 대분류
//   const middleSelect = document.querySelector('.middle');   // 중분류
//   const minorSelect  = document.querySelector('.small');    // 소분류
//   const typeSelect   = document.querySelector('.type');     // 구분

//   // --- 날짜 input & 조회 버튼 ---
//   const dateMinInput = document.getElementById('start_date');
//   const dateMaxInput = document.getElementById('end_date');
//   const searchBtn    = document.querySelector('.date_filter button'); // 선택사항

//   // --- 숫자 유틸 ---
//   function toInt(v) {
//     const n = parseInt(String(v).replace(/[^0-9.-]/g, ''), 10);
//     return isNaN(n) ? 0 : n;
//   }
//   function fmt(n) {
//     return (Number(n) || 0).toLocaleString();
//   }

//   // --- 컬럼 인덱스 (표 구조에 맞게 유지) ---
//   const COL = { NO:0, DATE:1, TYPE:6, QTY:7, PRICE:8 };

//   // YYYYMMDD → Date
//   function parseCellDate(yyyymmdd) {
//     const s = String(yyyymmdd || '').trim();
//     if (!/^\d{8}$/.test(s)) return null;
//     const y = parseInt(s.slice(0, 4), 10);
//     const m = parseInt(s.slice(4, 6), 10) - 1;
//     const d = parseInt(s.slice(6, 8), 10);
//     return new Date(y, m, d);
//   }
//   // input[type="date"] → Date
//   function parseInputDate(value) {
//     if (!value) return null;
//     const parts = value.split('-').map(Number);
//     if (parts.length !== 3) return null;
//     return new Date(parts[0], parts[1] - 1, parts[2]);
//   }
//   // 시작/종료 교정
//   function normalizeDateRange(minDate, maxDate) {
//     if (minDate && maxDate && minDate > maxDate) {
//       const t = minDate; minDate = maxDate; maxDate = t;
//     }
//     return { minDate, maxDate };
//   }

//   // ===== 체크박스(보이는 행 기준) =====
//   function isVisibleRow(tr) {
//     return tr && getComputedStyle(tr).display !== 'none';
//   }
//   function visibleBoxes() {
//     return Array.from(tbody.querySelectorAll('input.row_check[type="checkbox"]'))
//       .filter(cb => isVisibleRow(cb.closest('tr')) && !cb.disabled);
//   }
//   // 모두 체크일 때만 헤더 체크, 아니면 해제 (indeterminate 사용 안 함)
//   function syncHeaderStrict() {
//     if (!headerCb) return;
//     const boxes = visibleBoxes();
//     const total = boxes.length;
//     const allChecked = total > 0 && boxes.every(cb => cb.checked);
//     headerCb.checked = allChecked;
//     headerCb.indeterminate = false;
//   }
//   // 헤더 → 보이는 행 전체 토글
//   if (headerCb) {
//     headerCb.addEventListener('change', () => {
//       const v = headerCb.checked;
//       visibleBoxes().forEach(cb => cb.checked = v);
//       // 헤더 상태는 이미 v로 설정됨
//     });
//   }
//   // 개별 체크 → 헤더 동기화
//   tbody.addEventListener('change', e => {
//     const t = e.target;
//     if (!(t instanceof HTMLInputElement)) return;
//     if (t.type !== 'checkbox' || !t.classList.contains('row_check')) return;
//     syncHeaderStrict();
//   });

//   // ===== 필터 적용 =====
//   function applyFilter() {
//     const rows = tbody.querySelectorAll('tr');

//     const majorValue  = majorSelect ? String(majorSelect.value || '').trim()  : '전체';
//     const middleValue = middleSelect ? String(middleSelect.value || '').trim() : '전체';
//     const minorValue  = minorSelect ? String(minorSelect.value || '').trim()  : '전체';
//     const typeValue   = typeSelect ? String(typeSelect.value || '').trim()    : '전체';

//     let minDate = parseInputDate(dateMinInput ? dateMinInput.value : '');
//     let maxDate = parseInputDate(dateMaxInput ? dateMaxInput.value : '');
//     ({ minDate, maxDate } = normalizeDateRange(minDate, maxDate));

//     let visibleIndex = 0;
//     let totalQty = 0;
//     let totalAmt = 0;

//     rows.forEach(tr => {
//       const tds = tr.children;
//       let ok = true;

//       const rowText = tr.textContent || '';

//       // 1) 대/중/소분류 (포함 매칭)
//       if (majorValue  !== '전체' && rowText.indexOf(majorValue)  === -1) ok = false;
//       if (middleValue !== '전체' && rowText.indexOf(middleValue) === -1) ok = false;
//       if (minorValue  !== '전체' && rowText.indexOf(minorValue)  === -1) ok = false;

//       // 2) 구분(정확 일치)
//       const rowType = tds[COL.TYPE] ? String(tds[COL.TYPE].textContent || '').trim() : '';
//       if (typeValue !== '전체' && rowType !== typeValue) ok = false;

//       // 3) 날짜 범위
//       const cellDateStr = tds[COL.DATE] ? String(tds[COL.DATE].textContent || '').trim() : '';
//       const rowDate = parseCellDate(cellDateStr);
//       if (minDate && rowDate && rowDate < minDate) ok = false;
//       if (maxDate && rowDate && rowDate > maxDate) ok = false;

//       // 4) 표시/숨김
//       tr.style.display = ok ? '' : 'none';

//       // 5) 보이는 행만 NO/합계
//       if (ok) {
//         if (tds[COL.NO]) tds[COL.NO].textContent = ++visibleIndex;

//         const qty   = toInt(tds[COL.QTY]   ? tds[COL.QTY].textContent   : 0);
//         const price = toInt(tds[COL.PRICE] ? tds[COL.PRICE].textContent : 0);

//         totalQty += qty;
//         totalAmt += qty * price;
//       }
//     });

//     // 6) 합계 갱신
//     if (tfoot) {
//       const footRow = tfoot.querySelector('tr');
//       if (footRow) {
//         const ftds = footRow.children;
//         if (ftds.length >= 3) {
//           ftds[ftds.length - 2].textContent = fmt(totalQty);
//           ftds[ftds.length - 1].textContent = fmt(totalAmt);
//         }
//       }
//     }

//     // 7) 필터 후 헤더 체크박스 상태 재동기화(보이는 행 기준)
//     syncHeaderStrict();
//   }

//   // --- 이벤트 연결 ---
//   if (majorSelect)  majorSelect.addEventListener('change', applyFilter);
//   if (middleSelect) middleSelect.addEventListener('change', applyFilter);
//   if (minorSelect)  minorSelect.addEventListener('change', applyFilter);
//   if (typeSelect)   typeSelect.addEventListener('change', applyFilter);
//   if (dateMinInput) dateMinInput.addEventListener('change', applyFilter);
//   if (dateMaxInput) dateMaxInput.addEventListener('change', applyFilter);

//   if (searchBtn) {
//     searchBtn.addEventListener('click', e => {
//       e.preventDefault();
//       applyFilter();
//     });
//   }

//   // 초기 실행
//   applyFilter();
//   // (초기에도 헤더 상태 정합)
//   syncHeaderStrict();
// }
