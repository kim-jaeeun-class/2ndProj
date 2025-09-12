document.addEventListener('DOMContentLoaded', init);

function init() {
    const processCUDForm = document.getElementById('processCUDForm');
    const actionInput = document.getElementById('action');
    const procIdHidden = document.getElementById('procId_hidden');
    const procSeqInput = document.getElementById('procSeq');
    const procImageInput = document.getElementById('procImageInput');
    const procImage = document.getElementById('procImage');
    const deleteImageBtn = document.getElementById('deleteImageBtn');
    const deleteImageHidden = document.getElementById('deleteImageHidden');

    const itemCodeSelectCUD = document.getElementById('itemCodeSelectCUD');
    const departSelectCUD = document.getElementById('departSelect');
    const procNameSelectCUD = document.getElementById('procNameSelect');

    const createBtn = document.querySelector('.createBtn');
    const updateBtn = document.querySelector('.updateBtn');
    const deleteBtn = document.querySelector('.deleteBtn');

    const urlParams = new URLSearchParams(window.location.search);
    const mode = urlParams.get('mode');
    const contextPath = location.pathname.substring(0, location.pathname.indexOf("/", 1));
    const isNewMode = mode === 'new';

    // 버튼 표시
    createBtn.style.display = isNewMode ? 'inline-block' : 'none';
    updateBtn.style.display = isNewMode ? 'none' : 'inline-block';
    deleteBtn.style.display = isNewMode ? 'none' : 'inline-block';

    // 품목 코드 변경 → 부서 로드
    itemCodeSelectCUD.addEventListener('change', function() {
	    const itemCode = this.value;
	    fetch(`${contextPath}/process?action=getDepartLevelsByItemCode&itemCode=${encodeURIComponent(itemCode)}`)
	        .then(res => res.text())
	        .then(data => {
	            departSelectCUD.innerHTML = '<option value="">선택</option>';
	            procNameSelectCUD.innerHTML = '<option value="">선택</option>';
	            if(data.trim() !== '') {
	                data.split(',').map(x=>x.trim()).forEach(level => {
	                    const option = document.createElement('option');
	                    option.value = level;
	                    option.textContent = level;
	                    departSelectCUD.appendChild(option);
	                });
	            }
	        });
	});

    // 부서 변경 → 공정 로드
    departSelectCUD.addEventListener('change', function() {
        const itemCode = itemCodeSelectCUD.value;
        const depart = this.value;
        fetch(`${contextPath}/process?action=getProcNamesByItemAndDepart&itemCode=${encodeURIComponent(itemCode)}&departLevel=${encodeURIComponent(depart)}`)
            .then(res => res.text())
            .then(data => {
                procNameSelectCUD.innerHTML = '<option value="">선택</option>';
                if(data.trim() !== '') {
                    data.split(',').forEach(proc => {
                        const procNameOnly = proc.split(' - ')[1] || proc.split(' - ')[0];
                        const option = document.createElement('option');
                        option.value = procNameOnly;
                        option.textContent = procNameOnly;
                        procNameSelectCUD.appendChild(option);
                    });
                }
            });
    });

    // 초기값 세팅 (update 모드)
    if (!isNewMode) {
	    const itemCode = itemCodeSelectCUD.value;
	    const depart = departSelectCUD.value;
	    const initialProc = procNameSelectCUD.dataset.initialProc || '';
	
	    if(itemCode && depart) {
	        fetch(`${contextPath}/process?action=getProcNamesByItemAndDepart&itemCode=${encodeURIComponent(itemCode)}&departLevel=${encodeURIComponent(depart)}`)
	            .then(res => res.text())
	            .then(data => {
	                procNameSelectCUD.innerHTML = '<option value="">선택</option>';
	                if(data.trim() !== '') {
	                    data.split(',').forEach(proc => {
	                        const procNameOnly = proc.split(' - ')[1] || proc.split(' - ')[0];
	                        const option = document.createElement('option');
	                        option.value = procNameOnly;
	                        option.textContent = procNameOnly;
	                        // 기존값 선택
	                        if(procNameOnly === initialProc) option.selected = true;
	                        procNameSelectCUD.appendChild(option);
	                    });
	                } else {
	                    // fetch 결과가 없으면 JSP에서 세팅된 값 그대로 두기
	                    const option = document.createElement('option');
	                    option.value = initialProc;
	                    option.textContent = initialProc;
	                    option.selected = true;
	                    procNameSelectCUD.appendChild(option);
	                }
	            });
	    }
	}

    // CUD 버튼 이벤트
    createBtn.addEventListener('click', () => { actionInput.value = 'create'; processCUDForm.submit(); });
    if(updateBtn) updateBtn.addEventListener('click', () => { actionInput.value = 'update'; processCUDForm.submit(); });
    if(deleteBtn) deleteBtn.addEventListener('click', () => {
        if(confirm('정말 삭제하시겠습니까?')) { actionInput.value = 'delete'; processCUDForm.submit(); }
    });

    // 이미지
    if(procImageInput){
        procImageInput.addEventListener('change', e=>{
            const file = e.target.files[0];
            if(file){
                const reader = new FileReader();
                reader.onload = ev => { procImage.src = ev.target.result; procImage.style.display='block'; };
                reader.readAsDataURL(file);
                deleteImageHidden.value='false';
            }
        });
    }
    if(deleteImageBtn){
        deleteImageBtn.addEventListener('click', ()=>{
            procImage.src=''; procImage.style.display='none'; procImageInput.value=''; deleteImageHidden.value='true';
        });
    }
}