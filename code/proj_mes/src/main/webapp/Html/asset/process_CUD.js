document.addEventListener('DOMContentLoaded', init);

function init() {
    
    // delete 버튼을 누르면 alert창 띄움
    const deleteBtn = document.querySelector('.deleteBtn');
    deleteBtn.addEventListener('click', function(){
        alert("정말 해당 공정을 삭제하시겠습니까?");
    })

}