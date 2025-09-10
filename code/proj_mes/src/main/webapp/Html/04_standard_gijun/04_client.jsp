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
    <div class="table-wrap">
      <table class="tables">
        <thead>
          <tr>
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
                  <td><c:out value="${row.client_id}" /></td>
                  <td><c:out value="${row.client_name}" /></td>
                  <td><c:out value="${row.client_phone}" /></td>
                  <td><c:out value="${row.business_number}" /></td>
                  <td><c:out value="${row.business_item}" /></td>
                  <td><c:out value="${row.client_address}" /></td>
                  <!-- DTO는 input_division, DB는 INOUT_DIVISION -->
                  <td><c:out value="${row.input_division}" /></td>
                  <td><c:out value="${row.worker_id}" /></td>
                </tr>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <tr><td colspan="8" style="text-align:center;">데이터가 없습니다.</td></tr>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </div>
  </div>

  <div style="display:flex; justify-content:end;">
    <button class="btn" type="button" id="std-search_0" style="margin-right:15px">뒤로가기</button>
    <button class="btn" type="button" id="std-search_1" style="margin-right:5%">등록하기</button>
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
            <select id="division" name="inout_division">
              <option value="">선택</option>
              <option value="출고">출고</option>
              <option value="발주">발주</option>
              <option value="공통">공통</option>
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

  <!-- 모달/주소/제출 스크립트 -->
  <script>
    const partnerModal = document.getElementById('partnerModal');
    document.getElementById('std-search_1').addEventListener('click', () => partnerModal.showModal());
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
  </script>

  <!-- 기타 페이지 스크립트 (필요 시) -->
  <script src="<c:url value='/Html/asset/04_client.js' />"></script>
</body>
</html>
