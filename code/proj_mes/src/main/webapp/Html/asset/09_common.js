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
                const checkedRows = modalOverlay.querySelectorAll('tbody input[type="checkbox"]:checked');
                checkedRows.forEach(rowCheckbox => {
                    const row = rowCheckbox.closest('tr');
                    const code = row.children[1].textContent;
                    const name = row.children[2].textContent;

                    // 중복 체크
                    const exists = Array.from(panelTable.children).some(tr => tr.children[2].textContent === code);
                    if (exists) return;

                    const tr = document.createElement('tr');

                    const tdCheckbox = document.createElement('td');
                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    tdCheckbox.appendChild(checkbox);
                    tr.appendChild(tdCheckbox);

                    const tdNo = document.createElement('td');
                    tdNo.textContent = panelTable.children.length + 1;
                    tr.appendChild(tdNo);

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

                    const tdNote = document.createElement('td');
                    tdNote.textContent = '';
                    tr.appendChild(tdNote);

                    panelTable.appendChild(tr);
                });

                modalOverlay.classList.remove('active');
                checkedRows.forEach(cb => cb.checked = false);
            });
        }

        // ===============================
        // 슬라이딩 입력 → 메인 테이블 추가
        // ===============================
        const panelForm = document.querySelector('.panel form');
        const mainTable = document.querySelector('.wrap-table tbody');
        const panel = document.querySelector('.panel');
        const saveBtn = document.querySelector('.panel .panel-save');

        if (saveBtn && mainTable && panelForm) {
            saveBtn.addEventListener('click', (e) => {
                e.preventDefault();

                // -------------------------------
                // 입력값 수집
                // -------------------------------
                const orderDate = panelForm.querySelector('input[name="order-date"]').value;
                const clientCode = panelForm.querySelector('input[name="client-code"]').value;
                const clientName = panelForm.querySelector('input[name="client-name"]').value;
                const person = panelForm.querySelector('input[name="person"]').value;
                const giveDate = panelForm.querySelector('input[name="give-date"]').value;
                const orderNo = panelForm.querySelector('input[type="number"]').value;

                const panelItems = panelForm.querySelectorAll('.panel-table tbody tr');

                // 품목명, 지시수량 총합 계산
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

                // 생산수량 (빈칸)
                tr.appendChild(document.createElement('td'));

                // 진행상태 (빈칸)
                tr.appendChild(document.createElement('td'));

                // 인쇄 버튼
                const tdPrint = document.createElement('td');
                const printBtn = document.createElement('button');
                printBtn.textContent = '인쇄';
                printBtn.addEventListener('click', () => window.print());
                tdPrint.appendChild(printBtn);
                tr.appendChild(tdPrint);

                // -------------------------------
                // 행 추가 및 패널 닫기/리셋
                // -------------------------------
                mainTable.appendChild(tr);
                panel.classList.remove('open');
                panelForm.reset();
            });
        }

        // ===============================
        // 메인 테이블 클릭 → 상세 모달 열기
        // ===============================
        if (mainTable && modalOverlay) {
            mainTable.addEventListener('click', (e) => {
                const row = e.target.closest('tr.data');
                if (!row) return;

                const modal = modalOverlay.querySelector('.modal');
                const modalCells = modal.querySelectorAll('table tr td:nth-child(2)');

                const rowData = Array.from(row.querySelectorAll('td')).slice(1).map(td => td.textContent.trim());
                rowData.forEach((val, i) => {
                    if (modalCells[i]) modalCells[i].textContent = val;
                });

                modalOverlay.classList.add('active');
            });

            modalOverlay.querySelector('.modal-close').addEventListener('click', () => {
                modalOverlay.classList.remove('active');
            });
            modalOverlay.addEventListener('click', (e) => {
                if (e.target === modalOverlay) modalOverlay.classList.remove('active');
            });
        }
    };


    // ===============================
    // 생산 실적 페이지 기능
    // ===============================
    const initProPerf = () => {
        const saveBtn = document.querySelector('.panel .panel-save');
        const mainTable = document.querySelector('.table-view table tbody');
        const panel = document.querySelector('.panel');
        const panelForm = document.querySelector('.panel form');

        if (saveBtn && mainTable && panelForm) {
            saveBtn.addEventListener('click', (e) => {
                e.preventDefault();

                // 값 수집
                let woNo = '';
                const woSelect = panelForm.querySelector('select[name="wo-num"]');
                if (woSelect) {
                    woNo = woSelect.value;
                }

                let stat = '';
                const statRadio = panelForm.querySelector('input[name="pro-perf-stat"]:checked');
                if (statRadio) {
                    stat = statRadio.parentElement.textContent.trim();
                }

                const date = new Date().toISOString().split('T')[0];

                let lotNo = '';
                const lotSelect = panelForm.querySelector('select[name="lot-num"]');
                if (lotSelect) {
                    lotNo = lotSelect.value;
                }

                let inR = '';
                const inRadio = panelForm.querySelector('input[name="in-form"]:checked');
                if (inRadio) {
                    inR = inRadio.parentElement.textContent.trim();
                }

                let outR = '';
                const outRadio = panelForm.querySelector('input[name="out-form"]:checked');
                if (outRadio) {
                    outR = outRadio.parentElement.textContent.trim();
                }

                // 행 생성
                const tr = document.createElement('tr');
                tr.classList.add('data'); // 모달 연동용 클래스

                const tdCheckbox = document.createElement('td');
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                tdCheckbox.appendChild(checkbox);
                tr.appendChild(tdCheckbox);

                const tdWo = document.createElement('td');
                tdWo.textContent = woNo;
                tr.appendChild(tdWo);

                const tdStat = document.createElement('td');
                tdStat.textContent = stat;
                tr.appendChild(tdStat);

                const tdDate = document.createElement('td');
                tdDate.textContent = date;
                tr.appendChild(tdDate);

                const tdLot = document.createElement('td');
                tdLot.textContent = lotNo;
                tr.appendChild(tdLot);

                const tdIn = document.createElement('td');
                tdIn.textContent = inR;
                tr.appendChild(tdIn);

                const tdOut = document.createElement('td');
                tdOut.textContent = outR;
                tr.appendChild(tdOut);

                mainTable.appendChild(tr);

                // 추가 후 처리
                panel.classList.remove('open');   // 패널 닫기
                panelForm.reset();                // 입력값 리셋
            });
        }
    };

    // ===============================
    // 생산 계획
    // ===============================
    const initProduction = () => {
    const panelForm = document.querySelector('.panel form');
    const mainTable = document.querySelector('.wrap-table tbody');
    const panel = document.querySelector('.panel');
    const saveBtn = document.querySelector('.panel .panel-save'); // ← panel-save 클래스 맞춰 수정
    
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

            // 순번 (행 개수 + 1)
            const tdNo = document.createElement('td');
            tdNo.textContent = mainTable.querySelectorAll('tr').length + 1;
            tr.appendChild(tdNo);

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

            // 진행 상태 (빈 칸)
            tr.appendChild(document.createElement('td'));

            // 계획 기간
            const tdDate = document.createElement('td');
            if (planStart && planEnd) {
                tdDate.textContent = `${planStart} ~ ${planEnd}`;
            }
            tr.appendChild(tdDate);

            // 진행률 (빈 칸)
            tr.appendChild(document.createElement('td'));

            // 작업 공정
            const tdWP = document.createElement('td');
            tdWP.textContent = workSeq;
            tr.appendChild(tdWP);

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

            // 패널 닫기 및 리셋
            panel.classList.remove('open');
            panelForm.reset();
        });
    }

    // 메인 테이블 행 클릭 -> 상세 모달 열기
    const modalOverlay = document.querySelector('.modal-overlay');
    if (mainTable && modalOverlay) {
        mainTable.addEventListener('click', (e) => {
            const row = e.target.closest('tr.data');
            if (!row) return;

            const modal = modalOverlay.querySelector('.modal');
            const modalCells = modal.querySelectorAll('table tr td:nth-child(2)');

            // 체크박스(td[0])는 제외 → 번호부터 마지막까지 가져오기
            const rowData = Array.from(row.querySelectorAll('td')).slice(2).map(td => td.textContent.trim());

            rowData.forEach((val, i) => {
                if (modalCells[i]) modalCells[i].textContent = val;
            });

            modalOverlay.classList.add('active');
        });

        modalOverlay.querySelector('.modal-close').addEventListener('click', () => {
            modalOverlay.classList.remove('active');
        });
        modalOverlay.addEventListener('click', (e) => {
            if (e.target === modalOverlay) modalOverlay.classList.remove('active');
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
    if (page === 'pro-perf') initProPerf();
});
