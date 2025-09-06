// 사이드 패널 열기/닫기
document.addEventListener("DOMContentLoaded", () => {
    // 등록 버튼 클릭 -> 패널 열기
    const openBtns = document.querySelectorAll('.open-btn');
    const panel = document.querySelector('.panel');
    const closeBtn = panel.querySelector('.close-btn');

    openBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            panel
                .classList
                .add('open');
        });
    });

    // 패널 닫기 버튼
    closeBtn.addEventListener('click', () => {
        panel
            .classList
            .remove('open');
    });

    // 체크박스 전체 선택 기능 예시
    const selectAllCheckbox = document.querySelector('.select-all');
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', (e) => {
            const checkboxes = document.querySelectorAll('input[type="checkbox"]');
            checkboxes.forEach(cb => cb.checked = e.target.checked);
        });
    }
});
