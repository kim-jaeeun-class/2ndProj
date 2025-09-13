<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>



<% String ctx = request.getContextPath();%>


<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>기준 목록</title>
<!--   <link rel="stylesheet" href="/proj_mes/Html/asset/mainpage.css" /> -->
<link rel="stylesheet" href="<c:url value='/Html/asset/04_standard_list.css'/>" />
<%-- <link rel="stylesheet" href="<c:url value='/Html/asset/mainpage.css'/>" /> --%>

<style>
.main{
	width : 90%;
	margin : 5%;
}

</style> 

</head>
<body>
	<div id="header-slot"></div>
	<div id="nav-slot"></div>




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
  'use strict';

  // === 서버 경로들 (JSP가 렌더링하면서 값이 들어갑니다) ===
  const ROOT         = '<c:url value="/"/>';                         // 예: "/proj_mes/"
  const SEARCH_URL   = '<c:url value="/StandardCtrl"/>';             // 조회 컨트롤러
  const TEMPLATE_URL = '<c:url value="/Html/00_template/template.html"/>'; // 템플릿 파일

  // === 1) 현재 카테고리 셀렉트 초기화 ===
  (function setCurrentCategory() {
    var current = '<c:out value="${category}" />';
    if (!current) return;
    var sel = document.getElementById('std-category');
    if (!sel) return;
    for (var i = 0; i < sel.options.length; i++) {
      var opt = sel.options[i];
      if (opt.value === current || opt.text === current) {
        sel.selectedIndex = i;
        break;
      }
    }
  })();

  // === 2) 조회 버튼 / Enter 키로 조회 ===
  (function wireSearch() {
    var sel = document.getElementById('std-category');
    var btn = document.getElementById('std-search');

    function goSearch() {
      var cat = (sel && sel.value ? sel.value : '').trim();
      location.href = SEARCH_URL + '?category=' + encodeURIComponent(cat);
    }

    if (btn) btn.addEventListener('click', goSearch);
    if (sel) sel.addEventListener('keydown', function (e) {
      if (e.key === 'Enter') goSearch();
    });
  })();

  // === 3) 등록하기 버튼 (카테고리 → JSP 파일 매핑) ===
  (function wireCreate() {
    var btn = document.getElementById('std-create');
    var sel = document.getElementById('std-category');

    // 필요 시 여기만 수정하면 됩니다.
    var routeMap = {
      '발주': 'orderRegistration', // /proj_mes/orderRegistration.jsp
      '공정': 'process',           // /proj_mes/process.jsp
      '재고': 'stock',             // /proj_mes/stock.jsp
      'BOM' : 'BOM',               // /proj_mes/BOM.jsp
      '생산': '생산',               // /proj_mes/생산.jsp (한글 파일명 사용 시)
      '품질': '품질'                // /proj_mes/품질.jsp
    };

    function goCreate() {
      var cat = (sel && sel.value ? sel.value : '').trim();
      if (!cat) { alert('분류를 먼저 선택해 주세요.'); return; }
      var page = routeMap[cat];
      if (!page) { alert('알 수 없는 분류입니다.'); return; }
      location.href = ROOT + encodeURIComponent(page) + '.jsp';
    }

    if (btn) btn.addEventListener('click', goCreate);
  })();

  // === 4) 템플릿(head의 CSS 포함) 로드 → header/nav 삽입 → 메뉴 버튼 초기화 ===
  (async function loadTemplateAndInject() {
    try {
      const res  = await fetch(TEMPLATE_URL, { credentials: 'same-origin' });
      const html = await res.text();
      const doc  = new DOMParser().parseFromString(html, 'text/html');

      // 4-1) 템플릿의 <link rel="stylesheet">, <style> 를 현재 문서 <head>로 이식
      const head = document.head;
      const existingHrefs = new Set(
        Array.from(head.querySelectorAll('link[rel="stylesheet"]'))
             .map(l => l.href).filter(Boolean)
      );

      // link rel="stylesheet"
      doc.querySelectorAll('link[rel="stylesheet"]').forEach(linkEl => {
        const href = linkEl.getAttribute('href');
        if (!href) return;
        const abs = new URL(href, location.origin).href;
        if (!existingHrefs.has(abs)) {
          const link = document.createElement('link');
          link.rel = 'stylesheet';
          link.href = abs;
          head.appendChild(link);
          existingHrefs.add(abs);
        }
      });

      // <style> (인라인 스타일) 복사
      doc.querySelectorAll('style').forEach(styleEl => {
        head.appendChild(styleEl.cloneNode(true));
      });

      // 4-2) header/nav 노드 교체
      const header    = doc.querySelector('header.header-bg') || doc.querySelector('header');
      const nav       = doc.querySelector('nav.nav-bg')       || doc.querySelector('nav');
      const headerSlot= document.getElementById('header-slot');
      const navSlot   = document.getElementById('nav-slot');
      if (header && headerSlot) headerSlot.replaceWith(header);
      if (nav && navSlot)       navSlot.replaceWith(nav);

      // 4-3) 템플릿이 삽입된 후에 메뉴 핸들러 바인딩
      initUserMenuHandlers();
    } catch (e) {
      console.error('템플릿 로드 실패:', e);
    }
  })();

  // === 5) 템플릿의 사용자 메뉴 토글 (템플릿 삽입 후 호출) ===
  function initUserMenuHandlers() {
    const myIconBtn = document.getElementById('myIconBtn');
    const userMenu  = document.getElementById('userMenu');
    if (!myIconBtn || !userMenu) return; // 템플릿 구조 변경 대비

    function closeUserMenu() {
      userMenu.classList.add('hidden');
      myIconBtn.setAttribute('aria-expanded', 'false');
    }
    function toggleMenu(e) {
      e && e.stopPropagation();
      userMenu.classList.toggle('hidden');
      myIconBtn.setAttribute('aria-expanded',
        userMenu.classList.contains('hidden') ? 'false' : 'true');
    }

    myIconBtn.addEventListener('click', toggleMenu);
    userMenu.addEventListener('click', function (e) { e.stopPropagation(); });
    document.addEventListener('click', closeUserMenu);
    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') closeUserMenu();
    });
  }

})();
</script>
</body>
</html>
