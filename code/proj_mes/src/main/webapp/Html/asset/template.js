document.addEventListener("DOMContentLoaded", function() {
    const myIconBtn = document.getElementById("myIconBtn");
    const mgrList = document.querySelector(".myIcon .mgrList");

    myIconBtn.addEventListener("click", function(e) {
        e.preventDefault();
        mgrList.classList.toggle("show");
    });

    // 바깥 클릭 시 닫기
    document.addEventListener("click", function(e) {
        if (!myIconBtn.contains(e.target) && !mgrList.contains(e.target)) {
            mgrList.classList.remove("show");
        }
    });
});