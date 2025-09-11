document.addEventListener('DOMContentLoaded', init);

function init() {
    const deleteBtn = document.querySelector('.deleteBtn');
    const updateBtn = document.querySelector('.updateBtn');
    const createBtn = document.querySelector('.createBtn');

    const processCUDForm = document.getElementById('processCUDForm');
    const actionInput = document.getElementById('action');
    const procIdHidden = document.getElementById('procId_hidden');
    const procSeqInput = document.getElementById('procSeq');
    const procImageInput = document.getElementById('procImageInput');
    const procImage = document.getElementById('procImage');
    const deleteImageBtn = document.getElementById('deleteImageBtn');
    const deleteImageHidden = document.getElementById('deleteImageHidden');

    const itemCodeSelectCUD = document.getElementById('itemCodeSelectCUD');
    const newItemCodeInput = document.getElementById('newItemCode');
    const departSelectCUD = document.getElementById('departSelect'); 
    const procNameSelectCUD = document.getElementById('procNameSelect');
    
    // 현재 페이지의 컨텍스트 경로를 가져옴
    const contextPath = location.pathname.substring(0, location.pathname.indexOf("/", 1));
    const urlParams = new URLSearchParams(window.location.search);
    const mode = urlParams.get('mode');

    // CUD 버튼에 대한 이벤트 리스너
    if (createBtn) {
        createBtn.addEventListener('click', function() {
            if (!procSeqInput.value) {
                alert("공정 순서는 필수 입력 항목입니다.");
                return;
            }
            actionInput.value = 'create';
            processCUDForm.submit();
        });
    }

    if (updateBtn) {
        updateBtn.addEventListener('click', function() {
            if (!procSeqInput.value) {
                alert("수정할 공정을 선택해주세요.");
                return;
            }
            actionInput.value = 'update';
            processCUDForm.submit();
        });
    }

    if (deleteBtn) {
        deleteBtn.addEventListener('click', function() {
            if (!procIdHidden.value) { // Changed to use the hidden input
                alert("삭제할 공정을 선택해주세요.");
                return;
            }
            if (confirm("정말 삭제하시겠습니까?")) {
                actionInput.value = 'delete';
                processCUDForm.submit();
            }
        });
    }

    // 모드에 따라 버튼 표시/숨김 설정
    if (mode === 'update') {
        if (createBtn) createBtn.style.display = 'none';
    } else {
        if (updateBtn) updateBtn.style.display = 'none';
        if (deleteBtn) deleteBtn.style.display = 'none';
    }

    // 이미지 파일 선택 시 미리보기
    if (procImageInput && procImage) {
        procImageInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(event) {
                    procImage.src = event.target.result;
                    procImage.style.display = 'block';
                };
                reader.readAsDataURL(file);
            }
        });
    }

    /* 첨부파일 이미지 관련 기능 */
    if (deleteImageBtn && procImage && procImageInput && deleteImageHidden) {
        deleteImageBtn.addEventListener('click', function() {
            if (confirm("정말 이미지를 삭제하시겠습니까?")) {
                procImage.src = '';
                procImage.style.display = 'none';
                procImageInput.value = '';
                deleteImageHidden.value = 'true';
                this.style.display = 'none';
            }
        });
    }
    
    if (procImageInput && deleteImageHidden) {
        procImageInput.addEventListener('change', function(e) {
            deleteImageHidden.value = 'false';
            if (deleteImageBtn) {
                deleteImageBtn.style.display = 'inline-block';
            }
        });
    }
    
    // 품목코드 선택 시 새 품목코드 입력 필드 제어
    if (itemCodeSelectCUD && newItemCodeInput) {
        itemCodeSelectCUD.addEventListener('change', function() {
            const selectedItemCode = this.value;
            if (selectedItemCode === '') {
                newItemCodeInput.style.display = 'inline-block';
                newItemCodeInput.name = 'itemCode';
                
                // CUD 폼 필드 초기화
                if (procSeqInput) procSeqInput.value = '';
                if (procNameSelectCUD) procNameSelectCUD.innerHTML = '<option value="">선택</option>';
                if (document.getElementById('procInfo')) document.getElementById('procInfo').value = '';
                if (procImage) procImage.src = '';
                if (procImage) procImage.style.display = 'none';

                if (createBtn) createBtn.style.display = 'inline-block';
                if (updateBtn) updateBtn.style.display = 'none';
                if (deleteBtn) deleteBtn.style.display = 'none';
                if (procSeqInput) procSeqInput.readOnly = false;
            } else {
                newItemCodeInput.style.display = 'none';
                newItemCodeInput.name = '';
            }
        });
    }


    if (departSelectCUD && procNameSelectCUD) {
        departSelectCUD.addEventListener('change', function() {
            const selectedDepartLevel = this.value;
            
            if (selectedDepartLevel) {
                // 부서를 선택했을 때만 AJAX 요청
                fetch(`${contextPath}/process?action=getUniqueProcNamesByDepart&departLevel=${encodeURIComponent(selectedDepartLevel)}`)
                    .then(response => response.json())
                    .then(procNames => {
                        // 공정명 드롭다운 초기화
                        procNameSelectCUD.innerHTML = '<option value="">선택</option>';
                        
                        // 서버에서 받은 공정명 목록으로 옵션 채우기
                        procNames.forEach(procName => {
                            const option = document.createElement('option');
                            option.value = procName;
                            option.textContent = procName;
                            procNameSelectCUD.appendChild(option);
                        });
                    })
                    .catch(error => console.error('Error fetching process names:', error));
            } else {
                // 부서 선택을 해제했을 때 공정명 드롭다운 초기화
                procNameSelectCUD.innerHTML = '<option value="">선택</option>';
            }
        });
    }
}