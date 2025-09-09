<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>기준 목록</title>
  <link rel="stylesheet" href="/proj_mes/Html/asset/template.css" />
  <link rel="stylesheet" href="/proj_mes/Html/asset/04_standard_list.css" />
  <script src="/proj_mes/Html/asset/template_load.js"></script>
</head>
<body>
  <!-- 헤더 -->
  <header></header>

  <!-- GNB -->
  <div class="gnb"></div>

  <!-- 타이틀 -->
  <div class="titleBox">
    <span>기준 목록</span>
    <a href="">
      <div class="toMainpage">
        <img src="https://i.postimg.cc/ZKF2nbTx/43-20250904122343.png" width="13" alt="메인 화면으로 가는 화살표"
             style="transform:scaleX(-1);" />
        메인 화면으로
      </div>
    </a>
  </div>

  <!-- 본문 -->
  <div class="wrap">
    <main class="main">
      <!-- 상단 툴바 -->
      <div class="toolbar" role="region" aria-label="검색 및 등록">
        <label for="std-category">분류</label>
        <div class="select">
          <select id="std-category">
            <option value="">선택하세요</option>
            <option>공정</option>
            <option>BOM</option>
            <option>발주</option>
            <option>재고</option>
            <option>생산</option>
            <option>품질</option>
          </select>
        </div>
        <button class="btn" type="button" id="std-search">조회</button>
        <div class="spacer"></div>
        <button class="btn" type="button" id="std-create">등록하기</button>
      </div>

      <!-- 표 -->
      <section aria-label="기준 목록 표">
        <div class="table-wrap" tabindex="0">
          <table class="tables" aria-label="기준 목록">
            <!-- 동적 colgroup: 첫 번째(번호) 고정폭 + 나머지 컬럼 갯수만큼 자동 -->
            <colgroup>
              <col style="width:120px" />
              <c:forEach var="i" begin="1" end="${fn:length(columns)}">
                <col />
              </c:forEach>
            </colgroup>

            <thead>
              <tr>
                <th scope="col">번호</th>
                <c:forEach var="col" items="${columns}">
                  <th scope="col">${col.title}</th>
                </c:forEach>
              </tr>
            </thead>

            <tbody id="std-body">
              <c:choose>
                <c:when test="${empty data}">
                  <tr>
                    <td colspan="${fn:length(columns) + 1}" style="text-align:center;color:#888;">
                      선택한 분류에 해당하는 데이터가 없습니다.
                    </td>
                  </tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="row" items="${data}" varStatus="st">
                    <tr>
                      <td>${st.index + 1}</td>
                      <c:forEach var="col" items="${columns}">
                        <td>${row[col.key]}</td>
                      </c:forEach>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>
      </section>
    </main>
  </div>

  <!-- 선택값 유지 + 조회 버튼 동작 -->
  <script>
    (function () {
      // 서버에서 내려준 현재 카테고리 값 적용
      var current = '<c:out value="${category}" />';
      if (current) {
        var sel = document.getElementById('std-category');
        for (var i = 0; i < sel.options.length; i++) {
          if (sel.options[i].value === current || sel.options[i].text === current) {
            sel.selectedIndex = i;
            break;
          }
        }
      }

      // 조회 버튼: 선택값을 쿼리로 전달
      document.getElementById('std-search').addEventListener('click', function () {
        var cat = document.getElementById('std-category').value || '';
        location.href = '<c:url value="/StandardCtrl"/>' + '?category=' + encodeURIComponent(cat);
      });

      // 등록하기 버튼: 카테고리별 등록 페이지 이동
      document.getElementById('std-create').addEventListener('click', function () {
        var cat = document.getElementById('std-category').value || '';

        // 예: /proj_mes/Html/04_standard_gijun/create_<카테고리>.jsp 로 이동하도록 구성
        if (!cat) { alert('분류를 먼저 선택해 주세요.'); return; }
//         location.href = '/proj_mes/Html/04_standard_gijun/create_' + encodeURIComponent(cat) + '.jsp';
        if(cat == "발주"){
        	cat = "orderRegistration";
        	location.href = '/proj_mes/WEB=INF/' + encodeURIComponent(cat) + '.jsp';        	
        }

      });
    })();
  </script>
</body>
</html>
