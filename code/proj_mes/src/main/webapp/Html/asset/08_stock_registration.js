window.addEventListener('load', init);

function init() {
  bindstock_reg();
}

function bindstock_reg() {

    // 숫자 유틸
    function toInt(v) {
        const n = parseInt(String(v).replace(/[^0-9.-]/g, ''), 10);
        return isNaN(n) ? 0 : n;
    }
    function fmt(n) {
        return (Number(n) || 0).toLocaleString();
    }

    // 공통 함수: 모달 오픈, 검색, 클로즈
    function modalHandler(openBtn, modal, onConfirm) {
        const searchInput = modal.querySelector('.m_search');
        const list        = modal.querySelector('.list');
        const Confirm     = modal.querySelector('.confirm');
        const Cancel      = modal.querySelector('.cancel');

        // 열기
        openBtn.addEventListener('click', () => {
        modal.classList.remove('hide');
        if (searchInput) {
            searchInput.value = '';
            filterTableRows(list, '');
        }
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

    // ===== 품목 모달 =====
    const itemInput     = document.querySelector('.item_input');       // 품목코드 입력칸
    const itemNameInput = document.querySelector('.itemName_input');   // 품목명/모델명 입력칸
    const priceInput    = document.querySelector('.unitPrice_input');  // 단가 입력칸
    const qtyInput      = document.querySelector('.qty_input');        // 수량 입력칸
    const totalInput    = document.querySelector('.total_input');      // 합계 입력칸
    const itemModal     = document.querySelector('#item_modal');       // 모달 자체

    // 입력 보호
    if (itemInput)     itemInput.readOnly = true;
    if (itemNameInput) itemNameInput.readOnly = true;
    if (priceInput)    priceInput.readOnly = true;
    if (totalInput)    totalInput.readOnly = true;

    // 합계 재계산
    function recalcTotal() {
        if (!qtyInput || !priceInput || !totalInput) return;
        const qty   = toInt(qtyInput.value);
        const price = toInt(priceInput.value);
        totalInput.value = fmt(qty * price);
    }

    // 수량 입력 시 숫자만 유지 + 합계 갱신
    if (qtyInput) {
        qtyInput.addEventListener('input', () => {
            qtyInput.value = qtyInput.value.replace(/[^\d]/g, ''); // 정수만
            recalcTotal();
        });
    }
    // (필요 시) 단가 입력이 바뀌어도 합계 갱신
    if (priceInput) {
        priceInput.addEventListener('input', () => {
            priceInput.value = priceInput.value.replace(/[^\d]/g, '');
            recalcTotal();
        });
    }

    // 모달 연결
    if (itemInput && itemModal) {
        modalHandler(itemInput, itemModal, function (tr) {
            // [0:라디오][1:품목코드][2:품목명][3:유형][4:단가]
            const code  = tr.children[1].textContent.trim();
            const name  = tr.children[2].textContent.trim();
            const price = toInt(tr.children[4].textContent.trim());

            itemInput.value     = code;
            itemNameInput.value = name;
            priceInput.value    = fmt(price);

            // 기본 수량을 1로 세팅
            if (qtyInput && !qtyInput.value) {
                qtyInput.value = 1;
            }

            // 품목 선택 직후 현재 수량 기준으로 합계 갱신
            recalcTotal();
        });
    }
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
