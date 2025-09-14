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
        const panel = document.querySelector('.panel');
        let saveBtn = null;
        const panelDown = document.querySelector('#panel-down');

        if (panelForm) {
            saveBtn = panelForm.querySelector('.panel-save');
        }
        if (!panelForm || !saveBtn) return;
        if (!panelForm || !mainTable || !panel || !panelDown) return;

        // -------------------------------
        // 작업지시 번호 자동 생성 함수
        // -------------------------------
        const woNum = panelForm.querySelector('input[name="wo_num"]');
        const woDate = panelForm.querySelector('input[name="wo_date"]');
        const woDuedate = panelForm.querySelector('input[name="wo_duedate"]');
        const workerId = panelForm.querySelector('input[name="worker_id"]');
        const woPQ = panelForm.querySelector('input[name="wo_pq"]');
        const itemCode = panelForm.querySelector('input[name="item_code"]:checked');
        const woNumHidden = panelForm.querySelector('input[name="wo_num_hidden"]');
        const woNumDisplay = panelForm.querySelector('input[name="wo_num_display"]'); // readonly 표시용

        async function setWoNum() {
            if (!woDate.value) return;
            const dateVal = woDate.value.replace(/-/g, '');
            try {
                const res = await fetch(`/workorder?action=getWoCount&date=${dateVal}`);
                const count = await res.json(); // 0~98
                const seq = String(count + 1).padStart(2, '0');
                const newWoNum = `${dateVal}-${seq}`;

                woNum.value = newWoNum;        // 실제 hidden에 저장
                woNumDisplay.value = newWoNum; // 화면 표시
            } catch (err) {
                console.error('작업지시번호 생성 실패:', err);
            }
        }

        woDate.addEventListener('change', setWoNum);
        if (woDate.value) setWoNum();

        // -------------------------------
        // 슬라이딩 저장 → 메인 테이블 추가
        // -------------------------------
        saveBtn.addEventListener('click', async (e) => {
            e.preventDefault(); // 기본 제출 막기

            // 작업지시 번호 fetch 완료
            await setWoNum();

            const itemCodeChecked = panelForm.querySelector('input[name="item_code"]:checked');
            const requiredInputs = [woNum, woDate, woDuedate, workerId, woPQ, itemCodeChecked];
            let allValid = true;
            requiredInputs.forEach(input => {
                if (!input || (input.tagName === 'INPUT' && (input.type === 'radio' ? !input.checked : !input.value))) {
                    allValid = false;
                }
            });
            if (!allValid) {
                alert('필수 항목을 모두 입력해주세요.');
                return;
            }

            if (woPQ.value < 1) {
                alert('수량은 1 이상 입력해야 합니다.');
                return;
            }

            if (new Date(woDate.value) > new Date(woDuedate.value)) {
                alert('지시일이 납기일보다 늦을 수 없습니다.');
                return;
            }

            if (!itemCodeChecked) {
                alert('품목을 선택해주세요.');
                return;
            }

            // hidden 값 세팅
            woNumHidden.value = woNum.value;
            hiddenBOM.value = itemCodeChecked.dataset.bomId || '';
            hiddenPROC.value = itemCodeChecked.dataset.procId || '';

            // 서버 전송 / 패널 닫기
            panel.classList.remove('open');
            panelForm.submit();
        });

        // -------------------------------
        // 메인 테이블 클릭 → 상세 패널
        // -------------------------------
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
