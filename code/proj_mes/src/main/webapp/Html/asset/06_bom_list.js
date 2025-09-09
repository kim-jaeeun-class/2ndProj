document.addEventListener('DOMContentLoaded', init);

function init() {

    const lookupBtn = document.querySelector(".lookupBtn");
    const newBOMBtn = document.querySelector(".newBOMBtn");
    const updateBOMBtn = document.querySelector(".updateBOMBtn");
    const tableContainer = document.querySelector(".table-container");

    // 조회 버튼을 누르면 table-container와 updateBOMBtn가 보이게
    lookupBtn.addEventListener('click', function(){
        tableContainer.style.display = 'block'; 
        updateBOMBtn.style.display = 'block'; 
    })

    // 신규 버튼을 누르면 <BOM 등록/수정/삭제> 페이지로 이동
    newBOMBtn.addEventListener('click', function(){
        window.open("./06_bom_CUD.html?mode=new", "_blank"); 
    })

    // 수정 버튼을 누르면 <BOM 등록/수정/삭제> 페이지로 이동
    updateBOMBtn.addEventListener('click', function(){
        window.open("./06_bom_CUD.html?mode=update", "_blank"); 
    })
}