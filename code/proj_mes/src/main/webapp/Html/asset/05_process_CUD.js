document.addEventListener('DOMContentLoaded', init);

function init() {
    const lookupBtn = document.querySelector('.lookupBtn');
    const newBtn = document.querySelector('.newBtn');
    const deleteBtn = document.querySelector('.deleteBtn');
    const updateBtn = document.querySelector('.updateBtn');
    const createBtn = document.querySelector('.createBtn');

    const lookupForm = document.getElementById('lookupForm');
    const processCUDForm = document.getElementById('processCUDForm');
    const actionInput = document.getElementById('action');
    const procIdInput = document.getElementById('procId');
    const procIdHidden = document.getElementById('procId_hidden');
    const procImageInput = document.getElementById('procImageInput');
    const procImage = document.getElementById('procImage');

    const itemCodeSelectCUD = document.getElementById('itemCodeSelectCUD');
    const newItemCodeInput = document.getElementById('newItemCode');
    const departSelect = document.getElementById('departSelect');
    const procSelect = document.getElementById('procSelect');
    const newProcNameInput = document.getElementById('newProcName');
    
    // 현재 페이지의 컨텍스트 경로를 가져옴
    const contextPath = location.pathname.substring(0, location.pathname.indexOf("/", 1));
    const urlParams = new URLSearchParams(window.location.search);
    const mode = urlParams.get('mode');

    // '조회' 버튼 클릭 이벤트: 폼을 전송하여 조회 결과를 반영
    lookupBtn.addEventListener('click', function(event) {
	    event.preventDefault(); // 기본 폼 제출 동작을 막습니다.
	
	    const formData = new FormData(lookupForm);
	    const searchParams = new URLSearchParams(formData);
	
	    // 서버에 AJAX 요청을 보냅니다.
	    fetch(contextPath + '/process?' + searchParams.toString(), {
	        method: 'GET'
	    })
	    .then(response => {
	        if (!response.ok) {
	            throw new Error('네트워크 응답이 올바르지 않습니다.');
	        }
	        return response.json(); // 서버에서 JSON 형태로 응답을 보낼 것으로 가정합니다.
	    })
	    .then(data => {
	        if (data) {
	            // 조회된 데이터로 processCUDForm의 입력 필드를 채웁니다.
	            document.getElementById('procId').value = data.proc_id || '';
	            document.getElementById('procId_hidden').value = data.proc_id || '';
	            document.getElementById('procName').value = data.proc_name || '';
	            document.getElementById('procInfo').value = data.proc_info || '';
	
	            // 품목 코드와 부서는 드롭다운이므로 옵션의 value를 설정합니다.
	            document.getElementById('itemCodeSelectCUD').value = data.item_code || '';
	            document.getElementById('departSelect').value = data.depart_level || '';
	
	            // 이미지 처리
	            const procImageElement = document.getElementById('procImage');
	            if (data.proc_img) {
	                procImageElement.src = contextPath + '/' + data.proc_img;
	                procImageElement.style.display = 'block';
	            } else {
	                procImageElement.style.display = 'none';
	                procImageElement.src = ''; // 이미지 URL 초기화
	            }
	
	            // 조회 후 '수정' 및 '삭제' 버튼을 보이게 설정 (업데이트 모드)
	            document.querySelector('.createBtn').style.display = 'none';
	            document.querySelector('.updateBtn').style.display = 'inline-block';
	            document.querySelector('.deleteBtn').style.display = 'inline-block';
	            document.getElementById('procId').readOnly = true; // 공정 순서 수정 불가
	        } else {
	            alert('일치하는 공정 정보가 없습니다.');
	            // 폼 내용 초기화
	            document.getElementById('processCUDForm').reset();
	            document.querySelector('.createBtn').style.display = 'inline-block';
	            document.querySelector('.updateBtn').style.display = 'none';
	            document.querySelector('.deleteBtn').style.display = 'none';
	            document.getElementById('procImage').style.display = 'none';
	            document.getElementById('procId').readOnly = false;
	        }
	    })
	    .catch(error => {
	        console.error('조회 중 오류 발생:', error);
	        alert('공정 정보를 불러오는 데 실패했습니다.');
	    });
	});

    // '새 공정 등록' 버튼 클릭 이벤트: 'new' 모드로 페이지 리로드
    newBtn.addEventListener('click', function() {
        window.location.href = contextPath + "/process?mode=new";
    });

    // CUD 버튼에 대한 이벤트 리스너
    createBtn.addEventListener('click', function() {
        if (!procIdInput.value) {
            alert("공정 순서는 필수 입력 항목입니다.");
            return;
        }
        actionInput.value = 'create';
        procIdHidden.value = procIdInput.value;
        processCUDForm.submit();
    });

    updateBtn.addEventListener('click', function() {
        if (!procIdInput.value) {
            alert("수정할 공정을 선택해주세요.");
            return;
        }
        actionInput.value = 'update';
        procIdHidden.value = procIdInput.value;
        processCUDForm.submit();
    });

    deleteBtn.addEventListener('click', function() {
        if (!procIdInput.value) {
            alert("삭제할 공정을 선택해주세요.");
            return;
        }
        if (confirm("정말 삭제하시겠습니까?")) {
            actionInput.value = 'delete';
            procIdHidden.value = procIdInput.value;
            processCUDForm.submit();
        }
    });

    // 이미지 파일 선택 시 미리보기
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

    // 모드에 따라 버튼 표시/숨김 설정
    if (mode === 'update') {
        createBtn.style.display = 'none';
    } else {
        updateBtn.style.display = 'none';
        deleteBtn.style.display = 'none';
    }

    // 소속 부서 선택 시 공정 드롭다운 동적 로딩
    departSelect.addEventListener('change', function() {
        const selectedDepartLevel = this.value;
        if (selectedDepartLevel !== '') {
            // 서버에 AJAX 요청 보내기 (텍스트 응답 기대)
            fetch(contextPath + '/process?action=getProcNamesByDepart&departLevel=' + selectedDepartLevel)
                .then(response => response.text())
                .then(html => {
                    // 서버로부터 받은 HTML로 공정 드롭다운 내용 교체
                    procSelect.innerHTML = '<option value="">선택</option>' + html;
                    procSelect.style.display = 'inline-block';
                    newProcNameInput.style.display = 'none';
                    newProcNameInput.name = '';
                    procSelect.name = 'procName';
                })
                .catch(error => console.error('Error fetching proc names:', error));
        } else {
            // '선택'을 선택하면 공정 드롭다운 숨김
            procSelect.style.display = 'none';
            newProcNameInput.style.display = 'inline-block';
            newProcNameInput.name = 'procName';
            procSelect.name = '';
        }
    });

    // 품목코드 선택 시 새 품목코드 입력 필드 제어
    itemCodeSelectCUD.addEventListener('change', function() {
	    const selectedItemCode = this.value;
	
	    if (selectedItemCode === '') {
	        // '선택' 옵션일 경우 폼 초기화 및 새 품목코드 입력 필드 보이기
	        newItemCodeInput.style.display = 'inline-block';
	        newItemCodeInput.name = 'itemCode';
	        
	        // CUD 폼 내용 초기화
	        document.getElementById('procId').value = '';
	        document.getElementById('procName').value = '';
	        document.getElementById('procInfo').value = '';
	        document.getElementById('procImage').src = '';
	        document.getElementById('procImage').style.display = 'none';
	
	        // 버튼 상태 변경: '등록' 버튼만 보이게
	        document.querySelector('.createBtn').style.display = 'inline-block';
	        document.querySelector('.updateBtn').style.display = 'none';
	        document.querySelector('.deleteBtn').style.display = 'none';
	        document.getElementById('procId').readOnly = false;
	        return;
	    } else {
	        // 품목 코드 선택 시 새 품목코드 입력 필드 숨기기
	        newItemCodeInput.style.display = 'none';
	        newItemCodeInput.name = '';
	    }
	
	    // 서버에 AJAX 요청을 보냅니다.
	    // 서버는 이 요청을 처리하여 품목코드에 해당하는 공정 데이터를 문자열로 반환해야 합니다.
	    fetch(contextPath + '/process?action=getProcessByItemCode&itemCode=' + selectedItemCode)
	        .then(response => {
	            if (!response.ok) {
	                throw new Error('네트워크 응답이 올바르지 않습니다.');
	            }
	            return response.text(); // JSON 대신 문자열로 응답을 받습니다.
	        })
	        .then(data => {
	            if (data && data.length > 0) {
	                // 예시: "proc1|부서A|조립공정|부품을 조립하는 공정|path/to/image.jpg"
	                const processData = data.split('|');
	                
	                // 파싱된 데이터로 폼 필드 채우기
	                document.getElementById('procId').value = processData[0] || '';
	                document.getElementById('procId_hidden').value = processData[0] || '';
	                document.getElementById('departSelect').value = processData[1] || '';
	                document.getElementById('procName').value = processData[2] || '';
	                document.getElementById('procInfo').value = processData[3] || '';
	
	                // 이미지 처리
	                const procImageElement = document.getElementById('procImage');
	                if (processData[4]) {
	                    procImageElement.src = contextPath + '/' + processData[4];
	                    procImageElement.style.display = 'block';
	                } else {
	                    procImageElement.style.display = 'none';
	                    procImageElement.src = '';
	                }
	                
	                // 버튼 상태 변경: '수정', '삭제'만 보이게
	                document.querySelector('.createBtn').style.display = 'none';
	                document.querySelector('.updateBtn').style.display = 'inline-block';
	                document.querySelector('.deleteBtn').style.display = 'inline-block';
	                document.getElementById('procId').readOnly = true;
	            } else {
	                alert('해당 품목코드에 등록된 공정이 없습니다. 새로 등록할 수 있습니다.');
	                // 폼 내용 초기화
	                document.getElementById('procId').value = '';
	                document.getElementById('procId_hidden').value = '';
	                document.getElementById('departSelect').value = '';
	                document.getElementById('procName').value = '';
	                document.getElementById('procInfo').value = '';
	                document.getElementById('procImage').style.display = 'none';
	                document.getElementById('procImage').src = '';
	                
	                // 버튼 상태 변경: '등록'만 보이게
	                document.querySelector('.createBtn').style.display = 'inline-block';
	                document.querySelector('.updateBtn').style.display = 'none';
	                document.querySelector('.deleteBtn').style.display = 'none';
	                document.getElementById('procId').readOnly = false;
	            }
	        })
	        .catch(error => {
	            console.error('데이터 로딩 중 오류 발생:', error);
	            alert('공정 정보를 불러오는 데 실패했습니다.');
	        });
	});
    
    /* 첨부파일 이미지 관련 기능 */
    const deleteImageBtn = document.getElementById('deleteImageBtn');
	const deleteImageHidden = document.getElementById('deleteImageHidden');

	// 이미지 삭제 버튼 클릭 이벤트
	if (deleteImageBtn) {
	    deleteImageBtn.addEventListener('click', function() {
	        if (confirm("정말 이미지를 삭제하시겠습니까?")) {
	            // 이미지 미리보기 초기화
	            procImage.src = '';
	            procImage.style.display = 'none';
	
	            // 파일 입력 필드 값 초기화
	            procImageInput.value = '';
	
	            // 서버에 이미지 삭제를 알리기 위한 값 설정
	            deleteImageHidden.value = 'true';
	
	            // 삭제 버튼 숨기기
	            this.style.display = 'none';
	        }
	    });
	}
	
	// 파일 교체는 input[type=file]의 change 이벤트로 자동 처리
	procImageInput.addEventListener('change', function(e) {
	    // ... (기존 이미지 미리보기 로직)
	    // 파일이 새로 선택되면 삭제 플래그 초기화
	    deleteImageHidden.value = 'false';
	    // 삭제 버튼이 있다면 다시 보이게 함
	    if (deleteImageBtn) {
	        deleteImageBtn.style.display = 'inline-block';
	    }
	});
}