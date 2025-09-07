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
        // 납품처 자동완성
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

        // 품목 추가 모달
        const itemAddBtn = document.querySelector('.panel .button[value="품목 추가"]');
        const modalOverlay = document.querySelector('.modal-overlay');
        const modalCloseBtn = document.querySelector('.modal-close');
        const modalResetBtn = document.querySelector('.modal .reset');
        const modalSaveBtn = document.querySelector('.modal .save');
        const panelTableBody = document.querySelector('.panel-table tbody');

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

        if (modalSaveBtn && panelTableBody && modalOverlay) {
            modalSaveBtn.addEventListener('click', () => {
                const checkedRows = modalOverlay.querySelectorAll('tbody input[type="checkbox"]:checked');
                checkedRows.forEach(rowCheckbox => {
                    const row = rowCheckbox.closest('tr');
                    const code = row.children[1].textContent;
                    const name = row.children[2].textContent;

                    // 중복 체크
                    const exists = Array.from(panelTableBody.children).some(tr => tr.children[2].textContent === code);
                    if (exists) return;

                    const tr = document.createElement('tr');

                    const tdCheckbox = document.createElement('td');
                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    tdCheckbox.appendChild(checkbox);
                    tr.appendChild(tdCheckbox);

                    const tdNo = document.createElement('td');
                    tdNo.textContent = panelTableBody.children.length + 1;
                    tr.appendChild(tdNo);

                    const tdCode = document.createElement('td');
                    tdCode.textContent = code;
                    tr.appendChild(tdCode);

                    const tdName = document.createElement('td');
                    tdName.textContent = name;
                    tr.appendChild(tdName);

                    // ===============================
                    // 수량 / 비고 추가 (빈칸)
                    // ===============================
                    const tdQty = document.createElement('td');
                    tdQty.textContent = '';
                    tr.appendChild(tdQty);

                    const tdNote = document.createElement('td');
                    tdNote.textContent = '';
                    tr.appendChild(tdNote);

                    panelTableBody.appendChild(tr);
                });

                modalOverlay.classList.remove('active');
                checkedRows.forEach(cb => cb.checked = false);
            });
        }
    };

    // ===============================
    // 생산 계획 / 생산 실적 페이지 기능
    // ===============================
    const initProduction = () => {
        const panelTableBody = document.querySelector('.panel-table tbody');
        const mainTableBody = document.querySelector('.wrap-table tbody');
        if (!panelTableBody || !mainTableBody) return;

        const saveBtn = document.querySelector('.panel .save');
        if (saveBtn) {
            saveBtn.addEventListener('click', () => {
                const panelForm = document.querySelector('.panel form');
                if (!panelForm) return;

                // 입력값 가져오기
                const wo = panelForm.querySelector('input[name="wo-num"]')?.value || '';
                const statusRadio = panelForm.querySelector('input[name="pro-perf-stat"]:checked');
                const status = statusRadio ? statusRadio.parentElement.textContent.trim() : '';
                const date = new Date().toISOString().split('T')[0];
                const lot = panelForm.querySelector('input[name="lot-num"]')?.value || '';
                const inFormRadio = panelForm.querySelector('input[name="in-form"]:checked');
                const inForm = inFormRadio ? inFormRadio.parentElement.textContent.trim() : '';
                const outFormRadio = panelForm.querySelector('input[name="out-form"]:checked');
                const outForm = outFormRadio ? outFormRadio.parentElement.textContent.trim() : '';

                // 메인 테이블 행 추가
                const tr = document.createElement('tr');

                const tdCheckbox = document.createElement('td');
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                tdCheckbox.appendChild(checkbox);
                tr.appendChild(tdCheckbox);

                const tdWo = document.createElement('td');
                tdWo.textContent = wo;
                tr.appendChild(tdWo);

                const tdStatus = document.createElement('td');
                tdStatus.textContent = status;
                tr.appendChild(tdStatus);

                const tdDate = document.createElement('td');
                tdDate.textContent = date;
                tr.appendChild(tdDate);

                const tdLot = document.createElement('td');
                tdLot.textContent = lot;
                tr.appendChild(tdLot);

                const tdIn = document.createElement('td');
                tdIn.textContent = inForm;
                tr.appendChild(tdIn);

                const tdOut = document.createElement('td');
                tdOut.textContent = outForm;
                tr.appendChild(tdOut);

                mainTableBody.appendChild(tr);

                // 패널 테이블 체크 해제 및 패널 닫기
                panelTableBody.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);
                document.querySelector('.panel').classList.remove('open');
            });
        }
        // 메인 테이블 행 클릭 -> 상세 모달 열기
        const modalOverlay = document.querySelector('.modal-overlay');

        if(mainTableBody && modalOverlay) {
        mainTableBody.querySelectorAll('tr.data').forEach(row => {
            row.addEventListener('click', () => {
                const modal = modalOverlay.querySelector('.modal');
                const modalCells = modal.querySelectorAll('table tr td:nth-child(2)');

                const rowData = Array.from(row.children).slice(1).map(td => td.textContent); 
                // slice(1) -> 첫 번째 체크박스 제외

                // modalCells 순서대로 데이터 채우기
                rowData.forEach((val, i) => {
                    if(modalCells[i]) modalCells[i].textContent = val;
                });

                modalOverlay.classList.add('active');
            });
        });

        // 모달 닫기
        modalOverlay.querySelector('.modal-close').addEventListener('click', () => {
            modalOverlay.classList.remove('active');
        });

        // 모달 외부 클릭 시 닫기
        modalOverlay.addEventListener('click', (e) => {
            if(e.target === modalOverlay) modalOverlay.classList.remove('active');
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
    if (page === 'pro-plan' || page === 'pro-perf') initProduction();
});
