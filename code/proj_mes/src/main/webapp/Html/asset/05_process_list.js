window.onload = function() {
    init(); 
}

function init() {
    /* 조회 버튼을 눌려야 표가 보이게 */
    const lookupBtn = document.querySelector('.lookupBtn');
    const tableContainer = document.querySelector('.table-container');

    lookupBtn.addEventListener('click', function() {
        tableContainer.style.display = 'block'; // 조회 버튼 누르면 보이게
    });

}