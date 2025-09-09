<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="cPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>거래처 관리</title>

  <!-- 정적 리소스: 컨텍스트 경로 안전 처리 -->
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
    <!-- 메인으로 이동: 컨텍스트 루트 -->
    <a href="${cPath}/">
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
            <th>분류</th>
            <th>거래처명</th>
            <th>사업자번호</th>
            <th>거래품목</th>
            <th>담당자</th>
            <th>연락번호</th>
            <th>지역</th>
          </tr>
        </thead>

        <tbody id="std-body">
          <!-- 서버에서 모델로 넘긴 리스트가 있으면 렌더링 -->
          <c:choose>
            <c:when test="${not empty clients}">
              <c:forEach var="row" items="${clients}">
                <tr>
                  <td>${row.category}</td>
                  <td>${row.corpName}</td>
                  <td>${row.bizNo}</td>
                  <td>${row.item}</td>
                  <td>${row.manager}</td>
                  <td>${row.tel}</td>
                  <td>${row.region}</td>
                </tr>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <!-- 라인 표시용 더미 행(필요 시 서버 데이터로 대체) -->
              <tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
              <tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
              <tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
              <tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
              <tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
              <tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
              <tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
              <tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
              <tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
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

  <dialog id="partnerModal" aria-labelledby="pmTitle">
    <div class="modal-card" role="dialog" aria-modal="true">
      <div class="modal-header">
        <h3 id="pmTitle" class="modal-title">거래처(출고/발주)</h3>
        <button class="modal-close" type="button" id="pmClose" aria-label="닫기">×</button>
      </div>

      <form id="partnerForm" class="modal-body" autocomplete="off">
        <div class="grid">
          <div class="field">
            <label for="corpName">거래처명</label>
            <input id="corpName" class="input" type="text" placeholder="예) 00유리" required />
          </div>

          <div class="field">
            <label for="bizNo">사업자번호</label>
            <input id="bizNo" class="input" type="text" inputmode="numeric"
                   placeholder="012-34-56789" maxlength="12" />
          </div>

          <div class="field">
            <label for="item">거래품목</label>
            <select id="item">
              <option value="">선택</option>
              <option>유리</option>
              <option>코팅액</option>
              <option>기타</option>
            </select>
          </div>

          <div class="field">
            <label for="manager">담당자</label>
            <input id="manager" class="input" type="text" placeholder="담당자" />
          </div>

          <div class="field">
            <label for="tel">연락번호</label>
            <input id="tel" class="input" type="tel" placeholder="010-5555-6666" maxlength="13" />
          </div>

          <!-- 지역은 주석 처리(필요 시 복구) -->

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

  <!-- '지연' 유틸: #msg 요소가 없어도 에러 없도록 안전 처리 -->
  <script>
    const sleep = (ms) => new Promise(res => setTimeout(res, ms));
    (async () => {
      await sleep(1000);
      const msg = document.getElementById('msg');
      if (msg) msg.style.display = 'block';
    })();
  </script>

  <!-- 페이지 스크립트 -->
  <script src="<c:url value='/Html/asset/04_client.js' />"></script>
</body>
</html>
