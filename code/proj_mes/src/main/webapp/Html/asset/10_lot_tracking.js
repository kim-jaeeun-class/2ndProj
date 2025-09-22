document.addEventListener("DOMContentLoaded", initLotTracking);

function initLotTracking() {
    const modal = document.getElementById("lotModal");
    const closeBtn = document.getElementById("modalClose");

    // 닫기 버튼 이벤트 바인딩
    bindCloseModal(closeBtn, modal);

    // LOT 링크 이벤트 바인딩
    bindLotLinks(modal);

    // 바깥 클릭 시 모달 닫기
    bindOutsideClick(modal);
}

// 이벤트 바인딩 함수 =============================

// 모달 닫기 버튼 이벤트 등록
function bindCloseModal(closeBtn, modal) {
    if (closeBtn) {
        closeBtn.addEventListener("click", () => hideModal(modal));
    }
}

// LOT 링크 클릭 이벤트 등록
function bindLotLinks(modal) {
    document.querySelectorAll(".lotLink").forEach(link => {
        link.addEventListener("click", (e) => {
            e.preventDefault();
            showLotModal(link, modal);
        });
    });
}

// 모달 외부 클릭 시 닫기 이벤트 등록
function bindOutsideClick(modal) {
    window.addEventListener("click", (event) => {
        if (event.target === modal) {
            hideModal(modal);
        }
    });
}

// 모달 제어 함수 =============================

//LOT 모달 표시
function showLotModal(link, modal) {
    document.getElementById("modalLotId").innerText = link.dataset.lot;
    document.getElementById("modalItem").innerText = link.dataset.item;
    document.getElementById("modalItemCode").innerText = link.dataset.itemcode; // ✅ 추가
    document.getElementById("modalDate").innerText = link.dataset.date;

    // 공정 / 검사 이력 불러오기
    loadProcessHistory(link.dataset.lot);
    loadInspectionResults(link.dataset.lot);

    modal.style.display = "block";
}

// LOT 모달 숨기기
function hideModal(modal) {
    modal.style.display = "none";
}

// Ajax 데이터 로딩 함수 =============================

// 공정 이력 로딩
function loadProcessHistory(lotId) {
    const tbody = document.getElementById("procHistoryBody");
    tbody.innerHTML = "<tr><td colspan='4'>불러오는 중...</td></tr>";

    fetch(`lotTracking?action=history&lotId=${lotId}`)
        .then(res => res.json())
        .then(data => renderProcessHistory(data, tbody))
        .catch(err => {
            console.error("공정 이력 불러오기 실패:", err);
            tbody.innerHTML = "<tr><td colspan='4'>불러오기 실패</td></tr>";
        });
}

// 검사 결과 로딩
function loadInspectionResults(lotId) {
    const tbody = document.getElementById("inspectionResultBody");
    tbody.innerHTML = "<tr><td colspan='6'>불러오는 중...</td></tr>";

    fetch(`lotTracking?action=inspection&lotId=${lotId}`)
        .then(res => res.json())
        .then(data => renderInspectionResults(data, tbody))
        .catch(err => {
            console.error("검사 결과 불러오기 실패:", err);
            tbody.innerHTML = "<tr><td colspan='6'>불러오기 실패</td></tr>";
        });
}

// 렌더링 함수 =============================

// 공정 이력 테이블 렌더링
function renderProcessHistory(data, tbody) {
    tbody.innerHTML = "";
    if (!data || data.length === 0) {
        tbody.innerHTML = "<tr><td colspan='4'>공정 이력이 없습니다.</td></tr>";
        return;
    }

    data.forEach(row => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${row.procName || ''}</td>
            <td>${row.startTime || ''}</td>
            <td>${row.endTime || ''}</td>
            <td>${row.workerId || ''}</td>`;
        tbody.appendChild(tr);
    });
}

// 검사 결과 테이블 렌더링
function renderInspectionResults(data, tbody) {
    tbody.innerHTML = "";
    if (!data || data.length === 0) {
        tbody.innerHTML = "<tr><td colspan='6'>검사 결과가 없습니다.</td></tr>";
        return;
    }

    data.forEach(row => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${row.procName || ''}</td>
            <td>${row.irType || ''}</td>
            <td>${row.defectName || ''}</td>
            <td>${row.quantity || 0}</td>
            <td>${row.workerId || ''}</td>
            <td>${row.endTime || ''}</td>`;
        tbody.appendChild(tr);
    });
}
