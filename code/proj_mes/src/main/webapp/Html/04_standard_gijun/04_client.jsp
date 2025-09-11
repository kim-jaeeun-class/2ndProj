<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="cPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>거래처 관리</title>

  <!-- 정적 리소스 -->
  <link rel="stylesheet" href="<c:url value='/Html/asset/template.css' />">
  <link rel="stylesheet" href="<c:url value='/Html/asset/04_client.css' />">
  <link rel="stylesheet" href="<c:url value='/Html/asset/04_standard_list.css' />">
  <script src="<c:url value='/Html/asset/template_load.js' />"></script>

  <!-- 다음 우편번호 서비스 -->
  <script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>

<body>
  <header></header>
  <div class="gnb"></div>

  <div class="titleBox">
    <span>거래처 관리</span>
    <a href="<c:url value='/' />">
      <div class="toMainpage">
        <img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png"
             width="13" alt="메인 화면으로 가는 화살표" style="transform: scaleX(-1);" />
        메인 화면으로
      </div>
    </a>
  </div>

  <div class="wrap">
    <!-- ✅ 삭제 전용 폼: 체크된 client_id들을 ids=... 로 전송, op=delete -->
    <form id="deleteForm" method="post" action="${cPath}/Client">
      <input type="hidden" name="op" value="delete" />

      <div class="table-wrap">
        <table class="tables">
          <thead>
            <tr>
              <th style="width:48px;">
                <!-- 전체 선택 체크박스 (편의 기능) -->
                <input type="checkbox" id="checkAll" aria-label="전체선택" />
              </th>
              <th>거래처ID</th>
              <th>거래처명</th>
              <th>연락번호</th>
              <th>사업자번호</th>
              <th>거래품목</th>
              <th>주소</th>
              <th>구분</th>
              <th>담당자(사번)</th>
            </tr>
          </thead>

          <tbody id="std-body">
            <c:choose>
              <c:when test="${not empty clients}">
                <c:forEach var="row" items="${clients}">
                  <tr>
                    <!-- ✅ 각 행 체크박스: name='ids' 로 서버에 배열 전송 -->
                    <td>
                      <input type="checkbox" class="row-check" name="ids" value="${row.client_id}" aria-label="선택" />
                    </td>

                    <td><c:out value="${row.client_id}" /></td>
                    <td><c:out value="${row.client_name}" /></td>
                    <td><c:out value="${row.client_phone}" /></td>
                    <td><c:out value="${row.business_number}" /></td>
                    <td><c:out value="${row.business_item}" /></td>
                    <td><c:out value="${row.client_address}" /></td>

                    <!-- ✅ 구분 매핑: -1=출고, 0=공통, 1=발주 (숫자/문자 모두 대응) -->
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

                    <td><c:out value="${row.worker_id}" /></td>
                  </tr>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <tr><td colspan="9" style="text-align:center;">데이터가 없습니다.</td></tr>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>
    </form>
  </div>

  <div style="display:flex; justify-content:end;">
    <!-- ✅ 삭제: 체크된 행들을 deleteForm으로 전송 -->
    <button class="btn" type="button" id="std-delete" style="margin-right:1%">삭제하기</button>
    <!-- 기존 등록 -->
    <button class="btn" type="button" id="std-register" style="margin-right:15px">등록하기</button>
  </div>

  <!-- 등록 모달 -->
  <dialog id="partnerModal" aria-labelledby="pmTitle">
    <div class="modal-card" role="dialog" aria-modal="true">
      <div class="modal-header">
        <h3 id="pmTitle" class="modal-title">거래처 등록</h3>
        <button class="modal-close" type="button" id="pmClose" aria-label="닫기">×</button>
      </div>

      <form id="partnerForm" class="modal-body" autocomplete="off"
            method="post" action="${cPath}/Client">
        <!-- 동작 구분 -->
        <input type="hidden" name="op" value="insert" />
        <!-- addr+addr2 합쳐 넣을 필드 -->
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
            <!-- DB NUMBER에 맞춰 -1/0/1 값 전송 -->
            <select id="division" name="inout_division">
              <option value="">선택</option>
              <option value="-1">출고</option>  <!-- 출고는 -1 -->
              <option value="0">공통</option>   <!-- 공통은 0  -->
              <option value="1">발주</option>   <!-- 발주는 1  -->
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
        <button type="button" class="btn" id="pmCancel">취소</button>
        <button type="submit" form="partnerForm" class="btn primary" id="pmSave">저장</button>
      </div>
    </div>
  </dialog>

  <!-- 모달/주소/삭제 스크립트 -->
  <script>
    // 등록 모달 열기/닫기
    const partnerModal = document.getElementById('partnerModal');
    document.getElementById('std-register').addEventListener('click', () => partnerModal.showModal());
    document.getElementById('pmClose').addEventListener('click', () => partnerModal.close());
    document.getElementById('pmCancel').addEventListener('click', () => partnerModal.close());

    // 다음 주소 검색
    document.getElementById('addrSearch').addEventListener('click', function () {
      new daum.Postcode({
        oncomplete: function (data) {
          document.getElementById('addr').value = data.roadAddress || data.jibunAddress || '';
          document.getElementById('addr2').focus();
        }
      }).open();
    });

    // 제출 직전에 addr+addr2 합쳐 hidden에 넣기
    document.getElementById('partnerForm').addEventListener('submit', function () {
      const a1 = document.getElementById('addr').value.trim();
      const a2 = document.getElementById('addr2').value.trim();
      document.getElementById('client_address_hidden').value = (a1 + ' ' + a2).trim();
    });

    // ✅ 전체선택
    const checkAll = document.getElementById('checkAll');
    checkAll.addEventListener('change', (e) => {
      document.querySelectorAll('.row-check').forEach(chk => chk.checked = e.target.checked);
    });

    // ✅ 삭제 버튼: 선택 확인 → confirm → 폼 제출
    document.getElementById('std-delete').addEventListener('click', () => {
      const checked = Array.from(document.querySelectorAll('.row-check:checked'));
      if (checked.length === 0) {
        alert('삭제할 항목을 선택하세요.');
        return;
      }
      if (!confirm(checked.length + '건을 삭제하시겠어요?')) return;

      document.getElementById('deleteForm').submit();
    });
  </script>

  <script src="<c:url value='/Html/asset/04_client.js' />"></script>
</body>
</html>
