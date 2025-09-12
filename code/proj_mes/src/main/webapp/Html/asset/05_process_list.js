document.addEventListener('DOMContentLoaded', init);

function init() {
    const updateProcessBtn = document.querySelector('.updateProcessBtn');
    const newProcessBtn = document.querySelector('.newProcessBtn');
    const contextPath = location.pathname.substring(0, location.pathname.indexOf("/", 1));

    // 수정 버튼
    updateProcessBtn.addEventListener('click', function() {
        const selectedRadio = document.querySelector('input[name="selectedProcId"]:checked');
        if (!selectedRadio) { alert("수정할 항목을 선택해주세요."); return; }
        const procId = selectedRadio.value;
        const row = selectedRadio.closest('tr');
        const departLevel = row.querySelector('.departLevelCell').textContent.trim();
        const url = `${contextPath}/process?mode=update&procId=${procId}&departLevel=${encodeURIComponent(departLevel)}`;
        window.open(url, "_blank");
    });

    // 신규 버튼
    newProcessBtn.addEventListener('click', function() {
        const url = `${contextPath}/process?mode=new`;
        window.open(url, "_blank");
    });

    // 이미지 클릭 확대/축소
    document.querySelectorAll('.proc_tab img').forEach(img => {
        img.addEventListener('click', function() {
            this.classList.toggle('expanded'); // 클릭할 때마다 확대/축소
        });
    });
    
}