document.addEventListener('DOMContentLoaded', init);

function init() {
    const lookup = document.querySelector('.lookup'); // 공정 정보 조회 div

    const processInfo = document.querySelector('.processInfo'); // 공정 정보 입력 div

    const bottomBtn = document.querySelector('.bottomBtn'); // 아래 버튼 3개 div
    const deleteBtn = document.querySelector('.deleteBtn'); // 삭제 버튼
    const updateBtn = document.querySelector('.updateBtn'); // 수정 버튼
    const createBtn = document.querySelector('.createBtn'); // 등록 버튼
    
    
    /* 05_process_list.html에서 신규로 넘어왔나, 수정으로 넘어왔나 */
    /* CUD 페이지에 직접 들어온 경우에만 looup div 보이게 */
    const params = new URLSearchParams(window.location.search);
    const mode = params.get("mode");

    // 05_process_list.htm에서 신규로 넘어왔으면 '수정' 버튼 안보이기
    if (mode === "new") {
        updateBtn.style.display = "none"; 
    // 05_process_list.htm에서 수정으로 넘어왔으면 '등록' 버튼 안보이기
    } else if (mode === "update") {
        createBtn.style.display = "none"; 
    } else {
        if (lookup) lookup.style.display = "flex";
    }
    
    /* delete 버튼을 누르면 alert창 띄움 */
    deleteBtn.addEventListener('click', function(){
        alert("정말 해당 공정을 삭제하시겠습니까?");
    })

    /* 페이지를 벗어나면 수정되는 내용이 날라가니 한번 더 묻기 */
    window.addEventListener("beforeunload", function (e) {
        e.preventDefault();
        e.returnValue = ""; 
    });

}