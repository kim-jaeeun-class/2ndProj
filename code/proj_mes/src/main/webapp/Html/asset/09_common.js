// 공통 JS : 페이지별 기능 통합
document.addEventListener("DOMContentLoaded", () => {
    const page = document.body.getAttribute('page');

    // ===============================
    // 공통 기능 : 사이드 패널 열기/닫기
    // ===============================
    const initPanel = () => {
        const openBtns = document.querySelectorAll('.open-btn');
        const panel = document.querySelector('.panel');
        if (!panel) return;
        const closeBtn = panel.querySelector('.close-btn');
        const cancelBtn = panel.querySelector('.cancel');

        openBtns.forEach(btn => btn.addEventListener('click', () => panel.classList.add('open')));
        if (closeBtn) closeBtn.addEventListener('click', () => panel.classList.remove('open'));
        if (cancelBtn) cancelBtn.addEventListener('click', () => panel.classList.remove('open'));
    };


    // ===============================
    // 공통 기능 : 체크박스 전체 선택
    // ===============================
    const initSelectAll = () => {
        const selectAllCheckbox = document.querySelector('.select-all');
        if (!selectAllCheckbox) return;
        selectAllCheckbox.addEventListener('change', e => {
            const checkboxes = document.querySelectorAll('input[type="checkbox"]');
            checkboxes.forEach(cb => cb.checked = e.target.checked);
        });
    };

    // ===============================
    // 공통 기능 : 테이블 행 삭제
    // ===============================
    const initTableDelete = () => {
        const mainDeleteBtns = document.querySelectorAll('.wrap-table .delete-btn, .wrap-table .delete');
        mainDeleteBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                const table = btn.closest('.wrap-table').querySelector('table');
                if (!table) return;
                const checked = table.querySelectorAll('tbody input[type="checkbox"]:checked');
                checked.forEach(cb => cb.closest('tr').remove());
            });
        });
    };

    // ===============================
    // 작업 지시서 페이지 기능
    // ===============================
    const initWorkOrder = () => {


        // ===============================
        // 납품처 자동완성
        // ===============================
        const clients = [
            { code: "C001", name: "삼성전자" },
            { code: "C002", name: "LG화학" },
            { code: "C003", name: "현대자동차" },
            { code: "C004", name: "SK하이닉스" }
        ];
        const autocompleteInput = document.querySelector('.autocomplete');
        const suggestionBox = document.querySelector('.suggestions');
        const clientNameInput = document.querySelector('.client-name');

        if (autocompleteInput && suggestionBox && clientNameInput) {
            autocompleteInput.addEventListener('input', () => {
                const query = autocompleteInput.value.toLowerCase();
                suggestionBox.innerHTML = '';
                if (query) {
                    const filtered = clients.filter(c => c.code.toLowerCase().includes(query));
                    filtered.forEach(c => {
                        const div = document.createElement('div');
                        div.textContent = `${c.code} - ${c.name}`;
                        div.addEventListener('click', () => {
                            autocompleteInput.value = c.code;
                            clientNameInput.value = c.name;
                            suggestionBox.style.display = 'none';
                        });
                        suggestionBox.appendChild(div);
                    });
                    suggestionBox.style.display = filtered.length > 0 ? 'block' : 'none';
                } else {
                    suggestionBox.style.display = 'none';
                }
            });
            document.addEventListener('click', (e) => {
                if (!autocompleteInput.contains(e.target) && !suggestionBox.contains(e.target)) {
                    suggestionBox.style.display = 'none';
                }
            });
        }

        // ===============================
        // 품목 추가 모달
        // ===============================
        const itemAddBtn = document.querySelector('.panel .button[value="품목 추가"]');
        const modalOverlay = document.querySelector('.modal-overlay');
        const modalCloseBtn = document.querySelector('.modal-close');
        const modalResetBtn = document.querySelector('.modal .reset');
        const modalSaveBtn = document.querySelector('.modal .save');
        const panelTable = document.querySelector('.panel-table tbody');

        if (itemAddBtn && modalOverlay && modalCloseBtn) {
            itemAddBtn.addEventListener('click', () => modalOverlay.classList.add('active'));
            modalCloseBtn.addEventListener('click', () => modalOverlay.classList.remove('active'));
            modalOverlay.addEventListener('click', (e) => {
                if (e.target === modalOverlay) modalOverlay.classList.remove('active');
            });
        }

        if (modalResetBtn && modalOverlay) {
            modalResetBtn.addEventListener('click', () => {
                const modalCheckboxes = modalOverlay.querySelectorAll('tbody input[type="checkbox"]');
                modalCheckboxes.forEach(cb => cb.checked = false);
            });
        }

        if (modalSaveBtn && panelTable && modalOverlay) {
            modalSaveBtn.addEventListener('click', () => {
                const checkedRows = modalOverlay.querySelectorAll('tbody input[type="radio"]:checked');
                checkedRows.forEach(rowCheckbox => {
                    const row = rowCheckbox.closest('tr');
                    const code = row.children[1].textContent;
                    const name = row.children[2].textContent;

                    // 중복 체크
                    const exists = Array.from(panelTable.children).some(tr => tr.children[2].textContent === code);
                    if (exists) {
                        alert('중복된 품목은 입력할 수 없습니다.');   
                        return
                    };

                    const tr = document.createElement('tr');

                    const tdCode = document.createElement('td');
                    tdCode.textContent = code;
                    tr.appendChild(tdCode);

                    const tdName = document.createElement('td');
                    tdName.textContent = name;
                    tr.appendChild(tdName);

                    // 수량 / 비고 (빈칸)
                    const tdQty = document.createElement('td');
                    tdQty.textContent = '';
                    tr.appendChild(tdQty);

                    panelTable.appendChild(tr);
                });

                modalOverlay.classList.remove('active');
                checkedRows.forEach(cb => cb.checked = false);
            });
        }

// ===============================
// 슬라이딩 입력 → 메인 테이블 추가 (통합 버전)
// ===============================
const panelForm = document.querySelector('.panel form');
const mainTable = document.querySelector('.wrap-table tbody');
const panel = document.querySelector('.panel');
const saveBtn = document.querySelector('.panel .panel-save');
const panelDown = document.querySelector('#panel-down');

if (saveBtn && mainTable && panelForm) {
    saveBtn.addEventListener('click', (e) => {
        e.preventDefault();

        let allValid = true; // 전체 유효성 체크

        // -------------------------------
        // 필수 입력값
        // -------------------------------
        const orderDateInput = panelForm.querySelector('input[name="order-date"]');
        const clientCodeInput = panelForm.querySelector('input[name="client-code"]');
        const clientNameInput = panelForm.querySelector('input[name="client-name"]');
        const personInput = panelForm.querySelector('input[name="person"]');
        const giveDateInput = panelForm.querySelector('input[name="give-date"]');
        const orderNoInput = panelForm.querySelector('input[type="number"]');

        const requiredInputs = [orderDateInput, clientCodeInput, clientNameInput, personInput, giveDateInput, orderNoInput];

        requiredInputs.forEach(input => {
            let msgEl = input.nextElementSibling;
            if (!msgEl || !msgEl.classList.contains('required-msg')) {
                msgEl = document.createElement('div');
                msgEl.classList.add('required-msg');
                msgEl.style.color = 'red';
                msgEl.style.fontSize = '0.85em';
                msgEl.style.marginTop = '2px';
                input.insertAdjacentElement('afterend', msgEl);
            }

            if (!input.value) {
                msgEl.textContent = '필수 입력값입니다.';
                msgEl.style.display = 'block';
                allValid = false;
            } else {
                msgEl.style.display = 'none';
            }
        });

        // -------------------------------
        // 주문번호 범위 체크 (1~5)
        // -------------------------------
        if (orderNoInput.value) {
            const orderNoVal = parseInt(orderNoInput.value, 10);
            const msgEl = orderNoInput.nextElementSibling;
            if (isNaN(orderNoVal) || orderNoVal < 1 || orderNoVal > 5) {
                msgEl.textContent = '1~5까지의 값만 입력 가능합니다.';
                msgEl.style.display = 'block';
                allValid = false;
            }
        }

        // -------------------------------
        // 지시일 ≤ 납기일 체크
        // -------------------------------
        if (orderDateInput.value && giveDateInput.value) {
            const orderDateVal = new Date(orderDateInput.value);
            const giveDateVal = new Date(giveDateInput.value);
            const msgEl = giveDateInput.nextElementSibling;

            if (orderDateVal > giveDateVal) {
                msgEl.textContent = '지시일이 납기일보다 늦을 수 없습니다.';
                msgEl.style.display = 'block';
                allValid = false;
            } else if (msgEl.textContent === '지시일이 납기일보다 늦을 수 없습니다.') {
                msgEl.style.display = 'none';
            }
        }

        // -------------------------------
        // 입력값이 모두 정상일 때만 테이블 추가
        // -------------------------------
        if (!allValid) return;

        // -------------------------------
        // 입력값 수집
        // -------------------------------
        const orderDate = orderDateInput.value;
        const clientName = clientNameInput.value;
        const person = personInput.value;
        const giveDate = giveDateInput.value;
        const orderNo = orderNoInput.value;

        const panelItems = panelForm.querySelectorAll('.panel-table tbody tr');
        let itemNames = [];
        let totalQty = 0;
        panelItems.forEach(row => {
            const name = row.children[3].textContent.trim();
            const qty = parseInt(row.children[4].textContent.trim()) || 0;
            if (name) itemNames.push(name);
            totalQty += qty;
        });

        const date = new Date().toISOString().split('T')[0]; // 오늘 날짜

        // -------------------------------
        // 행 생성
        // -------------------------------
        const tr = document.createElement('tr');
        tr.classList.add('data');

        // 체크박스
        const tdCheckbox = document.createElement('td');
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        tdCheckbox.appendChild(checkbox);
        tr.appendChild(tdCheckbox);

        // 작업지시번호
        const tdWoNo = document.createElement('td');
        tdWoNo.textContent = `${orderDate.replace(/-/g,'')}-${orderNo}`;
        tr.appendChild(tdWoNo);

        // 일자
        const tdDate = document.createElement('td');
        tdDate.textContent = date;
        tr.appendChild(tdDate);

        // 거래처명
        const tdClient = document.createElement('td');
        tdClient.textContent = clientName;
        tr.appendChild(tdClient);

        // 담당자명
        const tdPerson = document.createElement('td');
        tdPerson.textContent = person;
        tr.appendChild(tdPerson);

        // 납기일자
        const tdGive = document.createElement('td');
        tdGive.textContent = giveDate;
        tr.appendChild(tdGive);

        // 품목명
        const tdItems = document.createElement('td');
        tdItems.textContent = itemNames.join(', ');
        tr.appendChild(tdItems);

        // 지시수량
        const tdOrderQty = document.createElement('td');
        tdOrderQty.textContent = totalQty;
        tr.appendChild(tdOrderQty);

        // 생산수량 / 진행상태 / 인쇄 버튼
        tr.appendChild(document.createElement('td'));
        tr.appendChild(document.createElement('td'));
        const tdPrint = document.createElement('td');
        const printBtn = document.createElement('button');
        printBtn.textContent = '인쇄';
        printBtn.addEventListener('click', () => window.print());
        tdPrint.appendChild(printBtn);
        tr.appendChild(tdPrint);

        mainTable.appendChild(tr);

        // -------------------------------
        // 패널 닫기 + 입력값 리셋
        // -------------------------------
        panel.classList.remove('open');
        panelForm.reset();
    });

    // ==========================
    // 작업 지시 상세
    // ==========================
        if (mainTable && panelDown) {
        // 체크박스 클릭 문제 해결 1
        const existingCheckboxes = mainTable.querySelectorAll('input[type="checkbox"]');
        existingCheckboxes.forEach(function(cb) {
            cb.addEventListener('click', function(evt) {
                evt.stopPropagation();
            });
        });

        // 클릭 인식
        mainTable.addEventListener('click', function(e) {
            // 클릭한 요소로부터 가장 가까운 행 찾기
            const row = e.target.closest('tr.data');
            if (!row) return; // tr이 아니면 종료

            // 체크박스 영역 클릭 시 상세 열기 중단
            // 모달로 이동할 때도 이러더니 여기서도 또...
            // - e.target 자체가 checkbox일 때
            if (e.target.closest('input[type="checkbox"]')) return;

            // - 클릭한 td 내부에 checkbox가 있는 경우도 차단
            const clickedTd = e.target.closest('td');
            if (clickedTd && clickedTd.querySelector('input[type="checkbox"]')) return;

            // panelDown 내부의 상세 필드(두 번째 td들)를 선택
            const modalValueCells = panelDown.querySelectorAll('.modal-table .modal-table-con');

            // 체크박스 제외하고 데이터 뽑기
            const rowTds = Array.from(row.querySelectorAll('td')).slice(1);

            for (let i = 0; i < modalValueCells.length; i++) {
                const valueCell = modalValueCells[i];
                const correspondingTd = rowTds[i];
                if (correspondingTd) {
                    valueCell.textContent = correspondingTd.textContent.trim();
                } else {
                    valueCell.textContent = '';
                }
            }
            panelDown.classList.add('open');
        });

        // 닫기
        const panelDownCloseBtn = panelDown.querySelector('.close-btn');
        if (panelDownCloseBtn) {
            panelDownCloseBtn.addEventListener('click', function() {
                panelDown.classList.remove('open');
            });
        }
        panelDown.addEventListener('click', function(evt) {
            if (evt.target === panelDown) {
                panelDown.classList.remove('open');
            }
        });
    }
}


        // // ===============================
        // // 작업지시번호 input 범위 제한 + inline 경고 메시지
        // // ===============================
        // const orderNoInput = panelForm.querySelector('input[name="order-no"]');
        // if (orderNoInput) {
        //     // 메시지 요소 생성
        //     let msgEl = document.createElement('div');
        //     msgEl.style.color = 'red';
        //     msgEl.style.fontSize = '0.85em';
        //     msgEl.style.marginTop = '2px';
        //     msgEl.style.display = 'none'; // 초기에는 숨김
        //     orderNoInput.insertAdjacentElement('afterend', msgEl);

        //     orderNoInput.addEventListener('input', () => {
        //         const value = parseInt(orderNoInput.value, 10);

        //         if (isNaN(value) || orderNoInput.value === '') {
        //             msgEl.style.display = 'none'; // 비거나 숫자가 아니면 메시지 숨김
        //             return;
        //         }

        //         if (value < 1 || value > 5) {
        //             msgEl.textContent = '1~5까지의 값만 입력 가능합니다.';
        //             msgEl.style.display = 'block';
        //             orderNoInput.value = ''; // 입력란 초기화
        //         } else {
        //             msgEl.style.display = 'none'; // 정상 범위면 메시지 숨김
        //         }
        //     });
        // }

    };

    // ===============================
    // 생산 계획
    // ===============================
    const initProduction = () => {
    const panelForm = document.querySelector('.panel form');
    const mainTable = document.querySelector('.wrap-table tbody');
    const panel = document.querySelector('.panel');
    const saveBtn = document.querySelector('.panel .panel-save'); // ← panel-save 클래스 맞춰 수정
    const panelDown = document.querySelector('#panel-down');

    if (saveBtn && mainTable && panelForm) {
        saveBtn.addEventListener('click', (e) => {
            e.preventDefault();
            
            // 입력 값 모음
            const proPlanNo = panelForm.querySelector('input[name="pro-plan-no"]').value;
            const itemNo = panelForm.querySelector('input[name="item-no"]').value;
            const planAmount = panelForm.querySelector('input[name="plan-amount"]').value;
            const planStart = panelForm.querySelector('input[name="plan-start"]').value;
            const planEnd = panelForm.querySelector('input[name="plan-end"]').value;
            const workSeq = panelForm.querySelector('select[name="work-seq"]').value;
            const bigo = panelForm.querySelector('input[name="bigo"]').value;

            // 행 생성
            const tr = document.createElement('tr');
            tr.classList.add('data'); // 모달 연동용 클래스

            // 체크박스
            const tdCheckbox = document.createElement('td');
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            tdCheckbox.appendChild(checkbox);
            tr.appendChild(tdCheckbox);

            // 생산계획번호
            const tdPN = document.createElement('td');
            tdPN.textContent = proPlanNo;
            tr.appendChild(tdPN);

            // 제품번호
            const tdIN = document.createElement('td');
            tdIN.textContent = itemNo;
            tr.appendChild(tdIN);

            // 계획 수량
            const tdPA = document.createElement('td');
            tdPA.textContent = planAmount;
            tr.appendChild(tdPA);

            // 계획 기간
            const tdDate = document.createElement('td');
            if (planStart && planEnd) {
                tdDate.textContent = `${planStart} ~ ${planEnd}`;
            }
            tr.appendChild(tdDate);

            // 진행률 (빈 칸)
            tr.appendChild(document.createElement('td'));

            // 달성률 (빈 칸)
            tr.appendChild(document.createElement('td'));

            // 불량률 (빈 칸)
            tr.appendChild(document.createElement('td'));

            // 비고
            const tdB = document.createElement('td');
            tdB.textContent = bigo;
            tr.appendChild(tdB);

            // 행 추가
            mainTable.appendChild(tr);
            // 새로 추가된 체크박스에 이벤트 연결
            tr.querySelector('input[type="checkbox"]').addEventListener('click', e => e.stopPropagation());

            // 패널 닫기 및 리셋
            panel.classList.remove('open');
            panelForm.reset();
        });
    }

    // 메인 테이블 행 클릭 -> 상세 모달 열기. 이건 이제 등록 슬라이드보다 낮은 z-index 위치에서 열게 할 예정.
    // 현재 modalOverlay만 panel-down으로 작성하다 맒.
    // panel-down은 그냥 panel보다 z-index가 낮게 있는 것.
      // 메인 테이블 행 클릭 → 상세 패널(#panel-down) 열기
    if (mainTable && panelDown) {
        // 체크박스 클릭 문제 해결 1
        const existingCheckboxes = mainTable.querySelectorAll('input[type="checkbox"]');
        existingCheckboxes.forEach(function(cb) {
            cb.addEventListener('click', function(evt) {
                evt.stopPropagation();
            });
        });

        // 클릭 인식
        mainTable.addEventListener('click', function(e) {
            // 클릭한 요소로부터 가장 가까운 행 찾기
            const row = e.target.closest('tr.data');
            if (!row) return; // tr이 아니면 종료

            // 체크박스 영역 클릭 시 상세 열기 중단
            // 모달로 이동할 때도 이러더니 여기서도 또...
            // - e.target 자체가 checkbox일 때
            if (e.target.closest('input[type="checkbox"]')) return;

            // - 클릭한 td 내부에 checkbox가 있는 경우도 차단
            const clickedTd = e.target.closest('td');
            if (clickedTd && clickedTd.querySelector('input[type="checkbox"]')) return;

            // panelDown 내부의 상세 필드(두 번째 td들)를 선택
            const modalValueCells = panelDown.querySelectorAll('.modal-table .modal-table-con');

            // 체크박스 제외하고 데이터 뽑기
            const rowTds = Array.from(row.querySelectorAll('td')).slice(1);

            for (let i = 0; i < modalValueCells.length; i++) {
                const valueCell = modalValueCells[i];
                const correspondingTd = rowTds[i];
                if (correspondingTd) {
                    valueCell.textContent = correspondingTd.textContent.trim();
                } else {
                    valueCell.textContent = '';
                }
            }
            panelDown.classList.add('open');
        });

        // 닫기
        const panelDownCloseBtn = panelDown.querySelector('.close-btn');
        if (panelDownCloseBtn) {
            panelDownCloseBtn.addEventListener('click', function() {
                panelDown.classList.remove('open');
            });
        }
        panelDown.addEventListener('click', function(evt) {
            if (evt.target === panelDown) {
                panelDown.classList.remove('open');
            }
        });
    }

    };

    // ===============================
    // 초기화
    // ===============================
    initPanel();
    initSelectAll();
    initTableDelete();

    if (page === 'wo') initWorkOrder();
    if (page === 'pro-plan') initProduction();
});
