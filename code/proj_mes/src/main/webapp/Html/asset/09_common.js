// ===============================
// 공통 JS : 페이지별 기능 통합
// ===============================
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
        const mainDeleteBtns = document.querySelectorAll('.wrap-table .delete-btn');
        mainDeleteBtns.forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const table = btn.closest('.wrap-table').querySelector('table');
                if (!table) return;
                const checked = table.querySelectorAll('tbody input[type="checkbox"]:checked');
                if (checked.length === 0) {
                    alert('삭제할 항목을 선택해주세요.');
                    return;
                }
                if (!confirm('선택한 항목을 삭제하시겠습니까?')) return;

                const form = document.createElement('form');
                form.method = 'post';
                form.action = 'proplan';

                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'delete';
                form.appendChild(actionInput);

                checked.forEach(cb => {
                    const input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = 'chk';
                    input.value = cb.value;
                    form.appendChild(input);
                });

                document.body.appendChild(form);
                form.submit();
            });
        });
    };

    // ===============================
    // 작업 지시서 페이지 기능
    // ===============================
    const initWorkOrder = () => {
        const panelForm = document.querySelector('.panel form');
        const mainTable = document.querySelector('.wrap-table tbody');
        const panel = document.querySelector('.panel');
        const saveBtn = panel.querySelector('.panel-save');
        const panelDown = document.querySelector('#panel-down');

        if (!panelForm || !mainTable || !saveBtn) return;

        // -------------------------------
        // 슬라이딩 저장 → 메인 테이블 추가
        // -------------------------------
        saveBtn.addEventListener('click', (e) => {
            e.preventDefault();

            const orderDateInput = panelForm.querySelector('input[name="order-date"]');
            const orderNo = panelForm.querySelector('input[name="client-code"]');
            const person = panelForm.querySelector('input[name="client-name"]');
            const personInput = panelForm.querySelector('input[name="person"]');
            const duedate = panelForm.querySelector('input[name="give-date"]');
            const orderNoInput = panelForm.querySelector('input[type="number"]');

            const requiredInputs = [orderDateInput, orderNo, person, personInput, duedate, orderNoInput];
            let allValid = true;

            requiredInputs.forEach(input => {
                if (!input.value) allValid = false;
            });
            if (!allValid) { alert('필수 항목을 모두 입력해주세요.'); return; }

            if (orderNoInput.value < 1 || orderNoInput.value > 5) {
                alert('주문번호는 1~5까지 입력 가능합니다.');
                return;
            }

            if (new Date(orderDateInput.value) > new Date(duedate.value)) {
                alert('지시일이 납기일보다 늦을 수 없습니다.');
                return;
            }

            // // 테이블 행 생성
            // const tr = document.createElement('tr');
            // tr.classList.add('data');

            // // 체크박스
            // const tdCheckbox = document.createElement('td');
            // const checkbox = document.createElement('input');
            // checkbox.type = 'checkbox';
            // tdCheckbox.appendChild(checkbox);
            // tr.appendChild(tdCheckbox);

            // // 작업지시번호
            // const tdWoNo = document.createElement('td');
            // tdWoNo.textContent = `${orderDateInput.value.replace(/-/g,'')}-${orderNoInput.value}`;
            // tr.appendChild(tdWoNo);

            // // 일자
            // const tdDate = document.createElement('td');
            // tdDate.textContent = new Date().toISOString().split('T')[0];
            // tr.appendChild(tdDate);

            // // 거래처명
            // const tdClient = document.createElement('td');
            // tdClient.textContent = person.value;
            // tr.appendChild(tdClient);

            // // 담당자명
            // const tdPerson = document.createElement('td');
            // tdPerson.textContent = personInput.value;
            // tr.appendChild(tdPerson);

            // // 납기일
            // const tdGive = document.createElement('td');
            // tdGive.textContent = duedate.value;
            // tr.appendChild(tdGive);

            // // 빈 행 (품목 등)
            // tr.appendChild(document.createElement('td'));
            // tr.appendChild(document.createElement('td'));
            // tr.appendChild(document.createElement('td'));

            // mainTable.appendChild(tr);
            // checkbox.addEventListener('click', e => e.stopPropagation());

            // 패널 닫기 + 폼 리셋
            panel.classList.remove('open');
            panelForm.reset();
        });

        // -------------------------------
        // 메인 테이블 클릭 → 상세 패널
        // -------------------------------
        if (mainTable && panelDown) {
            mainTable.addEventListener('click', (e) => {
                const row = e.target.closest('tr.data');
                if (!row || e.target.closest('input[type="checkbox"]')) return;

                const modalValueCells = panelDown.querySelectorAll('.modal-table .modal-table-con');
                const rowTds = Array.from(row.querySelectorAll('td')).slice(1);

                rowTds.forEach((td, i) => {
                    if (modalValueCells[i]) modalValueCells[i].textContent = td.textContent.trim();
                });

                const hiddenInput = panelDown.querySelector('#detail-delete-id');
                hiddenInput.value = row.querySelector('td:nth-child(2)').textContent.trim();

                panelDown.classList.add('open');
            });

            const panelDownCloseBtn = panelDown.querySelector('.close-btn');
            if (panelDownCloseBtn) panelDownCloseBtn.addEventListener('click', () => panelDown.classList.remove('open'));
            panelDown.addEventListener('click', evt => { if (evt.target === panelDown) panelDown.classList.remove('open'); });
        }
    };

    // ===============================
    // 생산 계획 페이지
    // ===============================
    const initProduction = () => {
        const panelForm = document.querySelector('.panel form');
        const saveBtn = document.querySelector('.panel .panel-save');
        const panel = document.querySelector('.panel');
        const mainTable = document.querySelector('.wrap-table tbody');
        const panelDown = document.querySelector('#panel-down');
        let editingRow = null; // 수정 중인 행 저장

        if (!panelForm || !saveBtn || !mainTable) return;

        // ===============================
        // 등록/수정 버튼 클릭
        // ===============================
        saveBtn.addEventListener('click', (e) => {
            e.preventDefault();

            const itemNo = panelForm.querySelector('input[name="item-no"]');
            const planAmount = panelForm.querySelector('input[name="plan-amount"]');
            const planStart = panelForm.querySelector('input[name="plan-start"]');
            const planEnd = panelForm.querySelector('input[name="plan-end"]');
            const bigo = panelForm.querySelector('input[name="bigo"]');

            // 필수값 체크
            if (!itemNo.value || !planAmount.value || !planStart.value || !planEnd.value) {
                alert('필수 항목을 모두 입력해주세요.');
                return;
            }
            if (new Date(planStart.value) > new Date(planEnd.value)) {
                alert('계획 시작일은 종료일보다 늦을 수 없습니다.');
                return;
            }

            // 수정 모드라면 hidden에 수정 대상 ID 삽입
            if (editingRow) {
                let hiddenId = panelForm.querySelector('input[name="cpID"]');
                if (!hiddenId) {
                    hiddenId = document.createElement('input');
                    hiddenId.type = 'hidden';
                    hiddenId.name = 'cpID';
                    panelForm.appendChild(hiddenId);
                }
                hiddenId.value = editingRow.dataset.cpId;
            }

            // 서버 전송
            panelForm.submit();
        });

        // ===============================
        // 메인 테이블 클릭 → 상세 패널
        // ===============================
        if (mainTable && panelDown) {
            mainTable.addEventListener('click', (e) => {
                const row = e.target.closest('tr.data');
                if (!row || e.target.closest('input[type="checkbox"]')) return;

                const modalValueCells = panelDown.querySelectorAll('.modal-table .modal-table-con');
                const rowTds = Array.from(row.querySelectorAll('td')).slice(1);

                rowTds.forEach((td, i) => {
                    if (modalValueCells[i]) modalValueCells[i].textContent = td.textContent.trim();
                });

                const hiddenInput = panelDown.querySelector('#detail-delete-id');
                hiddenInput.value = row.dataset.cpId || row.querySelector('td:nth-child(2)').textContent.trim();

                panelDown.classList.add('open');

                // 수정 버튼
                const editBtn = panelDown.querySelector('.edit');
                if (editBtn) {
                    editBtn.onclick = () => {
                        panelDown.classList.remove('open');
                        panel.classList.add('open');

                        // 기존 데이터 input 채우기
                        panelForm.querySelector('input[name="item-no"]').value = rowTds[1]?.textContent.trim() || '';
                        panelForm.querySelector('input[name="plan-amount"]').value = rowTds[2]?.textContent.trim() || '';
                        const dateRange = rowTds[3]?.textContent.trim().split(' ~ ') || [];
                        panelForm.querySelector('input[name="plan-start"]').value = dateRange[0] || '';
                        panelForm.querySelector('input[name="plan-end"]').value = dateRange[1] || '';
                        panelForm.querySelector('input[name="bigo"]').value = rowTds[7]?.textContent.trim() || '';

                        // 수정 대상 row 저장
                        editingRow = row;
                    };
                }
            });

            const panelDownCloseBtn = panelDown.querySelector('.close-btn');
            if (panelDownCloseBtn) panelDownCloseBtn.addEventListener('click', () => panelDown.classList.remove('open'));
            panelDown.addEventListener('click', evt => { if (evt.target === panelDown) panelDown.classList.remove('open'); });

            // 상세 삭제
            const deleteBtn = panelDown.querySelector('.delete');
            if (deleteBtn) {
                deleteBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    const cpID = panelDown.querySelector('#detail-delete-id').value;
                    if (!cpID) return;
                    if (!confirm('정말 이 항목을 삭제하시겠습니까?')) return;

                    const form = document.createElement('form');
                    form.method = 'post';
                    form.action = 'proplan';

                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'delete';
                    form.appendChild(actionInput);

                    const idInput = document.createElement('input');
                    idInput.type = 'hidden';
                    idInput.name = 'cpID';
                    idInput.value = cpID;
                    form.appendChild(idInput);

                    document.body.appendChild(form);
                    form.submit();
                });
            }
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
