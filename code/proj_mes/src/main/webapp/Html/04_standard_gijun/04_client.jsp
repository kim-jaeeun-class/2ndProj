<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>거래처 관리</title>

  <!-- 페이지/공통 스타일 -->
  <link rel="stylesheet" href="<c:url value='/Html/asset/template.css' />">
  <link rel="stylesheet" href="<c:url value='/Html/asset/04_client.css' />">
  <link rel="stylesheet" href="<c:url value='/Html/asset/04_standard_list.css' />">
  
  <!-- Inter 폰트 & Tailwind -->
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>

  <!-- Chart.js -->
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

  <!-- 다음 우편번호 서비스 -->
  <script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>

<body>
  <div id="header-slot"></div>
  <div id="nav-slot"></div>

  <div class="titleBox">
    <span>거래처 목록</span>

  <div class="wrap">
    <!-- 삭제 전용 폼 -->
    <form id="deleteForm" method="post" action="<c:url value='/Client'/>">
      <input type="hidden" name="op" value="delete" />

      <div class="action">
        <div>
          <button class="btn delete_btn" type="button" id="std-delete">삭제</button>
        </div>
        <div>
          <button class="btn add_btn" type="button" id="btn-insert">등록</button>
        </div>
      </div>

      <table class="tables">
        <thead>
          <tr>
            <th style="width:48px;">
              <input type="checkbox" id="checkAll" aria-label="전체선택" />
            </th>
            <th>거래처ID</th>
            <th>거래처명</th>
            <th>사업자번호</th>
            <th>거래품목</th>
            <th>구분</th>
          </tr>
        </thead>

        <tbody id="std-body">
          <c:choose>
            <c:when test="${not empty clients}">
              <c:forEach var="row" items="${clients}">
                <tr class="data-row"
                    data-id="${row.client_id}"
                    data-name="${fn:escapeXml(row.client_name)}"
                    data-phone="${row.client_phone}"
                    data-bizno="${row.business_number}"
                    data-item="${row.business_item}"
                    data-address="${fn:escapeXml(row.client_address)}"
                    data-division="${row.inout_division}"
                    data-worker="${row.worker_id}">
                  <td>
                    <input type="checkbox" class="row-check" name="ids" value="${row.client_id}" aria-label="선택" />
                  </td>

                  <td><c:out value="${row.client_id}" /></td>
                  <td><c:out value="${row.client_name}" /></td>
                  <td><c:out value="${row.business_number}" /></td>
                  <td><c:out value="${row.business_item}" /></td>

                  <td>
                    <c:choose>
                      <c:when test="${row.inout_division == '-1' or row.inout_division == -1}">출고</c:when>
                      <c:when test="${row.inout_division == '0'  or row.inout_division == 0 }">공통</c:when>
                      <c:when test="${row.inout_division == '1'  or row.inout_division == 1 }">발주</c:when>
                      <c:otherwise>
                        <c:out value="${row.inout_division}" />
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <tr><td colspan="9" style="text-align:center;">데이터가 없습니다.</td></tr>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>

    </form>
   </div>
  </div>

  <!-- 등록/수정 모달 (공용) -->
  <dialog id="partnerModal" aria-labelledby="pmTitle">
    <div class="modal-card" role="dialog" aria-modal="true">
      <div class="modal-header">
        <h3 id="pmTitle" class="modal-title">거래처 등록</h3>
        <button class="modal-close" type="button" id="pmClose" aria-label="닫기">×</button>
      </div>

      <form id="partnerForm" class="modal-body" autocomplete="off"
            method="post" action="<c:url value='/Client'/>">
        <input type="hidden" name="op" id="pmOp" value="insert" />
        <input type="hidden" name="client_id" id="client_id_hidden" />
        <input type="hidden" name="client_address" id="client_address_hidden" />

        <div class="grid">
          <div class="field">
            <label for="corpName">거래처명</label>
            <input id="corpName" name="client_name" class="input" type="text" placeholder="예) 00유리" required />
          </div>

          <div class="field">
            <label for="bizNo">사업자번호</label>
            <input id="bizNo" name="business_number" class="input" type="text" inputmode="numeric"
                   placeholder="012-34-56789" maxlength="12" />
          </div>

          <div class="field">
            <label for="item">거래품목</label>
            <select id="item" name="business_item">
              <option value="">선택</option>
              <option>유리</option>
              <option>코팅액</option>
              <option>기타</option>
            </select>
          </div>

          <div class="field">
            <label for="manager">담당자(사번)</label>
            <input id="manager" name="worker_id" class="input" type="text" placeholder="사번 또는 담당자ID" />
          </div>

          <div class="field">
            <label for="tel">연락번호</label>
            <input id="tel" name="client_phone" class="input" type="tel" placeholder="010-5555-6666" maxlength="13" />
          </div>

          <div class="field">
            <label for="division">구분</label>
            <select id="division" name="inout_division">
              <option value="">선택</option>
              <option value="-1">출고</option>
              <option value="0">공통</option>
              <option value="1">발주</option>
            </select>
          </div>

          <div class="field" style="grid-column:1 / -1;">
            <label for="addr">주소</label>
            <div class="addr-row">
              <input id="addr" class="input" type="text" placeholder="도로명/지번 주소" />
              <button class="addr-btn" type="button" id="addrSearch">주소 검색</button>
            </div>
          </div>

          <div class="field" style="grid-column:1 / -1;">
            <label for="addr2">상세주소</label>
            <input id="addr2" class="input" type="text" placeholder="상세주소" />
          </div>
        </div>
      </form>

      <div class="modal-footer">
        <button type="button" class="btn back_btn" id="pmCancel">취소</button>
        <button type="submit" form="partnerForm" class="btn primary confirm_btn" id="pmSave">저장</button>
      </div>
    </div>
  </dialog>

  <!-- 상세 보기 모달 (행 클릭 시) -->
  <dialog id="detailModal" aria-labelledby="dmTitle">
    <div class="modal-card" role="dialog" aria-modal="true">
      <div class="modal-header">
        <h3 id="dmTitle" class="modal-title">거래처 상세</h3>
        <button class="modal-close" type="button" id="dmClose" aria-label="닫기">×</button>
      </div>

      <div class="modal-body">
        <div class="grid">
          <div class="field"><label>거래처ID</label><div id="dmId" class="readonly"></div></div>
          <div class="field"><label>거래처명</label><div id="dmName" class="readonly"></div></div>
          <div class="field"><label>사업자번호</label><div id="dmBiz" class="readonly"></div></div>
          <div class="field"><label>거래품목</label><div id="dmItem" class="readonly"></div></div>
          <div class="field"><label>담당자(사번)</label><div id="dmWorker" class="readonly"></div></div>
          <div class="field"><label>연락번호</label><div id="dmTel" class="readonly"></div></div>
          <div class="field"><label>구분</label><div id="dmDiv" class="readonly"></div></div>
          <div class="field" style="grid-column:1 / -1;"><label>주소</label><div id="dmAddr" class="readonly"></div></div>
        </div>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btnEdit" id="dmEdit">수정</button>
        <button type="button" class="btn back_btn" id="dmOk">닫기</button>
      </div>
    </div>
  </dialog>

  <!-- 스크립트 -->
  <script>
    // ========== 유틸 ==========
    const $ = (sel, root=document) => root.querySelector(sel);
    const $$ = (sel, root=document) => Array.from(root.querySelectorAll(sel));
    const mapDivision = (v) => ({ '-1': '출고', '0': '공통', '1': '발주' }[String(v)] || (v ?? ''));

    // 셀렉트 안전 설정(값/라벨 모두 시도, 없으면 임시 옵션 생성)
    function setSelectValue(sel, value) {
      const v = value == null ? '' : String(value);
      sel.value = v;                          // 1) value 직접 매칭
      if (sel.value === v) return;
      const opt = Array.from(sel.options).find(o => o.textContent === v);
      if (opt) { sel.value = opt.value; return; }
      if (v !== '') sel.add(new Option(v, v, true, true));  // 3) 임시 옵션 추가
    }

    // ========== 등록/수정 모달 제어 ==========
    const partnerModal = $('#partnerModal');
    const pmTitle = $('#pmTitle');
    const pmOp = $('#pmOp');
    const pmSave = $('#pmSave');

    const form = $('#partnerForm');
    const f = {
      id: $('#client_id_hidden'),
      name: $('#corpName'),
      biz: $('#bizNo'),
      item: $('#item'),
      worker: $('#manager'),
      tel: $('#tel'),
      div: $('#division'),
      addr: $('#addr'),
      addr2: $('#addr2'),
      addrHidden: $('#client_address_hidden')
    };

    function toInsertMode() {
      pmTitle.textContent = '거래처 등록';
      pmOp.value = 'insert';
      pmSave.textContent = '저장';
      f.id.value = '';
      form.reset();
      f.addrHidden.value = '';
    }

    function toUpdateMode(data) {
      pmTitle.textContent = '거래처 수정';
      pmOp.value = 'update';
      pmSave.textContent = '저장';
      f.id.value    = data.id || '';
      f.name.value  = data.name || '';
      f.biz.value   = data.bizno || '';
      setSelectValue(f.item, data.item);
      f.worker.value= data.worker || '';
      f.tel.value   = data.phone || '';
      setSelectValue(f.div, data.division);
      f.addr.value  = data.address || '';
      f.addr2.value = '';
      f.addrHidden.value = data.address || '';
    }

    $('#btn-insert').addEventListener('click', () => {
      toInsertMode();
      partnerModal.showModal();
    });

    $('#pmClose').addEventListener('click', () => { partnerModal.close(); toInsertMode(); });
    $('#pmCancel').addEventListener('click', () => { partnerModal.close(); toInsertMode(); });

    // 다음 주소 검색
    $('#addrSearch').addEventListener('click', function () {
      new daum.Postcode({
        oncomplete: function (data) {
          $('#addr').value = data.roadAddress || data.jibunAddress || '';
          $('#addr2').focus();
        }
      }).open();
    });

    // 제출 직전 주소 합치기 & 모드 보정
    form.addEventListener('submit', function () {
      const a1 = f.addr.value.trim();
      const a2 = f.addr2.value.trim();
      f.addrHidden.value = (a1 + ' ' + a2).trim();

      // id가 있으면 update로 보정 (혹시 insert로 덮였어도 안전)
      if (f.id.value && pmOp.value !== 'update') {
        pmOp.value = 'update';
      }
    });

    // ========== 상세 모달 ==========
    const detailModal = $('#detailModal');
    const dm = {
      id: $('#dmId'),
      name: $('#dmName'),
      biz: $('#dmBiz'),
      item: $('#dmItem'),
      worker: $('#dmWorker'),
      tel: $('#dmTel'),
      div: $('#dmDiv'),
      addr: $('#dmAddr')
    };

    function openDetailModal(data) {
      dm.id.textContent = data.id || '';
      dm.name.textContent = data.name || '';
      dm.biz.textContent = data.bizno || '';
      dm.item.textContent = data.item || '';
      dm.worker.textContent = data.worker || '';
      dm.tel.textContent = data.phone || '';
      dm.div.textContent = mapDivision(data.division);
      dm.addr.textContent = data.address || '';
      detailModal.dataset.selected = JSON.stringify(data);
      detailModal.showModal();
    }

    $('#dmClose').addEventListener('click', () => detailModal.close());
    $('#dmOk').addEventListener('click', () => detailModal.close());

    // 상세 → 수정
    $('#dmEdit').addEventListener('click', () => {
      const data = JSON.parse(detailModal.dataset.selected || '{}');
      detailModal.close();
      pmOp.value = 'update';          // 확실히 update로
      toUpdateMode(data);
      partnerModal.showModal();
    });

    // ========== 테이블 인터랙션 ==========
    const checkAll = $('#checkAll');
    if (checkAll) {
      checkAll.addEventListener('change', (e) => {
        $$('.row-check').forEach(chk => chk.checked = e.target.checked);
      });
    }

    // 행 클릭 시 상세보기 (체크박스 클릭은 제외)
    $('#std-body').addEventListener('click', (e) => {
      if (e.target.closest('input[type="checkbox"]')) return;
      const tr = e.target.closest('tr.data-row');
      if (!tr) return;
      const data = {
        id: tr.dataset.id,
        name: tr.dataset.name,
        phone: tr.dataset.phone,
        bizno: tr.dataset.bizno,
        item: tr.dataset.item,
        address: tr.dataset.address,
        division: tr.dataset.division,
        worker: tr.dataset.worker
      };
      openDetailModal(data);
    });

    // 삭제
    $('#std-delete').addEventListener('click', () => {
      const checked = $$('.row-check:checked');
      if (checked.length === 0) {
        alert('삭제할 항목을 선택하세요.');
        return;
      }
      if (!confirm(checked.length + '건을 삭제하시겠어요?')) return;
      $('#deleteForm').submit();
    });
  </script>

  <!-- 페이지 전용 스크립트 (있다면 유지) -->
  <script src="<c:url value='/Html/asset/04_client.js' />"></script>

  <!-- 템플릿 로더: 설정 + 스크립트 (맨 아래) -->
  <script>
    window.__TPL_CONFIG = {
      root: '<c:url value="/"/>',
      templateUrl: '<c:url value="/Html/00_template/template.html"/>'
    };
  </script>
  <script src="<c:url value='/Html/asset/template_load.js' />" defer></script>
</body>
</html>
