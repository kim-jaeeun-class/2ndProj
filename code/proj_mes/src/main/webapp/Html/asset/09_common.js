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
    const panelDown = document.querySelector('#panel-down');

    if (!panelForm || !mainTable || !panel || !panelDown) return;

    const saveBtn = panelForm.querySelector('.panel-save');
    const hiddenBOM = panelForm.querySelector('input[name="bom_id"]');
    const hiddenPROC = panelForm.querySelector('input[name="proc_id"]');

    const actionInput = panelForm.querySelector('#action-input');
    const woNumHidden = panelForm.querySelector('#wo-num-hidden');
    const panelTitle = document.querySelector('#panel-title-mode');
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
    mainTable.addEventListener('click', (e) => {
        const row = e.target.closest('tr.data');
        if (!row || e.target.closest('input[type="checkbox"]')) return;

        // 수정 모드 해제
        panelDown.classList.remove('open');

        // 상세 패널의 버튼에 woNum 데이터 추가
        const woNum = row.querySelector('a').textContent.trim();
        const editAllBtn = panelDown.querySelector('.edit-all-btn');
        if (editAllBtn) {
            editAllBtn.setAttribute('data-wo-num', woNum);
        }
        
        // ... 기존 상세 패널 데이터 채우기 로직
        panelDown.classList.add('open');
    });

    // -------------------------------
    // 상세 패널에서 '수정' 버튼 클릭
    // -------------------------------
    const editAllBtn = panelDown.querySelector('.edit-all-btn');
    if (editAllBtn) {
        editAllBtn.addEventListener('click', (e) => {
            e.preventDefault();
            const woNum = editAllBtn.getAttribute('data-wo-num');
            if (!woNum) {
                alert('수정할 항목이 선택되지 않았습니다.');
                return;
            }

            // AJAX 요청으로 상세 데이터 가져오기
            fetch(`workorder?action=getSingle&wo_num=${woNum}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답이 올바르지 않습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data) {
                        // 1. 등록 패널의 제목을 '수정'으로 변경
                        panelTitle.textContent = '수정';
                        // 2. hidden action 값을 'update'로 변경
                        actionInput.value = 'update';
                        // 3. 숨겨진 wo_num 필드에 값 설정
                        woNumHidden.value = data.woNum;
                        
                        // 4. 폼 필드에 데이터 채우기
                        document.querySelector('#wo-date-input').value = data.woDate;
                        document.querySelector('#wo-duedate-input').value = data.woDuedate;
                        document.querySelector('#worker-id-input').value = data.workerID;
                        document.querySelector('#wo-pq-input').value = data.woPQ;

                        // 5. 해당 품목의 라디오 버튼 체크
                        const itemRadio = document.querySelector(`input[name="item_code"][value="${data.item_code}"]`);
                        if (itemRadio) {
                            itemRadio.checked = true;
                            // BOM/PROC ID도 함께 업데이트
                            document.querySelector('#hidden-bom-id').value = itemRadio.dataset.bomId || '0';
                            document.querySelector('#hidden-proc-id').value = itemRadio.dataset.procId || '0';
                        }
                        
                        // 6. 상세 패널 닫기 및 등록/수정 패널 열기
                        panelDown.classList.remove('open');
                        panelAdd.classList.add('open');
                    } else {
                        alert('데이터를 가져오는 데 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('데이터 로딩 중 오류가 발생했습니다.');
                });
        });
    }

    // -------------------------------
    // '등록' 버튼 클릭 시 초기화
    // -------------------------------
    const openAddBtn = document.querySelector('.open-btn');
    if (openAddBtn) {
        openAddBtn.addEventListener('click', () => {
            panelTitle.textContent = '등록';
            actionInput.value = 'add';
            woNumHidden.value = '';
            panelForm.reset();
            document.querySelector('#wo-date-input').readOnly = false; // 등록일 경우에만 편집 가능하게

            panelAdd.classList.add('open');
        });
    }

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
            const woNum = panelDown.querySelector('.edit-all-btn').dataset.woNum;
            const newAq = editAqInput.value;

            if (newAq === "" || isNaN(newAq) || parseInt(newAq) < 0) {
                alert('유효한 생산 수량을 입력해주세요.');
                return;
            }

            // AJAX 요청으로 서버에 값 전송
            fetch('workorder', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `action=editAQ&wo_num=${woNum}&wo_aq=${newAq}`
            })
            .then(response => response.text()) // 혹은 response.json()
            .then(() => {
                alert('생산 수량이 성공적으로 수정되었습니다.');
                // 화면 업데이트
                aqDisplay.textContent = newAq;
                // 다시 보기 모드로 전환
                aqDisplay.style.display = 'block';
                editAqArea.style.display = 'none';
            })
            .catch(error => {
                console.error('Error:', error);
                alert('생산 수량 수정에 실패했습니다.');
            });
        });
    }

    // -------------------------------
    // 상세 패널 닫기
    // -------------------------------
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
