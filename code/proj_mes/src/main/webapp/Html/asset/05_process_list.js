window.onload = function() {
    init(); 
}

function init() {
    const lookupBtn = document.querySelector('.lookupBtn'); // 조회 버튼
    const updateProcessBtn = document.querySelector('.updateProcessBtn'); // 수정 버튼
    const newProcessBtn = document.querySelector('.newProcessBtn'); // 신규 버튼
    const tableContainer = document.querySelector('.table-container');
    
    /* 조회 버튼을 눌려야 표가 보이게 */
    lookupBtn.addEventListener('click', function() {
        tableContainer.style.display = 'block'; 
    });

    // 수정 버튼을 누르면 <공정 등록/수정/삭제> 페이지로 이동
    updateProcessBtn.addEventListener('click', function(){
        window.open("./05_process_CUD.html?mode=update", "_blank"); 
    })

    // 신규 버튼을 누르면 <공정 등록/수정/삭제> 페이지로 이동
    newProcessBtn.addEventListener('click', function(){
        window.open("./05_process_CUD.html?mode=new", "_blank"); 
    })




}