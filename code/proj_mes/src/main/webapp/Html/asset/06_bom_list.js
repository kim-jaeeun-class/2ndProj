document.addEventListener("DOMContentLoaded", () => {

    // ===============================
    // 사이드 패널 열기/닫기
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
    initPanel();


    // ===============================
    // BOM 상세 모달 열기
    // ===============================
    const bomModal = document.getElementById("bomDetailModal");
    const modalCloseBtn = document.getElementById("modalCloseBtn");
    const modalEditBtn = document.getElementById("modalEditBtn");
    const modalDeleteID = document.getElementById("modalDeleteID");

    document.querySelectorAll(".bom_tab tbody tr").forEach(row => {
        row.addEventListener("click", (e) => {
            // 체크박스 클릭 시 모달 열기 방지
            if(e.target.tagName.toLowerCase() === "input" && e.target.type === "checkbox") return;

            const bomID = row.querySelector("td:nth-child(2)").textContent;
            const itemCode1 = row.querySelector("td:nth-child(3)").textContent;
            const itemCode2 = row.querySelector("td:nth-child(4)").textContent;
            const requireAmount = row.querySelector("td:nth-child(5)").textContent;

            // 모달 내용 채우기
            document.getElementById("bomID").textContent = bomID;
            document.getElementById("itemCode1").textContent = itemCode1;
            document.getElementById("itemCode2").textContent = itemCode2;
            document.getElementById("requireAmount").textContent = requireAmount;
            modalDeleteID.value = bomID;

            // 모달 열기
            bomModal.classList.remove("hidden");
        });
    });

    // 모달 닫기
    modalCloseBtn.addEventListener("click", () => {
        bomModal.classList.add("hidden");
    });

    // 모달 -> 수정 버튼 클릭 시 슬라이드 패널 열기 및 값 세팅
    modalEditBtn.addEventListener("click", () => {
        const slideForm = document.querySelector(".panel form");
        slideForm.action = "bom";
        slideForm.querySelector("[name='action']").value = "edit";
        slideForm.querySelector("[name='bomID']").value = document.getElementById("bomID").textContent;
        slideForm.querySelector("[name='item-code-1']").value = document.getElementById("itemCode1").textContent;
        slideForm.querySelector("[name='item-code-2']").value = document.getElementById("itemCode2").textContent;
        slideForm.querySelector("[name='require-amount']").value = document.getElementById("requireAmount").textContent;

        document.querySelector(".panel").classList.add("open"); // 슬라이드 열기
        bomModal.classList.add("hidden"); // 모달 닫기
    });

});
