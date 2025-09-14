// ===============================
// 공통 JS : 페이지별 기능 통합
// ===============================
document.addEventListener("DOMContentLoaded", () => {
    const page = document.body.getAttribute('page');

    // ===============================
    // 공통 기능 : 사이드 패널 열기/닫기
    // ===============================
    document.querySelectorAll('.open-btn').forEach(btn => {
        btn.addEventListener('click', () => document.querySelector('.panel').classList.add('open'));
    });
    document.querySelectorAll('.close-btn').forEach(btn => {
        btn.addEventListener('click', () => btn.closest('.panel').classList.remove('open'));
    });

    // ===============================
    // 공통 기능 : 체크박스 전체 선택
    // ===============================
    const selectAll = document.querySelector('.select-all');
    if (selectAll) {
        selectAll.addEventListener('change', e => {
            document.querySelectorAll('.wrap-table tbody input[type="checkbox"]').forEach(cb => cb.checked = e.target.checked);
        });
    }

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

                if (page === 'pro-plan') {
                    form.action = 'proplan';
                } else if (page === 'wo') {
                    form.action = 'workorder';
                }

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
        const panelForm = document.querySelector('#form-add');
        const mainTable = document.querySelector('.wrap-table tbody');
        const panel = document.querySelector('#panel-add');
        const panelAdd = document.querySelector('#panel-add');
        const panelDown = document.querySelector('#panel-down');

        if (!panelForm || !mainTable || !panelAdd || !panelDown) {
            return;
        }
        
        const saveBtn = panelForm.querySelector('.panel-save');
        const hiddenBOM = panelForm.querySelector('input[name="bom_id"]');
        const hiddenPROC = panelForm.querySelector('input[name="proc_id"]');

        const actionInput = panelForm.querySelector('#action-input');
        const woNumHidden = panelForm.querySelector('#wo-num-hidden');
        const panelTitle = document.querySelector('#panel-title-mode');
        const woDateInput = panelForm.querySelector('#wo-date-input');

        // -------------------------------
        // 저장 버튼 클릭
        // -------------------------------
        saveBtn.addEventListener('click', (e) => {
            e.preventDefault();

            const woDate = panelForm.querySelector('input[name="wo_date"]');
            const woDuedate = panelForm.querySelector('input[name="wo_duedate"]');
            const workerId = panelForm.querySelector('input[name="worker_id"]');
            const woPQ = panelForm.querySelector('input[name="wo_pq"]');
            const itemCode = panelForm.querySelector('input[name="item_code"]:checked');

            // 필수 항목 체크
            if (!woDate.value || !woDuedate.value || !workerId.value || !woPQ.value || !itemCode) {
                alert('필수 항목을 모두 입력해주세요.');
                return;
            }

            // 수량 체크
            if (parseInt(woPQ.value) < 1) {
                alert('수량은 1 이상 입력해야 합니다.');
                return;
            }

            // 지시일/납기일 체크
            if (new Date(woDate.value) > new Date(woDuedate.value)) {
                alert('지시일이 납기일보다 늦을 수 없습니다.');
                return;
            }

            // 수정/등록 액션 분기
            if (actionInput.value === 'update') {
                const confirmed = confirm('수정하시겠습니까?');
                if (!confirmed) return;
            }

            // 선택된 품목에 따른 BOM/PROC hidden 세팅
            hiddenBOM.value = itemCode.dataset.bomId || '0';
            hiddenPROC.value = itemCode.dataset.procId || '0';

            // 서버에 submit
            panelForm.submit();
            panel.classList.remove('open');
        });

        // -------------------------------
        // 메인 테이블 클릭 → 상세 패널
        // -------------------------------
        if (mainTable) {
            mainTable.addEventListener('click', (e) => {
                // `a` 태그의 기본 동작(페이지 이동)을 막습니다.
                const link = e.target.closest('a');
                if (link) {
                    e.preventDefault();
                }
                
                const row = e.target.closest('tr.data');
                // 체크박스 클릭 제외 로직
                if (!row || e.target.closest('input[type="checkbox"]')) return;

                // 클릭된 행의 작업지시번호를 가져옵니다.
                const woNum = row.querySelector('.wo-link').textContent.trim();

                location.href = `workorder?wo_num=${woNum}`;

            });
        }

        // -------------------------------
        // 상세 패널에서 '수정' 버튼 클릭
        // -------------------------------
        const editAllBtn = panelDown.querySelector('.edit-all-btn');
        if (editAllBtn) {
            editAllBtn.addEventListener('click', (e) => {
                e.preventDefault();
                const woNum = row
                    .querySelector('.wo-link')
                    .textContent
                    .trim();
                location.href = `workorder?wo_num=${woNum}`;
                if (!woNum) {
                    alert('수정할 항목이 선택되지 않았습니다.');
                    return;
                }
            });
        }


        // -------------------------------
        // '등록' 버튼 클릭 시 초기화
        // -------------------------------
        const openAddBtn = document.querySelector('.open-btn');
        openAddBtn.addEventListener('click', () => {
            panelForm.reset();
            actionInput.value = 'add';
            woNumHidden.value = '';
            woDateInput.readOnly = false;
            document.querySelector('.slide-title').textContent = '작업 지시서 등록';
            panelDown.classList.remove('open');
            panelAdd.classList.add('open');
        });

    // -------------------------------
    // 생산수량 입력 버튼 클릭
    // -------------------------------
    const editAqBtn = panelDown.querySelector('#edit-aq-btn');
    const aqDisplay = panelDown.querySelector('.production-quantity-display');
    const editAqArea = panelDown.querySelector('.edit-aq-area');
    const completeAqBtn = panelDown.querySelector('.complete-aq-btn');
    const editAqInput = panelDown.querySelector('#edit-aq-input');

    if (editAqBtn) {
        editAqBtn.addEventListener('click', () => {
            // 입력 모드로 전환
            aqDisplay.style.display = 'none';
            editAqArea.style.display = 'block';
            editAqInput.value = aqDisplay.textContent.trim();
        });
    }

    // -------------------------------
    // 생산수량 입력 '완료' 버튼 클릭
    // -------------------------------
    if (completeAqBtn) {
        completeAqBtn.addEventListener('click', () => {
            const woNum = document.querySelector('#detail-wo-num').value;
            const newAq = editAqInput.value;

            if (newAq === "" || isNaN(newAq) || parseInt(newAq) < 0) {
                alert('유효한 생산 수량을 입력해주세요.');
                return;
            }

            const form = document.createElement('form');
            form.method = 'post';
            form.action = 'workorder';

            const action = document.createElement('input');
            action.type = 'hidden';
            action.name = 'action';
            action.value = 'editAQ';
            form.appendChild(action);

            const num = document.createElement('input');
            num.type = 'hidden';
            num.name = 'wo_num';
            num.value = woNum;
            form.appendChild(num);

            const aq = document.createElement('input');
            aq.type = 'hidden';
            aq.name = 'wo_aq';
            aq.value = newAq;
            form.appendChild(aq);

            document.body.appendChild(form);
            form.submit();
        });

    }

    // -------------------------------
    // 상세 패널 닫기
    // -------------------------------
    const panelDownCloseBtn = panelDown.querySelector('.close-btn');
    if (panelDownCloseBtn) panelDownCloseBtn.addEventListener('click', () => panelDown.classList.remove('open'));
    panelDown.addEventListener('click', evt => { if (evt.target === panelDown) panelDown.classList.remove('open'); });
}

    // ===============================
    // 생산 계획 페이지
    // ===============================
    const initProduction = () => {
        const panelForm = document.querySelector('.panel form');
        const saveBtn = document.querySelector('.panel .panel-save');
        const panel = document.querySelector('.panel');
        const mainTable = document.querySelector('.wrap-table tbody');
        const panelDown = document.querySelector('#panel-down');
        let editingRow = null;

        if (!panelForm || !saveBtn || !mainTable) return;

        saveBtn.addEventListener('click', (e) => {
            e.preventDefault();

            const itemNo = panelForm.querySelector('input[name="item-no"]');
            const planAmount = panelForm.querySelector('input[name="plan-amount"]');
            const planStart = panelForm.querySelector('input[name="plan-start"]');
            const planEnd = panelForm.querySelector('input[name="plan-end"]');
            const bigo = panelForm.querySelector('input[name="bigo"]');

            if (!itemNo.value || !planAmount.value || !planStart.value || !planEnd.value) {
                alert('필수 항목을 모두 입력해주세요.');
                return;
            }
            if (new Date(planStart.value) > new Date(planEnd.value)) {
                alert('계획 시작일은 종료일보다 늦을 수 없습니다.');
                return;
            }

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

            panelForm.submit();
        });
    };

    // ===============================
    // 초기화
    // ===============================
    initTableDelete();
    if (page === 'wo') initWorkOrder();
    if (page === 'pro-plan') initProduction();
});