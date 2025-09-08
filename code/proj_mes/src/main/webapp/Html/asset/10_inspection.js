window.onload = function() {
    init(); 
}

function init() {

    /* 모달  */
    const modal = document.getElementById("procModal");
    const searchProc = document.getElementById("searchProc");
    const closeBtn = document.querySelector(".close");

    // 클릭하면 모달 열기
    searchProc.onclick = function(e) {
        e.preventDefault();
        modal.style.display = "block";
    }

    // 닫기 버튼 클릭 시 닫기
    closeBtn.onclick = function() {
    modal.style.display = "none";
    }
}
