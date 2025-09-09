window.addEventListener('load', init);

function init() {
  bindstock_list();
}

function bindstock_list() {
  const table = document.querySelector('.tables');
  if (!table) return; // 테이블 없으면 종료

  const tbody = table.querySelector('tbody');
  const tfoot = table.querySelector('tfoot');
  if (!tbody) return;

  // rows는 정적 NodeList. 동적 행 추가가 있다면 매번 querySelectorAll로 다시 가져와도 됨.
  let rows = tbody.querySelectorAll('tr');

  // --- 필터 select ---
  const majorSelect  = document.querySelector('.big');      // 대분류
  const middleSelect = document.querySelector('.middle');   // 중분류
  const minorSelect  = document.querySelector('.small');    // 소분류
  const typeSelect   = document.querySelector('.type');     // 구분

  // --- 날짜 input & 조회 버튼 ---
  const dateMinInput = document.getElementById('start_date');
  const dateMaxInput = document.getElementById('end_date');
  const searchBtn    = document.querySelector('.date_filter button'); // 선택사항

  // --- 숫자 유틸 ---
  function toInt(v) {
    const n = parseInt(String(v).replace(/[^0-9.-]/g, ''), 10);
    return isNaN(n) ? 0 : n;
  }
  function fmt(n) {
    return (Number(n) || 0).toLocaleString();
  }

  // --- 컬럼 인덱스 ---
  const COL = { NO:0, DATE:1, TYPE:6, QTY:7, PRICE:8 };

  // YYYYMMDD → Date 객체
  function parseCellDate(yyyymmdd) {
    const s = String(yyyymmdd || '').trim();
    if (!/^\d{8}$/.test(s)) return null;
    const y = parseInt(s.slice(0, 4), 10);
    const m = parseInt(s.slice(4, 6), 10) - 1;
    const d = parseInt(s.slice(6, 8), 10);
    return new Date(y, m, d);
  }

  // input[type="date"] → Date 객체 (로컬)
  function parseInputDate(value) {
    if (!value) return null;
    const parts = value.split('-').map(Number);
    if (parts.length !== 3) return null;
    return new Date(parts[0], parts[1] - 1, parts[2]);
  }

  // 시작일/종료일 교정(시작일 > 종료일이면 맞바꾸기)
  function normalizeDateRange(minDate, maxDate) {
    if (minDate && maxDate && minDate > maxDate) {
      const tmp = minDate;
      minDate = maxDate;
      maxDate = tmp;
    }
    return { minDate, maxDate };
  }

  // --- 필터 적용 ---
  function applyFilter() {
    // 행이 동적으로 바뀐다면 주석 해제:
    // rows = tbody.querySelectorAll('tr');

    const majorValue  = majorSelect?.value?.trim()  || '전체';
    const middleValue = middleSelect?.value?.trim() || '전체';
    const minorValue  = minorSelect?.value?.trim()  || '전체';
    const typeValue   = typeSelect?.value?.trim()   || '전체';

    let minDate = parseInputDate(dateMinInput?.value);
    let maxDate = parseInputDate(dateMaxInput?.value);
    ({ minDate, maxDate } = normalizeDateRange(minDate, maxDate));

    let visibleIndex = 0;
    let totalQty = 0;
    let totalAmt = 0;

    rows.forEach(function (tr) {
      const tds = tr.children;
      let ok = true;

      const rowText = tr.textContent;

      // 1) 대/중/소분류 포함 매칭 (테이블에 별도 컬럼 없을 때)
      if (majorValue  !== '전체' && !rowText.includes(majorValue))  ok = false;
      if (middleValue !== '전체' && !rowText.includes(middleValue)) ok = false;
      if (minorValue  !== '전체' && !rowText.includes(minorValue))  ok = false;

      // 2) 구분(정확 일치)
      const rowType = (tds[COL.TYPE]?.textContent || '').trim();
      if (typeValue !== '전체' && rowType !== typeValue) ok = false;

      // 3) 날짜 범위 (포함 비교: >= min, <= max)
      const cellDateStr = (tds[COL.DATE]?.textContent || '').trim();
      const rowDate = parseCellDate(cellDateStr);
      if (minDate && rowDate && rowDate < minDate) ok = false;
      if (maxDate && rowDate && rowDate > maxDate) ok = false;

      // 4) 표시/숨김
      tr.style.display = ok ? '' : 'none';

      // 5) 보이는 행만 NO 재번호 + 합계 계산
      if (ok) {
        if (tds[COL.NO]) tds[COL.NO].textContent = ++visibleIndex;

        const qty   = toInt(tds[COL.QTY]?.textContent);
        const price = toInt(tds[COL.PRICE]?.textContent);

        totalQty += qty;
        totalAmt += qty * price;
      }
    });

    // 6) tfoot 합계 갱신
    if (tfoot) {
      const footRow = tfoot.querySelector('tr');
      if (footRow) {
        const ftds = footRow.children;
        // [Total(colspan=7), 수량합, 금액합] 구조 가정
        if (ftds.length >= 3) {
          ftds[ftds.length - 2].textContent = fmt(totalQty);
          ftds[ftds.length - 1].textContent = fmt(totalAmt);
        }
      }
    }
  }

  // --- 이벤트 연결 ---
  majorSelect?.addEventListener('change', applyFilter);
  middleSelect?.addEventListener('change', applyFilter);
  minorSelect?.addEventListener('change', applyFilter);
  typeSelect?.addEventListener('change', applyFilter);
  dateMinInput?.addEventListener('change', applyFilter);
  dateMaxInput?.addEventListener('change', applyFilter);

  // 조회 버튼으로만 필터링
  if (searchBtn) {
    searchBtn.addEventListener('click', function (e) {
      e.preventDefault();
      applyFilter();
    });
  }

  // 초기 실행
  applyFilter();
}
