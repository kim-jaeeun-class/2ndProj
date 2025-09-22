 
  document.getElementById('addrSearch').addEventListener('click', execDaumPostcode);

  function execDaumPostcode() {
    new daum.Postcode({
      oncomplete: function (data) {
        // 도로명/지번 선택 결과 처리
        const selected = (data.userSelectedType === 'R') ? data.roadAddress : data.jibunAddress;

        //실제 모달의 id에 맞춰 값 채우기
        const addrField   = document.getElementById('addr');   // 주소
        const detailField = document.getElementById('addr2');  // 상세주소

        if (addrField)   addrField.value = selected || data.address || '';
        if (detailField) detailField.focus();

        // 필요 시 우편번호도 쓰세요 (input을 추가해두었다면)
        // document.getElementById('postcode').value = data.zonecode;
      }
    }).open(); // 팝업 방식
  }

  // 트리거: 등록하기 버튼(id="registBtn")에서 모달 열기
  const partnerModal = document.getElementById('partnerModal');
  const registBtn = document.getElementById('std-search_1');  // ← 페이지에 이 버튼이 있어야 합니다.
  registBtn?.addEventListener('click', () => partnerModal.showModal());

  // 닫기 버튼/취소
  const closeModal = () => partnerModal.close();
  document.getElementById('pmClose').addEventListener('click', closeModal);
  document.getElementById('pmCancel').addEventListener('click', closeModal);

  // 바깥 클릭 시 닫기
  partnerModal.addEventListener('click', (e) => {
    if (e.target === partnerModal) closeModal();
  });

  // 저장(샘플) – 실제 저장 로직으로 교체하세요.
  document.getElementById('partnerForm').addEventListener('submit', (e) => {
    e.preventDefault();
    // TODO: 폼 데이터 수집 후 서버에 전송
    alert('저장되었습니다 (시뮬레이션)');
    closeModal();
  });

  // 입력 보조: 사업자번호 3-2-5
  const biz = document.getElementById('bizNo');
  biz.addEventListener('input', () => {
    let v = biz.value.replace(/[^0-9]/g,'').slice(0,10);
    const p1=v.slice(0,3), p2=v.slice(3,5), p3=v.slice(5);
    biz.value=[p1,p2,p3].filter(Boolean).join('-');
  });

  // 입력 보조: 전화번호 3-4-4(또는 02 지역번호 처리)
  const tel = document.getElementById('tel');
  tel.addEventListener('input', () => {
    let v = tel.value.replace(/[^0-9]/g,'').slice(0,11);
    if (v.startsWith('02')) {
      const p1=v.slice(0,2), p2=v.length>9? v.slice(2,6):v.slice(2,5), p3=v.slice(p2.length+2);
      tel.value=[p1,p2,p3].filter(Boolean).join('-');
    } else {
      const p1=v.slice(0,3), p2=v.slice(3,7), p3=v.slice(7);
      tel.value=[p1,p2,p3].filter(Boolean).join('-');
    }
  });