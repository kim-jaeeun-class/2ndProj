// 공통 JS : 사이드 패널, 모달, 테이블 삭제, 자동완성
document.addEventListener("DOMContentLoaded", () => {

    // 공통 JS : 사이드 패널 열기/닫기
    const openBtns = document.querySelectorAll('.open-btn');
    const panel = document.querySelector('.panel');
    const closeBtn = panel.querySelector('.close-btn');
    const cancelBtn = panel.querySelector('.cancel');

    openBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            panel.classList.add('open');
        });
    });

    if (closeBtn) closeBtn.addEventListener('click', () => panel.classList.remove('open'));
    if (cancelBtn) cancelBtn.addEventListener('click', () => panel.classList.remove('open'));

    // 공통 JS : 체크박스 전체 선택
    const selectAllCheckbox = document.querySelector('.select-all');
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', e => {
            const checkboxes = document.querySelectorAll('input[type="checkbox"]');
            checkboxes.forEach(cb => cb.checked = e.target.checked);
        });
    }

    // 공통 JS : 테이블 행 삭제 (메인 테이블 및 생산 실적 테이블)
    const mainDeleteBtns = document.querySelectorAll('.wrap-table .delete-btn, .wrap-table .delete');
    mainDeleteBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const table = btn.closest('.wrap-table').querySelector('table');
            if (!table) return;
            const checked = table.querySelectorAll('tbody input[type="checkbox"]:checked');
            checked.forEach(cb => cb.closest('tr').remove());
        });
    });

    // 공통 JS : 납품처 자동완성
    const clients = [
        { code: "C001", name: "삼성전자" },
        { code: "C002", name: "LG화학" },
        { code: "C003", name: "현대자동차" },
        { code: "C004", name: "SK하이닉스" }
    ];

    const autocompleteInput = document.querySelector('.autocomplete');
    const suggestionBox = document.querySelector('.suggestions');
    const clientNameInput = document.querySelector('.client-name');

    if (autocompleteInput) {
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

    // 공통 JS : 품목 추가 모달
    const itemAddBtn = document.querySelector('.panel .button[value="품목 추가"]');
    const modalOverlay = document.querySelector('.modal-overlay');
    const modalCloseBtn = document.querySelector('.modal-close');

    if (itemAddBtn && modalOverlay && modalCloseBtn) {
        itemAddBtn.addEventListener('click', () => {
            modalOverlay.classList.add('active');
        });

        modalCloseBtn.addEventListener('click', () => {
            modalOverlay.classList.remove('active');
        });

        modalOverlay.addEventListener('click', (e) => {
            if (e.target === modalOverlay) {
                modalOverlay.classList.remove('active');
            }
        });
    }

    // 공통 JS : 모달 내 전체 선택 해제
    const modalResetBtn = document.querySelector('.modal .reset');
    if (modalResetBtn) {
        modalResetBtn.addEventListener('click', () => {
            const modalCheckboxes = modalOverlay.querySelectorAll('tbody input[type="checkbox"]');
            modalCheckboxes.forEach(cb => cb.checked = false);
        });
    }

    // 공통 JS : 슬라이딩 영역(패널) 테이블
    const panelTableBody = document.querySelector('.panel-table tbody');

    // 공통 JS : 모달 저장 -> 패널 테이블에 품목 추가 (중복 방지)
    const modalSaveBtn = document.querySelector('.modal .save');
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

    // 공통 JS : 패널 테이블 품목 삭제
    const panelDeleteBtn = document.querySelector('.panel .delete');
    if (panelDeleteBtn) {
        panelDeleteBtn.addEventListener('click', () => {
            const checkedRows = panelTableBody.querySelectorAll('input[type="checkbox"]:checked');
            checkedRows.forEach(cb => cb.closest('tr').remove());

            // 순번 재정렬
            Array.from(panelTableBody.querySelectorAll('tr')).forEach((tr, idx) => {
                tr.children[1].textContent = idx + 1;
            });
        });
    }

});
